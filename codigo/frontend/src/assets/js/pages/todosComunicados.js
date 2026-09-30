const API_CONFIG = {
  BASE_URL: 'http://localhost:8080/api/v1',
  ENDPOINTS: {
    COMUNICADOS: '/comunicados'
  }
};

const COMUNICADO_TYPES = {
  IMPORTANTE: { icon: 'fa-exclamation-circle', bgClass: 'bg-danger-subtle', borderClass: 'border-danger', textClass: 'text-danger', label: 'Importante' },
  INFORMATIVO: { icon: 'fa-info', bgClass: 'bg-primary-subtle', borderClass: 'border-primary', textClass: 'text-primary', label: 'Informativo' },
  EVENTO: { icon: 'fa-calendar-alt', bgClass: 'bg-warning-subtle', borderClass: 'border-warning', textClass: 'text-warning', label: 'Evento' },
  DEFAULT: { icon: 'fa-info-circle', bgClass: 'bg-secondary-subtle', borderClass: 'border-secondary', textClass: 'text-secondary', label: 'Geral' }
};

document.addEventListener('DOMContentLoaded', async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    window.location.href = 'login.html';
    return;
  }

  let currentPage = 0;
  const pageSize = 6;

  async function init() {
    try {
      await carregarSidebar();
      await carregarComunicados(currentPage);
    } catch (error) {
      console.error('[Init Error]', error);
      mostrarErro('Erro ao inicializar a página');
    }
  }

  async function carregarSidebar() {
    try {
      const response = await fetch('components/sidebar-funcionario.html');
      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      const sidebarContainer = document.getElementById('sidebarCandidatoContainer');
      if (!sidebarContainer) throw new Error('Container sidebar não encontrado');

      sidebarContainer.innerHTML = await response.text();

      const activeLink = document.querySelector('.sidebar-item[href*="comunicados.html"]');
      if (activeLink) activeLink.classList.add('active');
    } catch (error) {
      console.error('[Sidebar Error]', error);
    }
  }

  async function carregarComunicados(page) {
    const container = document.getElementById('containerComunicados');
    if (!container) return;

    container.style.opacity = '0.5';

    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.COMUNICADOS}?page=${page}&size=${pageSize}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) throw new Error(`Erro HTTP ${response.status}`);

      const data = await response.json();   
      const lista = data.content || data;
      renderizarLista(lista, container);

      if (data.totalPages) {
        renderizarPaginacao(data);
        currentPage = data.number;
      }

    } catch (error) {
      console.error('[Comunicados Error]', error);
      mostrarErro('Erro ao carregar avisos', container);
    } finally {
      container.style.opacity = '1';
    }
  }

  function renderizarLista(lista, container) {
    container.innerHTML = '';

    if (!lista || lista.length === 0) {
      container.innerHTML = `
        <div class="col-12 text-center py-5">
          <i class="far fa-folder-open fa-3x text-muted mb-3"></i>
          <p class="text-muted">Nenhum comunicado encontrado.</p>
        </div>`;
      return;
    }

    const fragment = document.createDocumentFragment();

    lista.forEach(c => {
      try {
        const config = COMUNICADO_TYPES[c.tipo] || COMUNICADO_TYPES.DEFAULT;
        const dataFmt = formatarData(c.dataPublicacao);
        const textoDescricao = sanitizarTexto(c.conteudo); 

        const cardHtml = `
          <div class="col-12">
            <div class="card shadow-sm border-0 h-100">
              <div class="card-body d-flex align-items-start">
                <div class="icon-shape ${config.bgClass} ${config.textClass} rounded-3 me-3 flex-shrink-0">
                  <i class="fas ${config.icon} fa-lg"></i>
                </div>
                
                <div class="w-100">
                  <div class="d-flex justify-content-between align-items-start mb-1">
                    <h5 class="fw-bold mb-0 text-body">${sanitizarTexto(c.titulo)}</h5>
                    <small class="text-muted ms-2 text-nowrap">${dataFmt}</small>
                  </div>
                  
                  <span class="badge ${config.bgClass} ${config.textClass} border ${config.borderClass} mb-2" style="font-weight: 500;">
                    ${config.label}
                  </span>
                  
                  <p class="text-muted mb-0 mt-1" style="white-space: pre-line;">${textoDescricao}</p>
                </div>
              </div>
            </div>
          </div>
        `;

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = cardHtml;
        fragment.appendChild(tempDiv.firstElementChild);
      } catch (error) {
        console.error('[Card Render Error]', error, c);
      }
    });

    container.appendChild(fragment);
  }

  function renderizarPaginacao(pageData) {
    const paginationContainer = document.getElementById('paginationControls');
    if (!paginationContainer) return;

    paginationContainer.innerHTML = '';
    
    if (pageData.totalPages <= 1) return;

    const prevClass = pageData.first ? 'disabled' : '';
    paginationContainer.innerHTML += `
        <li class="page-item ${prevClass}">
            <button class="page-link" onclick="window.mudarPagina(${pageData.number - 1})" aria-label="Anterior">
                <span aria-hidden="true">&laquo;</span>
            </button>
        </li>
    `;

    for (let i = 0; i < pageData.totalPages; i++) {
        const activeClass = i === pageData.number ? 'active' : '';
        paginationContainer.innerHTML += `
            <li class="page-item ${activeClass}">
                <button class="page-link" onclick="window.mudarPagina(${i})">${i + 1}</button>
            </li>
        `;
    }

    const nextClass = pageData.last ? 'disabled' : '';
    paginationContainer.innerHTML += `
        <li class="page-item ${nextClass}">
            <button class="page-link" onclick="window.mudarPagina(${pageData.number + 1})" aria-label="Próximo">
                <span aria-hidden="true">&raquo;</span>
            </button>
        </li>
    `;
  }

  window.mudarPagina = (novaPagina) => {
    if (novaPagina >= 0) {
        carregarComunicados(novaPagina);
        const container = document.getElementById('containerComunicados');
        if(container) container.scrollIntoView({ behavior: 'smooth' });
    }
  };

  function formatarData(data) {
    try {
      const date = new Date(data);
      if (isNaN(date)) return '--/--/----';
      return date.toLocaleDateString('pt-BR');
    } catch { return '--/--/----'; }
  }

  function sanitizarTexto(texto) {
    if (!texto) return '';
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
  }

  function mostrarErro(mensagem, container = null) {
    const errorHtml = `<div class="col-12 text-center text-danger py-3">${mensagem}</div>`;
    if (container) container.innerHTML = errorHtml;
    else console.error(mensagem);
  }

  init();
});