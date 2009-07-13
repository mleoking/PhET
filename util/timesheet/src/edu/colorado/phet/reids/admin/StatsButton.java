package edu.colorado.phet.reids.admin;

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
        boolean overlap=false;
        //check that no times overlap
        for (int i = 0; i < data.getNumEntries(); i++) {
            TimesheetDataEntry entry = data.getEntry(i);
            for (int k = 0; k < data.getNumEntries(); k++) {
                TimesheetDataEntry other = data.getEntry(k);
                if (i != k && entry.overlapsWith(other)) {
                    stats += "Overlap [" + i + "," + k + "]\n";
                    overlap=true;
                }
            }
        }
        if (!overlap){
            stats+="No overlaps found";
        }
        return stats;
    }
}
