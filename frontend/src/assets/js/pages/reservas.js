// Dados mockados para simular as reservas do usuário
const mockReservas = [
    {
        id: 'RES001',
        recurso: 'Sala de Reunião Alpha',
        dataInicio: '2025-12-01 10:00',
        dataFim: '2025-12-01 12:00',
        status: 'Confirmada'
    },
    {
        id: 'RES002',
        recurso: 'Projetor Multimídia',
        dataInicio: '2025-12-05 14:30',
        dataFim: '2025-12-05 16:00',
        status: 'Pendente'
    },
    {
        id: 'RES003',
        recurso: 'Veículo Corporativo - Placa ABC-1234',
        dataInicio: '2025-11-28 08:00',
        dataFim: '2025-11-28 18:00',
        status: 'Cancelada'
    },
    {
        id: 'RES004',
        recurso: 'Estação de Trabalho 5B',
        dataInicio: '2025-12-10 09:00',
        dataFim: '2025-12-10 18:00',
        status: 'Confirmada'
    },
];

/**
 * Retorna a classe CSS apropriada para o status da reserva.
 * @param {string} status - O status da reserva.
 * @returns {string} A classe CSS.
 */
function getStatusClass(status) {
    switch (status) {
        case 'Confirmada':
            return 'status-confirmada';
        case 'Pendente':
            return 'status-pendente';
        case 'Cancelada':
            return 'status-cancelada';
        default:
            return '';
    }
}

/**
 * Renderiza a lista de reservas na tabela.
 * @param {Array<Object>} reservas - A lista de objetos de reserva.
 */
function renderReservas(reservas) {
    const tableBody = document.getElementById('reservasTableBody');
    const noReservasMessage = document.getElementById('noReservasMessage');
    tableBody.innerHTML = ''; // Limpa o corpo da tabela

    if (reservas.length === 0) {
        noReservasMessage.classList.remove('d-none');
        return;
    }

    noReservasMessage.classList.add('d-none');

    reservas.forEach(reserva => {
        const row = tableBody.insertRow();

        // Coluna ID
        row.insertCell().textContent = reserva.id;

        // Coluna Recurso
        row.insertCell().textContent = reserva.recurso;

        // Coluna Data Início
        row.insertCell().textContent = reserva.dataInicio;

        // Coluna Data Fim
        row.insertCell().textContent = reserva.dataFim;

        // Coluna Status com Badge
        const statusCell = row.insertCell();
        statusCell.innerHTML = `<span class="status-badge ${getStatusClass(reserva.status)}">${reserva.status}</span>`;

        // Coluna Ações
        const actionsCell = row.insertCell();
        actionsCell.innerHTML = `
            <button class="btn btn-sm btn-outline-primary me-2" onclick="alert('Detalhes da Reserva ${reserva.id}')">Detalhes</button>
            ${reserva.status === 'Confirmada' ? 
                `<button class="btn btn-sm btn-outline-danger" onclick="alert('Cancelar Reserva ${reserva.id}')">Cancelar</button>` : 
                ''
            }
        `;
    });
}

// Inicialização: Renderiza as reservas mockadas quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    // A função setup.js já deve ter carregado o header e a sidebar
    renderReservas(mockReservas);
});
