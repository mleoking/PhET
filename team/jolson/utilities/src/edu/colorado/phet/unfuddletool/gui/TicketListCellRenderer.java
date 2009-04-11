package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.util.DateUtils;

public class TicketListCellRenderer extends DefaultListCellRenderer {

    public TicketListCellRenderer() {

    }

    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
        super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

        Ticket ticket = (Ticket) value;

        String dateString = DateUtils.compactDate( ticket.lastUpdateTime() );

        setText( dateString + " " + ticket.toString() );

        switch( ticket.getPriority() ) {
            case 1:
                setBackground( new Color( 0xAA, 0xAA, 0xFF ) );
                break;
            case 2:
                setBackground( new Color( 0xDD, 0xDD, 0xFF ) );
                break;
            case 3:
                setBackground( new Color( 0xFF, 0xFF, 0xFF ) );
                break;
            case 4:
                setBackground( new Color( 0xFF, 0xDD, 0xDD ) );
                break;
            case 5:
                setBackground( new Color( 0xFF, 0xAA, 0xAA ) );
                break;
        }

        if ( ticket.getStatus().equals( "closed" ) ) {
            setForeground( new Color( 0x66, 0x66, 0x66 ) );
        }
        else if ( ticket.getStatus().equals( "resolved" ) ) {
            setForeground( new Color( 0x00, 0x88, 0x00 ) );
        }

        return this;
    }
}
