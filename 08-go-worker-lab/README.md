# 08 - Go Worker Lab

## Objetivo

Aprender Go aplicado a backend e sistemas assincronos.

Este lab complementa seu foco em Java/Spring. A ideia e construir um worker pequeno em Go que consome mensagens, processa trabalho em concorrencia controlada e expoe health e metricas.

## Projeto Recomendado

Nome sugerido: `go-order-worker-lab`

Fluxo:

1. Uma fila recebe eventos `OrderCreated`.
2. Worker Go consome eventos.
3. Worker processa cada pedido.
4. Worker registra sucesso ou falha.
5. Worker expoe health check e metricas.

Voce pode produzir mensagens com:

- um script simples;
- a API Java do messaging lab;
- ou endpoint HTTP no proprio worker apenas para estudo.

## Stack

| Area | Tecnologia |
|---|---|
| Linguagem | Go |
| Modulos | Go Modules |
| HTTP | `net/http` ou Chi |
| Concorrencia | Goroutines, channels, context |
| Fila local | RabbitMQ, Kafka ou SQS com LocalStack |
| Logs | Zap, Zerolog ou log/slog |
| Metricas | Prometheus client |
| Testes | `testing`, `httptest` |
| Container | Docker |

## Conceitos Que Voce Deve Aprender

- Estrutura de projeto Go.
- `go mod`.
- Interfaces pequenas.
- `context.Context`.
- Goroutines.
- Channels.
- Worker pool.
- Graceful shutdown.
- Timeouts.
- Retries.
- Idempotencia.
- Logs estruturados.
- Health endpoint.
- Metricas Prometheus.
- Testes com `testing`.

## Escopo Funcional

### Worker

O worker deve:

- conectar em uma fila;
- consumir eventos `OrderCreated`;
- processar eventos com limite de concorrencia;
- simular processamento externo;
- registrar sucesso;
- tratar falha temporaria;
- tratar falha definitiva;
- encerrar com graceful shutdown.

### Health

Endpoint:

- `GET /health`

Resposta:

- status do worker;
- status da conexao com fila;
- quantidade de workers ativos, se fizer sentido.

### Metricas

Endpoint:

- `GET /metrics`

Metricas sugeridas:

- `messages_consumed_total`;
- `messages_processed_total`;
- `messages_failed_total`;
- `message_processing_seconds`;
- `worker_active_jobs`.

## Modelo de Evento

Evento sugerido:

```json
{
  "eventId": "uuid",
  "eventType": "OrderCreated",
  "occurredAt": "2026-01-01T10:00:00Z",
  "orderId": "uuid",
  "customerId": "uuid",
  "amount": "100.00"
}
```

## Regras

- `eventId` deve ser usado para idempotencia.
- Evento invalido deve ser rejeitado de forma controlada.
- Falha temporaria deve permitir retry.
- Falha definitiva deve ir para DLQ ou ser registrada.
- O worker deve parar sem perder mensagem em processamento, dentro do possivel.

## Arquitetura Sugerida

Pacotes:

- `cmd/worker`: entrada da aplicacao;
- `internal/config`: configuracao;
- `internal/queue`: cliente da fila;
- `internal/processor`: regra de processamento;
- `internal/observability`: logs, metricas e health;
- `internal/idempotency`: controle de eventos processados.

Como este lab pede apenas README, use essa estrutura quando for implementar o codigo.

## Passo a Passo Sugerido

1. Criar modulo Go.
2. Criar parsing de configuracao por env vars.
3. Criar servidor HTTP com `/health`.
4. Subir fila local.
5. Criar consumidor simples.
6. Adicionar worker pool.
7. Adicionar `context` e graceful shutdown.
8. Adicionar logs estruturados.
9. Adicionar metricas Prometheus.
10. Adicionar idempotencia.
11. Adicionar testes unitarios.
12. Dockerizar o worker.

## Testes Obrigatorios

Crie testes para:

- parse de evento valido;
- rejeicao de evento invalido;
- processamento com sucesso;
- falha temporaria;
- falha definitiva;
- idempotencia;
- endpoint `/health`.

## Criterios de Pronto

O lab esta pronto quando:

- worker consome mensagens;
- processamento usa concorrencia controlada;
- graceful shutdown funciona;
- evento duplicado nao e processado duas vezes;
- logs tem contexto;
- metricas aparecem em `/metrics`;
- testes cobrem regra principal;
- worker roda em Docker.

## O Que Evitar

- Criar goroutine sem controle.
- Ignorar `context`.
- Processar mensagens sem timeout.
- Assumir que mensagem nunca duplica.
- Esconder erro em log generico.
- Misturar configuracao, fila e regra no `main.go`.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- por que usar Go para worker;
- como goroutines diferem de threads tradicionais;
- para que serve `context`;
- como limitar concorrencia;
- como fazer graceful shutdown;
- como tratar retry;
- como expor metricas;
- como testar codigo Go.

## Como Levar Para a Fintech

Depois deste lab, voce pode criar um worker Go separado para:

- consumir eventos de pagamento;
- processar notificacoes;
- gerar relatorios assincronos;
- consumir SQS;
- expor metricas proprias;
- rodar como container separado no ECS.

