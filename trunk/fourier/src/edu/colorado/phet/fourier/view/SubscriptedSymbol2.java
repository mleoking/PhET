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

import javax.swing.JLabel;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;


/**
 * SubscriptedSymbol
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SubscriptedSymbol2 extends CompositePhetGraphic {

    private PhetGraphic _labelGraphic;
    private JLabel _label;
    
    /**
     * 
     */
    public SubscriptedSymbol2( Component component, String symbol, String subscript, Font font, Color color ) {
        super( component );

        _label = new JLabel();
        _label.setFont( font );
        _label.setForeground( color );
        _label.setBackground( new Color( 255, 255, 255, 0 ) );
        _labelGraphic = PhetJComponent.newInstance( component, _label );
        addGraphic( _labelGraphic );
        setLabel( symbol, subscript );
    }
    
    public void setLabel( String symbol, String subscript ) {
        String string = "<html>" + symbol + "<sub>" + subscript + "</sub></html>";
        _label.setText( string );
        centerRegistrationPoint();
    }
    
    public void setFont( Font font ) {
        _label.setFont( font );
    }
    
    public void setColor( Color color ) {
        _label.setForeground( color );
    }
}
