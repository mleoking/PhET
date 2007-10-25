/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class TranslationPanel extends JPanel {
   
    private static final Font DEFAULT_FONT = new JLabel().getFont();
    private static final Font TITLE_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD,  DEFAULT_FONT.getSize() + 4 );
    private static final Font KEY_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD, DEFAULT_FONT.getSize() );
    private static final Font SOURCE_VALUE_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    private static final Font TARGET_VALUE_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    
    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;
    
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );

    public TranslationPanel( String[] projectNames ) {
        super();
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right

        int row = 0;
        for ( int i = 0; i < projectNames.length; i++ ) {
            
            JLabel projectNameLabel = new JLabel( projectNames[i] );
            projectNameLabel.setFont( TITLE_FONT );
            layout.addAnchoredComponent( projectNameLabel, row, KEY_COLUMN, GridBagConstraints.WEST );
            JLabel sourceLocaleLable = new JLabel( "en" );
            sourceLocaleLable.setFont( TITLE_FONT );
            layout.addAnchoredComponent( sourceLocaleLable, row, SOURCE_COLUMN, GridBagConstraints.WEST );
            JLabel targetLocaleLable = new JLabel( "fr" );
            targetLocaleLable.setFont( TITLE_FONT );
            layout.addAnchoredComponent( targetLocaleLable, row, TARGET_COLUMN, GridBagConstraints.WEST );
            row++;
            layout.addComponent( new JSeparator(), row, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
            row++;

            PhetResources projectResources = PhetResources.forProject( projectNames[i] );
            Properties sourceProperties = projectResources.getLocalizedProperties();
            Enumeration keys = sourceProperties.propertyNames();
            while ( keys.hasMoreElements() ) {

                String key = (String) keys.nextElement();
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
            }
            
            // space & separator between projects
            layout.addComponent( Box.createVerticalStrut( 25 ), row, 0 );
            row++;
            layout.addComponent( new JSeparator(), row, 0, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
            row++;
        }
    }
}
