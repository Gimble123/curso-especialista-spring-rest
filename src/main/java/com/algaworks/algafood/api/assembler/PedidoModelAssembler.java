package com.algaworks.algafood.api.assembler;

import com.algaworks.algafood.api.AlgaLinks;
import com.algaworks.algafood.api.controller.*;
import com.algaworks.algafood.api.model.PedidoModel;
import com.algaworks.algafood.domain.model.Pedido;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PedidoModelAssembler
		extends RepresentationModelAssemblerSupport<Pedido, PedidoModel> {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AlgaLinks algaLinks;

	public PedidoModelAssembler() {
		super(PedidoController.class, PedidoModel.class);
	}

	@Override
	public PedidoModel toModel(Pedido pedido) {
		PedidoModel pedidoModel = createModelWithId(pedido.getCodigo(), pedido);
		modelMapper.map(pedido, pedidoModel);

		pedidoModel.add(algaLinks.linkToPedidos("pedidos"));

		if (pedido.podeSerConfirmado()) {
			pedidoModel.add(algaLinks.linkToConfirmacaoPedido(pedidoModel.getCodigo(), "confirmar"));
		}

		if (pedido.podeSerCancelado()) {
			pedidoModel.add(algaLinks.linkToCancelamentoPedido(pedidoModel.getCodigo(), "cancelar"));
		}

		if (pedido.podeSerEntregue()) {
			pedidoModel.add(algaLinks.linkToEntregaPedido(pedidoModel.getCodigo(), "entregar"));
		}

		pedidoModel.getRestaurante().add(
				algaLinks.linkToRestaurante(pedido.getRestaurante().getId()));

		pedidoModel.getCliente().add(
				algaLinks.linkToUsuario(pedido.getCliente().getId()));

		pedidoModel.getFormaPagamento().add(
				algaLinks.linkToFormaPagamento(pedido.getFormaPagamento().getId()));

		pedidoModel.getEnderecoEntrega().getCidade().add(
				algaLinks.linkToCidade(pedido.getEnderecoEntrega().getCidade().getId()));

		pedidoModel.getItens().forEach(item -> {
			item.add(algaLinks.linkToProduto(
					pedidoModel.getRestaurante().getId(), item.getProdutoId(), "produto"));
		});

		return pedidoModel;
	}
}
