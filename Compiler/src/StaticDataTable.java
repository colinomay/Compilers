import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class StaticDataTable {
    LinkedList<StaticData> table;
    //addresses for static data begin at hex100(dec256)
    short tempAddress;
    String tempPrefix;
    short offset;
    //used for temp entries such as evaluations that need space but have no var
    //e.g. print (2 + 2 + 2)
    ArrayList<String> temporaryEntries;
    short tempCounter;

    public StaticDataTable() {
        table = new LinkedList<>();
        tempAddress = 256;
        tempPrefix = "T";
        offset = 0;
        temporaryEntries = new ArrayList<String>();
        tempCounter = 0;
    }

    public LinkedList<StaticData> getTable(){
        return table;
    }

    //method to iterate through table and retrieve the static data object
    public StaticData get(String var, int s) {
        if (this.containsEntry(var, s)) {
            StaticData temp = new StaticData(var, s);
            Iterator it = table.iterator();
            while (it.hasNext()) {
                StaticData next = (StaticData)it.next();
                if (next.equals(temp)) {
                    return next;
                }
            }
        }
        return null;
    }

    //gets a static data object based on the var and scope
    public boolean containsEntry(String var, int s) {
        StaticData temp = new StaticData(var, s);
        boolean contains = false;
        for (StaticData k : table) {
            if (k.equals(temp)) {
                contains = true;
            }
        }

        return contains;
    }

    public void put(String t, String v, int s) {
        StaticData sd = new StaticData(t, v, s, offset);
        table.add(sd);
        offset += 1;
    }

    //used for temporary entries
    public void put(String t, int s) {
        StaticData sd = new StaticData(t, tempCounter + "", s, offset);
        table.add(sd);
        tempCounter += 1;
        offset += 1;
    }

    public String getTempAddress() {
        String temp = tempPrefix + tempAddress;
        tempAddress += 1;
        return temp;
    }

    public void print() {
        System.err.println("#> Printing static data table");
        System.err.println("#> Temp/var/scope/offset/actual");
        Iterator it = table.iterator();
        while (it.hasNext()) {
            StaticData sd = (StaticData)it.next();
            System.err.println(sd.getTempAddress() + "   " +
                    sd.getVar() + "   " +
                    sd.getScope() + "   " +
                    sd.getOffset() + "   " +
                    sd.getAddress());
        }
    }
}
