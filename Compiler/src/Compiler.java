import java.io.*;
import java.util.*;

public class Compiler{
    public static void main(String args[]) throws Exception {
        System.out.println("Colin's Compiler");

        //takes in the file
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();

        //creates a list of programs
        ArrayList<ArrayList<Lexeme>> programs = new ArrayList<ArrayList<Lexeme>>();
        ArrayList<String> programs_string = new ArrayList<String>();

        //divides file into programs to be lexed
        String program = "";
        int c;
        String character=null;
        while ((c = br.read()) != -1) {
            character = String.valueOf((char)c);
            program = program + character;
            //makes program as string when it finds a EOP symbol
            if(character.equals("$")) {
                programs_string.add(program);
                program = "";
            }
        }
        //checks to see if there is a missing EOP symbol at EOF
        if(program.length()!=0) {
            System.out.println("Warning Lexer - WARNING: No end of program symbol. Added EOP symbol at EOF");
            program = program + ("$");
            programs_string.add(program);
        }
        //compiles each program
        for(int i=0; i<programs_string.size();i++) {
            //lexer
            System.out.println("INFO  Lexer - Lexing program " + (i+1) + "...");
            lex(programs_string.get(i));

        }
    }

    public static ArrayList<Lexeme> lex(String program) {
        ArrayList<Lexeme> tokens = new ArrayList<Lexeme>();
        //creates a dictionary of tokens
        ArrayList<Lexeme> dictionary = createDictionary();
        //counter of errors
        int errors = 0;
        //declaration of variables concerning token building
        String word="";
        String match = "";
        String match_name = "";
        //iterates through every character of the program
        for (int i = 0; i < program.length(); i++) {
            //checks for comments
            if (((program.charAt(i) == '/') && (program.charAt(i + 1) == '*'))) {
                while ((program.charAt(i) != '*') || (program.charAt(i + 1) != '/')) {
                    i++;
                    if ((i + 1) == program.length()) {
                        System.out.println("ERROR Lexer - Error: No End of Comment by End of Line");
                        System.exit(0);
                    }
                }
                i = i + 2;
            }
            //checks if character is whitespace
            else if(Character.isWhitespace((program.charAt(i)))) {
            }
            //token builder
            else {
                //building word to compare to token list
                word = word + (program.charAt(i));
                //System.out.println("Character: " +program.charAt(i));
                //System.out.println("Word: " + word);
                //System.out.println("Current match: " +match);

                //in dictionary flag
                boolean flag = false;
                //compare word to dictionary of tokens
                for (int x = 0; x < dictionary.size(); x++) {
                    //if the word matches a token
                    if (word.equals(dictionary.get(x).getValue())) {
                        if (word.equals(" ")) {
                            word = "";
                        } else {
                            //sets the match to the current word
                            match = word;
                            match_name = dictionary.get(x).getName();
                            //System.out.println("New Match: " + match);
                        }
                    }
                    //checks to see if there are tokens based on the word
                    if (dictionary.get(x).getValue().startsWith(word)) {
                        flag = true;
                    }
                }
                //if there are no more tokens that can be built from the current word
                if (!flag) {
                    //if there is a match
                    if (match.length() > 0) {
                        //add match to token list/create token
                        tokens.add(new Lexeme(match, match_name));
                        System.out.println("DEBUG Lexer - " + match_name + " [ " + match + " ]");
                        //resets the character position to the last character not counted as a token
                        i = i - (word.length() - match.length());
                        //resets the word and match after adding token
                        match = "";
                        word = "";
                    } else {
                        //this means that the current character is not in the dictionary and is an unrecognized token
                        errors++;
                        System.out.println("ERROR Lexer - Error: Unrecognized Token: " + word);
                        word = "";
                    }
                 //if current character is the last character
                } else if (i + 1 == program.length()) {
                    //checks if the current character finished a token
                    if (word.length() == match.length()) {
                        //adds token to token list
                        //System.out.println("Token Added");
                        tokens.add(new Lexeme(match, match_name));
                        System.out.println("DEBUG Lexer - " + match_name + " [ " + match + " ]");
                    } else
                        i = i - (word.length() - match.length());
                    match = "";
                    word = "";
                }
            }
        }
        System.out.println("INFO  Lexer - Lex complete with " + errors + " errors");
        System.out.println();
        return tokens;
    }

