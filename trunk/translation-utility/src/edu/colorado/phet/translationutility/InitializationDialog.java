/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * InitializationDialog is the first dialog seen by the user.
 * It requests information required a initialization time, including
 * the JAR file name and the language code for the translation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InitializationDialog extends JDialog {
    
    private static final String JAR_PATH_LABEL = TUResources.getString( "label.jarPath" );
    private static final String BROWSE_BUTTON_LABEL = TUResources.getString( "button.browse" );
    private static final String CANCEL_BUTTON_LABEL = TUResources.getString( "button.cancel" );
    private static final String CONTINUE_BUTTON_LABEL = TUResources.getString( "button.continue" );
    private static final String HELP_BUTTON_LABEL = TUResources.getString( "button.help" );
    private static final String LANGUAGE_CODE_LABEL = TUResources.getString( "label.languageCode" );
    private static final String AUTO_TRANSLATE_CHECKBOX_LABEL = TUResources.getString( "checkbox.autoTranslate" );
    private static final String ERROR_TITLE = TUResources.getString( "title.errorDialog" );
    private static final String ERROR_NO_SUCH_JAR = TUResources.getString( "error.noSuchJar" );
    private static final String ERROR_LANGUAGE_CODE_FORMAT = TUResources.getString( "error.languageCodeFormat" );
    private static final String HELP_TITLE = TUResources.getString( "title.help" );
    private static final String HELP_TEXT = TUResources.getString( "help.initialization" );
    
    private static final Font TITLE_FONT = new PhetDefaultFont( 32, true /* bold */ );
    private static final String LANGUAGE_CODE_PATTERN = "[a-z][a-z]"; // regular expression that describes ISO 639-1 specification
    
    private JTextField _jarFileTextField;
    private JTextField _languageCodeTextField;
    private JCheckBox _autoTranslateCheckBox;
    private JButton _continueButton;
    private boolean _continue; // true if the user pressed the Continue button and their inputs contained no errors
    private File _currentDirectory; // most recent directory visited when browsing for JAR files
    
    /**
     * Constructs a dialog with no owner.
     * 
     * @param title
     */
    public InitializationDialog( String title ) {
        this( null, title );
    }
    
    /**
     * Constructs a dialog with a specified owner.
     * 
     * @param owner
     * @param title
     */
    public InitializationDialog( Frame owner, String title ) {
        super( owner, title );
        
        setModal( true );
        setResizable( false );
        
        _continue = false;
        _currentDirectory = null;
        
        // panel with title and PhET logo
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            Image titleImage = TUResources.getCommonImage( PhetLookAndFeel.PHET_LOGO_120x50 );
            JLabel titleImageLabel = new JLabel( new ImageIcon( titleImage ) );
            JLabel titleLabel = new JLabel( TUResources.getString( "translation-utility.name" ) );
            titleLabel.setFont( TITLE_FONT );
            titlePanel.add( titleImageLabel );
            titlePanel.add( titleLabel );
        }
        
        // panel with textfield for JAR file name, and Browse button
        JPanel jarFilePanel = new JPanel();
        jarFilePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel jarFileLabel = new JLabel( JAR_PATH_LABEL );
            
            _jarFileTextField = new JTextField();
            _jarFileTextField.setColumns( 30 );
            _jarFileTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );

            JButton _browseButton = new JButton( BROWSE_BUTTON_LABEL );
            _browseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleJarBrowse();
                }
            } );
            
            jarFilePanel.add( jarFileLabel );
            jarFilePanel.add( _jarFileTextField );
            jarFilePanel.add( _browseButton );
        }
        
        // panel with language code
        JPanel languageCodePanel = new JPanel();
        languageCodePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel languageCodeLabel = new JLabel( LANGUAGE_CODE_LABEL );
            
            _languageCodeTextField = new JTextField();
            _languageCodeTextField.setColumns( 3 );
            _languageCodeTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            
            languageCodePanel.add( languageCodeLabel );
            languageCodePanel.add( _languageCodeTextField );
        }
        
        // panel for selecting automatic translation
        JPanel autoTranslatePanel = new JPanel();
        autoTranslatePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            _autoTranslateCheckBox = new JCheckBox( AUTO_TRANSLATE_CHECKBOX_LABEL );
            _autoTranslateCheckBox.setSelected( false );
            autoTranslatePanel.add( _autoTranslateCheckBox );
        }
        
        // buttons at the bottom of the dialog
        JPanel buttonPanel = new JPanel();
        {
            Icon continueIcon = TUResources.getIcon( "continueButton.png" );
            _continueButton = new JButton( CONTINUE_BUTTON_LABEL, continueIcon );
            _continueButton.setEnabled( false );
            _continueButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleContinueButton();
                }
            } );

            Icon cancelIcon = TUResources.getIcon( "cancelButton.png" );
            JButton cancelButton = new JButton( CANCEL_BUTTON_LABEL, cancelIcon );
            cancelButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleCancelButton();
                }
            } );

            Icon helpIcon = TUResources.getIcon( "helpButton.png" );
            JButton helpButton = new JButton( HELP_BUTTON_LABEL, helpIcon );
            helpButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleHelpButton();
                }
            } );

            JPanel innerPanel = new JPanel( new GridLayout( 1, 7 ) );
            innerPanel.add( _continueButton );
            innerPanel.add( cancelButton );
            innerPanel.add( Box.createHorizontalStrut( 20 ) );
            innerPanel.add( helpButton );
            buttonPanel.add( innerPanel );
        }
        
        // layout
        Box mainPanel = new Box( BoxLayout.Y_AXIS );
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        mainPanel.add( titlePanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( jarFilePanel );
        mainPanel.add( languageCodePanel );
//        mainPanel.add( autoTranslatePanel ); //TODO: possible feature for future
        mainPanel.add( new JSeparator() );
        mainPanel.add( buttonPanel );
        
        setContentPane( mainPanel );
        pack();
    }
    
    /*
     * Continue button is enabled only when all fields have been filled in.
     */
    private void updateContinueButton() {
        boolean b1 = _jarFileTextField.getText() != null && _jarFileTextField.getText().length() != 0;
        boolean b2 = _languageCodeTextField.getText() != null && _languageCodeTextField.getText().length() != 0;
        _continueButton.setEnabled( b1 && b2 );
    }
    
    /**
     * Determines if the user pushed the Continue button.
     * @return true or false
     */
    public boolean isContinue() {
        return _continue;
    }
    
    /**
     * Gets the value entered in the JAR file text field.
     * @return String
     */
    public String getJarFileName() {
        return _jarFileTextField.getText();
    }
    
    /**
     * Gets the value entered in the language code text field.
     * @return String
     */
    public String getTargetLanguageCode() {
        return _languageCodeTextField.getText();
    }
    
    /**
     * Determines if automatic translation was selected.
     * @return true or false
     */
    public boolean isAutoTranslateEnabled() { 
        return _autoTranslateCheckBox.isSelected();
    }
    
    /*
     * Determines if a language code is well formed.
     * A well-formed language code conforms to the ISO 639-1 specification,
     * as documented at http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes.
     */
    private boolean isWellFormedLanguageCode( String languageCode ) {
        return ( languageCode.length() == 2 && languageCode.matches( LANGUAGE_CODE_PATTERN ) );
    }
    
    /*
     * Called when the Browse button is pressed.
     * Opens a JAR file chooser and handles user interaction with the chooser.
     */
    private void handleJarBrowse() {
        JFileChooser chooser = FileChooserFactory.createJarFileChooser( _currentDirectory );
        int option = chooser.showOpenDialog( this );
        _currentDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            String fileName = chooser.getSelectedFile().getAbsolutePath();
            _jarFileTextField.setText( fileName );
            updateContinueButton();
        }
    }
    
    /*
     * Called when the Continue button is pressed.
     * Notifies the user if there are any problems with the information they're specified.
     * If everything is OK, disposes of this dialog.
     * Clients should register as a WindowListener, and interrogate the dialog for values.
     */
    private void handleContinueButton() {
        boolean error = false;
        File jarFile = new File( _jarFileTextField.getText() );
        if ( !jarFile.exists() ) {
            error = true;
            DialogUtils.showErrorDialog( InitializationDialog.this, ERROR_NO_SUCH_JAR, ERROR_TITLE );
        }
        String languageCode = _languageCodeTextField.getText();
        if ( !isWellFormedLanguageCode( languageCode ) ) {
            error = true;
            DialogUtils.showErrorDialog( InitializationDialog.this, ERROR_LANGUAGE_CODE_FORMAT, ERROR_TITLE );
        }
        if ( !error ) {
            _continue = true;
            dispose();
        }
    }
    
    /*
     * Called when the Cancel buttons is pressed.
     * Disposes of this dialog.
     */
    private void handleCancelButton() {
        _continue = false;
        dispose();
    }
    
    /*
     * Called when the Help button is pressed.
     * Displays a dialog containing Help information.
     */
    private void handleHelpButton() {
        
        JEditorPane helpText = new JEditorPane();
        helpText.setEditorKit( new HTMLEditorKit() );
        helpText.setText( HELP_TEXT );
        helpText.setEditable( false );
        helpText.setBackground( new JLabel().getBackground() );
        helpText.setFont( new JLabel().getFont() );
        helpText.addHyperlinkListener( new HyperlinkListener() {
            public void hyperlinkUpdate( HyperlinkEvent e ) {
                if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                    PhetServiceManager.showWebPage( e.getURL() );
                }
            }
        } );
        
        JOptionPane.showMessageDialog( this, helpText, HELP_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
}
