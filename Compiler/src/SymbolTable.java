import java.util.*;

public class SymbolTable {
    public SymbolTable parent;
    private ArrayList<Symbol> symbols;
    private double scope;
    private LinkedList<SymbolTable> children;

    public SymbolTable(ArrayList<Symbol> symbols, SymbolTable parent) {
        this.parent = parent;
        this.symbols = symbols;
        symbols = new ArrayList<>();
        children = new LinkedList<>();
    }

    public SymbolTable() {
        symbols = new ArrayList<>();
        children = new LinkedList<>();
    }

    public SymbolTable(SymbolTable parent, double scope) {
        symbols = new ArrayList<>();
        children = new LinkedList<>();
        this.parent=parent;
        this.scope=scope;
    }

    public SymbolTable(Symbol symbol, SymbolTable parent) {
        symbols = new ArrayList<>();
        symbols.add(symbol);
        this.parent = parent;
        children = new LinkedList<>();
    }

    public SymbolTable(ArrayList<Symbol> symbols) {
        symbols = new ArrayList<>();
        this.symbols = symbols;
        children = new LinkedList<>();
    }

    public SymbolTable(Symbol symbol) {
        symbols = new ArrayList<>();
        symbols.add(symbol);
        children = new LinkedList<>();
    }

    public SymbolTable(double scope) {
        this.scope=scope;
        symbols = new ArrayList<>();
        children = new LinkedList<>();

    }

    public double getScope() {
        return scope;
    }

    public void setScope(double scope) {
        this.scope = scope;
    }

    public SymbolTable getParent() {
        return parent;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(ArrayList<Symbol> symbols) {
        this.symbols = symbols;
    }

    public LinkedList<SymbolTable> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<SymbolTable> children) {
        this.children = children;
    }

    public void addChild(SymbolTable symboltable) {
        children.add(symboltable);
    }

    public Symbol getSymbol(int index) {
        return symbols.get(index);
    }

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }
    public SymbolTable getChild(int index) {
        return children.get(index);
    }
}

