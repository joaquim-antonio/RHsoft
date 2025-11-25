document.addEventListener('DOMContentLoaded', init);

async function init() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    const usuarioValido = await validarSessao();
    if (!usuarioValido) return;
    await carregarSidebarFuncionario();
}

async function carregarSidebarFuncionario() {
    try {
        const response = await fetch('components/sidebar-funcionario.html');
        if (response.ok) {
            const html = await response.text();
            document.getElementById('sidebarFuncionarioContainer').innerHTML = html;
            // const linkFolha = document.getElementById('link-folha-pagamento');
            // if (linkFolha){
            //     linkFolha.classList.add('active');
            //     linkFolha.style.color = 'var(--cor-azul-escuro)'
            // }
        }
    } catch (e) {
        console.error("Erro ao carregar sidebar");
    }
}

async function validarSessao() {
    
}