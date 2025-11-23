const API_URL = "http://localhost:8080";
const LOGIN_PAGE_URL = "/frontend/src/detalhesVagas.html";
const VAGAS_PAGE_URL = "vagas.html";

let VAGA_ID_GLOBAL = null;
let originalDescHTML = null;
let dropzoneDescElement = null;
let curriculoInputElement = null;

function formatDate(isoDate) {
    if (!isoDate) return "";
    try {
        const date = new Date(isoDate);
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    } catch (e) {
        return isoDate;
    }
}


function controlarVisibilidadeBotoesAdmin() {
    const userRole = localStorage.getItem('role');
    const updateVagasButton = document.getElementById('update_vaga');
    const deleteVagasButton = document.getElementById('deletar_vaga');


    if (userRole === 'ROLE_ADMIN') {
        updateVagasButton.classList.remove('hidden');
        deleteVagasButton.classList.remove('hidden');
    } else {
        updateVagasButton.classList.add('hidden');
        deleteVagasButton.classList.add('hidden')
    }

}


async function loadVagaDetails(token, vagaId) {
    try {
        const response = await fetch(`${API_URL}/api/v1/vagas/${vagaId}`, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error("Vaga não encontrada.");
            }
            if (response.status === 401 || response.status === 403) {
                alert("Sessão expirada. Faça login novamente.");
                window.location.href = LOGIN_PAGE_URL;
                return;
            }
            throw new Error(`Falha ao buscar dados da vaga: ${response.statusText} (${response.status})`);
        }

        const vaga = await response.json();
        populateVagaData(vaga);

    } catch (error) {
        console.error("Erro ao carregar detalhes da vaga:", error);
        handleError(error.message);
    }
}

async function deleteVaga(token, vagaId) {
    const confirmacao = confirm("Tem certeza de que deseja excluir esta vaga? Esta ação é irreversível.");

    if (!confirmacao) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/api/v1/vagas/${vagaId}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json",
            },
        });

        if (response.ok || response.status === 204) {
            alert(`Vaga ${vagaId} excluída com sucesso!`);

            window.location.href = VAGAS_PAGE_URL;
            return true;
        } else {
            if (response.status === 404) {
                throw new Error("A vaga já foi excluída ou não foi encontrada.");
            }
            if (response.status === 401 || response.status === 403) {
                alert("Sessão expirada. Faça login novamente.");
                window.location.href = LOGIN_PAGE_URL;
                return false;
            }

            const errorData = await response.json().catch(() => ({}));
            const errorMessage = errorData.message || response.statusText;

            throw new Error(`Falha ao excluir a vaga: ${errorMessage} (${response.status})`);
        }

    } catch (error) {
        console.error("Erro ao deletar a vaga:", error);
        alert(`Erro na exclusão: ${error.message}`);
        return false;
    }
}

function populateVagaData(vaga) {
    setText("#jobTitle", vaga.titulo || "Título não informado");
    setText("#jobFunction", vaga.funcao || "Função não informada");
    setHtml("#jobDescription", vaga.descricao || "Nenhuma descrição fornecida.");
    setText("#dataLimite", formatDate(vaga.dataLimite) || "Data limite não informada");

    if (vaga.cargo) {
        setText("#jobCargo", vaga.cargo.nome || "Cargo não informado");
    } else {
        setText("#jobCargo", "Cargo não informado");
    }

    if (vaga.departamento) {
        setText("#departamentoNome", vaga.departamento.nome || "Departamento não informado");
        setHtml("#departamentoDescricao", vaga.departamento.descricao || "Sem descrição do departamento.");
        setText("#departamentoNome_secundario", vaga.departamento.nome || "Departamento");
    } else {
        setText("#departamentoNome", "Departamento não informado");
        setText("#departamentoNome_secundario", "Departamento");
    }
}


function handleError(message) {
    const mainContent = document.querySelector(".main-content");
    if (mainContent) {
        mainContent.innerHTML = `
            <div class="alert alert-danger text-center" role="alert">
                <h4 class="alert-heading">Erro!</h4>
                <p>${message}</p>
                <hr>
                <p class="mb-0">Você será redirecionado para a página de vagas.</p>
            </div>
        `;
    }
}

function setText(selector, text) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = text || "Não informado";
    }
}

function setHtml(selector, html) {
    const element = document.querySelector(selector);
    if (element) {
        element.innerHTML = html || "Não informado";
    }
}

function setSrc(selector, src) {
    const element = document.querySelector(selector);
    if (element) {
        element.src = src;
    }
}

//modal
function setupModalHandlers() {
    const updateVagaButton = document.getElementById("update_vaga")
    const modal = document.getElementById("vagaModal")
    const closeModalButton = document.getElementById("closeModal")
    const vagaForm = document.getElementById("vagaForm")

    if (updateVagaButton) {
        updateVagaButton.addEventListener("click", () => {
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
        const response = await fetch(`${API_URL}/api/v1/vagas/${VAGA_ID_GLOBAL}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token,
            },
            body: JSON.stringify(vagaDTO),
        })

        if (!response.ok) {
            throw new Error("Falha ao atualizar vaga. Status: " + response.status)
        }

        alert("Vaga atualizada com sucesso!")

        await loadVagaDetails(token, VAGA_ID_GLOBAL)

        document.getElementById("vagaModal").classList.add("hidden")
        formElement.reset()

    } catch (error) {
        alert("Erro ao criar vaga: " + error.message)
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem("authToken");
    const urlParams = new URLSearchParams(window.location.search);
    const vagaId = urlParams.get('id');
    const deleteButton = document.getElementById('deletar_vaga');

    if (vagaId) {
        VAGA_ID_GLOBAL = vagaId;
    }

    if (!token) {
        window.location.href = LOGIN_PAGE_URL;
        return;
    }

    if (!vagaId) {
        handleError("ID da vaga não encontrado.");
        setTimeout(() => { window.location.href = VAGAS_PAGE_URL; }, 3000);
        return;
    }

    if (deleteButton) {
        deleteButton.addEventListener('click', () => {
            if (token && VAGA_ID_GLOBAL) {
                deleteVaga(token, VAGA_ID_GLOBAL);
            } else {
                alert("Erro: ID da vaga ou token de autenticação não disponíveis.");
            }
        });
    }

    controlarVisibilidadeBotoesAdmin()
    setupModalHandlers()
    loadVagaDetails(token, vagaId);
});

