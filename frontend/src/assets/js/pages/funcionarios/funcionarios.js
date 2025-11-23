let editingRow = null;

// Abrir/fechar cadastro
function openAddModal() {
  document.getElementById('modalAdd').style.display = 'flex';
}
function closeAddModal() {
  document.getElementById('modalAdd').style.display = 'none';
}

// Adicionar funcionário
function addFuncionario() {
  const nome = document.getElementById('add-nome').value;
  const sobrenome = document.getElementById('add-sobrenome').value;
  const email = document.getElementById('add-email').value;
  const dep = document.getElementById('add-departamento').value;
  const id = Math.floor(Math.random()*9000+1000);

  if(nome && sobrenome && email && dep){
    const tbody = document.getElementById('func-list');
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${nome} ${sobrenome}</td>
      <td>${id}</td>
      <td>${email}</td>
      <td>${dep}</td>
      <td class="actions">
        <button class="btn btn-edit" onclick="openEditModal(this)">Editar</button>
        <button class="btn btn-delete" onclick="deleteFuncionario(this)">Excluir</button>
      </td>
    `;
    tbody.appendChild(row);
    closeAddModal();
  } else {
    alert("Preencha pelo menos Nome, Sobrenome, Email e Departamento!");
  }
}

// Abrir edição
function openEditModal(button) {
  editingRow = button.parentElement.parentElement;
  const cells = editingRow.getElementsByTagName('td');

  document.getElementById('edit-nome').value = cells[0].innerText.split(" ")[0];
  document.getElementById('edit-sobrenome').value = cells[0].innerText.split(" ")[1] || "";
  document.getElementById('edit-email').value = cells[2].innerText;
  document.getElementById('edit-departamento').value = cells[3].innerText;
  document.getElementById('edit-registro').value = cells[1].innerText;

  document.getElementById('modalEdit').style.display = 'flex';
}

function closeEditModal() {
  document.getElementById('modalEdit').style.display = 'none';
}

// Salvar edição
function saveEdit() {
  if(editingRow){
    const cells = editingRow.getElementsByTagName('td');
    const nome = document.getElementById('edit-nome').value;
    const sobrenome = document.getElementById('edit-sobrenome').value;
    const email = document.getElementById('edit-email').value;
    const dep = document.getElementById('edit-departamento').value;

    cells[0].innerText = nome + " " + sobrenome;
    cells[2].innerText = email;
    cells[3].innerText = dep;
  }
  closeEditModal();
}

// Excluir com confirmação
function deleteFuncionario(button) {
  if(confirm("Tem certeza que deseja excluir este funcionário?")){
    button.parentElement.parentElement.remove();
  }
}

// Pesquisa em tempo real
document.getElementById('search').addEventListener('keyup', function(){
  const filter = this.value.toLowerCase();
  const rows = document.querySelectorAll("#func-list tr");

  rows.forEach(row => {
    const nome = row.cells[0].textContent.toLowerCase();
    const id = row.cells[1].textContent.toLowerCase();
    const email = row.cells[2].textContent.toLowerCase();
    const dep = row.cells[3].textContent.toLowerCase();

    if(nome.includes(filter) || id.includes(filter) || email.includes(filter) || dep.includes(filter)){
      row.style.display = "";
    } else {
      row.style.display = "none";
    }
  });
});