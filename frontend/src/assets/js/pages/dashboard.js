
function initializeDashboard() {
    

    const isDarkMode = document.body.classList.contains('dark-mode');

    const corAzul = '#50C8FF';
    const corVerde = '#2ECC71';
    const corVermelho = '#E74C3C';
    const corAmarelo = '#F1C40F'; 
    
    const corGrid = isDarkMode ? '#444' : '#ddd';
    const corTexto = isDarkMode ? '#fff' : '#333';
    const corLinhaSaldo = isDarkMode ? '#FFFFFF' : '#000000';
    const corTooltipBg = isDarkMode ? 'rgba(0, 0, 0, 0.8)' : 'rgba(255, 255, 255, 0.9)';
    const corTooltipText = isDarkMode ? '#fff' : '#000';

    Chart.defaults.color = corTexto;
    Chart.defaults.plugins.legend.display = false; 
    Chart.defaults.plugins.tooltip.backgroundColor = corTooltipBg;
    Chart.defaults.plugins.tooltip.titleColor = corTooltipText;
    Chart.defaults.plugins.tooltip.bodyColor = corTooltipText;
    Chart.defaults.plugins.tooltip.titleFont = { weight: 'bold' };
    Chart.defaults.plugins.tooltip.bodyFont = { size: 14 };
    Chart.defaults.plugins.tooltip.padding = 10;
    Chart.defaults.plugins.tooltip.cornerRadius = 8;
    Chart.defaults.plugins.tooltip.displayColors = true; 

    //GRÁFICO DE FUNCIONÁRIOS (DONUT) - ATUALIZADO   
    async function loadDistribuicaoChart() {
        const token = localStorage.getItem("authToken");
        if (!token) return; 
        const coresSegmentos = [corVerde, corAzul, corVermelho, corAmarelo];
        try {
            const response = await fetch(`${API_URL}/api/v1/dashboard/distribuicao`, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            
            if (!response.ok) throw new Error('Falha ao buscar dados do dashboard');
            
            const data = await response.json();

            const textoCentral = document.querySelector('.chart-center-text .display-4');
            if (textoCentral) {
                textoCentral.textContent = data.totalFuncionarios;
            }

            //Preparar dados
            const labels = data.segmentos.map(s => s.nomeDepartamento);
            const counts = data.segmentos.map(s => s.count);

            //Renderizar o Gráfico
            const ctxFunc = document.getElementById('funcionariosChart').getContext('2d');
            new Chart(ctxFunc, {
                type: 'doughnut',
                data: {
                    labels: labels, 
                    datasets: [{
                        data: counts, 
                        backgroundColor: coresSegmentos, 
                        borderWidth: 0,
                        spacing: 8,
                        borderRadius: 10
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    cutout: '80%',
                    plugins: {
                        tooltip: {
                            enabled: true, 
                            callbacks: {
                                label: function(context) {
                                    const label = context.label || '';
                                    const value = context.raw || 0;
                                    return `${label}: ${value}`;
                                }
                            }
                        },
                        legend: { 
                            display: true,
                            position: 'bottom',
                            labels: {
                                color: corTexto,
                                boxWidth: 10,
                                usePointStyle: true,
                            }
                        }
                    }
                }
            });

        } catch (error) {
            console.error("Erro ao carregar gráfico de distribuição:", error);
        }
    }

    loadDistribuicaoChart();

    //GRÁFICO DE HORAS EXTRAS (BARRAS HORIZONTAIS)
    const ctxDept = document.getElementById('departamentosChart').getContext('2d');
    new Chart(ctxDept, {
        type: 'bar',
        data: {
            labels: ['T.I', 'Financeiro', 'Outros'],
            datasets: [{
                data: [4000, 7500, 2500],
                backgroundColor: [corAzul, corVerde, corVermelho],
                borderRadius: 5
            }]
        },
        options: {
            responsive: true,
            indexAxis: 'y',
            scales: {
                x: {
                    beginAtZero: true,
                    grid: { color: corGrid },
                    ticks: { callback: (value) => 'R$ ' + value / 1000 + 'k' }
                },
                y: { grid: { display: false } }
            }
        }
    });


    // GRÁFICO DE DESPESAS (LINHA + BARRAS)
    const ctxBalanco = document.getElementById('balancoChart').getContext('2d');
    new Chart(ctxBalanco, {
        type: 'bar',
        data: {
            labels: ['Ja', 'Fe', 'Ma', 'Ab', 'Ma', 'Ju', 'Ju', 'Ag', 'Se', 'Ou', 'No', 'De'],
            datasets: [
                {
                    type: 'line',
                    label: 'Saldo',
                    data: [5000, 6000, -2000, 4000, 6000, 7000, 6000, -1000, 3000, 6500, 2000, 5000],
                    borderColor: corLinhaSaldo,
                    borderWidth: 3,
                    tension: 0.4,
                    pointRadius: 0
                },
                {
                    label: 'Receita',
                    data: [10000, 10000, 8000, 9000, 10000, 10000, 10000, 8000, 9000, 10000, 10000, 10000],
                    backgroundColor: corVerde,
                    borderRadius: 5
                },
                {
                    label: 'Despesa',
                    data: [-5000, -4000, -10000, -5000, -4000, -3000, -4000, -9000, -6000, -3500, -8000, -5000],
                    backgroundColor: corVermelho,
                    borderRadius: 5
                }
            ]
        },
        options: {
            responsive: true,
            scales: {
                x: { stacked: false, grid: { display: false } },
                y: {
                    stacked: false,
                    beginAtZero: true,
                    grid: { color: corGrid },
                    ticks: { callback: (value) => 'R$' + value / 1000 + 'k' }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    align: 'start',
                    labels: {
                        usePointStyle: true,
                        boxWidth: 10,
                        color: corTexto
                    }
                }
            }
        }
    });
    
} 