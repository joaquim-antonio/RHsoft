document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const API_BASE = "http://localhost:8080"; 

    // Elementos da Interface
    const selectMeses = document.getElementById("meses_contracheque");
    const loading = document.getElementById("loading");
    const holeriteArea = document.getElementById("holeriteArea");
    const emptyState = document.getElementById("emptyState");
    const btnExportar = document.getElementById("exportar_contracheque");

    // Cache de dados
    let pagamentosCache = [];
    let usuarioLogado = null;

    // Verificação de Auth
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    // Inicialização
    await init();

    async function init() {
        try {
            // Busca Usuário
            usuarioLogado = await fetchUsuario();
            
            // Busca Contracheques
            if (usuarioLogado && usuarioLogado.cpf) {
                await carregarContracheques(usuarioLogado.cpf);
            }
        } catch (error) {
            console.error("Erro ao inicializar:", error);
            mostrarEstadoVazio();
        } finally {
            loading.classList.add("d-none");
        }
    }

    //  API CALLS

    async function fetchUsuario() {
        const resp = await fetch(`${API_BASE}/user/me`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!resp.ok) throw new Error("Erro ao buscar usuário");
        return await resp.json();
    }

    async function carregarContracheques(cpf) {
        try {
            const resp = await fetch(`${API_BASE}/pagamento/funcionario?cpf=${cpf}`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (resp.status === 404) {
                mostrarEstadoVazio();
                return;
            }

            if (!resp.ok) throw new Error("Erro na API de pagamentos");

            pagamentosCache = await resp.json();

            if (pagamentosCache.length === 0) {
                mostrarEstadoVazio();
                return;
            }

            // Ordena: Mais recente primeiro
            pagamentosCache.sort((a, b) => new Date(b.mesAnoReferencia) - new Date(a.mesAnoReferencia));

            popularSelectMeses();

        } catch (error) {
            console.error(error);
            mostrarEstadoVazio();
        }
    }

    function mostrarEstadoVazio() {
        holeriteArea.classList.add("d-none");
        emptyState.classList.remove("d-none");
        btnExportar.disabled = true;
    }

    //  RENDERIZAÇÃO DA TELA

    function popularSelectMeses() {
        selectMeses.innerHTML = '<option value="" disabled>Selecione o Mês</option>';

        pagamentosCache.forEach((pagamento, index) => {
            const option = document.createElement("option");
            option.value = index;
            option.textContent = formatarMesAnoExtenso(pagamento.mesAnoReferencia);
            
            if (index === 0) option.selected = true;
            selectMeses.appendChild(option);
        });

        // Renderiza o primeiro
        renderizarHoleriteTela(pagamentosCache[0]);

        // Evento de troca
        selectMeses.addEventListener("change", (e) => {
            renderizarHoleriteTela(pagamentosCache[e.target.value]);
        });
    }

    function renderizarHoleriteTela(data) {
        if (!data) return;

        holeriteArea.classList.remove("d-none");
        emptyState.classList.add("d-none");
        btnExportar.disabled = false;

        // Cabeçalho
        setText("nome", data.nomeFuncionario || usuarioLogado.nome);
        setText("cpf", formatarCPF(data.cpfFuncionario || usuarioLogado.cpf));
        setText("departamento", "Geral"); // Placeholder
        setText("data_admissao", "-");   // Placeholder
        
        // Referências
        setText("codigo_contracheque", data.codigo ? data.codigo.split('-').pop() : "---");
        setText("cbo_codigo", data.cbo || "---");
        setText("data_vencimento", formatarData(data.vencimento));

        // Tabela
        const tbody = document.getElementById("tabelaItens");
        tbody.innerHTML = "";

        const itensOrdenados = [...data.itens].sort((a, b) => (a.tipo === "PROVENTO" ? -1 : 1));

        itensOrdenados.forEach(item => {
            const tr = document.createElement("tr");
            const isProvento = item.tipo === "PROVENTO";
            const valorFmt = formatarMoeda(item.valor);

            tr.innerHTML = `
                <td>${item.nome}</td>
                <td class="text-center text-muted small">-</td>
                <td class="text-end text-success fw-bold">${isProvento ? valorFmt : ""}</td>
                <td class="text-end text-danger fw-bold">${!isProvento ? valorFmt : ""}</td>
            `;
            tbody.appendChild(tr);
        });

        // Totais
        setText("total_proventos", formatarMoeda(data.proventos));
        setText("total_descontos", formatarMoeda(data.descontos));
        setText("total_liquido", formatarMoeda(data.valorLiquido));
        setText("mensagens_contracheque", data.mensagens || "Sem mensagens.");
    }

    //  GERAÇÃO DE PDF 

    btnExportar.addEventListener("click", () => {
        const index = selectMeses.value;
        if (index === "") return;

        const data = pagamentosCache[index];
        const mesArquivo = selectMeses.options[selectMeses.selectedIndex].text.replace("/", "-");

        document.getElementById("print_referencia").textContent = formatarMesAnoCurto(data.mesAnoReferencia);
        document.getElementById("print_codigo").textContent = data.codigo ? data.codigo.split('-').pop() : "001";
        document.getElementById("print_nome").textContent = (data.nomeFuncionario || usuarioLogado.nome).toUpperCase();
        
        console.log(data);
        document.getElementById("print_cbo").textContent = "..."; 
        document.getElementById("print_cargo").textContent = (data.cbo || "COLABORADOR").toUpperCase(); 

        // Tabela Oficial
        const tbody = document.getElementById("print_itens_body");
        tbody.innerHTML = "";

        const itensOrdenados = [...data.itens].sort((a, b) => (a.tipo === "PROVENTO" ? -1 : 1));

        itensOrdenados.forEach(item => {
            const tr = document.createElement("tr");
            const valorFmt = formatarDecimal(item.valor); // Formato 1.000,00
            
            const codItem = Math.abs(item.nome.split("").reduce((a,b)=>{a=((a<<5)-a)+b.charCodeAt(0);return a&a},0)) % 900 + 100;

            tr.innerHTML = `
                <td class="w-cod">${codItem}</td>
                <td class="w-desc">${item.nome.toUpperCase()}</td>
                <td class="w-ref">-</td>
                <td class="w-valor">${item.tipo === "PROVENTO" ? valorFmt : ""}</td>
                <td class="w-valor">${item.tipo === "DESCONTO" ? valorFmt : ""}</td>
            `;
            tbody.appendChild(tr);
        });

        const linhasVazias = 20 - itensOrdenados.length;
        for(let i=0; i<linhasVazias; i++){
            tbody.innerHTML += `<tr><td></td><td>&nbsp;</td><td></td><td></td><td></td></tr>`;
        }

        // Totais 
        document.getElementById("print_mensagem").textContent = data.mensagens || "";
        document.getElementById("print_total_proventos").textContent = formatarDecimal(data.proventos);
        document.getElementById("print_total_descontos").textContent = formatarDecimal(data.descontos);
        document.getElementById("print_total_liquido").textContent = formatarDecimal(data.valorLiquido);

        // CÁLCULO DAS BASES
        
        // Encontra o valor do INSS descontado
        const itemINSS = data.itens.find(i => i.nome.toUpperCase().includes("INSS"));
        const valorDescontoINSS = itemINSS ? itemINSS.valor : 0;

        // Define as Bases
        const baseCalculoGeral = data.proventos; // Salário Bruto
        const baseIRRF = Math.max(0, baseCalculoGeral - valorDescontoINSS); // Bruto - INSS
        const valorFGTS = baseCalculoGeral * 0.08; // 8% do Bruto

        // Determina a Faixa IRRF (Estimativa 2025)
        let faixaIRRF = "Isento";
        if (baseIRRF > 2259.20 && baseIRRF <= 2826.65) faixaIRRF = "7,5%";
        else if (baseIRRF <= 3751.05) faixaIRRF = "15%";
        else if (baseIRRF <= 4664.68) faixaIRRF = "22,5%";
        else if (baseIRRF > 4664.68) faixaIRRF = "27,5%";

        document.getElementById("print_base_salario").textContent = formatarDecimal(data.salarioBase);
        document.getElementById("print_base_inss").textContent = formatarDecimal(baseCalculoGeral);
        document.getElementById("print_base_fgts").textContent = formatarDecimal(baseCalculoGeral);
        document.getElementById("print_fgts_mes").textContent = formatarDecimal(valorFGTS);
        document.getElementById("print_base_irrf").textContent = formatarDecimal(baseIRRF);
        document.getElementById("print_faixa_irrf").textContent = faixaIRRF;

        // Gerar PDF
        const element = document.getElementById("modeloImpressao");
        
        const opt = {
            margin:       5, 
            filename:     `Contracheque_${mesArquivo}.pdf`,
            image:        { type: 'jpeg', quality: 0.98 },
            html2canvas:  { scale: 2, logging: false, backgroundColor: '#ffffff' },
            jsPDF:        { unit: 'mm', format: 'a4', orientation: 'landscape' } 
        };

        const originalText = btnExportar.innerHTML;
        btnExportar.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Gerando...`;
        btnExportar.disabled = true;

        // Remove d-none temporariamente para renderizar
        element.classList.remove("d-none");

        html2pdf().set(opt).from(element).save()
            .then(() => {
                btnExportar.innerHTML = originalText;
                btnExportar.disabled = false;
                element.classList.add("d-none"); // Esconde novamente
            })
            .catch(err => {
                console.error(err);
                alert("Erro ao gerar PDF");
                btnExportar.innerHTML = originalText;
                btnExportar.disabled = false;
                element.classList.add("d-none");
            });
    });

    //  HELPERS


    function setText(id, text) {
        const el = document.getElementById(id);
        if (el) el.textContent = text;
    }

    function formatarMoeda(valor) {
        if (valor == null) return "R$ 0,00";
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
    }

    // Formato 1.000,00 (sem símbolo R$) 
    function formatarDecimal(valor) {
        if (valor == null) return "0,00";
        return valor.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function formatarMesAnoExtenso(anoMes) {
        if (!anoMes) return "";
        const [ano, mes] = anoMes.split('-');
        const data = new Date(ano, mes - 1);
        return data.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' }).toUpperCase();
    }

    function formatarMesAnoCurto(anoMes) {
        if (!anoMes) return "";
        const [ano, mes] = anoMes.split('-');
        return `${mes}/${ano}`;
    }

    function formatarData(dataString) {
        if (!dataString) return "-";
        const [ano, mes, dia] = dataString.split('-');
        return `${dia}/${mes}/${ano}`;
    }

    function formatarCPF(cpf) {
        if (!cpf) return "";
        return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
    }
});