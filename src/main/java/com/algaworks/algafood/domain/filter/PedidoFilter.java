package com.algaworks.algafood.domain.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;

@Setter
@Getter
public class PedidoFilter {

    private Long clienteId;
    private Long restauranteId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dataCriacaoInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime dataCriacaoFim;

}
