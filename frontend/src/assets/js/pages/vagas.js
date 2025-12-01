const API_BASE = 'http://localhost:8080';

// Helper para cabeçalhos com Token
const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};


const DominioService = {
    // Busca lista de cargos para popular o select
    listarCargos: async () => {
        const response = await fetch(`${API_BASE}/api/v1/cargo`, { headers: getAuthHeaders() });
        if (!response.ok) return []; 
        return await response.json();
    },
    // Busca lista de departamentos para popular o select
    listarDepartamentos: async () => {
        const response = await fetch(`${API_BASE}/api/v1/departamento`, { headers: getAuthHeaders() });
        if (!response.ok) return [];
        return await response.json();
    }
}

const CandidaturaAdminService = {
    // Lista candidatos de uma vaga específica
    listarCandidatosPorVaga: async (vagaId) => {
        const response = await fetch(`${API_BASE}/api/v1/candidaturas/vaga/${vagaId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error("Erro ao buscar candidatos");
        return await response.json(); 
    },

    // Atualiza status simples (ex: RECUSADA)
    atualizarStatus: async (candidaturaId, novoStatusEnum) => {
        const response = await fetch(`${API_BASE}/api/v1/candidaturas/${candidaturaId}/status`, {
            method: 'PATCH', 
            headers: getAuthHeaders(),
            body: JSON.stringify({ status: novoStatusEnum })
        });
        if (!response.ok) throw new Error('Erro ao atualizar status');
        return await response.json();
    },

    // Aprova e Contrata (Envia dados financeiros/contratuais)
    aprovarEContratar: async (candidaturaId, dadosContratacaoObj) => {
        const response = await fetch(`${API_BASE}/api/v1/candidaturas/${candidaturaId}/aprovar`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(dadosContratacaoObj) 
        });
        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(errorMessage || 'Erro ao aprovar contratação');
        }
        return await response.json();
    }
};

const VagaService = {
    listarTodas: async () => {
        const response = await fetch(`${API_BASE}/api/v1/vagas`, { headers: getAuthHeaders() });
        if (!response.ok) throw new Error("Erro ao carregar vagas");
        return await response.json();
    },
    criar: async (payload) => {
        const response = await fetch(`${API_BASE}/api/v1/vagas`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        if (!response.ok) {
            const errText = await response.text();
            throw new Error(errText || "Erro ao criar vaga");
        }
        return await response.json();
    },
    deletar: async (id) => {
        const response = await fetch(`${API_BASE}/api/v1/vagas/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || "Erro ao deletar vaga");
        }
    }
};

