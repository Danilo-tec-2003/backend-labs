# 01 - API Foundation Lab

## Objetivo

Criar uma API REST simples e bem organizada com Spring Boot, PostgreSQL, Flyway e Docker Compose.

Este lab existe para formar a base dos outros. Antes de estudar JWT, mensageria, observabilidade ou AWS, voce precisa ter uma API pequena que rode localmente, persista dados e tenha endpoints claros.

## Projeto Recomendado

Nome sugerido: `order-api-foundation`

Dominio simples: pedidos.

A API deve permitir:

- cadastrar clientes;
- cadastrar produtos;
- criar pedidos;
- consultar pedidos;
- alterar status de pedidos de forma controlada.

Nao implemente autenticacao neste lab. Nao implemente mensageria. Nao implemente AWS. O foco e API, persistencia, validacao e estrutura.

## Stack

| Area | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot |
| API | Spring Web |
| Validacao | Bean Validation |
| Banco | PostgreSQL |
| Persistencia | Spring Data JPA |
| Migrations | Flyway |
| Ambiente local | Docker Compose |
| Documentacao | SpringDoc OpenAPI |

## Conceitos Que Voce Deve Aprender

- Estrutura basica de um projeto Spring Boot.
- Controllers, services, repositories e DTOs.
- Validacao de entrada com Bean Validation.
- Mapeamento JPA basico.
- Relacionamentos simples entre entidades.
- Migrations com Flyway.
- Tratamento padronizado de erros.
- Configuracao por `application.yml`.
- Separacao entre DTO de entrada, DTO de saida e entidade persistida.

## Escopo Funcional

### Cliente

Endpoints:

- `POST /api/v1/customers`
- `GET /api/v1/customers/{id}`
- `GET /api/v1/customers`

Campos:

- `id`
- `name`
- `email`
- `document`
- `createdAt`

Regras:

- nome obrigatorio;
- email obrigatorio e valido;
- documento obrigatorio;
- email nao pode duplicar.

### Produto

Endpoints:

- `POST /api/v1/products`
- `GET /api/v1/products/{id}`
- `GET /api/v1/products`

Campos:

- `id`
- `name`
- `price`
- `active`
- `createdAt`

Regras:

- nome obrigatorio;
- preco maior que zero;
- produto nasce ativo.

### Pedido

Endpoints:

- `POST /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `GET /api/v1/orders`
- `PATCH /api/v1/orders/{id}/status`

Campos:

- `id`
- `customerId`
- `items`
- `total`
- `status`
- `createdAt`

Status:

- `CREATED`
- `PAID`
- `CANCELED`

Regras:

- pedido precisa ter cliente existente;
- pedido precisa ter pelo menos um item;
- produto inativo nao pode entrar em pedido;
- total deve ser calculado no backend;
- pedido cancelado nao pode virar pago;
- pedido pago nao pode voltar para criado.

## Escopo Tecnico

Implemente:

- `docker-compose.yml` com PostgreSQL.
- Flyway com migrations iniciais.
- Entidades JPA.
- Repositories.
- Services com regras de negocio.
- Controllers.
- DTOs de request e response.
- `@ControllerAdvice` para erros.
- Swagger/OpenAPI.

Nao implemente:

- login;
- JWT;
- filas;
- Prometheus;
- deploy;
- cache;
- microservicos.

## Passo a Passo Sugerido

1. Criar projeto no Spring Initializr.
2. Configurar PostgreSQL no Docker Compose.
3. Configurar Flyway.
4. Criar migration de clientes.
5. Implementar `POST /customers`.
6. Implementar tratamento de erro e validacao.
7. Repetir para produtos.
8. Criar migrations de pedidos e itens.
9. Implementar criacao de pedido.
10. Implementar consulta de pedido.
11. Implementar alteracao de status.
12. Documentar como rodar.

## Criterios de Pronto

O lab esta pronto quando:

- a API sobe com um comando;
- o PostgreSQL sobe via Docker Compose;
- as tabelas sao criadas por Flyway;
- todos os endpoints principais funcionam;
- entradas invalidas retornam erro claro;
- o total do pedido e calculado no backend;
- o README do codigo explica como rodar;
- o Swagger mostra os endpoints.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- por que usar migrations;
- por que nao confiar no total enviado pelo cliente;
- diferenca entre DTO e entidade JPA;
- onde ficam regras de negocio;
- como voce tratou erros de validacao;
- como voce evitaria duplicidade de email.

## Como Levar Para a Fintech

Depois deste lab, implemente na fintech:

- `POST /api/v1/customers`;
- migration de cliente;
- validacoes de entrada;
- DTOs;
- tratamento global de erros;
- persistencia com PostgreSQL.

