document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const API_URL = "http://localhost:8080/api/v1/configuracoes";

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    // Inicialização
    await init();

    async function init() {
        if (!(await validarPermissaoAdmin())) return;
        
        carregarDadosConfiguracao();
        configurarEventos();
    }

    // Valida se é ADMIN
    async function validarPermissaoAdmin() {
        try {
            const response = await fetch("http://localhost:8080/user/me", {
                headers: { Authorization: `Bearer ${token}` },
            });
            if (response.ok) {
                const user = await response.json();
                if (user.role !== "ROLE_ADMIN") {
                    alert("Acesso restrito a administradores.");
                    window.location.href = "portalFuncionario.html";
                    return false;
                }
                return true;
            }
            throw new Error("Falha na autenticação");
        } catch {
            window.location.href = "login.html";
            return false;
        }
    }


    // Busca os dados do backend e popula o formulário
    async function carregarDadosConfiguracao() {
        try {
            const response = await fetch(API_URL, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (response.ok) {
                const data = await response.json();
                preencherFormulario(data);
            } else {
                mostrarToast("Erro ao carregar configurações.", "danger");
            }
        } catch (error) {
            console.error(error);
            mostrarToast("Erro de conexão com o servidor.", "danger");
        }
    }

    function preencherFormulario(data) {
        // Geral
        setVal('diaFechamentoMensal', data.diaFechamentoMensal);
        setVal('diasLimiteReabertura', data.diasLimiteReabertura);
        setVal('salarioMinimoVigente', data.salarioMinimoVigente);
        setVal('tetoInss', data.tetoInss);
        setVal('valorValeAlimentacao', data.valorValeAlimentacao);
        
        // Percentuais
        setVal('percentualValeTransporte', data.percentualValeTransporte);
        setVal('percentualPericulosidade', data.percentualPericulosidade);
        setVal('percentualInsalubridadeMin', data.percentualInsalubridadeMin);
        setVal('percentualInsalubridadeMedia', data.percentualInsalubridadeMedia);
        setVal('percentualInsalubridadeMax', data.percentualInsalubridadeMax);

        // IRRF - Dedução
        setVal('irrfDeducaoPorDependente', data.irrfDeducaoPorDependente);

        // Preencher Listas INSS
        if(data.faixasInss && data.faixasInss.length >= 4) {
            setVal('inssLim1', data.faixasInss[0].limiteSuperior);
            setVal('inssAliq1', data.faixasInss[0].aliquota);

            setVal('inssLim2', data.faixasInss[1].limiteSuperior);
            setVal('inssAliq2', data.faixasInss[1].aliquota);

            setVal('inssLim3', data.faixasInss[2].limiteSuperior);
            setVal('inssAliq3', data.faixasInss[2].aliquota);

            setVal('inssAliq4', data.faixasInss[3].aliquota);
        }

        // Preencher Listas IRRF
        if(data.faixasIrrf && data.faixasIrrf.length >= 5) {
            setVal('irrfLim1', data.faixasIrrf[0].limiteSuperior);

            setVal('irrfLim2', data.faixasIrrf[1].limiteSuperior);
            setVal('irrfAliq2', data.faixasIrrf[1].aliquota);
            setVal('irrfDed2', data.faixasIrrf[1].deducao);

            setVal('irrfLim3', data.faixasIrrf[2].limiteSuperior);
            setVal('irrfAliq3', data.faixasIrrf[2].aliquota);
            setVal('irrfDed3', data.faixasIrrf[2].deducao);

            setVal('irrfLim4', data.faixasIrrf[3].limiteSuperior);
            setVal('irrfAliq4', data.faixasIrrf[3].aliquota);
            setVal('irrfDed4', data.faixasIrrf[3].deducao);

            setVal('irrfAliq5', data.faixasIrrf[4].aliquota);
            setVal('irrfDed5', data.faixasIrrf[4].deducao);
        }
    }

    // Helper para setar valor por ID
    function setVal(id, val) {
        const el = document.getElementById(id);
        if (el && val !== undefined && val !== null) {
            el.value = val;
        }
    }

    // Helper para pegar valor por ID (retorna null se vazio)
    function getVal(id) {
        const el = document.getElementById(id);
        return el && el.value !== "" ? parseFloat(el.value) : null;
    }

    function configurarEventos() {
        document.getElementById("btnSalvar").addEventListener("click", salvarConfiguracoes);
    }

    async function salvarConfiguracoes() {
        const btn = document.getElementById("btnSalvar");
        const originalText = btn.innerHTML;
        
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Salvando...';
        btn.disabled = true;

        // Constrói objeto de INSS
        const faixasInss = [
            { ordem: 1, aliquota: getVal('inssAliq1'), limiteSuperior: getVal('inssLim1'), limiteInferior: 0 },
            { ordem: 2, aliquota: getVal('inssAliq2'), limiteSuperior: getVal('inssLim2'), limiteInferior: getVal('inssLim1') },
            { ordem: 3, aliquota: getVal('inssAliq3'), limiteSuperior: getVal('inssLim3'), limiteInferior: getVal('inssLim2') },
            { ordem: 4, aliquota: getVal('inssAliq4'), limiteSuperior: getVal('tetoInss'), limiteInferior: getVal('inssLim3') }
        ];

        // Constrói objeto de IRRF
        const faixasIrrf = [
            { ordem: 1, aliquota: 0, deducao: 0, limiteSuperior: getVal('irrfLim1') },
            { ordem: 2, aliquota: getVal('irrfAliq2'), deducao: getVal('irrfDed2'), limiteSuperior: getVal('irrfLim2') },
            { ordem: 3, aliquota: getVal('irrfAliq3'), deducao: getVal('irrfDed3'), limiteSuperior: getVal('irrfLim3') },
            { ordem: 4, aliquota: getVal('irrfAliq4'), deducao: getVal('irrfDed4'), limiteSuperior: getVal('irrfLim4') },
            { ordem: 5, aliquota: getVal('irrfAliq5'), deducao: getVal('irrfDed5'), limiteSuperior: null }
        ];

        const payload = {
            // Geral
            diaFechamentoMensal: getVal('diaFechamentoMensal'),
            diasLimiteReabertura: getVal('diasLimiteReabertura'),
            salarioMinimoVigente: getVal('salarioMinimoVigente'),
            tetoInss: getVal('tetoInss'),
            valorValeAlimentacao: getVal('valorValeAlimentacao'),

            // Percentuais
            percentualValeTransporte: getVal('percentualValeTransporte'),
            percentualPericulosidade: getVal('percentualPericulosidade'),
            percentualInsalubridadeMin: getVal('percentualInsalubridadeMin'),
            percentualInsalubridadeMedia: getVal('percentualInsalubridadeMedia'),
            percentualInsalubridadeMax: getVal('percentualInsalubridadeMax'),

            // IRRF Global
            irrfDeducaoPorDependente: getVal('irrfDeducaoPorDependente'),

            // Listas
            faixasInss: faixasInss,
            faixasIrrf: faixasIrrf
        };

        try {
            const response = await fetch(API_URL, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                mostrarToast("Configurações atualizadas com sucesso!", "success");
            } else {
                const errorData = await response.json();
                
                let errorMsg = "Erro ao salvar.";

                // Erro de Validação de Campos (@Valid / MethodArgumentNotValidException)
                if (errorData.errors) {
                    errorMsg = "<strong>Verifique os seguintes campos:</strong><ul class='mb-0 mt-1 ps-3 small'>";
                    Object.entries(errorData.errors).forEach(([field, msg]) => {
                        errorMsg += `<li>${msg}</li>`; // Ex: "salarioMinimoVigente: deve ser positivo"
                    });
                    errorMsg += "</ul>";
                } 
                // Erro de Regra de Negócio (RegraNegocioException / IllegalArgumentException)
                else if (errorData.message) {
                    errorMsg = `<strong>Atenção:</strong><br>${errorData.message}`;
                }

                mostrarToast(errorMsg, "danger");
            }
        } catch (error) {
            console.error(error);
            mostrarToast("Erro de comunicação com o servidor.", "danger");
        } finally {
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    }

    // Função de Toast
    function mostrarToast(mensagem, tipo) {
        const toastEl = document.getElementById("liveToast");
        const toastBody = document.getElementById("toastMessage");
        
        toastEl.className = `toast align-items-center text-white bg-${tipo} border-0`;
        
        toastBody.innerHTML = mensagem; 

        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }
});