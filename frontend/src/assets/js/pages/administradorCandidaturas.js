const API_BASE_URL = 'http://localhost:8080/api/v1/candidaturas';

const CandidaturaAdminService = {

    
    getHeaders: () => {
        const token = localStorage.getItem('token'); 
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        };
    },

    
    listarCandidatosPorVaga: async (vagaId) => {
        try {
            const response = await fetch(`${API_BASE_URL}/vaga/${vagaId}`, {
                method: 'GET',
                headers: CandidaturaAdminService.getHeaders()
            });

            if (!response.ok) {
                throw new Error(`Erro ao buscar candidatos: ${response.statusText}`);
            }

            
            return await response.json(); 
        } catch (error) {
            console.error('Erro no service de listagem:', error);
            throw error;
        }
    },

    
    