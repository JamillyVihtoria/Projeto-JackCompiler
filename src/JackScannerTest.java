import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;

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
            { " 123 ", "<integerConstant> 123 </integerConstant>" }, 
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
}