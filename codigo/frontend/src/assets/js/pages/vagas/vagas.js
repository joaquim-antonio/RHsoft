const API_URL = "http://localhost:8080"
const LOGIN_PAGE_URL = "/frontend/src/vagas.html"

let allVagas = []

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("authToken")

  if (!token) {
    console.warn("Token não encontrado. Redirecionando para login.")
    window.location.href = LOGIN_PAGE_URL
    return
  }

  controlarVisibilidadeBotoesAdmin()
  loadVagas(token)
  setupSearchFilter()
  setupModalHandlers()
})


function controlarVisibilidadeBotoesAdmin() {
    const userRole = localStorage.getItem('role');
    const adicionarVagasButton = document.getElementById('adicionar_vagas');

    if (adicionarVagasButton) {
        // Se a role for 'ROLE_ADMIN', remove a classe 'hidden' para torná-lo visível.
        if (userRole === 'ROLE_ADMIN') {
            adicionarVagasButton.classList.remove('hidden');
        } else {
            // Caso contrário, garante que a classe 'hidden' está aplicada.
            adicionarVagasButton.classList.add('hidden');
        }
    }
}

async function loadVagas(token) {
  const vagasContainer = document.getElementById("vagasContainer")
  const emptyState = document.getElementById("emptyState")

  try {
    const response = await fetch(`${API_URL}/api/v1/vagas`, {
      method: "GET",
      headers: {
        Authorization: "Bearer " + token,
        "Content-Type": "application/json",
      },
    })

    if (!response.ok) {
      throw new Error(`Falha ao buscar vagas: ${response.statusText}`)
    }

    allVagas = await response.json()
    renderVagas(allVagas)
  } catch (error) {
    console.error("Erro ao carregar vagas:", error)
    vagasContainer.innerHTML = `
            <div class="col-12">
                <div class="alert alert-danger" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    Erro ao carregar vagas. Tente novamente mais tarde.
                </div>
            </div>
        `
  }
}

function renderVagas(vagas) {
  const vagasContainer = document.getElementById("vagasContainer")
  const emptyState = document.getElementById("emptyState")

  if (vagas.length === 0) {
    vagasContainer.classList.add("hidden")
    emptyState.classList.remove("hidden")
    return
  }

  vagasContainer.classList.remove("hidden")
  emptyState.classList.add("hidden")
  vagasContainer.innerHTML = ""

  vagas.forEach((vaga) => {
    const vagaElement = createVagaCard(vaga)
    vagasContainer.appendChild(vagaElement)
  })
}

function createVagaCard(vaga) {
  const cardWrapper = document.createElement("div")

  const descricaoCurta =
    vaga.descricao && vaga.descricao.length > 120
      ? vaga.descricao.substring(0, 120) + "..."
      : vaga.descricao || "Sem descrição disponível"

  const dtLimite = vaga.dataLimite || "Oportunidade"

  cardWrapper.innerHTML = `
          <div class="card" data-vaga-id="${vaga.id}">
              <h5 class="card-title">${vaga.titulo || "Cargo não especificado"}</h5>
              <span class="card-text">Disponível até ${dtLimite}</span>
              <p class="card-text">${descricaoCurta}</p>
          </div>
    `

  const cardElement = cardWrapper.querySelector(".card")
  cardElement.addEventListener("click", () => {
    abrirVaga(vaga.id)
  })

  return cardElement
}

function setupSearchFilter() {
  const searchInput = document.getElementById("search-box")
  if (!searchInput) return

  searchInput.addEventListener("input", (event) => {
    const searchTerm = event.target.value.toLowerCase()

    const vagasFiltradas = allVagas.filter((vaga) => {
      const titulo = vaga.titulo ? vaga.titulo.toLowerCase() : ""
      const descricao = vaga.descricao ? vaga.descricao.toLowerCase() : ""

      return titulo.includes(searchTerm) || descricao.includes(searchTerm)
    })

    renderVagas(vagasFiltradas)
  })
}

function abrirVaga(vagaId) {
  window.location.href = "detalhes-vagas.html?id=" + vagaId
}

//modal
function setupModalHandlers() {
    const createVagaButton = document.getElementById("adicionar_vagas")
    const modal = document.getElementById("vagaModal")
    const closeModalButton = document.getElementById("closeModal")
    const vagaForm = document.getElementById("vagaForm")

    if (createVagaButton) {
        createVagaButton.addEventListener("click", () => {
            modal.classList.remove("hidden")
        })
    }

    if (closeModalButton) {
        closeModalButton.addEventListener("click", () => {
            modal.classList.add("hidden")
            vagaForm.reset()
        })
    }

    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            modal.classList.add("hidden");
            vagaForm.reset();
        }
    })

    vagaForm.addEventListener("submit", async (e) => {
        e.preventDefault()
        await handleSubmitVaga(vagaForm)
    })
}

async function handleSubmitVaga(formElement) {
    const token = localStorage.getItem("authToken")
    const formData = new FormData(formElement)
    
    const vagaDTO = {
        funcao: formData.get("funcao"),
        titulo: formData.get("titulo"),
        descricao: formData.get("descricao"),
        dataLimite: formData.get("dataLimite"),
        cargoId: parseInt(formData.get("cargoId")), 
        departamentoId: parseInt(formData.get("departamentoId")),
    }

    try {
        const response = await fetch(`${API_URL}/api/v1/vagas`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token,
            },
            body: JSON.stringify(vagaDTO),
        })

        if (!response.ok) {
            throw new Error("Falha ao criar vaga. Status: " + response.status)
        }

        alert("Vaga criada com sucesso!")
        
        await loadVagas(token) 
        
        document.getElementById("vagaModal").classList.add("hidden")
        formElement.reset()

    } catch (error) {
        alert("Erro ao criar vaga: " + error.message)
    }
}

