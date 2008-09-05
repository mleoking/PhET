/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.FileChooserFactory;
import edu.colorado.phet.translationutility.util.LanguageCodes;

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
    private static final String LANGUAGE_LABEL = TUResources.getString( "label.language" );
    
    private static final String ERROR_TITLE = TUResources.getString( "title.errorDialog" );
    
    private static final String ERROR_NO_SUCH_JAR = "JAR file does not exist";
    private static final String ERROR_LANGUAGE_CODE_FORMAT = "Language code must in ISO 639-1 format (2 lowercase letters)";
    private static final String ERROR_NOT_CUSTOM_LANGUAGE_CODE = "<html>{0} is not a custom language code.<br>It is the code for {1}.</html>";
    
    private static final String HELP_TITLE = TUResources.getString( "title.help" );
    private static final String HELP_JAR_FILE = TUResources.getString( "help.jarFile" );
    private static final String HELP_LANGUAGE_CODE = TUResources.getString( "help.languageCode" );
    
    private static final Font TITLE_FONT = new PhetFont( 32, true /* bold */ );
    private static final String LANGUAGE_CODE_PATTERN = "[a-z][a-z]"; // regular expression that describes ISO 639-1 specification
    
    private JTextField _jarFileTextField;
    private LanguageComboBox _languageComboBox;
    private JTextField _languageCodeTextField;
    private JCheckBox _autoTranslateCheckBox;
    private JButton _continueButton;
    private boolean _continue; // true if the user pressed the Continue button and their inputs contained no errors
    private File _currentDirectory; // most recent directory visited when browsing for JAR files
    
    /**
     * Constructs a dialog with no owner.
     */
    public InitializationDialog( String sourceLanguageCode ) {
        this( null, sourceLanguageCode );
    }
    
    /**
     * Constructs a dialog with a specified owner.
     * 
     * @param owner
     * @param sourceLanguageCode
     */
    public InitializationDialog( Frame owner, String sourceLanguageCode ) {
        super( owner, TUResources.getTitle() );
        
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
            
            Icon helpIcon = TUResources.getIcon( "helpButton.png" );
            JLabel helpLabel = new JLabel( helpIcon );
            helpLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    showHelp( HELP_JAR_FILE );
                }
            } );
            helpLabel.setCursor( new Cursor( Cursor.HAND_CURSOR ) );

            JButton _browseButton = new JButton( BROWSE_BUTTON_LABEL );
            _browseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleJarBrowse();
                }
            } );
            
            jarFilePanel.add( jarFileLabel );
            jarFilePanel.add( _jarFileTextField );
            jarFilePanel.add( helpLabel );
            jarFilePanel.add( _browseButton );
        }
        
        // panel with language
        JPanel languagePanel = new JPanel();
        languagePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel languageLabel = new JLabel( LANGUAGE_LABEL );
            
            _languageComboBox = new LanguageComboBox( sourceLanguageCode );
            _languageComboBox.setMaximumRowCount( 10 );
            _languageComboBox.addItemListener( new ItemListener() {
                public void itemStateChanged( ItemEvent e ) {
                    if ( e.getStateChange() == ItemEvent.SELECTED ) {
                        updateLanguageCodeTextField();
                        updateContinueButton();
                    }
                }
            } );
            
            _languageCodeTextField = new JTextField();
            _languageCodeTextField.setColumns( 3 );
            _languageCodeTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            
            Icon helpIcon = TUResources.getIcon( "helpButton.png" );
            JLabel helpLabel = new JLabel( helpIcon );
            helpLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    showHelp( HELP_LANGUAGE_CODE );
                }
            } );
            helpLabel.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
            
            languagePanel.add( languageLabel );
            languagePanel.add( _languageComboBox );
            languagePanel.add( _languageCodeTextField );
            languagePanel.add( helpLabel );
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

            JPanel innerPanel = new JPanel( new GridLayout( 1, 7 ) );
            innerPanel.add( _continueButton );
            innerPanel.add( cancelButton );
            buttonPanel.add( innerPanel );
        }
        
        // layout
        Box mainPanel = new Box( BoxLayout.Y_AXIS );
        mainPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        mainPanel.add( titlePanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( jarFilePanel );
        mainPanel.add( languagePanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( buttonPanel );
        
        setContentPane( mainPanel );
        pack();
        
        updateContinueButton();
        updateLanguageCodeTextField();
    }
    
    /*
     * Continue button is enabled only when all fields have been filled in.
     */
    private void updateContinueButton() {
        String jarFileName = getJarFileName();
        String languageCode = getTargetLanguageCode();
        _continueButton.setEnabled( jarFileName != null && languageCode != null );
    }
    
    /*
     * Language Code text field is visible only when the language code combo box is set to "custom".
     */
    private void updateLanguageCodeTextField() {
        boolean isCustomSelected = _languageComboBox.isCustomSelected();
        _languageCodeTextField.setVisible( isCustomSelected );
        if ( !isCustomSelected ) {
            _languageCodeTextField.setText( "" );
        }
        validate();
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
        String jarFileName = _jarFileTextField.getText();
        if ( jarFileName.length() == 0 ) {
            jarFileName = null;
        }
        return jarFileName;
    }
    
    /**
     * Gets the value selected for language code.
     * 
     * @return String
     */
    public String getTargetLanguageCode() {
        String code = _languageComboBox.getSelectedCode();;
        if ( code == null ) {
            code = _languageCodeTextField.getText();
        }
        if ( code.length() == 0 ) {
            code = null;
        }
        return code;
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
            showErrorDialog( ERROR_NO_SUCH_JAR );
        }
        else {
            String languageCode = getTargetLanguageCode();
            if ( !isWellFormedLanguageCode( languageCode ) ) {
                error = true;
                showErrorDialog( ERROR_LANGUAGE_CODE_FORMAT );
            }
            else if ( _languageComboBox.isCustomSelected() ) {
                // if "custom" is selected, then the language code shouldn't be one of the standard codes
                LanguageCodes lc = LanguageCodes.getInstance();
                String name = lc.getName( languageCode );
                if ( name != null ) {
                    error = true;
                    Object[] args = { languageCode, name };
                    String message = MessageFormat.format( ERROR_NOT_CUSTOM_LANGUAGE_CODE, args );
                    showErrorDialog( message );
                }
            }
        }
        
        if ( !error ) {
            _continue = true;
            dispose();
        }
    }
    
    /*
     * Displays a modal error dialog.
     */
    private void showErrorDialog( String message ) {
        JOptionPane.showMessageDialog( InitializationDialog.this, message, ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
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
    private void showHelp( String helpText ) {
        
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit( new HTMLEditorKit() );
        editorPane.setText( helpText );
        editorPane.setEditable( false );
        editorPane.setBackground( new JLabel().getBackground() );
        editorPane.setFont( new JLabel().getFont() );
        editorPane.addHyperlinkListener( new HyperlinkListener() {
            public void hyperlinkUpdate( HyperlinkEvent e ) {
                if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                    PhetServiceManager.showWebPage( e.getURL() );
                }
            }
        } );
        
        JOptionPane.showMessageDialog( this, editorPane, HELP_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
}
