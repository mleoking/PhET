/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class RectangleGraphic extends PNode {
    private PPath areaGraphic;
    private SchrodingerPanel schrodingerPanel;
    private PPath grabbablePart;
    private RectangularObject rectangularObject;

    public RectangleGraphic( SchrodingerPanel component, final RectangularObject rectangularObject, Color fill ) {
        super();
        this.schrodingerPanel = component;
        this.rectangularObject = rectangularObject;
        areaGraphic = new PPath();//todo transparent green.
        // todo piccolo
        // component, null, fill, new BasicStroke( 1.0f ), Color.blue
        addChild( areaGraphic );
//        areaGraphic.addi( new ContinuousDrag( new LocationGetter() {
//            public Point getLocation() {
//                return rectangularObject.getLocation();
//            }
//        } ) );
//        areaGraphic.setCursorHand();

        grabbablePart = new PPath( new Rectangle( 0, 0, 10, 10 ) );
        //todo piccolo
        //, Color.yellow, new BasicStroke( 1 ), Color.green
        addChild( grabbablePart );
//        grabbablePart.addMouseInputListener( new CornerDrag() );
//        grabbablePart.setCursorHand();

//        probDisplay = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), "", Color.red );
//        addGraphic( probDisplay );
        rectangularObject.addObserver( new SimpleObserver() {
            public void update() {
                RectangleGraphic.this.update();
            }
        } );
        update();
    }

    public void setResizeComponentVisible( boolean visible ) {
        this.grabbablePart.setVisible( visible );
    }

    private void update() {
        Rectangle modelRect = rectangularObject.getBounds();
//        ColorGrid grid = getColorGrid();
//        Rectangle viewRect = grid.getViewRectangle( modelRect );
        Rectangle viewRect = getViewRectangle( modelRect );
        areaGraphic.setPathTo( viewRect );
        grabbablePart.setOffset( (int)viewRect.getMaxX() - grabbablePart.getWidth() / 2, (int)viewRect.getMaxY() - grabbablePart.getHeight() / 2 );

//        double probPercent = rectangularObject.getProbability() * 100;
//        String formatted = format.format( probPercent );
//        probDisplay.setText( formatted + " %" );
//        probDisplay.setLocation( (int)viewRect.getX(), (int)viewRect.getY() );
    }

    protected ColorGrid getColorGrid() {
        ColorGrid grid = schrodingerPanel.getWavefunctionGraphic().getColorGrid();
        return grid;
    }

    public PPath getAreaGraphic() {
        return areaGraphic;
    }

    public Rectangle getViewRectangle( Rectangle modelRect ) {
//        return getColorGrid().getViewRectangle( modelRect );
        Rectangle gridRect = getColorGrid().getViewRectangle( modelRect );
        Rectangle bounds = schrodingerPanel.waveAreaToScreen( gridRect );
        return bounds;
    }

//    public Rectangle getViewRectangleORIG( Rectangle modelRect ) {
//        Rectangle gridRect = getColorGrid().getViewRectangle( modelRect );
//        return schrodingerPanel.getWavefunctionGraphic().getNetTransform().createTransformedShape( gridRect ).getBounds();
//    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private static interface LocationGetter {
        Point getLocation();
    }

    public class ContinuousDrag extends MouseInputAdapter {
        // implements java.awt.event.MouseListener
        Point startLocation;
        Point origLoc;
        LocationGetter locationGetter;

        public ContinuousDrag( LocationGetter locationGetter ) {
            this.locationGetter = locationGetter;
        }

        public void mousePressed( MouseEvent e ) {
            startLocation = new Point( e.getPoint() );
            origLoc = locationGetter.getLocation();
        }

        // implements java.awt.event.MouseListener
        public void mouseReleased( MouseEvent e ) {
            startLocation = null;
            origLoc = null;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( MouseEvent e ) {
            if( startLocation == null ) {
                mousePressed( e );
            }
            else {
                Point fin = e.getPoint();

                Point rel = new Point( fin.x - startLocation.x, fin.y - startLocation.y );
                double dx = rel.x;
                double dy = rel.y;
                int modelDX = (int)( dx / getColorGrid().getBlockWidth() );
                int modelDY = (int)( dy / getColorGrid().getBlockHeight() );
                rectangularObject.setLocation( modelDX + origLoc.x, modelDY + origLoc.y );
            }

        }
    }

    public class CornerDrag extends MouseInputAdapter {
        Point startLocation;
        Dimension origDim;


        public void mousePressed( MouseEvent e ) {
            startLocation = new Point( e.getPoint() );
            origDim = rectangularObject.getDimension();
        }

        public void mouseReleased( MouseEvent e ) {
            startLocation = null;
            origDim = null;
        }

        public void mouseDragged( MouseEvent e ) {
            if( startLocation == null ) {
                mousePressed( e );
            }
            else {
                Point fin = e.getPoint();

                Point rel = new Point( fin.x - startLocation.x, fin.y - startLocation.y );
                double dx = rel.x;
                double dy = rel.y;

                int modelDX = (int)( dx / getColorGrid().getBlockWidth() );
                int modelDY = (int)( dy / getColorGrid().getBlockHeight() );
                rectangularObject.setDimension( origDim.width + modelDX, origDim.height + modelDY );
            }

        }
    }
}
