    
    import axios from 'https://cdn.jsdelivr.net/npm/axios@1.6.7/+esm';
    
    export const api = axios.create({
      baseURL: 'http://localhost:8080', 
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    
    api.interceptors.request.use(config => {
      const token = localStorage.getItem('token');
      if (token) config.headers.Authorization = `Bearer ${token}`;
      return config;
    });
    
    
    const baseUrl = 'http://localhost:8080/api/v1/Candidato';
    
    async function listarCandidatos() {
  try {
    
    const response = await axios.get(`${baseUrl}/all`);
    
    console.log('Candidatos listados:', response.data);
    return response.data;

  } catch (error) {
    
    console.error('Falha ao listar candidatos:', error.message);
  }
}





async function buscarCandidatoPorCpf(cpf) {
  try {
    const response = await axios.get(`${baseUrl}/${cpf}`);

    console.log('Candidato encontrado:', response.data);
    return response.data;

  } catch (error) {
    if (error.response && error.response.status === 404) {
      console.warn(error.response.data); 

      return null;
    } else {
      console.error('Falha ao buscar candidato:', error.message);
    }
  }
}



async function adicionarCandidato(dadosCandidato) {
  

  try {
    const response = await axios.post(baseUrl, dadosCandidato);
    console.log('Candidato adicionado:', response.data);
    return response.data;
  } catch (error) {
    console.error('Erro ao adicionar:', error.response?.data || error.message);
  }
}


async function atualizarCandidato(cpf, dadosAtualizados) {
  try {    
    const response = await axios.put(`${baseUrl}/${cpf}`, dadosAtualizados);
    console.log('Candidato atualizado:', response.data);
    return response.data;
  } catch (error) {
    
    if (error.response && error.response.status === 404) {
      console.warn(error.response.data); 
      return null;
    } else {
      console.error('Falha ao atualizar candidato:', error.message);
    }
  }
}
