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


    atualizarStatus: async (candidaturaId, novoStatusEnum) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${candidaturaId}/status`, {
                method: 'PATCH', 
                headers: CandidaturaAdminService.getHeaders(),
                body: JSON.stringify({ 
                    status: novoStatusEnum 
                })
            });

            if (!response.ok) {
                
                const errorMessage = await response.text(); 
                throw new Error(errorMessage || 'Erro ao atualizar status');
            }

            return await response.json();
        } catch (error) {
            console.error('Erro ao atualizar status:', error);
            throw error;
        }
    },

    
    aprovarEContratar: async (candidaturaId, dadosContratacaoObj) => {
        try {
            const response = await fetch(`${API_BASE_URL}/${candidaturaId}/aprovar`, {
                method: 'POST',
                headers: CandidaturaAdminService.getHeaders(),
                body: JSON.stringify(dadosContratacaoObj) 
                
            });

            if (!response.ok) {
                
                const errorMessage = await response.text();
                throw new Error(errorMessage || 'Erro ao aprovar contratação');
            }

            const resultado = await response.json();
            console.log("Sucesso! Candidato contratado.");
            return resultado;

        } catch (error) {
            console.error('Erro na contratação:', error);
            throw error;
        }
    }
};