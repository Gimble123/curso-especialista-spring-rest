package com.algaworks.algafood;

import com.algaworks.algafood.core.io.Base64ProtocolResolver;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.util.DatabaseCleaner;
import com.algaworks.algafood.util.ResourceUtils;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@ContextConfiguration(initializers = Base64ProtocolResolver.class) //Chama o conversor base64
public class CadastroCozinhaIT {

    private static final int COZINHA_ID_INEXISTENTE = 100;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private CozinhaRepository cozinhaRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    private Cozinha cozinhaAmericana;
    private int quantidadeCozinhasCadastradas;
    private String jsonCorretoCozinhaChinesa;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssuredMockMvc.basePath = "/v1/cozinhas";

        jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource("/json/correto/cozinha-chinesa.json");

        databaseCleaner.clearTables();
        prepararDados();
    }

    @Test
    @WithMockUser(
            username="joao.ger@algafood.com.br",
            authorities = {
                    "SCOPE_READ",
                    "SCOPE_WRITE",
                    "EDITAR_COZINHAS"
            }
    )
    public void deveRetornarStatus200_QuandoConsultarCozinhas() {
        RestAssuredMockMvc
                .given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @WithMockUser(
            username="joao.ger@algafood.com.br",
            authorities = {
                    "SCOPE_READ",
                    "SCOPE_WRITE",
                    "EDITAR_COZINHAS"
            }
    )
    public void deveRetornarQuantidadeCorretaDeCozinhas_QuandoConsultarCozinhas() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .body("_embedded.cozinhas", hasSize(quantidadeCozinhasCadastradas));
        //_embedded.cozinhas já que estou usando HATEOAS
    }

    @Test
    @WithMockUser(
            username="joao.ger@algafood.com.br",
            authorities = {
                    "SCOPE_READ",
                    "SCOPE_WRITE",
                    "EDITAR_COZINHAS"
            }
    )
    public void deveRetornarStatus201_QuandoCadastrarCozinha() {
        RestAssuredMockMvc
                .given()
                .body(jsonCorretoCozinhaChinesa)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }


    @Test
    @WithMockUser(
            username="joao.ger@algafood.com.br",
            authorities = {
                    "SCOPE_READ",
                    "SCOPE_WRITE",
                    "EDITAR_COZINHAS"
            }
    )
    public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get(cozinhaAmericana.getId().toString())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("nome", equalTo(cozinhaAmericana.getNome()));
    }

    @Test
    @WithMockUser(
            username="joao.ger@algafood.com.br",
            authorities = {
                    "SCOPE_READ",
                    "SCOPE_WRITE",
                    "EDITAR_COZINHAS"
            }
    )
    public void deveRetornarStatus404_QuandoConsultarCozinhaInexistente() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get(String.valueOf(COZINHA_ID_INEXISTENTE))
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    private void prepararDados() {
        Cozinha cozinhaTailandesa = new Cozinha();
        cozinhaTailandesa.setNome("Tailandesa");
        cozinhaRepository.save(cozinhaTailandesa);

        cozinhaAmericana = new Cozinha();
        cozinhaAmericana.setNome("Americana");
        cozinhaRepository.save(cozinhaAmericana);

        quantidadeCozinhasCadastradas = (int) cozinhaRepository.count();
    }

}