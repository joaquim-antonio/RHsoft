document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    
    // Se não tiver token, desloga
    if (!token) {
        logout();
        return;
    }

    const vagasGrid = document.getElementById('vagasGrid');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const emptyState = document.getElementById('emptyState');
    const searchInput = document.getElementById('searchVaga');

    // Elementos do Modal de Cancelamento
    const modalElement = document.getElementById('modalConfirmarCancelamento');
    const modalCancelamento = new bootstrap.Modal(modalElement);
    const btnConfirmarModal = document.getElementById('btnConfirmarAcao');
    const spanNomeVagaModal = document.getElementById('nomeVagaModal');

    let todasVagas = []; 
    let minhasCandidaturasIds = new Set(); 
    let filtroAtual = 'todas';
    let vagaIdParaCancelar = null;

    async function init() {
        await carregarSidebarCandidato();
        await fetchDados();
    }

    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
        window.location.href = 'login.html';
    }

    // Carrega o menu lateral
    async function carregarSidebarCandidato() {
        try {
            const response = await fetch('components/sidebar-candidato.html');
            if (response.ok) {
                const html = await response.text();
                const container = document.getElementById('sidebarCandidatoContainer');
                if (container) {
                    container.innerHTML = html;
                    const linkVagas = document.getElementById('link-vagas');
                    if (linkVagas) linkVagas.classList.add('active');
                }
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

            // Busca Vagas Disponíveis e Minhas Candidaturas em paralelo
            const [resVagas, resCandidaturas] = await Promise.all([
                fetch('http://localhost:8080/api/v1/vagas/disponiveis', { headers }),
                fetch('http://localhost:8080/api/v1/candidaturas/minhas', { headers })
            ]);

            // Verifica Token Expirado
            if (resVagas.status === 403 || resCandidaturas.status === 403) {
                showToast("Sessão Expirada", "Faça login novamente.", "danger");
                setTimeout(logout, 2000);
                return;
            }

            const dadosVagas = await resVagas.json();
            const dadosCandidaturas = await resCandidaturas.json();

            if (Array.isArray(dadosVagas)) {
                todasVagas = dadosVagas;
            } else {
                console.error("API Vagas retornou erro:", dadosVagas);
                todasVagas = [];
                showToast("Erro", "Falha ao carregar lista de vagas.", "warning");
            }

            // Mapeia IDs das candidaturas
            minhasCandidaturasIds.clear();
            if (Array.isArray(dadosCandidaturas)) {
                dadosCandidaturas.forEach(c => {
                    const idVaga = c.vagaId || (c.vaga ? c.vaga.id : null) || c.idVaga; 
                    
                    if (idVaga) {
                        minhasCandidaturasIds.add(idVaga); 
                    } else if (c.tituloVaga) {
                        const vagaCorrespondente = todasVagas.find(v => v.titulo === c.tituloVaga);
                        if(vagaCorrespondente) minhasCandidaturasIds.add(vagaCorrespondente.id);
                    }
                });
            }

            renderizar();

        } catch (error) {
            console.error("Erro de conexão:", error);
            showToast("Erro", "Não foi possível conectar ao servidor.", "danger");
            todasVagas = []; 
            renderizar();
        } finally {
            loadingSpinner.classList.add('d-none');
        }
    }

    async function candidatarSe(vagaId, btn) {
        const originalText = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = `<span class="spinner-border spinner-border-sm"></span>`;

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
                minhasCandidaturasIds.add(vagaId);
                renderizar(); 
            } else {
                const msg = await response.text();
                showToast("Atenção", msg, "warning");
            }
        } catch (e) {
            showToast("Erro", "Falha na comunicação.", "danger");
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    }

    // Função global para o HTML chamar
    window.triggerCandidatar = function(id, btn) {
        candidatarSe(id, btn);
    }

    window.triggerCancelar = function(vagaId, titulo) {
        vagaIdParaCancelar = vagaId;
        if(spanNomeVagaModal) spanNomeVagaModal.innerText = titulo;
        modalCancelamento.show();
    }

    // Configura botão do modal
    if (btnConfirmarModal) {
        // Clone para remover listeners antigos
        const novoBtn = btnConfirmarModal.cloneNode(true);
        btnConfirmarModal.parentNode.replaceChild(novoBtn, btnConfirmarModal);

        novoBtn.addEventListener('click', async () => {
            if (!vagaIdParaCancelar) return;

            const originalText = novoBtn.innerHTML;
            novoBtn.disabled = true;
            novoBtn.innerHTML = `<span class="spinner-border spinner-border-sm"></span>`;

            try {
                const response = await fetch(`http://localhost:8080/api/v1/candidaturas/cancelar/${vagaIdParaCancelar}`, {
                    method: 'DELETE',
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (response.ok) {
                    modalCancelamento.hide();
                    showToast("Cancelado", "Inscrição removida.", "info");
                    minhasCandidaturasIds.delete(vagaIdParaCancelar);
                    renderizar();
                } else {
                    const msg = await response.text();
                    showToast("Erro", msg, "warning");
                }
            } catch (e) {
                showToast("Erro", "Erro de conexão.", "danger");
            } finally {
                novoBtn.disabled = false;
                novoBtn.innerHTML = originalText;
                vagaIdParaCancelar = null;
                modalCancelamento.hide();
            }
        });
    }

    window.filtrarTab = function(tipo) {
        filtroAtual = tipo;
        
        // Atualiza visual das abas
        document.getElementById('tabTodas').classList.toggle('active', tipo === 'todas');
        document.getElementById('tabTodas').classList.toggle('text-muted', tipo !== 'todas');
        
        document.getElementById('tabMinhas').classList.toggle('active', tipo === 'minhas');
        document.getElementById('tabMinhas').classList.toggle('text-muted', tipo !== 'minhas');

        renderizar();
    }

    function renderizar() {
        const termo = searchInput ? searchInput.value.toLowerCase() : "";
        
        const listaFiltrada = Array.isArray(todasVagas) ? todasVagas.filter(vaga => {
            const jaCandidatou = minhasCandidaturasIds.has(vaga.id);
            
            if (filtroAtual === 'minhas' && !jaCandidatou) return false;

            const matchTexto = 
                vaga.titulo.toLowerCase().includes(termo) || 
                (vaga.nomeDepartamento && vaga.nomeDepartamento.toLowerCase().includes(termo)) ||
                (vaga.funcao && vaga.funcao.toLowerCase().includes(termo));
            
            return matchTexto;
        }) : [];

        vagasGrid.innerHTML = '';

        if (listaFiltrada.length === 0) {
            vagasGrid.classList.add('d-none');
            emptyState.classList.remove('d-none');
            const tituloEmpty = document.getElementById('emptyStateTitle');
            if(tituloEmpty) {
                tituloEmpty.innerText = filtroAtual === 'minhas' 
                    ? "Você ainda não se candidatou a nenhuma vaga." 
                    : "Nenhuma vaga encontrada.";
            }
            return;
        }

        emptyState.classList.add('d-none');
        vagasGrid.classList.remove('d-none');

        listaFiltrada.forEach(vaga => {
            const jaAplicado = minhasCandidaturasIds.has(vaga.id);
            
            const botaoAcao = jaAplicado 
                ? `<button class="btn btn-outline-danger w-100 fw-bold" onclick="window.triggerCancelar(${vaga.id}, '${vaga.titulo}')">
                        <i class="fas fa-times-circle me-2"></i>Cancelar Inscrição
                   </button>`
                : `<button class="btn btn-primary btn-apply w-100 shadow-sm" onclick="window.triggerCandidatar(${vaga.id}, this)">
                        Candidatar-se Agora
                   </button>`;

            const html = `
            <div class="col-md-6 col-xl-4">
                <div class="vaga-card h-100 ${jaAplicado ? 'inscrito border-success' : ''}">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start">
                            <span class="badge-dept">${vaga.nomeDepartamento || 'Geral'}</span>
                            ${jaAplicado 
                                ? '<span class="badge bg-success"><i class="fas fa-check"></i> Inscrito</span>' 
                                : '<span class="badge bg-light text-primary border">Aberta</span>'}
                        </div>
                        
                        <h5 class="vaga-title mt-3">${vaga.titulo}</h5>
                        <div class="text-muted small mb-3">
                            <i class="fas fa-tools me-1"></i> ${vaga.funcao}
                        </div>

                        <p class="vaga-desc">
                            ${vaga.descricao || 'Sem descrição.'}
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
        const parts = str.split('-');
        if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
        return str;
    }

    function showToast(titulo, msg, tipo) {
        const toastEl = document.getElementById('liveToast');
        const toastBody = document.getElementById('toastMessage');
        
        if (toastEl && toastBody) {
            toastEl.className = `toast align-items-center text-white bg-${tipo} border-0`;
            toastBody.innerText = `${titulo}: ${msg}`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        } else {
            // Fallback se não tiver toast no HTML
            console.log(`${titulo}: ${msg}`);
        }
    }

    if (searchInput) {
        searchInput.addEventListener('input', renderizar);
    }

    init();
});