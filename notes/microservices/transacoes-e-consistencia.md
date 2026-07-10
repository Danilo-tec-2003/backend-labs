# Transacoes e consistencia

Estas anotacoes organizam os estudos iniciais sobre deadlock, atomicidade, rollback, transacoes distribuidas e consistencia.

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

## 4. Two-Phase Commit

Two-Phase Commit, ou 2PC, e um protocolo de transacao distribuida usado para tentar garantir atomicidade em mais de um sistema.

A ideia principal e: ou todos os participantes confirmam a transacao, ou todos desfazem.

Ele funciona em duas fases.

## Fase 1: preparacao ou votacao

O coordenador da transacao pergunta para cada participante se ele consegue fazer o commit.

Cada participante executa sua parte localmente, mas ainda nao finaliza. Depois responde:

- `SIM`: estou pronto para confirmar;
- `NAO`: nao consigo confirmar, precisa abortar.

Enquanto isso, os recursos podem ficar bloqueados.

## Fase 2: compromisso ou decisao

Se todos responderem `SIM`, o coordenador manda todos fazerem `COMMIT`.

Se algum responder `NAO`, o coordenador manda todos fazerem `ROLLBACK`.

Isso tenta garantir que a operacao nao fique pela metade.

## Exemplo simples

```txt
Coordenador
  -> Pedido: pode confirmar?
  -> Estoque: pode confirmar?
  -> Pagamento: pode confirmar?

Se todos responderem SIM:
  -> COMMIT em todos

Se algum responder NAO:
  -> ROLLBACK em todos
```

Exemplo do mundo real:

Uma compra online pode depender de pedido, estoque e pagamento. Se o pagamento falhar, o pedido e a reserva de estoque tambem precisam ser desfeitos.

## Observacao sobre microsservicos

2PC nao costuma ser a melhor escolha para microsservicos de alto volume, porque pode bloquear recursos e criar acoplamento forte entre servicos.

Por isso, em muitos cenarios, outras abordagens sao preferidas:

- Saga Pattern;
- Outbox Pattern;
- Event-Driven Architecture;
- mensageria com Kafka ou RabbitMQ.

---

## 5. Saga Pattern

Saga Pattern e um padrao usado para controlar uma operacao distribuida sem depender de uma unica transacao gigante.

Em vez de fazer tudo dentro de uma transacao global, a Saga divide o processo em pequenas transacoes locais.

Juntas, essas transacoes locais formam uma operacao maior.

## Diferenca para Two-Phase Commit

No Two-Phase Commit, se um servico falhar, o coordenador tenta fazer rollback em todos os participantes.

No Saga Pattern, cada servico faz sua propria transacao local. Se algo der errado depois, o sistema executa uma transacao compensatoria.

Transacao compensatoria e uma acao que desfaz ou compensa algo que ja foi feito.

Exemplo:

```txt
1. Reserva voo
2. Reserva hotel
3. Reserva carro

Se o hotel falhar:
  -> cancelar voo
  -> nao reservar carro
```

O objetivo e evitar que um servico fique bloqueando diretamente outro servico.

Isso combina melhor com microsservicos, porque cada servico continua tendo sua propria responsabilidade.

## Formas de implementar Saga

Existem duas formas principais:

1. Orquestracao
2. Coreografia

## Orquestracao

Na orquestracao, existe um servico central chamado orquestrador.

Ele sabe a ordem do processo e chama cada servico conforme a regra de negocio.

Exemplo com pacote de viagem:

```txt
Cliente comprou pacote
Orquestrador solicita voo
Orquestrador solicita hotel
Orquestrador solicita carro

Voo: OK
Hotel: erro
Carro: nao solicitado

Orquestrador solicita cancelamento do voo
```

Nesse modelo, o orquestrador controla o fluxo e registra o que aconteceu em cada etapa.

## Coreografia

Na coreografia, nao existe um servico central controlando tudo.

A ideia e usar eventos e mensageria. Cada servico reage aos eventos que recebe e publica novos eventos quando termina sua parte.

Exemplo:

```txt
Pedido criado
  -> Servico de voo escuta o evento e reserva voo

Voo reservado
  -> Servico de hotel escuta o evento e tenta reservar hotel

Hotel falhou
  -> Servico de voo escuta o evento e cancela o voo
```

