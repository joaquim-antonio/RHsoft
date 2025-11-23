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

    //Máscara e Validação de CPF
    cpfInput.addEventListener("input", (e) => {
        let value = e.target.value.replace(/\D/g, "");
        value = value.slice(0, 11);

        if (value.length > 9) {
            value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        } else if (value.length > 6) {
            value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
        } else if (value.length > 3) {
            value = value.replace(/(\d{3})(\d{1,3})/, "$1.$2");
        }
        e.target.value = value;
        validateCpf(e.target.value);
    });

    function validateCpf(cpf) {
        const cleanedCpf = cpf.replace(/\D/g, "");
        if (cleanedCpf.length === 0) {
            cpfInput.classList.remove("is-valid", "is-invalid");
            cpfFeedback.textContent = "";
            cpfSuccess.classList.add("d-none");
        } else if (cleanedCpf.length < 11) {
            cpfInput.classList.add("is-invalid");
            cpfInput.classList.remove("is-valid");
            cpfFeedback.textContent = "CPF deve conter 11 dígitos.";
            cpfSuccess.classList.add("d-none");
        } else {
            cpfInput.classList.add("is-valid");
            cpfInput.classList.remove("is-invalid");
            cpfFeedback.textContent = "";
            cpfSuccess.classList.remove("d-none");
        }
    }

    togglePassword.addEventListener("click", () => {
        const type = passwordInput.getAttribute("type") === "password" ? "text" : "password";
        passwordInput.setAttribute("type", type);
        const icon = togglePassword.querySelector("i");
        icon.classList.toggle("fa-eye");
        icon.classList.toggle("fa-eye-slash");
    });

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const cpf = cpfInput.value;
        const password = passwordInput.value;

        if (cpf.replace(/\D/g, "").length !== 11 || password.length === 0) {
            if (cpf.replace(/\D/g, "").length !== 11) validateCpf(cpf);
            if (password.length === 0) passwordInput.classList.add("is-invalid");
            return;
        }

        const cleanedCpf = cpf.replace(/\D/g, "");
        const loginData = { cpf: cleanedCpf, password: password };

        setLoading(true);

        try {
            const response = await fetch("http://localhost:8080/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(loginData),
            });

            if (response.ok) {
                const data = await response.json();

                localStorage.setItem("authToken", data.token);
                localStorage.setItem("userCpf", data.cpf);
                localStorage.setItem("role", data.role);

                switch (data.role) {
                    case "ROLE_ADMIN":
                    case "ROLE_USER":
                        window.location.href = "/dashboard.html";
                        break;
                    case "ROLE_CANDIDATO":
                        window.location.href = "/vagas.html";
                        break;
                    default:
                        console.warn("Papel não reconhecido, redirecionando para a home:", data.role);
                        window.location.href = "/";
                }

            } else {
                const errorData = await response.json();
                showError(errorData.message || "CPF ou senha inválidos.");
            }
        } catch (error) {
            console.error("Erro ao tentar fazer login:", error);
            showError("Não foi possível conectar ao servidor. Tente novamente.");
        } finally {
            setLoading(false);
        }
    });

    //Funções Auxiliares
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
    }
});