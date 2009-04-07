/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.colorado.phet.translationutility.TULocales;
import edu.colorado.phet.translationutility.TUResources;


public class LocaleComboBox extends JComboBox {
    
    public static final String SELECT_NAME = TUResources.getString( "label.selectLocale" );
    public static final String CUSTOM_NAME = TUResources.getString( "label.custom" );

    private static class LocaleChoice {

        private final String _name;
        private final Locale _locale;

        public LocaleChoice( String name, Locale locale ) {
            _name = name;
            _locale = locale;
        }

        public String getName() {
            return _name;
        }
        
        public Locale getLocale() {
            return _locale;
        }
    }
    
    private static class LocaleRenderer extends JLabel implements ListCellRenderer {
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            
            if ( isSelected ) {
                setForeground( Color.RED );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            // Set text.
            LocaleChoice choice = (LocaleChoice) value;
            String name = choice.getName();
            Locale locale = choice.getLocale();
            String text = null;
            if ( locale == null ) {
                text = name;
            }
            else {
                text = name + " (" + locale + ")";
            }
            setText( text );

            return this;
        }
    }
    
    public LocaleComboBox( Locale sourceLocale ) {
        super();
        
        setRenderer( new LocaleRenderer() );
        
        addItem( new LocaleChoice( SELECT_NAME, null ) );
        
        TULocales locales = TULocales.getInstance();
        String[] names = locales.getSortedNames();
        for ( int i = 0; i < names.length; i++ ) {
            String name = names[i];
            Locale locale = locales.getLocale( name );
            // exclude source locale from the choices
            if ( !sourceLocale.equals( locale ) ) {
                addItem( new LocaleChoice( name, locale ) );
            }
        }
        
        addItem( new LocaleChoice( CUSTOM_NAME, null ) );
    }
    
    private boolean isLocaleSelected() {
        LocaleChoice choice = (LocaleChoice) getSelectedItem();
        return ! ( choice.getName().equals( SELECT_NAME ) || choice.getName().equals( CUSTOM_NAME ) );
    }
    
    public Locale getSelectedLocale() {
        Locale locale = null;
        if ( isLocaleSelected() ) {
            LocaleChoice choice = (LocaleChoice) getSelectedItem();
            locale = choice.getLocale();
        }
        return locale;
    }

    public String getSelectedName() {
        LocaleChoice choice = (LocaleChoice) getSelectedItem();
        String name = choice.getName();
        if ( !isLocaleSelected() ) {
            name = null;
        }
        return name;
    }
    
    public boolean isCustomSelected() {
        LocaleChoice choice = (LocaleChoice) getSelectedItem();
        String name = choice.getName();
        return ( name.equals( CUSTOM_NAME ) );
    }
}
