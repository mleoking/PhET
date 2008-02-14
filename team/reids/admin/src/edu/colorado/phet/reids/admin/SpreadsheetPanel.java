package edu.colorado.phet.reids.admin;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Created by: Sam
 * Feb 13, 2008 at 11:20:22 PM
 */
public class SpreadsheetPanel extends JPanel {
    private GridBagConstraints gridBagConstraints;

    public SpreadsheetPanel( final TimesheetData data ) {
        setLayout( new GridBagLayout() );
        gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        for ( int i = 0; i < data.getNumEntries(); i++ ) {
            add( new EntryPanel( data, data.getEntry( i ) ), gridBagConstraints );
        }
        data.addListener( new TimesheetData.Listener() {
            public void timeEntryAdded( TimesheetDataEntry e ) {
                add( new EntryPanel( data, e ), gridBagConstraints );
                notifyListener();
            }

            public void timeChanged() {
            }

            public void timeEntryRemoved( TimesheetDataEntry entry ) {
                for ( int i = 0; i < getComponentCount(); i++ ) {
                    EntryPanel ep = (EntryPanel) getComponent( i );
                    if ( ep.getTimesheetDataEntry() == entry ) {
                        remove( ep );
                        i--;
                    }
                }
            }
        } );
    }

    public static interface Listener {
        void entryPanelAdded();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).entryPanelAdded();
        }
    }
}
