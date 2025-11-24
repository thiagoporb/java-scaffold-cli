package net.jlstechnology.scaffold.templates;

import net.jlstechnology.scaffold.core.ExecutionProfile;

/**
 * Constrói os contratos OpenAPI baseados nos profiles definidos.
 */
public final class OpenApiTemplate {

    private OpenApiTemplate() {
        // Classe utilitária, não deve ser instanciada.
    }

    /**
     * Gera o conteúdo do arquivo OpenAPI para um profile específico.
     *
     * @param profile profile considerado.
     * @return contrato em formato YAML.
     */
    public static String render(ExecutionProfile profile) {
        String profileName = profile.profileName();
        return """
                openapi: 3.0.3
                info:
                  title: API - Serviço Base
                  description: Contrato OpenAPI para o profile %1$s.
                  version: 1.0.0
                servers:
                  - url: https://api.example.com/%1$s
                    description: Ambiente %1$s
                tags:
                  - name: Exemplo
                    description: Operações de manutenção de exemplos.
                paths:
                  /api/v1/exemplos:
                    post:
                      tags:
                        - Exemplo
                      summary: Cria um registro de exemplo
                      operationId: criarExemplo
                      requestBody:
                        required: true
                        content:
                          application/json:
                            schema:
                              $ref: '#/components/schemas/RequestExemplo'
                      responses:
                        '201':
                          description: Registro criado com sucesso.
                          headers:
                            Location:
                              description: URI do recurso recém-criado.
                              schema:
                                type: string
                          content:
                            application/json:
                              schema:
                                $ref: '#/components/schemas/ResponseExemplo'
                    get:
                      tags:
                        - Exemplo
                      summary: Lista registros de exemplo
                      operationId: listarExemplos
                      responses:
                        '200':
                          description: Requisição bem-sucedida.
                          content:
                            application/json:
                              schema:
                                type: array
                                items:
                                  $ref: '#/components/schemas/ResponseExemplo'
                components:
                  schemas:
                    RequestExemplo:
                      type: object
                      properties:
                        valor:
                          type: string
                    ResponseExemplo:
                      type: object
                      properties:
                        id:
                          type: string
                          format: uuid
                        valor:
                          type: string
                """.formatted(profileName);
    }

}

