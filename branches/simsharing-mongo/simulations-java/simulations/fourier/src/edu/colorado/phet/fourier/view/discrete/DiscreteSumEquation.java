// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.discrete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.RenderingHints;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.enums.Domain;
import edu.colorado.phet.fourier.enums.MathForm;
import edu.colorado.phet.fourier.enums.WaveType;


/**
 * DiscreteSumEquation is the equation that appears about the Sum graph
 * in the Discrete module.
 * <p>
 * NOTE!  The locations for children of this composite graphic
 * were arrived at via trial-&-error.  If you change fonts, you
 * will undoubtedly have to re-tweak the graphics locations.
 * You have been warned...
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DiscreteSumEquation extends CompositePhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Fonts and colors
    private static final Color EQUATION_COLOR = Color.BLACK;
    private static final Font LHS_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Font RHS_FONT = LHS_FONT;
    private static final Font SUMMATION_SYMBOL_FONT = new PhetFont( Font.PLAIN, 30 );
    private static final Font SUMMATION_RANGE_FONT = new PhetFont( Font.PLAIN, 12 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _lhsGraphic; // everything on the left-hand side (lhs) of the summation
    private HTMLGraphic _rhsGraphic; // everything on the right-hand side (rhs) of the summation
    private CompositePhetGraphic _summationGraphic;
    private PhetTextGraphic _upperRangeGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public DiscreteSumEquation( Component component ) {
        super( component );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        _lhsGraphic = new HTMLGraphic( component, LHS_FONT, "", EQUATION_COLOR );
        addGraphic( _lhsGraphic );
        
        _rhsGraphic = new HTMLGraphic( component, RHS_FONT, "", EQUATION_COLOR );
        addGraphic( _rhsGraphic );
        
        /*
         * Put the summation symbol and its range subscripts in a composite graphic,
         * so that we can position it as one unit between the lefthand- and righthand-
         * sides of the equation.
         */
        {
            _summationGraphic = new CompositePhetGraphic( component );
            addGraphic( _summationGraphic );
            
            // Sigma (summation) symbol
            String sigmaSymbol = "<html>" + MathStrings.C_SIGMA + "</html>";
            HTMLGraphic summationSymbolGraphic = new HTMLGraphic( component, SUMMATION_SYMBOL_FONT, sigmaSymbol, EQUATION_COLOR );
            summationSymbolGraphic.setLocation( 0, 0 );
            _summationGraphic.addGraphic( summationSymbolGraphic );

            // Lower range
            {
                PhetTextGraphic lowerRangeGraphic = new PhetTextGraphic( component, SUMMATION_RANGE_FONT, "n = 1", EQUATION_COLOR );
                int x = summationSymbolGraphic.getX();
                int y = summationSymbolGraphic.getY() + summationSymbolGraphic.getHeight();
                lowerRangeGraphic.setLocation( x, y );
                _summationGraphic.addGraphic( lowerRangeGraphic );
            }

            // Upper range
            {
                _upperRangeGraphic = new PhetTextGraphic( component, SUMMATION_RANGE_FONT, "", EQUATION_COLOR );
                int x = summationSymbolGraphic.getX() + summationSymbolGraphic.getWidth() + 2;
                int y = summationSymbolGraphic.getY() + 5;
                _upperRangeGraphic.setLocation( x, y );
                _summationGraphic.addGraphic( _upperRangeGraphic );
            }
        }
        
        setForm( Domain.SPACE, MathForm.WAVE_NUMBER, 1, WaveType.SINES );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the form of the equation.
     * If numberOfHarmonics is Integer.MAX_VALUE, then the number
     * will be replaced with the Unicode symbol for infinity.
     * 
     * @param domain
     * @param mathForm
     * @param numberOfHarmonics
     * @param waveType
     */
    public void setForm( Domain domain, MathForm mathForm, int numberOfHarmonics, WaveType waveType ) {
    
        // Get the strings for the domain & math form.
        String lhsString = MathStrings.getFunction( domain );;
        String rhsFormat = MathStrings.getTerm( domain, mathForm, waveType );
        String coefficientString = MathStrings.getCoefficient();
        Object[] args = { coefficientString, "n" };
        String rhsString = MessageFormat.format( rhsFormat, args );
        
        // Set the text.
        _lhsGraphic.setHTML( "<html>" + lhsString + " = </html>" );   
        _rhsGraphic.setHTML( "<html>" + rhsString + "</html>" );
        if ( numberOfHarmonics < Integer.MAX_VALUE ) {
            _upperRangeGraphic.setText( String.valueOf( numberOfHarmonics ) );
        }
        else {
            _upperRangeGraphic.setText( String.valueOf( MathStrings.C_INFINITY ) );
        }
        
        /*
         * Adjust locations so that things are aligned properly.
         * Values were arrived at via trial-&-error and are dependent
         * on the Font used.
         */
        {
            // LHS
            int x = 0;
            int y = 0;
            _lhsGraphic.setLocation( x, y );

            // Summation
            x = _lhsGraphic.getX() + _lhsGraphic.getWidth() + 5;
            y = -5;
            _summationGraphic.setLocation( x, y );

            // RHS
            x = _summationGraphic.getX() + _summationGraphic.getWidth() + 6;
            y = 0;
            _rhsGraphic.setLocation( x, y );
        }
    }
}
