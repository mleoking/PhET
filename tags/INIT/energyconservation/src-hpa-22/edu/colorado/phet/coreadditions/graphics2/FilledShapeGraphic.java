/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.graphics2;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.controllers.AbstractShape;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 16, 2003
 * Time: 11:18:02 PM
 * Copyright (c) Sep 16, 2003 by Sam Reid
 */
public class FilledShapeGraphic implements Graphic, AbstractShape {
    Shape shape;
    Color color;

    public FilledShapeGraphic( Color color ) {
        this.color = color;
    }

    public void setShape( Shape shape ) {
        this.shape = shape;
    }

    public void paint( Graphics2D g ) {
        if( shape == null ) {
            return;
        }
        g.setColor( color );
        g.fill( shape );
    }

    public boolean containsPoint( Point pt ) {
        if( shape == null ) {
            return false;
        }
        else {
            return shape.contains( pt );
        }
    }
}
