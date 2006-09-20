package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public class ComponentImageNode extends ComponentNode {
    private PImage pImage;

    public ComponentImageNode( final CCKModel model, final CircuitComponent circuitComponent, BufferedImage image ) {
        super( model, circuitComponent );
        pImage = new PImage( image );
        addChild( pImage );
        update();
    }

    protected void update() {
        super.update();

        Rectangle2D aShape = new Rectangle2D.Double( 0, 0, pImage.getFullBounds().getWidth(), pImage.getFullBounds().getHeight() );
        aShape = RectangleUtils.expand( aShape, 2, 2 );
        getHighlightNode().setPathTo( aShape );
        getHighlightNode().setVisible( getBranch().isSelected() );

        double resistorLength = getBranch().getStartPoint().distance( getBranch().getEndPoint() );
        double imageLength = pImage.getFullBounds().getWidth();
        double sx = resistorLength / imageLength;
        double sy = getCircuitComponent().getHeight() / pImage.getFullBounds().getHeight();
        double angle = new Vector2D.Double( getBranch().getStartPoint(), getBranch().getEndPoint() ).getAngle();
        setTransform( new AffineTransform() );
        setScale( sx );
//        transformBy( AffineTransform.getScaleInstance( sx,sy));
        setOffset( getBranch().getStartPoint() );
        rotate( angle );
        translate( 0, -pImage.getFullBounds().getHeight() / 2 );
    }

    public static class BatteryNode extends ComponentImageNode {
        private Battery battery;

        public BatteryNode( CCKModel model, Battery battery ) {
            super( model, battery, CCKImageSuite.getInstance().getLifelikeSuite().getBatteryImage() );
            this.battery = battery;
        }
    }
}
