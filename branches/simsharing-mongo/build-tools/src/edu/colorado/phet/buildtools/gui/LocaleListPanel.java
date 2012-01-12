package edu.colorado.phet.buildtools.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A panel that holds a list of locales that can be selected
 */
public class LocaleListPanel extends JPanel {
    private LocaleList list;

    public LocaleListPanel( Locale[] locales ) {
        super( new GridLayout( 1, 1 ) );
        list = new LocaleList( locales );
        setMinimumSize( new Dimension( 400, 0 ) );
        JScrollPane holder = new JScrollPane( list );
        holder.setBorder( BorderFactory.createTitledBorder( "Locales" ) );
        holder.setMinimumSize( new Dimension( 70, 0 ) );
        holder.setMaximumSize( new Dimension( 70, 10000 ) );
        holder.setPreferredSize( new Dimension( 70, 400 ) );
        add( holder );
    }

    public Locale getSelectedLocale() {
        return list.getSelectedLocale();
    }
}
