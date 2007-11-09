/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.util.DialogUtils;

/**
 * InitializationDialog requests information required a initialization time.
 * It asks for the JAR file name and the country code for the translation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InitializationDialog extends JDialog {
    
    private JTextField _jarFileTextField;
    private JTextField _countryCodeTextField;
    private JCheckBox _autoTranslateCheckBox;
    private JButton _continueButton;
    private boolean _continue;
    
    public InitializationDialog( String title ) {
        this( null, title );
    }
    
    public InitializationDialog( Frame owner, String title ) {
        super( owner, title );
        setModal( true );
        setResizable( false );
        
        _continue = false;
        
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel instructionsLabel = new JLabel( "Please enter the following information to get started." );
            instructionsPanel.add( instructionsLabel );
        }
        
        JPanel jarFilePanel = new JPanel();
        jarFilePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel jarFileLabel = new JLabel( "JAR file path:" );
            
            _jarFileTextField = new JTextField();
            _jarFileTextField.setColumns( 30 );
            _jarFileTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            _jarFileTextField.setToolTipText( "<html>enter the full path name of the JAR file<br>for the simulation that you want to translate</html>" );

            JButton _browseButton = new JButton( "Browse..." );
            _browseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    JFileChooser chooser = new JFileChooser();
                    int option = chooser.showOpenDialog( InitializationDialog.this );
                    if ( option == JFileChooser.APPROVE_OPTION ) {
                        String fileName = chooser.getSelectedFile().getAbsolutePath();
                        _jarFileTextField.setText( fileName );
                        updateContinueButton();
                    }
                }
            } );
            
            jarFilePanel.add( jarFileLabel );
            jarFilePanel.add( _jarFileTextField );
            jarFilePanel.add( _browseButton );
        }
        
        JPanel countryCodePanel = new JPanel();
        countryCodePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            JLabel countryCodeLabel = new JLabel( "Country code for your translation:" );
            
            _countryCodeTextField = new JTextField();
            _countryCodeTextField.setColumns( 3 );
            _countryCodeTextField.addKeyListener( new KeyAdapter() {
                public void keyReleased( KeyEvent event ) {
                    updateContinueButton();
                }
            } );
            _countryCodeTextField.setToolTipText( "<html>enter a 2-letter ISO 3661 country code<br>that identifies the language you're translating to</html>" );
            
            countryCodePanel.add( countryCodeLabel );
            countryCodePanel.add( _countryCodeTextField );
        }
        
        JPanel autoTranslatePanel = new JPanel();
        autoTranslatePanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
        {
            _autoTranslateCheckBox = new JCheckBox( TUResources.getString( "checkbox.autoTranslate" ) );
            _autoTranslateCheckBox.setSelected( true );
            autoTranslatePanel.add( _autoTranslateCheckBox );
        }
        
        _continueButton = new JButton( "Continue..." );
        _continueButton.setEnabled( false );
        _continueButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                boolean error = false;
                File jarFile = new File( _jarFileTextField.getText() );
                if ( !jarFile.exists() ) {
                    error = true;
                    DialogUtils.showErrorDialog( InitializationDialog.this, "JAR file does not exist", "Error" );
                }
                String countryCode = _countryCodeTextField.getText();
                if ( !isWellFormedCountryCode( countryCode ) ) {
                    error = true;
                    DialogUtils.showErrorDialog( InitializationDialog.this, "Country code must be 2 lowercase letters", "Error" );
                }
                if ( !error ) {
                    _continue = true;
                    dispose();  
                }
            }
        });
        
        JButton cancelButton = new JButton( "Cancel" );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                _continue = false;
                dispose();
            }
        });
        
        Box topPanel = new Box( BoxLayout.Y_AXIS );
        topPanel.add( instructionsPanel );
        topPanel.add( jarFilePanel );
        topPanel.add( countryCodePanel );
        topPanel.add( autoTranslatePanel );
        
        JPanel innerPanel = new JPanel( new GridLayout( 1, 5 ) );
        innerPanel.add( _continueButton );
        innerPanel.add( cancelButton );
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
        return ( countryCode.length() == 2 && countryCode.matches( "[a-z][a-z]" ) );
    }
}
