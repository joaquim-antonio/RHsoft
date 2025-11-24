// Dados mockados para simular os contracheques
const mockContracheques = [
    {
        mes: "Ago, 2025",
        nome: "Neymar Santos Júnior",
        cargo: "Gerente",
        departamento: "Financeiro",
        cpf: "400.288.220-17",
        dataNascimento: "01/05/1999",
        salarioBase: 1600.80,
        horasExtras: 250.90,
        feriasRemuneradas: 190.45,
        inss: 190.45,
        irrf: 190.45,
        fgts: 190.45,
        valeTransporte: 130.00,
        valeAlimentacao: 130.00,
        gymPass: 130.00,
        codigoContracheque: "00076",
        cbo: "41105",
        baseInss: 0.00,
        baseFgts: 0.00,
        baseIrrf: 0.00,
        fgtsMes: 0.00,
        faixaIrrf: 0.00,
        mensagens: "(caixa de mensagens)",
        status: "Aguardando assinatura"
    },
    {
        mes: "Jul, 2025",
        nome: "Neymar Santos Júnior",
        cargo: "Gerente",
        departamento: "Financeiro",
        cpf: "400.288.220-17",
        dataNascimento: "01/05/1999",
        salarioBase: 1600.80,
        horasExtras: 0.00,
        feriasRemuneradas: 0.00,
        inss: 190.45,
        irrf: 190.45,
        fgts: 190.45,
        valeTransporte: 130.00,
        valeAlimentacao: 130.00,
        gymPass: 130.00,
        codigoContracheque: "00075",
        cbo: "41105",
        baseInss: 0.00,
        baseFgts: 0.00,
        baseIrrf: 0.00,
        fgtsMes: 0.00,
        faixaIrrf: 0.00,
        mensagens: "Parabéns pelo seu desempenho!",
        status: "Assinado"
    }
];

let contrachequeAtual = mockContracheques[0];

// Função utilitária para formatar moeda
function formatCurrency(value) {
    return `R$ ${value.toFixed(2).replace('.', ',')}`;
}

// Função para calcular o total líquido
function calcularTotalLiquido(contracheque) {
    const totalProventos = contracheque.salarioBase + contracheque.horasExtras + contracheque.feriasRemuneradas;
    const totalDescontos = contracheque.inss + contracheque.irrf + contracheque.fgts;
    return totalProventos - totalDescontos;
}

// Função para renderizar o contracheque na tela
function renderContracheque(contracheque) {
    const totalLiquido = calcularTotalLiquido(contracheque);

    // Dados Pessoais
    document.getElementById('nome').innerHTML = `<strong>Nome:</strong> ${contracheque.nome}`;
    document.getElementById('cargo').innerHTML = `<strong>Cargo:</strong> ${contracheque.cargo}`;
    document.getElementById('departamento').innerHTML = `<strong>Departamento:</strong> ${contracheque.departamento}`;
    document.getElementById('cpf').innerHTML = `<strong>CPF:</strong> ${contracheque.cpf}`;
    document.getElementById('data_nascimento').innerHTML = `<strong>Data de nascimento:</strong> ${contracheque.dataNascimento}`;

    // Salário
    document.getElementById('salario_base').textContent = formatCurrency(contracheque.salarioBase);
    document.getElementById('horas_extras_valor').textContent = formatCurrency(contracheque.horasExtras);
    document.getElementById('ferias_remuneradas_valor').textContent = formatCurrency(contracheque.feriasRemuneradas);
    document.getElementById('inss_desconto').textContent = formatCurrency(contracheque.inss);
    document.getElementById('irrf_desconto').textContent = formatCurrency(contracheque.irrf);
    document.getElementById('fgts_desconto').textContent = formatCurrency(contracheque.fgts);
    document.getElementById('total_liquido').textContent = formatCurrency(totalLiquido);

    // Códigos
    document.getElementById('codigo_contracheque').textContent = contracheque.codigoContracheque;
    document.getElementById('cbo_codigo').textContent = contracheque.cbo;

    // Cálculos
    document.getElementById('base_inss').textContent = formatCurrency(contracheque.baseInss);
    document.getElementById('base_fgts').textContent = formatCurrency(contracheque.baseFgts);
    document.getElementById('base_irrf').textContent = formatCurrency(contracheque.baseIrrf);
    document.getElementById('fgts_mes').textContent = formatCurrency(contracheque.fgtsMes);
    document.getElementById('faixa_irrf').textContent = contracheque.faixaIrrf;

    // Benefícios (Mantendo os valores fixos do HTML, pois não estão no mock)
    // document.getElementById('vale_transporte_valor').textContent = formatCurrency(contracheque.valeTransporte);
    // document.getElementById('vale_alimentacao_valor').textContent = formatCurrency(contracheque.valeAlimentacao);
    // document.getElementById('gym_pass_valor').textContent = formatCurrency(contracheque.gymPass);

    // Mensagens
    document.getElementById('mensagens_contracheque').textContent = contracheque.mensagens;

    // Status e Ações
    const statusSpan = document.getElementById('status_contracheque');
    const assinarBtn = document.getElementById('assinar');

    statusSpan.textContent = contracheque.status;
    statusSpan.className = contracheque.status === 'Assinado' ? 'valor_verde' : 'valor_azul';
    
    if (contracheque.status === 'Assinado') {
        assinarBtn.textContent = 'Contracheque Assinado';
        assinarBtn.disabled = true;
    } else {
        assinarBtn.textContent = 'Assinar contracheque';
        assinarBtn.disabled = false;
    }
}

// Função para simular a exportação
function exportarContracheque() {
    alert(`Simulando exportação do contracheque de ${contrachequeAtual.mes}.`);
    // Em um ambiente real, aqui seria a lógica para gerar um PDF ou outro formato.
}

// Função para simular a assinatura
function assinarContracheque() {
    if (contrachequeAtual.status !== 'Assinado') {
        if (confirm(`Tem certeza que deseja assinar o contracheque de ${contrachequeAtual.mes}?`)) {
            // Simula a mudança de status
            contrachequeAtual.status = 'Assinado';
            renderContracheque(contrachequeAtual);
            alert("Contracheque assinado com sucesso! (Mock)");
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