Nesse modelo, os servicos conversam indiretamente por eventos.

Isso reduz acoplamento direto, mas pode deixar o fluxo mais dificil de rastrear se nao houver bons logs, rastreamento e observabilidade.

---

## 6. ACID

ACID e um conjunto de propriedades que ajudam a garantir que transacoes em bancos relacionais sejam executadas com confiabilidade.

A ideia e manter os dados integros mesmo quando acontecem falhas, concorrencia ou regras de negocio complexas.

```txt
A -> Atomicity   -> Atomicidade
C -> Consistency -> Consistencia
I -> Isolation   -> Isolamento
D -> Durability  -> Durabilidade
```

## Atomicidade

Atomicidade significa que uma transacao e indivisivel.

Ou ela executa por completo, ou nenhuma alteracao deve ser aplicada.

Se alguma etapa falhar, o banco desfaz o que ja foi feito usando `ROLLBACK`.

Exemplo:

```txt
Transferencia bancaria:
1. Debitar R$ 100 da Conta A
2. Creditar R$ 100 na Conta B
```

Se o sistema debitar a Conta A, mas falhar antes de creditar a Conta B, a transacao inteira precisa ser cancelada.

Resumo:

```txt
Tudo ou nada.
```

## Consistencia

Consistencia significa que uma transacao deve levar o banco de um estado valido para outro estado valido.

Ela precisa respeitar as regras definidas no banco e na aplicacao.

Exemplos de regras:

- `NOT NULL`;
- `PRIMARY KEY`;
- `FOREIGN KEY`;
- `UNIQUE`;
- `CHECK`;
- triggers;
- regras de negocio da aplicacao.

Exemplos de problemas que nao devem ser permitidos:

- estoque negativo;
- idade menor que zero;
- CPF duplicado;
- pedido ligado a um cliente inexistente.

Se alguma regra for violada, a transacao deve ser cancelada.

Resumo:

```txt
Nenhuma regra de integridade deve ser quebrada.
```

## Isolamento

Isolamento significa que transacoes executadas ao mesmo tempo nao devem interferir incorretamente umas nas outras.

Cada transacao deve se comportar como se estivesse trabalhando sobre um estado consistente dos dados.

Exemplo:

```txt
Saldo atual: R$ 1.000

Usuario A faz saque de R$ 300
Usuario B consulta o saldo ao mesmo tempo
```

Sem isolamento, o Usuario B poderia ver um saldo temporario de R$ 700 antes do saque ser confirmado.

Se o saque falhar e sofrer `ROLLBACK`, o Usuario B teria visto uma informacao que nunca existiu oficialmente.

Problemas que o isolamento ajuda a evitar:

- Dirty Read: ler dados ainda nao confirmados;
- Non-Repeatable Read: a mesma consulta retornar valores diferentes dentro da mesma transacao;
- Phantom Read: novas linhas aparecerem entre duas consultas iguais;
- Lost Update: uma atualizacao sobrescrever outra sem perceber.

Resumo:

```txt
Transacoes concorrentes nao devem causar inconsistencias umas nas outras.
```

## Durabilidade

Durabilidade significa que, depois do `COMMIT`, os dados confirmados nao devem ser perdidos.

Mesmo que aconteca falha no sistema, o banco precisa manter o que ja foi confirmado.

Exemplos de falhas:

- queda de energia;
- reinicializacao do servidor;
- falha do sistema;
- travamento da aplicacao.

Mecanismos que ajudam nisso:

- Write-Ahead Log (WAL);
- Transaction Log;
- Redo Log;
- replicacao;
- persistencia em disco.

Exemplo:

```txt
Voce faz um PIX e recebe a confirmacao.

Mesmo que o servidor desligue depois disso,
a transferencia deve continuar registrada.
```

Resumo:

```txt
Depois do COMMIT, os dados nao devem ser perdidos.
```

---

# Topicos para estudar depois

## Event Driven

Estudar sistemas baseados em eventos e comunicacao assincrona.

## Event-Driven Architecture

Estudar arquitetura orientada a eventos, produtores, consumidores, filas e mensageria.

## CQRS

Estudar separacao entre comandos de escrita e consultas de leitura.

## Trade-offs

Estudar decisoes tecnicas e seus custos, como simplicidade vs escalabilidade, consistencia vs disponibilidade e monolito vs microsservicos.
