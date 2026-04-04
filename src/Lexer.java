import java.util.*;

public class Lexer {

    private String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            // ignora espaços
            if (Character.isWhitespace(c)) {
                pos++;
                continue;
            }

            // número
            if (Character.isDigit(c)) {
                tokens.add(readNumber());
                continue;
            }

            // identificador
            if (Character.isLetter(c)) {
                tokens.add(readIdentifier());
                continue;
            }

            // símbolo simples
            tokens.add(new Token("symbol", String.valueOf(c)));
            pos++;
        }

        return tokens;
    }

    private Token readNumber() {
        StringBuilder sb = new StringBuilder();

        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }

        return new Token("integerConstant", sb.toString());
    }

    private Token readIdentifier() {
        StringBuilder sb = new StringBuilder();

        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }

        return new Token("identifier", sb.toString());
    }
}