package edu.colorado.phet.unfuddletool.gui.cell;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class TicketNumberCellRenderer extends DefaultTableCellRenderer {

    public TicketNumberCellRenderer() {

    }

    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int col ) {
        super.getTableCellRendererComponent( table, value, isSelected, cellHasFocus, row, col );

        Number number = (Number) value;

        setText( "#" + String.valueOf( number ) );

        setHorizontalAlignment( SwingConstants.RIGHT );

        return this;
    }
}
