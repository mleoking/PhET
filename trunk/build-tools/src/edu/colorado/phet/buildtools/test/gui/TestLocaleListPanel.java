package edu.colorado.phet.buildtools.test.gui;

import java.awt.*;
import java.util.Locale;

import javax.swing.*;

public class TestLocaleListPanel extends JPanel {
    private TestLocaleList list;

    public TestLocaleListPanel( Locale[] locales ) {
        super( new GridLayout( 1, 1 ) );
        list = new TestLocaleList( locales );
        //list.setMinimumSize( new Dimension( 400, 0 ) );
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
