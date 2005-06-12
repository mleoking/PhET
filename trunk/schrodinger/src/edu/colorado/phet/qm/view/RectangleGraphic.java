/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class RectangleGraphic extends GraphicLayerSet {
    private PhetShapeGraphic areaGraphic;
    private SchrodingerPanel schrodingerPanel;
    private PhetShapeGraphic grabbablePart;
    private RectangularObject rectangularObject;

    public RectangleGraphic( SchrodingerPanel component, final RectangularObject rectangularObject, Color fill ) {
        super( component );
        this.schrodingerPanel = component;
        this.rectangularObject = rectangularObject;
        areaGraphic = new PhetShapeGraphic( component, null, fill, new BasicStroke( 1.0f ), Color.blue );//todo transparent green.
        addGraphic( areaGraphic );
        areaGraphic.addMouseInputListener( new ContinuousDrag( new LocationGetter() {
            public Point getLocation() {
                return rectangularObject.getLocation();
            }
        } ) );
        areaGraphic.setCursorHand();

        grabbablePart = new PhetShapeGraphic( component, new Rectangle( 0, 0, 10, 10 ), Color.yellow, new BasicStroke( 1 ), Color.green );
        addGraphic( grabbablePart );
        grabbablePart.addMouseInputListener( new CornerDrag() );
        grabbablePart.setCursorHand();

//        probDisplay = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), "", Color.red );
//        addGraphic( probDisplay );
        rectangularObject.addObserver( new SimpleObserver() {
            public void update() {
                RectangleGraphic.this.update();
            }
        } );
        update();
    }

    private void update() {
        Rectangle modelRect = rectangularObject.getBounds();
        ColorGrid grid = getColorGrid();
        Rectangle viewRect = grid.getViewRectangle( modelRect );
        areaGraphic.setShape( viewRect );
        grabbablePart.setLocation( (int)viewRect.getMaxX() - grabbablePart.getWidth() / 2, (int)viewRect.getMaxY() - grabbablePart.getHeight() / 2 );

//        double probPercent = rectangularObject.getProbability() * 100;
//        String formatted = format.format( probPercent );
//        probDisplay.setText( formatted + " %" );
//        probDisplay.setLocation( (int)viewRect.getX(), (int)viewRect.getY() );
    }

    private ColorGrid getColorGrid() {
        ColorGrid grid = schrodingerPanel.getColorGrid();
        return grid;
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
