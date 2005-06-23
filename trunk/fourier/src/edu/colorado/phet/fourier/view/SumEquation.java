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

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;


/**
 * SumEquation is the equation that appears about the Sum graph.
 * <p>
 * NOTE!  The locations for children of this composite graphic
 * were arrived at via trial-&-error.  If you change fonts, you
 * will undoubtedly have to re-tweak the graphics locations.
 * You have been warned...
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SumEquation extends CompositePhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Functions
    private static final String FUNCTION_SPACE = "F(x) = ";
    private static final String FUNCTION_TIME = "F(t) = ";
    private static final String FUNCTION_SPACE_AND_TIME = "F(x,t) = ";
    
//  Math forms for "space" domain
    private static final String TEXT_SPACE_WAVE_NUMBER = 
        "<html>A<sub>n</sub> sin( k<sub>n</sub>x )</html>";
    private static final String TEXT_SPACE_WAVELENGTH = 
        "<html>A<sub>n</sub> sin( 2\u03c0x / \u03BB<sub>n</sub> )</html>";
    private static final String TEXT_SPACE_MODE = 
        "<html>A<sub>n</sub> sin( 2\u03c0nx / L )</html>";
    
    // Math forms for "time" domain
    private static final String TEXT_TIME_ANGULAR_FREQUENCY = 
        "<html>A<sub>n</sub> sin( \u03C9<sub>n</sub>t )</html>";
    private static final String TEXT_TIME_FREQUENCY = 
        "<html>A<sub>n</sub> sin( 2\u03c0f<sub>n</sub>t )</html>";
    private static final String TEXT_TIME_PERIOD = 
        "<html>A<sub>n</sub> sin( 2\u03c0t / T<sub>n</sub> )</html>";
    private static final String TEXT_TIME_MODE = 
        "<html>A<sub>n</sub> sin( 2\u03c0nt / T )</html>";
    
    // Math forms for "space & time" domain
    private static final String TEXT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY = 
        "<html>A<sub>n</sub> sin( k<sub>n</sub>x - \u03C9<sub>n</sub>t )</html>";
    private static final String TEXT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD =
        "<html>A<sub>n</sub> sin( 2\u03c0( x/\u03BB<sub>n</sub> - t/T<sub>n</sub> ) )</html>";
    private static final String TEXT_SPACE_AND_TIME_MODE = 
        "<html>A<sub>n</sub> sin( 2\u03c0n( x/L - t/T ) )</html>";
    
    // Fonts and colors
    private static final Color EQUATION_COLOR = Color.BLACK;
    private static final Font LHS_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Font RHS_FONT = LHS_FONT;
    private static final Font SUMMATION_SYMBOL_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 30 );
    private static final Font SUMMATION_RANGE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 12 );

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
    
    public SumEquation( Component component ) {
        super( component );
        
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
            String sigmaSymbol = SimStrings.get( "symbol.sigma" );
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
        
        setForm( FourierConstants.DOMAIN_SPACE, FourierConstants.MATH_FORM_WAVE_NUMBER, 1 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setForm( int domain, int mathForm, int numberOfHarmonics ) {
        
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
    
        String lhsString = null;
        String rhsString = null;
        
        // Choose the corresponding HTML string.
        switch ( domain ) {
            case FourierConstants.DOMAIN_SPACE:
                lhsString = FUNCTION_SPACE;
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER:
                        rhsString = TEXT_SPACE_WAVE_NUMBER;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH:
                        rhsString = TEXT_SPACE_WAVELENGTH;
                         break;
                    case FourierConstants.MATH_FORM_MODE:
                        rhsString = TEXT_SPACE_MODE;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_TIME:
                lhsString = FUNCTION_TIME;
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_ANGULAR_FREQUENCY:
                        rhsString = TEXT_TIME_ANGULAR_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_FREQUENCY:
                        rhsString = TEXT_TIME_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_PERIOD:
                        rhsString = TEXT_TIME_PERIOD;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        rhsString = TEXT_TIME_MODE;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_SPACE_AND_TIME:
                lhsString = FUNCTION_SPACE_AND_TIME;
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY:
                        rhsString = TEXT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD:
                        rhsString = TEXT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        rhsString = TEXT_SPACE_AND_TIME_MODE;
                        break;
                    default:
                }
                break;
            default:
        }
        
        // Set the text.
        _lhsGraphic.setHTML( "<html>" + lhsString + "</html>" );
        _rhsGraphic.setHTML( rhsString );
        _upperRangeGraphic.setText( String.valueOf( numberOfHarmonics ) );
        
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
