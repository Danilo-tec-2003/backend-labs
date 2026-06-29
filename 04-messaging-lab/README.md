# 04 - Messaging Lab

## Objetivo

Aprender mensageria de forma pratica: producer, consumer, fila/topico, retry, DLQ, idempotencia, eventos e outbox.

Este lab deve te dar seguranca para explicar por que uma operacao deve ser assincrona, o que acontece quando o consumidor falha e como evitar perda de eventos.

## Projeto Recomendado

Nome sugerido: `order-messaging-lab`

Dominio simples: processamento assincrono de pedidos.

Fluxo principal:

1. API recebe um pedido.
2. Pedido e salvo como `CREATED`.
3. Sistema publica evento `OrderCreated`.
4. Consumidor recebe evento.
5. Consumidor simula processamento de pagamento.
6. Pedido vira `CONFIRMED` ou `REJECTED`.
7. Falhas vao para retry e depois DLQ.

## Tecnologia Recomendada

Se voce nunca teve contato com mensageria:

1. Comece com RabbitMQ para entender filas, ack, retry e DLQ.
2. Depois replique o fluxo com Kafka para entender topicos, particoes, consumer group e offsets.

Como sua fintech mira Kafka, a segunda fase deste lab deve ser Kafka.

## Stack

| Area | Tecnologia |
|---|---|
| API | Spring Boot |
| Banco | PostgreSQL |
| Mensageria inicial | RabbitMQ |
| Mensageria avancada | Kafka |
| Infra local | Docker Compose |
| Testes | JUnit, Testcontainers |
| Padrao | Outbox Pattern |

## Conceitos Que Voce Deve Aprender

- Comunicacao sincrona vs assincrona.
- Producer e consumer.
- Fila vs topico.
- Ack e nack.
- Retry.
- Dead letter queue ou dead letter topic.
- Idempotencia no consumidor.
- Ordem de mensagens.
- Duplicidade de mensagens.
- Consumer group.
- Offset no Kafka.
- Outbox pattern.
- Evento de dominio vs evento de integracao.

## Escopo Funcional

### Criacao de Pedido

Endpoint:

- `POST /api/v1/orders`

Regras:

- salvar pedido com status `CREATED`;
- gravar evento `OrderCreated`;
- retornar `201 Created` sem esperar processamento final.

### Processamento Assincrono

Consumidor:

- recebe `OrderCreated`;
- busca pedido;
- simula regra de pagamento;
- atualiza status para `CONFIRMED` ou `REJECTED`;
- registra data do processamento.

### Falha Controlada

Crie um modo de simular falha:

- produto com nome `FAIL_ONCE` falha uma vez e depois passa;
- produto com nome `FAIL_ALWAYS` falha sempre e vai para DLQ.

### Idempotencia

Regras:

- se o mesmo evento for consumido duas vezes, o pedido nao pode ser processado duas vezes;
- registre `eventId`;
- mantenha tabela `processed_event`;
- consumidor deve ignorar evento ja processado.

### DLQ

Regras:

- mensagem com falha definitiva deve ir para DLQ;
- deve existir forma de consultar ou logar mensagens mortas;
- explique no README como reprocessaria uma DLQ em producao.

## Fase 1 - RabbitMQ

Implemente:

- exchange;
- queue principal;
- retry queue;
- dead letter queue;
- producer;
- consumer;
- ack manual, se possivel;
- logs claros de cada etapa.

Objetivo desta fase: entender o ciclo da mensagem.

## Fase 2 - Kafka

Implemente:

- topico `order.created`;
- topico `order.created.dlt`;
- producer Kafka;
- consumer Kafka;
- consumer group;
- tratamento de erro;
- controle de offset;
- teste com Testcontainers.

Objetivo desta fase: entender modelo de log distribuido, consumer groups e diferenca para fila tradicional.

## Fase 3 - Outbox

Implemente:

1. Criar pedido.
2. Gravar evento em tabela `outbox_event` na mesma transacao.
3. Publisher busca eventos pendentes.
4. Publisher publica na mensageria.
5. Publisher marca evento como publicado.

Campos sugeridos da outbox:

- `id`
- `aggregateType`
- `aggregateId`
- `eventType`
- `payload`
- `status`
- `createdAt`
- `publishedAt`
- `attempts`

## Passo a Passo Sugerido

1. Criar API minima de pedidos.
2. Subir RabbitMQ no Docker Compose.
3. Publicar evento direto apos criar pedido.
4. Criar consumidor.
5. Implementar retry.
6. Implementar DLQ.
7. Implementar idempotencia no consumidor.
8. Trocar publicacao direta por outbox.
9. Repetir fluxo com Kafka.
10. Criar teste de integracao com Testcontainers.
11. Documentar diferencas entre RabbitMQ e Kafka.

## Criterios de Pronto

O lab esta pronto quando:

- pedido e criado sem esperar consumidor;
- consumidor processa evento;
- falha temporaria faz retry;
- falha definitiva vai para DLQ;
- evento duplicado nao causa processamento duplicado;
- outbox evita perda de evento;
- existe teste de integracao da mensageria;
- voce sabe explicar RabbitMQ vs Kafka.

## O Que Evitar

- Publicar evento antes de salvar no banco.
- Assumir que mensagem nunca duplica.
- Nao tratar falha de consumidor.
- Fazer regra de negocio importante apenas no consumer sem consistencia.
- Usar mensageria para tudo.
- Misturar payload externo com entidade interna.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- por que usar mensageria;
- diferenca entre fila e topico;
- o que e DLQ;
- como funciona retry;
- por que consumidor precisa ser idempotente;
- qual problema o outbox resolve;
- o que acontece se o banco confirma e o Kafka cai;
- quando Kafka e melhor que RabbitMQ;
- quando RabbitMQ e suficiente.

## Como Levar Para a Fintech

Depois deste lab, aplique na fintech:

- evento `PaymentCreated`;
- tabela `outbox_event`;
- publisher para Kafka;
- consumer de notificacao;
- DLQ para eventos com falha;
- idempotencia no consumer;
- metricas de outbox pendente.

