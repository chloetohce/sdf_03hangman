import java.io.Console;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class WBManager {
    private final File wb;
    
    public WBManager(File wb) {
        this.wb = wb;
    }

    private static void menu() {
        String menu = """
                \n
                ===================== EDIT =====================\n
                list: List all words within word bank.\n
                add <word>: Add word to word bank.\n
                delete <num>: Delete word at number <num>. \n
                save: Save all words to file. \n
                quit: Exit edit screen.\n
                ================================================\n\n
                """;
        System.out.println(menu);
    }

    private List<String> loadWB() throws IOException {
        List<String> words = new ArrayList<>();
        FileReader fr = new FileReader(wb);
        BufferedReader br = new BufferedReader(fr);
        String word = "";
        while ((word = br.readLine()) != null) {
            words.add(word);
        }
        br.close();
        fr.close();
        return words;
    }

    private void save(List<String> words) {
        try {
            FileWriter fw = new FileWriter(wb, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String word : words) {
                bw.append(word);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            bw.close();
        } catch (IOException e) {
            System.err.println("Unable to write to word bank.");
        }
    }

    public void editWordBank(File wb) throws IOException {
        Console editConsole = System.console();
        String input = "";
        List<String> words = loadWB();

        while (!input.equals("quit")) {
            menu();
            input = editConsole.readLine("> ");

            if (input.equals("list")) {
                for (int i = 0; i < words.size(); i++) {
                    System.out.println(i+1 + ". " + words.get(i));
                }
            } else if (input.startsWith("add")) {
                String items = input.substring(4).replace(",", " ");
                Scanner s = new Scanner(items);
                while (s.hasNext()) {
                    String item = s.next().toUpperCase();
                    if (words.contains(item)) {
                        System.out.println(item + " is already in word bank.");
                    } else {
                        words.add(item);
                    }
                }
                s.close();
            } else if (input.startsWith("delete")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                if (index > words.size() - 1 || index < 0) {
                    System.out.println("Incorrect item index. ");
                } else {
                    words.remove(index);
                }
            } else if (input.equals("save")) {
                save(words);
            } else if (input.equals("quit")) {
                System.out.println("Exiting edit screen...");
            }
        }
    }
}