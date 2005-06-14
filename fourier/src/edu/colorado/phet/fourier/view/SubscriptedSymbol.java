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
import java.awt.Rectangle;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;


/**
 * SubscriptedSymbol is the graphical representation of a symbol with a subscript.
 * All such symbols are encapsulated here, so that we can easily change the 
 * implementation for the entire application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SubscriptedSymbol extends CompositePhetGraphic {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetTextGraphic _symbolGraphic, _subscriptGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component
     * @param symbol
     * @param subscript
     * @param font
     * @param color
     */
    public SubscriptedSymbol( Component component, String symbol, String subscript, Font font, Color color ) {
        super( component );

        _symbolGraphic = new PhetTextGraphic( component, font, "", color );
        addGraphic( _symbolGraphic );

        _subscriptGraphic = new PhetTextGraphic( component, font, "", color );
        addGraphic( _subscriptGraphic );

        setLabel( symbol, subscript );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the label.
     * 
     * @param symbol
     * @param subscript
     */
    public void setLabel( String symbol, String subscript ) {
        _symbolGraphic.setText( symbol );
        _symbolGraphic.setJustification( PhetTextGraphic.SOUTH_EAST );
        _symbolGraphic.setLocation( 0, 0 );
        
        _subscriptGraphic.setText( subscript );
        _subscriptGraphic.setJustification( PhetTextGraphic.WEST );
        _subscriptGraphic.setLocation( 0, 0 );
    }
    
    /**
     * Sets the font.
     * 
     * @param font
     */
    public void setFont( Font font ) {
        _symbolGraphic.setFont( font );
        _subscriptGraphic.setFont( font );
    }
    
    /**
     * Sets the color.
     * 
     * @param color
     */
    public void setColor( Color color ) {
        _symbolGraphic.setColor( color );
        _subscriptGraphic.setColor( color );
    }
}
