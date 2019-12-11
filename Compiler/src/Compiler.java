import java.io.*;
import java.util.*;
public class Compiler {
    public static int errors = 0;
    public static ArrayList<ArrayList<Lexeme>> programs = new ArrayList<ArrayList<Lexeme>>();
    public static ArrayList<String> programs_string = new ArrayList<String>();
    public static boolean quote = false;
    public static SymbolTable root = null;
    public static SymbolTable currentscope= null;
    static double start;

    public static void main(String args[]) throws Exception {
        System.out.println("Colin's Compiler");

        //takes in the file
        File file = new File(args[0]);
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();

        //creates a list of programs
        ArrayList<String> programs_string = new ArrayList<String>();

        //divides file into programs to be lexed
        String program = "";
        int c;
        String character = null;
        while ((c = br.read()) != -1) {
            character = String.valueOf((char) c);
            program = program + character;
            //makes program as string when it finds a EOP symbol
            if (character.equals("$")) {
                programs_string.add(program);
                program = "";
            }
        }
        //checks to see if there is a missing EOP symbol at EOF
        if (program.length() != 0) {
            System.out.println("Warning Lexer - WARNING: No end of program symbol in program " + (programs_string.size() + 1) + ". Added EOP symbol at EOF");
            program = program + ("$");
            programs_string.add(program);
        }
        //compiles each program
        for (int i = 0; i < programs_string.size(); i++) {
            errors=0;
            //lexer
            System.out.println("INFO  Lexer - Lexing program " + (i + 1) + "...");
            programs.add(lex(programs_string.get(i)));
            if (errors > 0) {
                System.out.println("Lexer found errors, stopping Compiler");
                continue;
            }
            System.out.println("INFO  Parser - Parsing program " + (i + 1) + "...");
            SyntaxTreeNode CST = parse(programs.get(i));
            if (errors > 0) {
                System.out.println("Parser found errors, stopping Compiler on program: " + (i + 1));
                continue;
            } else {
                System.out.println("INFO  Parser - CST for program " + (i + 1) + "...");
                transverse(CST, 0);
                System.out.println();
            }
            SyntaxTreeNode AST = ASTbuild(CST, null);
            System.out.println("INFO  Semantic Analysis - AST for program " + (i + 1) + "...");
            transverse(AST, 0);
            semanticAnalysis(AST,null,AST.level);
            if(errors>0) {
                System.out.println("Semantic Analysis found errors, stopping Compiler on program: " + (i + 1));
                continue;
            }
            else {
                System.out.println("INFO  Semantic Analysis complete for program " + (i + 1));
                checkSymbolTable(root);
                System.out.println();
                System.out.println("Symbol Table of program "+(i+1));
                transverseSymbolTable(root);
            }
        }
    }

