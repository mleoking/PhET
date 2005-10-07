/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view.d2c;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.RenderingHints;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;


/**
 * D2CSumEquation is the equation that appears about the Sum graph in the
 * "Discrete to Continuous" (D2C) module.
 * 
 * <p>
 * NOTE!  The locations for children of this composite graphic
 * were arrived at via trial-&-error.  If you change fonts, you
 * will undoubtedly have to re-tweak the graphics locations.
 * You have been warned...
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CSumEquation extends CompositePhetGraphic {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Fonts and colors
    private static final Color EQUATION_COLOR = Color.BLACK;
    private static final Font LHS_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Font RHS_FONT = LHS_FONT;
    private static final Font SUMMATION_SYMBOL_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 30 );
    private static final Font INTEGRAL_SYMBOL_FONT = SUMMATION_SYMBOL_FONT;
    private static final Font RANGE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 12 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private HTMLGraphic _lhsGraphic; // everything on the left-hand side (lhs) of the summation|integral
    private HTMLGraphic _rhsGraphic; // everything on the right-hand side (rhs) of the summation|integral
    private CompositePhetGraphic _summationGraphic;
    private CompositePhetGraphic _integralGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component
     */
    public D2CSumEquation( Component component ) {
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
            String summationHTML = "<html>" + MathStrings.C_SIGMA + "</html>";
            HTMLGraphic summationSymbolGraphic = new HTMLGraphic( component, SUMMATION_SYMBOL_FONT, summationHTML, EQUATION_COLOR );
            summationSymbolGraphic.setLocation( 0, 0 );
            _summationGraphic.addGraphic( summationSymbolGraphic );

            // Lower range
            {
                String lowerRangeString = "n = -" + MathStrings.C_INFINITY;
                PhetTextGraphic lowerRangeGraphic = new PhetTextGraphic( component, RANGE_FONT, lowerRangeString, EQUATION_COLOR );
                int x = summationSymbolGraphic.getX();
                int y = summationSymbolGraphic.getY() + summationSymbolGraphic.getHeight();
                lowerRangeGraphic.setLocation( x, y );
                _summationGraphic.addGraphic( lowerRangeGraphic );
            }

            // Upper range
            {
                String upperRangeString = "" + MathStrings.C_INFINITY;
                PhetTextGraphic upperRangeGraphic = new PhetTextGraphic( component, RANGE_FONT, upperRangeString, EQUATION_COLOR );
                int x = summationSymbolGraphic.getX() + summationSymbolGraphic.getWidth() + 2;
                int y = summationSymbolGraphic.getY() + 5;
                upperRangeGraphic.setLocation( x, y );
                _summationGraphic.addGraphic( upperRangeGraphic );
            }
        }
        
        /*
         * Put the integral symbol and its range subscripts in a composite graphic,
         * so that we can position it as one unit between the lefthand- and righthand-
         * sides of the equation.
         */
        {
            _integralGraphic = new CompositePhetGraphic( component );
            addGraphic( _integralGraphic );
            
            // Sigma (summation) symbol
            String integralHTML = "<html>" + MathStrings.C_INTEGRAL + "</html>";
            HTMLGraphic integralSymbolGraphic = new HTMLGraphic( component, SUMMATION_SYMBOL_FONT, integralHTML, EQUATION_COLOR );
            integralSymbolGraphic.setLocation( 0, 0 );
            _integralGraphic.addGraphic( integralSymbolGraphic );

            // Lower range
            {
                String lowerRangeString = "-" + MathStrings.C_INFINITY;
                PhetTextGraphic lowerRangeGraphic = new PhetTextGraphic( component, RANGE_FONT, lowerRangeString, EQUATION_COLOR );
                int x = integralSymbolGraphic.getX() + integralSymbolGraphic.getWidth() + 2;
                int y = integralSymbolGraphic.getY() + integralSymbolGraphic.getHeight() - 10;
                lowerRangeGraphic.setLocation( x, y );
                _integralGraphic.addGraphic( lowerRangeGraphic );
            }

            // Upper range
            {
                String upperRangeString = "+" + MathStrings.C_INFINITY;
                PhetTextGraphic upperRangeGraphic = new PhetTextGraphic( component, RANGE_FONT, upperRangeString, EQUATION_COLOR );
                int x = integralSymbolGraphic.getX() + integralSymbolGraphic.getWidth() + 2;
                int y = integralSymbolGraphic.getY() + 5;
                upperRangeGraphic.setLocation( x, y );
                _integralGraphic.addGraphic( upperRangeGraphic );
            }
        }
        
        setForm( FourierConstants.DOMAIN_SPACE, false, FourierConstants.WAVE_TYPE_COSINE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the form of the equation.
     * If numberOfHarmonics is Integer.MAX_VALUE, then we use an integral.
     * 
     * @param domain
     * @param numberOfHarmonics
     * @param waveType
     */
    public void setForm( int domain, boolean infinity, int waveType ) {
    
        _summationGraphic.setVisible( !infinity );
        _integralGraphic.setVisible( infinity );
        
        // Set the lhs and rhs based on the domain.
        String lhsHTML = null;
        String rhsHTML = null;
        if ( !infinity ) {
            if ( domain == FourierConstants.DOMAIN_SPACE ) {
                // F(x)=
                lhsHTML = "<html>F(x) = </html>";
                // An sin(knx)
                rhsHTML = "<html>A<sub>n</sub> sin( k<sub>n</sub>x )</html>";
            }
            else if ( domain == FourierConstants.DOMAIN_TIME ) {
                // F(t)=
                lhsHTML = "<html>F(t) = </html>";
                // An sin(wnt)
                rhsHTML = "<html>A<sub>n</sub> sin( " + MathStrings.C_OMEGA + "<sub>n</sub>t )</html>";
            }
            else {
                throw new IllegalArgumentException( "invalid or unsupported domain: " + domain );
            }
        }
        else {
            if ( domain == FourierConstants.DOMAIN_SPACE ) {
                // F(x)=
                lhsHTML = "<html>F(x) = </html>";
                // A(k) sin(kx) dx
                rhsHTML = "<html>A(k) sin( kx ) dk</html>";
            }
            else if ( domain == FourierConstants.DOMAIN_TIME ) {
                // F(t)
                lhsHTML = "<html>F(t) = </html>";
                // A(w) sin(wt) dw
                rhsHTML = "<html>A(" + MathStrings.C_OMEGA + ") sin( " + MathStrings.C_OMEGA + "t ) d" + MathStrings.C_OMEGA + "</html>";
            }
            else {
                throw new IllegalArgumentException( "invalid or unsupported domain: " + domain );
            }
        }
        
        // The equations are in terms of sine.  Do we need to change to cosine?
        if ( waveType == FourierConstants.WAVE_TYPE_COSINE ) {
            // sin -> cos
            rhsHTML = rhsHTML.replaceAll( "sin\\(", "cos(" );
        }
        
        // Set the text.
        _lhsGraphic.setHTML( lhsHTML );   
        _rhsGraphic.setHTML( rhsHTML );
        
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

            // Summation and integral
            x = _lhsGraphic.getX() + _lhsGraphic.getWidth() + 5;
            y = -5;
            _summationGraphic.setLocation( x, y );
            _integralGraphic.setLocation( x, y );

            // RHS
            if ( infinity ) {
                x = _integralGraphic.getX() + _integralGraphic.getWidth() + 6;
            }
            else {
                x = _summationGraphic.getX() + _summationGraphic.getWidth() + 6;
            }
            y = 0;
            _rhsGraphic.setLocation( x, y );
        }
    }
}
