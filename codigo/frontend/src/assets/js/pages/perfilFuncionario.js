document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const userJson = localStorage.getItem("usuario"); 

    if (!token || !userJson) {
        window.location.href = "login.html";
        return;
    }

    let pessoaRecebida = null; 

    const user = JSON.parse(userJson);

    if (user && user.cpf) {
        pessoaRecebida = { cpf: user.cpf };
    }

    async function init() {
        if (!(await validarPermissao())) return;
        await carregarSidebar();
        await buscarEPreencherDados();
        document.getElementById("formAtualizacaoPessoa").addEventListener("submit", salvarAtualizacoes);
    }

    async function carregarSidebar() {
        try {
            const response = await fetch("components/sidebar-funcionario.html");
            if (response.ok) {
                document.getElementById("sidebarCandidatoContainer").innerHTML =
                    await response.text();
                // Marca o link do Dashboard como ativo
                const linkDash = document.querySelector(
                    'a[href="perfilFuncionario.html"]'
                );
                if (linkDash) linkDash.classList.add("active");
            }
        } catch { }
    }

    async function validarPermissao() {
        if (user.role === "ROLE_ADMIN" || user.role === "ROLE_CANDIDATO") {
            alert("Acesso negado. Redirecionando...");
            window.location.href =
                user.role === "ROLE_ADMIN"
                    ? "portalAdministrador.html"
                    : "portalCandidato.html";
            return false;
        }
        return true;
    }
        
    function formatarDataParaInput(dataString) {
        if (dataString && dataString.includes('/')) {
             const parts = dataString.split('/');
             return `${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}`;
        }
        return dataString; 
    }
    
    function extrairDadosDoFormulario() {
        const dadosAtualizados = {
            cpf: document.getElementById("inputCPF").value,
            nome: document.getElementById("inputNome").value,
            sobrenome: document.getElementById("inputSobrenome").value,
            telefone: document.getElementById("inputTelefone").value,
            sexo: document.getElementById("inputSexo").value,
            dataNascimento: document.getElementById("inputDataNascimento").value,
            endereco: {
                id: pessoaRecebida.endereco?.id,
                rua: document.getElementById("inputRua").value,
                numero: document.getElementById("inputNumero").value,
                complemento: document.getElementById("inputComplemento").value,
                bairro: document.getElementById("inputBairro").value,
                logradouro: document.getElementById("inputRua").value,
                cep: document.getElementById("inputCEP").value,
                cidade: document.getElementById("inputCidade").value,
                estado: document.getElementById("inputEstado").value
            }
        };

        if (!dadosAtualizados.endereco.id) {
            delete dadosAtualizados.endereco.id;
        }
        return dadosAtualizados;
    }
        
    async function buscarEPreencherDados() {
        const cpf = pessoaRecebida?.cpf;
        if (!cpf) {
            console.error("CPF do usuário não encontrado. Não é possível buscar dados.");
            return;
        }

        try {
            const url = `http://localhost:8080/api/v1/pessoa/${cpf}`;
            const response = await fetch(url, {
                headers: { Authorization: `Bearer ${token}` },
            });

            if (!response.ok) {
                throw new Error("Falha ao buscar dados da pessoa.");
            }

            pessoaRecebida = await response.json();
            console.log("Dados recebidos:", pessoaRecebida);

            document.getElementById("inputCPF").value = pessoaRecebida.cpf || '';
            document.getElementById("inputNome").value = pessoaRecebida.nome || '';
            document.getElementById("inputSobrenome").value = pessoaRecebida.sobrenome || '';
            document.getElementById("inputTelefone").value = pessoaRecebida.telefone || '';
            document.getElementById("inputSexo").value = pessoaRecebida.sexo || '';

            document.getElementById("inputDataNascimento").value = pessoaRecebida.dataNascimento ? formatarDataParaInput(pessoaRecebida.dataNascimento) : '';

            if (pessoaRecebida.endereco) {
                document.getElementById("inputRua").value = pessoaRecebida.endereco.rua || '';
                document.getElementById("inputNumero").value = pessoaRecebida.endereco.numero || '';
                document.getElementById("inputComplemento").value = pessoaRecebida.endereco.complemento || '';
                document.getElementById("inputBairro").value = pessoaRecebida.endereco.bairro || '';
                document.getElementById("inputLogradouro").value = pessoaRecebida.endereco.logradouro || '';
                document.getElementById("inputCEP").value = pessoaRecebida.endereco.cep || '';
                document.getElementById("inputCidade").value = pessoaRecebida.endereco.cidade || '';
                document.getElementById("inputEstado").value = pessoaRecebida.endereco.estado || '';
            }

        } catch (error) {
            console.error("Erro ao carregar dados:", error);
            alert("Não foi possível carregar seus dados. Tente novamente.");
        }
    }
    
    async function salvarAtualizacoes(event) {
        event.preventDefault(); 

        if (!pessoaRecebida) {
            alert("Erro: Dados originais da Pessoa não foram carregados.");
            return;
        }

        const isConfirmed = confirm("Você tem certeza que deseja salvar estas alterações?");

        if (!isConfirmed) {
            console.log("Atualização cancelada pelo usuário.");
            return;
        }
        
        const dadosAtualizados = extrairDadosDoFormulario();

        const pessoaParaEnvio = {
            ...pessoaRecebida,
            ...dadosAtualizados,
            endereco: {
                ...pessoaRecebida.endereco,
                ...dadosAtualizados.endereco
            }
        };

        const cpf = pessoaParaEnvio.cpf;

        try {
            const url = `http://localhost:8080/api/v1/pessoa/${cpf}`;
            const response = await fetch(url, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(pessoaParaEnvio),
            });

            if (response.ok) {
                alert("Dados atualizados com sucesso!");
                console.log("Pessoa enviada", pessoaParaEnvio);
                await buscarEPreencherDados();
            } else {
                const errorBody = await response.json();
                throw new Error(`Erro ao atualizar dados: ${response.status} - ${errorBody.message || JSON.stringify(errorBody)}`);
            }
        } catch (error) {
            console.error("Falha ao salvar dados:", error);
            alert(`Ocorreu um erro ao tentar salvar. Verifique o console.`);
        }
    }

    init();
});