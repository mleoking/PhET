/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.translationutility.Command.CommandException;
import edu.colorado.phet.translationutility.JarFileManager.JarIOException;

/**
 * TranslationPanel is a panel that consists of 3 columns for localizing strings.
 * From left-to-right, the columns are: key, source language value, target language value.
 * The target language value is editable.
 * Buttons at the bottom of the panel support various actions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationPanel extends JPanel {
   
    private static final String SAVE_BUTTON_LABEL = TUResources.getString( "button.save" );
    private static final String LOAD_BUTTON_LABEL = TUResources.getString( "button.load" );
    private static final String TEST_BUTTON_LABEL = TUResources.getString( "button.testTranslation" );
    private static final String SUBMIT_BUTTON_LABEL = TUResources.getString( "button.submitTranslation" );
    private static final String HELP_BUTTON_LABEL = TUResources.getString( "button.help" );
    
    private static final String PHET_EMAIL_ADDRESS = ProjectProperties.getPhetEmailAddress();
    private static final String SUBMIT_MESSAGE = TUResources.getString( "message.submit" );
    private static final String SUBMIT_TITLE = TUResources.getString( "title.submitDialog" );

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
    
    private static final Color AUTO_TRANSLATED_BACKGROUND = Color.YELLOW;
    
    /*
     * Associates a key with a JTextArea.
     */
    private static class TargetTextArea extends JTextArea {

        private final String _key;

        public TargetTextArea( String key, String value ) {
            super( value );
            _key = key;
        }

        public String getKey() {
            return _key;
        }
    }
    
    private JarFileManager _jarFileManager;
    private final String _sourceCountryCode;
    private final String _targetCountryCode;
    private ArrayList _targetTextAreas; // array of TargetTextArea

    public TranslationPanel( JarFileManager jarFileManager, String sourceCountryCode, String targetCountryCode, boolean autoTranslate ) {
        super();
        
        _jarFileManager = jarFileManager;
        _sourceCountryCode = sourceCountryCode;
        _targetCountryCode = targetCountryCode;
        _targetTextAreas = new ArrayList();
        
        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();
        JScrollPane scrollPane = new JScrollPane( inputPanel );
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );

        setLayout( new BorderLayout() );
        setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        add( scrollPane, BorderLayout.CENTER );
        add( bottomPanel, BorderLayout.SOUTH );
    }
    
    private JPanel createInputPanel() {
        
        JPanel inputPanel = new JPanel();
        
        String projectName = _jarFileManager.getProjectName();
        Properties sourceProperties = null;
        Properties targetProperties = null;
        try {
            sourceProperties = _jarFileManager.readProperties( _sourceCountryCode );
            targetProperties = _jarFileManager.readProperties( _targetCountryCode );
        }
        catch ( JarIOException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        
        if ( targetProperties == null ) {
            targetProperties = new Properties();
        }
        
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setInsets( new Insets( 2, 5, 2, 5 ) ); // top, left, bottom, right
        int row = 0;
        
        JLabel projectNameLabel = new JLabel( projectName );
        projectNameLabel.setFont( TITLE_FONT );
        layout.addAnchoredComponent( projectNameLabel, row, KEY_COLUMN, GridBagConstraints.WEST );
        JLabel sourceLocaleLable = new JLabel( _sourceCountryCode );
        sourceLocaleLable.setFont( TITLE_FONT );
        layout.addAnchoredComponent( sourceLocaleLable, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        JLabel targetLocaleLable = new JLabel( _targetCountryCode );
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
            String targetValue = targetProperties.getProperty( key );
            boolean autoTranslated = false;
//            if ( targetValue == null ) {
//                System.out.println( "auto translating " + key );//XXX
//                targetValue = AutoTranslator.translate( sourceValue, _sourceCountryCode, _targetCountryCode );
//                autoTranslated = true;
//            }

            JLabel keyLabel = new JLabel( key );
            keyLabel.setFont( KEY_FONT );

            JTextArea sourceTextArea = new JTextArea( sourceValue );
            sourceTextArea.setFont( SOURCE_VALUE_FONT );
            sourceTextArea.setColumns( TEXT_AREA_COLUMNS );
            sourceTextArea.setLineWrap( true );
            sourceTextArea.setWrapStyleWord( true );
            sourceTextArea.setEditable( false );
            sourceTextArea.setBorder( TEXT_AREA_BORDER );
            sourceTextArea.setBackground( this.getBackground() );

            TargetTextArea targetTextArea = new TargetTextArea( key, targetValue );
            if ( autoTranslated ) {
                targetTextArea.setBackground( AUTO_TRANSLATED_BACKGROUND );
            }
            targetTextArea.setFont( TARGET_VALUE_FONT );
            targetTextArea.setColumns( sourceTextArea.getColumns() );
            targetTextArea.setRows( sourceTextArea.getLineCount() );
            targetTextArea.setLineWrap( true );
            targetTextArea.setWrapStyleWord( true );
            targetTextArea.setEditable( true );
            targetTextArea.setBorder( TEXT_AREA_BORDER );
            _targetTextAreas.add( targetTextArea );

            layout.addAnchoredComponent( keyLabel, row, KEY_COLUMN, GridBagConstraints.EAST );
            layout.addComponent( sourceTextArea, row, SOURCE_COLUMN );
            layout.addComponent( targetTextArea, row, TARGET_COLUMN );
            row++;
        }
        
        return inputPanel;
    }
    
    private JPanel createButtonPanel() {
        
        JButton saveButton = new JButton( SAVE_BUTTON_LABEL );
        saveButton.setEnabled( false );
       
        JButton loadButton = new JButton( LOAD_BUTTON_LABEL );
        loadButton.setEnabled( false );
        
        JButton testButton = new JButton( TEST_BUTTON_LABEL );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                testTranslation();
            }
        } );
        
        JButton submitButton = new JButton( SUBMIT_BUTTON_LABEL );
        submitButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                submitTranslation();
            }
        } );
        
        JButton helpButton = new JButton( HELP_BUTTON_LABEL );
        helpButton.setEnabled( false );//XXX
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 9 ) );
        buttonPanel.add( saveButton );
        buttonPanel.add( loadButton );
        buttonPanel.add( testButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( helpButton );

        JPanel panel = new JPanel();
        panel.add( buttonPanel );
        return panel;
    }
    
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
    
    private void testTranslation() {
        Properties targetProperties = getTargetProperties();
        try {
            String testJarFileName =_jarFileManager.writeProperties( targetProperties, _targetCountryCode );
            _jarFileManager.runJarFile( testJarFileName, _targetCountryCode );
        }
        catch ( JarIOException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        catch ( CommandException e ) {
            ExceptionHandler.handleFatalException( e );
        }
    }
    
    private void submitTranslation() {

        Properties properties = getTargetProperties();
        String fileName = null;
        try {
            fileName = _jarFileManager.savePropertiesToFile( properties, _targetCountryCode );
        }
        catch ( JarIOException e ) {
            ExceptionHandler.handleFatalException( e );
        }
        
        String[] args = { fileName, PHET_EMAIL_ADDRESS };
        String message = MessageFormat.format( SUBMIT_MESSAGE, args );
        DialogUtils.showInformationDialog( this, message, SUBMIT_TITLE );
    }
}
