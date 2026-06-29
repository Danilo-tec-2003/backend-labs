# 01 - API Foundation Lab

## Objetivo

Criar uma API REST simples e bem organizada com Spring Boot, PostgreSQL, Flyway e Docker Compose.

Este lab existe para formar a base dos outros. Antes de estudar JWT, mensageria, observabilidade ou AWS, voce precisa ter uma API pequena que rode localmente, persista dados e tenha endpoints claros.

## Arquitetura Que Sera Praticada

Este lab deve usar **Arquitetura Modular Orientada a Dominio**, tambem conhecida na pratica como **Package by Feature** combinado com conceitos leves de **DDD** e **Arquitetura Hexagonal**.

O objetivo nao e criar uma arquitetura enterprise pesada. O objetivo e treinar desde o primeiro lab a separacao correta entre:

- entrada HTTP;
- casos de uso;
- regras de dominio;
- persistencia;
- configuracoes compartilhadas.

Cada feature deve ter seus proprios pacotes:

```text
src/main/java/br/com/danilo/orderfoundation
|-- customer
|   |-- api
|   |-- application
|   |-- domain
|   `-- infrastructure
|-- product
|   |-- api
|   |-- application
|   |-- domain
|   `-- infrastructure
|-- order
|   |-- api
|   |-- application
|   |-- domain
|   `-- infrastructure
`-- shared
    |-- api
    |-- config
    `-- domain
```

### Responsabilidade Das Camadas

| Camada | Responsabilidade |
|---|---|
| `api` | Controllers, DTOs HTTP, validacao de entrada e mapeamento de resposta |
| `application` | Casos de uso, orquestracao, transacoes e portas/interfaces quando necessario |
| `domain` | Entidades de dominio, value objects, enums e regras que nao dependem de Spring |
| `infrastructure` | JPA, repositories concretos, entidades de persistencia e adapters |
| `shared` | Erros padronizados, configuracoes e objetos realmente compartilhados |

### Regras Arquiteturais Deste Lab

- `domain` nao deve depender de Spring, JPA, Hibernate ou HTTP.
- Controller nao deve acessar repository diretamente.
- Regra de negocio nao deve ficar no controller.
- DTO de request nao deve ser usado como entidade de dominio.
- Entidade JPA pode existir separada do dominio se a regra comecar a ficar poluida por persistencia.
- Use ports/interfaces somente quando houver ganho claro. Neste lab, nao force abstracao para tudo.

### Exemplo de Estrutura Para Pedido

```text
order
|-- api
|   |-- OrderController.java
|   |-- CreateOrderRequest.java
|   `-- OrderResponse.java
|-- application
|   |-- CreateOrderUseCase.java
|   |-- GetOrderUseCase.java
|   `-- ChangeOrderStatusUseCase.java
|-- domain
|   |-- Order.java
|   |-- OrderItem.java
|   |-- OrderStatus.java
|   `-- Money.java
`-- infrastructure
    `-- persistence
        |-- OrderJpaEntity.java
        |-- OrderJpaRepository.java
        `-- JpaOrderRepository.java
```

Se preferir simplificar no inicio, a entidade de dominio e a entidade JPA podem ser a mesma classe, desde que voce registre no README do projeto que essa foi uma decisao pragmatica para o lab. Se a classe comecar a ficar cheia de anotacoes e detalhes de banco, separe dominio e persistencia.

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
- Package by Feature com `api`, `application`, `domain` e `infrastructure`.
- Controllers, use cases, repositories/adapters e DTOs.
- Dominio sem dependencia de framework.
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
- Classes de dominio com regras principais.
- Use cases na camada `application`.
- Repositories/adapters na camada `infrastructure`.
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
2. Criar a estrutura package by feature: `customer`, `product`, `order` e `shared`.
3. Configurar PostgreSQL no Docker Compose.
4. Configurar Flyway.
5. Criar migration de clientes.
6. Implementar dominio de cliente sem depender de Spring.
7. Implementar use case `CreateCustomerUseCase`.
8. Implementar adapter de persistencia.
9. Implementar controller `POST /customers`.
10. Implementar tratamento de erro e validacao.
11. Repetir para produtos.
12. Criar dominio de pedido com regras de status e total.
13. Criar migrations de pedidos e itens.
14. Implementar criacao de pedido.
15. Implementar consulta de pedido.
16. Implementar alteracao de status.
17. Documentar como rodar e quais decisoes arquiteturais foram tomadas.

## Criterios de Pronto

O lab esta pronto quando:

- a API sobe com um comando;
- o PostgreSQL sobe via Docker Compose;
- as tabelas sao criadas por Flyway;
- todos os endpoints principais funcionam;
- entradas invalidas retornam erro claro;
- o total do pedido e calculado no backend;
- o projeto esta organizado por feature;
- regras principais ficam no `domain` ou no `application`, nao no controller;
- `domain` nao depende de Spring/JPA, salvo decisao pragmatica documentada;
- a camada `api` nao acessa repository diretamente;
- o README do codigo explica como rodar;
- o README do codigo explica a arquitetura usada;
- o Swagger mostra os endpoints.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- por que usar migrations;
- por que nao confiar no total enviado pelo cliente;
- diferenca entre DTO e entidade JPA;
- onde ficam regras de negocio;
- como voce tratou erros de validacao;
- como voce evitaria duplicidade de email;
- por que organizar por feature em vez de por tipo tecnico;
- qual papel de `api`, `application`, `domain` e `infrastructure`;
- quando vale separar entidade de dominio e entidade JPA.

## Como Levar Para a Fintech

Depois deste lab, implemente na fintech:

- `POST /api/v1/customers`;
- migration de cliente;
- validacoes de entrada;
- DTOs;
- tratamento global de erros;
- persistencia com PostgreSQL.
- estrutura modular por dominio;
- regra de dominio fora de controller;
- base para evoluir depois para outbox, mensageria e seguranca.
