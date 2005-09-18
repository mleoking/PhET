/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class RectangleGraphic extends PNode {
    private SchrodingerPanel schrodingerPanel;

    private PPath areaGraphic;
    private PPath resizeCorner;
    private RectangularObject rectangularObject;

    public RectangleGraphic( SchrodingerPanel component, final RectangularObject rectangularObject, Color fill ) {
        super();
        this.schrodingerPanel = component;
        this.rectangularObject = rectangularObject;
        areaGraphic = new PPath();//todo transparent green.
        areaGraphic.setStrokePaint( Color.blue );
        areaGraphic.setStroke( new BasicStroke( 1.0f ) );
        areaGraphic.setPaint( fill );
        addChild( areaGraphic );
        areaGraphic.addInputEventListener( new ContinuousDrag( new LocationGetter() {
            public Point getLocation() {
                return rectangularObject.getLocation();
            }
        } ) );
        areaGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        resizeCorner = new PPath( new Rectangle( 0, 0, 10, 10 ) );
        resizeCorner.setPaint( Color.yellow );
        resizeCorner.setStroke( new BasicStroke( 1 ) );
        resizeCorner.setStrokePaint( Color.green );
        resizeCorner.addInputEventListener( new CornerDrag() );
        resizeCorner.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        addChild( resizeCorner );
        rectangularObject.addObserver( new SimpleObserver() {
            public void update() {
                RectangleGraphic.this.update();
            }
        } );
        update();
    }

    public void setResizeComponentVisible( boolean visible ) {
        this.resizeCorner.setVisible( visible );
    }

    private void update() {
        Rectangle modelRect = rectangularObject.getBounds();
        Rectangle viewRect = getViewRectangle( modelRect );
        areaGraphic.setPathTo( viewRect );
        resizeCorner.setOffset( (int)viewRect.getMaxX() - resizeCorner.getWidth() / 2, (int)viewRect.getMaxY() - resizeCorner.getHeight() / 2 );
    }

    protected ColorGrid getColorGrid() {
        ColorGrid grid = schrodingerPanel.getWavefunctionGraphic().getColorGrid();
        return grid;
    }

    public PPath getAreaGraphic() {
        return areaGraphic;
    }

    public Rectangle getViewRectangle( Rectangle modelRect ) {
        Rectangle gridRect = getColorGrid().getViewRectangle( modelRect );
        getSchrodingerPanel().getWavefunctionGraphic().localToGlobal( gridRect );
        globalToLocal( gridRect );
        localToParent( gridRect );
        return gridRect;
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private static interface LocationGetter {
        Point getLocation();
    }

    public class ContinuousDrag extends PBasicInputEventHandler {
        // implements java.awt.event.MouseListener
        Point2D startLocation;
        Point origLoc;
        LocationGetter locationGetter;

        public ContinuousDrag( LocationGetter locationGetter ) {
            this.locationGetter = locationGetter;
        }

        public void mousePressed( PInputEvent e ) {
            startLocation = new Point2D.Double( e.getPosition().getX(), e.getPosition().getY() );
            origLoc = locationGetter.getLocation();
        }


        // implements java.awt.event.MouseListener
        public void mouseReleased( PInputEvent e ) {
            startLocation = null;
            origLoc = null;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( PInputEvent e ) {
            if( startLocation == null ) {
                mousePressed( e );
            }
            else {
                Point2D fin = e.getPosition();

                Point2D.Double rel = new Point2D.Double( fin.getX() - startLocation.getX(), fin.getY() - startLocation.getY() );
                double dx = rel.x;
                double dy = rel.y;
                int modelDX = (int)( dx / getColorGrid().getBlockWidth() );
                int modelDY = (int)( dy / getColorGrid().getBlockHeight() );
                rectangularObject.setLocation( modelDX + origLoc.x, modelDY + origLoc.y );
            }
        }
    }

    public class CornerDrag extends PBasicInputEventHandler {
        Point2D.Double startLocation;
        Dimension origDim;

        public void mousePressed( PInputEvent e ) {
            startLocation = new Point2D.Double( e.getPosition().getX(), e.getPosition().getY() );
            origDim = rectangularObject.getDimension();
        }

        public void mouseReleased( PInputEvent e ) {
            startLocation = null;
            origDim = null;
        }

        public void mouseDragged( PInputEvent e ) {
            if( startLocation == null ) {
                mousePressed( e );
            }
            else {
                Point2D fin = e.getPosition();

                Point2D.Double rel = new Point2D.Double( fin.getX() - startLocation.x, fin.getY() - startLocation.y );
                double dx = rel.x;
                double dy = rel.y;

                int modelDX = (int)( dx / getColorGrid().getBlockWidth() );
                int modelDY = (int)( dy / getColorGrid().getBlockHeight() );
                rectangularObject.setDimension( origDim.width + modelDX, origDim.height + modelDY );
            }

        }
    }
}
