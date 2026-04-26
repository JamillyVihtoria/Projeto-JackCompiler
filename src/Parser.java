import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;        // índice do token atual
    private final StringBuilder xml = new StringBuilder();
    private int indentLevel = 0;    // controla indentação do XML


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ---------------------------------------------------------------
    // Navegação — peek(), advance(), consume()
    // ---------------------------------------------------------------

    /** Retorna o token atual sem avançar. */ 
    private Token peek() {
        if (current < tokens.size()) return tokens.get(current);
        return new Token(TokenType.EOF, "");
    }

    /** Avança para o próximo token e retorna o atual. */
    private Token advance() { 
        Token t = peek();
        current++;
        return t;
    }

    /**
     * Verifica se o token atual é do tipo esperado,
     * escreve no XML e avança. Lança erro se não bater.
     */
    private void consume(TokenType expected) {
        Token t = peek();
        if (t.tag != expected) {
            throw new RuntimeException(
                "Erro sintático: esperado " + expected +
                " mas encontrado '" + t.value + "' (" + t.tag + ")");
        }
        writeToken(advance());
    }

    /**
     * Versão de consume que aceita qualquer valor mas exige o tipo.
     * Usada quando sabemos o tipo mas não o valor exato.
     */
    private void consumeIdentifier() {
        consume(TokenType.IDENT);
    }

    // ---------------------------------------------------------------
    // Helpers de XML
    // ---------------------------------------------------------------

    private String indent() {
        return "  ".repeat(indentLevel);
    }

    private void openTag(String tag) {
        xml.append(indent()).append("<").append(tag).append(">").append("\n");
        indentLevel++;
    }

    private void closeTag(String tag) {
        indentLevel--;
        xml.append(indent()).append("</").append(tag).append(">").append("\n");
    }

    private void writeToken(Token t) {
        xml.append(indent()).append(t.toXML()).append("\n");
    }

    public String getXml() {
        return xml.toString();
    }

    // ---------------------------------------------------------------
    // Ponto de entrada
    // ---------------------------------------------------------------

    /** Inicia o parsing pela regra raiz: class */
    public void parse() {
        parseClass();
    }

    // ---------------------------------------------------------------
    // Regras da gramática — estrutura de classe
    // ---------------------------------------------------------------

    /**
     * class → 'class' className '{' classVarDec* subroutineDec* '}'
     */
    private void parseClass() {
        openTag("class");

        consume(TokenType.CLASS);
        consumeIdentifier();          // className
        consume(TokenType.LBRACE);    // {

        // classVarDec*
        while (peek().tag == TokenType.STATIC || peek().tag == TokenType.FIELD) {
            parseClassVarDec();
        }

        // subroutineDec*
        while (peek().tag == TokenType.CONSTRUCTOR ||
               peek().tag == TokenType.FUNCTION    ||
               peek().tag == TokenType.METHOD) {
            parseSubroutineDec();
        }

        consume(TokenType.RBRACE);    // }

        closeTag("class");
    }

    /**
     * classVarDec → ('static'|'field') type varName (',' varName)* ';'
     */
    private void parseClassVarDec() {
        openTag("classVarDec");

        writeToken(advance());   // static | field
        parseType();             // type
        consumeIdentifier();     // varName

        while (peek().tag == TokenType.COMMA) {
            consume(TokenType.COMMA);
            consumeIdentifier(); // varName
        }

        consume(TokenType.SEMICOLON);

        closeTag("classVarDec");
    }

    /**
     * subroutineDec → ('constructor'|'function'|'method')
     *                 ('void'|type) subroutineName '(' parameterList ')' subroutineBody
     */
    private void parseSubroutineDec() {
        openTag("subroutineDec");

        writeToken(advance());   // constructor | function | method

        // void ou type
        if (peek().tag == TokenType.VOID) {
            writeToken(advance());
        } else {
            parseType();
        }

        consumeIdentifier();          // subroutineName
        consume(TokenType.LPAREN);    // (
        parseParameterList();
        consume(TokenType.RPAREN);    // )
        parseSubroutineBody();

        closeTag("subroutineDec");
    }

    /**
     * parameterList → ((type varName) (',' type varName)*)?
     */
    private void parseParameterList() {
        openTag("parameterList");

        if (peek().tag != TokenType.RPAREN) {
            parseType();
            consumeIdentifier();

            while (peek().tag == TokenType.COMMA) {
                consume(TokenType.COMMA);
                parseType();
                consumeIdentifier();
            }
        }

        closeTag("parameterList");
    }

    /**
     * subroutineBody → '{' varDec* statements '}'
     */
    private void parseSubroutineBody() {
        openTag("subroutineBody");

        consume(TokenType.LBRACE);    // {

        while (peek().tag == TokenType.VAR) {
            parseVarDec();
        }

        parseStatements();

        consume(TokenType.RBRACE);    // }

        closeTag("subroutineBody");
    }

    /**
     * varDec → 'var' type varName (',' varName)* ';'
     */
    private void parseVarDec() {
        openTag("varDec");

        consume(TokenType.VAR);
        parseType();
        consumeIdentifier();

        while (peek().tag == TokenType.COMMA) {
            consume(TokenType.COMMA);
            consumeIdentifier();
        }

        consume(TokenType.SEMICOLON);

        closeTag("varDec");
    }

    /**
     * type → 'int' | 'char' | 'boolean' | className
     */
    private void parseType() {
        Token t = peek();
        if (t.tag == TokenType.INT    ||
            t.tag == TokenType.CHAR   ||
            t.tag == TokenType.BOOLEAN) {
            writeToken(advance());
        } else if (t.tag == TokenType.IDENT) {
            writeToken(advance());   // className
        } else {
            throw new RuntimeException(
                "Erro sintático: tipo esperado, encontrado '" + t.value + "'");
        }
    }

    // ---------------------------------------------------------------
    // Statements
    // ---------------------------------------------------------------

    /**
     * statements → statement*
     */
    private void parseStatements() {
        openTag("statements");

        while (peek().tag == TokenType.LET    ||
               peek().tag == TokenType.IF     ||
               peek().tag == TokenType.WHILE  ||
               peek().tag == TokenType.DO     ||
               peek().tag == TokenType.RETURN) {
            parseStatement();
        }

        closeTag("statements");
    }

    private void parseStatement() {
        switch (peek().tag) {
            case LET    -> parseLet();
            case IF     -> parseIf();
            case WHILE  -> parseWhile();
            case DO     -> parseDo();
            case RETURN -> parseReturn();
            default -> throw new RuntimeException(
                "Erro sintático: statement esperado, encontrado '" + peek().value + "'");
        }
    }

    /**
     * letStatement → 'let' varName ('[' expression ']')? '=' expression ';'
     */
    private void parseLet() {
        openTag("letStatement");

        consume(TokenType.LET);
        consumeIdentifier();          // varName

        if (peek().tag == TokenType.LBRACKET) {
            consume(TokenType.LBRACKET);   // [
            parseExpression();
            consume(TokenType.RBRACKET);   // ]
        }

        consume(TokenType.EQ);
        parseExpression();
        consume(TokenType.SEMICOLON);

        closeTag("letStatement");
    }

    /**
     * ifStatement → 'if' '(' expression ')' '{' statements '}'
     *               ('else' '{' statements '}')?
     */
    private void parseIf() {
        openTag("ifStatement");

        consume(TokenType.IF);
        consume(TokenType.LPAREN);
        parseExpression();
        consume(TokenType.RPAREN);
        consume(TokenType.LBRACE);
        parseStatements();
        consume(TokenType.RBRACE);

        if (peek().tag == TokenType.ELSE) {
            consume(TokenType.ELSE);
            consume(TokenType.LBRACE);
            parseStatements();
            consume(TokenType.RBRACE);
        }

        closeTag("ifStatement");
    }

    /**
     * whileStatement → 'while' '(' expression ')' '{' statements '}'
     */
    private void parseWhile() {
        openTag("whileStatement");

        consume(TokenType.WHILE);
        consume(TokenType.LPAREN);
        parseExpression();
        consume(TokenType.RPAREN);
        consume(TokenType.LBRACE);
        parseStatements();
        consume(TokenType.RBRACE);

        closeTag("whileStatement");
    }

    /**
     * doStatement → 'do' subroutineCall ';'
     */
    private void parseDo() {
        openTag("doStatement");

        consume(TokenType.DO);
        parseSubroutineCall();
        consume(TokenType.SEMICOLON);

        closeTag("doStatement");
    }

    /**
     * returnStatement → 'return' expression? ';'
     */
    private void parseReturn() {
        openTag("returnStatement");

        consume(TokenType.RETURN);

        if (peek().tag != TokenType.SEMICOLON) {
            parseExpression();
        }

        consume(TokenType.SEMICOLON);

        closeTag("returnStatement");
    }

    // ---------------------------------------------------------------
    // Expressões
    // ---------------------------------------------------------------

    /**
     * expression → term (op term)*
     */
    private void parseExpression() {
        openTag("expression");

        parseTerm();

        while (isOp(peek())) {
            writeToken(advance());   // operador
            parseTerm();
        }

        closeTag("expression");
    }

    /**
     * term → integerConstant | stringConstant | keywordConstant
     *       | varName | varName '[' expression ']'
     *       | subroutineCall | '(' expression ')' | unaryOp term
     */
    private void parseTerm() {
        openTag("term");

        Token t = peek();

        if (t.tag == TokenType.NUMBER) {
            writeToken(advance());

        } else if (t.tag == TokenType.STRING) {
            writeToken(advance());

        } else if (isKeywordConstant(t)) {
            writeToken(advance());

        } else if (t.tag == TokenType.LPAREN) {
            consume(TokenType.LPAREN);   // (
            parseExpression();
            consume(TokenType.RPAREN);   // )

        } else if (t.tag == TokenType.MINUS || t.tag == TokenType.NOT) {
            writeToken(advance());       // unaryOp
            parseTerm();

        } else if (t.tag == TokenType.IDENT) {
            // Lookahead: próximo token decide o que é
            Token next = tokens.get(current + 1);

            if (next.tag == TokenType.LPAREN) {
                // subroutineName '(' expressionList ')'
                parseSubroutineCall();

            } else if (next.tag == TokenType.DOT) {
                // (className|varName) '.' subroutineName '(' expressionList ')'
                parseSubroutineCall();

            } else if (next.tag == TokenType.LBRACKET) {
                // varName '[' expression ']'
                consumeIdentifier();
                consume(TokenType.LBRACKET);
                parseExpression();
                consume(TokenType.RBRACKET);

            } else {
                // varName simples
                consumeIdentifier();
            }

        } else {
            throw new RuntimeException(
                "Erro sintático: term esperado, encontrado '" + t.value + "'");
        }

        closeTag("term");
    }

    /**
     * subroutineCall → subroutineName '(' expressionList ')'
     *                | (className|varName) '.' subroutineName '(' expressionList ')'
     */
    private void parseSubroutineCall() {
        consumeIdentifier();   // subroutineName ou className/varName

        if (peek().tag == TokenType.DOT) {
            consume(TokenType.DOT);
            consumeIdentifier();   // subroutineName
        }

        consume(TokenType.LPAREN);
        parseExpressionList();
        consume(TokenType.RPAREN);
    }

    /**
     * expressionList → (expression (',' expression)*)?
     */
    private void parseExpressionList() {
        openTag("expressionList");

        if (peek().tag != TokenType.RPAREN) {
            parseExpression();

            while (peek().tag == TokenType.COMMA) {
                consume(TokenType.COMMA);
                parseExpression();
            }
        }

        closeTag("expressionList");
    }

    // ---------------------------------------------------------------
    // Helpers de classificação
    // ---------------------------------------------------------------

    /** op → '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '=' */
    private boolean isOp(Token t) {
        return t.tag == TokenType.PLUS      ||
               t.tag == TokenType.MINUS     ||
               t.tag == TokenType.ASTERISK  ||
               t.tag == TokenType.SLASH     ||
               t.tag == TokenType.AND       ||
               t.tag == TokenType.OR        ||
               t.tag == TokenType.LT        ||
               t.tag == TokenType.GT        ||
               t.tag == TokenType.EQ;
    }

    /** keywordConstant → 'true' | 'false' | 'null' | 'this' */
    private boolean isKeywordConstant(Token t) {
        return t.tag == TokenType.TRUE  ||
               t.tag == TokenType.FALSE ||
               t.tag == TokenType.NULL  ||
               t.tag == TokenType.THIS;
    }
}
