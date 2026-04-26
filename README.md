# Projeto JackCompiler - Analisador Léxico e Sintático

Este repositório contém a implementação de um *analisador léxico  e Analisador sintático * para a linguagem *Jack*, como parte da disciplina de Compiladores. O objetivo desta etapa é identificar e classificar os tokens do código-fonte Jack.

---

## 🎓 Informações Acadêmicas

* **Instituição:** Universidade Federal do Maranhão (UFMA)
* **Curso:** Engenharia da Computação – CCET
* **Professor:** Sergio Souza Costa

---

## 👥 Equipe

* **Jamilly Vitoria Ferreira Barbosa**
  Matrícula: 20250071213

* **Marcos Vinicius Jansem Oliveira**
  Matrícula: 20250071278

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java
* **Framework de Testes:** JUnit 5

---

## 📁 Estrutura do Projeto

```text
CompiladorJack/
├── src/
│   ├── Main.java             # Ponto de entrada da aplicação
│   ├── JackScanner.java      # Analisador léxico (Tokenizer)
│   ├── Parser.java           # Analisador Sintaático 
│   ├── Token.java            # Classe que representa um token
│   ├── TokenType.java        # Enumeração dos tipos de tokens
│   ├── XMLGenerator.java     # Responsável pela geração do XML
│   ├──JackScannerTest.java  # Testes unitários (JUnit 5)
│   └── ParserTest.java  # Testes unitários (JUnit 5)

├── tests/
│   ├── Main.jack
│   ├── Square.jack
│   └── SquareGame.jack
├── .project                  # Configuração do Eclipse
├── .classpath                # Configuração do Eclipse
└── README.md
```

---

## ▶️ Como Compilar e Executar

### 🧰 Via Spring Tools Suite (STS / Eclipse)

#### Importar o projeto

1. `File` → `Import`
2. `General` → `Existing Projects into Workspace`
3. Selecione a pasta `CompiladorJack`
4. Clique em `Finish`

#### Executar o programa

1. Clique com botão direito em `Main.java`
2. `Run As` → `Run Configurations`
3. Na aba **Arguments**, em **Program arguments**, insira:

* Para um arquivo:

```text
tests/Main.jack
```

* Para um diretório:

```text
tests
```

4. Clique em `Run`

---

## 📄 Saída e Validação

* **Formato de saída:** XML

* **Nome do arquivo gerado:**
  Mantém o nome original com sufixo `T.xml` e `P.xml`
  Exemplo:

  ```
  Main.jack → MainT.xml (Analisador Léxico)
  Main.jack → MainP.xml (Analisador sintático)
  ```

* **Localização:** Mesmo diretório do arquivo de entrada

* **Validação:**
  Os arquivos gerados foram comparados com os arquivos oficiais utilizando a ferramenta **TextComparer**, apresentando **100% de correspondência**.

---

## 🧗 Desafios Enfrentados

Durante o desenvolvimento do analisador léxico, alguns pontos exigiram maior atenção:

* **Tratamento de comentários:**

  * Comentários de linha (`//`)
  * Comentários de múltiplas linhas (`/* ... */`)
  * Comentários de documentação (`/** ... */`)

* **Ignorar corretamente espaços em branco** sem perder a posição dos tokens.

* **Escape de caracteres especiais no XML:**

  * `<`, `>`, `&`, `"`

* **Reconhecimento preciso de tokens**, evitando ambiguidades entre identificadores e palavras-chave.

Esses desafios foram fundamentais para garantir a robustez do analisador.

---

## 🧪 Testes (JUnit 5)

### Adicionar JUnit ao projeto

1. Clique com botão direito no projeto
2. `Build Path` → `Add Libraries`
3. Selecione `JUnit`
4. Escolha **JUnit 5**
5. Clique em `Finish`

### Executar testes

1. Clique com botão direito em `JackScannerTest.java`
2. `Run As` → `JUnit Test`

---

## 📌 Tokens Reconhecidos

| Tipo              | Exemplos                              |
| ----------------- | ------------------------------------- |
| `keyword`         | class, int, while, return             |
| `symbol`          | { } ( ) [ ] . , ; + - * / & | < > = ~ |
| `integerConstant` | 0, 42, 255                            |
| `stringConstant`  | "hello", "Jack"                       |
| `identifier`      | x, Square, moveUp                     |

---

## 📄 Formato da Saída XML

Cada token é encapsulado em sua respectiva tag:

```xml
<tokens>
  <keyword> class </keyword>
  <identifier> Main </identifier>
  <symbol> { </symbol>
  ...
</tokens>
```

### 🔐 Escape de Caracteres Especiais

| Caractere | Representação XML |
| --------- | ----------------- |
| &         | &                 |
| <         | <                 |
| >         | >                 |
| "         | "                 |
