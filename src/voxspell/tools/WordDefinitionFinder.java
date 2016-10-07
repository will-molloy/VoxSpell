package voxspell.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Finds the definition for a given word using sdcv bash calls and a WebSter 1913 dictionary.
 * <p>
 * Both the dictionary and sdcv needs to be installed on your system for this to work.
 * <p>
 * Install sdcv using sudo apt-get install sdcv
 * And extract the dictionary inside Voxspell/Dictionary to /usr/share/stardict/dic
 *
 * @author Will Molloy
 */
public class WordDefinitionFinder {

    private static final String definitionFileName = ".definition";
    private static File definitionFile = new File(definitionFileName);
    private static String word;
    private static BufferedReader bufferedReader;

    static {
        try {
            bufferedReader = new BufferedReader(new FileReader(definitionFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*
         TEST WITH:
         New Zealand - no numbers
         to - finds other words
         you - difficult lookup
         eyes - timeout
         */
      //  System.out.println(getDefinition("knew"));
    }



    /**
     * Returns the definition for the supplied word.
     * <p>
     * Note: does not work for every word. E.g. 'got', sdcv will lookup 'get' instead..
     */
    public static String getDefinition(String word) {
        WordDefinitionFinder.word = word;
        String command;
        String definition;

        // Run the sdcv command and print the output to a hidden file '.definition'
        if (!definitionFile.exists()) {
            List<String> lines = Collections.singletonList("");
            Path file = Paths.get(definitionFileName);
            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        command = "sdcv " + "\"" + word + "\"" + " > " + definitionFileName;
        runBashCommand(command);

        // Extract the first definition only (sdcv may print ~10+ definitions for a word)
        definition = extractDefinition(false);

        // Trim defintion - remove numbers and dates etc
        definition = trimDefinition(definition);


        return definition;
    }

    private static String extractDefinition(boolean defFound) {
        String def = "";
        boolean wordFound = false;
        String line;

        try {

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue; // avoid AIOOB with empty lines
                }
                if (defFound && wordFound) {
                    line = line.trim();
                    // keep adding to definition until "[1913 Webster]" or "[Webster 1913 ...]"
                    if (line.contains("[1913") || line.contains("[Webster")) {
                        break;
                        // Ignore starting lines with [], () or {} , definition begins with a capital letter.
                    } else if ((line.contains("(") || line.contains("[") || line.contains("{") || !Character.isUpperCase(line.charAt(0))) && def.equals("")) {
                        continue;
                    }
                    def += line;
                }
                if (!wordFound) {
                    wordFound = lineContainsWord(line);
                } else if (!defFound) {
                    line = line.trim();
                    // First definition is on a line beginning with "1." -- only want first definition
                    if (line.split("\\s+")[0].equals("1.")) {
                        def += line + " ";
                        defFound = true;
                    }
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // special case if word is found but definition isn't (def didn't begin with a number i.e. sdcv only found one definition)
        if (wordFound && !defFound) {
            def = extractDefinition(true);
        } else if (!wordFound || def.trim().equals("")) {
            return "Definition not found."; // 'word' not found in dictionary
        }
        return def;
    }

    private static boolean lineContainsWord(String line) {
        for (int i = 0; i < word.split("\\s+").length; i++) {
            if (!(line.split("\\s+")[i].toLowerCase().equals(word.split("\\s+")[i].toLowerCase()))) {
                return false;
            }
        }
        return true;
    }

    private static String trimDefinition(String definition) {
        String output = "";
        for (String s : definition.split("\\s+")) {
            if (s.equals("1.")) {
                continue;
            }
            if (s.contains(";") || s.contains(".")) {
                output += s; // the definition ends once reaching ";" or "." (sometimes includes information like Noun etc)
                break;
            }
            output += s + " ";
        }
        return output;
    }

    private static void runBashCommand(String command) {
        try {
            ProcessBuilder _processBuilder = new ProcessBuilder("bash", "-c", command);
            Process _process = _processBuilder.start();
            _process.waitFor(100, TimeUnit.MILLISECONDS); // Sometimes sdcv gives an option if word is vague, timeout if this happens
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