    public static ArrayList<Lexeme> createDictionary() {
        ArrayList<Lexeme> dictionary = new ArrayList<Lexeme>();
        dictionary.add(new Lexeme("L_BRACE","{"));
        dictionary.add(new Lexeme("R_BRACE","}"));
        dictionary.add(new Lexeme("PRINT_OP","print"));
        dictionary.add(new Lexeme("L_PAREN","("));
        dictionary.add(new Lexeme("R_BRACE",")"));
        dictionary.add(new Lexeme("ASSIGN_OP","="));
        dictionary.add(new Lexeme("WHILE_OP","while"));
        dictionary.add(new Lexeme("IF_OP","if"));
        dictionary.add(new Lexeme("QUOTE","\""));
        dictionary.add(new Lexeme("INT_TYPE","int"));
        dictionary.add(new Lexeme("STRING_TYPE","string"));
        dictionary.add(new Lexeme("BOOLEAN_TYPE","boolean"));
        dictionary.add(new Lexeme("SPACE"," "));
        dictionary.add(new Lexeme("CHAR","a"));
        dictionary.add(new Lexeme("CHAR","b"));
        dictionary.add(new Lexeme("CHAR","c"));
        dictionary.add(new Lexeme("CHAR","d"));
        dictionary.add(new Lexeme("CHAR","e"));
        dictionary.add(new Lexeme("CHAR","f"));
        dictionary.add(new Lexeme("CHAR","g"));
        dictionary.add(new Lexeme("CHAR","h"));
        dictionary.add(new Lexeme("CHAR","i"));
        dictionary.add(new Lexeme("CHAR","j"));
        dictionary.add(new Lexeme("CHAR","k"));
        dictionary.add(new Lexeme("CHAR","l"));
        dictionary.add(new Lexeme("CHAR","m"));
        dictionary.add(new Lexeme("CHAR","n"));
        dictionary.add(new Lexeme("CHAR","o"));
        dictionary.add(new Lexeme("CHAR","p"));
        dictionary.add(new Lexeme("CHAR","q"));
        dictionary.add(new Lexeme("CHAR","r"));
        dictionary.add(new Lexeme("CHAR","s"));
        dictionary.add(new Lexeme("CHAR","t"));
        dictionary.add(new Lexeme("CHAR","u"));
        dictionary.add(new Lexeme("CHAR","v"));
        dictionary.add(new Lexeme("CHAR","w"));
        dictionary.add(new Lexeme("CHAR","x"));
        dictionary.add(new Lexeme("CHAR","y"));
        dictionary.add(new Lexeme("CHAR","z"));
        dictionary.add(new Lexeme("DIGIT","0"));
        dictionary.add(new Lexeme("DIGIT","1"));
        dictionary.add(new Lexeme("DIGIT","2"));
        dictionary.add(new Lexeme("DIGIT","3"));
        dictionary.add(new Lexeme("DIGIT","4"));
        dictionary.add(new Lexeme("DIGIT","5"));
        dictionary.add(new Lexeme("DIGIT","6"));
        dictionary.add(new Lexeme("DIGIT","7"));
        dictionary.add(new Lexeme("DIGIT","8"));
        dictionary.add(new Lexeme("DIGIT","9"));
        dictionary.add(new Lexeme("BOOLOP","=="));
        dictionary.add(new Lexeme("BOOLOP","!="));
        dictionary.add(new Lexeme("BOOLVAL","false"));
        dictionary.add(new Lexeme("BOOLVAL","true"));
        dictionary.add(new Lexeme("INTOP","+"));
        dictionary.add(new Lexeme("EOP","$"));
        return dictionary;
    }
}