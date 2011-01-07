// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.view.RelativeDragHandler;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterTowerNode extends PNode {
    public WaterTowerNode( final ModelViewTransform transform, final WaterTower waterTower ) {
        addChild( new PhetPPath( Color.gray, new BasicStroke( 5 ), Color.darkGray ) {{ // tank
            addInputEventListener( new RelativeDragHandler( this, transform, waterTower.getTankBottomCenter(), new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D modelLocation ) {
                    if ( modelLocation.getY() < 0 ) {
                        return new Point2D.Double( waterTower.getTankBottomCenter().getValue().getX(), 0 );
                    }
                    return new Point2D.Double( waterTower.getTankBottomCenter().getValue().getX(), modelLocation.getY() );
                }
            } ) );
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getTankShape() ) );
                }
            } );
            addInputEventListener( new CursorHandler() );
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
    }
}
