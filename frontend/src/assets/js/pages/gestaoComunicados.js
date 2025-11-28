document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const ITENS_POR_PAGINA = 5; 
    let paginaAtual = 1;
    let listaOriginal = []; 
    let listaFiltrada = []; 
    let ordemAscendente = false; 
    let idParaExcluir = null;

    const modalElement = document.getElementById('modalComunicado');
    const modalInstance = new bootstrap.Modal(modalElement);
    const modalExcluirElement = document.getElementById('modalExcluir');
    const modalExcluirInstance = new bootstrap.Modal(modalExcluirElement);
    const toastElement = document.getElementById('liveToast');
    const toastInstance = new bootstrap.Toast(toastElement);

    async function init() {
        const usuarioValido = await validarSessao();
        if (!usuarioValido) return;
        await carregarSidebar();
        await carregarComunicados();
    }

    async function validarSessao() {
        try {
            const response = await fetch('http://localhost:8080/user/me',{
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error("Sessão inválida!");

            const dadosUsuario = await response.json();
            if (dadosUsuario.role !== 'ROLE_ADMIN'){
                alert("Acesso não autorizado!");
                window.location.href = 'login.html';
                return false;
            }
            return true;
        } catch (error) {
            console.log(error);
            localStorage.removeItem('token');
            localStorage.removeItem('usuario');
            window.location.href = 'login.html';
            return false;
        }
    }

    async function carregarSidebar() {
        try {
            const response = await fetch('components/sidebar-administrador.html');
            if (response.ok) {
                document.getElementById('sidebarAdministradorContainer').innerHTML = await response.text();
                const paginaAtualLink = window.location.pathname.split('/').pop() || 'gestaoComunicados.html';
                document.querySelectorAll('.sidebar-item').forEach(l => {
                    l.classList.remove('active');
                    if (l.getAttribute('href') === paginaAtualLink) l.classList.add('active');
                });
            }
        } catch (e) { console.error("Erro sidebar:", e); }
    }

    async function carregarComunicados() {
        const container = document.getElementById('listaComunicados');
        container.innerHTML = '<div class="text-center py-5"><i class="fas fa-spinner fa-spin fa-2x text-muted"></i></div>';

        try {
            const response = await fetch('http://localhost:8080/api/v1/comunicados?page=0&size=50', {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                listaOriginal = data.content || data;
                
                listaFiltrada = [...listaOriginal];
                atualizarInterface();
            } else {
                container.innerHTML = '<div class="text-center text-danger py-4">Erro ao carregar dados.</div>';
                exibirToast('Falha ao buscar dados.', 'error');
            }
        } catch (e) {
            console.error(e);
            container.innerHTML = '<div class="text-center text-danger py-4">Erro de conexão.</div>';
        }
    }

    function atualizarInterface() {
        const container = document.getElementById('listaComunicados');
        const containerPagina = document.getElementById('paginationControls');

        const inicio = (paginaAtual - 1) * ITENS_POR_PAGINA;
        const fim = inicio + ITENS_POR_PAGINA;
        const itensDaPagina = listaFiltrada.slice(inicio, fim);

        renderizarLista(itensDaPagina, container);
        renderizarControlesPaginacao(listaFiltrada.length, containerPagina);
    }

    function renderizarControlesPaginacao(totalItens, container) {
        container.innerHTML = '';
        const totalPaginas = Math.ceil(totalItens / ITENS_POR_PAGINA);

        if (totalPaginas <= 1) return;

        const prevDisabled = paginaAtual === 1 ? 'disabled' : '';
        container.innerHTML += `
            <li class="page-item ${prevDisabled}">
                <button class="page-link" onclick="mudarPagina(${paginaAtual - 1})"><i class="fas fa-chevron-left"></i></button>
            </li>
        `;

        for (let i = 1; i <= totalPaginas; i++) {
            const active = i === paginaAtual ? 'active' : '';
            container.innerHTML += `
                <li class="page-item ${active}">
                    <button class="page-link" onclick="mudarPagina(${i})">${i}</button>
                </li>
            `;
        }

        const nextDisabled = paginaAtual === totalPaginas ? 'disabled' : '';
        container.innerHTML += `
            <li class="page-item ${nextDisabled}">
                <button class="page-link" onclick="mudarPagina(${paginaAtual + 1})"><i class="fas fa-chevron-right"></i></button>
            </li>
        `;
    }

    window.mudarPagina = (novaPagina) => {
        paginaAtual = novaPagina;
        atualizarInterface();
        document.getElementById('listaComunicados').scrollIntoView({ behavior: 'smooth', block: 'start' });
    };

    function renderizarLista(lista, container) {
        container.innerHTML = '';
        
        if (!lista || lista.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="far fa-folder-open fa-3x text-muted mb-3"></i>
                    <p class="text-muted">Nenhum comunicado encontrado.</p>
                </div>`;
            return;
        }

        lista.forEach(c => {
            let badgeClass = 'badge-informativo';
            let badgeLabel = 'Informativo';

            if (c.tipo === 'IMPORTANTE') { badgeClass = 'badge-importante'; badgeLabel = 'Importante'; }
            else if (c.tipo === 'EVENTO') { badgeClass = 'badge-evento'; badgeLabel = 'Evento'; }

            const dataFmt = c.dataPublicacao ? new Date(c.dataPublicacao).toLocaleDateString('pt-BR') : '--/--/----';
            const textoConteudo = c.conteudo || c.descricao || '';
            const tituloSafe = (c.titulo || '').replace(/"/g, '&quot;');
            const conteudoSafe = textoConteudo.replace(/"/g, '&quot;').replace(/\n/g, ' ');
            const tipoSafe = c.tipo || 'INFORMATIVO';

            const card = document.createElement('div');
            card.className = 'comunicado-card';
            card.innerHTML = `
                <div class="card-content-left">
                    <div class="card-header-line">
                        <h3 class="card-title">${c.titulo}</h3>
                        <span class="badge-pill ${badgeClass}">${badgeLabel}</span>
                    </div>
                    <p class="card-desc" title="${textoConteudo}">${textoConteudo || 'Sem descrição'}</p>
                </div>
                <div class="card-content-right">
                    <div class="date-box">
                        <span class="date-label">Data de Publicação</span>
                        <span class="date-value">${dataFmt}</span>
                    </div>
                    <div class="action-buttons">
                        <button class="btn-icon-action btn-edit" onclick='prepararEdicao(${c.id}, "${tituloSafe}", "${tipoSafe}", "${conteudoSafe}")' title="Editar">
                            <i class="fas fa-pen"></i>
                        </button>
                        <button class="btn-icon-action btn-delete" onclick="deletarComunicado(${c.id})" title="Excluir">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            `;
            container.appendChild(card);
        });
    }
    window.filtrarLista = () => {
        const termo = document.getElementById('inputBusca').value.toLowerCase();
        listaFiltrada = listaOriginal.filter(c => {
            const titulo = c.titulo ? c.titulo.toLowerCase() : '';
            const conteudo = (c.conteudo || c.descricao || '').toLowerCase();
            return titulo.includes(termo) || conteudo.includes(termo);
        });
        paginaAtual = 1;
        atualizarInterface();
    };

    window.aplicarFiltro = (tipo) => {
        const textoFiltro = document.getElementById('textoFiltro');
        if (tipo === 'TODOS') {
            listaFiltrada = [...listaOriginal];
            textoFiltro.textContent = 'Todos';
        } else {
            listaFiltrada = listaOriginal.filter(c => c.tipo === tipo);
            textoFiltro.textContent = tipo.charAt(0) + tipo.slice(1).toLowerCase();
        }
        const termo = document.getElementById('inputBusca').value;
        if(termo) window.filtrarLista();
        else {
            paginaAtual = 1;
            atualizarInterface();
        }
    };

    window.alternarOrdem = () => {
        ordemAscendente = !ordemAscendente;
        const textoOrdem = document.getElementById('textoOrdem');
        
        if (ordemAscendente) {
            textoOrdem.textContent = 'Antigos';
            listaFiltrada.sort((a, b) => new Date(a.dataPublicacao) - new Date(b.dataPublicacao));
        } else {
            textoOrdem.textContent = 'Recentes';
            listaFiltrada.sort((a, b) => new Date(b.dataPublicacao) - new Date(a.dataPublicacao));
        }
        atualizarInterface();
    };

    window.abrirModal = () => {
        document.getElementById('formComunicado').reset();
        document.getElementById('comunicadoId').value = '';
        document.getElementById('modalTitle').textContent = 'Novo Comunicado';
        modalInstance.show();
    };

    window.prepararEdicao = (id, titulo, tipo, conteudo) => {
        document.getElementById('comunicadoId').value = id;
        document.getElementById('titulo').value = titulo;
        document.getElementById('tipo').value = tipo;
        document.getElementById('descricao').value = conteudo;
        document.getElementById('modalTitle').textContent = 'Editar Comunicado';
        modalInstance.show();
    };

    window.salvarComunicado = async () => {
        const id = document.getElementById('comunicadoId').value;
        const btnSalvar = document.querySelector('#modalComunicado .btn-primary');
        const textoOriginal = btnSalvar.textContent;
        
        btnSalvar.disabled = true;
        btnSalvar.textContent = 'Salvando...';

        const valorTexto = document.getElementById('descricao').value;
        const dados = {
            titulo: document.getElementById('titulo').value,
            tipo: document.getElementById('tipo').value,
            conteudo: valorTexto
        };

        if (!dados.titulo || !dados.conteudo) {
            exibirToast('Preencha todos os campos.', 'error');
            btnSalvar.disabled = false;
            btnSalvar.textContent = textoOriginal;
            return;
        }

        const method = id ? 'PUT' : 'POST';
        const url = id 
            ? `http://localhost:8080/api/v1/comunicados/${id}` 
            : 'http://localhost:8080/api/v1/comunicados';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(dados)
            });

            if (response.ok) {
                modalInstance.hide();
                exibirToast('Comunicado salvo com sucesso!', 'success');
                carregarComunicados();
            } else {
                exibirToast('Erro ao salvar comunicado.', 'error');
            }
        } catch (e) {
            exibirToast('Erro de conexão.', 'error');
        } finally {
            btnSalvar.disabled = false;
            btnSalvar.textContent = textoOriginal;
        }
    };

    window.deletarComunicado = (id) => {
        idParaExcluir = id;
        modalExcluirInstance.show();
    };

    window.confirmarExclusao = async () => {
        if (!idParaExcluir) return;
        const btn = document.querySelector('#modalExcluir .btn-danger');
        btn.disabled = true;
        try {
            const response = await fetch(`http://localhost:8080/api/v1/comunicados/${idParaExcluir}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                exibirToast('Comunicado excluído.', 'success');
                carregarComunicados();
                modalExcluirInstance.hide();
            } else {
                exibirToast('Erro ao excluir.', 'error');
            }
        } catch (e) {
            exibirToast('Erro de conexão.', 'error');
        } finally {
            btn.disabled = false;
            idParaExcluir = null;
        }
    };

    function exibirToast(mensagem, tipo = 'success') {
        const toastBody = toastElement.querySelector('.toast-body');
        toastBody.textContent = mensagem;
        toastElement.classList.remove('bg-success', 'bg-danger', 'bg-warning');
        if (tipo === 'success') toastElement.classList.add('bg-success');
        else if (tipo === 'error') toastElement.classList.add('bg-danger');
        else toastElement.classList.add('bg-warning');
        toastInstance.show();
    }
    init();
});