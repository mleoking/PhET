package edu.colorado.phet.coreadditions.plaf;
import java.util.*;
import javax.swing.*;

public class PlafTest {
    public static void main(String[] arg) {
        UIDefaults ui = UIManager.getLookAndFeelDefaults();
        ArrayList al = new ArrayList(ui.keySet());
        Collections.sort(al);
        Iterator it = al.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            System.out.println(key + " = " + ui.get(key));
        }
    }

}