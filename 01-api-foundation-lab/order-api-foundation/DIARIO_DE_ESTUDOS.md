# Diario de Estudos - Order API Foundation

Este arquivo e o diario tecnico do projeto `order-api-foundation`.

Ele serve como guia de desenvolvimento para entender o que foi feito, por que foi feito dessa forma e como reaplicar a mesma organizacao em outros projetos Java/Spring Boot.

## Sumario

- [1. Objetivo do Projeto](#1-objetivo-do-projeto)
- [2. Stack Usada](#2-stack-usada)
- [3. Estrutura Geral da Arquitetura](#3-estrutura-geral-da-arquitetura)
- [4. Responsabilidade de Cada Camada](#4-responsabilidade-de-cada-camada)
- [5. Fluxo Atual do Modulo Customer](#5-fluxo-atual-do-modulo-customer)
- [6. Setup do Projeto](#6-setup-do-projeto)
- [7. Banco de Dados e Flyway](#7-banco-de-dados-e-flyway)
- [8. Dominio](#8-dominio)
- [9. Application](#9-application)
- [10. Infrastructure Persistence](#10-infrastructure-persistence)
- [11. Testes](#11-testes)
- [12. Perguntas e Respostas de Arquitetura](#12-perguntas-e-respostas-de-arquitetura)
- [13. Como Criar Uma Nova Feature Seguindo Este Padrao](#13-como-criar-uma-nova-feature-seguindo-este-padrao)
- [14. Checklist de Qualidade](#14-checklist-de-qualidade)
- [15. Comandos Uteis](#15-comandos-uteis)
- [16. Commits Feitos e Intencao](#16-commits-feitos-e-intencao)
- [17. Proximos Passos](#17-proximos-passos)

## 1. Objetivo do Projeto

O `order-api-foundation` e o primeiro lab do repositorio `backend-labs`.

O objetivo deste lab e construir uma API REST simples, mas usando uma organizacao profissional:

- Spring Boot;
- Java 21;
- PostgreSQL;
- Flyway;
- JPA;
- arquitetura modular orientada a dominio;
- separacao entre dominio, aplicacao e infraestrutura;
- testes unitarios.

Este projeto nao tenta ser grande. Ele tenta ser correto, claro e evolutivo.

O foco atual e o modulo `customer`.

O primeiro fluxo implementado foi:

```text
criar cliente
```

Ainda nao existe endpoint HTTP. O projeto ja possui dominio, caso de uso, migration e adapter JPA para persistencia.

## 2. Stack Usada

| Area | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.x |
| Build | Maven Wrapper |
| API | Spring Web |
| Persistencia | Spring Data JPA |
| Banco | PostgreSQL 16 |
| Migration | Flyway |
| Validacao futura da API | Bean Validation |
| Health/observabilidade basica | Spring Boot Actuator |
| Documentacao futura da API | Springdoc OpenAPI |
| Ambiente local | Docker Compose |
| Testes | JUnit 5 |
| SDK local | SDKMAN com `.sdkmanrc` |

## 3. Estrutura Geral da Arquitetura

Este projeto usa uma arquitetura modular orientada a dominio.

Na pratica, estamos combinando:

- Package by Feature;
- principios de DDD;
- ideias de Arquitetura Hexagonal;
- separacao entre camadas.

Estrutura atual do modulo `customer`:

```text
src/main/java/br/com/danilo/orderfoundation/customer
|-- api
|-- application
|   |-- CreateCustomerCommand.java
|   |-- CreateCustomerUseCase.java
|   `-- CustomerRepository.java
|-- domain
|   |-- Customer.java
|   |-- CustomerStatus.java
|   |-- DocumentNumber.java
|   `-- Email.java
`-- infrastructure
    `-- persistence
        |-- CustomerJpaEntity.java
        |-- JpaCustomerRepository.java
        `-- SpringDataCustomerRepository.java
```

Visao do fluxo entre camadas:

```text
api
 -> application
     -> domain
     -> port: CustomerRepository
         -> infrastructure adapter: JpaCustomerRepository
             -> SpringDataCustomerRepository
                 -> PostgreSQL
```

Regra central:

```text
camadas internas nao dependem de camadas externas
```

Ou seja:

- `domain` nao conhece Spring, JPA, HTTP ou banco;
- `application` nao conhece JPA, controller ou SQL;
- `infrastructure` conhece detalhes tecnicos;
- `api` conhece HTTP e DTOs.

## 4. Responsabilidade de Cada Camada

### `domain`

Responsavel pelas regras centrais do negocio.

No projeto atual:

```text
Customer
Email
DocumentNumber
CustomerStatus
```

O dominio deve responder perguntas como:

- o que e um cliente?
- quais dados sao obrigatorios?
- qual status inicial de um cliente?
- email invalido pode existir?
- documento invalido pode existir?

O dominio nao deve saber:

- se existe PostgreSQL;
- se existe JPA;
- se existe Spring;
- se existe endpoint REST;
- se existe JSON.

### `application`

Responsavel pelos casos de uso.

No projeto atual:

```text
CreateCustomerUseCase
CreateCustomerCommand
CustomerRepository
```

A camada de aplicacao orquestra o fluxo:

1. Recebe os dados de entrada.
2. Cria value objects.
3. Verifica duplicidade usando uma porta.
4. Cria a entidade de dominio.
5. Salva usando a porta.

Ela nao deve conhecer JPA, SQL ou controller.

### `infrastructure`

Responsavel pelos detalhes tecnicos.

No projeto atual:

```text
CustomerJpaEntity
SpringDataCustomerRepository
JpaCustomerRepository
```

Essa camada sabe:

- como mapear dados para tabela;
- como usar Spring Data JPA;
- como salvar no PostgreSQL;
- como converter entidade de dominio para entidade JPA.

### `api`

Ainda nao foi implementada.

No proximo ciclo, ela tera:

```text
CreateCustomerRequest
CustomerResponse
CustomerController
```

Essa camada sera responsavel por:

- receber HTTP;
- validar request com Bean Validation;
- transformar request em command;
- chamar use case;
- transformar dominio em response;
- retornar status HTTP correto.

## 5. Fluxo Atual do Modulo Customer

Fluxo logico do caso de uso:

```text
CreateCustomerCommand
    -> CreateCustomerUseCase
        -> Email.of(...)
        -> DocumentNumber.of(...)
        -> CustomerRepository.existsByEmail(...)
        -> CustomerRepository.existsByDocument(...)
        -> Customer.create(...)
        -> CustomerRepository.save(...)
            -> JpaCustomerRepository
                -> CustomerJpaEntity
                -> SpringDataCustomerRepository
                -> PostgreSQL
```

Ainda falta o inicio HTTP:

```text
POST /api/v1/customers
```

Quando a API for criada, o fluxo completo ficara:

```text
HTTP Request
    -> CustomerController
        -> CreateCustomerRequest
        -> CreateCustomerCommand
        -> CreateCustomerUseCase
        -> CustomerRepository
        -> PostgreSQL
        -> CustomerResponse
```

## 6. Setup do Projeto

### Java por projeto com SDKMAN

Como outros projetos podem usar Java 17, este projeto nao altera o Java global.

Foi criado o arquivo:

```text
.sdkmanrc
```

Conteudo:

```text
java=21.0.11-amzn
```

Sempre que entrar neste projeto, rode:

```bash
sdk env
```

Confirme:

```bash
java -version
./mvnw -v
```

O Maven precisa mostrar Java 21.

### Docker Compose

O arquivo `compose.yaml` sobe o PostgreSQL local:

```text
Host: localhost
Port: 5433
Database: order_api_foundation
User: order_user
Password: order_password
```

Comando:

```bash
docker compose up -d postgres
```

## 7. Banco de Dados e Flyway

Foi criada a migration:

```text
src/main/resources/db/migration/V1__create_customers_table.sql
```

Conteudo:

```sql
create table customers (
    id uuid primary key,
    name varchar(120) not null,
    email varchar(180) not null unique,
    document varchar(14) not null unique,
    status varchar(30) not null,
    created_at timestamp with time zone not null
);
```

Por que usar Flyway?

- versiona o banco;
- evita criar tabela manualmente;
- permite reproduzir o ambiente;
- registra quais migrations foram aplicadas;
- ajuda em deploy.

Tabela criada:

```text
customers
```

Tabela de controle do Flyway:

```text
flyway_schema_history
```

Para inspecionar:

```bash
docker exec -it order-api-foundation-postgres psql -U order_user -d order_api_foundation
```

Dentro do `psql`:

```sql
\d customers
select installed_rank, version, description, success
from flyway_schema_history;
```

## 8. Dominio

### `Email`

Arquivo:

```text
customer/domain/Email.java
```

Responsabilidade:

- representar email como value object;
- impedir email nulo ou vazio;
- normalizar para lowercase;
- validar formato basico;
- comparar por valor.

Exemplo:

```java
Email email = Email.of(" Danilo@GMAIL.com ");
email.value(); // "danilo@gmail.com"
```

Por que `Email` nao e apenas `String`?

Porque uma `String` qualquer pode ser invalida. Um `Email` ja nasce validado.

### `DocumentNumber`

Arquivo:

```text
customer/domain/DocumentNumber.java
```

Responsabilidade:

- representar documento como value object;
- remover caracteres que nao sejam numeros;
- aceitar 11 ou 14 digitos;
- comparar por valor.

Exemplo:

```java
DocumentNumber document = DocumentNumber.of("123.456.789-01");
document.value(); // "12345678901"
```

### `CustomerStatus`

Arquivo:

```text
customer/domain/CustomerStatus.java
```

Estados atuais:

```java
PENDING,
ACTIVE,
INACTIVE
```

O cliente nasce como:

```java
CustomerStatus.PENDING
```

### `Customer`

Arquivo:

```text
customer/domain/Customer.java
```

Responsabilidade:

- representar o cliente no negocio;
- manter identidade propria;
- proteger regras basicas;
- centralizar criacao e restauracao.

Campos:

```text
id
name
email
document
status
createdAt
```

Metodos importantes:

```java
Customer.create(...)
Customer.restore(...)
```

`create(...)` cria cliente novo:

```text
gera UUID
define status PENDING
define createdAt agora
```

`restore(...)` reconstroi cliente existente:

```text
preserva id
preserva status
preserva createdAt
```

`restore(...)` nao e update. Update futuro seria comportamento como:

```java
customer.changeName(...)
customer.activate()
customer.deactivate()
```

## 9. Application

### `CreateCustomerCommand`

Arquivo:

```text
customer/application/CreateCustomerCommand.java
```

Responsabilidade:

- carregar dados de entrada do caso de uso;
- nao depende de HTTP;
- nao depende de JPA.

Atual:

```java
public record CreateCustomerCommand(
        String name,
        String email,
        String document
) {
}
```

Por que nao usar `CreateCustomerRequest` aqui?

Porque request pertence a camada `api`. O command pertence a camada `application`.

### `CustomerRepository`

Arquivo:

```text
customer/application/CustomerRepository.java
```

Responsabilidade:

- definir a porta de persistencia que o use case precisa;
- esconder detalhes do banco;
- permitir trocar implementacao.

Contrato:

```java
public interface CustomerRepository {
    boolean existsByEmail(Email email);
    boolean existsByDocument(DocumentNumber document);
    Customer save(Customer customer);
}
```

Essa interface nao sabe se a implementacao usa:

- PostgreSQL;
- MongoDB;
- arquivo;
- memoria;
- API externa.

### `CreateCustomerUseCase`

Arquivo:

```text
customer/application/CreateCustomerUseCase.java
```

Responsabilidade:

- executar o fluxo de criacao de cliente;
- validar command nulo;
- criar value objects;
- verificar duplicidade;
- criar `Customer`;
- salvar usando `CustomerRepository`.

Fluxo:

```text
execute(command)
    -> Email.of(command.email)
    -> DocumentNumber.of(command.document)
    -> repository.existsByEmail(email)
    -> repository.existsByDocument(document)
    -> Customer.create(...)
    -> repository.save(customer)
```

Quem verifica duplicidade?

```text
CreateCustomerUseCase
```

Motivo: duplicidade depende de consulta externa. O dominio nao deve consultar banco.

## 10. Infrastructure Persistence

### `CustomerJpaEntity`

Arquivo:

```text
customer/infrastructure/persistence/CustomerJpaEntity.java
```

Responsabilidade:

- representar a tabela `customers`;
- usar anotacoes JPA;
- mapear colunas;
- servir ao Hibernate.

Ela nao representa o conceito de negocio. Ela representa persistencia.

Campos mapeados:

```text
id         -> uuid primary key
name       -> varchar(120)
email      -> varchar(180) unique
document   -> varchar(14) unique
status     -> varchar(30)
createdAt  -> created_at
```

Por que nao usar `@GeneratedValue`?

Porque o `id` e UUID gerado pelo dominio em `Customer.create(...)`.

`GenerationType.IDENTITY` seria adequado para ids numericos auto-incremento, nao para este caso.

### `SpringDataCustomerRepository`

Arquivo:

```text
customer/infrastructure/persistence/SpringDataCustomerRepository.java
```

Responsabilidade:

- usar Spring Data JPA;
- executar operacoes no banco;
- consultar por email;
- consultar por documento.

Atual:

```java
interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    boolean existsByEmail(String email);
    boolean existsByDocument(String document);
}
```

Por que nao e `public`?

Porque e detalhe interno do pacote de persistencia.

Quem deve ser usado pelo resto do sistema e:

```text
JpaCustomerRepository
```

### `JpaCustomerRepository`

Arquivo:

```text
customer/infrastructure/persistence/JpaCustomerRepository.java
```

Responsabilidade:

- implementar a porta `CustomerRepository`;
- usar Spring Data internamente;
- converter dominio para JPA;
- converter JPA para dominio.

Fluxo interno:

```text
Customer
    -> CustomerJpaEntity
        -> SpringDataCustomerRepository.save(...)
            -> CustomerJpaEntity salvo
                -> Customer.restore(...)
```

Esse adapter e o ponto onde a arquitetura se conecta ao banco real.

## 11. Testes

Testes atuais:

```text
src/test/java/br/com/danilo/orderfoundation/customer/domain/EmailTest.java
src/test/java/br/com/danilo/orderfoundation/customer/domain/DocumentNumberTest.java
src/test/java/br/com/danilo/orderfoundation/customer/domain/CustomerTest.java
src/test/java/br/com/danilo/orderfoundation/customer/application/CreateCustomerUseCaseTest.java
```

### Testes de dominio

Validam regras puras:

- email normalizado;
- email invalido rejeitado;
- documento normalizado;
- documento com tamanho invalido rejeitado;
- cliente criado com status `PENDING`;
- cliente sem nome/email/documento rejeitado.

Nao usam:

- Spring;
- banco;
- JPA;
- Docker.

### Teste de application

`CreateCustomerUseCaseTest` usa um fake manual:

```java
InMemoryCustomerRepository
```

Por que fake manual?

- ajuda a entender a porta;
- mostra o comportamento real do use case;
- evita usar Mockito cedo demais;
- nao depende de banco.

O fake simula o contrato:

```java
existsByEmail(...)
existsByDocument(...)
save(...)
```

## 12. Perguntas e Respostas de Arquitetura

### Qual e a diferenca entre `Customer.create(...)` e `Customer.restore(...)`?

`create(...)` cria um cliente novo e aplica valores iniciais.

`restore(...)` reconstroi um cliente que ja existia, preservando estado salvo.

Resumo:

```text
create  = novo objeto de negocio
restore = objeto existente vindo da persistencia
```

### `restore(...)` serve para update?

Nao.

`restore(...)` nao altera cliente. Ele apenas recria em memoria um cliente existente.

Update futuro deve ser representado por comportamento de dominio:

```java
changeName(...)
activate()
deactivate()
```

### Por que `Customer` nao tem `@Entity`?

Porque `Customer` pertence ao dominio.

`@Entity` pertence a infraestrutura JPA.

Se misturarmos os dois, o dominio passa a depender de banco/framework.

### Quem deve conhecer Spring Data JPA?

Somente `infrastructure`.

`application` conhece apenas a interface `CustomerRepository`.

### Por que `SpringDataCustomerRepository` ficou sem `public`?

Porque ele e detalhe interno de persistencia.

Ele nao deve ser usado por controller, use case ou dominio.

### Quem verifica email duplicado?

O use case.

O dominio nao acessa banco. O adapter apenas executa a consulta.

### Se trocar PostgreSQL por MongoDB, o que muda?

Mudam classes de infraestrutura:

```text
CustomerJpaEntity
SpringDataCustomerRepository
JpaCustomerRepository
```

Nao deveriam mudar:

```text
Customer
Email
DocumentNumber
CustomerRepository
CreateCustomerUseCase
CreateCustomerCommand
```

## 13. Como Criar Uma Nova Feature Seguindo Este Padrao

Use este roteiro para criar novos modulos.

Exemplo futuro:

```text
product
order
account
payment
```

### Passo 1: Comece pelo dominio

Crie:

```text
feature/domain
```

Defina:

- entidade principal;
- value objects;
- enums;
- regras que nao dependem de banco.

Evite:

- `@Entity`;
- `@Service`;
- `@Repository`;
- `@Autowired`;
- imports de Spring/JPA.

### Passo 2: Crie o caso de uso

Crie:

```text
feature/application
```

Arquivos comuns:

```text
CreateFeatureCommand
CreateFeatureUseCase
FeatureRepository
```

O use case deve orquestrar o fluxo.

### Passo 3: Crie testes de dominio e application

Antes de banco e controller, teste:

- regra de negocio;
- validacoes;
- duplicidade com fake manual;
- fluxo principal.

### Passo 4: Crie migration

Crie:

```text
src/main/resources/db/migration/Vx__description.sql
```

Exemplo:

```text
V2__create_products_table.sql
```

### Passo 5: Crie infrastructure persistence

Crie:

```text
feature/infrastructure/persistence
```

Arquivos comuns:

```text
FeatureJpaEntity
SpringDataFeatureRepository
JpaFeatureRepository
```

### Passo 6: Crie a API HTTP

Crie:

```text
feature/api
```

Arquivos comuns:

```text
CreateFeatureRequest
FeatureResponse
FeatureController
```

### Passo 7: Rode validacoes

```bash
sdk env
./mvnw test
docker compose up -d postgres
./mvnw spring-boot:run
```

## 14. Checklist de Qualidade

Antes de considerar uma feature pronta, confira:

- [ ] `domain` nao importa Spring/JPA.
- [ ] `application` nao conhece JPA ou controller.
- [ ] `infrastructure` implementa portas da aplicacao.
- [ ] `api` nao acessa repository diretamente.
- [ ] regras de negocio nao estao no controller.
- [ ] migration Flyway existe.
- [ ] nomes de migration nao duplicam versao.
- [ ] teste unitario de dominio existe.
- [ ] teste de application existe.
- [ ] `./mvnw test` passa.
- [ ] aplicacao sobe com PostgreSQL.
- [ ] commit tem escopo pequeno e claro.

## 15. Comandos Uteis

### Selecionar Java do projeto

```bash
sdk env
```

### Rodar testes

```bash
./mvnw test
```

### Subir PostgreSQL

```bash
docker compose up -d postgres
```

### Rodar aplicacao

```bash
./mvnw spring-boot:run
```

### Limpar build antigo

Use quando Flyway ou recursos antigos ficarem presos em `target/classes`.

```bash
./mvnw clean
```

### Conectar no banco

```bash
docker exec -it order-api-foundation-postgres psql -U order_user -d order_api_foundation
```

### Ver tabelas

```sql
\dt
```

### Ver estrutura da tabela customers

```sql
\d customers
```

### Ver migrations aplicadas

```sql
select installed_rank, version, description, success
from flyway_schema_history;
```

### Sair do psql

```sql
\q
```

## 16. Commits Feitos e Intencao

### `Add Spring Boot API foundation project`

Criou a base Spring Boot do lab.

### `Remove unused Lombok dependency`

Removeu Lombok porque nao estava sendo usado e nao era necessario para o objetivo do lab.

### `Add customer domain and create customer use case`

Criou:

- dominio de customer;
- value objects;
- command;
- use case;
- porta `CustomerRepository`;
- testes unitarios.

### `Add Java SDK config and customer table migration`

Criou:

- `.sdkmanrc` para usar Java 21 neste projeto;
- migration da tabela `customers`.

### `Add customer JPA persistence adapter`

Criou:

- `Customer.restore(...)`;
- `CustomerJpaEntity`;
- `SpringDataCustomerRepository`;
- `JpaCustomerRepository`.

Objetivo:

```text
conectar a porta CustomerRepository ao PostgreSQL via Spring Data JPA
```

## 17. Proximos Passos

Proximo ciclo recomendado:

```text
POST /api/v1/customers
```

Arquivos esperados:

```text
customer/api/CreateCustomerRequest.java
customer/api/CustomerResponse.java
customer/api/CustomerController.java
```

O controller deve:

1. receber request HTTP;
2. validar entrada;
3. montar `CreateCustomerCommand`;
4. chamar `CreateCustomerUseCase`;
5. retornar `201 Created`;
6. devolver `CustomerResponse`.

Depois disso, o projeto tera o fluxo completo:

```text
HTTP -> API -> Application -> Domain -> Infrastructure -> PostgreSQL
```

