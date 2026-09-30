document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const BASE_URL = "http://localhost:8080/api/v1/folha-pagamento";
    
    // Elementos de UI
    const loader = document.getElementById("loader");
    const contentArea = document.getElementById("contentArea");

    // Estado global
    let currentFolhaId = null;
    let currentStatus = null;

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    await init();

    async function init() {
        await carregarSidebarAdmin();
        await carregarFolhaAtual();
        configurarEventos();
    }

    // --- CARREGAMENTO ---

    async function carregarSidebarAdmin() {
        try {
            const response = await fetch('components/sidebar-administrador.html');
            if (response.ok) {
                const sidebarHTML = await response.text();
                document.getElementById("sidebarPlaceHolder").innerHTML = sidebarHTML;
                const links = document.querySelectorAll('.sidebar-item');
                links.forEach(link => {
                    // Verifica se é a página atual
                    if (link.href.includes('folhaPagamento.html')) {
                        link.classList.add('active');
                    }
                });

            } else {
                console.error('Erro ao carregar sidebar');
            }
        } catch (error) {
            console.error('Erro de rede sidebar:', error);
        }
    }

    async function carregarFolhaAtual() {
        toggleLoader(true);
        try {
            const response = await fetch(`${BASE_URL}/listar`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const folhas = await response.json();
                
                if (folhas && folhas.length > 0) {
                    // Ordena por ID decrescente para pegar a ÚLTIMA criada
                    folhas.sort((a, b) => b.id - a.id);
                    
                    const folhaAtual = folhas[0]; 
                    currentFolhaId = folhaAtual.id;
                    currentStatus = folhaAtual.status;
                    
                    renderizarCards(folhaAtual);
                    renderizarTabela(folhaAtual.pagamentos);
                    atualizarBotoesAcao(folhaAtual.status);
                } else {
                    renderizarEstadoVazio();
                    atualizarBotoesAcao(null); // Habilita apenas "Abrir"
                }
            } else {
                 if(response.status === 403) {
                     mostrarToast("Sessão expirada. Faça login novamente.", "bg-danger");
                     setTimeout(() => window.location.href = 'login.html', 2000);
                 } else {
                    mostrarToast("Erro ao buscar dados.", "bg-danger");
                 }
            }
        } catch (error) {
            console.error(error);
            mostrarToast("Erro de conexão com o servidor.", "bg-danger");
        } finally {
            toggleLoader(false);
        }
    }

    function toggleLoader(show) {
        if (show) {
            loader.classList.remove("d-none");
            contentArea.classList.add("opacity-50"); // Efeito visual de desabilitado
        } else {
            loader.classList.add("d-none");
            contentArea.classList.remove("opacity-50");
        }
    }

    function renderizarCards(folha) {
        const badge = document.getElementById("badgeStatus");
        const txtComp = document.getElementById("txtCompetencia");
        const txtLiq = document.getElementById("txtTotalLiquido");
        const txtData = document.getElementById("txtDataFechamento");

        const statusConfig = {
            'ABERTO': { class: 'badge-status-aberta', label: 'Aberta' },
            'FECHADA': { class: 'badge-status-fechada', label: 'Fechada' },
            'CONSOLIDADA': { class: 'badge-status-consolidada', label: 'Consolidada' },
            'ENVIADO': { class: 'bg-primary text-white', label: 'Enviada' }
        };

        const config = statusConfig[folha.status] || { class: 'bg-secondary text-white', label: folha.status };
        
        badge.className = `badge rounded-pill px-3 py-2 ${config.class}`;
        badge.textContent = config.label;

        // Tenta pegar a competência do primeiro pagamento ou usa data atual
        let competenciaTexto = "Mês Atual";
        if (folha.pagamentos && folha.pagamentos.length > 0) {
            competenciaTexto = formatarCompetencia(folha.pagamentos[0].mesAnoReferencia);
        }
        
        txtComp.textContent = competenciaTexto;
        txtLiq.textContent = formatarMoeda(folha.totalLiquido || 0);
        txtData.textContent = folha.dataFechamento ? formatarData(folha.dataFechamento) : "--/--/----";
    }

    function renderizarTabela(pagamentos) {
        const tbody = document.getElementById("tbodyPagamentos");
        const txtContador = document.getElementById("txtContadorFuncionarios");
        const txtFooter = document.getElementById("txtFooterInfo");
        
        tbody.innerHTML = "";

        if (!pagamentos || pagamentos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center py-5 text-muted">Nenhum holerite gerado nesta folha. Clique em "Gerar Cálculo".</td></tr>`;
            txtContador.textContent = "0 funcionários";
            txtFooter.textContent = "0 registros";
            return;
        }

        txtContador.textContent = `${pagamentos.length} funcionários`;
        txtFooter.textContent = `Mostrando ${pagamentos.length} registro(s)`;

        pagamentos.forEach(p => {
            const tr = document.createElement("tr");
            
            const editDisabled = currentStatus !== 'ABERTO' ? 'disabled' : '';
            const btnClass = currentStatus !== 'ABERTO' ? 'text-muted' : 'text-primary';

            tr.innerHTML = `
                <td class="ps-3">
                    <div class="fw-bold text-dark">${p.nomeFuncionario}</div>
                    <small class="text-muted" style="font-size: 0.75rem;">CPF: ${p.cpfFuncionario}</small>
                </td>
                <td><span class="text-secondary small fw-semibold">${p.cbo || 'N/A'}</span></td>
                
                <td class="text-value-positive">${formatarMoeda(p.salarioBase)}</td>
                
                <td class="text-value-positive">+ ${formatarMoeda(p.proventos)}</td>
                
                <td class="text-value-negative">- ${formatarMoeda(p.descontos)}</td>
                
                <td class="text-value-info">${formatarMoeda(p.valorLiquido)}</td>
                
                <td class="text-end pe-3">
                    <div class="dropdown">
                        <button class="btn-action-icon" type="button" data-bs-toggle="dropdown">
                            <i class="fa-solid fa-ellipsis-vertical"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0">
                            <li>
                                <button class="dropdown-item btn-editar" 
                                    data-codigo="${p.codigo}" 
                                    data-nome="${p.nomeFuncionario}"
                                    ${editDisabled}>
                                    <i class="fa-solid fa-pen me-2 ${btnClass}"></i> Editar Lançamentos
                                </button>
                            </li>
                            <li><button class="dropdown-item text-muted"><i class="fa-solid fa-file-pdf me-2"></i> Visualizar Holerite</button></li>
                        </ul>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });

        document.querySelectorAll(".btn-editar").forEach(btn => {
            btn.addEventListener("click", () => abrirModalEdicao(btn.dataset));
        });
    }

    function renderizarEstadoVazio() {
        document.getElementById("badgeStatus").textContent = "Nenhuma Folha";
        document.getElementById("badgeStatus").className = "badge rounded-pill bg-light text-muted border px-3 py-2";
        document.getElementById("tbodyPagamentos").innerHTML = `<tr><td colspan="7" class="text-center py-5 text-muted">Não há folha de pagamento ativa. Clique em "Abrir Mês" para começar.</td></tr>`;
        
        document.getElementById("txtCompetencia").textContent = "--";
        document.getElementById("txtTotalLiquido").textContent = "R$ 0,00";
        document.getElementById("txtDataFechamento").textContent = "--/--/----";
    }

    function atualizarBotoesAcao(status) {
        const btnAbrir = document.getElementById("btnAbrirFolha");
        const btnGerar = document.getElementById("btnGerarCalculo");
        const btnAcoes = document.getElementById("btnAcoes");
        
        const btnFechar = document.getElementById("btnFecharFolha");
        const btnConsolidar = document.getElementById("btnConsolidarFolha");
        const btnReabrir = document.getElementById("btnReabrirFolha");

        // Desabilita tudo por padrão
        btnAbrir.disabled = true;
        btnGerar.disabled = true;
        btnAcoes.disabled = true;
        
        if (!status) {
            btnAbrir.disabled = false; // Se não tem folha, pode abrir
            return;
        }

        btnAcoes.disabled = false; // Habilita menu de ações se existe folha

        switch (status) {
            case 'ABERTO':
                btnGerar.disabled = false;
                // Ações permitidas: Fechar
                break;
            case 'FECHADA':
                // Ações permitidas: Consolidar, Reabrir
                break;
            case 'CONSOLIDADA':
                // Ações permitidas: Reabrir (com restrição de data no backend)
                break;
        }
    }

    // --- EVENTOS E ACTIONS ---

    function configurarEventos() {
        document.getElementById("btnAbrirFolha").addEventListener("click", () => executarAcao(`${BASE_URL}/abrir`, 'POST'));
        
        // Gerar cálculo precisa passar o ID da folha
        document.getElementById("btnGerarCalculo").addEventListener("click", () => {
            if(currentFolhaId) executarAcao(`${BASE_URL}/gerar/${currentFolhaId}`, 'POST');
        });

        // Ações do Dropdown
        document.getElementById("btnFecharFolha").addEventListener("click", () => executarAcao(`${BASE_URL}/fechar/${currentFolhaId}`, 'POST'));
        document.getElementById("btnConsolidarFolha").addEventListener("click", () => executarAcao(`${BASE_URL}/consolidar/${currentFolhaId}`, 'POST'));
        document.getElementById("btnReabrirFolha").addEventListener("click", () => executarAcao(`${BASE_URL}/reabrir/${currentFolhaId}`, 'POST'));
        
        // Modal Salvar
        document.getElementById("btnSalvarEdicao").addEventListener("click", salvarEdicaoPagamento);

        // Filtro Local
        document.getElementById("searchFuncionario").addEventListener("keyup", (e) => {
            const termo = e.target.value.toLowerCase();
            const linhas = document.querySelectorAll("#tbodyPagamentos tr");
            linhas.forEach(tr => {
                const texto = tr.textContent.toLowerCase();
                tr.style.display = texto.includes(termo) ? "" : "none";
            });
        });
    }

    async function executarAcao(url, method) {
        mostrarToast("Processando solicitação...", "bg-primary");
        
        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                mostrarToast("Operação realizada com sucesso!", "bg-success");
                // Recarrega dados após 1.5s
                setTimeout(() => carregarFolhaAtual(), 1500);
            } else {
                const erroTexto = await response.text();
                try {
                   const erroJson = JSON.parse(erroTexto);
                   mostrarToast(`Erro: ${erroJson.message || erroJson.error}`, "bg-danger");
                } catch(e) {
                   mostrarToast(`Erro: ${erroTexto}`, "bg-danger");
                }
            }
        } catch (error) {
            mostrarToast("Falha na comunicação com o servidor.", "bg-danger");
        }
    }

    // --- MODAL ---

    function abrirModalEdicao(dataset) {
        document.getElementById("editCodigoPagamento").value = dataset.codigo;
        document.getElementById("editNomeFuncionario").value = dataset.nome;
        document.getElementById("editHorasExtras").value = "";
        document.getElementById("editAdicional").value = "";
        
        new bootstrap.Modal(document.getElementById("modalEditarPagamento")).show();
    }

    async function salvarEdicaoPagamento() {
        const btnSalvar = document.getElementById("btnSalvarEdicao");
        const spinner = btnSalvar.querySelector(".spinner-border");
        
        // Loading state do botão
        btnSalvar.disabled = true;
        spinner.classList.remove("d-none");

        const codigo = document.getElementById("editCodigoPagamento").value;
        const horas = document.getElementById("editHorasExtras").value;
        const bonus = document.getElementById("editAdicional").value;

        const payload = {
            codigo: codigo,
            quantidadeHorasExtras: horas ? parseFloat(horas) : 0,
            adicionalManual: bonus ? parseFloat(bonus) : 0
        };

        try {
            const response = await fetch(`${BASE_URL}/pagamento/editar`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                mostrarToast("Lançamento atualizado!", "bg-success");
                bootstrap.Modal.getInstance(document.getElementById("modalEditarPagamento")).hide();
                carregarFolhaAtual();
            } else {
                mostrarToast("Erro ao salvar alterações.", "bg-danger");
            }
        } catch (error) {
            mostrarToast("Erro de conexão.", "bg-danger");
        } finally {
            btnSalvar.disabled = false;
            spinner.classList.add("d-none");
        }
    }

    // --- HELPERS ---
    function formatarMoeda(valor) {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
    }

    function formatarData(dataIso) {
        if(!dataIso) return "";
        const [ano, mes, dia] = dataIso.split('-');
        return `${dia}/${mes}/${ano}`;
    }

    function formatarCompetencia(anoMes) {
        if (!anoMes) return "Mês Atual";
        const [ano, mes] = anoMes.split('-');
        const meses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
        return `${meses[parseInt(mes) - 1]} de ${ano}`;
    }

    function mostrarToast(msg, bgClass) {
        const toastEl = document.getElementById("liveToast");
        const toastBody = document.getElementById("toastMessage");
        
        // Reseta classes de cor
        toastEl.className = `toast align-items-center text-white border-0 ${bgClass}`;
        toastBody.textContent = msg; 
        
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }
});