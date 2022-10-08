package com.app.msavaliadorcredito.application;

import com.app.msavaliadorcredito.application.ex.DadosClienteNotFoundException;
import com.app.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import com.app.msavaliadorcredito.application.ex.ErrorComunicacaoMicroservicesException;
import com.app.msavaliadorcredito.domain.model.*;
import com.app.msavaliadorcredito.infra.clients.CartoesResourceClient;
import com.app.msavaliadorcredito.infra.clients.ClienteResourceClient;
import com.app.msavaliadorcredito.infra.mque.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteResourceClient clienteResourceClient;
    private final CartoesResourceClient cartoesResourceClient;
    private final SolicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;
    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErrorComunicacaoMicroservicesException {
        try {
            ResponseEntity<List<CartaoCliente>> cartaoClienteResponse = cartoesResourceClient.getCartoesByCliente(cpf);
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartaoClienteResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException ex) {
            var status = ex.status();

            if(HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClienteNotFoundException();
            }

            throw new ErrorComunicacaoMicroservicesException(ex.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda)
            throws DadosClienteNotFoundException, ErrorComunicacaoMicroservicesException {
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clienteResourceClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesResourceClient.getCartoesRendaAteh(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();

            var listaCartoesAprovados = cartoes.stream().map(cartao -> {

                DadosCliente dadosCliente = dadosClienteResponse.getBody();

                BigDecimal limiteBasico = cartao.getLimiteBasico();
                BigDecimal idadeBigDecimal = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeBigDecimal.divide(BigDecimal.valueOf(10));
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());

            return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        } catch (FeignException.FeignClientException ex) {
        var status = ex.status();

        if(HttpStatus.NOT_FOUND.value() == status) {
            throw new DadosClienteNotFoundException();
        }

        throw new ErrorComunicacaoMicroservicesException(ex.getMessage(), status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dadosSolicitacaoEmissaoCartao) {
        try {
            emissaoCartaoPublisher.solicitarCartao(dadosSolicitacaoEmissaoCartao);
            var protocolo = UUID.randomUUID().toString();

            return new ProtocoloSolicitacaoCartao(protocolo);
        } catch (Exception ex) {
            throw new ErroSolicitacaoCartaoException(ex.getMessage());
        }
    }
}
