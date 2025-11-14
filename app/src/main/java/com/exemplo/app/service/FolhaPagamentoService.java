package com.exemplo.app.service;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoPericulosidade;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.Funcionario;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;


@Service
public class FolhaPagamentoService {

@Autowired
private FuncionarioService funcionarioService;

@Autowired
private FuncionarioRepository funcionarioRepository;

@Autowired
private FolhaPagamentoRepository folhaPagamentoRepository;


public void gerarFolhaDePagamento(){
    List<Funcionario> funcionarios = funcionarioService.listarTodosFuncionarios();
}
    

public BigDecimal CalcularSalariosDeFuncionarios(Funcionario funcionario ){

   BigDecimal valorBruto = funcionario.getSalario(); 
    Double horasTrabalhadas = funcionario.getHorasTrabalhadas();
 
    BigDecimal valorHora = valorBruto.divide(BigDecimal.valueOf(220), 2, RoundingMode.HALF_UP);

    BigDecimal salario = valorHora.multiply(BigDecimal.valueOf(horasTrabalhadas));

    return salario;
    
}


public BigDecimal incluirAcrescimo(
        Funcionario funcionario,
        TipoAcrescimo tipo,
        TipoPericulosidade nivelPericulosidade) {

    BigDecimal salarioBase = funcionario.getSalario();

   
    BigDecimal cem = new BigDecimal("100");
    BigDecimal percentual;

    
    BigDecimal insalubridadePercentual = new BigDecimal("30");

   
    switch (tipo) {
        case INSALUBRIDADE:
            percentual = insalubridadePercentual;
            break;

        case PERICULOSIDADE:
            switch (nivelPericulosidade) {
                case BAIXO:
                    percentual = new BigDecimal("10");
                    break;
                case MEDIO:
                    percentual = new BigDecimal("20");
                    break;
                case ALTO:
                    percentual = new BigDecimal("40");
                    break;
                default:
                    percentual = BigDecimal.ZERO;
            }
            break;

        default:
            percentual = BigDecimal.ZERO;

    }
    BigDecimal fator = percentual.divide(cem, 4, RoundingMode.HALF_UP);
    return salarioBase.multiply(fator);
}
































   
}






    

