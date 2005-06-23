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
    
    // Math forms for "space" domain
    private static final String TEXT_SPACE_WAVE_NUMBER = 
        "<html>sin( k<sub>n</sub>x )</html>";
    private static final String TEXT_SPACE_WAVELENGTH = 
        "<html>sin( 2\u03c0x / \u03BB<sub>n</sub> )</html>";
    private static final String TEXT_SPACE_MODE = 
        "<html>sin( 2\u03c0nx / L )</html>";
    
    // Math forms for "time" domain
    private static final String TEXT_TIME_ANGULAR_FREQUENCY = 
        "<html>sin( \u03C9<sub>n</sub>t )</html>";
    private static final String TEXT_TIME_FREQUENCY = 
        "<html>sin( 2\u03c0f<sub>n</sub>t )</html>";
    private static final String TEXT_TIME_PERIOD = 
        "<html>sin( 2\u03c0t / T<sub>n</sub> )</html>";
    private static final String TEXT_TIME_MODE = 
        "<html>sin( 2\u03c0nt / T )</html>";
    
    // Math forms for "space & time" domain
    private static final String TEXT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY = 
        "<html>sin( k<sub>n</sub>x - \u03C9<sub>n</sub>t )</html>";
    private static final String TEXT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD =
        "<html>sin( 2\u03c0( x/\u03BB<sub>n</sub> - t/T<sub>n</sub> ) )</html>";
    private static final String TEXT_SPACE_AND_TIME_MODE = 
        "<html>sin( 2\u03c0n( x/L - t/T ) )</html>";
    
    // Fonts and colors
    private static final Color EQUATION_COLOR = Color.BLACK;
    private static final Font LHS_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Font RHS_FONT = LHS_FONT;
    private static final Font SUMMATION_SYMBOL_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 40 );
    private static final Font SUMMATION_RANGE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 12 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _lhsGraphic;
    private HTMLGraphic _rhsGraphic;
    private CompositePhetGraphic _summationGraphic;
    private PhetTextGraphic _summationSuperscriptGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SumEquation( Component component ) {
        super( component );
        
        _lhsGraphic = new HTMLGraphic( component, LHS_FONT, "", EQUATION_COLOR );
        addGraphic( _lhsGraphic );
        
        _rhsGraphic = new HTMLGraphic( component, RHS_FONT, "", EQUATION_COLOR );
        addGraphic( _rhsGraphic );
        
        // Summation is a composite graphic.
        {
            _summationGraphic = new CompositePhetGraphic( component );
            addGraphic( _summationGraphic );
            
            // Sigma (summation) symbol
            String sigmaSymbol = SimStrings.get( "symbol.sigma" );
            HTMLGraphic summationSymbolGraphic = new HTMLGraphic( component, SUMMATION_SYMBOL_FONT, sigmaSymbol, EQUATION_COLOR );
            summationSymbolGraphic.setLocation( 0, 0 );
            _summationGraphic.addGraphic( summationSymbolGraphic );

            // Range subscript
            {
                PhetTextGraphic summationSubscriptGraphic = new PhetTextGraphic( component, SUMMATION_RANGE_FONT, "n = 1", EQUATION_COLOR );
                int x = summationSymbolGraphic.getX() + summationSymbolGraphic.getWidth() + 3;
                int y = summationSymbolGraphic.getY() + summationSymbolGraphic.getHeight() - 8;
                summationSubscriptGraphic.setLocation( x, y );
                _summationGraphic.addGraphic( summationSubscriptGraphic );
            }

            // Range superscript
            {
                _summationSuperscriptGraphic = new PhetTextGraphic( component, SUMMATION_RANGE_FONT, "", EQUATION_COLOR );
                int x = summationSymbolGraphic.getX() + summationSymbolGraphic.getWidth() + 3;
                int y = summationSymbolGraphic.getY() + 8;
                _summationSuperscriptGraphic.setLocation( x, y );
                _summationGraphic.addGraphic( _summationSuperscriptGraphic );
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
        
        {
            _lhsGraphic.setHTML( "<html>" + lhsString + "</html>" );
            int x = 0;
            int y = 0;
            _lhsGraphic.setLocation( x, y );
        }
        
        {
            _summationSuperscriptGraphic.setText( String.valueOf( numberOfHarmonics ) );
            int x = _lhsGraphic.getX() + _lhsGraphic.getWidth() + 5;
            int y = -10;
            _summationGraphic.setLocation( x, y );
        }
        
        {
            _rhsGraphic.setHTML( rhsString );
            int x = _summationGraphic.getX() + _summationGraphic.getWidth() + 5;
            int y = 0;
            _rhsGraphic.setLocation( x, y );
        }
    }
}