let CACHE_CARGOS = [];
let CACHE_DEPTOS = [];
let CACHE_VAGAS = []; 

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    if (!token) { window.location.href = "login.html"; return; }

    const vagasGrid = document.getElementById("vagas-grid");
    
    const modalCandidatos = new bootstrap.Modal(document.getElementById("modalCandidatos"));
    const modalContratacao = new bootstrap.Modal(document.getElementById("modalContratacao"));
    const modalNovaVaga = new bootstrap.Modal(document.getElementById("modalNovaVaga"));

    // Toast Notification
    const toastEl = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastMessage');
    const toastInstance = new bootstrap.Toast(toastEl);

    // Função auxiliar de Toast
    function showToast(msg, type='success') {
        toastBody.innerText = msg;
        toastEl.className = `toast align-items-center text-white border-0 bg-${type}`;
        toastInstance.show();
    }

    const selectAdicional = document.getElementById("hire-acrescimo");
    const selectNivel = document.getElementById("hire-insalubridade");

    if (selectAdicional && selectNivel) {
        // Estado inicial
        selectNivel.disabled = true;

        selectAdicional.addEventListener("change", function() {
            if (this.value === "INSALUBRIDADE") {
                selectNivel.disabled = false;
                selectNivel.value = "BAIXO"; // Valor padrão
            } else {
                selectNivel.disabled = true;
                selectNivel.value = ""; // Limpa seleção
            }
        });
    }

    // 1. Carrega listas de apoio (Cargos/Deptos)
    await carregarDadosDominio(); 
    // 2. Carrega as vagas na tela
    await carregarVagas();

    async function carregarDadosDominio() {
        try {
            // Faz as duas requisições em paralelo
            const [cargos, deptos] = await Promise.all([
                DominioService.listarCargos(),
                DominioService.listarDepartamentos()
            ]);
            
            CACHE_CARGOS = cargos;
            CACHE_DEPTOS = deptos;
            
            // Popula selects do Modal "Nova Vaga"
            popularSelect('new-cargoId', cargos, 'codigo', 'nome');
            popularSelect('new-deptoId', deptos, 'codigo', 'nome');

            // Popula selects do Modal "Contratação"
            popularSelect('hire-cargo', cargos, 'codigo', 'nome');
            popularSelect('hire-depto', deptos, 'codigo', 'nome');

        } catch (e) {
            console.error("Erro ao carregar domínios", e);
            showToast("Erro ao carregar listas de Cargos/Deptos", "warning");
        }
    }

    function popularSelect(elementId, dados, keyId, keyLabel) {
        const sel = document.getElementById(elementId);
        if(!sel) return;
        
        sel.innerHTML = '<option value="" selected disabled>Selecione...</option>';
        
        dados.forEach(item => {
            const opt = document.createElement('option');
            opt.value = item[keyId]; // ex: item.codigo
            opt.text = item[keyLabel]; // ex: item.nome
            sel.appendChild(opt);
        });
    }

    async function carregarVagas() {
        try {
            const vagas = await VagaService.listarTodas();
            CACHE_VAGAS = vagas; // Guarda em memória para usar na contratação
            renderizarVagas(vagas);
        } catch (error) {
            console.error(error);
            vagasGrid.innerHTML = `<div class="col-12 text-center text-danger py-5">Erro ao carregar vagas.</div>`;
        }
    }

    function renderizarVagas(vagas) {
        vagasGrid.innerHTML = "";
        
        // Aplica filtro visual (frontend side)
        const filtro = document.getElementById("filtroStatus").value; 
        const vagasFiltradas = vagas.filter(v => {
            if(filtro === 'abertas') return v.aberta;
            if(filtro === 'fechadas') return !v.aberta;
            return true;
        });

        if (vagasFiltradas.length === 0) {
            vagasGrid.innerHTML = `<div class="col-12 text-center text-muted py-5">Nenhuma vaga encontrada para este filtro.</div>`;
            return;
        }

        vagasFiltradas.forEach(v => {
            const isAberta = v.aberta;
            const badgeClass = isAberta ? "bg-success" : "bg-secondary";
            const badgeText = isAberta ? "ABERTA" : "FECHADA";

            const card = document.createElement("div");
            card.className = "col-md-6 col-lg-4";
            
            // Layout do Card
            card.innerHTML = `
                <div class="card card-vaga shadow-sm h-100">
                    <div class="card-header d-flex justify-content-between align-items-center bg-white">
                        <span class="small text-muted fw-bold">#${v.id}</span>
                        <div>
                            <span class="badge ${badgeClass} me-1">${badgeText}</span>
                            <button class="btn btn-sm text-danger border-0 p-0" onclick="deletarVaga(${v.id})" title="Excluir Vaga">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    <div class="card-body">
                        <h5 class="card-title fw-bold text-dark mb-1">${v.titulo}</h5>
                        <p class="card-text text-muted small mb-3">${v.funcao}</p>
                        
                        <div class="d-flex justify-content-between text-muted small border-top pt-3">
                            <span><i class="fa-solid fa-building me-1"></i> ${v.nomeDepartamento || 'Geral'}</span>
                            <span><i class="fa-solid fa-calendar me-1"></i> Até ${formatarData(v.dataLimite)}</span>
                        </div>
                    </div>
                    <div class="card-footer bg-white text-center py-3">
                        <button class="btn btn-outline-primary btn-sm w-100" onclick="verCandidatos(${v.id}, '${v.titulo}')">
                            <i class="fa-solid fa-users me-2"></i>Ver Candidatos
                        </button>
                    </div>
                </div>
            `;
            vagasGrid.appendChild(card);
        });
    }

    window.abrirModalNovaVaga = () => {
        document.getElementById("formNovaVaga").reset();
        modalNovaVaga.show();
    };

    window.salvarNovaVaga = async () => {
        const btn = document.querySelector("#modalNovaVaga .btn-primary");
        const originalText = btn.innerHTML;
        
        const payload = {
            titulo: document.getElementById("new-titulo").value,
            funcao: document.getElementById("new-funcao").value,
            descricao: document.getElementById("new-descricao").value,
            dataLimite: document.getElementById("new-dataLimite").value,
            cargoId: parseInt(document.getElementById("new-cargoId").value),
            departamentoId: parseInt(document.getElementById("new-deptoId").value)
        };

        if(!payload.titulo || !payload.dataLimite || isNaN(payload.cargoId) || isNaN(payload.departamentoId)) {
            showToast("Preencha todos os campos obrigatórios.", "warning");
            return;
        }

        btn.disabled = true;
        btn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Salvando...`;

        try {
            await VagaService.criar(payload);
            showToast("Vaga publicada com sucesso!", "success");
            modalNovaVaga.hide();
            await carregarVagas(); // Refresh lista
        } catch (error) {
            showToast("Erro: " + error.message, "danger");
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    };

    window.deletarVaga = async (id) => {
        if(!confirm("Tem certeza que deseja excluir esta vaga? A ação não poderá ser desfeita.")) return;
        try {
            await VagaService.deletar(id);
            showToast("Vaga excluída com sucesso.", "success");
            await carregarVagas();
        } catch (e) {
            showToast("Erro: " + e.message, "danger");
        }
    }


    // ------------------------------------------------------------------
    window.verCandidatos = async (vagaId, tituloVaga) => {
        // Atualiza título do modal
        document.getElementById("tituloVagaModal").innerText = tituloVaga;
        document.getElementById("modalCandidatos").dataset.vagaId = vagaId;

        const tbody = document.getElementById("listaCandidatosBody");
        const msgVazio = document.getElementById("msgSemCandidatos");
        
        // Loading...
        tbody.innerHTML = `<tr><td colspan="4" class="text-center py-4"><div class="spinner-border text-primary"></div></td></tr>`;
        msgVazio.classList.add("d-none");
        
        modalCandidatos.show();

        try {
            const candidatos = await CandidaturaAdminService.listarCandidatosPorVaga(vagaId);
            tbody.innerHTML = "";

            if (candidatos.length === 0) {
                msgVazio.classList.remove("d-none");
            } else {
                msgVazio.classList.add("d-none");
                candidatos.forEach(c => {
                    const tr = document.createElement("tr");
                    
                    // Configura Badge de Status
                    let badgeClass = "bg-secondary";
                    if(c.status === 'ABERTA') badgeClass = "bg-info text-dark";
                    if(c.status === 'ANALISE') badgeClass = "bg-warning text-dark";
                    if(c.status === 'APROVADA') badgeClass = "bg-success";
                    if(c.status === 'RECUSADA') badgeClass = "bg-danger";

                    // Configura Botões (Ações)
                    let actionsHtml = '';
                    if (c.status === 'ABERTA' || c.status === 'ANALISE') {
                        actionsHtml = `
                            <button class="btn btn-sm btn-success me-1" onclick="iniciarContratacao(${c.id})" title="Aprovar e Contratar">
                                <i class="fa-solid fa-check"></i>
                            </button>
                            <button class="btn btn-sm btn-danger" onclick="recusarCandidato(${c.id})" title="Recusar">
                                <i class="fa-solid fa-xmark"></i>
                            </button>
                        `;
                    } else if (c.status === 'APROVADA') {
                        actionsHtml = `<span class="text-success small fw-bold"><i class="fa-solid fa-user-check me-1"></i> Contratado</span>`;
                    } else {
                        actionsHtml = `<span class="text-muted small">Finalizado</span>`;
                    }

                    tr.innerHTML = `
                        <td>
                            <div class="fw-bold">${c.nomeCandidato}</div>
                            <div class="small text-muted">${c.emailCandidato || '...'}</div>
                        </td>
                        <td>${formatarData(c.dataCandidatura)}</td>
                        <td><span class="badge ${badgeClass}">${c.status}</span></td>
                        <td class="text-end">${actionsHtml}</td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        } catch (error) {
            console.error(error);
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao carregar lista.</td></tr>`;
        }
    };

    window.recusarCandidato = async (id) => {
        if(!confirm("Deseja realmente recusar este candidato?")) return;
        try {
            await CandidaturaAdminService.atualizarStatus(id, "RECUSADA");
            showToast("Candidato recusado.", "success");
            modalCandidatos.hide();
        } catch (e) {
            showToast("Erro: " + e.message, "danger");
        }
    };

    window.iniciarContratacao = (candidaturaId) => {
        document.getElementById("formContratacao").reset();
        document.getElementById("contratacaoCandidaturaId").value = candidaturaId;

        const vagaId = document.getElementById("modalCandidatos").dataset.vagaId;
        
        const vagaAtual = CACHE_VAGAS.find(v => v.id == vagaId);
        
        if (vagaAtual) {
            
            if (vagaAtual.cargoId) {
                document.getElementById("hire-cargo").value = vagaAtual.cargoId;
            }
            if (vagaAtual.departamentoId) {
                document.getElementById("hire-depto").value = vagaAtual.departamentoId;
            }
        }
        
        // Troca de modal (Fecha lista, abre form)
        const modalLista = bootstrap.Modal.getInstance(document.getElementById("modalCandidatos"));
        if(modalLista) modalLista.hide();
        
        modalContratacao.show();
    };

    window.confirmarContratacao = async () => {
        const id = document.getElementById("contratacaoCandidaturaId").value;
        const btn = document.querySelector("#modalContratacao .btn-success");
        const originalText = btn.innerHTML;
        
        const dados = {
            dataAdmissao: document.getElementById("hire-data").value,
            salario: parseFloat(document.getElementById("hire-salario").value),
            
            // Pega o valor do Select
            cargoId: parseInt(document.getElementById("hire-cargo").value),
            departamentoId: parseInt(document.getElementById("hire-depto").value),
            
            horasTrabalhadas: parseFloat(document.getElementById("hire-horas").value),
            tipoAcrescimo: document.getElementById("hire-acrescimo").value,
            tipoInsalubridade: document.getElementById("hire-insalubridade").value || null,
            
            nomeBanco: document.getElementById("hire-banco").value,
            agencia: document.getElementById("hire-agencia").value,
            numeroConta: document.getElementById("hire-conta").value,
            chavePix: document.getElementById("hire-pix").value
        };

        // Validação básica frontend
        if(!dados.dataAdmissao || !dados.salario || isNaN(dados.cargoId) || isNaN(dados.departamentoId)) {
            alert("Por favor, preencha Data, Salário, Cargo e Departamento.");
            return;
        }

        btn.disabled = true;
        btn.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Processando...`;

        try {
            await CandidaturaAdminService.aprovarEContratar(id, dados);
            showToast("Candidato contratado com sucesso!", "success");
            modalContratacao.hide();
        } catch (e) {
            console.error(e);
            showToast("Erro na contratação: " + e.message, "danger");
        } finally {
            btn.disabled = false;
            btn.innerHTML = originalText;
        }
    };


    // Listeners
    document.getElementById("filtroStatus").addEventListener("change", () => {
        carregarVagas();
    });

    // Helpers Data
    function formatarData(str) {
        if(!str) return "-";
        const parts = str.split('-'); 
        if(parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
        return new Date(str).toLocaleDateString('pt-BR');
    }
});