# 02 - Testing Lab

## Objetivo

Aprender testes backend com JUnit, Mockito, MockMvc, Testcontainers e ArchUnit.

Este lab deve te ensinar a testar comportamento, nao apenas aumentar cobertura. O foco e saber quando usar teste unitario, teste de integracao, teste de API e teste arquitetural.

## Projeto Recomendado

Nome sugerido: `order-testing-lab`

Voce pode reaproveitar uma versao pequena do dominio de pedidos ou criar um dominio menor de transferencias.

Escopo recomendado: pedidos com regra de status e total.

## Stack

| Area | Tecnologia |
|---|---|
| Testes | JUnit |
| Assertivas | AssertJ |
| Mocks | Mockito |
| API | MockMvc |
| Integracao | Spring Boot Test |
| Banco real em teste | Testcontainers com PostgreSQL |
| Arquitetura | ArchUnit |

## Conceitos Que Voce Deve Aprender

- Teste unitario de dominio.
- Teste unitario de service com mock.
- Teste de controller com MockMvc.
- Teste de integracao com banco real.
- Massa de dados de teste.
- Testes de excecao.
- Testes parametrizados.
- Testcontainers.
- ArchUnit para proteger dependencias.
- Diferenca entre mock, stub e fake.

## Escopo Funcional

Implemente um mini dominio com:

- cliente;
- produto;
- pedido;
- item de pedido;
- status do pedido.

Regras para testar:

- pedido nao pode ser criado sem itens;
- produto com preco zero deve ser rejeitado;
- total do pedido deve ser soma dos itens;
- pedido cancelado nao pode ser pago;
- pedido pago nao pode ser cancelado, se essa for a regra escolhida;
- repository deve persistir e consultar corretamente;
- controller deve retornar `400` para request invalido;
- controller deve retornar `201` ao criar pedido.

## Tipos de Teste Obrigatorios

### 1. Teste Unitario de Dominio

Objetivo: testar regra pura, sem Spring.

Exemplos:

- `OrderTest`
- `MoneyTest`
- `OrderStatusTransitionTest`

Nao use:

- `@SpringBootTest`;
- banco;
- repository;
- controller.

### 2. Teste de Service Com Mockito

Objetivo: testar caso de uso isolado de infraestrutura.

Exemplos:

- mock de `CustomerRepository`;
- mock de `ProductRepository`;
- mock de `OrderRepository`.

Valide:

- chamadas esperadas;
- excecoes;
- regras de negocio;
- retorno do caso de uso.

### 3. Teste de API Com MockMvc

Objetivo: validar contrato HTTP.

Valide:

- status code;
- JSON de resposta;
- erro de validacao;
- headers importantes, se houver.

### 4. Teste de Integracao Com Testcontainers

Objetivo: validar JPA, Flyway e PostgreSQL real.

Valide:

- migrations sobem;
- entidade salva corretamente;
- constraint de email unico funciona;
- query customizada funciona.

### 5. Teste Arquitetural Com ArchUnit

Objetivo: impedir acoplamento ruim.

Regras sugeridas:

- `domain` nao depende de Spring;
- `domain` nao depende de JPA;
- `api` nao acessa repository diretamente;
- `infrastructure` pode depender de detalhes externos;
- `application` nao depende de controller.

## Passo a Passo Sugerido

1. Criar uma classe de dominio sem Spring.
2. Escrever testes unitarios antes de criar controller.
3. Criar service e testar com Mockito.
4. Criar controller e testar com MockMvc.
5. Configurar Testcontainers.
6. Testar repository com PostgreSQL real.
7. Criar teste ArchUnit.
8. Quebrar uma regra de proposito e confirmar que o teste falha.
9. Documentar a diferenca entre cada tipo de teste.

## Criterios de Pronto

O lab esta pronto quando:

- existem testes unitarios sem Spring;
- existem testes de service com Mockito;
- existem testes de API com MockMvc;
- existe teste com PostgreSQL via Testcontainers;
- existe ao menos um teste ArchUnit;
- `mvn test` executa tudo;
- voce sabe explicar por que cada teste existe.

## O Que Evitar

- Usar `@SpringBootTest` em todos os testes.
- Mockar tudo em teste de integracao.
- Testar getter e setter sem regra.
- Testar detalhe interno em vez de comportamento.
- Criar teste que passa mas nao protege nenhuma regra.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- diferenca entre teste unitario e integracao;
- quando usar Mockito;
- quando nao usar Mockito;
- por que Testcontainers e melhor que H2 para alguns casos;
- o que MockMvc testa;
- como ArchUnit ajuda em arquitetura;
- como lidar com testes lentos.

## Como Levar Para a Fintech

Depois deste lab, aplique na fintech:

- testes puros para `Money`;
- testes de dominio para regras de `ledger`;
- testes de service para `customer` e `account`;
- testes de API para `POST /customers`;
- Testcontainers para PostgreSQL;
- ArchUnit para proteger `domain` contra Spring/JPA.

