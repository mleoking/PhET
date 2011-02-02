// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.view.PoolNode;
import edu.colorado.phet.fluidpressureandflow.view.RelativeDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class WaterTowerNode extends PNode {
//    public static final Color WATER_COLOR = Color.blue;

    public WaterTowerNode( final ModelViewTransform transform, final WaterTower waterTower, final Property<Double> fluidDensity ) {

        //Handle
        addChild( new PImage( RESOURCES.getImage( "handle.png" ) ) {{
            addInputEventListener( new RelativeDragHandler( this, transform, waterTower.tankBottomCenter, new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D modelLocation ) {
                    return new Point2D.Double( waterTower.tankBottomCenter.getValue().getX(), MathUtil.clamp( 0, modelLocation.getY(), WaterTower.MAX_Y ) );
                }
            } ) );
            scale( 1.75 );
            addInputEventListener( new CursorHandler( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) ) );
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D tankTopCenter = waterTower.getTankTopCenter();
                    final Point2D view = transform.modelToView( tankTopCenter );
                    setOffset( view.getX(), view.getY() - getFullBounds().getHeight() );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.gray, new BasicStroke( 8 ), Color.black ) {{ // tank outline
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getTankShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.gray ) {{ // chop off the inner part of the tank stroke from above
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getTankShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.black ) {{
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getSupportShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( new Color( PoolNode.getTopColor( fluidDensity.getValue() ).getRGB() ) ) {{
            fluidDensity.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( new Color( PoolNode.getTopColor( fluidDensity.getValue() ).getRGB() ) );
                }
            } );
            final SimpleObserver updateWaterLocation = new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getWaterShape() ) );
                }
            };
            waterTower.tankBottomCenter.addObserver( updateWaterLocation );
            waterTower.fluidVolume.addObserver( updateWaterLocation );
            setPickable( false );
        }} );

        //Slider panel covering the hole
        addChild( new PImage( BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "panel.png" ), 50 ) ) {{
            final SimpleObserver updatePanelLocation = new SimpleObserver() {
                public void update() {
                    ImmutableVector2D viewPoint = transform.modelToView( waterTower.panelOffset.getValue().getAddedInstance( waterTower.tankBottomCenter.getValue() ) );
                    setOffset( viewPoint.getX(), viewPoint.getY() - getFullBounds().getHeight() );
                }
            };
            waterTower.panelOffset.addObserver( updatePanelLocation );
            waterTower.tankBottomCenter.addObserver( updatePanelLocation );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    if ( event.getDeltaRelativeTo( getParent() ).getHeight() > 0 ) {
                        waterTower.panelOffset.setValue( new ImmutableVector2D( WaterTower.TANK_RADIUS, 0 ) );
                    }
                    else if ( event.getDeltaRelativeTo( getParent() ).getHeight() < 0 ) {
                        waterTower.panelOffset.setValue( new ImmutableVector2D( WaterTower.TANK_RADIUS, 2 ) );
                    }
                }
            } );
            addInputEventListener( new CursorHandler( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) ) );
        }} );
    }
}
