# 07 - AWS Cloud Lab

## Objetivo

Aprender o basico de AWS aplicado a backend: IAM, VPC, ECS, RDS, S3, SQS, ECR e CloudWatch.

Este lab deve ser feito com cuidado porque cloud pode gerar custo. O objetivo nao e criar uma arquitetura perfeita, e sim entender como uma API containerizada roda em um ambiente real.

## Projeto Recomendado

Nome sugerido: `spring-aws-cloud-lab`

API pequena:

- endpoint de health;
- endpoint de pedidos;
- upload simples de arquivo no S3;
- envio de mensagem para SQS;
- persistencia em PostgreSQL RDS.

## Aviso de Custo

Antes de criar qualquer recurso:

1. Configure alerta de billing.
2. Defina budget baixo.
3. Anote tudo que sera criado.
4. Tenha checklist de destruicao.
5. Evite NAT Gateway no inicio, pois costuma gerar custo relevante.
6. Destrua recursos ao terminar o estudo.

Este lab pode ser iniciado com LocalStack para S3 e SQS antes de usar AWS real.

## Stack

| Area | Tecnologia |
|---|---|
| Container registry | Amazon ECR |
| Execucao da API | Amazon ECS com Fargate |
| Banco | Amazon RDS PostgreSQL |
| Arquivos | Amazon S3 |
| Fila | Amazon SQS |
| Permissoes | IAM |
| Rede | VPC, subnets, security groups |
| Logs | CloudWatch Logs |
| CI/CD opcional | GitHub Actions |

## Conceitos Que Voce Deve Aprender

- Regiao e Availability Zone.
- IAM user, role e policy.
- Principio do menor privilegio.
- VPC.
- Subnet publica e privada.
- Route table.
- Internet Gateway.
- Security Group.
- ECR repository.
- ECS cluster, task definition e service.
- RDS instance.
- S3 bucket.
- SQS queue.
- CloudWatch Logs.
- Variaveis de ambiente e secrets.

## Fase 1 - LocalStack Opcional

Antes da AWS real, pratique localmente:

- subir LocalStack;
- criar bucket S3;
- criar fila SQS;
- fazer upload de arquivo;
- enviar e consumir mensagem.

Objetivo: entender SDK e APIs sem custo.

## Fase 2 - Infra Minima AWS

Crie:

- VPC de estudo;
- duas subnets;
- security group da aplicacao;
- security group do banco;
- RDS PostgreSQL;
- bucket S3;
- fila SQS;
- repositorio ECR;
- cluster ECS;
- task definition;
- service ECS.

Para reduzir complexidade inicial, voce pode comecar com ECS Fargate em subnet publica com public IP. Depois estude arquitetura mais madura com ALB, subnets privadas e VPC endpoints.

## Escopo Funcional Da API

Endpoints:

- `GET /actuator/health`;
- `POST /api/v1/orders`;
- `GET /api/v1/orders/{id}`;
- `POST /api/v1/files`;
- `POST /api/v1/messages`;

Regras:

- pedido salva no RDS;
- arquivo vai para S3;
- mensagem vai para SQS;
- logs aparecem no CloudWatch.

## IAM

Crie roles separadas:

- role de execucao da task ECS;
- role da aplicacao para acessar S3 e SQS.

Permissoes minimas:

- S3 apenas no bucket do lab;
- SQS apenas na fila do lab;
- CloudWatch Logs apenas para log group necessario.

Nao use usuario root.

Nao coloque access key dentro do codigo.

## Rede

Voce deve saber explicar:

- por que a API precisa sair para internet ou acessar ECR;
- por que RDS nao deve ficar aberto para o mundo;
- como security group controla acesso;
- diferenca entre subnet publica e privada;
- como a aplicacao conversa com o banco.

## Checklist de Deploy

1. Buildar imagem Docker local.
2. Criar repositorio ECR.
3. Fazer push da imagem.
4. Criar RDS.
5. Criar S3.
6. Criar SQS.
7. Criar task definition ECS.
8. Criar service.
9. Validar logs no CloudWatch.
10. Testar endpoints.
11. Destruir recursos quando terminar.

## Observabilidade Minima

Valide:

- logs da aplicacao no CloudWatch;
- health check respondendo;
- erro de conexao com banco aparece claramente;
- falha de permissao S3/SQS aparece claramente.

## Criterios de Pronto

O lab esta pronto quando:

- imagem esta no ECR;
- API roda no ECS;
- API conecta no RDS;
- endpoint faz upload no S3;
- endpoint envia mensagem para SQS;
- logs aparecem no CloudWatch;
- security groups nao deixam RDS aberto para qualquer IP;
- voce consegue destruir todos os recursos criados.

## Checklist de Destruicao

Ao terminar:

- parar/deletar ECS service;
- deletar task definitions antigas, se quiser limpar;
- deletar cluster ECS;
- deletar imagem e repositorio ECR;
- deletar RDS;
- deletar snapshots manuais, se criados;
- esvaziar e deletar bucket S3;
- deletar fila SQS;
- deletar log groups;
- deletar security groups criados;
- deletar VPC, subnets e rotas, se criadas apenas para o lab.

## O Que Evitar

- Usar conta root.
- Abrir RDS para `0.0.0.0/0`.
- Colocar secrets no GitHub.
- Criar NAT Gateway sem necessidade.
- Esquecer recursos ligados.
- Misturar Kubernetes antes de entender ECS.
- Tentar fazer arquitetura enterprise no primeiro deploy.

## Perguntas Para Entrevista

Ao terminar, voce deve conseguir explicar:

- o que e IAM role;
- como ECS roda um container;
- diferenca entre ECR e ECS;
- por que usar RDS em vez de banco em EC2;
- para que serve S3;
- para que serve SQS;
- como security group protege o banco;
- como voce faria rollback;
- como voce investigaria erro de deploy.

## Como Levar Para a Fintech

Depois deste lab, aplique na fintech:

- Dockerfile confiavel;
- imagem no ECR;
- deploy minimo em ECS;
- banco no RDS;
- armazenamento de comprovantes no S3;
- fila SQS para fluxo assincrono simples;
- logs da aplicacao no CloudWatch.

