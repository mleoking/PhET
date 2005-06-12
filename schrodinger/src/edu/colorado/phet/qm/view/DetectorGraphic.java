/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.qm.model.Detector;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DetectorGraphic extends RectangleGraphic {
    private Detector detector;
    public PhetShapeGraphic areaGraphic;
    private SchrodingerPanel schrodingerPanel;
    private PhetShapeGraphic grabbablePart;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    public PhetTextGraphic probDisplay;

    public DetectorGraphic( SchrodingerPanel component, final Detector detector ) {
        super( component, detector );
        this.schrodingerPanel = component;
        this.detector = detector;
        areaGraphic = new PhetShapeGraphic( component, null, new Color( 0, 255, 0, 30 ), new BasicStroke( 1.0f ), Color.blue );//todo transparent green.
        addGraphic( areaGraphic );
        areaGraphic.addMouseInputListener( new ContinuousDrag( new LocationGetter() {
            public Point getLocation() {
                return detector.getLocation();
            }
        } ) );
        areaGraphic.setCursorHand();

        grabbablePart = new PhetShapeGraphic( component, new Rectangle( 0, 0, 10, 10 ), Color.yellow, new BasicStroke( 1 ), Color.green );
        addGraphic( grabbablePart );
        grabbablePart.addMouseInputListener( new CornerDrag() );
        grabbablePart.setCursorHand();

        probDisplay = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), "", Color.red );
        addGraphic( probDisplay );
        detector.addObserver( new SimpleObserver() {
            public void update() {
                DetectorGraphic.this.update();
            }
        } );
        update();
    }

    private void update() {
        Rectangle modelRect = detector.getBounds();
        ColorGrid grid = getColorGrid();
        Rectangle viewRect = grid.getViewRectangle( modelRect );
        areaGraphic.setShape( viewRect );
        grabbablePart.setLocation( (int)viewRect.getMaxX() - grabbablePart.getWidth() / 2, (int)viewRect.getMaxY() - grabbablePart.getHeight() / 2 );

        double probPercent = detector.getProbability() * 100;
        String formatted = format.format( probPercent );
        probDisplay.setText( formatted + " %" );
        probDisplay.setLocation( (int)viewRect.getX(), (int)viewRect.getY() );
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
                detector.setLocation( modelDX + origLoc.x, modelDY + origLoc.y );
            }

        }
    }

    public class CornerDrag extends MouseInputAdapter {
        Point startLocation;
        Dimension origDim;


        public void mousePressed( MouseEvent e ) {
            startLocation = new Point( e.getPoint() );
            origDim = detector.getDimension();
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
                detector.setDimension( origDim.width + modelDX, origDim.height + modelDY );
            }

        }
    }
}
