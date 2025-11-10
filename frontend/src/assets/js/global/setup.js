const API_URL = "http://localhost:8080";
const loginPageURL = "/frontend/src/login.html";

/*
Carrega os componetes header e sidebar
 */
async function loadComponents(params) {
    const headerHolder = document.getElementById("headerPlaceHolder");
    const sidebarHolder = document.getElementById("sidebarPlaceHolder");

    if (headerHolder) {
        try {
            const response = await fetch("/frontend/src/components/header.html");
            const headerHtml = await response.text();
            headerHolder.innerHTML = headerHtml;

            setupHeaderMenu();
            loadHeaderUserName();

        } catch (error) {
            console.error("Erro ao carregar o header");
            headerHolder.innerHTML = "<p>Erro ao carregar o header</p>"
        }
    }

    if (sidebarHolder) {
        try {
            const response = await fetch("/frontend/src/components/sidebar.html");
            const sidebarHtml = await response.text();
            sidebarHolder.innerHTML = sidebarHtml;

        } catch (error) {
            console.error("Erro ao carregar o header");
            sidebarHolder.innerHTML = "<p>Erro ao carregar o header</p>"
        }
    }
}


//Confdigura o botão de logout
function setupHeaderMenu() {
    const logoutBtn = document.getElementById("logoutBtn");
    if (!logoutBtn) {
        setTimeout(setupHeaderMenu, 120);
        console.log("Header carregando...");
        return;
    }

    logoutBtn.addEventListener("click", (event) => {
        event.preventDefault();
        localStorage.removeItem("token");
        // window.location.href = loginPageURL;
    });
}

/**
 * Busca o nome do usuário e o exibe no header.
 */
async function loadHeaderUserName() {
    const token = localStorage.getItem("token");
    if (!token) {
        console.warn("Token não encontrado para o header.");
        // window.location.href = loginPageURL;
        return;
    }

    try {
        const response = await fetch(`${API_URL}/user/me`, {
            method: "GET",
            headers: {
                Authorization: "Bearer " + token,
                "Content-Type": "application/json",
            },
        });

        const nomeUsuarioElement = document.getElementById("nomeUserHeader");
        if (!nomeUsuarioElement) {
            setTimeout(loadHeaderUserName, 120);
            console.log("Carregando...");
            return;
        }

        if (response.ok) {
            const user = await response.json();
            console.log(user)
            const pessoa = user.pessoa || {};
            const nomeCompleto = `${pessoa.nome || ""} ${pessoa.sobrenome || ""}`.trim();
            nomeUsuarioElement.textContent = nomeCompleto || "Usuário";
        } else if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("token");
            // window.location.href = loginPageURL;
        } else {
            nomeUsuarioElement.textContent = "Visitante";
        }
    } catch (error) {
        console.error("Erro de rede ao buscar nome para o header:", error);
        const nomeUsuarioElement = document.getElementById("nomeUserHeader");
        if (nomeUsuarioElement) {
            nomeUsuarioElement.textContent = "Visitante (Erro)";
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadComponents();
});