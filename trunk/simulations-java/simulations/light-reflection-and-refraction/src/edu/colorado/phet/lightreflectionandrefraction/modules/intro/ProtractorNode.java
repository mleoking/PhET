// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class ProtractorNode extends PNode {
    public ProtractorNode( final ModelViewTransform transform, final Property<Boolean> showProtractor, double x, double y ) {
        final BufferedImage image = BufferedImageUtils.multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "protractor.png" ), 250 );
        addChild( new PImage( image ) {{
            setOffset( transform.modelToViewX( 0 ) - getFullBounds().getWidth() / 2, transform.modelToViewY( 0 ) - getFullBounds().getHeight() / 2 );
            showProtractor.addObserver( new SimpleObserver() {
                public void update() {
                    ProtractorNode.this.setVisible( showProtractor.getValue() );
                }
            } );
        }} );
        final Ellipse2D.Double outerShape = new Ellipse2D.Double( 0, 0, image.getWidth(), image.getHeight() );
        Area draggableShape = new Area( outerShape ) {{
            final double rx = image.getWidth() * 0.3;//tuned to the given image
            final double ry = image.getHeight() * 0.3;
            subtract( new Area( new Ellipse2D.Double( outerShape.getCenterX() - rx, outerShape.getCenterY() - ry, rx * 2, ry * 2 ) ) );
            add( new Area( new Rectangle2D.Double( 20, outerShape.getCenterY(), outerShape.getWidth() - 40, 38 ) ) );
        }};
        addChild( new PhetPPath( draggableShape, new Color( 0, 0, 0, 0 ) ) {{
//        addChild( new PhetPPath( draggableShape, new Color( 255,0,0,128 ) ) {{//For debugging the drag hit area
            setOffset( transform.modelToViewX( 0 ) - getFullBounds().getWidth() / 2, transform.modelToViewY( 0 ) - getFullBounds().getHeight() / 2 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    doDrag( event );
                }
            } );
        }} );

        final Point2D point2D = transform.modelToViewDelta( new ImmutableVector2D( x, y ) ).toPoint2D();
        translate( point2D.getX() + getFullBounds().getWidth() / 2, point2D.getY() - getFullBounds().getHeight() / 2 );
    }

    public void doDrag( PInputEvent event ) {
        final PDimension delta = event.getDeltaRelativeTo( getParent() );
        translate( delta.width, delta.height );
    }

    @Override
    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( isVisible );
        setChildrenPickable( isVisible );
    }
}
