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
      const response = await fetch("./components/header.html"); 
      const headerHtml = await response.text();
      headerHolder.innerHTML = headerHtml;

      setupHeaderMenu();
      loadHeaderUserName();
    } catch (error) {
      console.error("Erro ao carregar o sidebar");
      headerHolder.innerHTML = "<p>Erro ao carregar o header</p>";
    }
  }

  if (sidebarHolder) {
    try {
      const response = await fetch("./components/sidebar.html"); 
      const sidebarHtml = await response.text();
      sidebarHolder.innerHTML = sidebarHtml;
    } catch (error) {
      console.error("Erro ao carregar o sidebar");
      sidebarHolder.innerHTML = "<p>Erro ao carregar o sidebar</p>";
    }
  }

  setupThemeToggle();

    // Inicializa o dashboard e o observador de tema após o carregamento dos componentes
  if (typeof initializeDashboard === 'function') {
    initializeDashboard();
  }
  if (typeof observeThemeChanges === 'function') {
    observeThemeChanges();
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
    localStorage.removeItem("authToken"); 
    localStorage.removeItem("userCpf"); 
    window.location.href = loginPageURL;
  });
}

/**
 * Busca o nome do funcionario e o exibe no header.
 */
async function loadHeaderUserName() {
  const token = localStorage.getItem("authToken"); 
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
      const pessoa = await response.json(); 
      const nomeCompleto = `${pessoa.nome || ""} ${
        pessoa.sobrenome || ""
      }`.trim();
      nomeUsuarioElement.textContent = nomeCompleto || "Usuário";

    } else if (response.status === 401 || response.status === 403) {
      localStorage.removeItem("authToken");
      // window.location.href = loginPageURL; //Fazer logout
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

function setupThemeToggle() {
  const moonIcon = document.getElementById("theme-toggle-dark");
  const sunIcon = document.getElementById("theme-toggle-light");
  const body = document.body;

  if (!moonIcon || !sunIcon) {
    setTimeout(setupThemeToggle, 120);
    return;
  }

  function applyTheme(theme) {
    if (theme === 'dark') {
      body.classList.add("dark-mode");
    } else {
      body.classList.remove("dark-mode");
    }
    localStorage.setItem("theme", theme);
  }

  moonIcon.addEventListener("click", () => applyTheme('dark'));
  sunIcon.addEventListener("click", () => applyTheme('light'));

  const currentTheme = localStorage.getItem("theme") || 'light';
  applyTheme(currentTheme);

  
}

document.addEventListener("DOMContentLoaded", () => {
  loadComponents();
});