package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 1:02:42 PM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class GroundGraphic implements Graphic, TransformListener {
    ModelViewTransform2d transform;
    Ground ground;
    Rectangle shape;

    public GroundGraphic( ModelViewTransform2d transform, Ground ground ) {
        this.transform = transform;
        this.ground = ground;
        transform.addTransformListener( this );
    }

    public void paint( Graphics2D g ) {
        if( shape != null ) {
            g.setColor( Color.darkGray );
            g.fillRect( shape.x, shape.y, shape.width, shape.height );
        }
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        shape = mvt.modelToView( new Rectangle2D.Double( ground.getX(), ground.getY(), ground.getWidth(), ground.getHeight() ) );
    }
}
