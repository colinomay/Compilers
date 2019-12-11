public class Lexeme {
    String name;
    String value;
    int line;
    int position;

    public Lexeme(String name, String value, int line, int position) {
        this.name = name;
        this.value = value;
        this.position = position;
        this.line = line;
    }

    public Lexeme(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }
    public void setPosition(int position) { this.position = position; }
    public int getPosition() { return position; }
}
