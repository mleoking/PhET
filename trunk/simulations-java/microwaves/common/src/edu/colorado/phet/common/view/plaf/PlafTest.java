package edu.colorado.phet.common.view.plaf;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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