public class Symbol {

    private String type;
    private String value;
    private boolean initialized;
    private boolean used;

    public Symbol(String type, String value) {
        this.type = type;
        this.value = value;
        used = false;
        initialized = false;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean getUsed() {
        return used;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean getInitialized() {
        return initialized;
    }

    public boolean equals(String s) {
        if (s.equals(this.value)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
