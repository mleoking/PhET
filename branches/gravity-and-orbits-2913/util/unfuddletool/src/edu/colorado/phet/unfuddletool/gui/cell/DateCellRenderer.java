package edu.colorado.phet.unfuddletool.gui.cell;

import java.awt.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import edu.colorado.phet.unfuddletool.util.DateUtils;

public class DateCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int col ) {
        super.getTableCellRendererComponent( table, value, isSelected, cellHasFocus, row, col );

        Date date = (Date) value;

        setText( DateUtils.compactDate( date ) );

        return this;
    }
}
