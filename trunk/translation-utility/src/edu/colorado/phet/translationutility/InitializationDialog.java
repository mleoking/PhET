/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * InitializationDialog requests information required a initialization time.
 * It asks for the JAR file name and the country code for the translation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InitializationDialog extends JDialog {
    
    private static final String LABEL_JAR_PATH = TUResources.getString( "label.jarPath" );
    private static final String BUTTON_BROWSE = TUResources.getString( "button.browse" );
    private static final String BUTTON_CANCEL = TUResources.getString( "button.cancel" );
    private static final String BUTTON_CONTINUE = TUResources.getString( "button.continue" );
    private static final String BUTTON_HELP = TUResources.getString( "button.help" );
    private static final String LABEL_COUNTRY_CODE = TUResources.getString( "label.countryCode" );
    private static final String CHECKBOX_AUTO_TRANSLATE = TUResources.getString( "checkbox.autoTranslate" );
    private static final String TITLE_ERROR = TUResources.getString( "title.errorDialog" );
    private static final String TOOLTIP_JAR_PATH = TUResources.getString( "tooltip.jarPath" );
    private static final String TOOLTIP_COUNTRY_CODE = TUResources.getString( "tooltip.countryCode" );
    private static final String ERROR_NO_SUCH_JAR = TUResources.getString( "error.noSuchJar" );
    private static final String ERROR_COUNTRY_CODE_FORMAT = TUResources.getString( "error.countryCodeFormat" );
    private static final String JAR_FILE_FILTER_NAME = TUResources.getString( "fileFilter.jar" );
    private static final String HELP_TITLE = TUResources.getString( "title.help" );
    private static final String HELP_TEXT = TUResources.getString( "help.initialization" );
    
    private static final Font TITLE_FONT = new PhetDefaultFont( 32, true /* bold */ );
    private static final String COUNTRY_CODE_PATTERN = "[a-z][a-z]"; // regular expression
    
    private JTextField _jarFileTextField;
    private JTextField _countryCodeTextField;
    private JCheckBox _autoTranslateCheckBox;
    private JButton _continueButton;
    private boolean _continue;
    private File _currentDirectory;
    
    private static class JarFileFilter extends FileFilter {
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( ".jar" );
        }
        public String getDescription() {
            return JAR_FILE_FILTER_NAME;
        }
    }

    private static class JarFileChooser extends JFileChooser {
        public JarFileChooser( File currentDirectory ) {
            super( currentDirectory );
            FileFilter fileFilter = new JarFileFilter();
            setAcceptAllFileFilterUsed( false );
            addChoosableFileFilter( fileFilter );
            setFileFilter( fileFilter );
        }
    }
    
    public InitializationDialog( String title ) {
        this( null, title );
    }
    
    public InitializationDialog( Frame owner, String title ) {
        super( owner, title );
        setModal( true );
        setResizable( false );
        
        _continue = false;
        _currentDirectory = null;
        
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
        
        JPanel jarFilePanel = new JPanel();
        jarFilePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel jarFileLabel = new JLabel( LABEL_JAR_PATH );
            
            _jarFileTextField = new JTextField();
            _jarFileTextField.setColumns( 30 );
            _jarFileTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            _jarFileTextField.setToolTipText( TOOLTIP_JAR_PATH );

            JButton _browseButton = new JButton( BUTTON_BROWSE );
            _browseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleJarBrowse();
                }
            } );
            
            jarFilePanel.add( jarFileLabel );
            jarFilePanel.add( _jarFileTextField );
            jarFilePanel.add( _browseButton );
        }
        
        JPanel countryCodePanel = new JPanel();
        countryCodePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel countryCodeLabel = new JLabel( LABEL_COUNTRY_CODE );
            
            _countryCodeTextField = new JTextField();
            _countryCodeTextField.setColumns( 3 );
            _countryCodeTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            _countryCodeTextField.setToolTipText( TOOLTIP_COUNTRY_CODE );
            
            countryCodePanel.add( countryCodeLabel );
            countryCodePanel.add( _countryCodeTextField );
        }
        
        JPanel autoTranslatePanel = new JPanel();
        autoTranslatePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            _autoTranslateCheckBox = new JCheckBox( CHECKBOX_AUTO_TRANSLATE );
            _autoTranslateCheckBox.setSelected( false );
            autoTranslatePanel.add( _autoTranslateCheckBox );
            _autoTranslateCheckBox.setEnabled( false );//XXX
        }
        
        _continueButton = new JButton( BUTTON_CONTINUE );
        _continueButton.setEnabled( false );
        _continueButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleContinueButton();
            }
        });
        
        JButton cancelButton = new JButton( BUTTON_CANCEL );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleCancelButton();
            }
        });
        
        JButton helpButton = new JButton( BUTTON_HELP );
        helpButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleHelpButton();
            }
        });
        
        Box topPanel = new Box( BoxLayout.Y_AXIS );
        topPanel.add( titlePanel );
        topPanel.add( new JSeparator() );
        topPanel.add( jarFilePanel );
        topPanel.add( countryCodePanel );
//XXX        topPanel.add( autoTranslatePanel );
        
        JPanel innerPanel = new JPanel( new GridLayout( 1, 5 ) );
        innerPanel.add( _continueButton );
        innerPanel.add( cancelButton );
        innerPanel.add( helpButton );
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( innerPanel );
        
        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( buttonPanel, BorderLayout.CENTER );

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( topPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );
        
        setContentPane( mainPanel );
        pack();
    }
    
    private void updateContinueButton() {
        boolean b1 = _jarFileTextField.getText() != null && _jarFileTextField.getText().length() != 0;
        boolean b2 = _countryCodeTextField.getText() != null && _countryCodeTextField.getText().length() != 0;
        _continueButton.setEnabled( b1 && b2 );
    }
    
    public boolean isContinue() {
        return _continue;
    }
    
    public String getJarFileName() {
        return _jarFileTextField.getText();
    }
    
    public String getTargetCountryCode() {
        return _countryCodeTextField.getText();
    }
    
    public boolean isAutoTranslateEnabled() { 
        return _autoTranslateCheckBox.isSelected();
    }
    
    // must have the form of an ISO 3166-1 alpha-2 country code
    private boolean isWellFormedCountryCode( String countryCode ) {
        return ( countryCode.length() == 2 && countryCode.matches( COUNTRY_CODE_PATTERN ) );
    }
    
    private void handleJarBrowse() {
        JFileChooser chooser = new JarFileChooser( _currentDirectory );
        int option = chooser.showOpenDialog( InitializationDialog.this );
        _currentDirectory = chooser.getCurrentDirectory();
        if ( option == JFileChooser.APPROVE_OPTION ) {
            String fileName = chooser.getSelectedFile().getAbsolutePath();
            _jarFileTextField.setText( fileName );
            updateContinueButton();
        }
    }
    
    private void handleContinueButton() {
        boolean error = false;
        File jarFile = new File( _jarFileTextField.getText() );
        if ( !jarFile.exists() ) {
            error = true;
            DialogUtils.showErrorDialog( InitializationDialog.this, ERROR_NO_SUCH_JAR, TITLE_ERROR );
        }
        String countryCode = _countryCodeTextField.getText();
        if ( !isWellFormedCountryCode( countryCode ) ) {
            error = true;
            DialogUtils.showErrorDialog( InitializationDialog.this, ERROR_COUNTRY_CODE_FORMAT, TITLE_ERROR );
        }
        if ( !error ) {
            _continue = true;
            dispose();  
        }
    }
    
    private void handleCancelButton() {
        _continue = false;
        dispose();
    }
    
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
