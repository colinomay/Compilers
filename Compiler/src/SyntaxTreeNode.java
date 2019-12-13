import java.util.*;

public class SyntaxTreeNode {
    private String name;
    private LinkedList<SyntaxTreeNode> children;
    private SyntaxTreeNode parent;
    double level=0;

    public SyntaxTreeNode(String name, SyntaxTreeNode parent) {
        this.name = name;
        this.parent = parent;
        children = new LinkedList<SyntaxTreeNode>();
    }

    public SyntaxTreeNode(String name) {
        this.name = name;
        children = new LinkedList<SyntaxTreeNode>();
    }

    public void addChild(SyntaxTreeNode child) {
        this.children.add(child);
    }

    public void addChildren(List<SyntaxTreeNode> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public List<SyntaxTreeNode> getChildren() {
        return children;
    }

    public SyntaxTreeNode getChild(int index) {
        return children.get(index);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(SyntaxTreeNode parent) {
        this.parent=parent;
    }

    public SyntaxTreeNode getParent() {
        return parent;
    }
}
