package edu.colorado.phet.reids.admin.version1;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatsButton extends JButton {
    private final TimesheetData data;

    public StatsButton(TimesheetData data) {
        super("Stats");
        this.data = data;
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SummaryButton.displayText("Stats", getStatsText());
            }
        });
    }

    private String getStatsText() {
        String stats = "";
        stats = getOverlapText()     //checks that no entry is embedded within another
                + getDuplicateText();//checks that no 2 entries share a start time or end time;
        return stats;
    }

    private String getDuplicateText() {
        String stats = "";
        boolean overlap = false;
        //check that no times overlap
        for (int i = 0; i < data.getNumEntries(); i++) {
            TimesheetDataEntry entry = data.getEntry(i);
            for (int k = 0; k < data.getNumEntries(); k++) {
                TimesheetDataEntry other = data.getEntry(k);
                if (i != k && entry.hasSameStartOrSameEnd(other)) {
                    stats += "Duplicate point[" + i + "," + k + "]: "+entry+"\n";
                    overlap = true;
                }
            }
        }
        if (!overlap) {
            stats += "No duplicates found\n";
        }
        return stats;
    }

    private String getOverlapText() {
        String stats = "";
        boolean overlap = false;
        //check that no times overlap
        for (int i = 0; i < data.getNumEntries(); i++) {
            TimesheetDataEntry entry = data.getEntry(i);
            for (int k = 0; k < data.getNumEntries(); k++) {
                TimesheetDataEntry other = data.getEntry(k);
                if (i != k && entry.overlapsWith(other)) {
                    stats += "Overlap [" + i + "," + k + "]\n";
                    overlap = true;
                }
            }
        }
        if (!overlap) {
            stats += "No overlaps found\n";
        }
        return stats;
    }
}
