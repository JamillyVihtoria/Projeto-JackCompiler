import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;


/**
 * Testes unitários do Parser Jack — fases incrementais:
 *
 * Fase 1: term simples
 * Fase 2: expression
 * Fase 3: letStatement
 * Fase 4: statements (if, while, do, return)
 * Fase 5: estrutura completa (class, classVarDec, subroutineDec)
 */
public class ParserTest {


    /** Tokeniza e remove EOF. */
    private List<Token> tokenize(String code) {
        return new JackScanner(code).tokenize()
               .stream()
               .filter(t -> t.tag != TokenType.EOF)
               .toList();
    }

    // ---------------------------------------------------------------
    // Fase 1: term
    // ---------------------------------------------------------------

    @Test
    void testTermInteiro() {
        String code = wrapExpression("10");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<term>"));
        assertTrue(xml.contains("<integerConstant> 10 </integerConstant>"));
    }

    @Test
    void testTermString() {
        String code = wrapExpression("\"hello\"");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<stringConstant> hello </stringConstant>"));
    }

    @Test
    void testTermKeywordConstant() {
        String code = wrapExpression("true");
        Parser p = new Parser(tokenize(code));
        p.parse();
        assertTrue(p.getXml().contains("<keyword> true </keyword>"));
    }

    @Test
    void testTermVarName() {
        String code = wrapExpression("x");
        Parser p = new Parser(tokenize(code));
        p.parse();
        assertTrue(p.getXml().contains("<identifier> x </identifier>"));
        assertTrue(p.getXml().contains("<term>"));
    }

    // ---------------------------------------------------------------
    // Fase 2: expression
    // ---------------------------------------------------------------

    @Test
    void testExpressionSimples() {
        String code = wrapExpression("10");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<expression>"));
        assertTrue(xml.contains("<integerConstant> 10 </integerConstant>"));
    }

    @Test
    void testExpressionComOperador() {
        String code = wrapExpression("10 + 20");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<symbol> + </symbol>"));
        assertTrue(xml.contains("<integerConstant> 10 </integerConstant>"));
        assertTrue(xml.contains("<integerConstant> 20 </integerConstant>"));
    }

    @Test
    void testExpressionUnaria() {
        String code = wrapExpression("~x");
        Parser p = new Parser(tokenize(code));
        p.parse();
        assertTrue(p.getXml().contains("<symbol> ~ </symbol>"));
    }

    // ---------------------------------------------------------------
    // Fase 3: letStatement
    // ---------------------------------------------------------------

    @Test
    void testLetSimples() {
        String code = wrapStatement("let x = 5;");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<letStatement>"));
        assertTrue(xml.contains("<keyword> let </keyword>"));
        assertTrue(xml.contains("<identifier> x </identifier>"));
        assertTrue(xml.contains("<integerConstant> 5 </integerConstant>"));
    }

    @Test
    void testLetComArray() {
        String code = wrapStatement("let a[0] = 10;");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<letStatement>"));
        assertTrue(xml.contains("<symbol> [ </symbol>"));
        assertTrue(xml.contains("<symbol> ] </symbol>"));
    }

    // ---------------------------------------------------------------
    // Fase 4: outros statements
    // ---------------------------------------------------------------

    @Test
    void testReturnVazio() {
        String code = wrapStatement("return;");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<returnStatement>"));
        assertTrue(xml.contains("<keyword> return </keyword>"));
    }

    @Test
    void testReturnComExpressao() {
        String code = wrapStatement("return x;");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<returnStatement>"));
        assertTrue(xml.contains("<expression>"));
    }

    @Test
    void testDoStatement() {
        String code = wrapStatement("do Output.printInt(x);");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<doStatement>"));
        assertTrue(xml.contains("<identifier> Output </identifier>"));
        assertTrue(xml.contains("<identifier> printInt </identifier>"));
        assertTrue(xml.contains("<expressionList>"));
    }

    @Test
    void testIfSimples() {
        String code = wrapStatement("if (x) { return; }");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<ifStatement>"));
        assertTrue(xml.contains("<keyword> if </keyword>"));
    }

    @Test
    void testIfComElse() {
        String code = wrapStatement("if (x) { return; } else { return; }");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<keyword> else </keyword>"));
    }

    @Test
    void testWhile() {
        String code = wrapStatement("while (x) { let x = 0; }");
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<whileStatement>"));
        assertTrue(xml.contains("<keyword> while </keyword>"));
    }

    // ---------------------------------------------------------------
    // Fase 5: estrutura de classe
    // ---------------------------------------------------------------

    @Test
    void testClassVazia() {
        String code = "class Main { }";
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<class>"));
        assertTrue(xml.contains("<keyword> class </keyword>"));
        assertTrue(xml.contains("<identifier> Main </identifier>"));
    }

    @Test
    void testClassVarDec() {
        String code = "class Main { field int x; }";
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<classVarDec>"));
        assertTrue(xml.contains("<keyword> field </keyword>"));
        assertTrue(xml.contains("<keyword> int </keyword>"));
    }

    @Test
    void testSubroutineVazia() {
        String code = "class Main { function void main() { return; } }";
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<subroutineDec>"));
        assertTrue(xml.contains("<parameterList>"));
        assertTrue(xml.contains("<subroutineBody>"));
    }

    @Test
    void testParameterList() {
        String code = "class Main { function void main(int x, int y) { return; } }";
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<parameterList>"));
        assertTrue(xml.contains("<identifier> x </identifier>"));
        assertTrue(xml.contains("<identifier> y </identifier>"));
    }

    @Test
    void testVarDec() {
        String code = "class Main { function void main() { var int x; return; } }";
        Parser p = new Parser(tokenize(code));
        p.parse();
        String xml = p.getXml();
        assertTrue(xml.contains("<varDec>"));
        assertTrue(xml.contains("<keyword> var </keyword>"));
    }

    // ---------------------------------------------------------------
    // Helpers — encapsula código dentro de classe/método mínimo
    // ---------------------------------------------------------------

    /** Envolve uma expression dentro de um método mínimo para testar o parser. */
    private String wrapExpression(String expr) {
        return "class Main { function void main() { let x = " + expr + "; } }";
    }

    /** Envolve um statement dentro de um método mínimo. */
    private String wrapStatement(String stmt) {
        return "class Main { function void main() { " + stmt + " } }";
    }
}
