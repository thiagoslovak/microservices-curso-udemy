package com.app.mscartoes.application.representation;

import com.app.mscartoes.domain.ClienteCartao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartoesPorClienteResponse {

    private String nome;
    private String bandeira;
    private BigDecimal limiteLiberado;

    public static CartoesPorClienteResponse fromModel(ClienteCartao clienteCartaoModel) {
        return new CartoesPorClienteResponse(
                clienteCartaoModel.getCartao().getNome(),
                clienteCartaoModel.getCartao().getBandeira().toString(),
                clienteCartaoModel.getLimite()
        );
    }
}
