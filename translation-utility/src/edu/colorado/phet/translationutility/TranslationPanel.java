/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * TranslationPanel is a panel that consists of 3 columns for localizing strings.
 * From left-to-right, the columns are: key, source language value, target language value.
 * The target language value  is editable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationPanel extends JPanel {
   
    private static final Font DEFAULT_FONT = new JLabel().getFont();
    private static final Font TITLE_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD,  DEFAULT_FONT.getSize() + 4 );
    private static final Font KEY_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    private static final Font SOURCE_VALUE_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    private static final Font TARGET_VALUE_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    
    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;
    
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    
    private static class TranslationControlInfo {

        private final String _key;
        private final JTextArea _textArea;

        public TranslationControlInfo( String key, JTextArea textArea ) {
            _key = key;
            _textArea = textArea;
        }

        public String getKey() {
            return _key;
        }

        public JTextArea getTextArea() {
            return _textArea;
        }
    }
    
    private ArrayList _translationControlInfo; // array of TranslationControlInfo

    public TranslationPanel( JarFileManager jarFileManager, String targetCountryCode ) {
        super();
        
        String projectName = jarFileManager.getProjectName();
        Properties sourceProperties = jarFileManager.readSourceProperties();
        
        _translationControlInfo = new ArrayList();
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right
        int row = 0;
        
        JLabel projectNameLabel = new JLabel( projectName );
        projectNameLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( projectNameLabel, row, KEY_COLUMN, GridBagConstraints.WEST );
        JLabel sourceLocaleLable = new JLabel( "en" );
        sourceLocaleLable.setFont( TITLE_FONT );
        layout.addAnchoredComponent( sourceLocaleLable, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        JLabel targetLocaleLable = new JLabel( targetCountryCode );
        targetLocaleLable.setFont( TITLE_FONT );
        layout.addAnchoredComponent( targetLocaleLable, row, TARGET_COLUMN, GridBagConstraints.WEST );
        row++;
        layout.addComponent( new JSeparator(), row, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
        row++;

        // sort the keys in lexographically acending order
        Enumeration keys = sourceProperties.propertyNames();
        TreeSet sortedSet = new TreeSet();
        while ( keys.hasMoreElements() ) {
            String key = (String) keys.nextElement();
            sortedSet.add( key );
        }
        
        Iterator i = sortedSet.iterator();
        while ( i.hasNext() ) {

            String key = (String) i.next();
            String sourceValue = sourceProperties.getProperty( key );

            JLabel keyLabel = new JLabel( key );
            keyLabel.setFont( KEY_FONT );

            JTextArea sourceValueTextArea = new JTextArea( sourceValue );
            sourceValueTextArea.setFont( SOURCE_VALUE_FONT );
            sourceValueTextArea.setColumns( TEXT_AREA_COLUMNS );
            sourceValueTextArea.setLineWrap( true );
            sourceValueTextArea.setWrapStyleWord( true );
            sourceValueTextArea.setEditable( false );
            sourceValueTextArea.setBorder( TEXT_AREA_BORDER );
            sourceValueTextArea.setBackground( this.getBackground() );

            JTextArea targetValueTextArea = new JTextArea( sourceValue );
            targetValueTextArea.setFont( TARGET_VALUE_FONT );
            targetValueTextArea.setColumns( sourceValueTextArea.getColumns() );
            targetValueTextArea.setRows( sourceValueTextArea.getLineCount() );
            targetValueTextArea.setLineWrap( true );
            targetValueTextArea.setWrapStyleWord( true );
            targetValueTextArea.setEditable( true );
            targetValueTextArea.setBorder( TEXT_AREA_BORDER );

            layout.addAnchoredComponent( keyLabel, row, KEY_COLUMN, GridBagConstraints.EAST );
            layout.addComponent( sourceValueTextArea, row, SOURCE_COLUMN );
            layout.addComponent( targetValueTextArea, row, TARGET_COLUMN );
            row++;
            
            _translationControlInfo.add( new TranslationControlInfo( key, targetValueTextArea ) );
        }
    }
    
    public Properties getTargetProperties() {
        Properties properties = new Properties();
        Iterator i = _translationControlInfo.iterator();
        while ( i.hasNext() ) {
            TranslationControlInfo tci = (TranslationControlInfo) i.next();
            String key = tci.getKey();
            String targetValue = tci.getTextArea().getText();
            properties.put( key, targetValue );
        }
        return properties;
    }
}
