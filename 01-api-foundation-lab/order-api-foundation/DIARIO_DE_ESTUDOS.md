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
- [11. API HTTP](#11-api-http)
- [12. Tratamento Global de Erros](#12-tratamento-global-de-erros)
- [13. Padrao de Mensagens](#13-padrao-de-mensagens)
- [14. Testes](#14-testes)
- [15. Perguntas e Respostas de Arquitetura](#15-perguntas-e-respostas-de-arquitetura)
- [16. Como Criar Uma Nova Feature Seguindo Este Padrao](#16-como-criar-uma-nova-feature-seguindo-este-padrao)
- [17. Checklist de Qualidade](#17-checklist-de-qualidade)
- [18. Comandos Uteis](#18-comandos-uteis)
- [19. Testando no Postman](#19-testando-no-postman)
- [20. Commits Feitos e Intencao](#20-commits-feitos-e-intencao)
- [21. Proximos Passos](#21-proximos-passos)

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

O projeto ja possui o fluxo basico completo:

```text
HTTP -> API -> Application -> Domain -> Infrastructure -> PostgreSQL
```

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
|   |-- CreateCustomerRequest.java
|   |-- CustomerController.java
|   `-- CustomerResponse.java
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

Tambem existe uma area compartilhada para erros da API:

```text
src/main/java/br/com/danilo/orderfoundation/shared/api/error
|-- ApiError.java
`-- GlobalExceptionHandler.java
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

Responsavel por expor o sistema para o mundo externo via HTTP.

No projeto atual:

```text
CreateCustomerRequest
CustomerResponse
CustomerController
```

Essa camada sera responsavel por:

- recebe HTTP;
- valida request com Bean Validation;
- transforma request em command;
- chama use case;
- transforma dominio em response;
- retorna status HTTP correto.

Ela nao deve acessar Spring Data repository diretamente.

## 5. Fluxo Atual do Modulo Customer

Fluxo completo atual:

```text
POST /api/v1/customers
    -> CustomerController
        -> CreateCustomerRequest
        -> CreateCustomerCommand
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
        -> CustomerResponse
```

Resumo por responsabilidade:

```text
Controller              = entrada HTTP
Request                 = dados recebidos da API
Command                 = dados de entrada do caso de uso
UseCase                 = orquestracao da regra
Domain                  = regras centrais e objetos validos
Repository Port         = contrato da aplicacao
JPA Adapter             = implementacao concreta da persistencia
Spring Data Repository  = acesso tecnico ao banco
Response                = dados devolvidos pela API
```
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

## 11. API HTTP

### `CreateCustomerRequest`

Arquivo:

```text
customer/api/CreateCustomerRequest.java
```

Responsabilidade:

- representar o JSON recebido pela API;
- validar campos com Bean Validation;
- ficar restrito a camada `api`.

Atual:

```java
public record CreateCustomerRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must have at most 120 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 180, message = "Email must have at most 180 characters")
        String email,

        @NotBlank(message = "Document is required")
        @Size(max = 20, message = "Document must have at most 20 characters")
        String document
) {
}
```

Por que existe request se ja existe command?

Porque eles pertencem a camadas diferentes.

```text
CreateCustomerRequest = contrato HTTP
CreateCustomerCommand = entrada do caso de uso
```

Se a API mudar, o command nao precisa mudar automaticamente.

### `CustomerResponse`

Arquivo:

```text
customer/api/CustomerResponse.java
```

Responsabilidade:

- representar o JSON devolvido pela API;
- esconder detalhes internos do dominio;
- controlar exatamente quais campos saem na resposta.

Ele possui um factory method:

```java
public static CustomerResponse from(Customer customer)
```

Esse metodo converte:

```text
Customer -> CustomerResponse
```

### `CustomerController`

Arquivo:

```text
customer/api/CustomerController.java
```

Responsabilidade:

- receber `POST /api/v1/customers`;
- aplicar validacao da request;
- criar `CreateCustomerCommand`;
- chamar `CreateCustomerUseCase`;
- retornar `CustomerResponse`;
- responder `201 Created` em caso de sucesso.

Fluxo do controller:

```text
request JSON
    -> CreateCustomerRequest
        -> CreateCustomerCommand
            -> CreateCustomerUseCase
                -> Customer
                    -> CustomerResponse
```

O controller nao deve:

- acessar repository diretamente;
- conter regra de negocio;
- converter para entidade JPA;
- executar SQL;
- decidir duplicidade de email/documento.

## 12. Tratamento Global de Erros

### `ApiError`

Arquivo:

```text
shared/api/error/ApiError.java
```

Responsabilidade:

- padronizar resposta de erro da API;
- evitar respostas diferentes para cada controller;
- facilitar consumo por clientes HTTP.

Formato:

```java
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        List<String> messages,
        String path
) {
}
```

Exemplo:

```json
{
  "timestamp": "2026-07-06T20:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "messages": [
    "Customer email already exists."
  ],
  "path": "/api/v1/customers"
}
```

### `GlobalExceptionHandler`

Arquivo:

```text
shared/api/error/GlobalExceptionHandler.java
```

Responsabilidade:

- capturar excecoes comuns;
- transformar excecoes em resposta HTTP padronizada;
- evitar `try/catch` repetido nos controllers.

Tratamentos atuais:

```text
IllegalArgumentException        -> 400 Bad Request
MethodArgumentNotValidException -> 400 Bad Request
```

`IllegalArgumentException` cobre erros de dominio/application, como:

```text
Customer email already exists.
Document number must have 11 or 14 digits.
```

`MethodArgumentNotValidException` cobre erros de Bean Validation, como:

```text
Name is required
Email is invalid
```

## 13. Padrao de Mensagens

As mensagens da aplicacao foram padronizadas em ingles.

Motivos:

- manter consistencia tecnica;
- facilitar leitura em logs;
- seguir padrao comum em APIs;
- evitar mistura de portugues e ingles no codigo.

Exemplos:

```text
Email is required.
Email is invalid.
Document number is required.
Document number must have 11 or 14 digits.
Create customer command is required.
Customer email already exists.
Customer document already exists.
```

As mensagens de Bean Validation tambem foram explicitadas em ingles:

```java
@NotBlank(message = "Name is required")
@Email(message = "Email is invalid")
```

Regra para os proximos modulos:

```text
nomes de classes, metodos, testes e mensagens tecnicas devem ficar em ingles
```

## 14. Testes

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

## 15. Perguntas e Respostas de Arquitetura

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

## 16. Como Criar Uma Nova Feature Seguindo Este Padrao

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

## 17. Checklist de Qualidade

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

## 18. Comandos Uteis

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

## 19. Testando no Postman

Com a aplicacao rodando, crie uma request no Postman.

Metodo:

```text
POST
```

URL:

```text
http://localhost:8080/api/v1/customers
```

Headers:

```text
Content-Type: application/json
```

Body:

```json
{
  "name": "Danilo Mendes",
  "email": "danilo.customer@example.com",
  "document": "123.456.789-01"
}
```

Resposta esperada:

```text
201 Created
```

Exemplo de response:

```json
{
  "id": "uuid-gerado",
  "name": "Danilo Mendes",
  "email": "danilo.customer@example.com",
  "document": "12345678901",
  "status": "PENDING",
  "createdAt": "2026-07-06T20:00:00Z"
}
```

Se enviar a mesma request novamente, deve retornar:

```text
400 Bad Request
```

Exemplo:

```json
{
  "timestamp": "2026-07-06T20:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "messages": [
    "Customer email already exists."
  ],
  "path": "/api/v1/customers"
}
```

Teste de validacao:

```json
{
  "name": "",
  "email": "invalid-email",
  "document": ""
}
```

Resultado esperado:

```text
400 Bad Request
```

Com mensagens vindas do Bean Validation:

```text
Name is required
Email is invalid
Document is required
```

## 20. Commits Feitos e Intencao

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

### `Add customer API endpoint`

Criou:

- `CreateCustomerRequest`;
- `CustomerResponse`;
- `CustomerController`;
- `ApiError`;
- `GlobalExceptionHandler`;
- anotacao `@Service` em `CreateCustomerUseCase`.

Objetivo:

```text
expor POST /api/v1/customers e padronizar respostas de erro
```

### `Standardize customer messages in English`

Padronizou:

- mensagens de dominio;
- mensagens de use case;
- mensagens de Bean Validation;
- nomes de testes que ainda estavam em portugues.

Objetivo:

```text
manter API, codigo, testes e logs tecnicos consistentes em ingles
```

## 21. Proximos Passos

O fluxo basico de `customer` esta praticamente fechado:

```text
HTTP -> API -> Application -> Domain -> Infrastructure -> PostgreSQL
```

Proximos passos recomendados:

- criar teste de controller com MockMvc;
- criar teste de integracao do adapter JPA;
- criar endpoint `GET /api/v1/customers/{id}`;
- criar endpoint `GET /api/v1/customers`;
- melhorar erros de duplicidade com excecoes especificas;
- adicionar location header no `POST /api/v1/customers`;
- atualizar Swagger/OpenAPI com exemplos.

Proximo ciclo mais recomendado:

```text
CustomerControllerTest com MockMvc
```
