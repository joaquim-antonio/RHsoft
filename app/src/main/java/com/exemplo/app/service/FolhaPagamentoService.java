package com.exemplo.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.dto.EditarPagamentoDto;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.Enums.TipoAcrescimo;
import com.exemplo.app.model.Enums.TipoInsalubridade;
import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.transaction.Transactional;


@Service

public class FolhaPagamentoService {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

   
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1412.00");
    private static final BigDecimal HORAS_MENSAIS = new BigDecimal("220");

    
    @Transactional
    public void gerarFolhaDePagamento(Long idFolha) {
        FolhaPagamento folha = folhaPagamentoRepository.findById(idFolha)
                .orElseThrow(() -> new RuntimeException("Folha não encontrada"));

        if (folha.getStatus() != StatusPagamento.ABERTO) {
            throw new RuntimeException("A folha não está aberta para geração.");
        }

        List<Funcionario> funcionarios = funcionarioService.listarTodosFuncionarios();

        for (Funcionario f : funcionarios) {
            criarPagamentoParaFuncionario(f, folha);
        }
    }

    
    private void criarPagamentoParaFuncionario(Funcionario funcionario, FolhaPagamento folha) {
        Pagamento pagamento = new Pagamento();
        pagamento.setFuncionario(funcionario);
        pagamento.setFolhaPagamento(folha);
        pagamento.setMesAnoReferencia(LocalDate.now().toString().substring(0, 7)); 
        pagamento.setVencimento(LocalDate.now().plusDays(5)); 
        pagamento.setItens(new ArrayList<>());

      
        BigDecimal salarioBase = calcularSalarioBase(funcionario);
        pagamento.setSalarioBase(salarioBase);
        adicionarItem(pagamento, "Salário Base", TipoItemPagamento.PROVENTO, salarioBase);

        
        BigDecimal valorHorasExtras = BigDecimal.ZERO;
        pagamento.setHorasExtras(valorHorasExtras);

        
        BigDecimal adicional = calcularAdicional(funcionario);
        if (adicional.compareTo(BigDecimal.ZERO) > 0) {
            String nomeAdicional = funcionario.getTipoAcrescimo() == TipoAcrescimo.INSALUBRIDADE ? "Insalubridade" : "Periculosidade";
            adicionarItem(pagamento, nomeAdicional, TipoItemPagamento.PROVENTO, adicional);
        }

        
        BigDecimal totalBruto = salarioBase.add(valorHorasExtras).add(adicional);

   
        BigDecimal inss = calcularINSS(totalBruto);
        adicionarItem(pagamento, "INSS", TipoItemPagamento.DESCONTO, inss);

       
        BigDecimal baseIrrf = totalBruto.subtract(inss);
        BigDecimal irrf = calcularIRRF(baseIrrf);
        if (irrf.compareTo(BigDecimal.ZERO) > 0) {
            adicionarItem(pagamento, "IRRF", TipoItemPagamento.DESCONTO, irrf);
        }

        
        BigDecimal vt = calcularValeTransporte(salarioBase);
        if (vt.compareTo(BigDecimal.ZERO) > 0) {
            adicionarItem(pagamento, "Vale Transporte", TipoItemPagamento.DESCONTO, vt);
        }

       
        pagamento.calcularTotais();
        
        pagamentoRepository.save(pagamento);
    }

    @Transactional
    public Pagamento editarPagamento(EditarPagamentoDto dto) {
        Pagamento pagamento = pagamentoRepository.findByCodigo(dto.codigo())
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (pagamento.getFolhaPagamento().getStatus() != StatusPagamento.ABERTO) {
            throw new RuntimeException("Só é possível editar pagamentos em folhas ABERTAS.");
        }

        
        pagamento.getItens().clear();

        Funcionario funcionario = pagamento.getFuncionario();
        BigDecimal salarioBase = calcularSalarioBase(funcionario);
        pagamento.setSalarioBase(salarioBase);
        adicionarItem(pagamento, "Salário Base", TipoItemPagamento.PROVENTO, salarioBase);

        
        BigDecimal adicionalAuto = calcularAdicional(funcionario);
        if (adicionalAuto.compareTo(BigDecimal.ZERO) > 0) {
            String nomeAdicional = funcionario.getTipoAcrescimo() == TipoAcrescimo.INSALUBRIDADE ? "Insalubridade" : "Periculosidade";
            adicionarItem(pagamento, nomeAdicional, TipoItemPagamento.PROVENTO, adicionalAuto);
        }

        
        BigDecimal horasExtras = dto.horasExtras() != null ? dto.horasExtras() : BigDecimal.ZERO;
        pagamento.setHorasExtras(horasExtras);
        if (horasExtras.compareTo(BigDecimal.ZERO) > 0) {
            adicionarItem(pagamento, "Horas Extras", TipoItemPagamento.PROVENTO, horasExtras);
        }

        
        BigDecimal adicionalManual = dto.adicionalManual() != null ? dto.adicionalManual() : BigDecimal.ZERO;
        if (adicionalManual.compareTo(BigDecimal.ZERO) > 0) {
            adicionarItem(pagamento, "Adicional Manual", TipoItemPagamento.PROVENTO, adicionalManual);
        }

        BigDecimal totalBruto = salarioBase.add(adicionalAuto).add(horasExtras).add(adicionalManual);

        BigDecimal inss = calcularINSS(totalBruto);
        adicionarItem(pagamento, "INSS", TipoItemPagamento.DESCONTO, inss);

        BigDecimal irrf = calcularIRRF(totalBruto.subtract(inss));
        if (irrf.compareTo(BigDecimal.ZERO) > 0) {
            adicionarItem(pagamento, "IRRF", TipoItemPagamento.DESCONTO, irrf);
        }

        BigDecimal vt = calcularValeTransporte(salarioBase);
        if (vt.compareTo(BigDecimal.ZERO) > 0) {
            adicionarItem(pagamento, "Vale Transporte", TipoItemPagamento.DESCONTO, vt);
        }

        
        pagamento.calcularTotais();

        return pagamentoRepository.save(pagamento);
    }

   
    private void adicionarItem(Pagamento pagamento, String descricao, TipoItemPagamento tipo, BigDecimal valor) {
        ItemPagamento item = ItemPagamento.builder()
                .descricao(descricao)
                .tipo(tipo)
                .valor(valor.setScale(2, RoundingMode.HALF_UP))
                .pagamento(pagamento)
                .build();
        pagamento.getItens().add(item);
    }

