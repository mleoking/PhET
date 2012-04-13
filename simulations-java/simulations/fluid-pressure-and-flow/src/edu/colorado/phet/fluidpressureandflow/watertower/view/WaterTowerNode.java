// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor;
import edu.colorado.phet.fluidpressureandflow.watertower.model.WaterTower;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.HANDLE;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PANEL;

/**
 * @author Sam Reid
 */
public class WaterTowerNode extends PNode {

    public WaterTowerNode( final ModelViewTransform transform, final WaterTower waterTower, final Property<Double> fluidDensity ) {
        //Legs
        //in the back layer to prevent sticking into the water tank
        addChild( new PhetPPath( Color.black ) {{
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getSupportShape() ) );
                }
            } );
        }} );

        //Handle
        addChild( new PImage( HANDLE ) {{
            addInputEventListener( new RelativeDragHandler( this, transform, waterTower.tankBottomCenter, new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D modelLocation ) {
                    return new Point2D.Double( waterTower.tankBottomCenter.get().getX(), MathUtil.clamp( 0, modelLocation.getY(), WaterTower.MAX_Y ) );
                }
            } ) {
                @Override protected void sendMessage( final Point2D modelPoint ) {
                    super.sendMessage( modelPoint );
                    SimSharingManager.sendUserMessage( UserComponents.waterTowerHandle, UserComponentTypes.sprite, UserActions.drag, ParameterSet.parameterSet( ParameterKeys.x, modelPoint.getX() ).with( ParameterKeys.y, modelPoint.getY() ) );
                }
            } );
            scale( 0.45 );
            addInputEventListener( new CursorHandler( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) ) );
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D tankTopCenter = waterTower.getTankTopCenter();
                    final Point2D view = transform.modelToView( tankTopCenter );
                    setOffset( view.getX(), view.getY() - getFullBounds().getHeight() );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.gray ) {{ // tank interior
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getTankShape() ) );
                }
            } );
        }} );

        //Tank border
        addChild( new PhetPPath( Color.black ) {{
            waterTower.tankBottomCenter.addObserver( new SimpleObserver() {
                public void update() {
                    final Area shape = new Area( new BasicStroke( 0.55f ).createStrokedShape( waterTower.getTankShape() ) );
                    shape.subtract( new Area( new Rectangle2D.Double( waterTower.getTankShape().getCenterX(), waterTower.getTankShape().getMinY(), 10, 1 ) ) );
                    //These numbers were sampled based on the size of max water drops, may need to be fine tuned if other parts of the sim change
                    shape.subtract( new Area( new Rectangle2D.Double( -3.6203504908994, waterTower.getTankShape().getCenterY(), 0.6203504908994 * 2, 10 ) ) );
                    shape.subtract( new Area( waterTower.getTankShape() ) );
                    setPathTo( transform.modelToView( shape ) );
                }
            } );
        }} );

        //water in the tower
        addChild( new PhetPPath( new Color( WaterColor.getTopColor( fluidDensity.get() ).getRGB() ) ) {{
            fluidDensity.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( new Color( WaterColor.getTopColor( fluidDensity.get() ).getRGB() ) );
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

        //water in the hole
        addChild( new PhetPPath( new Color( WaterColor.getTopColor( fluidDensity.get() ).getRGB() ) ) {{
            fluidDensity.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( new Color( WaterColor.getTopColor( fluidDensity.get() ).getRGB() ) );
                }
            } );
            final SimpleObserver updateWaterLocation = new SimpleObserver() {
                public void update() {
                    final Rectangle2D waterBounds = waterTower.getWaterShape().getBounds2D();
                    setPathTo( transform.modelToView( new Rectangle2D.Double( waterBounds.getMinX(), waterBounds.getMinY(),
                                                                              waterBounds.getWidth() + 0.55 / 2, Math.min( WaterTower.HOLE_SIZE, waterBounds.getHeight() ) ) ) );//SEE HOLE HEIGHT
                }
            };
            waterTower.tankBottomCenter.addObserver( updateWaterLocation );
            waterTower.fluidVolume.addObserver( updateWaterLocation );
            setPickable( false );
        }} );

        //Slider panel covering the hole
        addChild( new PImage( BufferedImageUtils.multiScaleToHeight( PANEL, 50 ) ) {{
            final SimpleObserver updatePanelLocation = new SimpleObserver() {
                public void update() {
                    ImmutableVector2D viewPoint = transform.modelToView( waterTower.panelOffset.get().getAddedInstance( waterTower.tankBottomCenter.get() ) );
                    setOffset( viewPoint.getX(), viewPoint.getY() - getFullBounds().getHeight() );
                }
            };
            waterTower.panelOffset.addObserver( updatePanelLocation );
            waterTower.tankBottomCenter.addObserver( updatePanelLocation );

            addInputEventListener( new SimSharingDragHandler( UserComponents.waterTowerDoor, true ) {
                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    if ( event.getDeltaRelativeTo( getParent() ).getHeight() > 0 ) {
                        waterTower.panelOffset.set( new ImmutableVector2D( WaterTower.PANEL_OFFSET, 0 ) );
                    }
                    else if ( event.getDeltaRelativeTo( getParent() ).getHeight() < 0 ) {
                        waterTower.panelOffset.set( new ImmutableVector2D( WaterTower.PANEL_OFFSET, 2 ) );
                    }
                }

                @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                    return super.getParametersForAllEvents( event ).with( ParameterKeys.y, waterTower.panelOffset.get().getY() );
                }
            } );
            addInputEventListener( new CursorHandler( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) ) );
        }} );
    }
}