    public static ArrayList<Lexeme> lex(String program) {
        ArrayList<Lexeme> tokens = new ArrayList<Lexeme>();
        //creates a dictionary of tokens
        ArrayList<Lexeme> dictionary = createDictionary();
        //declaration of variables concerning token building
        String word = "";
        String match = "";
        String match_name = "";
        int line = 1;
        int position = 1;
        //System.out.println(program);
        //iterates through every character of the program
        for (int i = 0; i < program.length(); i++) {
            //checks for comments
            if (((program.charAt(i) == '/') && (program.charAt(i + 1) == '*'))) {
                while ((program.charAt(i) != '*') || (program.charAt(i + 1) != '/')) {
                    i++;
                    position++;
                    if (program.charAt(i) == ('\r')) {
                        errors++;
                        System.out.println("ERROR Lexer - Error: No End of Comment by End of Line");
                        System.exit(0);
                    }
                }
                i = i + 2;
                position = position + 2;
            } else if (program.charAt(i) == ('\"') && match.length() == 0) {
                tokens.add(new Lexeme("QUOTE", "\""));
                System.out.println("DEBUG Lexer - QUOTE  [ \" ]");
                i++;
                position++;
                while (!(program.charAt(i) == ('\"'))) {
                    if (program.charAt(i) == ('\r')) {
                        errors++;
                        System.out.println("ERROR Lexer - Error: No End of Quote by End of Line");
                        System.exit(0);
                    } else if ((program.charAt(i) >= 'a' && program.charAt(i) <= 'z') || Character.isWhitespace(program.charAt(i))) {
                        tokens.add(new Lexeme("CHAR", Character.toString(program.charAt(i))));
                        System.out.println("DEBUG Lexer - CHAR  [ " + program.charAt(i) + " ]");
                    } else {
                        errors++;
                        System.out.println("ERROR Lexer - Error: Unrecognized Character in Quote: " + program.charAt(i));
                        System.exit(0);
                    }
                    i++;
                    position++;
                }
                tokens.add(new Lexeme("QUOTE", "\""));
                System.out.println("DEBUG Lexer - QUOTE  [ \" ]");
                word = "";
                i = i++;
                position++;
            } else if (program.charAt(i) == ('\r') && program.charAt(i + 1) == ('\n')) {
                line++;
                position = 0;
                i = i++;
            }
            //checks if character is whitespace
            else if (Character.isWhitespace((program.charAt(i)))) {
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
                        tokens.add(new Lexeme(match_name, match));
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
                        tokens.add(new Lexeme(match_name, match));
                        System.out.println("DEBUG Lexer - " + match_name + " [ " + match + " ]");
                    } else
                        i = i - (word.length() - match.length());
                    match = "";
                    word = "";
                }
            }
        }
        System.out.println("INFO  Lexer - Lex complete with " + errors + " errors");
        if (errors > 0) {
            tokens.add(new Lexeme("ERRORS", Integer.toString(errors)));
        }
        System.out.println();
        return tokens;
    }

    public static SyntaxTreeNode parse(ArrayList<Lexeme> tokens) {
        ArrayList<Lexeme> program = tokens;
        System.out.println("PARSER: parse()");
        SyntaxTreeNode root = new SyntaxTreeNode("Root");
        program = parseProgram(program, root);
        if (program.size() == 0 && errors == 0) {
            System.out.println("PARSER: Parse completed successfully");
            System.out.println();
        }
        return root;
    }

    public static ArrayList<Lexeme> parseProgram(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        System.out.println("PARSER: parseProgram()");
        SyntaxTreeNode Program = new SyntaxTreeNode("Program", node);
        node.addChild(Program);
        program = parseBlock(program, Program);
        if (program.get(0).getValue().equals("$") && errors == 0 && program.size() > 0) {
            System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
            SyntaxTreeNode EOF = new SyntaxTreeNode("$", Program);
            Program.addChild(EOF);
            program.remove(0);
        } else {
            errors++;
            System.out.println("PARSER: ERROR: Expected \"$\" got " + program.get(0).getValue());
        }
        return program;
    }

    public static ArrayList<Lexeme> parseBlock(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode Block = new SyntaxTreeNode("Block", node);
            node.addChild(Block);
            System.out.println("PARSER: parseBlock()");
            if (program.get(0).getValue().equals("{")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode LeftBracket = new SyntaxTreeNode("{", Block);
                Block.addChild(LeftBracket);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"{\" got " + program.get(0).getValue());
            }
            program = parseStatementList(program, Block);
            if (program.get(0).getValue().equals("}") && errors == 0 && program.size() > 0) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode RightBracket = new SyntaxTreeNode("}", Block);
                Block.addChild(RightBracket);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"}\" got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseStatementList(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode StatementList = new SyntaxTreeNode("StatementList", node);
            node.addChild(StatementList);
            System.out.println("PARSER: parseStatementList()");
            if (program.get(0).getValue().equals("print") || program.get(0).getValue().equals("while") || Character.isLetter(program.get(0).getValue().charAt(0)) ||
                    program.get(0).getValue().equals("int") || program.get(0).getValue().equals("string") || program.get(0).getValue().equals("boolean")
                    || program.get(0).getValue().equals("if") || program.get(0).getValue().equals("{")) {
                program = parseStatement(program, StatementList);
                program = parseStatementList(program, StatementList);
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseStatement(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0) {
            SyntaxTreeNode Statement = new SyntaxTreeNode("Statement", node);
            node.addChild(Statement);
            System.out.println("PARSER: parseStatement()");
            if (program.get(0).getValue().equals("print")) {
                program = parsePrintStatement(program, Statement);
            } else if (program.get(0).getValue().equals("while")) {
                program = parseWhileStatement(program, Statement);
            } else if (program.get(0).getValue().equals("if")) {
                program = parseIfStatement(program, Statement);
            } else if (program.get(0).getValue().equals("int") || program.get(0).getValue().equals("string") || program.get(0).getValue().equals("boolean")) {
                program = parseVarDecl(program, Statement);
            } else if (program.get(0).getValue().equals("{")) {
                program = parseBlock(program, Statement);
            } else if (Character.isLetter(program.get(0).getValue().charAt(0)) && program.get(0).getValue().length() == 1) {
                program = parseAssignmentStatement(program, Statement);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected start of statement. Got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parsePrintStatement(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode PrintStatement = new SyntaxTreeNode("PrintStatement", node);
            node.addChild(PrintStatement);
            System.out.println("PARSER: parsePrintStatement()");
            if (program.get(0).getValue().equals("print")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Print = new SyntaxTreeNode("print", PrintStatement);
                PrintStatement.addChild(Print);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"print\" got " + program.get(0).getValue());
            }
            if (program.get(0).getValue().equals("(")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode LeftParen = new SyntaxTreeNode("(", PrintStatement);
                PrintStatement.addChild(LeftParen);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"(\" got " + program.get(0).getValue());
            }
            program = parseExpr(program, PrintStatement);
            if (program.get(0).getValue().equals(")")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode RightParen = new SyntaxTreeNode(")", PrintStatement);
                PrintStatement.addChild(RightParen);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \")\" got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseWhileStatement(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode WhileStatement = new SyntaxTreeNode("WhileStatement", node);
            node.addChild(WhileStatement);
            System.out.println("PARSER: parseWhileStatement()");
            if (program.get(0).getValue().equals("while")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode While = new SyntaxTreeNode("while", WhileStatement);
                WhileStatement.addChild(While);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"while\" got " + program.get(0).getValue());
            }
            program = parseBooleanExpr(program, WhileStatement);
            program = parseBlock(program, WhileStatement);
        }
        return program;
    }

    public static ArrayList<Lexeme> parseIfStatement(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode IfStatement = new SyntaxTreeNode("IfStatement", node);
            node.addChild(IfStatement);
            System.out.println("PARSER: parseIfStatement()");
            if (program.get(0).getValue().equals("if")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode If = new SyntaxTreeNode("if", IfStatement);
                IfStatement.addChild(If);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"if\" got " + program.get(0).getValue());
            }
            program = parseBooleanExpr(program, IfStatement);
            program = parseBlock(program, IfStatement);
        }
        return program;
    }

    public static ArrayList<Lexeme> parseVarDecl(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode VarDecl = new SyntaxTreeNode("VarDecl", node);
            node.addChild(VarDecl);
            System.out.println("PARSER: parseVarDecl()");
            if (program.get(0).getValue().equals("int") || program.get(0).getValue().equals("string") || program.get(0).getValue().equals("boolean")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Type = new SyntaxTreeNode(program.get(0).getValue(), VarDecl);
                VarDecl.addChild(Type);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"int\" or \"string\" or \"boolean\" got " + program.get(0).getValue());
            }
            if (Character.isLetter(program.get(0).getValue().charAt(0)) && program.get(0).getValue().length() == 1) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode ID = new SyntaxTreeNode(program.get(0).getValue(), VarDecl);
                VarDecl.addChild(ID);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected ID got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseAssignmentStatement(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode AssignmentStatement = new SyntaxTreeNode("AssignmentStatement", node);
            node.addChild(AssignmentStatement);
            System.out.println("PARSER: parseAssignmentStatement()");
            if (program.get(0).getName().equals("ID")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode ID = new SyntaxTreeNode(program.get(0).getValue(), AssignmentStatement);
                AssignmentStatement.addChild(ID);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected ID got " + program.get(0).getValue());
            }
            if (program.get(0).getValue().equals("=")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Assignment = new SyntaxTreeNode("=", AssignmentStatement);
                AssignmentStatement.addChild(Assignment);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"=\" got " + program.get(0).getValue());
            }
            program = parseExpr(program, AssignmentStatement);
        }
        return program;
    }

    public static ArrayList<Lexeme> parseExpr(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode Expr = new SyntaxTreeNode("Expr", node);
            node.addChild(Expr);
            System.out.println("PARSER: parseExpr()");
            if (Character.isDigit(program.get(0).getValue().charAt(0))) {
                program = parseIntExpr(program, Expr);
            } else if (program.get(0).getValue().equals("\"")) {
                program = parseStringExpr(program, Expr);
            } else if (program.get(0).getValue().equals("false") || program.get(0).getValue().equals("true") || program.get(0).getValue().equals("(")) {
                program = parseBooleanExpr(program, Expr);
            } else if (program.get(0).getName().equals("ID")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode ID = new SyntaxTreeNode(program.get(0).getValue(), Expr);
                Expr.addChild(ID);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected ID got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseIntExpr(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0) {
            SyntaxTreeNode IntExpr = new SyntaxTreeNode("IntExpr", node);
            node.addChild(IntExpr);
            System.out.println("PARSER: parseIntExpr()");
            if (Character.isDigit(program.get(0).getValue().charAt(0)) && program.get(1).getValue().equals("+")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Digit = new SyntaxTreeNode(program.get(0).getValue(), IntExpr);
                IntExpr.addChild(Digit);
                program.remove(0);
                program = parseIntop(program, IntExpr);
                program = parseExpr(program, IntExpr);
            } else if (Character.isDigit(program.get(0).getValue().charAt(0))) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Digit = new SyntaxTreeNode(program.get(0).getValue(), IntExpr);
                IntExpr.addChild(Digit);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected DIGIT got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseStringExpr(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode StringExpr = new SyntaxTreeNode("StringExpr", node);
            node.addChild(StringExpr);
            System.out.println("PARSER: parseStringExpr()");
            if (program.get(0).getValue().equals("\"")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode QuoteStart = new SyntaxTreeNode("\"", StringExpr);
                StringExpr.addChild(QuoteStart);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"\"\" got " + program.get(0).getValue());
            }
            program = parseCharList(program, StringExpr);
            if (program.get(0).getValue().equals("\"")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode QuoteEnd = new SyntaxTreeNode("\"", StringExpr);
                StringExpr.addChild(QuoteEnd);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"\"\" got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseBooleanExpr(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode BooleanExpr = new SyntaxTreeNode("BooleanExpr", node);
            node.addChild(BooleanExpr);
            System.out.println("PARSER: parseBooleanExpr()");
            if (program.get(0).getValue().equals("(")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode LeftParen = new SyntaxTreeNode("(", BooleanExpr);
                BooleanExpr.addChild(LeftParen);
                program.remove(0);
                program = parseExpr(program, BooleanExpr);
                program = parseBoolop(program, BooleanExpr);
                program = parseExpr(program, BooleanExpr);
                if (program.get(0).getValue().equals(")")) {
                    System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                    SyntaxTreeNode RightParen = new SyntaxTreeNode(")", BooleanExpr);
                    BooleanExpr.addChild(RightParen);
                    program.remove(0);
                } else {
                    errors++;
                    System.out.println("PARSER: ERROR: Expected \")\" got " + program.get(0).getValue());
                }
            } else if (program.get(0).getValue().equals("true") || program.get(0).getValue().equals("false")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Boolval = new SyntaxTreeNode(program.get(0).getValue(), BooleanExpr);
                BooleanExpr.addChild(Boolval);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected start of Boolean Expr got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseCharList(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode CharList = new SyntaxTreeNode("CharList", node);
            node.addChild(CharList);
            System.out.println("PARSER: parseCharList()");
            if (program.get(0).getName().equals("CHAR")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Char = new SyntaxTreeNode(program.get(0).getValue(), CharList);
                CharList.addChild(Char);
                program.remove(0);
                parseCharList(program, CharList);
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseIntop(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode Intop = new SyntaxTreeNode("Intop", node);
            node.addChild(Intop);
            System.out.println("PARSER: parseIntop()");
            if (program.get(0).getValue().equals("+")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Add = new SyntaxTreeNode("+", Intop);
                Intop.addChild(Add);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"+\" got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static ArrayList<Lexeme> parseBoolop(ArrayList<Lexeme> program, SyntaxTreeNode node) {
        if (errors == 0 && program.size() > 0) {
            SyntaxTreeNode Boolop = new SyntaxTreeNode("Boolop", node);
            node.addChild(Boolop);
            System.out.println("PARSER: parseBoolop()");
            if (program.get(0).getValue().equals("==") || program.get(0).getValue().equals("!=")) {
                System.out.println("PARSER: Matched Expected token " + program.get(0).getName() + " with token " + program.get(0).getValue());
                SyntaxTreeNode Equality = new SyntaxTreeNode(program.get(0).getValue(), Boolop);
                Boolop.addChild(Equality);
                program.remove(0);
            } else {
                errors++;
                System.out.println("PARSER: ERROR: Expected \"==\" or \"!=\" got " + program.get(0).getValue());
            }
        }
        return program;
    }

    public static void transverse(SyntaxTreeNode node, int level) {
        String dash = "";
        for (int j = 0; j < level; j++) {
            dash = dash + "-";
        }
        System.out.println(dash + node.getName());
        if (node.getChildren().size() > 0) {
            for (int i = 0; i < node.getChildren().size(); i++) {
                transverse(node.getChild(i), (level + 1));
            }
        }
    }

    public static SyntaxTreeNode ASTbuild(SyntaxTreeNode CST, SyntaxTreeNode AST) {
        if (AST == null) {
            AST = new SyntaxTreeNode("Root");
        }
        if (CST.getName().equals("Block")) {
            SyntaxTreeNode Block = new SyntaxTreeNode("Block", AST);
            AST.addChild(Block);
            AST = Block;
            AST.level=AST.getParent().level+1;
        } else if (CST.getName().equals("PrintStatement")) {
            SyntaxTreeNode print = new SyntaxTreeNode("Print", AST);
            AST.addChild(print);
            AST = print;
            AST.level=AST.getParent().level;
        } else if (CST.getName().equals("VarDecl")) {
            SyntaxTreeNode vardecl = new SyntaxTreeNode("VarDecl", AST);
            AST.addChild(vardecl);
            AST = vardecl;
            AST.level=AST.getParent().level;
        } else if (CST.getName().equals("AssignmentStatement")) {
            SyntaxTreeNode assign = new SyntaxTreeNode("Assign", AST);
            AST.addChild(assign);
            AST = assign;
            AST.level=AST.getParent().level;
        } else if (CST.getName().equals("IntExpr") && CST.getChildren().size() > 1) {
            SyntaxTreeNode add = new SyntaxTreeNode("Add", AST);
            AST.addChild(add);
            AST = add;
        } else if (CST.getName().equals("WhileStatement")) {
            SyntaxTreeNode While = new SyntaxTreeNode("While", AST);
            AST.addChild(While);
            AST = While;
            AST.level=AST.getParent().level;
        } else if (CST.getName().equals("IfStatement")) {
            SyntaxTreeNode If = new SyntaxTreeNode("If", AST);
            AST.addChild(If);
            AST = If;
            AST.level=AST.getParent().level;
        } else if (CST.getName().equals("CharList") && !quote) {
            SyntaxTreeNode Quote = new SyntaxTreeNode("\"", AST);
            AST.addChild(Quote);
            if(CST.getChildren().size()>0) {
                Quote.setName(Quote.getName()+CST.getChild(0).getName());
                quote = true;
            }
            else {
                Quote.setName(Quote.getName()+"\"");
                quote=false;
            }
            AST = Quote;
            AST.level=AST.getParent().level;
        } else if (CST.getName().equals("CharList") && quote) {
            if (CST.getChildren().size() == 0) {
                AST.setName(AST.getName() + "\"");
                quote = false;
            } else {
                AST.setName(AST.getName() + CST.getChild(0).getName());
            }
        } else if (CST.getName().equals("BooleanExpr")) {
            if (CST.getChildren().size() > 1) {
                if (CST.getChild(2).getChild(0).getName().equals("!=") && CST.getChildren().size() > 1) {
                    SyntaxTreeNode NotEqual = new SyntaxTreeNode("NotEqual", AST);
                    AST.addChild(NotEqual);
                    AST = NotEqual;
                    AST.level=AST.getParent().level;
                }
                if (CST.getChild(2).getChild(0).getName().equals("==") && CST.getChildren().size() > 1) {
                    SyntaxTreeNode IsEqual = new SyntaxTreeNode("IsEqual", AST);
                    AST.addChild(IsEqual);
                    AST = IsEqual;
                    AST.level=AST.getParent().level;
                }
            }
        } else if (((Character.isLetter(CST.getName().charAt(0)) || Character.isDigit(CST.getName().charAt(0))) && CST.getName().length() == 1 && !CST.getParent().getName().equals("CharList")) || (CST.getName().equals("int")) || (CST.getName().equals("string")) || (CST.getName().equals("boolean")) || (CST.getName().equals("true")) || (CST.getName().equals("false"))) {
            SyntaxTreeNode terminal = new SyntaxTreeNode(CST.getName(), AST);
            AST.addChild(terminal);
        }
        for (int i = 0; i < CST.getChildren().size(); i++) {
            ASTbuild(CST.getChild(i), AST);
        }
        return AST;
    }


    private static void semanticAnalysis(SyntaxTreeNode AST, SymbolTable symboltable,int currentlevel) {
        double scope;
        if (errors == 0) {
            if (AST.getName().equals("Block") && symboltable ==null) {
                symboltable = new SymbolTable(0.0);
                currentlevel = AST.level;
            } else if (AST.getName().equals("Block")&&symboltable!=null) {
                //System.out.println("Block level:" + AST.level);
                //System.out.println("Scope Level:"+currentlevel);
                if(AST.getParent().level != currentlevel) {
                    symboltable = symboltable.getParent();
                }
                    scope = (symboltable.getChildren().size()/10.0);
                    SymbolTable newscope = new SymbolTable(symboltable, (((int)(symboltable.getScope())) + 1.0 + scope));
                    symboltable.addChild(newscope);
                    symboltable = newscope;
                    currentlevel = AST.level;
            } else if (AST.getName().equals("VarDecl")) {
                boolean inscope = false;
                for (int i = 0; i < symboltable.getSymbols().size(); i++) {
                    if (symboltable.getSymbol(i).getValue().equals(AST.getChild(1).getName())) {
                        inscope = true;
                    }
                }
                if (inscope == false) {
                    //System.out.println(AST.getChild(0).getName()+ AST.getChild(1).getName());
                    symboltable.addSymbol(new Symbol(AST.getChild(0).getName(), AST.getChild(1).getName()));
                } else {
                    System.out.println("ID: " + AST.getChild(1).getName() + " has already been declared in this scope");
                    errors++;
                }
            } else if (AST.getName().equals("Assign")) {
               String type = compare(AST, symboltable);
            } else if (AST.getName().equals("NotEqual")) {
                String type = compare(AST, symboltable);
            } else if (AST.getName().equals("IsEqual")) {
                String type = compare(AST, symboltable);
            } else if(AST.getName().equals("Add")) {
                String type = compare(AST, symboltable);
            } else if(AST.getName().equals("Print")) {
                start = symboltable.getScope();
                Symbol symbol = findVar(AST.getChild(0),symboltable,null);
                if (symbol == null) {
                    System.out.println("ID: " + AST.getChild(0).getName() + " has not been declared in scope: "+symboltable.getScope());
                    errors++;
                }
            }
            for (int i = 0; i < AST.getChildren().size(); i++) {
                if(AST.getChildren().size()>0) {
                   semanticAnalysis(AST.getChild(i), symboltable,currentlevel);
                }
            }
            if(symboltable!=null){
                //System.out.println(AST.getName()+symboltable.getScope());
                root = symboltable;
            }
        }
    }

    public static void checkSymbolTable(SymbolTable symboltable) {
        for(int j=0; j<symboltable.getSymbols().size();j++) {
            if(symboltable.getSymbol(j).getUsed()==false){
                if(symboltable.getSymbol(j).getInitialized()==false){
                    System.out.println("Warning: ID:<"+symboltable.getSymbol(j).getValue()+"> is not initialized and not used in scope: "+(symboltable.getScope()));
                } else if(symboltable.getSymbol(j).getInitialized()==true){
                    System.out.println("Warning: ID:<"+symboltable.getSymbol(j).getValue()+"> is initialized but is not used in scope: "+(symboltable.getScope()));
                }
            }
        }
        if (symboltable.getChildren().size() > 0) {
            for (int i = 0; i < symboltable.getChildren().size(); i++) {
                checkSymbolTable(symboltable.getChild(i));
            }
        }
    }

    public static void transverseSymbolTable(SymbolTable symboltable) {
        for(int j=0; j<symboltable.getSymbols().size();j++) {
            System.out.println("Type: <"+symboltable.getSymbol(j).getType()+">  Name: <"+symboltable.getSymbol(j).getValue()+">  Scope: <"+symboltable.getScope()+">");
        }
        if (symboltable.getChildren().size() > 0) {
            for (int i = 0; i < symboltable.getChildren().size(); i++) {
                transverseSymbolTable(symboltable.getChild(i));
            }
        }
    }

    public static Symbol findVar(SyntaxTreeNode AST, SymbolTable symboltable, Symbol var) {
        if (var != null) {
            return var;
        } else {
            for (int i = 0; i < symboltable.getSymbols().size(); i++) {
                if (symboltable.getSymbol(i).getValue().equals(AST.getName())) {
                    if (AST.getParent().getName().equals("Assign")) {
                        symboltable.getSymbol(i).setInitialized(true);
                    }
                    else if(AST.getParent().getName().equals("Print")||AST.getParent().getName().equals("IsEqual")||AST.getParent().getName().equals("Add")||AST.getParent().getName().equals("NotEqual")) {
                        if(symboltable.getSymbol(i).getInitialized()==false) {
                            System.out.println("Warning: ID: <"+symboltable.getSymbol(i).getValue()+"> is being used without be initialized");
                        }
                        symboltable.getSymbol(i).setUsed(true);
                    }
                    var = symboltable.getSymbol(i);
                }
            }
            if (symboltable.getParent() != null && var == null) {
                var = findVar(AST, symboltable.getParent(), var);
            }
            return var;
        }
    }

    public static String compare(SyntaxTreeNode node, SymbolTable symboltable) {
        String type=null;
        String left=null;
        String right=null;
        if (errors == 0) {
            for (int i = 0; i < node.getChildren().size(); i++) {
                if(i==0) {
                    if (node.getChild(i).getName().length() == 1 && Character.isLetter(node.getChild(i).getName().charAt(0))) {
                        Symbol var = null;
                        var = findVar(node.getChild(i), symboltable, var);
                        if (var == null) {
                            System.out.println("ID: " + node.getChild(i).getName() + " has not been declared");
                            errors++;
                        }
                        else {
                            left = var.getType();

                        }
                    } else if (node.getChild(i).getName().length() == 1 && Character.isDigit(node.getChild(i).getName().charAt(0))) {
                        left = "int";
                    } else if (node.getChild(i).getName().equals("false") || node.getChild(i).getName().equals("true")) {
                        left = "boolean";
                    } else if (node.getChild(i).getName().charAt(0) == '\"') {
                        left = "string";
                    }
                    else {
                        left = compare(node.getChild(0),symboltable);
                    }
                }
                else if(i==1){
                        if (node.getChild(i).getName().length() == 1 && Character.isLetter(node.getChild(i).getName().charAt(0))) {
                            Symbol var = null;
                            var = findVar(node.getChild(i), symboltable, var);
                            if (var == null) {
                                System.out.println("ID: " + node.getChild(i).getName() + " has not been declared in scope: "+symboltable.getScope());
                                errors++;
                            }
                            else {
                                right = var.getType();
                            }
                        } else if (node.getChild(i).getName().length() == 1 && Character.isDigit(node.getChild(i).getName().charAt(0))) {
                            right = "int";
                        } else if (node.getChild(i).getName().equals("false") || node.getChild(i).getName().equals("true")) {
                            right = "boolean";
                        } else if (node.getChild(i).getName().charAt(0) == '\"') {
                            right = "string";
                        } else {
                            right = compare(node.getChild(1), symboltable);
                        }
                }
            }
            if (left==null || right==null) {
                errors++;
            } else if(left.equals(right)) {
                if(node.getName().equals("NotEqual")||node.getName().equals("IsEqual")) {
                    type="boolean";
                }
                else {
                    type=left;
                }
            } else {
                System.out.println("Type Mismatch with Operation: \""+node.getName()+"\". Expecting: "+left+". Got: "+right);
                errors++;
            }
        }
        return type;
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
        dictionary.add(new Lexeme("ID","a"));
        dictionary.add(new Lexeme("ID","b"));
        dictionary.add(new Lexeme("ID","c"));
        dictionary.add(new Lexeme("ID","d"));
        dictionary.add(new Lexeme("ID","e"));
        dictionary.add(new Lexeme("ID","f"));
        dictionary.add(new Lexeme("ID","g"));
        dictionary.add(new Lexeme("ID","h"));
        dictionary.add(new Lexeme("ID","i"));
        dictionary.add(new Lexeme("ID","j"));
        dictionary.add(new Lexeme("ID","k"));
        dictionary.add(new Lexeme("ID","l"));
        dictionary.add(new Lexeme("ID","m"));
        dictionary.add(new Lexeme("ID","n"));
        dictionary.add(new Lexeme("ID","o"));
        dictionary.add(new Lexeme("ID","p"));
        dictionary.add(new Lexeme("ID","q"));
        dictionary.add(new Lexeme("ID","r"));
        dictionary.add(new Lexeme("ID","s"));
        dictionary.add(new Lexeme("ID","t"));
        dictionary.add(new Lexeme("ID","u"));
        dictionary.add(new Lexeme("ID","v"));
        dictionary.add(new Lexeme("ID","w"));
        dictionary.add(new Lexeme("ID","x"));
        dictionary.add(new Lexeme("ID","y"));
        dictionary.add(new Lexeme("ID","z"));
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

    public static String codeGen(SyntaxTreeNode AST, SymbolTable symbolTable) {
        String code = "";

    }
}