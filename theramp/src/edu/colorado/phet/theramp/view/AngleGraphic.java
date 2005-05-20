/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 8, 2005
 * Time: 1:51:39 PM
 * Copyright (c) May 8, 2005 by Sam Reid
 */

public class AngleGraphic extends CompositePhetGraphic {
    private RampGraphic rampGraphic;
    private PhetShapeGraphic phetShapeGraphic;
    private ShadowHTMLGraphic label;

    public AngleGraphic( RampGraphic rampGraphic ) {
        super( rampGraphic.getComponent() );
        this.rampGraphic = rampGraphic;
        phetShapeGraphic = new PhetShapeGraphic( getComponent(), null, new BasicStroke( 2 ), Color.black );
        label = new ShadowHTMLGraphic( getComponent(), "test", new Font( "Lucida Sans", 0, 14 ), Color.black, 1, 1, Color.gray );
        addGraphic( phetShapeGraphic );
        addGraphic( label );
        update();
    }

    public void update() {
        Point origin = rampGraphic.getViewLocation( 0 );
        Point twoMetersOver = getGroundLocationView( 5 );

        int squareWidth = ( twoMetersOver.x - origin.x ) * 2;

        Rectangle2D ellipseBounds = new Rectangle2D.Double();
        ellipseBounds.setFrameFromCenter( origin, new Point2D.Double( origin.x + squareWidth / 2, origin.y + squareWidth / 2 ) );

        double extent = rampGraphic.getSurface().getAngle() * 180 / Math.PI;
        extent = Math.max( extent, 0.00001 );
        Arc2D.Double arc = new Arc2D.Double( ellipseBounds, 0, extent, Arc2D.OPEN );
        phetShapeGraphic.setShape( arc );

        label.setLocation( RectangleUtils.getRightCenter( phetShapeGraphic.getBounds() ) );
        label.setLocation( label.getLocation().x, label.getLocation().y + rampGraphic.getImageHeight() + 5 );
        label.setHTML( "" + getAngleMessage() );
    }

    private String getAngleMessage() {
        double angle = rampGraphic.getSurface().getAngle() * 180 / Math.PI;
        String text = "<html>" + new DecimalFormat( "0.0" ).format( angle ) + "<sup>o</sup></html>";
        return text;
    }

    private Point getGroundLocationView( double dist ) {
        Point2D modelOrigion = rampGraphic.getSurface().getOrigin();
        Point2D.Double pt = new Point2D.Double( modelOrigion.getX() + dist, modelOrigion.getY() );
        return rampGraphic.getViewLocation( pt );
    }
}
