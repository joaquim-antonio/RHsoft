// Dados mockados para simular as configurações atuais
const mockConfig = {
    nomeEmpresa: "RHSoft S.A.",
    emailSuporte: "suporte@rhsoft.com.br",
    fusoHorario: "-03:00",
    modoManutencao: true
};

// Função para carregar os dados mockados no formulário
function loadConfig() {
    document.getElementById('nomeEmpresa').value = mockConfig.nomeEmpresa;
    document.getElementById('emailSuporte').value = mockConfig.emailSuporte;
    document.getElementById('fusoHorario').value = mockConfig.fusoHorario;
    document.getElementById('manutencaoSwitch').checked = mockConfig.modoManutencao;
}

// Função para simular o salvamento das configurações
function saveConfig(event) {
    event.preventDefault();

    const newConfig = {
        nomeEmpresa: document.getElementById('nomeEmpresa').value,
        emailSuporte: document.getElementById('emailSuporte').value,
        fusoHorario: document.getElementById('fusoHorario').value,
        modoManutencao: document.getElementById('manutencaoSwitch').checked
    };

    // Simulação de salvamento (em um ambiente real, isso seria uma chamada de API)
    console.log("Novas Configurações Salvas (Mock):", newConfig);

    // Feedback visual para o usuário
    alert("Configurações salvas com sucesso! (Mock)");
}

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    loadConfig();
    document.getElementById('configForm').addEventListener('submit', saveConfig);
});
