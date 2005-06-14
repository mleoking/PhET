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
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;


/**
 * SubscriptedSymbol
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SubscriptedSymbol extends CompositePhetGraphic {

    private PhetTextGraphic _symbolGraphic, _subscriptGraphic;
    
    /**
     * 
     */
    public SubscriptedSymbol( Component component, String symbol, String subscript, Font font, Color color ) {
        super( component );
        
            _symbolGraphic = new PhetTextGraphic( component, font, symbol, color );
            addGraphic( _symbolGraphic );
            
            _subscriptGraphic = new PhetTextGraphic( component, font, subscript, color );
            addGraphic( _subscriptGraphic );
            
            setLabel( symbol, subscript );
    }
    
    public void setLabel( String symbol, String subscript ) {
        _symbolGraphic.setText( symbol );
        _symbolGraphic.setJustification( PhetTextGraphic.SOUTH_EAST );
        _symbolGraphic.setLocation( 0, 0 );
        
        _subscriptGraphic.setText( subscript );
        _subscriptGraphic.setJustification( PhetTextGraphic.WEST );
        _subscriptGraphic.setLocation( 0, 0 );
    }
    
    public void setFont( Font font ) {
        _symbolGraphic.setFont( font );
        _subscriptGraphic.setFont( font );
    }
    
    public void setColor( Color color ) {
        _symbolGraphic.setColor( color );
        _subscriptGraphic.setColor( color );
    }
}
