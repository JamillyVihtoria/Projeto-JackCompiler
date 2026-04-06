import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JackScanner {

    private final String input;
    private int  pos  = 0;
    private char peek = ' ';   
    private final Map<String, TokenType> keywords = new HashMap<>();

    private static final String SYMBOL_CHARS = "{}()[].,;+-*/&|<>=~";

 
    public JackScanner(String input) {
        this.input = input;

        keywords.put("class",       TokenType.CLASS);
        keywords.put("constructor", TokenType.CONSTRUCTOR);
        keywords.put("function",    TokenType.FUNCTION);
        keywords.put("method",      TokenType.METHOD);
        keywords.put("field",       TokenType.FIELD);
        keywords.put("static",      TokenType.STATIC);
        keywords.put("var",         TokenType.VAR);
        keywords.put("int",         TokenType.INT);
        keywords.put("char",        TokenType.CHAR);
        keywords.put("boolean",     TokenType.BOOLEAN);
        keywords.put("void",        TokenType.VOID);
        keywords.put("true",        TokenType.TRUE);
        keywords.put("false",       TokenType.FALSE);
        keywords.put("null",        TokenType.NULL);
        keywords.put("this",        TokenType.THIS);
        keywords.put("let",         TokenType.LET);
        keywords.put("do",          TokenType.DO);
        keywords.put("if",          TokenType.IF);
        keywords.put("else",        TokenType.ELSE);
        keywords.put("while",       TokenType.WHILE);
        keywords.put("return",      TokenType.RETURN);

        readch(); 
    }

    
    private void readch() {
        peek = (pos < input.length()) ? input.charAt(pos++) : '\0';
    }
    
 public Token scan() {

        
        while (peek == ' ' || peek == '\t' || peek == '\r' || peek == '\n') {
            readch();
        }
        
        if (Character.isDigit(peek)) return scanNumber();
        
        if (peek == '"') return scanString();
        
        if (Character.isLetter(peek) || peek == '_') return scanIdentifier();
        
        if (peek == '\0') return new Token(TokenType.EOF, "");

        
        throw new RuntimeException("Token não reconhecido: '" + peek + "'");
    }

    private Token scanNumber() {
        int v = 0;
        do {
            v = 10 * v + (peek - '0');
            readch();
        } while (Character.isDigit(peek));

        return new Token(TokenType.NUMBER, String.valueOf(v));
    }
    
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        do {
            t = scan();
            tokens.add(t);
        } while (t.tag != TokenType.EOF);
        return tokens;
    }
    
    private Token scanString() {
        readch(); 
        StringBuilder b = new StringBuilder();
        while (peek != '"' && peek != '\0') {
            b.append(peek);
            readch();
        }
        readch(); 
        return new Token(TokenType.STRING, b.toString());
    }
    
    private Token scanIdentifier() {
        StringBuilder b = new StringBuilder();
        do {
            b.append(peek);
            readch();
        } while (Character.isLetterOrDigit(peek) || peek == '_');

        String lexeme = b.toString();
        TokenType type = keywords.getOrDefault(lexeme, TokenType.IDENT);
        return new Token(type, lexeme);
    }
   

}
