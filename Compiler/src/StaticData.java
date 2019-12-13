public class StaticData {
    String tempAddress;
    String var;
    double scope;
    int offset;
    String actualAddress;
    String type;

    public StaticData(String t, String v, double s, int o) {
        tempAddress = t;
        var = v;
        scope = s;
        offset = o;
    }

    public StaticData(String v, double s, int o, String t) {
        var = v;
        scope = s;
        offset = o;
        type = t;
    }

    public StaticData(String v, double s, String t) {
        var = v;
        scope = s;
        type = t;
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

    public double getScope() {
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
