package edu.colorado.phet.reids.admin.version1;

import java.awt.*;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 13, 2008 at 11:20:22 PM
 */
public class SpreadsheetPanel extends JPanel {
    private GridBagConstraints gridBagConstraints;
    private TimesheetData data;

    public SpreadsheetPanel( final TimesheetData data ) {
        this.data = data;
        setLayout( new GridBagLayout() );
        gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        rebuildAll();
        data.addListener( new TimesheetData.Adapter() {
            public void timeEntryAppended( TimesheetDataEntry e ) {
                add( new EntryPanel( data, e ), gridBagConstraints );
            }

            public void timeEntryInserted( TimesheetDataEntry e ) {
                rebuildAll();
            }

            public void timeEntryRemoved( TimesheetDataEntry entry ) {
                for ( int i = 0; i < getComponentCount(); i++ ) {
                    EntryPanel ep = (EntryPanel) getComponent( i );
                    if ( ep.getTimesheetDataEntry() == entry ) {
                        remove( ep );
                        i--;
                    }
                }
                revalidateAndRepaint();
            }
        } );
    }

    private void revalidateAndRepaint() {
        revalidate();
        repaint();
    }

    private void rebuildAll() {
        for ( int i = 0; i < data.getNumEntries(); i++ ) {
            add( new EntryPanel( data, data.getEntry( i ) ), gridBagConstraints );
        }
        revalidateAndRepaint();
    }

}
