import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String code = Files.readString(Path.of("teste.jack"));

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        System.out.println("<tokens>");
        for (Token t : tokens) {
            System.out.println(t.toXML());
        }
        System.out.println("</tokens>");
    }
}