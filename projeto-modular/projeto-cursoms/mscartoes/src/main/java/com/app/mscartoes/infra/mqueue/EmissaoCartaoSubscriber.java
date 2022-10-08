package com.app.mscartoes.infra.mqueue;

import com.app.mscartoes.domain.Cartao;
import com.app.mscartoes.domain.ClienteCartao;
import com.app.mscartoes.domain.DadosSolicitacaoEmissaoCartao;
import com.app.mscartoes.infra.repository.CartaoRepository;
import com.app.mscartoes.infra.repository.ClienteCartaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmissaoCartaoSubscriber {

    private final CartaoRepository cartaoRepository;
    private final ClienteCartaoRepository clienteCartaoRepository;

    @RabbitListener(queues = "${mq.queues.emissao-cartoes}")
    public void receberSolicitacaoEmissao(@Payload String payload) {
        try {
            var mapper = new ObjectMapper();

            DadosSolicitacaoEmissaoCartao dados = mapper.readValue(payload, DadosSolicitacaoEmissaoCartao.class);
            Cartao cartao = cartaoRepository.findById(dados.getIdCartao()).orElseThrow();

            ClienteCartao clienteCatao = new ClienteCartao();
            clienteCatao.setCartao(cartao);
            clienteCatao.setCpf(dados.getCpf());
            clienteCatao.setLimite(dados.getLimiteLiberado());

            clienteCartaoRepository.save(clienteCatao);
        } catch (Exception ex) {
            log.error("Erro ao receber solicitacao de emissao de cartao: {}", ex.getMessage());
        }
    }
}
