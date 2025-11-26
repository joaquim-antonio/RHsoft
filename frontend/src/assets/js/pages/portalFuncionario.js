document.addEventListener("DOMContentLoaded", async () => {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "login.html";
    return;
  }

  async function init() {
    if (!(await validarPermissao())) return;
    await carregarSidebar();
    await carregarDadosFuncionario();
    await carregarResumoFinanceiro();
    calcularProximoPagamento();
    carregarComunicados();
  }
  async function validarPermissao() {
    try {
      const response = await fetch("http://localhost:8080/user/me", {
        headers: { Authorization: `Bearer ${token}` },
      });
      const user = await response.json();
      if (user.role === "ROLE_ADMIN" || user.role === "ROLE_CANDIDATO") {
        alert("Acesso negado. Redirecionando...");
        window.location.href =
          user.role === "ROLE_ADMIN"
            ? "portalAdministrador.html"
            : "portalCandidato.html";
        return false;
      }
      return true;
    } catch {
      window.location.href = "login.html";
      return false;
    }
  }

  async function carregarSidebar() {
    try {
      const response = await fetch("components/sidebar-funcionario.html");
      if (response.ok) {
        document.getElementById("sidebarCandidatoContainer").innerHTML =
          await response.text();
        // Marca o link do Dashboard como ativo
        const linkDash = document.querySelector(
          'a[href="portalFuncionario.html"]'
        );
        if (linkDash) linkDash.classList.add("active");
      }
    } catch {}
  }

  async function carregarDadosFuncionario() {
    const user = JSON.parse(localStorage.getItem("usuario"));
    if (user)
      document.getElementById("nomeFuncionario").textContent = user.nome;
  }

  async function carregarComunicados() {
    const listaEl = document.querySelector(".list-group");

    if (!listaEl) return;

    listaEl.innerHTML =
      '<div class"text-center py-3"><i class="fas fa-spinner fa-spin"></i></div>';

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/comunicados/recentes",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (response.ok) {
        const comunicados = await response.json();
        listaEl.innerHTML = "";
        if (comunicados.length === 0) {
          listaEl.innerHTML =
            '<div class"text-center text-muted small py-2">Nehum aviso recente</div>';
          return;
        }
        comunicados.forEach((c) => {
          let corIcone = "text-secondary";
          if (c.tipo === "IMPORTANTE") corIcone = "text-danger";
          if (c.tipo === "INFORMATIVO") corIcone = "text-primary";
          if (c.tipo === "EVENTO") corIcone = "text-warning";

          const dataFmt = new Date(c.dataPublicacao).toLocaleDateString(
            "pt-BR",
            {
              day: "2-digit",
              month: "2-digit",
            }
          );

          const item = document.createElement("li");
          item.className =
            "list-group-item px-0 border-0 d-flex justify-content-between align-items-center";
          item.innerHTML = `
                        <div class="d-flex align-items-center text-truncate pe-2">
                            <i class="fas fa-circle ${corIcone} me-2 flex-shrink-0" style="font-size: 8px;"></i>
                            <span class="text-truncate fw-medium" title="${
                              c.descricao || ""
                            }">${c.titulo}</span>
                        </div>
                        <span class="text-muted flex-shrink-0 small" style="font-size: 0.75em;">${dataFmt}</span>`;
          listaEl.appendChild(item);
        });
      }
    } catch (error) {
        console.error("Error comunicado", error);
        listaEl.innerHTML = '<div class="text-danger small text-center">Erro ao carregar comunicados</div>'
    }
  }

  async function carregarResumoFinanceiro() {
    const user = JSON.parse(localStorage.getItem("usuario"));
    try {
      const response = await fetch(
        `http://localhost:8080/pagamento/funcionario?cpf=${user.cpf}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (response.ok) {
        const dados = await response.json();

        if (dados.length > 0) {
          const ultimo = dados.sort(
            (a, b) => new Date(b.vencimento) - new Date(a.vencimento)
          )[0];

          document.getElementById(
            "mesReferencia"
          ).textContent = `Ref: ${ultimo.mesAnoReferencia}`;
          document.getElementById("dashLiquido").textContent = formatarMoeda(
            ultimo.valorLiquido
          );
          document.getElementById("dashProventos").textContent = formatarMoeda(
            ultimo.proventos
          );
          document.getElementById("dashDescontos").textContent = formatarMoeda(
            ultimo.descontos
          );
        } else {
          document.getElementById("mesReferencia").textContent = "Sem dados";
        }
      }
    } catch (e) {
      console.error("Erro ao buscar resumo:", e);
    }
  }

  function calcularProximoPagamento() {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = hoje.getMonth();

    let dataPagamento = new Date(ano, mes + 1, 5);

    if (hoje.getDate() < 5) {
      dataPagamento = new Date(ano, mes, 5);
    }

    const diaFmt = String(dataPagamento.getDate()).padStart(2, "0");
    const mesFmt = String(dataPagamento.getMonth() + 1).padStart(2, "0");

    const elData = document.getElementById("dataProximoPagamento");
    if (elData) elData.textContent = `${diaFmt}/${mesFmt}`;

    const diffTempo = dataPagamento - hoje;
    const diffDias = Math.ceil(diffTempo / (1000 * 60 * 60 * 24));

    const badge = document.getElementById("diasRestantesBadge");
    const barra = document.getElementById("barraProgressoPagamento");
    const texto = document.getElementById("textoStatusPagamento");

    if (!barra) return;

    if (diffDias <= 0) {
      badge.textContent = "Hoje!";
      badge.className = "badge bg-success";
      barra.style.width = "100%";
      barra.classList.add("bg-success");
      texto.textContent = "Pagamento disponível!";
    } else {
      badge.textContent = `Faltam ${diffDias} dias`;
      const porcentagem = Math.max(0, Math.min(100, 100 - diffDias * 3.3));
      barra.style.width = `${porcentagem}%`;
      texto.textContent = "Aguardando processamento...";
    }
  }

  function formatarMoeda(val) {
    return new Intl.NumberFormat("pt-BR", {
      style: "currency",
      currency: "BRL",
    }).format(val);
  }

  init();
});
