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
            //makes program when it finds a EOP symbol
            if(character.equals("$")) {
                programs_string.add(program);
                program = "";
            }
        }
        if(program.length()!=0) {
            System.out.println("Warning Lexer - WARNING: No end of program symbol. Added EOP symbol at EOF");
            program = program + ("$");
            programs_string.add(program);
        }

        for(int i=0; i<programs_string.size();i++) {
            System.out.println("INFO  Lexer - Lexing program " + (i+1) + "...");
            lex(programs_string.get(i));

        }
    }

    public static ArrayList<Lexeme> lex(String program) {
        ArrayList<Lexeme> tokens = new ArrayList<Lexeme>();
        //creates a dictionary of tokens
        int errors = 0;
        ArrayList<Lexeme> dictionary = createDictionary();
        String word="";
        String match = "";
        String match_name = "";
        for (int i = 0; i < program.length(); i++) {
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
            else if(Character.isWhitespace((program.charAt(i)))) {
            }
            else {
                word = word + (program.charAt(i));
                //System.out.println("Character: " +program.charAt(i));
                //System.out.println("Word: " + word);
                //System.out.println("Current match: " +match);
                boolean flag = false;
                for (int x = 0; x < dictionary.size(); x++) {
                    if (word.equals(dictionary.get(x).getValue())) {
                        if (word.equals(" ")) {
                            word = "";
                        } else {
                            match = word;
                            match_name = dictionary.get(x).getName();
                            //System.out.println("New Match: " + match);
                        }
                    }
                    if (dictionary.get(x).getValue().startsWith(word)) {
                        flag = true;
                    }
                }
                if (!flag) {
                    if (match.length() > 0) {
                        tokens.add(new Lexeme(match, match_name));
                        System.out.println("DEBUG Lexer - " + match_name + " [ " + match + " ]");
                        i = i - (word.length() - match.length());
                        match = "";
                        word = "";
                    } else {
                        errors++;
                        System.out.println("ERROR Lexer - Error: Unrecognized Token: " + word);
                        word = "";
                    }
                } else if (i + 1 == program.length()) {
                    if (word.length() == match.length()) {
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