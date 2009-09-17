/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.translationutility.TULocales;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.userinterface.FindDialog.FindListener;

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
    
    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList<TargetTextArea> _targetTextAreas; // the right column of the table ordered from top to bottom
    private ArrayList<JTextArea> _findTextAreas; // all the JTextAreas that Find will search in
    private String _previousFindText; // text we previously search for in findNext or findPrevious
    private int _previousFindTextAreaIndex; // index into _findTextArea, identifies the JTextArea in which text was found
    private int _previousFindSelectionIndex; // index into a JTextArea's text, identifies where in the JTextArea the text was found
    
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
        
        _targetTextAreas = new ArrayList<TargetTextArea>();
        _findTextAreas = new ArrayList<JTextArea>();
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
        Enumeration<?> keys = sourceProperties.propertyNames();
        TreeSet<String> sortedSet = new TreeSet<String>();
        while ( keys.hasMoreElements() ) {
            String key = (String) keys.nextElement();
            sortedSet.add( key );
        }
        
        // create the table
        Font sourceFont = PhetFont.getPreferredFont( sourceLocale );
        Font targetFont = PhetFont.getPreferredFont( targetLocale );
        Iterator<String> i = sortedSet.iterator();
        while ( i.hasNext() ) {

            String key = i.next();
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
        Iterator<TargetTextArea> i = _targetTextAreas.iterator();
        while ( i.hasNext() ) {
            TargetTextArea targetTextArea = i.next();
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
        Iterator<TargetTextArea> i = _targetTextAreas.iterator();
        while ( i.hasNext() ) {
            TargetTextArea targetTextArea = i.next();
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
