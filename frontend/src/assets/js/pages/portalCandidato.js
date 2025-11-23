document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const vagasGrid = document.getElementById('vagasGrid');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const emptyState = document.getElementById('emptyState');
    const searchInput = document.getElementById('searchVaga');

    const modalElement = document.getElementById('modalConfirmarCancelamento');
    const modalCancelamento = new bootstrap.Modal(modalElement);
    const btnConfirmarModal = document.getElementById('btnConfirmarAcao');
    const spanNomeVagaModal = document.getElementById('nomeVagaModal');
    
    let todasVagas = [];
    let minhasCandidaturasIds = new Set(); 
    let filtroAtual = 'todas';

    let vagaIdParaCancelar = null;
    let vagaTituloParaCancelar = null;

    async function init() {
        await carregarSidebarCandidato();
        await fetchDados();
    }

    async function carregarSidebarCandidato() {
        try {
            const response = await fetch('components/sidebar-candidato.html');
            if (response.ok) {
                const html = await response.text();
                document.getElementById('sidebarCandidatoContainer').innerHTML = html;
                
                const linkVagas = document.getElementById('link-vagas');
                if (linkVagas) linkVagas.classList.add('active');
            }
        } catch (e) {
            console.error("Erro ao carregar sidebar:", e);
        }
    }

    async function fetchDados() {
        loadingSpinner.classList.remove('d-none');
        vagasGrid.classList.add('d-none');
        emptyState.classList.add('d-none');

        try {
            const headers = { 'Authorization': `Bearer ${token}` };

            const [resVagas, resCandidaturas] = await Promise.all([
                fetch('http://localhost:8080/api/v1/vagas/disponiveis', { headers }),
                fetch('http://localhost:8080/api/v1/candidaturas/minhas', { headers })
            ]);

            if (resVagas.status === 403 || resCandidaturas.status === 403) {
                showToast("Sessão Expirada", "Faça login novamente.", "danger");
                setTimeout(logout, 2000);
                return;
            }

            const vagas = await resVagas.json();
            const candidaturas = await resCandidaturas.json();

            minhasCandidaturasIds.clear();
            candidaturas.forEach(c => {
                minhasCandidaturasIds.add(c.tituloVaga); 
            });

            todasVagas = vagas;
            renderizar();

        } catch (error) {
            console.error("Erro de conexão:", error);
            showToast("Erro", "Não foi possível carregar os dados.", "danger");
        } finally {
            loadingSpinner.classList.add('d-none');
        }
    }

    async function candidatarSe(vagaId, vagaTitulo, btn) {
        const originalText = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Enviando...`;

        try {
            const response = await fetch(`http://localhost:8080/api/v1/candidaturas/aplicar/${vagaId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                showToast("Sucesso", "Candidatura enviada!", "success");
                minhasCandidaturasIds.add(vagaTitulo);
                renderizar(); 

            } else {
                const msg = await response.text();
                showToast("Atenção", msg, "warning");
                btn.disabled = false;
                btn.innerHTML = originalText;
            }
        } catch (e) {
            showToast("Erro", "Falha na comunicação.", "danger");
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    }

    window.triggerCancelar = function(vagaId, titulo) {
        vagaIdParaCancelar = vagaId;
        vagaTituloParaCancelar = titulo;
        
        if(spanNomeVagaModal) spanNomeVagaModal.innerText = titulo;
        
        modalCancelamento.show();
    }

    if(btnConfirmarModal) {
        btnConfirmarModal.addEventListener('click', async () => {
            if (!vagaIdParaCancelar) return;

            const originalText = btnConfirmarModal.innerHTML;
            btnConfirmarModal.disabled = true;
            btnConfirmarModal.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Processando...`;

            try {
                const response = await fetch(`http://localhost:8080/api/v1/candidaturas/cancelar/${vagaIdParaCancelar}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (response.ok) {
                    modalCancelamento.hide();
                    showToast("Cancelado", "Inscrição removida com sucesso.", "info");
                    
                    minhasCandidaturasIds.delete(vagaTituloParaCancelar);
                    renderizar();

                } else {
                    const msg = await response.text();
                    modalCancelamento.hide();
                    showToast("Erro", "Não foi possível cancelar: " + msg, "warning");
                }
            } catch (e) {
                console.error(e);
                modalCancelamento.hide();
                showToast("Erro", "Erro de conexão.", "danger");
            } finally {
                btnConfirmarModal.disabled = false;
                btnConfirmarModal.innerHTML = originalText;
                vagaIdParaCancelar = null;
            }
        });
    }

    window.triggerCandidatar = function(id, titulo, btn) {
        candidatarSe(id, titulo, btn);
    }

    window.filtrarTab = function(tipo) {
        filtroAtual = tipo;
        
        document.getElementById('tabTodas').classList.toggle('active', tipo === 'todas');
        document.getElementById('tabTodas').classList.toggle('text-muted', tipo !== 'todas');
        
        document.getElementById('tabMinhas').classList.toggle('active', tipo === 'minhas');
        document.getElementById('tabMinhas').classList.toggle('text-muted', tipo !== 'minhas');

        renderizar();
    }

    function renderizar() {
        const termo = searchInput.value.toLowerCase();
        
        const listaFiltrada = todasVagas.filter(vaga => {
            const jaCandidatou = minhasCandidaturasIds.has(vaga.titulo);
            
            if (filtroAtual === 'minhas' && !jaCandidatou) return false;

            const matchTexto = 
                vaga.titulo.toLowerCase().includes(termo) || 
                vaga.nomeDepartamento.toLowerCase().includes(termo) ||
                vaga.funcao.toLowerCase().includes(termo);
            
            return matchTexto;
        });

        vagasGrid.innerHTML = '';

        if (listaFiltrada.length === 0) {
            vagasGrid.classList.add('d-none');
            document.getElementById('emptyState').classList.remove('d-none');
            document.getElementById('emptyStateTitle').innerText = filtroAtual === 'minhas' 
                ? "Você ainda não se candidatou a nenhuma vaga." 
                : "Nenhuma vaga encontrada.";
            return;
        }

        document.getElementById('emptyState').classList.add('d-none');
        vagasGrid.classList.remove('d-none');

        listaFiltrada.forEach(vaga => {
            const jaAplicado = minhasCandidaturasIds.has(vaga.titulo);
            
            const botaoAcao = jaAplicado 
                ? `<button class="btn btn-outline-danger w-100 fw-bold" onclick="window.triggerCancelar(${vaga.id}, '${vaga.titulo}')">
                        <i class="fas fa-times-circle me-2"></i>Cancelar Inscrição
                   </button>`
                : `<button class="btn btn-primary btn-apply w-100 shadow-sm" onclick="window.triggerCandidatar(${vaga.id}, '${vaga.titulo}', this)">
                        Candidatar-se Agora
                   </button>`;

            const html = `
            <div class="col-md-6 col-xl-4">
                <div class="vaga-card h-100 ${jaAplicado ? 'inscrito border-success' : ''}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <span class="badge-dept">${vaga.nomeDepartamento}</span>
                            ${jaAplicado 
                                ? '<span class="badge bg-success"><i class="fas fa-check"></i> Inscrito</span>' 
                                : '<span class="badge bg-light text-primary border">Aberta</span>'}
                        </div>
                        
                        <h5 class="vaga-title mt-3">${vaga.titulo}</h5>
                        <div class="text-muted small mb-3">
                            <i class="fas fa-tools me-1"></i> ${vaga.funcao}
                        </div>

                        <p class="vaga-desc">
                            ${vaga.descricao || 'Descrição não disponível.'}
                        </p>

                        <div class="mt-auto pt-3 text-muted small border-top d-flex align-items-center">
                            <i class="far fa-calendar-alt me-2"></i>
                            <span>Encerra em: <strong>${formatarData(vaga.dataLimite)}</strong></span>
                        </div>
                    </div>
                    <div class="card-footer border-0 pt-0 pb-4 px-4">
                        ${botaoAcao}
                    </div>
                </div>
            </div>`;
            
            vagasGrid.insertAdjacentHTML('beforeend', html);
        });
    }

    function formatarData(str) {
        if (!str) return "N/A";
        const [ano, mes, dia] = str.split('-');
        return `${dia}/${mes}/${ano}`;
    }

    function showToast(titulo, msg, tipo) {
        const toastEl = document.getElementById('liveToast');
        const toastBody = document.getElementById('toastMessage');
        
        toastEl.className = `toast align-items-center text-white bg-${tipo} border-0`;
        toastBody.innerText = `${titulo}: ${msg}`;
        
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }

    searchInput.addEventListener('input', renderizar);

    init();
});