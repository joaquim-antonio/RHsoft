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
        
        carregarSidebarAdmin();
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
}

// Função para lidar com a mudança de mês
function handleMesChange(event) {
    const mesSelecionado = event.target.value;
    contrachequeAtual = mockContracheques.find(c => c.mes === mesSelecionado) || mockContracheques[0];
    renderContracheque(contrachequeAtual);
}

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    // 1. Popular o select de meses
    const selectMeses = document.getElementById('meses_contracheque');
    selectMeses.innerHTML = mockContracheques.map(c => `<option>${c.mes}</option>`).join('');
    selectMeses.addEventListener('change', handleMesChange);

    // 2. Renderizar o primeiro contracheque
    renderContracheque(contrachequeAtual);

    // 3. Adicionar listeners aos botões
    document.getElementById('exportar_contracheque').addEventListener('click', exportarContracheque);
    document.getElementById('assinar').addEventListener('click', assinarContracheque);
    document.getElementById('contatar_suporte').addEventListener('click', () => {
        alert("Simulando contato com o suporte. Um formulário ou chat seria aberto aqui.");
    });
});


document.addEventListener('DOMContentLoaded', () => {
    const payrollConfigForm = document.getElementById('payrollConfigForm');
    if (payrollConfigForm) {
        payrollConfigForm.addEventListener('submit', (event) => {
            event.preventDefault();
            const closingDate = document.getElementById('closingDate').value;
            const reopeningDeadline = document.getElementById('reopeningDeadline').value;
            const taxRules = document.getElementById('taxRules').value;

            alert(`Configurações da Folha de Pagamento Salvas (simulação):\nData de Fechamento: ${closingDate}\nPrazo para Reabertura: ${reopeningDeadline} dias\nRegras de Encargos: ${taxRules}`);

            // Em um ambiente real, aqui seria a lógica para enviar os dados para o backend.
        });
    }
});
