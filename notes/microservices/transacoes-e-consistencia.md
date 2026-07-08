# Transacoes e consistencia

Estas anotacoes organizam os estudos iniciais sobre deadlock, atomicidade e rollback.

## 1. Deadlock

Deadlock e um problema que acontece quando dois ou mais processos ficam esperando uns aos outros liberarem recursos, e nenhum deles consegue continuar.

Em outras palavras: cada processo segura um recurso que o outro precisa. Como ninguem libera o que esta segurando, todos ficam bloqueados.

## Exemplo simples

Programa A:

1. Pega a impressora.
2. Agora precisa acessar o banco de dados.

Programa B:

1. Pega o banco de dados.
2. Agora precisa usar a impressora.

O problema fica assim:

```txt
Programa A
  -> segurou Impressora
  -> esperando Banco de Dados

Programa B
  -> segurou Banco de Dados
  -> esperando Impressora
```

Resultado:

- A espera B liberar o banco.
- B espera A liberar a impressora.
- Nenhum dos dois continua.
- Nenhum dos dois libera o recurso.

Isso e um deadlock.

## Exemplo do mundo real

Imagine duas pessoas em uma rua estreita, cada uma vindo de um lado com um carro.

- O carro A precisa que o carro B de re.
- O carro B precisa que o carro A de re.
- Nenhum dos dois se move.
- A rua fica travada.

Enquanto ninguem toma uma acao para liberar o caminho, os dois ficam parados.

## Exemplo tecnico

Em um sistema com banco de dados, duas transacoes podem tentar atualizar os mesmos dados em ordem diferente.

```txt
Transacao 1:
1. Bloqueia a tabela pedido.
2. Tenta atualizar a tabela pagamento.

Transacao 2:
1. Bloqueia a tabela pagamento.
2. Tenta atualizar a tabela pedido.
```

A transacao 1 espera a transacao 2 liberar `pagamento`.

A transacao 2 espera a transacao 1 liberar `pedido`.

Se o banco nao detectar e encerrar uma delas, as duas ficam travadas.

## Como tratar deadlocks

Existem algumas formas de lidar com deadlocks:

- Deteccao e recuperacao: o sistema detecta o deadlock e encerra uma das transacoes.
- Prevencao: o sistema evita que as condicoes para deadlock acontecam.
- Evitar deadlock: o sistema organiza a ordem de acesso aos recursos para reduzir o risco.

Uma pratica comum e sempre acessar recursos na mesma ordem.

Exemplo:

```txt
Sempre acessar:
1. Pedido
2. Pagamento
3. Estoque
```

Assim, as transacoes nao ficam pegando recursos em ordem diferente.

---

## 2. Atomicidade

Atomicidade significa que uma transacao deve ser executada por completo ou nao deve ser aplicada.

Ou tudo da certo, ou nada e salvo.

Se alguma etapa falhar, o sistema deve desfazer o que ja foi feito. Esse desfazer e chamado de `rollback`.

## Ideia principal

Uma transacao atomica nao pode ficar pela metade.

Ela precisa terminar em um destes estados:

```txt
COMMIT   -> deu tudo certo e as alteracoes foram salvas
ROLLBACK -> algo falhou e as alteracoes foram desfeitas
```

## Exemplo do mundo real

Imagine uma transferencia bancaria de R$ 100.

1. Tirar R$ 100 da conta A.
2. Adicionar R$ 100 na conta B.

Se o sistema tirar o dinheiro da conta A, mas falhar antes de adicionar na conta B, o dinheiro some.

Por isso a operacao precisa ser atomica:

- se as duas etapas funcionarem, salva tudo;
- se alguma etapa falhar, desfaz tudo.

## Exemplo tecnico

```sql
BEGIN;

UPDATE conta
SET saldo = saldo - 100
WHERE id = 1;

UPDATE conta
SET saldo = saldo + 100
WHERE id = 2;

COMMIT;
```

Se a segunda atualizacao falhar, o correto e executar:

```sql
ROLLBACK;
```

Assim, a primeira alteracao tambem e desfeita.

## Por que atomicidade e importante

Atomicidade e importante principalmente quando lidamos com:

- transacoes em varias etapas;
- atualizacoes em varias tabelas;
- regras de negocio que precisam manter dados sincronizados;
- sistemas distribuidos;
- operacoes financeiras;
- baixa tolerancia a inconsistencia.

## Atomicidade em Java com Spring

Em aplicacoes Java com Spring, e comum usar `@Transactional`.

Exemplo:

```java
@Transactional
public void transferir(Long contaOrigem, Long contaDestino, BigDecimal valor) {
    debitar(contaOrigem, valor);
    creditar(contaDestino, valor);
}
```

Se `debitar` funcionar, mas `creditar` falhar, o Spring pode fazer rollback da transacao.

Assim, o banco nao fica em um estado inconsistente.

## Design para falhas

Ao desenvolver sistemas, e melhor assumir que falhas podem acontecer.

Podem ocorrer falhas de:

- banco de dados;
- rede;
- validacao;
- servico externo;
- timeout;
- concorrencia.

Por isso, operacoes criticas devem ser protegidas com transacoes, tratamento de erro e logs adequados.

---

## 3. Rollback

Rollback e a acao de desfazer alteracoes feitas dentro de uma transacao.

Ele acontece quando alguma etapa falha e o sistema precisa voltar ao estado anterior.

## Exemplo do mundo real

Imagine uma compra online:

1. Criar pedido.
2. Dar baixa no estoque.
3. Processar pagamento.

Se o pagamento falhar, o sistema nao deve deixar o pedido confirmado nem o estoque reservado como se a compra tivesse dado certo.

## Exemplo tecnico

```txt
1. Criar pedido
2. Dar baixa no estoque
3. Registrar pagamento
```

Se o pagamento falhar, talvez seja necessario desfazer a baixa no estoque e cancelar o pedido.

Em bancos relacionais, isso pode ser feito dentro de uma transacao com `ROLLBACK`.

---

# Topicos para estudar depois

## 2-Phase Commit

Estudar como funciona o protocolo de confirmacao em duas fases para transacoes distribuidas.

## Saga Pattern

Estudar como coordenar operacoes distribuidas usando uma sequencia de acoes e compensacoes.

## ACID

Estudar as propriedades de transacoes em bancos de dados:

- Atomicidade
- Consistencia
- Isolamento
- Durabilidade

## Event Driven

Estudar sistemas baseados em eventos e comunicacao assincrona.

## Event-Driven Architecture

Estudar arquitetura orientada a eventos, produtores, consumidores, filas e mensageria.

## CQRS

Estudar separacao entre comandos de escrita e consultas de leitura.

## Trade-offs

Estudar decisoes tecnicas e seus custos, como simplicidade vs escalabilidade, consistencia vs disponibilidade e monolito vs microsservicos.

