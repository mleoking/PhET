package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.unfuddletool.TicketListModel;
import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketList extends JList implements ListSelectionListener, TicketListModel.TicketListDisplay {

    public TicketListModel model;

    public UnfuddleToolGUI gui;

    public TicketList( UnfuddleToolGUI ggui ) {
        super();

        gui = ggui;

        model = new TicketListModel();

        model.addDisplay( this );

        /*
        tickets = TicketHandler.getTicketHandler().getTicketListByUpdate();

        Iterator<Ticket> iter = tickets.iterator();

        while ( iter.hasNext() ) {
            Ticket ticket = iter.next();

            model.addElement( ticket );
        }
        */

        setModel( model );

        addListSelectionListener( this );

        setCellRenderer( new TicketListCellRenderer() );
    }

    public void valueChanged( ListSelectionEvent event ) {
        if ( !event.getValueIsAdjusting() ) {
            if ( getSelectedIndex() != -1 ) {
                Ticket ticket = (Ticket) model.getElementAt( getSelectedIndex() );
                gui.displayArea.setText( ticket.toHTMLString() );
            }
        }
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                              RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
    }
}
