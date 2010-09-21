package edu.colorado.phet.unfuddletool.gui.cell;

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

        return this;
    }
}
