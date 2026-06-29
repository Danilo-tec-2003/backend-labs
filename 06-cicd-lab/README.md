# 06 - CI/CD Lab

## Objetivo

Aprender a automatizar validacao de codigo com GitHub Actions.

Este lab deve garantir que cada push execute testes, build e verificacoes basicas. O foco inicial e CI. CD entra como extensao controlada.

## Projeto Recomendado

Nome sugerido: `spring-cicd-lab`

Use uma API pequena em Spring Boot com alguns testes. Pode ser a API do `01-api-foundation-lab` reduzida.

## Stack

| Area | Tecnologia |
|---|---|
| CI/CD | GitHub Actions |
| Build Java | Maven ou Gradle |
| Testes | JUnit |
| Container | Docker |
| Registry opcional | GitHub Container Registry |
| Qualidade opcional | Checkstyle, Spotless ou Sonar |

## Conceitos Que Voce Deve Aprender

- Workflow.
- Evento de trigger.
- Job.
- Step.
- Runner.
- Cache de dependencias.
- Secrets.
- Variables.
- Artifacts.
- Status check.
- Matrix build.
- Permissoes minimas.
- Separacao entre CI e CD.

## Escopo Principal

Criar pipeline que rode em:

- `push` para `main`;
- `pull_request` para `main`;
- execucao manual com `workflow_dispatch`.

Pipeline deve:

1. Baixar codigo.
2. Configurar Java.
3. Restaurar cache Maven ou Gradle.
4. Rodar testes.
5. Gerar build.
6. Buildar imagem Docker.
7. Publicar artifact ou imagem, se configurado.

## Jobs Recomendados

### Job 1 - Test

Responsavel por:

- compilar;
- rodar testes unitarios;
- rodar testes de integracao, se forem leves;
- publicar relatorio de teste como artifact.

### Job 2 - Docker Build

Responsavel por:

- buildar imagem Docker;
- validar que a imagem e criada;
- opcionalmente publicar no GitHub Container Registry.

### Job 3 - Quality

Opcional no inicio.

Responsavel por:

- format check;
- static analysis;
- dependencia vulneravel, se usar ferramenta adequada.

## Escopo Tecnico

Crie:

- `.github/workflows/ci.yml`;
- Dockerfile;
- README explicando status do pipeline;
- badge do workflow, se quiser;
- arquivo `.dockerignore`;
- comandos locais equivalentes aos do pipeline.

## Testcontainers No CI

Se usar Testcontainers:

- valide que o runner suporta Docker;
- separe testes lentos, se necessario;
- nao dependa de servico local fora do workflow;
- prefira o proprio Testcontainers ou service containers do GitHub Actions.

## Secrets

Para este lab, use secrets apenas se publicar imagem.

Nunca coloque no YAML:

- senha;
- token pessoal;
- access key;
- string de conexao sensivel.

## CD Opcional

Depois do CI funcionar, crie uma extensao de CD:

- publicar imagem no GitHub Container Registry;
- fazer deploy em ambiente local simulado;
- ou preparar deploy para ECS em outro lab.

Nao misture CD em AWS antes de terminar o `07-aws-cloud-lab`.

## Passo a Passo Sugerido

1. Criar API minima com teste.
2. Criar Dockerfile.
3. Rodar build local.
4. Criar workflow `ci.yml`.
5. Rodar testes no GitHub Actions.
6. Corrigir erros de ambiente.
7. Adicionar cache.
8. Adicionar build Docker.
9. Adicionar artifact ou imagem.
10. Documentar fluxo.

## Criterios de Pronto

O lab esta pronto quando:

- pull request dispara pipeline;
- push na main dispara pipeline;
- pipeline falha se teste falhar;
- pipeline passa com build correto;
- imagem Docker e criada;
- README mostra como rodar localmente os mesmos comandos;
- voce sabe explicar cada step do YAML.

## O Que Evitar

- Pipeline que so roda build e nao roda teste.
- Secrets hardcoded.
- Workflow com permissao ampla sem necessidade.
- Pipeline que depende da sua maquina.
- Ignorar falhas intermitentes.
- Fazer deploy automatico antes de ter CI confiavel.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- diferenca entre CI e CD;
- o que e um runner;
- por que usar cache;
- onde ficam secrets;
- como impedir merge com teste falhando;
- como buildar imagem Docker no pipeline;
- como separar teste rapido de teste lento.

## Como Levar Para a Fintech

Depois deste lab, aplique na fintech:

- GitHub Actions rodando `mvn test`;
- build da aplicacao;
- validacao de Dockerfile;
- execucao de testes de integracao;
- badge de status no README;
- branch protection, se o repositorio estiver no GitHub.

