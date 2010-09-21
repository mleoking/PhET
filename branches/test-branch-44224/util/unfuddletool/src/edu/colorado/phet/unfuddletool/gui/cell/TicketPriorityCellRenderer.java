package edu.colorado.phet.unfuddletool.gui.cell;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketPriorityCellRenderer extends DefaultTableCellRenderer {

    public TicketPriorityCellRenderer() {

    }

    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int col ) {
        super.getTableCellRendererComponent( table, value, isSelected, cellHasFocus, row, col );

        Number priority = (Number) value;

        setText( Ticket.getPrettyPriorityOf( priority.intValue() ) );

        return this;
    }
}