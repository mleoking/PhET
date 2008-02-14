package edu.colorado.phet.reids.admin;

import java.awt.*;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 13, 2008 at 11:40:19 PM
 */
public class ContentPane extends JPanel {
    private SpreadsheetPanel spreadsheetPanel;
    private JScrollPane scrollPane;

    public ContentPane( TimesheetData data, TimesheetApp app ) {
        setLayout( new BorderLayout() );
        spreadsheetPanel = new SpreadsheetPanel( data );
        scrollPane = new JScrollPane( spreadsheetPanel );
        data.addListener( new TimesheetData.Listener() {
            public void timeEntryAdded( TimesheetDataEntry e ) {
                scrollToBottom();
            }

            public void timeChanged() {
            }

            public void timeEntryRemoved( TimesheetDataEntry entry ) {
            }
        } );
        add( scrollPane, BorderLayout.CENTER );
        add( new SummaryPanel( data, app ), BorderLayout.SOUTH );
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                //after other listeners
                scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() );
            }
        } );
    }
}
