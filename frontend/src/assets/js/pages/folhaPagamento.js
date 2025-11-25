document.addEventListener('DOMContentLoaded', init);

async function init() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }
    const usuarioValido = await validarSessao();
    if (!usuarioValido) return;
    await carregarSidebarAdministrador();
    await carregarTabelaPagamentos();
}

async function validarSessao() {
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8080/user/me',{
            method: 'GET',
            headers: {
                'Authorization':
    `Bearer ${token}`
            }
        });
        if (!response.ok) throw new Error("Sessão inválida!");
        const dadosUsuario = await response.json();
        if (dadosUsuario.role !== 'ROLE_ADMIN'){
            alert("Acesso não autorizado!");
            window.location.href = 'login.html';
            return false;
        }
        return true;
    } catch (error) {
        console.log(error);
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
        window.location.href = 'login.html';
        return false;
    }
}

async function carregarSidebarAdministrador() {
    try {
        const response = await fetch('components/sidebar-administrador.html');
        if (response.ok) {
            const html = await response.text();
            document.getElementById('sidebarAdmnistradorContainer').innerHTML = html;
            const linkFolha = document.getElementById('link-folha-pagamento');
            if (linkFolha){
                linkFolha.classList.add('active');
                linkFolha.style.color = 'var(--cor-azul-escuro)'
            }
        }
    }
    catch (e) {
        console.error("Erro ao carregar sidebar:", e);
    }
}

// Atualizar posteriomente
async function carregarTabelaPagamentos(){
    try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8080/folha-pagamento',{
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        if (response.ok){
            const pagamentos = await response.json();
            const tabelaBody = document.getElementById('tabelaPagamentos');
            tabelaBody.innerHTML = '';
            pagamentos.forEach(pagamento => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${pagamento.id}</td>
                    <td>${pagamento.funcionarioNome}</td>
                    <td>${pagamento.mesAno}</td>
                    <td>R$ ${pagamento.valor.toFixed(2)}</td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-primary-custom" onclick="baixarRecibo(${pagamento.id})">Baixar Recibo</button>
                    </td>
                `;
                tabelaBody.appendChild(row);
            });
        } else {
            console.error("Erro ao carregar pagamentos");
        }
    } catch (error) {
        console.error("Erro ao carregar pagamentos:", error);
    }
}