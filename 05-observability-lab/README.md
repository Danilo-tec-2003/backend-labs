# 05 - Observability Lab

## Objetivo

Aprender a observar uma aplicacao backend em execucao com logs estruturados, correlation id, metricas, health checks, Prometheus e Grafana.

Este lab existe para voce sair do nivel "a API funciona na minha maquina" e chegar no nivel "eu consigo diagnosticar comportamento em execucao".

## Projeto Recomendado

Nome sugerido: `order-observability-lab`

Dominio simples: API de pedidos com endpoints normais, endpoint lento e endpoint que falha propositalmente.

Voce deve conseguir responder:

- quantas requisicoes chegaram;
- quais endpoints estao mais lentos;
- quantos pedidos foram criados;
- quantos erros aconteceram;
- qual request gerou determinado log;
- se banco e mensageria estao saudaveis.

## Stack

| Area | Tecnologia |
|---|---|
| API | Spring Boot |
| Health e metricas | Spring Actuator |
| Metricas | Micrometer |
| Coleta | Prometheus |
| Dashboard | Grafana |
| Logs | JSON estruturado |
| Tracing opcional | OpenTelemetry com Jaeger ou Tempo |
| Infra local | Docker Compose |

## Conceitos Que Voce Deve Aprender

- Observabilidade vs monitoramento.
- Logs estruturados.
- Correlation id.
- Health check.
- Readiness e liveness.
- Metricas tecnicas.
- Metricas de negocio.
- Histogramas e percentis.
- Prometheus scrape.
- PromQL basico.
- Dashboard Grafana.
- Alertas basicos.
- Tracing distribuido, como extensao.

## Escopo Funcional

Crie uma API com endpoints:

- `POST /api/v1/orders`
- `GET /api/v1/orders/{id}`
- `GET /api/v1/simulations/slow`
- `GET /api/v1/simulations/error`

O endpoint lento deve demorar de forma controlada.

O endpoint de erro deve retornar `500` propositalmente para gerar evidencias.

## Logs Estruturados

Cada log relevante deve ter:

- timestamp;
- level;
- message;
- correlationId;
- method;
- path;
- status;
- durationMs;
- userId, quando houver;
- eventName, quando fizer sentido.

Exemplo de eventos a logar:

- request iniciada;
- request finalizada;
- pedido criado;
- erro inesperado;
- dependencia indisponivel.

## Correlation ID

Regras:

- se request vier com header `X-Correlation-ID`, reutilize;
- se nao vier, gere um novo;
- devolva o correlation id na resposta;
- inclua o correlation id em todos os logs da request.

## Metricas Obrigatorias

Use Actuator e Micrometer para expor:

- quantidade de requests;
- tempo de resposta por endpoint;
- quantidade de respostas por status;
- uso de JVM;
- conexoes de banco;
- health da aplicacao.

Crie metricas customizadas:

- `orders_created_total`;
- `orders_failed_total`;
- `order_processing_seconds`, se houver processamento;
- `outbox_pending_total`, se simular outbox.

## Prometheus

Configure:

- Prometheus no Docker Compose;
- scrape do endpoint `/actuator/prometheus`;
- intervalo de coleta;
- validacao de alvos ativos.

PromQL minimo para praticar:

- taxa de requests por minuto;
- percentil de latencia;
- quantidade de erros 5xx;
- pedidos criados por minuto.

## Grafana

Crie um dashboard com:

- requests por minuto;
- erros por minuto;
- latencia media;
- p95 de latencia;
- pedidos criados;
- status da aplicacao;
- uso de memoria JVM;
- conexoes de banco.

## Alertas Basicos

Crie pelo menos regras conceituais, mesmo que nao envie alerta real:

- erro 5xx acima de limite;
- latencia p95 alta;
- aplicacao down;
- banco down;
- outbox pendente crescendo.

## Passo a Passo Sugerido

1. Criar API minima.
2. Ativar Spring Actuator.
3. Expor `/actuator/health` e `/actuator/prometheus`.
4. Criar filtro de correlation id.
5. Configurar logs em JSON.
6. Criar metricas customizadas.
7. Subir Prometheus.
8. Validar scrape.
9. Subir Grafana.
10. Criar dashboard.
11. Gerar carga simples com `curl` ou ferramenta similar.
12. Documentar o que aparece em cada painel.

## Criterios de Pronto

O lab esta pronto quando:

- cada request tem correlation id;
- logs saem estruturados;
- Actuator expoe health;
- Prometheus coleta metricas;
- Grafana mostra dashboard util;
- erros aparecem em metricas e logs;
- endpoint lento aparece na latencia;
- voce sabe explicar diferenca entre log, metrica e trace.

## O Que Evitar

- Logar senha, token ou dado sensivel.
- Criar log sem contexto.
- Ter dashboard bonito mas inutil.
- Medir so CPU e memoria.
- Ignorar metrica de negocio.
- Usar correlation id apenas no controller.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- o que e correlation id;
- por que logs estruturados ajudam;
- como Prometheus coleta metricas;
- o que Grafana faz;
- diferenca entre health check e metrica;
- o que e p95 de latencia;
- quais metricas voce colocaria em pagamento;
- como investigaria um erro em producao.

## Como Levar Para a Fintech

Depois deste lab, aplique na fintech:

- correlation id em todas as requests;
- logs estruturados em fluxos financeiros;
- metricas de pagamentos aprovados e rejeitados;
- metrica de outbox pendente;
- health checks para banco e Kafka;
- dashboard do fluxo `payment`.

