// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.translationutility.TUStrings;

/**
 * Combo box that shows a list of locales supported by Translation Utility.
 * At the bottom of the list is a "CUSTOM" entry, which allows the user to
 * enter a locale that's not in the list.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class LocaleComboBox extends JComboBox {
    
    private static class LocaleChoice {

        private final String name;
        private final Locale locale;

        public LocaleChoice( String name, Locale locale ) {
            this.name = name;
            this.locale = locale;
        }

        public String getName() {
            return name;
        }
        
        public Locale getLocale() {
            return locale;
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
        
        addItem( new LocaleChoice( TUStrings.SELECT_LOCALE_LABEL, null ) );
        
        PhetLocales locales = PhetLocales.getInstance();
        String[] names = locales.getSortedNames();
        for ( int i = 0; i < names.length; i++ ) {
            String name = names[i];
            Locale locale = locales.getLocale( name );
            // exclude source locale from the choices
            if ( !sourceLocale.equals( locale ) ) {
                addItem( new LocaleChoice( name, locale ) );
            }
        }
        
        addItem( new LocaleChoice( TUStrings.CUSTOM_LOCALE_LABEL, null ) );
    }
    
    private boolean isLocaleSelected() {
        LocaleChoice choice = (LocaleChoice) getSelectedItem();
        return ! ( choice.getName().equals( TUStrings.SELECT_LOCALE_LABEL ) || choice.getName().equals( TUStrings.CUSTOM_LOCALE_LABEL ) );
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
        return ( name.equals( TUStrings.CUSTOM_LOCALE_LABEL ) );
    }
}
