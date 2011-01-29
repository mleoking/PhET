// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.view.RelativeDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class WaterTowerNode extends PNode {
    public WaterTowerNode( final ModelViewTransform transform, final WaterTower waterTower ) {

        //Handle
        addChild( new PImage( RESOURCES.getImage( "handle.png" ) ) {{
            addInputEventListener( new RelativeDragHandler( this, transform, waterTower.getTankBottomCenter(), new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D modelLocation ) {
                    return new Point2D.Double( waterTower.getTankBottomCenter().getValue().getX(), MathUtil.clamp( 0, modelLocation.getY(), WaterTower.MAX_Y ) );
                }
            } ) );
            scale( 1.75 );
            addInputEventListener( new CursorHandler() );
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D tankTopCenter = waterTower.getTankTopCenter();
                    final Point2D view = transform.modelToView( tankTopCenter );
                    setOffset( view.getX(), view.getY() - getFullBounds().getHeight() );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.gray, new BasicStroke( 5 ), Color.darkGray ) {{ // tank
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getTankShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.black ) {{
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getSupportShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.blue ) {{
            final SimpleObserver updateWaterLocation = new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getWaterShape() ) );
                }
            };
            waterTower.getTankBottomCenter().addObserver( updateWaterLocation );
            waterTower.getFluidVolumeProperty().addObserver( updateWaterLocation );
            setPickable( false );
        }} );

        //Panel covering the hole
        addChild( new PImage( BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "panel.png" ), 50 ) ) {{
            final Rectangle2D towerBounds = waterTower.getTankShape().getBounds2D();
            final Point2D point = transform.modelToView( towerBounds.getMaxX(), towerBounds.getMinY() );
            setOffset( point.getX(), point.getY() - getFullBounds().getHeight() );
            final Property<ImmutableVector2D> modelLocation = new Property<ImmutableVector2D>( new ImmutableVector2D(
                    waterTower.getTankBottomCenter().getValue().getX() + waterTower.getTankShape().getWidth() / 2,
                    waterTower.getTankBottomCenter().getValue().getY() ) );
            modelLocation.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double modelPoint = transform.modelToView( modelLocation.getValue() ).toPoint2D();
                    setOffset( modelPoint.getX(), modelPoint.getY() - getFullBounds().getHeight() );
                }
            } );
            addInputEventListener( new RelativeDragHandler( this, transform, modelLocation, new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D point2D ) {
                    final double y = MathUtil.clamp( waterTower.getTankBottomCenter().getValue().getY(), point2D.getY(), waterTower.getTankShape().getMaxY() );
                    return new Point2D.Double( waterTower.getTankBottomCenter().getValue().getX() + waterTower.getTankShape().getWidth() / 2, y );
                }
            } ) );
            addInputEventListener( new CursorHandler() );
        }} );
    }
}
