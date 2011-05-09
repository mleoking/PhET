// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 9:04:48 PM
 */

public class CrossSectionGraphic extends PhetPNode {
    private WaveModel waveModel;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private PPath path;
    public static final BasicStroke STROKE = new BasicStroke( 4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{15, 10}, 0 );
    private int modelY = 30;

    public CrossSectionGraphic( final WaveModel waveModel, final LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.waveModel = waveModel;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.path = new PPath();

        path.setStroke( STROKE );
        path.setStrokePaint( Color.black );
        addChild( path );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                update();
            }
        } );
        update();
        PDragEventHandler dragHandler = new PDragEventHandler() {
            private Point2D dragStartPt;
            int origLocation;

            protected void startDrag( PInputEvent event ) {
                super.startDrag( event );
                this.dragStartPt = event.getCanvasPosition();
                origLocation = modelY;
            }

            protected void drag( PInputEvent event ) {
                Point2D pos = event.getCanvasPosition();
                double dy = pos.getY() - dragStartPt.getY();
                double latticeDX = latticeScreenCoordinates.toLatticeCoordinatesDifferentialY( dy );
                modelY = (int) ( origLocation + latticeDX );
                if ( modelY < 0 ) {
                    modelY = 0;
                }
                if ( modelY > waveModel.getHeight() - 1 ) {
                    modelY = waveModel.getHeight() - 1;
                }
                update();
                notifyListeners();
            }
        };
        addInputEventListener( dragHandler );
        addInputEventListener( new CursorHandler() );
    }

    public static interface Listener {
        void changed( int crossSectionY );
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
//        System.out.println( "CrossSectionGraphic.notifyListeners, modely=" + modelY );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).changed( modelY );
        }
    }

    private void update() {
        Rectangle2D rect = latticeScreenCoordinates.getScreenRect();
        final double y1 = latticeScreenCoordinates.toScreenCoordinates( 0, modelY ).getY();
        path.setPathTo( new Line2D.Double( rect.getMinX(), y1, rect.getMaxX(), y1 ) );
    }

    public void setColor( Color color ) {
        path.setStrokePaint( color );
    }
}
