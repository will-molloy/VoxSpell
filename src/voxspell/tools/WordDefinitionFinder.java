package voxspell.tools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by will on 5/10/16.
 */
public class WordDefinitionFinder {

    private static final File defintionFile = new File(".definition");
    private ProcessBuilder _processBuilder;
    private Process _process;

    public static void main(String[] args) {
        WordDefinitionFinder wordDefinitionFinder = new WordDefinitionFinder();
        System.out.println(wordDefinitionFinder.getDefinition("leg"));
    }

    public String getDefinition(String word) {
        String command;
        String def = "";
        boolean defFound = false;
        command = "sdcv " + word + " > .definition";
        runBashCommand(command);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(defintionFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                // First definition is on a line beginning with "1." -- only want first definition
                if (line.split("\\s+")[0].equals("1.")) {
                    def += line + " ";
                    defFound = true;
                } else if (defFound) {
                    def += line;
                    // keep adding to definition until "[1913 Webster]" or "[Webster 1913 ...]"
                } else if (line.contains("[1913") || line.contains("[Webster")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String output = "";
        for (String s : def.split("\\s+")) {
            if (s.equals("1.")) {
                continue;
            }
            if (s.contains("(") || s.contains(")")) {
                continue;
            }
            if (s.contains(";")) {
                output += s; // the definition ends once reaching ";"
                break;
            }
            output += s + " ";
        }

        command = "rm -f .definition";

        runBashCommand(command);

        return output;
    }

    private void runBashCommand(String command) {
        try {
            _processBuilder = new ProcessBuilder("bash", "-c", command);
            _process = _processBuilder.start();
            _process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
