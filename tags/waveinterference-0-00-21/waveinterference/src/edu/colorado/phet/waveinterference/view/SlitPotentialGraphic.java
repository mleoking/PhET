/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.waveinterference.model.SlitPotential;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:11:44 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class SlitPotentialGraphic extends PhetPNode {
    private TopViewBarrierVisibility topViewBarrierVisibility;
    private SlitPotential slitPotential;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private PDragEventHandler dragHandler;

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
        addInputEventListener( new CursorHandler() );
        dragHandler = new PDragEventHandler() {
            private Point2D dragStartPt;
            int origLocation;

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                this.dragStartPt = event.getCanvasPosition();
                origLocation = slitPotential.getLocation();
//                System.out.println( "origLocation = " + origLocation );
            }

            protected void drag( PInputEvent event ) {
//                super.drag( event );
//                System.out.println( "SlitPotentialGraphic.drag" );
                Point2D pos = event.getCanvasPosition();
                double dx = pos.getX() - dragStartPt.getX();
//                System.out.println( "dx = " + dx );
                double latticeDX = latticeScreenCoordinates.toLatticeCoordinatesDifferentialX( dx );
//                System.out.println( "latticeDX = " + latticeDX );
                slitPotential.setLocation( (int)( origLocation + latticeDX ) );
            }
        };
        addInputEventListener( dragHandler );
    }

    protected PDragEventHandler getDragHandler() {
        return dragHandler;
    }

    public void update() {
        removeAllChildren();
        if( topViewBarrierVisibility.isTopVisible() ) {
            Rectangle[]r = slitPotential.getBarrierRectangles();
            for( int i = 0; i < r.length; i++ ) {
                Rectangle rectangle = r[i];
                if( !rectangle.isEmpty() ) {
                    addChild( toShape( latticeScreenCoordinates.toScreenRect( rectangle ) ) );
                }
            }
        }
    }

    public SlitPotential getSlitPotential() {
        return slitPotential;
    }

    public LatticeScreenCoordinates getLatticeScreenCoordinates() {
        return latticeScreenCoordinates;
    }

    public PNode toShape( Rectangle2D screenRect ) {

        PPath path = new PPath( screenRect );
        path.setPaint( new Color( 241, 216, 148 ) );
        path.setStroke( new BasicStroke( 2 ) );
        path.setStrokePaint( Color.black );
        return path;
    }
}
