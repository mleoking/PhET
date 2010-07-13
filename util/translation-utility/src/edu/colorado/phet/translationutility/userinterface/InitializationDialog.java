/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.userinterface;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUImages;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.TUStrings;
import edu.colorado.phet.translationutility.util.FileChooserFactory;

/**
 * InitializationDialog is the first dialog seen by the user.
 * It requests information required a initialization time, including
 * the JAR file name and the language code for the translation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InitializationDialog extends JDialog {
    
    private static final Font TITLE_FONT = new PhetFont( 32, true /* bold */ );
    
    private JTextField _jarFileTextField;
    private LocaleComboBox _localeComboBox;
    private JTextField _localeTextField;
    private JCheckBox _autoTranslateCheckBox;
    private JButton _continueButton;
    private boolean _continue; // true if the user pressed the Continue button and their inputs contained no errors
    private File _currentDirectory; // most recent directory visited when browsing for JAR files
    
    /**
     * Constructs a dialog with no owner.
     */
    public InitializationDialog( Locale sourceLocale ) {
        this( null, sourceLocale );
    }
    
    /**
     * Constructs a dialog with a specified owner.
     * 
     * @param owner
     * @param sourceLocale
     */
    public InitializationDialog( Frame owner, Locale sourceLocale ) {
        super( owner, TUResources.getTitle() );
        
        setModal( true );
        setResizable( false );
        
        _continue = false;
        _currentDirectory = null;
        
        // panel with title and PhET logo
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            Image titleImage = TUImages.PHET_LOGO;
            JLabel titleImageLabel = new JLabel( new ImageIcon( titleImage ) );
            JLabel titleLabel = new JLabel( TUStrings.TRANSLATION_UTILITY_NAME );
            titleLabel.setFont( TITLE_FONT );
            titlePanel.add( titleImageLabel );
            titlePanel.add( titleLabel );
        }
        
        // panel with textfield for JAR file name, and Browse button
        JPanel jarFilePanel = new JPanel();
        jarFilePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel jarFileLabel = new JLabel( TUStrings.JAR_PATH_LABEL );
            
            _jarFileTextField = new JTextField();
            _jarFileTextField.setColumns( 30 );
            _jarFileTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            
            JLabel helpLabel = new JLabel( TUImages.HELP_ICON );
            helpLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    String message = MessageFormat.format( TUStrings.HELP_JAR_MESSAGE, TUConstants.GET_PHET_URL, TUConstants.GET_PHET_URL );
                    showHelp( message );
                }
            } );
            helpLabel.setCursor( new Cursor( Cursor.HAND_CURSOR ) );

            JButton _browseButton = new JButton( TUStrings.BROWSE_BUTTON );
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
        
        // panel with locale
        JPanel localePanel = new JPanel();
        localePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel localeLabel = new JLabel( TUStrings.LOCALE_LABEL );
            
            _localeComboBox = new LocaleComboBox( sourceLocale );
            _localeComboBox.setMaximumRowCount( 10 );
            _localeComboBox.addItemListener( new ItemListener() {
                public void itemStateChanged( ItemEvent e ) {
                    if ( e.getStateChange() == ItemEvent.SELECTED ) {
                        updateLocaleTextField();
                        updateContinueButton();
                    }
                }
            } );
            
            _localeTextField = new JTextField();
            _localeTextField.setColumns( 6 );
            _localeTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            
            JLabel helpLabel = new JLabel( TUImages.HELP_ICON );
            helpLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    String message = MessageFormat.format( TUStrings.HELP_LOCALE_MESSAGE, TUConstants.PHETHELP_EMAIL );
                    showHelp( message );
                }
            } );
            helpLabel.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
            
            localePanel.add( localeLabel );
            localePanel.add( _localeComboBox );
            localePanel.add( _localeTextField );
            localePanel.add( helpLabel );
        }
        
        // buttons at the bottom of the dialog
        JPanel buttonPanel = new JPanel();
        {
            _continueButton = new JButton( TUStrings.CONTINUE_BUTTON, TUImages.CONTINUE_ICON );
            _continueButton.setEnabled( false );
            _continueButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleContinueButton();
                }
            } );

            JButton cancelButton = new JButton( TUStrings.CANCEL_BUTTON, TUImages.CANCEL_ICON );
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
        mainPanel.add( localePanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( buttonPanel );
        
        setContentPane( mainPanel );
        pack();
        
        updateContinueButton();
        updateLocaleTextField();
    }
    
    /*
     * Continue button is enabled only when all fields have been filled in.
     */
    private void updateContinueButton() {
        String jarFileName = getJarFileName();
        Locale locale = getTargetLocale();
        _continueButton.setEnabled( jarFileName != null && locale != null );
    }
    
    /*
     * Locale text field is visible only when the locale combo box is set to "custom".
     */
    private void updateLocaleTextField() {
        boolean isCustomSelected = _localeComboBox.isCustomSelected();
        _localeTextField.setVisible( isCustomSelected );
        if ( !isCustomSelected ) {
            _localeTextField.setText( "" );
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
     * Gets the value selected for locale.
     * 
     * @return String
     */
    public Locale getTargetLocale() {
        Locale locale = _localeComboBox.getSelectedLocale();
        if ( locale == null ) {
            String text = _localeTextField.getText();
            if ( text != null && text.length() != 0 ) {
                locale = LocaleUtils.stringToLocale( _localeTextField.getText() );
            }
        }
        return locale;
    }
    
    /**
     * Determines if automatic translation was selected.
     * @return true or false
     */
    public boolean isAutoTranslateEnabled() { 
        return _autoTranslateCheckBox.isSelected();
    }
    
    /*
     * Called when the Browse button is pressed.
     * Opens a JAR file chooser and handles user interaction with the chooser.
     */
    private void handleJarBrowse() {
        JFileChooser chooser = FileChooserFactory.createJarFileChooser();
        chooser.setCurrentDirectory( _currentDirectory );
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
            showErrorDialog( TUStrings.ERROR_NO_SUCH_JAR );
        }
        else {
            Locale locale = getTargetLocale();
            if ( _localeComboBox.isCustomSelected() ) {
                // if "custom" is selected, then the locale shouldn't be one of the standard codes
                PhetLocales lc = PhetLocales.getInstance();
                String name = lc.getName( locale );
                if ( name != null ) {
                    error = true;
                    Object[] args = { locale, name };
                    String message = MessageFormat.format( TUStrings.ERROR_NOT_CUSTOM_LOCALE, args );
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
        JOptionPane.showMessageDialog( InitializationDialog.this, message, TUStrings.ERROR_TITLE, JOptionPane.ERROR_MESSAGE );
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
        
        JOptionPane.showMessageDialog( this, editorPane, TUStrings.HELP_TITLE, JOptionPane.INFORMATION_MESSAGE );
    }
}
