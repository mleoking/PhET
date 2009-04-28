package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketSummaryCellRenderer extends DefaultTableCellRenderer {

    public TicketSummaryCellRenderer() {

    }

    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int col ) {
        super.getTableCellRendererComponent( table, value, isSelected, cellHasFocus, row, col );

        Ticket ticket = (Ticket) value;

        setText( ticket.getSummary() );

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
            setForeground( new Color( 0x33, 0x33, 0x33 ) );
        }
        else if ( ticket.getStatus().equals( "resolved" ) ) {
            setForeground( new Color( 0x00, 0x88, 0x00 ) );
        }
        else {
            setForeground( new Color( 0x00, 0x00, 0x00 ) );
            Font f = getFont();
            setFont( f.deriveFont( f.getStyle() ^ Font.BOLD ) );
        }

        return this;
    }
}
