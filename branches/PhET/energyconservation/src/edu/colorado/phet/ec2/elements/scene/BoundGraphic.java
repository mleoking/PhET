/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.scene;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.ec2.EC2Module;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 13, 2003
 * Time: 11:55:29 AM
 * Copyright (c) Jul 13, 2003 by Sam Reid
 */
public class BoundGraphic implements Graphic {
    private EC2Module module;
    RectangularBound bound;

    public BoundGraphic( EC2Module module, RectangularBound bound ) {
        this.module = module;
        this.bound = bound;
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        Shape s = module.getTransform().toAffineTransform().createTransformedShape( bound.getRectangle() );
        g.fill( s );
    }
}
