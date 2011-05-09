// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.dialogs.CreditsDialog;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.translationutility.TUStrings;
import edu.colorado.phet.translationutility.userinterface.FindDialog.FindListener;

/**
 * TranslationPanel is a panel that consists of 3 columns for localizing strings.
 * From left-to-right, the columns are: key, source language value, target language value.
 * The target language value is editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class TranslationPanel extends JPanel implements FindListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Font DEFAULT_FONT = new JLabel().getFont();
    private static final Font TITLE_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD,  DEFAULT_FONT.getSize() + 4 );

    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;

    private static final Logger LOGGER = Logger.getLogger( TranslationPanel.class.getCanonicalName() );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final ArrayList<TargetTextPanel> targetTextPanels; // the right column of the table ordered from top to bottom
    private final ArrayList<JTextArea> findTextAreas; // all the JTextAreas that Find will search in
    private String previousFindText; // text we previously search for in findNext or findPrevious
    private int previousFindTextAreaIndex; // index into _findTextArea, identifies the JTextArea in which text was found
    private int previousFindSelectionIndex; // index into a JTextArea's text, identifies where in the JTextArea the text was found

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
    public TranslationPanel( JFrame parent, String projectName,
            Locale sourceLocale, Properties sourceProperties,
            Locale targetLocale, Properties targetProperties ) {
        super();

        targetTextPanels = new ArrayList<TargetTextPanel>();
        findTextAreas = new ArrayList<JTextArea>();
        previousFindText = null;
        previousFindTextAreaIndex = -1;
        previousFindSelectionIndex = -1;

        // get locale-specific fonts
        final Font sourceFont = PhetFont.getPreferredFont( sourceLocale );
        final Font targetFont = PhetFont.getPreferredFont( targetLocale );
        LOGGER.fine( "TranslationPanel, sourceFont=" + sourceFont + " (" + sourceFont.getFontName() + ")" );
        LOGGER.fine( "TranslationPanel, targetFont=" + targetFont + " (" + targetFont.getFontName() + ")");

        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right
        int row = 0;

        // column headings
        PhetLocales lc = PhetLocales.getInstance();
        JLabel projectNameLabel = new JLabel( projectName );
        projectNameLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( projectNameLabel, row, KEY_COLUMN, GridBagConstraints.WEST );
        String sourceText = lc.getName( sourceLocale ) + " (" + sourceLocale + ")";
        JLabel sourceLanguageLabel = new JLabel( sourceText );
        sourceLanguageLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( sourceLanguageLabel, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        String targetName = lc.getName( targetLocale );
        if ( targetName == null ) {
            targetName = TUStrings.CUSTOM_LOCALE_LABEL;
        }
        String targetText = targetName + " (" + targetLocale + ")";
        JLabel targetLanguageLabel = new JLabel( targetText );
        targetLanguageLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( targetLanguageLabel, row, TARGET_COLUMN, GridBagConstraints.WEST );
        row++;

        // font names
        JLabel sourceFontLabel = new JLabel( "font: " + sourceFont.getFontName() );
        layout.addAnchoredComponent( sourceFontLabel, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        JLabel targetFontName = new JLabel( "font: " + targetFont.getFontName() );
        layout.addAnchoredComponent( targetFontName, row, TARGET_COLUMN, GridBagConstraints.WEST );
        row++;

        // separator
        layout.addComponent( new JSeparator(), row, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
        row++;

        // sort the keys in ascending order
        ArrayList<String> keysList = new ArrayList<String>();
        for ( Object key : sourceProperties.keySet() ) {
            if ( key instanceof String ) {
                keysList.add( (String) key );
            }
        }
        Collections.sort( keysList );

        // Move translation credits to the top
        keysList.remove( CreditsDialog.TRANSLATION_CREDITS_KEY );
        keysList.add( 0, CreditsDialog.TRANSLATION_CREDITS_KEY );

        // create the table
        ArrayList<Component> targetTextAreas = new ArrayList<Component>();
        for ( String key : keysList) {

            String sourceValue = sourceProperties.getProperty( key );
            String targetValue = targetProperties.getProperty( key );

            JLabel keyLabel = new KeyLabel( key );

            JTextArea sourceTextArea = new SourceTextArea( sourceValue );
            sourceTextArea.setFont( sourceFont );

            TargetTextPanel targetTextPanel = new TargetTextPanel( key, sourceValue, sourceLocale, sourceFont, targetValue, targetLocale, targetFont );
            targetTextPanel.setFont( targetFont );
            targetTextPanels.add( targetTextPanel );
            targetTextAreas.add( targetTextPanel.getTextArea() );
            if ( key.equals( CreditsDialog.TRANSLATION_CREDITS_KEY ) ) {
                // put a Help button next to translation credits
                targetTextPanel.setHelpText( TUStrings.HELP_TRANSLATION_CREDITS );
                // add a preview button, for previewing HTML
                targetTextPanel.setPreviewIconVisible( true );
            }

            findTextAreas.add( sourceTextArea );
            findTextAreas.add( targetTextPanel.getTextArea() );

            layout.addAnchoredComponent( keyLabel, row, KEY_COLUMN, GridBagConstraints.EAST );
            layout.addComponent( sourceTextArea, row, SOURCE_COLUMN );
            layout.addComponent( targetTextPanel, row, TARGET_COLUMN );
            row++;
        }

        setFocusTraversalPolicy( new ComponentListFocusPolicy( targetTextAreas ) );
        setFocusCycleRoot( true ); // enable this container as a FocusCycleRoot, so that custom FocusTraversalPolicy will work

        validateTargets();
    }

    public boolean validateTargets() {
        boolean valid = true;
        for ( TargetTextPanel target : targetTextPanels ) {
            if ( !target.isValidValue() ) {
                valid = false;
            }
        }
        return valid;
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
        for ( TargetTextPanel panel : targetTextPanels ) {
            TargetTextArea textArea = panel.getTextArea();
            String key = textArea.getKey();
            String targetValue = textArea.getText();
            // only add properties that have values
            if ( targetValue != null && targetValue.length() != 0 ) {
                properties.put( key, targetValue );
            }
        }
        addKSUCredits( properties );
        return properties;
    }

    /*
     *  If the translation credits are "KSU", add a special KSU credits string.
     *  This modifies the input Properties.
     */
    private void addKSUCredits( Properties properties ) {
        String translationCredits = properties.getProperty( CreditsDialog.TRANSLATION_CREDITS_KEY );
        if ( translationCredits != null && translationCredits.trim().equals( "KSU" ) ) {
            properties.put( CreditsDialog.KSU_CREDITS_KEY, TUStrings.KSU_CREDITS );
        }
    }

    /**
     * Sets the targets strings.
     *
     * @param targetProperties
     */
    public void setTargetProperties( Properties targetProperties ) {
        for ( TargetTextPanel panel : targetTextPanels ) {
            TargetTextArea textArea = panel.getTextArea();
            String key = textArea.getKey();
            String value = targetProperties.getProperty( key );
            textArea.setText( value );
        }
        validateTargets();
        markAllSaved();
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
        if ( !findText.equals( previousFindText ) ) {
            clearSelection( previousFindTextAreaIndex );
            previousFindTextAreaIndex = -1;
            previousFindSelectionIndex = -1;
            previousFindText = findText;
        }
        else if ( previousFindTextAreaIndex != -1 ) {
            // search forwards in the current JTextField for another occurrence of the text
            JTextArea textArea = (JTextArea) findTextAreas.get( previousFindTextAreaIndex );
            String text = textArea.getText();
            int matchIndex = text.indexOf( findText, previousFindSelectionIndex + 1 );
            if ( matchIndex != -1 ) {
                found = true;
                findTextAreaIndex = previousFindTextAreaIndex;
                findSelectionIndex = matchIndex;
            }
        }

        if ( !found ) {

            int startTextAreaIndex = previousFindTextAreaIndex + 1;
            if ( startTextAreaIndex > findTextAreas.size() ) {
                startTextAreaIndex = 0;
            }

            // search forwards from current location to end
            for ( int i = startTextAreaIndex; found == false && i < findTextAreas.size() - 1; i++ ) {
                JTextArea textArea = (JTextArea) findTextAreas.get( i );
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
                    JTextArea textArea = (JTextArea) findTextAreas.get( i );
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
            clearSelection( previousFindTextAreaIndex );
            setSelection( findTextAreaIndex, findSelectionIndex, findText.length() );
            previousFindTextAreaIndex = findTextAreaIndex;
            previousFindSelectionIndex = findSelectionIndex;
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
        if ( !findText.equals( previousFindText ) ) {
            clearSelection( previousFindTextAreaIndex );
            previousFindTextAreaIndex = -1;
            previousFindSelectionIndex = -1;
            previousFindText = findText;
        }
        else if ( previousFindTextAreaIndex != -1 ) {
            // search backwards in the current JTextArea for another occurrence of the text
            JTextArea textArea = (JTextArea) findTextAreas.get( previousFindTextAreaIndex );
            String text = textArea.getText();
            int matchIndex = text.lastIndexOf( findText, previousFindSelectionIndex - 1 );
            if ( matchIndex != -1 ) {
                found = true;
                findTextAreaIndex = previousFindTextAreaIndex;
                findSelectionIndex = matchIndex;
            }
        }

        if ( !found ) {

            int startTextAreaIndex = previousFindTextAreaIndex - 1;
            if ( startTextAreaIndex < 0 ) {
                startTextAreaIndex = findTextAreas.size() - 1;
            }

            // search backwards from current location to beginning
            for ( int i = startTextAreaIndex; found == false && i >= 0; i-- ) {
                JTextArea textArea = (JTextArea) findTextAreas.get( i );
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
                for ( int i = findTextAreas.size() - 1; found == false && i > startTextAreaIndex; i-- ) {
                    JTextArea textArea = (JTextArea) findTextAreas.get( i );
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
            clearSelection( previousFindTextAreaIndex );
            setSelection( findTextAreaIndex, findSelectionIndex, findText.length() );
            previousFindTextAreaIndex = findTextAreaIndex;
            previousFindSelectionIndex = findSelectionIndex;
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
        if ( index >= 0 && index < findTextAreas.size() ) {
            JTextArea textArea = (JTextArea) findTextAreas.get( index );
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
        if ( index >= 0 && index < findTextAreas.size() ) {
            JTextArea textArea = (JTextArea) findTextAreas.get( index );
            textArea.requestFocus(); // not recommended according to Javadoc, but necessary here
            textArea.select( startIndex, startIndex + length );
        }
    }

    /**
     * Are there unsaved changes?
     */
    public boolean hasUnsavedChanges() {
        boolean hasUnSavedChanges = false;
        for ( TargetTextPanel targetTextPanel : targetTextPanels ) {
            if ( targetTextPanel.getTextArea().isDirty() ) {
                hasUnSavedChanges = true;
                break;
            }
        }
        return hasUnSavedChanges;
    }

    /**
     * Marks all target text areas as having been saved.
     */
    public void markAllSaved() {
        for ( TargetTextPanel targetTextPanel : targetTextPanels ) {
            targetTextPanel.getTextArea().markSaved();
        }
    }
}
