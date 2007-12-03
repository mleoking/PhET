/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.translationutility.Command.CommandException;
import edu.colorado.phet.translationutility.FindDialog.FindListener;
import edu.colorado.phet.translationutility.JarFileManager.JarIOException;

/**
 * TranslationPanel is a panel that consists of 3 columns for localizing strings.
 * From left-to-right, the columns are: key, source language value, target language value.
 * The target language value is editable.
 * Buttons at the bottom of the panel support various actions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TranslationPanel extends JPanel implements FindListener {
   
    private static final String SAVE_BUTTON_LABEL = TUResources.getString( "button.save" );
    private static final String LOAD_BUTTON_LABEL = TUResources.getString( "button.load" );
    private static final String TEST_BUTTON_LABEL = TUResources.getString( "button.test" );
    private static final String SUBMIT_BUTTON_LABEL = TUResources.getString( "button.submit" );
    private static final String FIND_BUTTON_LABEL = TUResources.getString( "button.find" );
    private static final String HELP_BUTTON_LABEL = TUResources.getString( "button.help" );
    
    private static final String SUBMIT_MESSAGE = TUResources.getString( "message.submit" );
    private static final String SUBMIT_TITLE = TUResources.getString( "title.submitDialog" );

    private static final String CONFIRM_OVERWRITE_TITLE = TUResources.getString( "title.confirmOverwrite" );
    private static final String CONFIRM_OVERWRITE_MESSAGE = TUResources.getString( "message.confirmOverwrite" );
    
    private static final String HELP_TITLE = TUResources.getString( "title.help" );
    private static final String HELP_MESSAGE = TUResources.getString( "help.translation" );
    
    private static final Font DEFAULT_FONT = new JLabel().getFont();
    private static final Font TITLE_FONT = new Font( DEFAULT_FONT.getName(), Font.BOLD,  DEFAULT_FONT.getSize() + 4 );
    private static final Font KEY_FONT = new Font( DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() );
    private static final Color SOURCE_BACKGROUND = new JPanel().getBackground();
    
    private static final Color SELECTION_COLOR = Color.GREEN;
    
    private static final int KEY_COLUMN = 0;
    private static final int SOURCE_COLUMN = 1;
    private static final int TARGET_COLUMN = 2;
    
    private static final int TEXT_AREA_COLUMNS = 20;
    private static final Border TEXT_AREA_BORDER = BorderFactory.createCompoundBorder( 
            /* outside */ BorderFactory.createLineBorder( Color.BLACK, 1 ), 
            /* inside */ BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    
    private static final Color AUTO_TRANSLATED_BACKGROUND = Color.YELLOW;
    
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
     * Associates a key with a JTextArea.
     * Pressing tab or shift-tab in the text area moves focus forward or backward.
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
            
            // clear selection when focus is lost, needed for Find feature
            addFocusListener( new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    select( 0, 0 );
                }
            });
        }

        public String getKey() {
            return _key;
        }
    }
    
    private Frame _dialogOwner;
    private JarFileManager _jarFileManager;
    private final String _sourceLanguageCode;
    private final String _targetLanguageCode;
    private ArrayList _targetTextAreas; // array of TargetTextArea
    private ArrayList _findTextAreas; // array of JTextArea
    private String _previousFindText; // text provided to previous call to findNext or findPrevious
    private int _previousFindTextAreaIndex; // index into _findTextArea, identifies the JTextArea in which text was found
    private int _previousFindSelectionIndex; // index into a JTextArea's text, identifies where in the JTextArea the text was found
    private File _currentDirectory;
    private FindDialog _findDialog;

    public TranslationPanel( Frame dialogOwner, JarFileManager jarFileManager, String sourceLanguageCode, String targetLanguageCode, boolean autoTranslate ) {
        super();
        
        _dialogOwner = dialogOwner;
        _jarFileManager = jarFileManager;
        _sourceLanguageCode = sourceLanguageCode;
        _targetLanguageCode = targetLanguageCode;
        _targetTextAreas = new ArrayList();
        _findTextAreas = new ArrayList();
        _previousFindText = null;
        _previousFindTextAreaIndex = -1;
        _previousFindSelectionIndex = -1;
        _currentDirectory = null;
        
        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();
        JScrollPane scrollPane = new JScrollPane( inputPanel );

        setLayout( new BorderLayout() );
        setBorder( new EmptyBorder( 0, 10, 10, 10 ) );
        add( buttonPanel, BorderLayout.NORTH );
        add( scrollPane, BorderLayout.CENTER );
        
        setFocusTraversalPolicy( new ContainerOrderFocusTraversalPolicy() );
    }
    
    private JPanel createInputPanel() {
        
        JPanel inputPanel = new JPanel();
        
        String projectName = _jarFileManager.getProjectName();
        Properties sourceProperties = null;
        Properties targetProperties = null;
        try {
            sourceProperties = _jarFileManager.readProperties( _sourceLanguageCode );
            targetProperties = _jarFileManager.readProperties( _targetLanguageCode );
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
        JLabel sourceLocaleLable = new JLabel( _sourceLanguageCode );
        sourceLocaleLable.setFont( TITLE_FONT );
        layout.addAnchoredComponent( sourceLocaleLable, row, SOURCE_COLUMN, GridBagConstraints.WEST );
        JLabel targetLocaleLable = new JLabel( _targetLanguageCode );
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
        
        Font sourceFont = FontFactory.createFont( _sourceLanguageCode, Font.PLAIN, DEFAULT_FONT.getSize() );
        Font targetFont = FontFactory.createFont( _targetLanguageCode, Font.PLAIN, DEFAULT_FONT.getSize() );
        
        JTextArea previousTargetTextArea = null;
        Iterator i = sortedSet.iterator();
        while ( i.hasNext() ) {

            String key = (String) i.next();
            String sourceValue = sourceProperties.getProperty( key );
            String targetValue = targetProperties.getProperty( key );
            boolean autoTranslated = false;
//            if ( targetValue == null ) {
//                System.out.println( "auto translating " + key );//XXX
//                targetValue = AutoTranslator.translate( sourceValue, _sourceLanguageCode, _targetLanguageCode );
//                autoTranslated = true;
//            }

            JLabel keyLabel = new JLabel( key );
            keyLabel.setFont( KEY_FONT );

            JTextArea sourceTextArea = new SourceTextArea( sourceValue );
            sourceTextArea.setFont( sourceFont );

            TargetTextArea targetTextArea = new TargetTextArea( key, targetValue );
            targetTextArea.setFont( targetFont );
            if ( autoTranslated ) {
                targetTextArea.setBackground( AUTO_TRANSLATED_BACKGROUND );
            }
            targetTextArea.setColumns( sourceTextArea.getColumns() );
            targetTextArea.setRows( sourceTextArea.getLineCount() );
            if ( previousTargetTextArea != null ) {
                previousTargetTextArea.setNextFocusableComponent( targetTextArea ); // deprecated, but much simplier than using FocusTraversalPolicy
            }
            previousTargetTextArea = targetTextArea;
            _targetTextAreas.add( targetTextArea );

            _findTextAreas.add( sourceTextArea );
            _findTextAreas.add( targetTextArea );
            
            layout.addAnchoredComponent( keyLabel, row, KEY_COLUMN, GridBagConstraints.EAST );
            layout.addComponent( sourceTextArea, row, SOURCE_COLUMN );
            layout.addComponent( targetTextArea, row, TARGET_COLUMN );
            row++;
        }
        
        return inputPanel;
    }
    
    private JPanel createButtonPanel() {
        
        Icon testIcon = TUResources.getIcon( "testButton.png" );
        JButton testButton = new JButton( TEST_BUTTON_LABEL, testIcon );
        testButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                testTranslation();
            }
        } );
        
        Icon saveIcon = TUResources.getIcon( "saveButton.png" );
        JButton saveButton = new JButton( SAVE_BUTTON_LABEL, saveIcon );
        saveButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                saveTranslation();
            }
        } );
       
        Icon loadIcon = TUResources.getIcon( "loadButton.png" );
        JButton loadButton = new JButton( LOAD_BUTTON_LABEL, loadIcon );
        loadButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                loadTranslation();
            }
        } );
        
        Icon submitIcon = TUResources.getIcon( "submitButton.png" );
        JButton submitButton = new JButton( SUBMIT_BUTTON_LABEL, submitIcon );
        submitButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                submitTranslation();
            }
        } );
        
        Icon findIcon = TUResources.getIcon( "findButton.png" );
        JButton findButton = new JButton( FIND_BUTTON_LABEL, findIcon );
        findButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFind();
            }
        } );
        
        Icon helpIcon = TUResources.getIcon( "helpButton.png" );
        JButton helpButton = new JButton( HELP_BUTTON_LABEL, helpIcon );
        helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                showHelp();
            }
        } );
        
        JPanel buttonPanel = new JPanel( new GridLayout( 1, 9 ) );
        buttonPanel.add( testButton );
        buttonPanel.add( saveButton );
        buttonPanel.add( loadButton );
        buttonPanel.add( submitButton );
        buttonPanel.add( Box.createHorizontalStrut( 20 ) );
        buttonPanel.add( findButton );
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
            String testJarFileName =_jarFileManager.writeProperties( targetProperties, _targetLanguageCode );
            JarFileManager.runJarFile( testJarFileName, _targetLanguageCode );
        }
        catch ( JarIOException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
        catch ( CommandException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
    }
    
    private void submitTranslation() {

        Properties properties = getTargetProperties();
        
        // create the output File, in same directory as JAR file
        String dirName = _jarFileManager.getJarDirName();
        String baseName = JarFileManager.getPropertiesFileBaseName( _jarFileManager.getProjectName(), _targetLanguageCode );
        String fileName = null;
        if ( dirName != null && dirName.length() > 0 ) {
            fileName = dirName + File.separatorChar + baseName;
        }
        else {
            fileName = baseName;
        }
        File outFile = new File( fileName );
        if ( outFile.exists() ) {
            Object[] args = { fileName };
            String message = MessageFormat.format( CONFIRM_OVERWRITE_MESSAGE, args );
            int selection = JOptionPane.showConfirmDialog( this, message, CONFIRM_OVERWRITE_TITLE, JOptionPane.YES_NO_OPTION );
            if ( selection != JOptionPane.YES_OPTION ) {
                return;
            }
        }
        
        try {
            JarFileManager.savePropertiesToFile( properties, outFile );
        }
        catch ( JarIOException e ) {
            ExceptionHandler.handleNonFatalException( e );
        }
        
        // Use a JEditorPane so that it's possible to copy-paste the filename and email address.
        JEditorPane submitText = new JEditorPane();
        submitText.setEditorKit( new HTMLEditorKit() );
        Object[] args = { fileName };
        String html = MessageFormat.format( SUBMIT_MESSAGE, args );
        submitText.setText( html );
        submitText.setEditable( false );
        submitText.setBackground( new JLabel().getBackground() );
        submitText.setFont( new JLabel().getFont() );
        
        JOptionPane.showMessageDialog( this, submitText, SUBMIT_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
    
    private void saveTranslation() {
        JFileChooser chooser = new JFileChooser( _currentDirectory );
        int option = chooser.showSaveDialog( this );
        _currentDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            Properties properties = getTargetProperties();
            File outFile = chooser.getSelectedFile();
            if ( outFile.exists() ) {
                Object[] args = { outFile.getAbsolutePath() };
                String message = MessageFormat.format( CONFIRM_OVERWRITE_MESSAGE, args );
                int selection = JOptionPane.showConfirmDialog( this, message, CONFIRM_OVERWRITE_TITLE, JOptionPane.YES_NO_OPTION );
                if ( selection != JOptionPane.YES_OPTION ) {
                    return;
                }
            }
            try {
                JarFileManager.savePropertiesToFile( properties, outFile );
            }
            catch ( JarIOException e ) {
                ExceptionHandler.handleNonFatalException( e );
            }
        }
    }
    
    private void loadTranslation() {
        JFileChooser chooser = new JFileChooser( _currentDirectory );
        int option = chooser.showOpenDialog( this );
        _currentDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            File inFile = chooser.getSelectedFile();
            Properties properties = null;
            try {
                properties = JarFileManager.readPropertiesFromFile( inFile );
            }
            catch ( JarIOException e ) {
                ExceptionHandler.handleNonFatalException( e );
            }
            Iterator i = _targetTextAreas.iterator();
            while ( i.hasNext() ) {
                TargetTextArea targetTextArea = (TargetTextArea) i.next();
                String key = targetTextArea.getKey();
                String value = properties.getProperty( key );
                targetTextArea.setText( value );
            }
        }
    }
    
    private void showHelp() {
        DialogUtils.showInformationDialog( this, HELP_MESSAGE, HELP_TITLE );
    }
    
    public void handleFind() {
        if ( _findDialog == null ) {
            _findDialog = new FindDialog( _dialogOwner, _previousFindText );
            _findDialog.addFindListener( this );
            _findDialog.addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _findDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _previousFindText = _findDialog.getText();
                    _findDialog = null;
                }
            } );
            SwingUtils.centerDialogInParent( _findDialog );
            _findDialog.show();
        }
    }

    public void findNext( String findText ) {
        
        boolean found = false;
        
        // start from the top when the text is changed
        if ( !findText.equals( _previousFindText ) ) {
            clearSelection( _previousFindTextAreaIndex );
            _previousFindTextAreaIndex = -1;
            _previousFindSelectionIndex = -1;
            _previousFindText = findText;
        }
        else {
            // search forwards in the current JTextField for another occurrence of the text
            JTextArea textArea = (JTextArea) _findTextAreas.get( _previousFindTextAreaIndex );
            String text = textArea.getText();
            int matchIndex = text.indexOf( findText, _previousFindSelectionIndex + 1 );
            if ( matchIndex != -1 ) {
                clearSelection( _previousFindTextAreaIndex );
                _previousFindSelectionIndex = matchIndex;
                setSelection( _previousFindTextAreaIndex, matchIndex, findText.length() );
                found = true;
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
                    clearSelection( _previousFindTextAreaIndex );
                    _previousFindTextAreaIndex = i;
                    _previousFindSelectionIndex = matchIndex;
                    setSelection( _previousFindTextAreaIndex, matchIndex, findText.length() );
                    found = true;
                }
            }
            
            // wrap around, search from beginning to current location
            if ( !found ) {
                for ( int i = 0; found == false && i < startTextAreaIndex; i++ ) {
                    JTextArea textArea = (JTextArea) _findTextAreas.get( i );
                    String text = textArea.getText();
                    int matchIndex = text.indexOf( findText );
                    if ( matchIndex != -1 ) {
                        clearSelection( _previousFindTextAreaIndex );
                        _previousFindTextAreaIndex = i;
                        _previousFindSelectionIndex = matchIndex;
                        setSelection( _previousFindTextAreaIndex, matchIndex, findText.length() );
                        found = true;
                    }
                }
            }
        }
        
        // text not found
        if ( !found ) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void findPrevious( String findText ) {
        System.out.println( "TranslationPanel.findPrevious " + findText );//XXX
        
        boolean found = false;
        
        // start from the top when the text is changed
        if ( !findText.equals( _previousFindText ) ) {
            clearSelection( _previousFindTextAreaIndex );
            _previousFindTextAreaIndex = -1;
            _previousFindSelectionIndex = -1;
            _previousFindText = findText;
        }
        else {
            // search backwards in the current JTextField for another occurrence of the text
            JTextArea textArea = (JTextArea) _findTextAreas.get( _previousFindTextAreaIndex );
            String text = textArea.getText();
            int matchIndex = text.lastIndexOf( findText, _previousFindSelectionIndex - 1 );
            if ( matchIndex != -1 ) {
                clearSelection( _previousFindTextAreaIndex );
                _previousFindSelectionIndex = matchIndex;
                setSelection( _previousFindTextAreaIndex, matchIndex, findText.length() );
                found = true;
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
                    clearSelection( _previousFindTextAreaIndex );
                    _previousFindTextAreaIndex = i;
                    _previousFindSelectionIndex = matchIndex;
                    setSelection( _previousFindTextAreaIndex, matchIndex, findText.length() );
                    found = true;
                }
            }
            
            // wrap around, search from end to current location
            if ( !found ) {
                for ( int i = _findTextAreas.size() - 1; found == false && i > startTextAreaIndex; i-- ) {
                    JTextArea textArea = (JTextArea) _findTextAreas.get( i );
                    String text = textArea.getText();
                    int matchIndex = text.lastIndexOf( findText );
                    if ( matchIndex != -1 ) {
                        clearSelection( _previousFindTextAreaIndex );
                        _previousFindTextAreaIndex = i;
                        _previousFindSelectionIndex = matchIndex;
                        setSelection( _previousFindTextAreaIndex, matchIndex, findText.length() );
                        found = true;
                    }
                }
            }
        }
        
        // text not found
        if ( !found ) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void clearSelection( int index ) {
        if ( index >= 0 && index < _findTextAreas.size() ) {
            JTextArea textArea = (JTextArea) _findTextAreas.get( index );
            textArea.select( 0, 0 );
        }
    }
    
    private void setSelection( int index, int startIndex, int length ) {
        if ( index >= 0 && index < _findTextAreas.size() ) {
            JTextArea textArea = (JTextArea) _findTextAreas.get( index );
            textArea.requestFocus();
            textArea.select( startIndex, startIndex + length );
        }
    }
}
