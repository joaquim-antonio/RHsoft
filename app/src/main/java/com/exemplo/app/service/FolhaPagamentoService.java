package com.exemplo.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.EditarPagamentoDto;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoPericulosidade;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.FuncionarioRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.transaction.Transactional;


@Service
public class FolhaPagamentoService {

@Autowired
private FuncionarioService funcionarioService;

@Autowired
private FuncionarioRepository funcionarioRepository;

@Autowired
private FolhaPagamentoRepository folhaPagamentoRepository;

@Autowired
private PagamentoRepository pagamentoRepository;


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
        case INSALUBRIDADE -> percentual = insalubridadePercentual;

        case PERICULOSIDADE -> {
            percentual = switch (nivelPericulosidade) {
            case BAIXO -> new BigDecimal("10");
            case MEDIO -> new BigDecimal("20");
            case ALTO -> new BigDecimal("40");
            default -> BigDecimal.ZERO;
        };
        }

        default -> percentual = BigDecimal.ZERO;

    }
    BigDecimal fator = percentual.divide(cem, 4, RoundingMode.HALF_UP);
    return salarioBase.multiply(fator);
}


public BigDecimal calcularValeTransporte(BigDecimal salarioBase) {
        return salarioBase.multiply(new BigDecimal("0.06"))
                .setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal calcularFGTS(BigDecimal salarioBruto) {
        return salarioBruto.multiply(new BigDecimal("0.08"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    
    public BigDecimal calcularINSS(BigDecimal salario) {

        if (salario.compareTo(new BigDecimal("1412.00")) <= 0) {
            return salario.multiply(new BigDecimal("0.075"));
        }
        if (salario.compareTo(new BigDecimal("2666.68")) <= 0) {
            return salario.multiply(new BigDecimal("0.09"))
                    .subtract(new BigDecimal("21.18"));
        }
        if (salario.compareTo(new BigDecimal("4000.03")) <= 0) {
            return salario.multiply(new BigDecimal("0.12"))
                    .subtract(new BigDecimal("101.18"));
        }
        if (salario.compareTo(new BigDecimal("7786.02")) <= 0) {
            return salario.multiply(new BigDecimal("0.14"))
                    .subtract(new BigDecimal("181.18"));
        }

        return new BigDecimal("908.86");
    }

    
    public BigDecimal calcularIRRF(BigDecimal base) {

        if (base.compareTo(new BigDecimal("2259.20")) <= 0) {
            return BigDecimal.ZERO;
        }
        if (base.compareTo(new BigDecimal("2826.65")) <= 0) {
            return base.multiply(new BigDecimal("0.075"))
                    .subtract(new BigDecimal("169.44"));
        }
        if (base.compareTo(new BigDecimal("3751.05")) <= 0) {
            return base.multiply(new BigDecimal("0.15"))
                    .subtract(new BigDecimal("381.44"));
        }
        if (base.compareTo(new BigDecimal("4664.68")) <= 0) {
            return base.multiply(new BigDecimal("0.225"))
                    .subtract(new BigDecimal("662.77"));
        }

        return base.multiply(new BigDecimal("0.275"))
                .subtract(new BigDecimal("896.00"));
    }


    
    public BigDecimal calculoDeBeneficios(
            Funcionario funcionario,
            TipoAcrescimo tipo,
            TipoPericulosidade nivelPericulosidade,
            BigDecimal valorAlimentacao) {

        BigDecimal salarioBaseCalculado = CalcularSalariosDeFuncionarios(funcionario);

        BigDecimal vt = calcularValeTransporte(salarioBaseCalculado);

        if (valorAlimentacao == null) valorAlimentacao = BigDecimal.ZERO;

        BigDecimal adicional = BigDecimal.ZERO;
        if (tipo != null && tipo != TipoAcrescimo.NENHUM) {
            adicional = incluirAcrescimo(funcionario, tipo, nivelPericulosidade);
        }

        
        BigDecimal salarioBruto = salarioBaseCalculado.add(adicional);

        
        BigDecimal inss = calcularINSS(salarioBruto);
        BigDecimal irrf = calcularIRRF(salarioBruto.subtract(inss));
        BigDecimal fgts = calcularFGTS(salarioBruto);

        return salarioBruto
                .subtract(inss)
                .subtract(irrf)
                .subtract(vt)
                .add(valorAlimentacao)
                .setScale(2, RoundingMode.HALF_UP);
    }



    public BigDecimal calcularSalarioLiquido(
        Funcionario funcionario,
        TipoAcrescimo tipo,
        TipoPericulosidade nivelPericulosidade,
        BigDecimal valorAlimentacao) {

    
    BigDecimal salarioBase = CalcularSalariosDeFuncionarios(funcionario);


    BigDecimal adicional = BigDecimal.ZERO;
    if (tipo != null && tipo != TipoAcrescimo.NENHUM) {
        adicional = incluirAcrescimo(funcionario, tipo, nivelPericulosidade);
    }

   
    BigDecimal salarioBruto = salarioBase.add(adicional);

    
    BigDecimal vt = calcularValeTransporte(salarioBase);
    BigDecimal inss = calcularINSS(salarioBruto);
    BigDecimal irrf = calcularIRRF(salarioBruto.subtract(inss));
    BigDecimal fgts = calcularFGTS(salarioBruto); 

 
    if (valorAlimentacao == null) valorAlimentacao = BigDecimal.ZERO;

    
    BigDecimal salarioLiquido = salarioBruto
            .subtract(inss)
            .subtract(irrf)
            .subtract(vt)
            .add(valorAlimentacao)
            .setScale(2, RoundingMode.HALF_UP);

    return salarioLiquido;
}



public FolhaPagamento abrirFolha(Administrador admin) {

    FolhaPagamento folha = new FolhaPagamento();
    folha.setStatus(StatusPagamento.ABERTO);
    folha.setAdministrador(admin);
    folha.setTotalLiquido(BigDecimal.ZERO);

    return folhaPagamentoRepository.save(folha);
}


public FolhaPagamento fecharFolha(Long idFolha) {

    FolhaPagamento folha = folhaPagamentoRepository.findById(idFolha)
            .orElseThrow(() -> new RuntimeException("Folha não encontrada"));

    if (folha.getStatus() != StatusPagamento.ABERTO) {
        throw new RuntimeException("Só é possível fechar uma folha ABERTA.");
    }

    BigDecimal total = folha.getPagamentos().stream()
            .map(Pagamento::getValorLiquido)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    folha.setTotalLiquido(total);
    folha.setDataFechamento(LocalDate.now());
    folha.setStatus(StatusPagamento.FECHADA);

    return folhaPagamentoRepository.save(folha);
}


public FolhaPagamento consolidarFolha(Long idFolha) {

    FolhaPagamento folha = folhaPagamentoRepository.findById(idFolha)
            .orElseThrow(() -> new RuntimeException("Folha não encontrada"));

    if (folha.getStatus() != StatusPagamento.FECHADA) {
        throw new RuntimeException("A folha precisa estar FECHADA para ser consolidada.");
    }

    folha.setStatus(StatusPagamento.CONSOLIDADA);

    return folhaPagamentoRepository.save(folha);
}


public FolhaPagamento reabrirFolha(Long idFolha) {

    FolhaPagamento folha = folhaPagamentoRepository.findById(idFolha)
            .orElseThrow(() -> new RuntimeException("Folha não encontrada"));

    if (folha.getStatus() == StatusPagamento.ABERTO) {
        throw new RuntimeException("A folha já está aberta.");
    }

    
    if (folha.getStatus() == StatusPagamento.CONSOLIDADA) {

        long dias = ChronoUnit.DAYS.between(folha.getDataFechamento(), LocalDate.now());

        if (dias > 5) {
            throw new RuntimeException("Folha consolidada não pode ser reaberta após 5 dias do fechamento.");
        }
    }

    folha.setStatus(StatusPagamento.ABERTO);

    return folhaPagamentoRepository.save(folha);
}



@Transactional
public Pagamento editarPagamento(EditarPagamentoDto dto) {

    Pagamento pagamento = pagamentoRepository.findById(dto.pagamentoId())
            .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

    FolhaPagamento folha = pagamento.getFolhaPagamento();

    if (folha.getStatus() != StatusPagamento.ABERTO) {
        throw new RuntimeException("Só é possível editar pagamentos em folhas ABERTAS.");
    }

    Funcionario funcionario = pagamento.getFuncionario();

    
    BigDecimal salarioBase = CalcularSalariosDeFuncionarios(funcionario);

    
    BigDecimal adicionalAuto = incluirAcrescimo(
            funcionario, 
            funcionario.getTipoAcrescimo(), 
            funcionario.getTipoPericulosidade()
    );


    BigDecimal horasExtras = dto.horasExtras() != null 
            ? dto.horasExtras() 
            : BigDecimal.ZERO;

    BigDecimal adicionalManual = dto.adicionalManual() != null
            ? dto.adicionalPagamento()
            : BigDecimal.ZERO;


    BigDecimal salarioBruto = salarioBase
            .add(adicionalAuto)
            .add(horasExtras)
            .add(adicionalManual);

    BigDecimal inss = calcularINSS(salarioBruto);
    BigDecimal irrf = calcularIRRF(salarioBruto.subtract(inss));
    BigDecimal vt = calcularValeTransporte(salarioBase);
    BigDecimal fgts = calcularFGTS(salarioBruto); 

    BigDecimal salarioLiquido = salarioBruto
            .subtract(inss)
            .subtract(irrf)
            .subtract(vt)
            .add(pagamento.getValeAlimentacao())
            .setScale(2, RoundingMode.HALF_UP);

    // 7. salvar alterações no pagamento
    pagamento.setHorasExtras(horasExtras);
    pagamento.setAdicionalPagamento(adicionalManual);
    pagamento.setSalarioBaseCalculado(salarioBase);
    pagamento.setValorLiquido(salarioLiquido);

    return pagamentoRepository.save(pagamento);
}






































   
}






    

