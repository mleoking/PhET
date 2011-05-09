// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:11:44 AM
 */

public class SlitPotentialGraphic extends PhetPNode {
    private TopViewBarrierVisibility topViewBarrierVisibility;
    private SlitPotential slitPotential;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private PDragEventHandler horizontalDragHandler;
    public static final Color BARRIER_FILL = new Color( 241, 216, 148 );
    public static final Stroke BARRIER_STROKE = new BasicStroke( 2 );
    public static final Paint BARRIER_STROKE_PAINT = Color.black;

    //todo remove assumption that all bars are distinct.
    public SlitPotentialGraphic( final SlitPotential slitPotential, final LatticeScreenCoordinates latticeScreenCoordinates ) {
        this( new TopViewBarrierVisibility() {
            public boolean isTopVisible() {
                return true;
            }
        }, slitPotential, latticeScreenCoordinates );
    }

    public SlitPotentialGraphic( TopViewBarrierVisibility topViewBarrierVisibility, final SlitPotential slitPotential, final LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.topViewBarrierVisibility = topViewBarrierVisibility;
        this.slitPotential = slitPotential;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        slitPotential.addListener( new SlitPotential.Listener() {
            public void slitsChanged() {
                update();
            }
        } );
        update();
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
//        addInputEventListener( new CursorHandler() );
        horizontalDragHandler = new PDragEventHandler() {
            private Point2D dragStartPt;
            int origLocation;

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                this.dragStartPt = event.getCanvasPosition();
                origLocation = slitPotential.getLocation();
            }

            protected void drag( PInputEvent event ) {
                Point2D pos = event.getCanvasPosition();
                double dx = pos.getX() - dragStartPt.getX();
                double latticeDX = latticeScreenCoordinates.toLatticeCoordinatesDifferentialX( dx );
                slitPotential.setLocation( (int) ( origLocation + latticeDX ) );
            }
        };

    }

    public void update() {
        removeAllChildren();
        if ( topViewBarrierVisibility.isTopVisible() ) {
            Rectangle[] r = slitPotential.getBarrierRectangles();
            for ( int i = 0; i < r.length; i++ ) {
                if ( !r[i].isEmpty() ) {
                    final PNode shapeNode = toShape( latticeScreenCoordinates.toScreenRect( r[i] ) );
                    final PNode node = new PNode();
                    node.addChild( shapeNode );

                    boolean[] handleEdges = getHandleEdges( i );
                    for ( int j = 0; j < handleEdges.length; j++ ) {
                        final PNode edgeDragHandle = getEdgeDragHandle( shapeNode, handleEdges[j] );
                        node.addChild( edgeDragHandle );
                    }

                    addChild( node );
                }
            }
        }
    }

    //todo: simplify
    private boolean[] getHandleEdges( int i ) {
        Rectangle[] r = slitPotential.getBarrierRectangles();
        if ( r.length == 0 || r.length == 1 ) {
            return new boolean[0];
        }
        else if ( r.length == 2 ) {
            if ( i == 0 ) {
                return new boolean[]{false};
            }
            else {
                return new boolean[]{true};
            }
        }
        else if ( r.length == 3 ) {
            if ( i == 0 ) {
                return new boolean[]{false};
            }
            else if ( i == 1 ) {
                return new boolean[]{false, true};
            }
            else if ( i == 2 ) {
                return new boolean[]{true};
            }
        }
        return new boolean[0];
    }

    private PNode getEdgeDragHandle( PNode shapeNode, final boolean topEdge ) {
        double y = topEdge ? shapeNode.getFullBounds().getMinY() : shapeNode.getFullBounds().getMaxY();
        Line2D.Double line = new Line2D.Double( shapeNode.getFullBounds().getX(), y, shapeNode.getFullBounds().getMaxX(), y );
        PPath edgeNode = new PhetPPath( line, new BasicStroke( 5 ), new Color( 0, 0, 0, 0 ) );

        PDragEventHandler slitSizeDragHandler = new PDragEventHandler() {
            private Point2D dragStartPt;
            int origWidth;

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                this.dragStartPt = event.getCanvasPosition();
                origWidth = slitPotential.getSlitWidth();
            }

            protected void drag( PInputEvent event ) {
                Point2D pos = event.getCanvasPosition();
                double dy = pos.getY() - dragStartPt.getY();
                double latticeDY = latticeScreenCoordinates.toLatticeCoordinatesDifferentialY( dy );
                double sign = topEdge ? +1 : -1;
                slitPotential.setSlitWidth( (int) ( origWidth + latticeDY * 2 * sign ) );
            }
        };
        edgeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        edgeNode.addInputEventListener( slitSizeDragHandler );
        return edgeNode;
    }

    public SlitPotential getSlitPotential() {
        return slitPotential;
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
    }

    public PNode toShape( Rectangle2D screenRect ) {
        PPath path = new PPath( screenRect );
        path.setPaint( BARRIER_FILL );
        path.setStroke( BARRIER_STROKE );
        path.setStrokePaint( BARRIER_STROKE_PAINT );
        path.addInputEventListener( horizontalDragHandler );
        path.addInputEventListener( new CursorHandler() );
        return path;
    }
}
