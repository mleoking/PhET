/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Apr 21, 2003
 * Time: 7:57:31 PM
 * Copyright (c) Apr 21, 2003 by Sam Reid
 */
public class ColorBands implements Graphic {
    boolean visible = true;
    ImageBranchGraphic resistorGraphic;
    private HasResistance hr;

    public ColorBands( boolean visible, ImageBranchGraphic resistorGraphic ) {
        this.visible = visible;
        this.resistorGraphic = resistorGraphic;
        hr = (HasResistance)resistorGraphic.getBranch();
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
//        if (!iwp.isImageFullWidth(w)) {
//            return;
//        }
        AffineTransform at = g.getTransform();
        AffineTransform newTransform = resistorGraphic.getImageTransform();
//        g.setTransform(iwp.getTransform(w, iwp.bi));
//        g.setTransform(newTransform);
        g.transform( newTransform );
        int resistance = (int)hr.getResistance();//resistorGraphic.getBranch().getint) ((Resistor) w).getResistance();
        Color[] c = new ColorComputation().to3Colors( resistance );
        g.setColor( c[0] );
        g.fillRect( 15, 2, 5, 23 );

        g.setColor( c[1] );
        g.fillRect( 25, 2, 5, 23 );
        g.setColor( c[2] );
        g.fillRect( 35, 2, 5, 23 );
        if( c[3] != null ) {
            g.setColor( c[3] );
            g.fillRect( 55, 2, 5, 23 );
        }
        g.setTransform( at );
    }

}
