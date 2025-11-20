package com.exemplo.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemplo.app.model.ItemPagamento;
import com.exemplo.app.model.Pagamento;
import com.exemplo.app.repository.ItemPagamentoRepository;
import com.exemplo.app.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ItemPagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ItemPagamentoRepository itemPagamentoRepository;

    //GET
    public ItemPagamento buscarItemPorId(Long id) {
        return itemPagamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item de Pagamento não encontrado."));
    }

    //PUT
    public Pagamento adicionarItemAoPagamento(String pagamentoCodigo, ItemPagamento novoItem){
        Pagamento pagamento = pagamentoRepository.findByCodigo(pagamentoCodigo)
            .orElseThrow(() -> new EntityNotFoundException("Pagamento não cadastrado"));

        novoItem.setPagamento(pagamento);
        pagamento.getItens().add(novoItem);

        pagamento.calcularTotais();

        return pagamentoRepository.save(pagamento);
    }
    
    //DELETE
    public Pagamento removerItemDoPagamento(String pagamentoCodigo, Long itemId){
        Pagamento pagamento = pagamentoRepository.findByCodigo(pagamentoCodigo)
            .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado"));
        
        ItemPagamento itemRemovido = buscarItemPorId(itemId);

        if (!itemRemovido.getPagamento().equals(pagamento)){
            throw new IllegalArgumentException("O item não corresponde ao pagamento");
        }

        pagamento.getItens().remove(itemRemovido);

        pagamento.calcularTotais();

        return pagamentoRepository.save(pagamento);
        
    }


}
