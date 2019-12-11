public class StaticData {
    String tempAddress;
    String var;
    int scope;
    int offset;
    String actualAddress;

    public StaticData(String t, String v, int s, int o) {
        tempAddress = t;
        var = v;
        scope = s;
        offset = o;
    }

    public StaticData(String v, int s) {
        var = v;
        scope = s;
    }

    //returns temp address as hex string
    public String getTempAddress() {
        return tempAddress;
    }

    //returns the offset
    public int getOffset() {
        return offset;
    }

    public String getVar() {
        return var;
    }

    public int getScope() {
        return scope;
    }

    public void setAddress(String a) {
        actualAddress = a;
    }

    public String getAddress() {
        return actualAddress;
    }

    //compares based on var and scope
    public boolean equals(StaticData d) {
        if (this.var.equals(d.getVar()) && this.scope == d.getScope()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void print() {
        System.out.println("tempAddress: " + tempAddress + "\tvar: " + var + "\tscope: " + scope);
    }
}
