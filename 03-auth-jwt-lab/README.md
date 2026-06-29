# 03 - Auth JWT Lab

## Objetivo

Construir um fluxo de autenticacao e autorizacao com Spring Security e JWT.

Este lab serve para entender login robusto sem jogar a complexidade dentro da fintech logo no comeco.

## Projeto Recomendado

Nome sugerido: `auth-jwt-lab`

Dominio simples: autenticacao de usuarios e acesso a endpoints protegidos.

A API deve permitir:

- registrar usuario;
- fazer login;
- emitir access token JWT;
- emitir refresh token;
- renovar access token;
- fazer logout;
- proteger endpoints por role ou scope.

## Stack

| Area | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot |
| Seguranca | Spring Security |
| Tokens | JWT |
| Persistencia | PostgreSQL |
| Senhas | BCrypt |
| Testes | JUnit, MockMvc, Testcontainers |

## Conceitos Que Voce Deve Aprender

- Autenticacao vs autorizacao.
- Spring Security filter chain.
- Password hashing com BCrypt.
- Access token e refresh token.
- Claims de JWT.
- Expiracao de token.
- Roles e scopes.
- Protecao de endpoint.
- Erros `401` vs `403`.
- Armazenamento seguro de refresh token.
- Revogacao de sessao.

## Aviso Importante

Este lab pode emitir JWT na propria aplicacao para aprendizado.

Em sistemas reais, especialmente em empresas maiores, e comum usar um provedor de identidade como Keycloak, Auth0, Cognito ou outro Authorization Server. Mesmo assim, implementar o fluxo uma vez ajuda voce a entender o que esta acontecendo.

## Escopo Funcional

### Registro

Endpoint:

- `POST /api/v1/auth/register`

Campos:

- `name`
- `email`
- `password`

Regras:

- email unico;
- senha deve ter regra minima de complexidade;
- senha nunca deve ser salva em texto puro;
- usuario nasce com role `USER`.

### Login

Endpoint:

- `POST /api/v1/auth/login`

Entrada:

- `email`
- `password`

Saida:

- `accessToken`
- `refreshToken`
- `expiresIn`

Regras:

- credencial invalida retorna `401`;
- login correto gera access token curto;
- refresh token deve ser persistido de forma segura.

### Refresh

Endpoint:

- `POST /api/v1/auth/refresh`

Regras:

- refresh token valido gera novo access token;
- refresh token expirado retorna `401`;
- refresh token revogado retorna `401`.

### Logout

Endpoint:

- `POST /api/v1/auth/logout`

Regras:

- revogar refresh token;
- access token pode continuar valido ate expirar;
- explicar essa decisao no README.

### Endpoints Protegidos

Crie endpoints de exemplo:

- `GET /api/v1/me` para usuario autenticado;
- `GET /api/v1/admin/users` apenas para `ADMIN`;
- `GET /api/v1/reports` apenas para scope ou role especifica.

## Escopo Tecnico

Implemente:

- entidade `User`;
- entidade ou tabela `RefreshToken`;
- hashing com BCrypt;
- filtro ou configuracao de JWT;
- `SecurityFilterChain`;
- tratamento padronizado de `401` e `403`;
- testes de login;
- testes de endpoint protegido;
- testes de refresh token;
- documentacao dos claims.

Nao implemente:

- tela de login;
- envio real de email;
- MFA;
- OAuth2 completo;
- login social.

## Claims Recomendadas

No access token:

- `sub`: identificador do usuario;
- `email`: email;
- `roles`: permissoes;
- `iat`: emitido em;
- `exp`: expiracao;
- `iss`: emissor.

Nao coloque no JWT:

- senha;
- documento sensivel;
- dados financeiros;
- informacoes que mudam toda hora.

## Passo a Passo Sugerido

1. Criar projeto Spring Boot.
2. Criar tabela de usuarios.
3. Implementar registro com BCrypt.
4. Implementar login validando senha.
5. Gerar JWT.
6. Proteger `GET /me`.
7. Criar role `ADMIN`.
8. Proteger endpoint admin.
9. Criar refresh token persistido.
10. Implementar refresh.
11. Implementar logout.
12. Criar testes de sucesso e falha.
13. Documentar decisoes de seguranca.

## Criterios de Pronto

O lab esta pronto quando:

- senha e salva com hash;
- login retorna JWT;
- endpoints protegidos rejeitam usuario anonimo;
- endpoint admin rejeita usuario comum;
- refresh token funciona;
- logout revoga refresh token;
- testes cobrem `401` e `403`;
- voce sabe explicar access token vs refresh token.

## O Que Evitar

- Salvar senha sem hash.
- Access token com validade muito longa.
- Colocar dados sensiveis no token.
- Usar uma string secreta fraca em codigo.
- Retornar erro diferente para "email nao existe" e "senha errada".
- Misturar regra de negocio com filtro de seguranca.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- diferenca entre `401` e `403`;
- por que usar BCrypt;
- o que e uma claim;
- como expirar token;
- como revogar refresh token;
- por que access token geralmente nao fica salvo no banco;
- quando usar Keycloak em vez de implementar login proprio.

## Como Levar Para a Fintech

Depois deste lab, aplique na fintech:

- protecao de endpoints;
- usuario autenticado em auditoria;
- roles ou scopes como `payment:create` e `ledger:read`;
- substituicao do HTTP Basic inicial;
- testes de acesso permitido e negado.

