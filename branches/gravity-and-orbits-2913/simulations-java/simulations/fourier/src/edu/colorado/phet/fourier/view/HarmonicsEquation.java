// Copyright 2002-2011, University of Colorado

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
import java.awt.RenderingHints;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.WaveType;


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
    
    private static final Font DEFAULT_FONT = new PhetFont( Font.PLAIN, 20 );
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
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        setForm( Domain.SPACE, MathForm.WAVE_NUMBER, WaveType.SINES );
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
     * @param waveType
     */
    public void setForm( Domain domain, MathForm mathForm, WaveType waveType ) {
        
        // Example: An sin( kn x )
        String termFormat = MathStrings.getTerm( domain, mathForm, waveType );
        String coefficientString = MathStrings.getCoefficient();
        Object[] args = { coefficientString, "n" };
        String termString = MessageFormat.format( termFormat, args );

        setHTML( "<html>" + termString + "</html>");
    }
}
