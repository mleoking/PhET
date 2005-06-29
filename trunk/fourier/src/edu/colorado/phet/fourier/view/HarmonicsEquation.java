/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.MessageFormat;

import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;


/**
 * HarmonicsEquation is the equation shown above the "Harmonics" graph.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsEquation extends HTMLGraphic {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font DEFAULT_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color DEFAULT_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param domain
     */
    public HarmonicsEquation( Component component ) {
        super( component, DEFAULT_FONT, "", DEFAULT_COLOR );
        setForm( FourierConstants.DOMAIN_SPACE, FourierConstants.MATH_FORM_WAVE_NUMBER );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the form of the equations.
     * The form is determined by the domain and math form.
     * 
     * @param domain
     * @param mathForm
     */
    public void setForm( int domain, int mathForm ) {
        
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        
        // Example: An sin( kn x )
        String termFormat = MathStrings.getTerm( domain, mathForm );
        String coefficientString = MathStrings.getCoefficient();
        Object[] args = { coefficientString, "n" };
        String termString = MessageFormat.format( termFormat, args );

        setHTML( "<html>" + termString + "</html>");
    }
}
