package edu.colorado.phet.unfuddletool.gui.tabs;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.data.Event;
import edu.colorado.phet.unfuddletool.gui.EventTable;
import edu.colorado.phet.unfuddletool.gui.EventTableModel;
import edu.colorado.phet.unfuddletool.handlers.EventHandler;

public class EventsTab extends JPanel {

    private EventTableModel model;
    private EventTable table;


    public EventsTab() {
        //super( new GridBagLayout() );
        super( new GridLayout( 1, 1 ) );

        model = new EventTableModel();
        EventHandler.getEventHandler().addEventAddListener( new EventHandler.EventAddListener() {
            public void onEventAdded( Event event ) {
                model.addEvent( event );
            }
        } );

        table = new EventTable( model );

        JScrollPane tableScrollPane = new JScrollPane( table );
        table.setFillsViewportHeight( true );
        tableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;

        //add( tableScrollPane, c );
        add( tableScrollPane );
    }
}
