import java.io.*;
import java.util.*;

public class Compiler{
    public static void main(String args[]) throws Exception {
        System.out.println("Colin's Compiler");
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String ln;
        ArrayList<Lexeme> dictionary = createDictionary();
        ArrayList<ArrayList<String>> programs = new ArrayList<ArrayList<String>>();
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<String> lines = new ArrayList<String>();
        String temp = "";
        String match = "";
        String match_name = "";
        int position=0;
        while ((ln = br.readLine()) != null) {
            lines.add(ln);
        }
        System.out.println("INFO  Lexer - Lexing program " + (programs.size()+1) + "...");
        for (int i = 0; i < lines.size(); i++) {
            char[] line = lines.get(i).toString().toLowerCase().toCharArray();
            //System.out.println("Line Size: " + line.length);
            for (int j = 0; j < line.length; j++) {
                temp = temp + line[j];
                //System.out.println("Character: " +line[j]);
                //System.out.println("Temp: " + temp);
                //System.out.println("Current match: " +match);
                boolean flag = false;
                for(int x = 0; x < dictionary.size(); x++) {
                    if (temp.equals(dictionary.get(x).getValue())) {
                        if (temp.equals(" ")) {
                            temp = "";
                        } else {
                            match = temp;
                            match_name = dictionary.get(x).getName();
                            //System.out.println("New Match: " + match);
                            if (temp.equals("$")) {
                                programs.add(tokens);
                            }
                        }
                    }
                    if (dictionary.get(x).getValue().startsWith(temp)) {
                        flag = true;
                    }
                }
                if(!flag) {
                    if (match.length() > 0) {
                        tokens.add(match);
                        System.out.println("DEBUG Lexer - " + match_name + " [ " + match + " ] found at (" + (i+1) + ":" + (j - match.length() + 1) + ")");
                        j = j - (temp.length() - match.length());
                        match = "";
                        temp = "";
                    } else {
                        System.out.println("ERROR Lexer - Error:" + (i + 1) + ":" + (j + 1) + " Unrecognized Token: " + temp);
                    }
                }
                else if(j+1 == line.length) {
                    if(temp.length() == match.length()) {
                        //System.out.println("Token Added");
                        tokens.add(match);
                        System.out.println("DEBUG Lexer - " +  match_name + " [ " + match + " ] found at (" + (i+1) + ":" + (position+1) + ")");
                    }
                    else
                        j = j - (temp.length() - match.length());
                    match = "";
                    temp = "";
                }
            }
        }
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
        dictionary.add(new Lexeme("QUOTE","'"));
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