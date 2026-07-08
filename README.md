# Labs de Estudos Backend

Este diretorio organiza labs pequenos para estudar backend Java, Spring Boot, Go e praticas de producao sem misturar tudo em um projeto grande desde o inicio.

A ideia e simples:

1. Aprender uma competencia por vez em um lab pequeno.
2. Validar que voce entendeu com uma entrega objetiva.
3. Levar o aprendizado para o projeto principal, como o `fintech-ecosystem`.

O erro que este roteiro evita e tentar aprender JWT, Kafka, testes, observabilidade, AWS, arquitetura financeira e Go ao mesmo tempo. Isso costuma gerar um projeto grande, mas raso. Aqui cada lab tem foco, escopo e criterio de conclusao.

## Ordem Recomendada

| Ordem | Lab | Foco |
|---|---|---|
| 1 | `01-api-foundation-lab` | Base REST com Spring Boot, PostgreSQL, Flyway e Docker Compose |
| 2 | `02-testing-lab` | JUnit, Mockito, MockMvc, Testcontainers e ArchUnit |
| 3 | `03-auth-jwt-lab` | Login, JWT, roles, refresh token e Spring Security |
| 4 | `04-messaging-lab` | Mensageria, eventos, retry, DLQ, idempotencia e outbox |
| 5 | `05-observability-lab` | Logs estruturados, correlation id, metricas, Prometheus e Grafana |
| 6 | `06-cicd-lab` | GitHub Actions, build, testes, Docker image e qualidade minima |
| 7 | `07-aws-cloud-lab` | IAM, VPC, ECS, RDS, S3, SQS e deploy controlado |
| 8 | `08-go-worker-lab` | Go para workers, concorrencia, filas, HTTP e observabilidade |

## Anotacoes

| Tema | Foco |
|---|---|
| [`notes/microservices`](notes/microservices) | Microsservicos, transacoes distribuidas, consistencia, eventos e padroes arquiteturais |

## Como Usar

Use um lab por vez. Nao abra todos ao mesmo tempo.

Para cada lab:

1. Leia o README inteiro.
2. Crie um repositorio ou pasta separada para o codigo daquele lab.
3. Implemente apenas o escopo proposto.
4. Marque os criterios de pronto.
5. Escreva no README do seu codigo o que voce aprendeu e quais decisoes tomou.
6. So depois leve a ideia para o projeto principal.

## Padrao de Entrega

Cada lab deve gerar, no minimo:

- README do projeto implementado.
- Docker Compose quando houver infraestrutura local.
- Testes coerentes com o foco do lab.
- Evidencia de execucao: comandos, prints ou logs importantes.
- Lista de decisoes tecnicas.
- Secao "O que eu explicaria em entrevista".

## Relacao Com a Fintech

Os labs nao substituem o `fintech-ecosystem`. Eles preparam voce para implementar melhor a fintech.

Exemplos:

- O `02-testing-lab` prepara os testes de `customer`, `account`, `ledger` e `payment`.
- O `03-auth-jwt-lab` prepara a seguranca real da API.
- O `04-messaging-lab` prepara `outbox`, eventos de pagamento e notificacoes.
- O `05-observability-lab` prepara logs e metricas de fluxos financeiros.
- O `07-aws-cloud-lab` prepara o deploy minimo da fintech.
- O `08-go-worker-lab` prepara um worker separado para consumo de eventos.

## Regra de Foco

Se voce estiver estudando mensageria, nao tente melhorar arquitetura, frontend, cloud, JWT e CI no mesmo ciclo.

Exemplo de foco correto:

- Lab atual: mensageria.
- Pode fazer: producer, consumer, retry, DLQ, idempotencia, outbox.
- Nao fazer agora: tela, AWS, OAuth2, dashboard Grafana, Kubernetes.

## Quando Integrar No Projeto Principal

Integre um conceito na fintech quando conseguir responder:

- Qual problema essa tecnologia resolve?
- Qual problema ela nao resolve?
- Como eu testo isso?
- O que acontece quando falha?
- Como eu observo isso rodando?
- O que eu faria diferente em producao?

Se voce ainda nao consegue responder essas perguntas, mantenha o estudo no lab.

## Ritmo Sugerido

Um bom ritmo para quem trabalha ou estuda em paralelo:

- Labs simples: 3 a 5 dias.
- Labs medios: 1 a 2 semanas.
- AWS: 1 a 2 semanas, com cuidado de custo.

Nao avance por ansiedade. Avance quando o criterio de pronto estiver cumprido.
