# Projeto JackCompiler - Analisador Léxico

Este repositório é dedicado ao desenvolvimento de um compilador para a linguagem *Jack*, conforme proposto na disciplina de Compiladores da UFMA. O objetivo é traduzir o código-fonte Jack para a linguagem de Máquina Virtual (VM).

## 👥 Equipe
* *Jamilly Vitoria Ferreira Barbosa*
  * Matrícula: 20250071213
* *Marcos Vinicius Jansem Oliveira*
  * Matrícula: 20250071278 

## 🛠️ Tecnologias Utilizadas
* *Linguagem de Programação:* Java

## 📁 Estrutura do Projeto

```
CompiladorJack/
├── src/
│   ├── Main.java             # Ponto de entrada
│   ├── JackScanner.java      # Analisador léxico 
│   ├── Token.java            # Representa um token
│   ├── TokenType.java        # Enum com os tipos de token
│   ├── XMLGenerator.java     # Gera o arquivo XML de saída
│   └── JackScannerTest.java  # Testes unitários (JUnit 5)
├── tests/
│   ├── Main.jack
│   ├── Square.jack
│   └── SquareGame.jack
├── .project                  # Configuração do Eclipse
├── .classpath                # Configuração do Eclipse
└── README.md
```

---

## ▶️ Como rodar no Spring Tools for Eclipse (STS)

### Importar o projeto

1. Abra o STS
2. `File` → `Import` → `General` → `Existing Projects into Workspace`
3. Selecione a pasta `CompiladorJack`
4. Clique em `Finish`

### Rodar o Main

1. Clique com botão direito em `Main.java`
2. `Run As` → `Run Configurations`
3. Na aba **Arguments**, em **Program arguments**, coloque:
   ```
   tests/Main.jack
   ```
   Ou para processar todos de uma vez:
   ```
   tests
   ```
4. Clique em `Run`

O XML gerado aparece na pasta `tests/`:
```
tests/MainT.xml
tests/SquareT.xml
tests/SquareGameT.xml
```

---

## 🧪 Como rodar os testes (JUnit 5)

### Adicionar JUnit 5 ao projeto

1. Clique com botão direito no projeto → `Build Path` → `Add Libraries`
2. Selecione `JUnit` → `Next`
3. Escolha **JUnit 5** → `Finish`

### Rodar os testes

1. Clique com botão direito em `JackScannerTest.java`
2. `Run As` → `JUnit Test`

---

## 📌 Tokens Reconhecidos

| Tipo | Exemplos |
|------|---------|
| `keyword` | `class`, `int`, `while`, `return` |
| `symbol` | `{`, `}`, `+`, `-`, `<`, `>` |
| `integerConstant` | `0`, `42`, `255` |
| `stringConstant` | `"hello"`, `"Jack"` |
| `identifier` | `x`, `Square`, `moveUp` |

---

## 📄 Formato da Saída XML

```xml
<tokens>
<keyword> class </keyword>
<identifier> Main </identifier>
<symbol> { </symbol>
...
</tokens>
```

Caracteres especiais são escapados:

| Caractere | Escape |
|-----------|--------|
| `&` | `&amp;` |
| `<` | `&lt;` |
| `>` | `&gt;` |
| `"` | `&quot;` |
EOF
echo "ok"

*Curso:* Engenharia da Computação - CCET - UFMA

*Curso:* Sergio Souza Costa
