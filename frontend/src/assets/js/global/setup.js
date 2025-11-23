document.addEventListener("DOMContentLoaded", () => {
  const body = document.body;
  const html = document.documentElement;
  const savedTheme = localStorage.getItem("theme");

  if (savedTheme === "dark") {
    body.classList.add("dark-mode");
    html.classList.add("dark-mode"); 
  }

  const headerPlaceHolder = document.getElementById("headerPlaceHolder");
  if (headerPlaceHolder) {
    fetch("components/header.html")
      .then((resp) => {
        if (!resp.ok) throw new Error("Erro ao carregar header");
        return resp.text();
      })
      .then((htmlContent) => {
        headerPlaceHolder.innerHTML = htmlContent;
        updateUserInfo();
      })
      .catch((err) => console.warn("Header não encontrado:", err));
  }

  function enableDarkMode() {
    body.classList.add("dark-mode");
    html.classList.add("dark-mode");
    localStorage.setItem("theme", "dark");
  }

  function disableDarkMode() {
    body.classList.remove("dark-mode");
    html.classList.remove("dark-mode");
    localStorage.setItem("theme", "light");
  }

  document.addEventListener("click", (e) => {
    const target = e.target.closest(".sidebar-icons") || e.target;

    if (target.id === "theme-toggle-dark") {
      enableDarkMode();
    }
    if (target.id === "theme-toggle-light") {
      disableDarkMode();
    }

    if (e.target.id === "logoutBtn" || e.target.closest("#logoutBtnSidebar")) {
      e.preventDefault();
      logout();
    }
  });

  function logout() {
    localStorage.clear();
    window.location.href = 'login.html';
  }

  function updateUserInfo() {
    const userData = localStorage.getItem("usuario");
    if (userData) {
      try {
        const user = JSON.parse(userData);
             console.log(user);
        const nomeEl = document.getElementById("nomeUserHeader");
        if (nomeEl) nomeEl.textContent = user.nome || user.cpf;
      } catch (e) {
        console.error("Erro ao ler usuário", e);
      }
    }
  }
});
