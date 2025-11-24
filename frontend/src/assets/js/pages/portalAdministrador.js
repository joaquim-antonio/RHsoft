document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // try{
    //     const response = await fetch('http://localhost:8080/auth/me'),{
    //         method: 'GET',
    //         headers: {
    //             'Authorization': `Bearer ${token}`
    //         }
    //     }); 

    //         if (!response.ok){
    //             throw new Error("Sessão inválida");
    //         }
    //     }
    //     const dadosUsuario = await response.josn;
    //     const roleDoBanco = dadosUsuario.role;

    //     if (roleDoBanco !== 'ADMIN'){
    //         alert()
    //     }
    async function init() {
        await carregarSidebarAdministrador();
        // await fetchDados();
    }


     async function carregarSidebarAdministrador() {
        try {
            const response = await fetch('components/sidebar-administrador.html');
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
    init();
});