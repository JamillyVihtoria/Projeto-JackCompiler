import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class JackScannerTest {
	
    @Test
    void testNumeroBasico() {
        // "289" → <integerConstant> 289 </integerConstant>
        JackScanner scanner = new JackScanner("289");
        List<Token> tokens = scanner.tokenize();

        assertEquals("<integerConstant> 289 </integerConstant>", tokens.get(0).toXML());
    }

    @Test
    void testNumerosComXml() {
        
        String[][] casos = {
            { "0",     "<integerConstant> 0 </integerConstant>"   },
            { "289",   "<integerConstant> 289 </integerConstant>" },
            { "42",    "<integerConstant> 42 </integerConstant>"  },
            { " 123 ", "<integerConstant> 123 </integerConstant>" }, // com espaços
        };

        for (String[] caso : casos) {
            JackScanner scanner = new JackScanner(caso[0]);
            List<Token> tokens = scanner.tokenize();
            assertEquals(caso[1], tokens.get(0).toXML(), "Falhou para: " + caso[0]);
        }
    }
    
    @Test
    void testStringBasica() {
       
        JackScanner scanner = new JackScanner("\"hello\"");
        List<Token> tokens = scanner.tokenize();

        assertEquals(TokenType.STRING,  tokens.get(0).tag);
        assertEquals("hello",           tokens.get(0).value); 
        assertEquals("<stringConstant> hello </stringConstant>", tokens.get(0).toXML());
    }

    @Test
    void testStringComEspacos() {
        JackScanner scanner = new JackScanner("\"hello world\"");
        List<Token> tokens = scanner.tokenize();

        assertEquals("<stringConstant> hello world </stringConstant>", tokens.get(0).toXML());
    }
	
    @Test
    void testIdentificadoresEKeywords() {
      
        JackScanner scanner = new JackScanner("minhaVar123");
        List<Token> tokens = scanner.tokenize();

        assertEquals(TokenType.IDENT,  tokens.get(0).tag);
        assertEquals("minhaVar123",    tokens.get(0).value);
        assertEquals("<identifier> minhaVar123 </identifier>", tokens.get(0).toXML());

      
        scanner = new JackScanner("function");
        tokens  = scanner.tokenize();

        assertEquals(TokenType.FUNCTION, tokens.get(0).tag);
        assertEquals("function",         tokens.get(0).value);
        assertEquals("<keyword> function </keyword>", tokens.get(0).toXML());
    }

    @Test
    void testTodasAsKeywords() {

        String[] kws = {
            "class", "constructor", "function", "method", "field", "static",
            "var", "int", "char", "boolean", "void", "true", "false", "null",
            "this", "let", "do", "if", "else", "while", "return"
        };

        for (String kw : kws) {
            JackScanner scanner = new JackScanner(kw);
            List<Token> tokens = scanner.tokenize();
            assertEquals("keyword", tokens.get(0).tag.getXmlTag(),
                "Deveria ser keyword: " + kw);
        }
    }
    
    @Test
    void testIdentificadorComUnderscore() {
        JackScanner scanner = new JackScanner("minha_var");
        List<Token> tokens = scanner.tokenize();

        assertEquals(TokenType.IDENT, tokens.get(0).tag);
        assertEquals("minha_var",     tokens.get(0).value);
    }
    
    
    @Test
    void testSimbolosXml() {
        
        JackScanner scanner = new JackScanner("x + y;");
        List<Token> tokens = scanner.tokenize();

       
        List<Token> semEof = tokens.stream()
            .filter(t -> t.tag != TokenType.EOF)
            .toList();

        String[] esperado = {
            "<identifier> x </identifier>",
            "<symbol> + </symbol>",
            "<identifier> y </identifier>",
            "<symbol> ; </symbol>",
        };

        for (int i = 0; i < esperado.length; i++) {
            assertEquals(esperado[i], semEof.get(i).toXML(),
                "Token " + i + " não corresponde");
        }
    }

    @Test
    void testEscapeXml() {
        
        JackScanner scanner = new JackScanner("a < b");
        List<Token> tokens = scanner.tokenize();

        
        Token lt = tokens.stream()
            .filter(t -> t.value.equals("<"))
            .findFirst()
            .orElseThrow();

        assertEquals("<symbol> &lt; </symbol>", lt.toXML());

        
        scanner = new JackScanner("a > b");
        tokens  = scanner.tokenize();

        Token gt = tokens.stream()
            .filter(t -> t.value.equals(">"))
            .findFirst()
            .orElseThrow();

        assertEquals("<symbol> &gt; </symbol>", gt.toXML());

        
        scanner = new JackScanner("a & b");
        tokens  = scanner.tokenize();

        Token amp = tokens.stream()
            .filter(t -> t.value.equals("&"))
            .findFirst()
            .orElseThrow();

        assertEquals("<symbol> &amp; </symbol>", amp.toXML());
    }
    
    
    @Test
    void testComentarioLinhaIgnorado() {
        
        JackScanner scanner = new JackScanner("let x = 5; // isto some");
        List<Token> tokens = scanner.tokenize()
            .stream()
            .filter(t -> t.tag != TokenType.EOF)
            .toList();

       
        boolean temComentario = tokens.stream()
            .anyMatch(t -> t.value.contains("//") || t.value.contains("isto"));
        assertFalse(temComentario, "Comentário não deveria gerar token");

       
        List<String> values = tokens.stream().map(t -> t.value).toList();
        assertTrue(values.contains("let"));
        assertTrue(values.contains("x"));
        assertTrue(values.contains("5"));
    }

    @Test
    void testComentarioBlocoIgnorado() {
        JackScanner scanner = new JackScanner("let /* ignora tudo aqui */ x = 1;");
        List<Token> tokens = scanner.tokenize()
            .stream()
            .filter(t -> t.tag != TokenType.EOF)
            .toList();

        List<String> values = tokens.stream().map(t -> t.value).toList();
        assertTrue(values.contains("let"));
        assertTrue(values.contains("x"));
        assertTrue(values.contains("1"));
        assertFalse(values.contains("ignora"));
    }

    @Test
    void testComentarioJavadocIgnorado() {
        String code = "/** Descrição da classe */\nclass Main {}";
        JackScanner scanner = new JackScanner(code);
        List<Token> tokens = scanner.tokenize();

        assertEquals(TokenType.CLASS, tokens.get(0).tag);
        assertEquals("class",         tokens.get(0).value);
    }



}
