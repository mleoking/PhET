/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.translationutility.TULocales;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.userinterface.FindDialog.FindListener;
import edu.colorado.phet.translationutility.util.FontFactory;

/**
 * TranslationPanel is a panel that consists of 3 columns for localizing strings.
 * From left-to-right, the columns are: key, source language value, target language value.
 * The target language value is editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationPanel extends JPanel implements FindListener {
   
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font DEFAULT_FONT = new JLabel().getFont();
    private static final Font TITLE_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD,  DEFAULT_FONT.getSize() + 4 );
    private static final Color SOURCE_BACKGROUND = new JPanel().getBackground();
    private static final Color SELECTION_COLOR = Color.GREEN;
    
    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;
    
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _targetTextAreas; // array of TargetTextArea, the right column of the table ordered from top to bottom
    private ArrayList _findTextAreas; // array of JTextArea, all the JTextAreas that Find will search in
    private String _previousFindText; // text we previously search for in findNext or findPrevious
    private int _previousFindTextAreaIndex; // index into _findTextArea, identifies the JTextArea in which text was found
    private int _previousFindSelectionIndex; // index into a JTextArea's text, identifies where in the JTextArea the text was found
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * SourceTextArea contain a string in the source language.
     * Source strings appear in the middle column of the interface.
     * They are searchable but not editable.
     */
    private static class SourceTextArea extends JTextArea {
        
        public SourceTextArea( String value ) {
            super( value );
            setColumns( TEXT_AREA_COLUMNS );
            setLineWrap( true );
            setWrapStyleWord( true );
            setEditable( false );
            setFocusable( true ); // must be true for Find selection to work
            setBorder( TEXT_AREA_BORDER );
            setBackground( SOURCE_BACKGROUND );
            setSelectionColor( SELECTION_COLOR );
        }
    }
    
    /*
     * TargetTextArea contains a string in the target language, associated with a key.
     * Target strings appear in the right column of the interface.
     * They are searchable and editable.
     * Pressing tab or shift-tab moves focus forward or backward.
     */
    private static class TargetTextArea extends JTextArea {

        private final String _key;

        public static final Action NEXT_FOCUS_ACTION = new AbstractAction( "Move Focus Forwards" ) {
            public void actionPerformed( ActionEvent evt ) {
                ( (Component) evt.getSource() ).transferFocus();
            }
        };
        
        public static final Action PREVIOUS_FOCUS_ACTION = new AbstractAction( "Move Focus Backwards" ) {
            public void actionPerformed( ActionEvent evt ) {
                ( (Component) evt.getSource() ).transferFocusBackward();
            }
        };
        
        public TargetTextArea( String key, String value ) {
            super( value );
            
            _key = key;
            
            setLineWrap( true );
            setWrapStyleWord( true );
            setEditable( true );
            setBorder( TEXT_AREA_BORDER );
            setSelectionColor( SELECTION_COLOR );
            
            // tab or shift-tab will move you to the next or previous text field
            getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "TAB" ), NEXT_FOCUS_ACTION.getValue( Action.NAME ) );
            getInputMap( JComponent.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( "shift TAB" ), PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ) );
            getActionMap().put( NEXT_FOCUS_ACTION.getValue( Action.NAME ), NEXT_FOCUS_ACTION );
            getActionMap().put( PREVIOUS_FOCUS_ACTION.getValue( Action.NAME ), PREVIOUS_FOCUS_ACTION );
        }

        public String getKey() {
            return _key;
        }
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param sourceLocale
     * @param sourceProperties
     * @param targetLocale
     * @param targetProperties
     */
    public TranslationPanel( String projectName, 
            Locale sourceLocale, Properties sourceProperties, 
            Locale targetLocale, Properties targetProperties ) {
        super();
        
        _targetTextAreas = new ArrayList();
        _findTextAreas = new ArrayList();
        _previousFindText = null;
        _previousFindTextAreaIndex = -1;
        _previousFindSelectionIndex = -1;
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right
        int row = 0;
        
        TULocales lc = TULocales.getInstance();
        JLabel projectNameLabel = new JLabel( projectName );
        projectNameLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( projectNameLabel, row, KEY_COLUMN, GridBagConstraints.WEST );
        String sourceText = lc.getName( sourceLocale ) + " (" + sourceLocale + ")";
        JLabel sourceLanguageLabel = new JLabel( sourceText );
        sourceLanguageLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( sourceLanguageLabel, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        String targetName = lc.getName( targetLocale );
        if ( targetName == null ) {
            targetName = TUResources.getString( "label.custom" );
        }
        String targetText = targetName + " (" + targetLocale + ")";
        JLabel targetLanguageLabel = new JLabel( targetText );
        targetLanguageLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( targetLanguageLabel, row, TARGET_COLUMN, GridBagConstraints.WEST );
        row++;
        layout.addComponent( new JSeparator(), row, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
        row++;

        // sort the keys in ascending order
        Enumeration keys = sourceProperties.propertyNames();
        TreeSet sortedSet = new TreeSet();
        while ( keys.hasMoreElements() ) {
            String key = (String) keys.nextElement();
            sortedSet.add( key );
        }
        
        // create the table
        Font sourceFont = FontFactory.createFont( sourceLocale );
        Font targetFont = FontFactory.createFont( targetLocale );
        Iterator i = sortedSet.iterator();
        while ( i.hasNext() ) {

            String key = (String) i.next();
            String sourceValue = sourceProperties.getProperty( key );
            String targetValue = targetProperties.getProperty( key );

            JLabel keyLabel = new JLabel( key );

            JTextArea sourceTextArea = new SourceTextArea( sourceValue );
            sourceTextArea.setFont( sourceFont );

            TargetTextArea targetTextArea = new TargetTextArea( key, targetValue );
            targetTextArea.setFont( targetFont );
            targetTextArea.setColumns( sourceTextArea.getColumns() );
            targetTextArea.setRows( sourceTextArea.getLineCount() );
            _targetTextAreas.add( targetTextArea );

            _findTextAreas.add( sourceTextArea );
            _findTextAreas.add( targetTextArea );
            
            layout.addAnchoredComponent( keyLabel, row, KEY_COLUMN, GridBagConstraints.EAST );
            layout.addComponent( sourceTextArea, row, SOURCE_COLUMN );
            layout.addComponent( targetTextArea, row, TARGET_COLUMN );
            row++;
        }
        
        setFocusTraversalPolicy( new ComponentListFocusPolicy( _targetTextAreas ) );
        setFocusCycleRoot( true ); // enable this container as a FocusCycleRoot, so that custom FocusTraversalPolicy will work
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the target strings.
     * 
     * @return Properties
     */
    public Properties getTargetProperties() {
        Properties properties = new Properties();
        Iterator i = _targetTextAreas.iterator();
        while ( i.hasNext() ) {
            TargetTextArea targetTextArea = (TargetTextArea) i.next();
            String key = targetTextArea.getKey();
            String targetValue = targetTextArea.getText();
            // only add properties that have values
            if ( targetValue != null && targetValue.length() != 0 ) {
                properties.put( key, targetValue );
            }
        }
        return properties;
    }
    
    /**
     * Sets the targets strings.
     * 
     * @param targetProperties
     */
    public void setTargetProperties( Properties targetProperties ) {
        Iterator i = _targetTextAreas.iterator();
        while ( i.hasNext() ) {
            TargetTextArea targetTextArea = (TargetTextArea) i.next();
            String key = targetTextArea.getKey();
            String value = targetProperties.getProperty( key );
            targetTextArea.setText( value );
        }
    }
    
    //----------------------------------------------------------------------------
    // Find
    //----------------------------------------------------------------------------
    
    /**
     * Finds the next occurrence of a string, searching through the source and target text areas.
     * 
     * @param findText
     */
    public void findNext( String findText ) {
        
        boolean found = false;
        int findTextAreaIndex = -1; // index of the JTextArea that contains a match
        int findSelectionIndex = -1; // index of the match within the JTextArea
        
        // start from the top when the text is changed
        if ( !findText.equals( _previousFindText ) ) {
            clearSelection( _previousFindTextAreaIndex );
            _previousFindTextAreaIndex = -1;
            _previousFindSelectionIndex = -1;
            _previousFindText = findText;
        }
        else if ( _previousFindTextAreaIndex != -1 ) {
            // search forwards in the current JTextField for another occurrence of the text
            JTextArea textArea = (JTextArea) _findTextAreas.get( _previousFindTextAreaIndex );
            String text = textArea.getText();
            int matchIndex = text.indexOf( findText, _previousFindSelectionIndex + 1 );
            if ( matchIndex != -1 ) {
                found = true;
                findTextAreaIndex = _previousFindTextAreaIndex;
                findSelectionIndex = matchIndex;
            }
        }
        
        if ( !found ) {
            
            int startTextAreaIndex = _previousFindTextAreaIndex + 1;
            if ( startTextAreaIndex > _findTextAreas.size() ) {
                startTextAreaIndex = 0;
            }
            
            // search forwards from current location to end
            for ( int i = startTextAreaIndex; found == false && i < _findTextAreas.size() - 1; i++ ) {
                JTextArea textArea = (JTextArea) _findTextAreas.get( i );
                String text = textArea.getText();
                int matchIndex = text.indexOf( findText );
                if ( matchIndex != -1 ) {
                    found = true;
                    findTextAreaIndex = i;
                    findSelectionIndex = matchIndex;
                }
            }
            
            // wrap around, search from beginning to current location
            if ( !found ) {
                for ( int i = 0; found == false && i < startTextAreaIndex; i++ ) {
                    JTextArea textArea = (JTextArea) _findTextAreas.get( i );
                    String text = textArea.getText();
                    int matchIndex = text.indexOf( findText );
                    if ( matchIndex != -1 ) {
                        found = true;
                        findTextAreaIndex = i;
                        findSelectionIndex = matchIndex;
                    }
                }
            }
        }
        
        if ( found ) {
            clearSelection( _previousFindTextAreaIndex );
            setSelection( findTextAreaIndex, findSelectionIndex, findText.length() );
            _previousFindTextAreaIndex = findTextAreaIndex;
            _previousFindSelectionIndex = findSelectionIndex;
        }
        else {
            Toolkit.getDefaultToolkit().beep(); 
        }
    }

    /**
     * Finds the previous occurrence of a string, searching through the source and target text areas.
     * 
     * @param findText
     */
    public void findPrevious( String findText ) {
        
        boolean found = false;
        int findTextAreaIndex = -1; // index of the JTextArea that contains a match
        int findSelectionIndex = -1; // index of the match within the JTextArea
        
        // start from the top when the text is changed
        if ( !findText.equals( _previousFindText ) ) {
            clearSelection( _previousFindTextAreaIndex );
            _previousFindTextAreaIndex = -1;
            _previousFindSelectionIndex = -1;
            _previousFindText = findText;
        }
        else if ( _previousFindTextAreaIndex != -1 ) {
            // search backwards in the current JTextArea for another occurrence of the text
            JTextArea textArea = (JTextArea) _findTextAreas.get( _previousFindTextAreaIndex );
            String text = textArea.getText();
            int matchIndex = text.lastIndexOf( findText, _previousFindSelectionIndex - 1 );
            if ( matchIndex != -1 ) {
                found = true;
                findTextAreaIndex = _previousFindTextAreaIndex;
                findSelectionIndex = matchIndex;
            }
        }
        
        if ( !found ) {
            
            int startTextAreaIndex = _previousFindTextAreaIndex - 1;
            if ( startTextAreaIndex < 0 ) {
                startTextAreaIndex = _findTextAreas.size() - 1;
            }
            
            // search backwards from current location to beginning
            for ( int i = startTextAreaIndex; found == false && i >= 0; i-- ) {
                JTextArea textArea = (JTextArea) _findTextAreas.get( i );
                String text = textArea.getText();
                int matchIndex = text.lastIndexOf( findText );
                if ( matchIndex != -1 ) {
                    found = true;
                    findTextAreaIndex = i;
                    findSelectionIndex = matchIndex;
                }
            }
            
            // wrap around, search from end to current location
            if ( !found ) {
                for ( int i = _findTextAreas.size() - 1; found == false && i > startTextAreaIndex; i-- ) {
                    JTextArea textArea = (JTextArea) _findTextAreas.get( i );
                    String text = textArea.getText();
                    int matchIndex = text.lastIndexOf( findText );
                    if ( matchIndex != -1 ) {
                        found = true;
                        findTextAreaIndex = i;
                        findSelectionIndex = matchIndex;
                    }
                }
            }
        }
        
        if ( found ) {
            clearSelection( _previousFindTextAreaIndex );
            setSelection( findTextAreaIndex, findSelectionIndex, findText.length() );
            _previousFindTextAreaIndex = findTextAreaIndex;
            _previousFindSelectionIndex = findSelectionIndex;
        }
        else {
            Toolkit.getDefaultToolkit().beep(); 
        }
    }
    
    /*
     * Clears the selection of a target text area.
     * 
     * @param index index of the target text area
     */
    private void clearSelection( int index ) {
        if ( index >= 0 && index < _findTextAreas.size() ) {
            JTextArea textArea = (JTextArea) _findTextAreas.get( index );
            textArea.select( 0, 0 );
        }
    }
    
    /*
     * Sets the selection of a portion of a target text area.
     * 
     * @param index index of the target text area
     * @param startIndex index of where to start the selection in the text area
     * @param length length of the selection
     */
    private void setSelection( int index, int startIndex, int length ) {
        if ( index >= 0 && index < _findTextAreas.size() ) {
            JTextArea textArea = (JTextArea) _findTextAreas.get( index );
            textArea.requestFocus();
            textArea.select( startIndex, startIndex + length );
        }
    }
}
