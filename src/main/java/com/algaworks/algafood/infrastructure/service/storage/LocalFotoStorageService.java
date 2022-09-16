package com.algaworks.algafood.infrastructure.service.storage;

import com.algaworks.algafood.domain.service.FotoStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;

import java.nio.file.Files;
import java.nio.file.Path;

public class LocalFotoStorageService implements FotoStorageService {

    @Value("${algafood.storage.local.diretorio-fotos}")
    private Path diretorioFotos;

    @Override
    public FotoRecuperada recuperar(String nomeArquivo) {
        try {
            Path arquivoPath = getArquivoPath(nomeArquivo);

            return FotoRecuperada.builder()
                    .inputStream(Files.newInputStream(arquivoPath))
                    .build();
        } catch (Exception e) {
            throw new StorageException("Não foi possível recuperar arquivo.", e);
        }

    }

    @Override
    public void armazenar(NovaFoto novaFoto) {
        try {
            Path arquivoPath = getArquivoPath(novaFoto.getNomeArquivo());

            FileCopyUtils.copy(novaFoto.getInputStream(),
                    Files.newOutputStream(arquivoPath));
        } catch (Exception e) {
            throw new StorageException("Não foi possível armazenar arquivo.", e);
        }
    }

    @Override
    public void remover(String nomeArquivo) {
        try {
            Path arquivoPath = getArquivoPath(nomeArquivo);

            Files.deleteIfExists(arquivoPath);
        } catch (Exception e) {
            throw new StorageException("Não foi possível excluir arquivo.", e);
        }
    }

    private Path getArquivoPath(String nomeArquivo) {
        return diretorioFotos.resolve(Path.of(nomeArquivo));
    }
}
