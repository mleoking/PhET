package edu.colorado.phet.reids.admin.v2;

import javax.swing.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: May 24, 2010
 * Time: 11:47:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class ReportFrame {
    private JFrame frame = new JFrame();
    private TimesheetModel selection;

    public ReportFrame(TimesheetModel selection) {
        this.selection = selection;
        final Hashtable<String, Long> table = new Hashtable<String, Long>();
        for (int i = 0; i < selection.getEntryCount(); i++) {
            Entry e = selection.getEntry(i);
            Long time = table.get(e.getCategory());
            if (time == null) time = 0L;
            time = time + e.getElapsedSeconds();
            table.put(e.getCategory(), time);
        }
//        text += table;

        ArrayList<String> sortedKeys = new ArrayList<String>(table.keySet());
        Collections.sort(sortedKeys, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return -Double.compare(table.get(o1), table.get(o2));//reverse so most used appear first
            }
        });

        String text = "Total time: " + Util.secondsToElapsedTimeString(selection.getTotalTimeSeconds()) + "\n";
        text += "Number of entries: " + selection.getEntryCount() + "\n";
        text += "Numer of categories: " + sortedKeys.size() + "\n";
//        text += selection.toCSV();

        text += "\n";
        for (String key : sortedKeys) {
            System.out.println(key + "\t" + Util.secondsToElapsedTimeString(table.get(key)));
            text += key + "\t" + Util.secondsToElapsedTimeString(table.get(key)) + "\n";
        }
        text+="\n";
        for (String key : sortedKeys) {
            System.out.println(key + "\t" + Util.secondsToElapsedTimeString(table.get(key)));
            text += key + "\t" + Util.secondsToElapsedTimeString(table.get(key)) + "\n";
            Entry[] taskLines = getTaskLines(key);
            for (Entry taskLine : taskLines) {
                if (taskLine.getNotes().trim().length()>0)
                    text+="\t"+Util.secondsToElapsedTimeString(taskLine.getElapsedSeconds())+"\t"+taskLine.getNotes()+"\n";
            }
        }

        frame.setContentPane(new JScrollPane(new JTextArea(text)));
        frame.setSize(800, 600);
    }

    //Gets the tasks completed in the specified category, sorted by time taken
    private Entry[] getTaskLines(String category) {
        Entry[] entries = selection.getEntriesForCategory(category);
        Arrays.sort(entries,new Comparator<Entry>() {
            public int compare(Entry o1, Entry o2) {
                return -Double.compare(o1.getElapsedSeconds(), o2.getElapsedSeconds());
            }
        });
        return entries;
    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }
}
