document.addEventListener('DOMContentLoaded', async () => {
    const token = localStorage.getItem('token');
    
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const loadingSpinner = document.getElementById('loadingSpinner');
    const profileContent = document.getElementById('profileContent');
    const btnSalvar = document.getElementById('btnSalvar');
    
    const inputNome = document.getElementById('inputNome');
    const inputCpf = document.getElementById('inputCpf');
    const inputTelefone = document.getElementById('inputTelefone');

    let usuarioCpf = '';



    // Função auxiliar para formatar CPF
    function mascaraCPF(valor) {
        valor = valor.replace(/\D/g, ""); // Remove não-números
        if (valor.length > 11) valor = valor.slice(0, 11); // Limita a 11 dígitos

        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
        return valor;
    }

    // Listener para CPF (enquanto digita)
    if (inputCpf) {
        inputCpf.addEventListener('input', (e) => {
            e.target.value = mascaraCPF(e.target.value);
        });
    }

    // Listener para Telefone (enquanto digita)
    if (inputTelefone) {
        inputTelefone.addEventListener('input', (e) => {
            let valor = e.target.value;
            
            valor = valor.replace(/\D/g, ""); // Remove letras
            if (valor.length > 11) valor = valor.slice(0, 11); // Limita tamanho

            if (valor.length > 10) {
                valor = valor.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
            } else if (valor.length > 5) {
                valor = valor.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, "($1) $2-$3");
            } else if (valor.length > 2) {
                valor = valor.replace(/^(\d{2})(\d{0,5}).*/, "($1) $2");
            } else {
                valor = valor.replace(/^(\d*)/, "($1");
            }
            e.target.value = valor;
        });
    }

    async function init() {
        await carregarSidebar();
        await carregarDadosCandidato();
    }

    async function carregarSidebar() {
        try {
            const response = await fetch('components/sidebar-candidato.html');
            if (response.ok) {
                const html = await response.text();
                document.getElementById('sidebarCandidatoContainer').innerHTML = html;
                
                const linkPerfil = document.getElementById('link-perfil');
                if (linkPerfil) linkPerfil.classList.add('active');
            }
        } catch (e) {
            console.error("Erro ao carregar sidebar", e);
        }
    }

    async function carregarDadosCandidato() {
        try {
            const userData = JSON.parse(localStorage.getItem('usuario'));
            if (userData) {
                usuarioCpf = userData.cpf;
                inputNome.value = userData.nome || '';
                inputCpf.value = mascaraCPF(usuarioCpf || '');
            }

            const response = await fetch(`http://localhost:8080/api/v1/Candidato/${usuarioCpf}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const candidato = await response.json();
                
                let nomeExibicao = candidato.nome || '';
                
                if (candidato.sobrenome) {
                    nomeExibicao += ` ${candidato.sobrenome}`;
                }

                inputNome.value = nomeExibicao;
                if(candidato.telefone) {
                    inputTelefone.value = candidato.telefone;
                    inputTelefone.dispatchEvent(new Event('input')); 
                }

                renderizarLista('listaHabilidades', candidato.habilidades || [], 'habilidade');
                renderizarLista('listaFormacao', candidato.formacao || [], 'formacao');
                renderizarLista('listaExperiencias', candidato.experiencias || [], 'experiencia', true);

            } else if (response.status === 403) {
                logout(); 
            }

        } catch (error) {
            console.error("Erro ao carregar perfil:", error);
            showToast("Erro", "Não foi possível carregar seus dados.", "danger");
        } finally {
            loadingSpinner.classList.add('d-none');
            profileContent.classList.remove('d-none');
        }
    }

    function renderizarLista(containerId, itens, tipo, isTextarea = false) {
        const container = document.getElementById(containerId);
        container.innerHTML = '';
        itens.forEach(item => adicionarItemNoDOM(container, tipo, item, isTextarea));
    }

    window.adicionarItem = function(containerId, tipo, isTextarea = false) {
        const container = document.getElementById(containerId);
        adicionarItemNoDOM(container, tipo, '', isTextarea);
    }

    function adicionarItemNoDOM(container, tipo, valor = '', isTextarea = false) {
        const div = document.createElement('div');
        div.className = 'input-group mb-2'; 
        
        let inputHTML = '';
        if (isTextarea) {
            inputHTML = `<textarea class="form-control" rows="2" placeholder="Descreva...">${valor}</textarea>`;
        } else {
            inputHTML = `<input type="text" class="form-control" value="${valor}" placeholder="Digite aqui...">`;
        }

        div.innerHTML = `
            ${inputHTML}
            <button class="btn btn-outline-danger" type="button" onclick="this.parentElement.remove()">
                <i class="fas fa-trash-alt"></i>
            </button>
        `;
        container.appendChild(div);
    }

    btnSalvar.addEventListener('click', async () => {
        const originalText = btnSalvar.innerHTML;
        btnSalvar.disabled = true;
        btnSalvar.innerHTML = `<span class="spinner-border spinner-border-sm"></span> Salvando...`;

        const nomeCompletoDigitado = inputNome.value.trim();

        if (!nomeCompletoDigitado) {
            showToast("Atenção", "O nome é obrigatório.", "warning");
            btnSalvar.disabled = false;
            btnSalvar.innerHTML = originalText;
            return;
        }

        const indiceEspaco = nomeCompletoDigitado.indexOf(' ');

        let nomeFinal = '';
        let sobrenomeFinal = '';

        if (indiceEspaco === -1) {
            nomeFinal = nomeCompletoDigitado;
            sobrenomeFinal = ''; 
        } else {
            nomeFinal = nomeCompletoDigitado.substring(0, indiceEspaco);
            sobrenomeFinal = nomeCompletoDigitado.substring(indiceEspaco + 1).trim();
        }
        const rawTelefone = inputTelefone.value;
        const telefoneLimpo = rawTelefone.replace(/\D/g, "");


        const payload = {
            nome: nomeFinal,          
            sobrenome: sobrenomeFinal, 
            telefone: telefoneLimpo,
            habilidades: coletarValores('listaHabilidades'),
            formacao: coletarValores('listaFormacao'),
            experiencias: coletarValores('listaExperiencias')
        };

        try {
            const response = await fetch(`http://localhost:8080/api/v1/Candidato/${usuarioCpf}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                showToast("Sucesso", "Perfil atualizado com sucesso!", "success");
                
                const currentData = JSON.parse(localStorage.getItem('usuario')) || {};
                
                currentData.nome = sobrenomeFinal ? `${nomeFinal} ${sobrenomeFinal}` : nomeFinal;
                
                localStorage.setItem('usuario', JSON.stringify(currentData));

                inputNome.value = currentData.nome;

            } else {
                const erro = await response.text();
                showToast("Erro ao Salvar", erro || "Verifique os dados.", "warning");
            }
        } catch (e) {
            console.error(e);
            showToast("Erro", "Falha de conexão com o servidor.", "danger");
        } finally {
            btnSalvar.disabled = false;
            btnSalvar.innerHTML = originalText;
        }
    });

    // Helpers
    function coletarValores(containerId) {
        const container = document.getElementById(containerId);
        const inputs = container.querySelectorAll('input, textarea');
        return Array.from(inputs).map(i => i.value).filter(v => v.trim() !== '');
    }

    function showToast(titulo, msg, tipo) {
        const toastEl = document.getElementById('liveToast');
        const toastBody = document.getElementById('toastMessage') || toastEl.querySelector('.toast-body');
        
        toastEl.className = `toast align-items-center text-white bg-${tipo} border-0`;
        if(toastBody) toastBody.innerText = `${titulo}: ${msg}`;
        
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }

    init();
});