package com.exemplo.app.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exemplo.app.dto.EditarPagamentoDto;
import com.exemplo.app.model.Administrador;
import com.exemplo.app.model.ConfiguracaoSistema;
import com.exemplo.app.model.Enums.StatusPagamento;
import com.exemplo.app.model.Enums.TipoItemPagamento;
import com.exemplo.app.model.FolhaPagamento;
import com.exemplo.app.model.Funcionario;
import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.FolhaPagamentoRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FolhaPagamentoService {

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ConfiguracaoService configuracaoService;

    @Autowired
    private CalculadoraFolhaService calculadoraService;


    public FolhaPagamento buscarFolhaPorId(Long id) {
        return folhaPagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Folha de pagamento não encontrada com ID: " + id));
    }

    public Pagamento buscarPagamentoPorCodigo(String codigo) {
        return pagamentoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado com código: " + codigo));
    }

    // GERAÇÃO DA FOLHA

    @Transactional
    public void gerarFolhaDePagamento(Long idFolha) {
        FolhaPagamento folha = buscarFolhaPorId(idFolha);

        if (folha.getStatus() != StatusPagamento.ABERTO) {
            throw new RuntimeException("A folha não está aberta para geração.");
        }

        // Busca configurações vigentes (Salário Mínimo, Tabelas IRRF, etc)
        ConfiguracaoSistema config = configuracaoService.buscarConfiguracaoAtual();

        // Busca funcionários ativos
        List<Funcionario> funcionarios = funcionarioService.listarTodosFuncionarios();

        // Gera pagamento para cada um
        for (Funcionario f : funcionarios) {
            criarPagamentoParaFuncionario(f, folha, config);
        }
    }

    private void criarPagamentoParaFuncionario(Funcionario funcionario, FolhaPagamento folha, ConfiguracaoSistema config) {
        Pagamento pagamento = new Pagamento();
        pagamento.setFuncionario(funcionario);
        pagamento.setFolhaPagamento(folha);
        pagamento.setMesAnoReferencia(LocalDate.now().toString().substring(0, 7)); // Ex: 2025-01
        pagamento.setVencimento(LocalDate.now().plusDays(5));
        
        // Gera código único
        String sufixoCpf = funcionario.getCpf().length() >= 3 ? funcionario.getCpf().substring(0, 3) : "000";
        pagamento.setCodigo("PAY-" + folha.getId() + "-" + System.currentTimeMillis() + "-" + sufixoCpf);

        calculadoraService.processarFolhaFuncionario(pagamento, funcionario, config);

        pagamentoRepository.save(pagamento);
    }

    // EDIÇÃO DE PAGAMENTOS

    @Transactional
    public Pagamento editarPagamento(EditarPagamentoDto dto) {
        Pagamento pagamento = buscarPagamentoPorCodigo(dto.codigo());

        if (pagamento.getFolhaPagamento().getStatus() != StatusPagamento.ABERTO) {
            throw new RuntimeException("Só é possível editar pagamentos em folhas ABERTAS.");
        }

        ConfiguracaoSistema config = configuracaoService.buscarConfiguracaoAtual();

        // Reseta os itens automáticos para garantir cálculo limpo
        if (pagamento.getItens() == null) {
            pagamento.setItens(new ArrayList<>());
        } else {
            pagamento.getItens().clear();
        }

        // Recalcula a base (Salário, INSS e IRRF padrões)
        calculadoraService.processarFolhaFuncionario(pagamento, pagamento.getFuncionario(), config);

        // Adiciona Itens Manuais (Horas Extras / Adicional)
        if (dto.horasExtras() != null && dto.horasExtras().compareTo(BigDecimal.ZERO) > 0) {
            pagamento.setHorasExtras(dto.horasExtras());
            adicionarItemManual(pagamento, "Horas Extras", TipoItemPagamento.PROVENTO, dto.horasExtras());
        }

        if (dto.adicionalManual() != null && dto.adicionalManual().compareTo(BigDecimal.ZERO) > 0) {
            adicionarItemManual(pagamento, "Adicional Manual", TipoItemPagamento.PROVENTO, dto.adicionalManual());
        }

        // (Base + Manuais)
        pagamento.calcularTotais();

        return pagamentoRepository.save(pagamento);
    }

    private void adicionarItemManual(Pagamento pagamento, String descricao, TipoItemPagamento tipo, BigDecimal valor) {
        ItemPagamento item = ItemPagamento.builder()
                .nome(descricao) // Usando 'nome' conforme sua entidade ItemPagamento
                .descricao("Lançamento Manual")
                .tipo(tipo)
                .valor(valor.setScale(2, RoundingMode.HALF_UP))
                .pagamento(pagamento)
                .build();
        pagamento.getItens().add(item);
    }

    // --- FLUXO DA FOLHA (ABRIR, FECHAR, REABRIR) ---

    @Transactional
    public FolhaPagamento abrirFolha(Administrador admin) {
        
        // Verifica se já existe folha aberta
        if (folhaPagamentoRepository.existsByStatus(StatusPagamento.ABERTO)) {
            throw new RuntimeException("Já existe uma folha de pagamento aberta.");
        }
        
        FolhaPagamento folha = new FolhaPagamento();
        folha.setStatus(StatusPagamento.ABERTO);
        folha.setAdministrador(admin);
        folha.setTotalLiquido(BigDecimal.ZERO);
        folha.setDataEnvio(LocalDate.now()); // Data referência inicial
        return folhaPagamentoRepository.save(folha);
    }

    @Transactional
    public FolhaPagamento fecharFolha(Long idFolha) {
        FolhaPagamento folha = buscarFolhaPorId(idFolha);
            

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

    @Transactional
    public FolhaPagamento consolidarFolha(Long idFolha) {
        FolhaPagamento folha = buscarFolhaPorId(idFolha);

        if (folha.getStatus() != StatusPagamento.FECHADA) {
            throw new RuntimeException("A folha precisa estar FECHADA para ser consolidada.");
        }
        folha.setStatus(StatusPagamento.CONSOLIDADA);
        
        return folhaPagamentoRepository.save(folha);
    }

    @Transactional
    public FolhaPagamento reabrirFolha(Long idFolha) {
        FolhaPagamento folha = buscarFolhaPorId(idFolha);
        ConfiguracaoSistema config = configuracaoService.buscarConfiguracaoAtual();

        if (folha.getStatus() == StatusPagamento.ABERTO) {
            throw new RuntimeException("A folha já está aberta.");
        }
        
        // Verifica prazo dinâmico do banco de dados
        if (folha.getStatus() == StatusPagamento.CONSOLIDADA) {
            long dias = ChronoUnit.DAYS.between(folha.getDataFechamento(), LocalDate.now());
            
            Integer prazoLimite = config.getDiasLimiteReabertura();
            
            if (dias > prazoLimite) {
                throw new RuntimeException("Folha consolidada há " + dias + " dias. Prazo limite para reabertura é de " + prazoLimite + " dias.");
            }
        }
        
        folha.setStatus(StatusPagamento.ABERTO);
        return folhaPagamentoRepository.save(folha);
    }

    public void enviarFolhaParaFuncionarios(Long idFolha) {
        FolhaPagamento folha = buscarFolhaPorId(idFolha);
        
        if (folha.getStatus() != StatusPagamento.FECHADA && folha.getStatus() != StatusPagamento.CONSOLIDADA) {
            throw new IllegalStateException("A folha precisa estar FECHADA ou CONSOLIDADA para ser enviada.");
        }
        // Simulação de envio (Email/Push)
        System.out.println("Enviando folha " + idFolha + " para " + folha.getPagamentos().size() + " funcionários...");
    }
}