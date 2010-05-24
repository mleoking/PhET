package edu.colorado.phet.reids.admin.v2;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 24, 2010
 * Time: 11:47:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportFrame {
    private JFrame frame = new JFrame();

    public ReportFrame(TimesheetModel selection) {
        final Hashtable<String, Long> table = new Hashtable<String, Long>();
        for (int i = 0; i < selection.getEntryCount(); i++) {
            Entry e = selection.getEntry(i);
            Long time = table.get(e.getCategory());
            if (time == null) time = 0L;
            time = time + e.getElapsedSeconds();
            table.put(e.getCategory(), time);
        }
//        text += table;

        ArrayList<String> keys = new ArrayList<String>(table.keySet());
        Collections.sort(keys, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return -Double.compare(table.get(o1), table.get(o2));//reverse so most used appear first
            }
        });

        String text = "Total time: " + Util.secondsToElapsedTimeString(selection.getTotalTimeSeconds()) + "\n";
        text += "Number of entries: " + selection.getEntryCount() + "\n";
        text += "Numer of categories: " + keys.size() + "\n";
//        text += selection.toCSV();

        text += "\n";
        for (String key : keys) {
            System.out.println(key + ": " + Util.secondsToElapsedTimeString(table.get(key)));
            text += key + ": " + Util.secondsToElapsedTimeString(table.get(key)) + "\n";
        }

        frame.setContentPane(new JScrollPane(new JTextArea(text)));
        frame.setSize(800, 600);
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}
