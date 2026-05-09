# TP1 - Simulador de Escalonamento EDF

Projeto desenvolvido para a disciplina de Sistemas Operacionais.

## Objetivo

Simular a execução dinâmica de processos utilizando:

- Linguagem assembly hipotética
- Escalonamento EDF (Earliest Deadline First)
- Troca de contexto
- Estados de processos
- Chamadas de sistema

---

# Integrantes

- Jullia Ross
- Guilehrme Messer
- Matheus Zanchetta
- Lucas Vinhatti

---

# Tecnologias

- Java 21
- Maven
- Git/GitHub

---

# Estrutura do Projeto

parser/ → leitura dos programas assembly

cpu/ → execução das instruções

scheduler/ → escalonamento EDF

model/ → entidades do sistema

ui/ → logs e visualização

---

# Como executar

## Clonar o projeto

git clone URL

## Compilar

mvn clean install

## Executar

mvn exec:java

---

# Formato dos programas

Exemplo:

.code
LOAD x
SUB #1
SYSCALL 1
BRPOS loop
SYSCALL 0
.endcode

.data
x 3
.enddata

---

# Estados do Processo

- READY
- RUNNING
- BLOCKED
- FINISHED

---

# Política de Escalonamento

O projeto utiliza EDF (Earliest Deadline First).

A tarefa com menor deadline absoluto possui maior prioridade.

---

# Divisão de Responsabilidades

## Parser
Responsável: Jullia Ross

## CPU
Responsável: Guilherme Messer

## Scheduler EDF
Responsável: Fulano

## Interface / Logs
Responsável: Fulano