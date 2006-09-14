/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck3.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck3.model.components.Resistor;
import edu.colorado.phet.common_cck.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Apr 21, 2003
 * Time: 7:57:31 PM
 * Copyright (c) Apr 21, 2003 by Sam Reid
 */
public class ColorBandsGraphic implements Graphic {
    private boolean visible = true;
    private CircuitComponentImageGraphic resistorGraphic;
    private Resistor resistor;

    public ColorBandsGraphic( boolean visible, CircuitComponentImageGraphic resistorGraphic, Resistor resistor ) {
        this.visible = visible;
        this.resistorGraphic = resistorGraphic;
        this.resistor = resistor;
    }

    public boolean containsRelativePoint( int xrel, int yrel ) {
        return false;
    }

    public void setVisible( boolean selected ) {
        this.visible = selected;
    }

    public void paint( Graphics2D g ) {
        if( !visible ) {
            return;
        }
        AffineTransform at = g.getTransform();
        AffineTransform newTransform = resistorGraphic.getTransform();
        g.transform( newTransform );
        int resistance = (int)resistor.getResistance();//resistorGraphic.getBranch().getint) ((Resistor) w).getResistance();
        Color[] c = new ResistorColors().to3Colors( resistance );
        g.setColor( c[0] );
        int y = 3;
        int width = 5;
        int height = 19;

        g.fillRect( 15, y, width, height );

        g.setColor( c[1] );
        g.fillRect( 25, y, width, height );
        g.setColor( c[2] );
        g.fillRect( 35, y, width, height );
        if( c[3] != null ) {
            g.setColor( c[3] );
            g.fillRect( 55, y, width, height );
        }
        g.setTransform( at );
    }

}
