import java.util.ArrayList;
import java.util.Iterator;

public class StaticDataTable {
    ArrayList<StaticData> table;

    public StaticDataTable() {
        table = new ArrayList<StaticData>();
    }

    public ArrayList<StaticData> getTable(){
        return this.table;
    }

    public void add(StaticData d) {
        this.table.add(d);
    }

    //method to iterate through table and retrieve the static data object
    public StaticData get(String var, double s, String t) {
        if (containsEntry(var, s, t)) {
            StaticData temp = new StaticData(var, s, t);
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
    public boolean containsEntry(String var, double s, String t) {
        StaticData temp = new StaticData(var, s, t);
        boolean contains = false;
        for (StaticData k : table) {
            if (k.equals(temp)) {
                contains = true;
            }
        }

        return contains;
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