    public BigDecimal calcularSalarioBase(Funcionario funcionario) {
        BigDecimal valorBruto = funcionario.getSalario();
       
        return valorBruto; 
    }

    public BigDecimal calcularAdicional(Funcionario funcionario) {
        TipoAcrescimo tipo = funcionario.getTipoAcrescimo();
        TipoInsalubridade nivel = funcionario.getTipoInsalubridade();
        BigDecimal baseCalculo = funcionario.getSalario(); 

        if (tipo == null || tipo == TipoAcrescimo.NENHUM) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentual = BigDecimal.ZERO;

        if (tipo == TipoAcrescimo.PERICULOSIDADE) {
            
            percentual = new BigDecimal("30");
        } else if (tipo == TipoAcrescimo.INSALUBRIDADE) {
            
            if (nivel != null) {
                switch (nivel) {
                    case BAIXO -> percentual = new BigDecimal("10");
                    case MEDIO -> percentual = new BigDecimal("20");
                    case ALTO -> percentual = new BigDecimal("40");
                    default -> percentual = BigDecimal.ZERO;
                }
            }
        }

        return baseCalculo.multiply(percentual).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularValeTransporte(BigDecimal salarioBase) {
        return salarioBase.multiply(new BigDecimal("0.06")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularFGTS(BigDecimal salarioBruto) {
        return salarioBruto.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularINSS(BigDecimal salario) {
        BigDecimal teto1 = new BigDecimal("1412.00");
        BigDecimal teto2 = new BigDecimal("2666.68");
        BigDecimal teto3 = new BigDecimal("4000.03");
        BigDecimal teto4 = new BigDecimal("7786.02");

        if (salario.compareTo(teto1) <= 0) {
            return salario.multiply(new BigDecimal("0.075"));
        }
        if (salario.compareTo(teto2) <= 0) {
            return salario.multiply(new BigDecimal("0.09")).subtract(new BigDecimal("21.18"));
        }
        if (salario.compareTo(teto3) <= 0) {
            return salario.multiply(new BigDecimal("0.12")).subtract(new BigDecimal("101.18"));
        }
        if (salario.compareTo(teto4) <= 0) {
            return salario.multiply(new BigDecimal("0.14")).subtract(new BigDecimal("181.18"));
        }
        return new BigDecimal("908.86"); 
    }

    public BigDecimal calcularIRRF(BigDecimal base) {
        BigDecimal faixa1 = new BigDecimal("2259.20");
        BigDecimal faixa2 = new BigDecimal("2826.65");
        BigDecimal faixa3 = new BigDecimal("3751.05");
        BigDecimal faixa4 = new BigDecimal("4664.68");

        if (base.compareTo(faixa1) <= 0) {
            return BigDecimal.ZERO;
        }
        if (base.compareTo(faixa2) <= 0) {
            return base.multiply(new BigDecimal("0.075")).subtract(new BigDecimal("169.44"));
        }
        if (base.compareTo(faixa3) <= 0) {
            return base.multiply(new BigDecimal("0.15")).subtract(new BigDecimal("381.44"));
        }
        if (base.compareTo(faixa4) <= 0) {
            return base.multiply(new BigDecimal("0.225")).subtract(new BigDecimal("662.77"));
        }
        return base.multiply(new BigDecimal("0.275")).subtract(new BigDecimal("896.00"));
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
                throw new RuntimeException("Folha consolidada não pode ser reaberta após 5 dias.");
            }
        }
        folha.setStatus(StatusPagamento.ABERTO);
        return folhaPagamentoRepository.save(folha);
    }
}







































   







    

