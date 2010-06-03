package edu.colorado.phet.reids.admin.version1;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
        scrollPane.getVerticalScrollBar().setUnitIncrement( 40 );
        spreadsheetPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                scrollToBottom();
            }
        } );
        add( scrollPane, BorderLayout.CENTER );
        add( new SummaryPanel( data, app ), BorderLayout.SOUTH );
        add( new ControlPanel( data, app ), BorderLayout.WEST );
        scrollToBottom();
    }

    private void scrollToBottom() {
        scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() );
    }
}
