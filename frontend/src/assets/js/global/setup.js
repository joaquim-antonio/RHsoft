document.addEventListener("DOMContentLoaded", async () => {
    const body = document.body;
    const html = document.documentElement;
  
    // 1. TEMA (DARK/LIGHT)
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
      enableDarkMode();
    }
  
    // 2. HEADER
    const headerPlaceHolder = document.getElementById("headerPlaceHolder");
    if (headerPlaceHolder) {
      try {
        const resp = await fetch("components/header.html");
        if (resp.ok) {
          headerPlaceHolder.innerHTML = await resp.text();
          updateUserInfo();
        }
      } catch (err) { console.warn("Header erro:", err); }
    }
  
    // 3. SIDEBAR
    const sidebarPlaceHolder = document.getElementById("sidebarPlaceHolder");
    if (sidebarPlaceHolder) {
      await carregarSidebarCorreta(sidebarPlaceHolder);
    }
  
    // --- LÓGICA ---
  
    async function carregarSidebarCorreta(placeholder) {
      const userData = localStorage.getItem("usuario");
      if (!userData) return;
  
      let user;
      try { user = JSON.parse(userData); } catch (e) { return; }
  
      let sidebarFile = "";
      // Normaliza a role para evitar erros de maiúsculas/minúsculas
      const role = user.role ? user.role.toUpperCase() : "";

      if (role.includes("ADMIN")) {
          sidebarFile = "components/sidebar-administrador.html";
      } else if (role.includes("USER") || role.includes("FUNCIONARIO")) {
          sidebarFile = "components/sidebar-funcionario.html";
      } else if (role.includes("CANDIDATO")) {
          sidebarFile = "components/sidebar-candidato.html";
      } else {
          return;
      }
  
      try {
          const resp = await fetch(sidebarFile);
          if (resp.ok) {
              placeholder.innerHTML = await resp.text();
              highlightActiveLink(); // Ativa o link após carregar o HTML
          }
      } catch (err) { console.error("Erro sidebar:", err); }
    }
  
    function highlightActiveLink() {
      // Pega apenas o nome do arquivo da URL atual (ex: contracheque.html)
      const currentFile = window.location.pathname.split("/").pop().split("?")[0];
      
      const links = document.querySelectorAll(".sidebar-item");
      links.forEach(link => {
          const href = link.getAttribute("href");
          if (!href) return;

          // Pega o nome do arquivo do link da sidebar
          const linkFile = href.split("/").pop().split("?")[0];

          // Compara se são iguais OU se um contém o outro (para casos de singular/plural)
          if (linkFile === currentFile || (currentFile.includes(linkFile) && linkFile.length > 4)) {
              link.classList.add("active");
          }
      });
    }
  
    // TEMA
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
  
    // EVENTOS GLOBAIS
    document.addEventListener("click", (e) => {
      const target = e.target.closest("#theme-toggle-dark") || e.target.closest("#theme-toggle-light");
      if (e.target.closest("#theme-toggle-dark")) enableDarkMode();
      else if (e.target.closest("#theme-toggle-light")) disableDarkMode();
  
      if (e.target.id === "logoutBtn" || e.target.closest("#logoutBtn")) {
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
          const nomeEl = document.getElementById("nomeUserHeader");
          if (nomeEl) nomeEl.textContent = user.nome || user.cpf;
        } catch (e) {}
      }
    }
  });