document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    async function init() {
        const usuarioValido = await validarSessao();
        if (!usuarioValido) return;

        await carregarSidebarAdministrador();
        await carregarDadosDashboard();
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


     async function carregarSidebarAdministrador() {
        try {
            const response = await fetch('components/sidebar-administrador.html');
            if (response.ok) {
                const html = await response.text();
                document.getElementById('sidebarCandidatoContainer').innerHTML = html;
                
                const linkVagas = document.getElementById('link-vagas');
                if (linkVagas){
                    linkVagas.classList.add('active');
                    linkVagas.style.color = 'var(--cor-azul-escuro)'
                }
            }
        } catch (e) {
            console.error("Erro ao carregar sidebar:", e);
        }
    }

    async function carregarDadosDashboard() {
        try {
            const response = await fetch('http://localhost:8080/api/v1/dashboard/distribuicao',{
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            })

            if(response.ok){
               const data = await response.json();
               console.log(data);
               
               const contadorElement = document.querySelector('.chart-center-text .display-4');
               if (contadorElement) contadorElement.textContent = data.totalFuncionarios;

               renderizarGrafico(data.segmentos);
            } else{
                console.error("Erro ao buscar dados da dashboard");
            }
        } catch (error) {
            console.error("Erro na requisição: ", error);
        }
    }

    function renderizarGrafico(segmentos){
        const ctx = document.getElementById('funcionariosChart');
        if (!ctx) return;

        const labels = segmentos.map(item => item.nomeDepartamento);
        const valores = segmentos.map(item => item.count);

        const cores = ['#80C0D4', '#305c69', '#C7E9FF', '#FFD966', '#FF5C5C', '#70FF66'];

        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: valores,
                    backgroundColor: cores,
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            usePointStyle: true,
                            padding: 20,
                            font: {
                                family: "'INTER', sans-serif"
                            }
                        }
                    }
                },
                cutout: '80%' //Esprressura do donut
            }
        });
    }

    init();
});