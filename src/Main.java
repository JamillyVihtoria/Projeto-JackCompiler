import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("Diretório atual: " + new File(".").getAbsolutePath());

        if (args.length < 1) {
            System.out.println("Nenhum argumento informado.");
            System.out.println("Uso: Main <arquivo.jack | diretório>");
            System.out.println("Exemplo de argumento: tests");
            System.exit(1);
        }

        
  File target = new File(args[0]);

        
        if (!target.exists()) {
            target = new File(new File(".").getAbsolutePath() + File.separator + args[0]);
        }

        if (!target.exists()) {
            System.err.println("Erro: '" + args[0] + "' não encontrado.");
            System.exit(1);
        }

        List<File> jackFiles = new ArrayList<>();

        if (target.isDirectory()) {
            File[] listed = target.listFiles(f -> f.getName().endsWith(".jack"));
            if (listed != null) jackFiles.addAll(Arrays.asList(listed));
        } else if (target.getName().endsWith(".jack")) {
            jackFiles.add(target);
        } else {
            System.err.println("Erro: informe um arquivo .jack ou um diretório.");
            System.exit(1);
        }

        if (jackFiles.isEmpty()) {
            System.err.println("Nenhum arquivo .jack encontrado em: " + target.getAbsolutePath());
            System.exit(1);
        }

        System.out.println();

        for (File jackFile : jackFiles) {
            processFile(jackFile);
        }
    }

  

    private static void processFile(File jackFile) {
        try {
            String source   = Files.readString(jackFile.toPath());
            String baseName = jackFile.getName().replace(".jack", "");
            String dir      = jackFile.getParent() + File.separator;

            // ── Scanner → ArquivoT.xml ───────────────────────────
            JackScanner scanner = new JackScanner(source);
            List<Token> tokens  = scanner.tokenize();

            XMLGenerator.write(tokens, dir + baseName + "T.xml");
            System.out.println("✓ Scanner: " + jackFile.getName() + " → " + baseName + "T.xml");

            // ── Parser → ArquivoP.xml ────────────────────────────
            List<Token> parseTokens = tokens.stream()
                .filter(t -> t.tag != TokenType.EOF)
                .toList();

            Parser parser = new Parser(parseTokens);
            parser.parse();

            String pXmlPath = dir + baseName + "P.xml";
            Files.writeString(Path.of(pXmlPath), parser.getXml());
            System.out.println("✓ Parser:  " + jackFile.getName() + " → " + baseName + "P.xml");

            // ── Comparação com arquivo de referência ─────────────
            String refPath = dir + baseName + "Ref.xml";
            File refFile   = new File(refPath);

            if (refFile.exists()) {
                compareXml(parser.getXml(), Files.readString(refFile.toPath()), baseName);
            } else {
                System.out.println("  ⚠ Referência não encontrada: " + baseName + "Ref.xml");
                System.out.println("    Coloque o arquivo oficial como " + baseName + "Ref.xml na pasta tests/");
            }

            System.out.println();

        } catch (IOException e) {
            System.err.println("✗ Erro de I/O: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("✗ Erro: " + e.getMessage());
        }
    }

    private static void compareXml(String generated, String reference, String baseName) {
        String genNorm = normalize(generated);
        String refNorm = normalize(reference);

        if (genNorm.equals(refNorm)) {
            System.out.println("  ✓ Validação: " + baseName + "P.xml idêntico ao arquivo de referência!");
        } else {
            System.out.println("  ✗ Validação: diferenças encontradas em " + baseName + "P.xml");

            List<String> genLines = genNorm.lines().toList();
            List<String> refLines = refNorm.lines().toList();

            int maxLines   = Math.min(genLines.size(), refLines.size());
            int diffsShown = 0;

            for (int i = 0; i < maxLines && diffsShown < 5; i++) {
                if (!genLines.get(i).equals(refLines.get(i))) {
                    System.out.println("    Linha " + (i + 1) + ":");
                    System.out.println("      Gerado:     " + genLines.get(i));
                    System.out.println("      Referência: " + refLines.get(i));
                    diffsShown++;
                }
            }

            if (genLines.size() != refLines.size()) {
                System.out.println("    Total de linhas — Gerado: " + genLines.size()
                    + " | Referência: " + refLines.size());
            }

            if (diffsShown == 5) {
                System.out.println("    ... (apenas as primeiras 5 diferenças exibidas)");
            }
        }
    }

    private static String normalize(String xml) {
        return xml.lines()
                  .map(String::stripLeading)
                  .filter(line -> !line.isBlank())
                  .collect(Collectors.joining("\n"));
    }
     
}
