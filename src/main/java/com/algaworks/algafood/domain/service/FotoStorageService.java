package com.algaworks.algafood.domain.service;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;
import java.util.UUID;

public interface FotoStorageService {

    FotoRecuperada recuperar(String nomeArquivo);

    void armazenar(NovaFoto novaFoto);

    void remover(String nomeArquivo);

    default void substituir(String nomeArquivoAntigo, NovaFoto novaFoto) {
        this.armazenar(novaFoto);

        if (nomeArquivoAntigo != null) {
            this.remover(nomeArquivoAntigo);
        }
    }

    default String gerarNomeArquivo(String nomeOriginal) {
        return UUID.randomUUID().toString() + "_" + nomeOriginal;
    }

    @Builder
    @Getter
    class NovaFoto {

        private String nomeArquivo;
        private InputStream inputStream;
        private String contentType;
        private Long size;

    }

    @Builder
    @Getter
    class FotoRecuperada {

        private InputStream inputStream;
        private String url;

        public boolean temUrl() {
            return url != null;
        }

        public boolean temInputStrea() {
            return inputStream != null;
        }

    }

}
