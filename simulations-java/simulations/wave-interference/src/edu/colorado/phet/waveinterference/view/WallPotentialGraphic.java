// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.waveinterference.WallPotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Created by: Sam
 * Feb 7, 2008 at 1:33:32 PM
 */
public class WallPotentialGraphic extends PNode {
    private WallPotential wallPotential;
    private LatticeScreenCoordinates latticeScreenCoordinates;

    public WallPotentialGraphic( WallPotential wallPotential, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.wallPotential = wallPotential;
        this.latticeScreenCoordinates = latticeScreenCoordinates;

        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        wallPotential.addListener( new WallPotential.Listener() {
            public void changed() {
                update();
            }
        } );
        update();
        addInputEventListener( new CursorHandler() );
    }

    private void update() {
        removeAllChildren();

        Line2D.Double line = new Line2D.Double(
                latticeScreenCoordinates.toScreenCoordinates( wallPotential.getSource().x, wallPotential.getSource().y ),
                latticeScreenCoordinates.toScreenCoordinates( wallPotential.getDestination().x, wallPotential.getDestination().y ) );
        final float strokeWidth = (float) latticeScreenCoordinates.toScreenCoordinatesDifferentialX( wallPotential.getThickness() );
        Shape shape = new BasicStroke( strokeWidth ).createStrokedShape( line );
        PNode body = new PhetPPath( shape, SlitPotentialGraphic.BARRIER_FILL, SlitPotentialGraphic.BARRIER_STROKE, SlitPotentialGraphic.BARRIER_STROKE_PAINT );
        addChild( body );

        PDragEventHandler handler = new MyPDragEventHandler( true, true );
        body.addInputEventListener( handler );
        final Color handleColor = new Color( 177, 120, 40 );
        {
            Ellipse2D.Double handle = new Ellipse2D.Double();
            Point2D screenCtr = latticeScreenCoordinates.toScreenCoordinates( wallPotential.getSource().x, wallPotential.getSource().y );
            handle.setFrameFromCenter( screenCtr.getX(), screenCtr.getY(),
                                       screenCtr.getX() + strokeWidth * 0.8 / 2, screenCtr.getY() + strokeWidth * 0.8 / 2 );
            PNode handleNode = new PhetPPath( handle, handleColor, new BasicStroke(), Color.black );
            addChild( handleNode );
            handleNode.addInputEventListener( new MyPDragEventHandler( true, false ) );

        }
        {
            Ellipse2D.Double handle = new Ellipse2D.Double();
            Point2D screenCtr = latticeScreenCoordinates.toScreenCoordinates( wallPotential.getDestination().x, wallPotential.getDestination().y );
            handle.setFrameFromCenter( screenCtr.getX(), screenCtr.getY(),
                                       screenCtr.getX() + strokeWidth * 0.8 / 2, screenCtr.getY() + strokeWidth * 0.8 / 2 );

            PNode handleNode = new PhetPPath( handle, handleColor, new BasicStroke(), Color.black );
            addChild( handleNode );
            handleNode.addInputEventListener( new MyPDragEventHandler( false, true ) );
        }
    }

    private class MyPDragEventHandler extends PDragEventHandler {
        private Point2D dragStartPt;
        private Point origStart;
        private Point origEnd;
        private boolean changeStart;
        private boolean changeEnd;

        private MyPDragEventHandler( boolean changeStart, boolean changeEnd ) {
            this.changeStart = changeStart;
            this.changeEnd = changeEnd;
        }

        protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            this.dragStartPt = event.getCanvasPosition();
            origStart = wallPotential.getSource();
            origEnd = wallPotential.getDestination();
        }

        protected void drag( PInputEvent event ) {
            Point2D pos = event.getCanvasPosition();
            double latticeDX = latticeScreenCoordinates.toLatticeCoordinatesDifferentialX( pos.getX() - dragStartPt.getX() );
            double latticeDY = latticeScreenCoordinates.toLatticeCoordinatesDifferentialY( pos.getY() - dragStartPt.getY() );
            if ( changeStart ) {
                wallPotential.setSrcPoint( new Point( origStart.x + (int) latticeDX, origStart.y + (int) latticeDY ) );
            }
            if ( changeEnd ) {
                wallPotential.setDstPoint( new Point( origEnd.x + (int) latticeDX, origEnd.y + (int) latticeDY ) );
            }
        }
    }
}
