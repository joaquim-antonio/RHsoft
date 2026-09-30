document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("token");
    const API_BASE = "http://localhost:8080";

    // Elementos do DOM
    const tableBody = document.getElementById("func-list");
    const searchInput = document.getElementById("search");
    
    // Instância do Modal
    const modalElement = document.getElementById('modalFuncionario');
    const modalInstance = modalElement ? new bootstrap.Modal(modalElement) : null;

    // Instância do Toast
    const toastEl = document.getElementById('liveToast');
    const toastBody = document.getElementById('toastMessage');
    const toastInstance = toastEl ? new bootstrap.Toast(toastEl) : null;

    // Elemento do CEP para o Autocomplete
    const cepInput = document.getElementById("inp-cep");

    // Verificação de Segurança
    if (!token) {
        window.location.href = "login.html";
        return;
    }

    if (await validarAdmin()) {
        await carregarFuncionarios();
    }

    if (cepInput) {
        cepInput.addEventListener("input", async (e) => {
            // Remove caracteres não numéricos
            let cep = e.target.value.replace(/\D/g, '');

            // Aplica máscara visual (00000-000)
            if (cep.length > 5) {
                e.target.value = cep.replace(/^(\d{5})(\d)/, '$1-$2');
            }

            // Se tiver 8 dígitos, faz a busca
            if (cep.length === 8) {
                await buscarCep(cep);
            }
        });
    }

    async function buscarCep(cep) {
        // Feedback visual
        toggleCamposEndereco(true);

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();

            if (data.erro) {
                showToast("CEP não encontrado.", "warning");
                limparCamposEndereco();
                toggleCamposEndereco(false); 
                return;
            }

            // Preenche os campos automaticamente
            setVal("inp-rua", data.logradouro);
            setVal("inp-bairro", data.bairro);
            setVal("inp-cidade", data.localidade);
            setVal("inp-estado", data.uf);

            // Libera edição
            toggleCamposEndereco(false);
            
            // Foca no número
            document.getElementById("inp-numero").focus();

        } catch (error) {
            console.error("Erro ViaCEP:", error);
            showToast("Erro ao buscar endereço. Preencha manualmente.", "error");
            toggleCamposEndereco(false);
        }
    }

    function toggleCamposEndereco(bloquear) {
        const campos = ["inp-rua", "inp-bairro", "inp-cidade", "inp-estado"];
        campos.forEach(id => {
            const el = document.getElementById(id);
            if (el) {
                el.readOnly = bloquear;
                if (bloquear) el.value = "...";
            }
        });
    }

    //  TOAST

    function showToast(message, type = 'success') {
        if (!toastInstance) return;

        toastBody.innerText = message;
        toastEl.classList.remove('bg-success', 'bg-danger', 'bg-warning');
        
        if (type === 'success') {
            toastEl.classList.add('bg-success'); 
        } else if (type === 'error') {
            toastEl.classList.add('bg-danger'); 
        } else {
            toastEl.classList.add('bg-warning'); 
        }
        
        toastInstance.show();
    }

    async function validarAdmin() {
        try {
            const resp = await fetch(`${API_BASE}/user/me`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            const user = await resp.json();
            
            const role = user.role || "";
            if (!role.toUpperCase().includes("ADMIN")) {
                alert("Acesso restrito a Administradores."); 
                window.location.href = "portalFuncionario.html";
                return false;
            }
            return true;
        } catch (e) {
            console.error(e);
            window.location.href = "login.html";
            return false;
        }
    }

    async function carregarFuncionarios() {
        try {
            const resp = await fetch(`${API_BASE}/api/v1/pessoa`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            if (!resp.ok) throw new Error("Erro ao listar funcionários");
            
            const pessoas = await resp.json();
            renderizarTabela(pessoas);

        } catch (error) {
            console.error(error);
            showToast("Não foi possível carregar a lista de funcionários.", "error");
        }
    }

    function renderizarTabela(lista) {
        if (!tableBody) return;
        tableBody.innerHTML = "";
        
        if (lista.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-4">Nenhum funcionário encontrado.</td></tr>`;
            return;
        }

        lista.forEach(f => {
            const tr = document.createElement("tr");
            
            const nome = f.nome || "";
            const sobrenome = f.sobrenome || "";
            const iniciais = (nome.charAt(0) + sobrenome.charAt(0)).toUpperCase();
            
            const cargo = f.cargo ? f.cargo.nome : "Sem Cargo";
            const depto = f.departamento ? f.departamento.nome : "Geral";
            const telefone = f.telefone || "-";
            
            const statusClass = "badge-status-ativo"; 
            const statusTexto = "Ativo"; 

            tr.innerHTML = `
                <td>
                    <div class="d-flex align-items-center">
                        <div class="avatar-circle me-3">${iniciais}</div>
                        <div>
                            <div class="fw-bold text-dark">${nome} ${sobrenome}</div>
                            <div class="small text-muted">${formatarCPF(f.cpf)}</div>
                        </div>
                    </div>
                </td>
                <td>
                    <div class="fw-bold text-dark" style="font-size: 0.9rem;">${cargo}</div>
                    <div class="small text-muted">${depto}</div>
                </td>
                <td>
                    <div class="d-flex align-items-center text-muted">
                        <i class="fa-solid fa-phone me-2 small"></i> 
                        <span style="font-size: 0.9rem;">${formatarTelefone(telefone)}</span>
                    </div>
                </td>
                <td><span class="${statusClass}">${statusTexto}</span></td>
                <td class="text-end">
                    <button class="btn btn-sm btn-light border" onclick="abrirModalEdicao('${f.cpf}')" title="Editar">
                        <i class="fa-solid fa-pen text-primary"></i>
                    </button>
                    <button class="btn btn-sm btn-light border ms-1" onclick="deletarFuncionario('${f.cpf}')" title="Excluir">
                        <i class="fa-solid fa-trash text-danger"></i>
                    </button>
                </td>
            `;
            tableBody.appendChild(tr);
        });
    }

    window.openAddModal = () => {
        document.getElementById("formFuncionario").reset();
        document.getElementById("edit-mode").value = "false";
        document.getElementById("modalTitle").innerHTML = '<i class="fa-solid fa-user-plus me-2"></i>Cadastrar Funcionário';
        
        // Habilita campos exclusivos de criação
        document.getElementById("inp-cpf").disabled = false;
        document.getElementById("div-senha").classList.remove("d-none");
        
        // Garante que campos de endereço estejam editáveis
        toggleCamposEndereco(false);

        if (modalInstance) modalInstance.show();
    };

    window.abrirModalEdicao = async (cpf) => {
        try {
            const resp = await fetch(`${API_BASE}/api/v1/pessoa/${cpf}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            if (!resp.ok) throw new Error("Erro ao buscar detalhes");
            const data = await resp.json();

            // Dados Pessoais
            setVal("inp-cpf", data.cpf);
            setVal("inp-nome", data.nome);
            setVal("inp-sobrenome", data.sobrenome);
            setVal("inp-nascimento", data.dataNascimento); 
            setVal("inp-genero", data.sexo); 
            setVal("inp-telefone", data.telefone);
            
            // Dados Contratuais
            if (data.cargo) setVal("inp-cargo", data.cargo.codigo);
            if (data.departamento) setVal("inp-departamento", data.departamento.codigo);
            setVal("inp-admissao", data.dataAdmissao);
            setVal("inp-salario", data.salario);
            setVal("inp-horas", data.horasTrabalhadas);
            setVal("inp-acrescimo", data.tipoAcrescimo);
            setVal("inp-insalubridade", data.tipoInsalubridade);

            // Endereço
            if (data.endereco) {
                setVal("inp-cep", data.endereco.cep);
                setVal("inp-rua", data.endereco.logradouro || data.endereco.rua);
                setVal("inp-numero", data.endereco.numero);
                setVal("inp-bairro", data.endereco.bairro);
                setVal("inp-cidade", data.endereco.cidade);
                setVal("inp-estado", data.endereco.estado);
            } else {
                limparCamposEndereco();
            }

            // Dados Bancários
            if (data.contaBancaria) {
                setVal("inp-banco", data.contaBancaria.nomeBanco);
                setVal("inp-agencia", data.contaBancaria.agencia);
                setVal("inp-conta", data.contaBancaria.numero);
                setVal("inp-pix", data.contaBancaria.chavePix);
            } else {
                limparCamposBanco();
            }

            document.getElementById("inp-cpf").disabled = true; 
            document.getElementById("edit-mode").value = "true";
            document.getElementById("div-senha").classList.add("d-none"); 
            document.getElementById("modalTitle").innerHTML = '<i class="fa-solid fa-user-pen me-2"></i>Editar Funcionário';
            
            // Garante que campos de endereço estejam editáveis
            toggleCamposEndereco(false);

            if (modalInstance) modalInstance.show();

        } catch (e) {
            console.error(e);
            showToast("Erro ao carregar dados do funcionário.", "error");
        }
    };

    window.salvarFuncionario = async () => {
        const isEdit = document.getElementById("edit-mode").value === "true";
        const cpf = getVal("inp-cpf");
        const btnSalvar = document.querySelector("#modalFuncionario .btn-primary");
        
        const textoOriginal = btnSalvar.innerHTML;
        btnSalvar.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Salvando...`;
        btnSalvar.disabled = true;

        const payload = {
            cpf: cpf,
            nome: getVal("inp-nome"),
            sobrenome: getVal("inp-sobrenome"),
            dataNascimento: getVal("inp-nascimento") || null,
            sexo: getVal("inp-genero"),
            telefone: getVal("inp-telefone"),
            
            dataAdmissao: getVal("inp-admissao") || null,
            salario: parseFloat(getVal("inp-salario") || 0),
            horasTrabalhadas: parseFloat(getVal("inp-horas") || 220),
            
            endereco: {
                cep: getVal("inp-cep"),
                logradouro: getVal("inp-rua"),
                rua: getVal("inp-rua"),
                numero: getVal("inp-numero"),
                bairro: getVal("inp-bairro"),
                cidade: getVal("inp-cidade"),
                estado: getVal("inp-estado")
            },
            
            contaBancaria: {
                nomeBanco: getVal("inp-banco"),
                agencia: getVal("inp-agencia"),
                numero: getVal("inp-conta"),
                chavePix: getVal("inp-pix")
            }
        };

        let url, method;

        if (isEdit) {
            url = `${API_BASE}/api/v1/pessoa/${cpf}`;
            method = "PUT";
            
            payload.cargoId = parseInt(getVal("inp-cargo") || 0);
            payload.departamentoId = parseInt(getVal("inp-departamento") || 0);
            payload.tipoAcrescimo = getVal("inp-acrescimo") || ""; 
            payload.tipoInsalubridade = getVal("inp-insalubridade") || ""; 

        } else {
            url = `${API_BASE}/auth/register-funcionario`;
            method = "POST";
            
            payload.cargo = { codigo: parseInt(getVal("inp-cargo") || 0) };
            payload.departamento = { codigo: parseInt(getVal("inp-departamento") || 0) };
            payload.tipoAcrescimo = getVal("inp-acrescimo") || null; 
            payload.tipoInsalubridade = getVal("inp-insalubridade") || null;
            payload.password = getVal("inp-password"); 
        }

        try {
            const resp = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (resp.ok) {
                showToast("Dados salvos com sucesso!", "success");
                if (modalInstance) modalInstance.hide();
                await carregarFuncionarios(); 
            } else {
                const erroTexto = await resp.text();
                let msgFinal = erroTexto;
                try {
                    const jsonErr = JSON.parse(erroTexto);
                    if(jsonErr.message) msgFinal = jsonErr.message;
                } catch(e) {} 

                showToast("Erro ao salvar: " + msgFinal, "error");
            }

        } catch (e) {
            console.error(e);
            showToast("Erro de conexão com o servidor.", "error");
        } finally {
            btnSalvar.innerHTML = textoOriginal;
            btnSalvar.disabled = false;
        }
    };

    window.deletarFuncionario = async (cpf) => {
        if (!confirm(`Tem certeza que deseja excluir o funcionário CPF ${formatarCPF(cpf)}? \nEsta ação não pode ser desfeita.`)) return;
        
        try {
            const resp = await fetch(`${API_BASE}/api/v1/pessoa/${cpf}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` }
            });

            if (resp.ok) {
                showToast("Funcionário excluído com sucesso.", "success");
                await carregarFuncionarios();
            } else {
                const err = await resp.text();
                showToast("Erro ao excluir: " + err, "error");
            }
        } catch (e) {
            console.error(e);
            showToast("Erro de conexão.", "error");
        }
    };

    // PESQUISA 
    if (searchInput) {
        searchInput.addEventListener("keyup", (e) => {
            const termo = e.target.value.toLowerCase();
            const linhas = document.querySelectorAll("#func-list tr");
            
            linhas.forEach(tr => {
                const textoLinha = tr.innerText.toLowerCase();
                tr.style.display = textoLinha.includes(termo) ? "" : "none";
            });
        });
    }

    // HELPERS 
    function getVal(id) { 
        const el = document.getElementById(id);
        return el ? el.value : ""; 
    }

    function setVal(id, val) { 
        const el = document.getElementById(id);
        if (el) {
            el.value = (val !== null && val !== undefined) ? val : ""; 
        }
    }

    function limparCamposEndereco() {
        setVal("inp-cep", ""); setVal("inp-rua", ""); setVal("inp-numero", "");
        setVal("inp-bairro", ""); setVal("inp-cidade", ""); setVal("inp-estado", "");
    }

    function limparCamposBanco() {
        setVal("inp-banco", ""); setVal("inp-agencia", ""); 
        setVal("inp-conta", ""); setVal("inp-pix", "");
    }

    function formatarCPF(v) { 
        if (!v) return "";
        return v.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4"); 
    }

    function formatarTelefone(v) {
        if (!v) return "-";
        if (v.length === 11) return v.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
        return v;
    }
});