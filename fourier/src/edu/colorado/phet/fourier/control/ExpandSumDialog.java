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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
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
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.model.FourierSeries;


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

        // function on the left side of the equation
        String function = MathStrings.getFunction( _domain ); 
        // format of one term in the expansion
        String termFormat = MathStrings.getTerm( _domain, _mathForm );
        
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
                
                String amplitudeString = VALUE_FORMATTER.format( amplitude );
                Object[] args = { amplitudeString, new Integer( i + 1 ) };
                String term = MessageFormat.format( termFormat, args );
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
