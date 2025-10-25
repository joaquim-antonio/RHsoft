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




