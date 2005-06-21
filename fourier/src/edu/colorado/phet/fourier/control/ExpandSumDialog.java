/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * ExpandSumDialog is the dialog that displays the sum waveform
 * equation in expanded form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ExpandSumDialog extends JDialog implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int TERMS_PER_LINE = 3;
    private static final NumberFormat VALUE_FORMATTER = new DecimalFormat( "0.##" );
    private static boolean SHOW_TERMS_WITH_ZERO_AMPLITUDE = true;
    
    // Functions
    private static final String FUNCTION_SPACE = "F(x)";
    private static final String FUNCTION_TIME = "F(t)";
    private static final String FUNCTION_SPACE_AND_TIME = "F(x,t)";
    
    // Math forms for "space" domain
    private static final String FORMAT_SPACE_WAVE_NUMBER = 
        "sin( k<sub>{0}</sub>x )";
    private static final String FORMAT_SPACE_WAVELENGTH = 
        "sin( 2\u03c0x / \u03BB<sub>{0}</sub> )";
    private static final String FORMAT_SPACE_MODE = 
        "sin( 2\u03c0{0}x / L )";
    
    // Math forms for "time" domain
    private static final String FORMAT_TIME_ANGULAR_FREQUENCY = 
        "sin( \u03C9<sub>{0}</sub>t )";
    private static final String FORMAT_TIME_FREQUENCY = 
        "sin( 2\u03c0f<sub>{0}</sub>t )";
    private static final String FORMAT_TIME_PERIOD = 
        "sin( 2\u03c0t / T<sub>{0}</sub> )";
    private static final String FORMAT_TIME_MODE = 
        "sin( 2\u03c0{0}t / T )";
    
    // Math forms for "space & time" domain
    private static final String FORMAT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY = 
        "sin( k<sub>{0}</sub>x - \u03C9<sub>{0}</sub>t )";
    private static final String FORMAT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD =
        "sin( 2\u03c0( x/\u03BB<sub>{0}</sub> - t/T<sub>{0}</sub> ) )";
    private static final String FORMAT_SPACE_AND_TIME_MODE = 
        "sin( 2\u03c0{0}( x/L - t/T ) )";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetApplication _app;
    private FourierSeries _fourierSeries;
    private JLabel _label;
    private JButton _closeButton;
    private int _domain, _mathForm;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param parentFrame the parent frame
     */
    public ExpandSumDialog( Frame parentFrame, FourierSeries fourierSeries ) {
        super( parentFrame );
        
        setTitle( SimStrings.get( "ExpandSumDialog.title" ) );
        setModal( false );
        setResizable( false );
        setLocationRelativeTo( parentFrame );
        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
        
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        _domain = FourierConstants.DOMAIN_SPACE;
        _mathForm = FourierConstants.MATH_FORM_WAVE_NUMBER;
        
        createUI();
        
        update();
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {
        dispose();
        _fourierSeries.removeObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // UI construction
    //----------------------------------------------------------------------------
    
    /**
     * Creates the user interface for the dialog.
     */
    private void createUI() {
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( inputPanel, BorderLayout.CENTER );
        panel.add( actionsPanel, BorderLayout.SOUTH );

        getContentPane().add( panel );
        pack();
    }
    
    /**
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {

        _label = new JLabel("Foo");

        JPanel inputPanel = new JPanel();
        inputPanel.add( _label );
        return inputPanel;
    }
    
    /** 
     * Creates the dialog's actions panel, consisting of OK and Cancel buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _closeButton = new JButton( SimStrings.get( "ExpandSumDialog.close" ) );

        JPanel innerPanel = new JPanel( new GridLayout( 1, 1, 10, 0 ) );
        innerPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( innerPanel );

        return actionPanel;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the domain and math form.
     * Together they determine the format of the equation.
     * 
     * @param domain
     * @param mathForm
     */
    public void setDomainAndMathForm( int domain, int mathForm ) {
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        _domain = domain;
        _mathForm = mathForm;
        update();
    }
    
    /**
     * Gets a reference to the dialog's close button.
     * It's the client's responsibility to handle the close button,
     * so use this to attach an ActionListener.
     * 
     * @return the close button
     */
    public JButton getCloseButton() {
        return _closeButton;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the dialog to match the model, domain and math form.
     */
    public void update() {

        String function = null; // function on the left side of the equation
        String format = null;  // format of one term in the expansion
        
        // Choose the corresponding HTML string.
        switch ( _domain ) {
            case FourierConstants.DOMAIN_SPACE:
                function = FUNCTION_SPACE;
                switch ( _mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER:
                        format = FORMAT_SPACE_WAVE_NUMBER;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH:
                        format = FORMAT_SPACE_WAVELENGTH;
                         break;
                    case FourierConstants.MATH_FORM_MODE:
                        format = FORMAT_SPACE_MODE;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_TIME:
                function = FUNCTION_TIME;
                switch ( _mathForm ) {
                    case FourierConstants.MATH_FORM_ANGULAR_FREQUENCY:
                        format = FORMAT_TIME_ANGULAR_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_FREQUENCY:
                        format = FORMAT_TIME_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_PERIOD:
                        format = FORMAT_TIME_PERIOD;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        format = FORMAT_TIME_MODE;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_SPACE_AND_TIME:
                function = FUNCTION_SPACE_AND_TIME;
                switch ( _mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY:
                        format = FORMAT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD:
                        format = FORMAT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        format = FORMAT_SPACE_AND_TIME_MODE;
                        break;
                    default:
                }
                break;
            default:
        }
        
        // Invalid combination?
        if ( format == null ) {  
            throw new IllegalArgumentException( 
                    "illegal combination of domain (" + _domain + ") " + "and math form (" + _mathForm + ")" );
        }
        
        // Build the equation, in HTML format.
        StringBuffer buffer = new StringBuffer( "<html>" );
        buffer.append( function );
        buffer.append( " =  " );
        int terms = 0;
        for ( int i = 0; i < _fourierSeries.getNumberOfHarmonics(); i++ ) {
            double amplitude = _fourierSeries.getHarmonic( i ).getAmplitude();
            if ( SHOW_TERMS_WITH_ZERO_AMPLITUDE || amplitude != 0 ) {
                
                if ( terms != 0 ) {
                    buffer.append( " + " );
                }
                
                buffer.append( VALUE_FORMATTER.format( amplitude ) );
                buffer.append( " " );
                Object[] args = { new Integer( i + 1 ), new Integer( i + 1 ) };
                String term = MessageFormat.format( format, args );
                buffer.append( term );

                if ( ( terms + 1 ) % TERMS_PER_LINE == 0 ) {
                    buffer.append( "<br>" );
                }
                
                terms++;
            }
        }
        buffer.append( "</html>" );
        
        // Update the dialog.
        _label.setText( buffer.toString() );
        pack();
        
        // HACK: make the dialog redraw itself
        if ( isVisible() ) {
            hide();
            show();
        }
    }
}
