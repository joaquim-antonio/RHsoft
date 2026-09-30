const API_URL = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registerForm');
    const cpfInput = document.getElementById('cpf');
    const telefoneInput = document.getElementById('telefone');
    const cepInput = document.getElementById('cep');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const registerButton = document.getElementById('registerButton');
    const formMessage = document.getElementById('formMessage');
    const formMessageText = document.getElementById('formMessageText');

    function formatCPF(value) {
        value = value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);
        return value
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
    }

    function formatTelefone(value) {
        value = value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);
        return value
            .replace(/(\d{2})(\d)/, '($1) $2')
            .replace(/(\d{5})(\d)/, '$1-$2');
    }

    function formatCEP(value) {
        value = value.replace(/\D/g, '');
        if (value.length > 8) value = value.slice(0, 8);
        return value.replace(/(\d{5})(\d)/, '$1-$2');
    }

    cpfInput.addEventListener('input', function() {
        this.value = formatCPF(this.value);
        validateCPF();
    });

    telefoneInput.addEventListener('input', function() {
        this.value = formatTelefone(this.value);
    });

    cepInput.addEventListener('input', function() {
        this.value = formatCEP(this.value);
        if (this.value.length === 9) {
            searchCEP(this.value);
        }
    });

   
    function validateCPF() {
        const cpfValue = cpfInput.value.replace(/\D/g, '');
        const feedback = document.getElementById('cpfFeedback');
        
        if (cpfValue.length === 0) {
             feedback.textContent = ''; 
             cpfInput.classList.remove('is-valid', 'is-invalid');
             return false; 
        }
        
        if (cpfValue.length !== 11) {
            feedback.textContent = 'CPF deve ter 11 dígitos.';
            cpfInput.classList.add('is-invalid');
            cpfInput.classList.remove('is-valid');
            return false;
        }
        
        feedback.textContent = ''; 
        cpfInput.classList.remove('is-invalid');
        cpfInput.classList.add('is-valid');
        return true;
    }

    function evaluatePasswordStrength() {
        const password = passwordInput.value;
        let strength = 0;
        if (password.length >= 8) strength++;
        if (password.length >= 12) strength++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[!@#$%^&*]/.test(password)) strength++;
        return strength;
    }

    passwordInput.addEventListener('input', function() {
        const strength = evaluatePasswordStrength();
        const strengthIndicator = document.getElementById('passwordStrength');
        if (!strengthIndicator) {
            const indicator = document.createElement('div');
            indicator.id = 'passwordStrength';
            indicator.style.marginTop = '0.5rem';
            this.parentNode.appendChild(indicator);
        }
        const strengthMessages = ['Muito fraca', 'Fraca', 'Média', 'Boa', 'Muito forte'];
        const strengthColors = ['danger', 'warning', 'info', 'primary', 'success'];
        document.getElementById('passwordStrength').innerHTML = `
            <div class="progress" style="height: 4px;">
                <div class="progress-bar bg-${strengthColors[strength - 1] || 'danger'}" 
                     role="progressbar" style="width: ${strength * 20}%"></div>
            </div>
            <small class="text-${strengthColors[strength - 1] || 'danger'}">
                Força: ${strengthMessages[strength - 1] || 'Muito fraca'}
            </small>
        `;
    });

    confirmPasswordInput.addEventListener('input', function() {
        const feedback = document.getElementById('passwordFeedback');
        if (passwordInput.value !== this.value) {
            feedback.textContent = 'As senhas não conferem.';
            confirmPasswordInput.classList.add('is-invalid');
            confirmPasswordInput.classList.remove('is-valid');
        } else {
            feedback.textContent = ''; 
            confirmPasswordInput.classList.remove('is-invalid');
            confirmPasswordInput.classList.add('is-valid');
        }
    });

    async function searchCEP(cep) {
        const cleanCEP = cep.replace(/\D/g, '');
        if (cleanCEP.length !== 8) return;

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cleanCEP}/json/`);
            const data = await response.json();

            if (data.erro) {
                showMessage('CEP não encontrado.', 'warning');
                return;
            }

            document.getElementById('bairro').value = data.bairro || '';
            document.getElementById('cidade').value = data.localidade || '';
            document.getElementById('estado').value = data.uf || '';

            const logradouroCompleto = data.logradouro || '';
            const primeiroEspaco = logradouroCompleto.indexOf(' ');
            
            if (primeiroEspaco > -1 && primeiroEspaco <= 10) { 
                document.getElementById('logradouro').value = logradouroCompleto.substring(0, primeiroEspaco);
                document.getElementById('rua').value = logradouroCompleto.substring(primeiroEspaco + 1);
            } else {
                document.getElementById('logradouro').value = logradouroCompleto;
                document.getElementById('rua').value = ''; 
            }

            showMessage('Endereço preenchido com sucesso!', 'success');
            document.getElementById('numero').focus();
        } catch (error) {
            console.error('Erro ao buscar CEP:', error);
        }
    }

    
    function showMessage(message, type = 'info') {
        formMessage.classList.remove('show', 'alert-success', 'alert-danger', 'alert-warning', 'alert-info');
        formMessageText.textContent = message;
        formMessage.classList.add(`alert-${type}`);
        void formMessage.offsetWidth; 

        formMessage.classList.add('show');
    }

   
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        if (!validateCPF()) {
            showMessage('CPF inválido. Verifique o campo.', 'danger');
            return;
        }

        if (passwordInput.value !== confirmPasswordInput.value) {
            showMessage('As senhas não conferem.', 'danger');
            return;
        }

        if (!form.checkValidity()) {
            e.stopPropagation();
            form.classList.add('was-validated');
            showMessage('Por favor, preencha todos os campos obrigatórios.', 'danger');
            return;
        }

        if (evaluatePasswordStrength() < 2) {
            showMessage('A senha é muito fraca. Tente combinar letras e números.', 'warning');
            return;
        }

        registerButton.disabled = true;
        const spinner = document.getElementById('btnSpinner');
        const btnText = document.querySelector('.btn-text');
        spinner.classList.remove('d-none');
        btnText.innerHTML = 'Processando...'; 

        try {
            const data = {
                cpf: cpfInput.value.replace(/\D/g, ""),
                password: passwordInput.value,
                nome: document.getElementById("nome").value,
                sobrenome: document.getElementById("sobrenome").value,
                telefone: telefoneInput.value.replace(/\D/g, ""),
                sexo: document.getElementById("sexo").value,
                dataNascimento: document.getElementById("dataNascimento").value,
                endereco: {
                    cep: cepInput.value.replace(/\D/g, ""),
                    logradouro: document.getElementById("logradouro").value,
                    rua: document.getElementById("rua").value,
                    numero: document.getElementById("numero").value,
                    complemento: document.getElementById("complemento").value,
                    bairro: document.getElementById("bairro").value,
                    cidade: document.getElementById("cidade").value,
                    estado: document.getElementById("estado").value
                }
            };

            const response = await fetch(`${API_URL}/auth/register-candidato`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data),
            });

            if (response.ok) {
                showMessage('Cadastro realizado com sucesso! Redirecionando...', 'success');
                setTimeout(() => {
                    window.location.href = 'login.html'; 
                }, 2000);
            } else {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Erro do servidor. Tente novamente.');
            }

        } catch (error) {
            showMessage(error.message, 'danger');
            console.error('Error:', error);
        } finally {
            registerButton.disabled = false;
            spinner.classList.add('d-none');
            btnText.innerHTML = '<i class="fas fa-check-circle me-2"></i>Finalizar Cadastro';
        }
    });
});