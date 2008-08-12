/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.LanguageCodes;


public class LanguageComboBox extends JComboBox {
    
    public static final String SELECT_NAME = TUResources.getString( "label.selectALanguage" );
    public static final String CUSTOM_NAME = TUResources.getString( "label.custom" );

    private static class LanguageChoice {

        private final String _name;
        private final String _code;

        public LanguageChoice( String name, String code ) {
            _name = name;
            _code = code;
        }

        public String getName() {
            return _name;
        }

        public String getCode() {
            return _code;
        }
    }
    
    private static class LanguageRenderer extends JLabel implements ListCellRenderer {
        
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            
            if ( isSelected ) {
                setForeground( Color.RED );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            // Set text.
            LanguageChoice choice = (LanguageChoice) value;
            String name = choice.getName();
            String code = choice.getCode();
            String text = name;
            if ( code != null ) {
               text = text + " (" + code + ")";
            }
            setText( text );

            return this;
        }
    }
    
    public LanguageComboBox( String sourceLanguageCode ) {
        super();
        
        setRenderer( new LanguageRenderer() );
        
        addItem( new LanguageChoice( SELECT_NAME, null ) );
        
        LanguageCodes lc = LanguageCodes.getInstance();
        String[] names = lc.getSortedNames();
        for ( int i = 0; i < names.length; i++ ) {
            String name = names[i];
            String code = lc.getCode( name );
            // exclude source language from the choices
            if ( !sourceLanguageCode.equals( code ) ) {
                addItem( new LanguageChoice( name, code ) );
            }
        }
        
        addItem( new LanguageChoice( CUSTOM_NAME, null ) );
    }
    
    private boolean isLanguageSelected() {
        LanguageChoice choice = (LanguageChoice) getSelectedItem();
        return ! ( choice.getName().equals( SELECT_NAME ) || choice.getName().equals( CUSTOM_NAME ) );
    }
    
    public String getSelectedCode() {
        String code = null;
        if ( isLanguageSelected() ) {
            LanguageChoice choice = (LanguageChoice) getSelectedItem();
            code = choice.getCode();
        }
        return code;
    }

    public String getSelectedName() {
        LanguageChoice choice = (LanguageChoice) getSelectedItem();
        String name = choice.getName();
        if ( !isLanguageSelected() ) {
            name = null;
        }
        return name;
    }
    
    public boolean isCustomSelected() {
        LanguageChoice choice = (LanguageChoice) getSelectedItem();
        String name = choice.getName();
        return ( name.equals( CUSTOM_NAME ) );
    }
}
