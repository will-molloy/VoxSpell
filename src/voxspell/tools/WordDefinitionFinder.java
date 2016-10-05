package voxspell.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    private static final File definitionFile = new File(definitionFileName);
    private String word;

    public WordDefinitionFinder(String word) {
        this.word = word;
    }

    public static void main(String[] args) {
        WordDefinitionFinder wordDefinitionFinder = new WordDefinitionFinder("new zealand");
        System.out.println(wordDefinitionFinder.getDefinition());
    }

    /**
     * Returns the definition for the supplied word.
     * <p>
     * Note: does not work for every word. E.g. 'got', sdcv will lookup 'get' instead..
     */
    public String getDefinition() {
        String command;
        String definition;

        // Run the sdcv command and print the output to a hidden file '.definition'
        command = "sdcv " + "\"" + word + "\"" + " > " + definitionFileName;
        runBashCommand(command);

        // Extract the first definition only (sdcv may print ~10+ definitions for a word)
        definition = extractDefinition(false);

        // Trim defintion - remove numbers and dates etc
        definition = trimDefinition(definition);

        // Delete hidden file
        command = "rm -f " + definitionFileName;
        runBashCommand(command);

        return definition;
    }

    private String extractDefinition(boolean defFound) {
        String def = "";
        boolean wordFound = false;
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(definitionFile));

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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // special case if word is found but definition isn't (def didn't begin with a number i.e. sdcv only found one definition)
        if (wordFound && !defFound) {
            def = extractDefinition(true);
        } else if (!wordFound) {
            return "Definition not found."; // 'word' not found in dictionary
        }
        return def;
    }

    private boolean lineContainsWord(String line) {
        for (int i = 0; i < word.split("\\s+").length; i++) {
            if (!(line.split("\\s+")[i].toLowerCase().equals(word.split("\\s+")[i].toLowerCase()))) {
                return false;
            }
        }
        return true;
    }

    private String trimDefinition(String definition) {
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

    private void runBashCommand(String command) {
        try {
            ProcessBuilder _processBuilder = new ProcessBuilder("bash", "-c", command);
            Process _process = _processBuilder.start();
            _process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
