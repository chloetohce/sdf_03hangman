import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Hangman {
    /* TASK
     * 1. Create a word list file
     * 2. Read the word list file in an array of words
     * 3. Pick a word randomly from the array of words
     * 4. Guess the word
     * 5. Prompt to enter a character
     * 6. If character exists in the word, display it as _ _ _ o _ _
     * 7. Repeat until the word is complete
     */
    private final String word;

    private Hangman(String word) {
        this.word = word;
    }

    public static Hangman start(String filePath) {
        /*
         * Checks  if file exists and if file contains words.
         * Randomly selects a word from the lsit of words within the file.
         * Returns a new Hangman instance with the selected. 
         */
        String word = "";
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            Random random = new Random();

            List<String> wordList = br.lines().toList();
            word = wordList.get(random.nextInt(wordList.size()));
            br.close();
            fr.close();
        } catch (IOException e) {
            System.err.println("File not found. Please create file and try again.");
        }

        if (word == "") {
            throw new IllegalArgumentException("No words in bank. Please restart program and add words.");
        }
        return new Hangman(word);
    }

    private String generateGameDisplay(List<String> guesses) {
        String display = "";
        String temp = this.word;

        // If the letter has not been guessed, replace it with '_' to denote unguessed letter
        for (Character c : this.word.toCharArray()) {
            if (!guesses.contains(c.toString())) {
                temp = temp.replace(c.toString(), "_");
            }
        }
        // Separate each letter or underscore by a space.
        display = String.join(" ", temp.split(""));
        return display;
    }

    private void initGame() {
        /* 
         * Initiates the game. Prints out blank spaces and waits for user input. 
         */
        System.out.println("Enter a letter to guess.");
        System.out.println("Enter 'quit' to leave the game.");
        System.out.println();

        Console game = System.console();
        String input = "";
        String display = generateGameDisplay(new ArrayList<>());
        List<String> guesses = new ArrayList<>();
        String validLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        while (display.contains("_") && !input.equals("quit")) {
            System.out.println(display);
            input = game.readLine("> ");

            if (input.equals("quit")) {
                System.out.println("Exiting game...");
            } else if (input.length() != 1) {
                System.out.println("Invalid entry. Enter only one letter at a time.");
            } else if (input.length() == 1 && validLetters.contains(input.toUpperCase())) {
                guesses.add(input.toUpperCase());
                display = generateGameDisplay(guesses);
            } else {
                System.out.println("Invalid entry. Please try again.");
            }
            System.out.println();

            if (!display.contains("_")) {
                System.out.println("Congratulations! You guessed the word: " + this.word);
            }
        }
        System.out.println();
    }

    private static void list(File db) {
        /*
         * List all existing files within the selected directory.
         */
        String[] filteredFiles = db.list((x, name) -> name.endsWith(".txt"));
        for (int i = 0; i < filteredFiles.length; i++) {
            String name = filteredFiles[i].substring(0, filteredFiles[i].length()-4);
            System.out.println(i+1 +". " + name);
        }
    }

    public static void menu() {
        System.out.println();
        System.out.println("===================== MENU =====================");
        System.out.println("list: List current word categories in database.");
        System.out.println("start <file>: Start game based on selection. ");
        System.out.println("create <file>: Create a new category and add words.");
        System.out.println("edit <file>: Edit a new category and add words.");
        System.out.println("quit: Quit program.");
        System.out.println("================================================");
        System.out.println();
    }

    private static void handleInput(File db) {
        /*
         * Handles user input. 
         */
        Console console = System.console();
        String input = "";

        while (!input.equals("quit")) {
            menu();
            input = console.readLine("> ");

            // List
            if (input.equals("list")) {
                list(db);

            // Start game
            } else if (input.startsWith("start")) {
                String file = input.substring(6);
                String filePath = db.toString() + File.separator + file + ".txt";
                Hangman game = start(filePath);
                game.initGame();

            // Create and edit word bank
            } else if (input.startsWith("create")) {
                String file = input.substring(7);
                String filePath = db.toString() + File.separator + file + ".txt";
                File wb = new File(filePath);
                WBManager wbManage = new WBManager(wb);
                try {
                    wb.createNewFile();
                    wbManage.editWordBank(wb);
                } catch (IOException e) {
                    System.err.println("Error creating and opening new file.");
                }
            
            // Edit existing word bank
            } else if (input.startsWith("edit")) {
                String file = input.substring(5);
                String filePath = db.toString() + File.separator + file + ".txt";
                File wb = new File(filePath);
                WBManager wbManage = new WBManager(wb);
                try {
                    wb.createNewFile();
                    wbManage.editWordBank(wb);
                } catch (IOException e) {
                    System.err.println("Error opening file.");
                }
            }
        }
        System.out.println();
        System.out.println("Thanks for playing!");
    }

    public static void main(String[] args) {
        /*
         * 1. Show menu with options: create new file, start, reset, quit
         */
        // Pass in args for database. Otherwise, use db as default. 
        String dbInput = "db";
        try {
            dbInput = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            dbInput = "db";
        }

        // Check if database exists, if not, create database. 
        File db = new File(dbInput);
        if (!db.exists()) {
            db.mkdir();
        }
        handleInput(db);
    }
}
