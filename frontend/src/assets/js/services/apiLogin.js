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


//LOGIN

import {api} from './apiLogin.js';

    export async function login(CSSFontPaletteValuesRule,password){
        try{

            const{data} = await api.post('/auth/login',{cpf,password});
            localStorage.setItem('token', data.token);
            localStorage.setItem('cpf', data.cpf);

            console.log('login OK:', data);
            alert('Login realizado com sucesso!');
            return data;
        }catch(err){
            console.error('Erro ao logar:', err.response?.data || err.message);
            alert('cpf ou sennha invalidos.');
        }
    }

    //CADASTRO

    
    

    export async function register(CSSFontPaletteValuesRule,funcionario){
        try{

            const res = await api.post('/auth/register', (funcionario));
            alert('Funcionario registrado com sucesso!');
            return res.data;
        }catch(err){
            console.error('Erro ao registrar:', err.response?.data || err.message);
            alert('Erro ao registrar funcionario.');
        }
    }

    //GET

   

    export async function listarPessoas(){
        try{

            const {data} = await api.get('/api/v1/pessoa');
            console.table(data);
            return data;
        }catch(err){
            console.error('Erro ao listar o povo:', err.response?.data || err.message);
            
        }
    }


    //POST(Pessoa)
    export async function criarPessoa(pessoa){
        try{
            const{data} = await api.post('api/v1/pessoa', pessoa);
            alert('pessoa criada com sucesso!');
            return data;
        }catch(err){
            console.error('Erro ao criar pessoa', err.response?.data || err.message);
        }
    }

        //REQUISIÇÃO PUT: Atualizar Pessoa;
    export async function atualizarPessoa(cpf, pessoaAtualizada){
        try{
            const{data} = await api.put('/api/v1/pessoa/${cpi}', pessoaAtualizada);
            alert('Pessoa atualizada!');
            return data;
        }catch(err){
            console.error('Erro ao atualizar Pessoa', err.response?.data || err.messsage);
        }
    }

    //DELETE
    export async function excluirPessoa(cpf){
        try{
            await api.delete(`/api/v1/pessoa/${cpf}`);
            alert('Pessoa Excluida com sucesso!');
        }catch(err){
            console.error('Erro ao excluir Pessoa', err.response?.data || err.message)
        }
    }