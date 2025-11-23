document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    const cpfInput = document.getElementById("cpfInput");
    const passwordInput = document.getElementById("passwordInput");
    const togglePassword = document.getElementById("togglePassword");
    const loginButton = document.getElementById("loginButton");
    const btnSpinner = document.getElementById("btnSpinner");
    const btnText = loginButton.querySelector(".btn-text");
    const errorMessage = document.getElementById("errorMessage");
    const errorText = document.getElementById("errorText");
    const cpfFeedback = document.getElementById("cpfFeedback");
    const cpfSuccess = document.getElementById("cpfSuccess");

    // URL da API (Ajuste conforme necessário)
    const API_URL = "http://localhost:8080/auth/login";

    // Máscara e Validação de CPF
    cpfInput.addEventListener("input", (e) => {
        let value = e.target.value.replace(/\D/g, "");
        if (value.length > 11) value = value.slice(0, 11);

        if (value.length > 9) {
            value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        } else if (value.length > 6) {
            value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
        } else if (value.length > 3) {
            value = value.replace(/(\d{3})(\d{1,3})/, "$1.$2");
        }
        e.target.value = value;
        
        validateCpf(value);
    });

    function validateCpf(cpf) {
        const cleanedCpf = cpf.replace(/\D/g, "");
        
        cpfInput.classList.remove("is-valid", "is-invalid");
        cpfSuccess.classList.add("d-none");
        cpfFeedback.textContent = "";

        if (cleanedCpf.length === 0) {
            return;
        }

        if (cleanedCpf.length < 11) {
            cpfInput.classList.add("is-invalid");
            cpfFeedback.textContent = "CPF deve conter 11 dígitos.";
        } else {
            // Aqui poderia ter uma validação real de algoritmo de CPF
            cpfInput.classList.add("is-valid");
            cpfSuccess.classList.remove("d-none");
        }
    }

    // Toggle Senha
    togglePassword.addEventListener("click", () => {
        const type = passwordInput.getAttribute("type") === "password" ? "text" : "password";
        passwordInput.setAttribute("type", type);
        
        const icon = togglePassword.querySelector("i");
        icon.classList.toggle("fa-eye");
        icon.classList.toggle("fa-eye-slash");
    });

    // Submit do Formulário
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        
        const cpf = cpfInput.value;
        const password = passwordInput.value;
        const cleanedCpf = cpf.replace(/\D/g, "");

        // Validação antes de enviar
        if (cleanedCpf.length !== 11 || password.length === 0) {
            if (cleanedCpf.length !== 11) {
                cpfInput.classList.add("is-invalid");
                cpfFeedback.textContent = "CPF inválido.";
            }
            if (password.length === 0) {
                passwordInput.classList.add("is-invalid");
            }
            return;
        }

        const loginData = { 
            cpf: cleanedCpf, 
            password: password 
        };

        setLoading(true);

        try {
            const response = await fetch(API_URL, {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json" 
                },
                body: JSON.stringify(loginData),
            });

            const data = await response.json();

            if (response.ok) {
                localStorage.setItem("token", data.token); 
                localStorage.setItem("usuario", JSON.stringify({
                    cpf: data.cpf,
                    nome: data.nome,
                    role: data.role
                }));

                // Redirecionamento baseado na Role
                console.log("Login realizado. Role:", data.role);
                
                switch (data.role) {
                    case "ROLE_ADMIN":
                    case "ROLE_USER":
                        window.location.href = "dashboard.html";
                        break;
                    case "ROLE_CANDIDATO":
                        window.location.href = "portalCandidato.html";
                        break;
                    default:
                        console.warn("Papel não reconhecido:", data.role);
                        window.location.href = "portalCandidato.html"; 
                }

            } else {
                let msg = "CPF ou senha inválidos.";
                if (data && data.message) msg = data.message;
                showError(msg);
            }
        } catch (error) {
            console.error("Erro de conexão:", error);
            showError("Erro ao conectar com o servidor. Verifique se o backend está rodando.");
        } finally {
            setLoading(false);
        }
    });

    // Funções Auxiliares
    function setLoading(isLoading) {
        if (isLoading) {
            loginButton.disabled = true;
            btnSpinner.classList.remove("d-none");
            btnText.textContent = "Entrando...";
            errorMessage.classList.add("d-none");
        } else {
            loginButton.disabled = false;
            btnSpinner.classList.add("d-none");
            btnText.textContent = "Entrar";
        }
    }

    function showError(message) {
        errorText.textContent = message;
        errorMessage.classList.remove("d-none");
        
        const card = document.querySelector('.login-card');
        card.classList.add('shake-animation'); 
        setTimeout(() => card.classList.remove('shake-animation'), 500);
    }
});