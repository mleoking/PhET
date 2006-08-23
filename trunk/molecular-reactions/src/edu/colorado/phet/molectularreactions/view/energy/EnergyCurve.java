/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molectularreactions.view.energy;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.PNode;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * EnergyCurve
 * <p>
 * The curve is made up of three PPaths: One for the flat line to on the left,
 * one for the curve in the center, and one for the flat line on the right.
 * They are drawn and handled separately, and they each have their own mouse handlers.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class EnergyCurve extends PNode {
    private double peakLevel;

    private PPath leftFloor;
    private PPath rightFloor;
    private PPath centralCurve;
    double x1;
    double x2;
    double x3;

    /**
     *
     * @param width Width of the entire curve
     * @param color
     */
    EnergyCurve( double width, Color color ) {

        x1 = width * 0.4;
        x2 = width * 0.5;
        x3 = width * 0.6;

        leftFloor = new PPath();
        leftFloor.setPathTo( new Line2D.Double( 0, 0, x1, 0 ) );
        addChild( leftFloor );
        leftFloor.setStrokePaint( color );
        leftFloor.setStroke( new BasicStroke( 3 ) );
        leftFloor.addInputEventListener( new FloorMouseHandler( leftFloor ) );

        rightFloor = new PPath();
        rightFloor.setPathTo( new Line2D.Double( x3, 0, width, 0 ) );
        addChild( rightFloor );
        rightFloor.setStrokePaint( color );
        rightFloor.setStroke( new BasicStroke( 3 ) );
        rightFloor.addInputEventListener( new FloorMouseHandler( rightFloor ) );

        centralCurve = new PPath();
        addChild( centralCurve );
        centralCurve.setStrokePaint( color );
        centralCurve.setStroke( new BasicStroke( 3 ) );
        centralCurve.addInputEventListener( new PeakMouseHandler( x2 - 5, x2 + 5));
        updateCentralCurve();
    }

    /**
     * Updates the central curve
     */
    private void updateCentralCurve() {
        DoubleGeneralPath centralPath = new DoubleGeneralPath();
        double leftLevel = leftFloor.getOffset().getY();
        double rightLevel = rightFloor.getOffset().getY();
        centralPath.moveTo( x1, leftLevel );
        centralPath.curveTo( x1 + ( x2 - x1 ) * 0.33, leftLevel,
                             x1 + ( x2 - x1 ) * 0.66, peakLevel,
                             x2, peakLevel );
        centralPath.curveTo( x2 + ( x3 - x2 ) * 0.33, peakLevel,
                             x2 + ( x3 - x2 ) * 0.66, rightLevel,
                             x3, rightLevel );
        centralCurve.setPathTo( centralPath.getGeneralPath() );
    }

    void setLeftLevel( double leftLevel ) {
        leftFloor.setOffset( leftFloor.getOffset().getX(), leftLevel );
        updateCentralCurve();
    }

    void setPeakLevel( double peakLevel ) {
        this.peakLevel = peakLevel;
        updateCentralCurve();
    }

    void setRightLevel( double rightLevel ) {
        rightFloor.setOffset( rightFloor.getOffset().getX(), rightLevel );
        updateCentralCurve();
    }


    /**
     * Handles mousing on the left and right floors. Each floor gets
     * an instance
     */
    private class FloorMouseHandler extends PBasicInputEventHandler {
        PNode pNode;

        public FloorMouseHandler( PNode pNode ) {
            this.pNode = pNode;
        }

        public void mouseEntered( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( new Cursor( Cursor.N_RESIZE_CURSOR ) );
        }

        public void mouseExited( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( Cursor.getDefaultCursor() );
        }

        public void mouseDragged( PInputEvent event ) {
            double dy = event.getDelta().getHeight();
            pNode.setOffset( pNode.getOffset().getX(),
                             pNode.getOffset().getY() + dy );
            updateCentralCurve();
        }
    }

    /**
     * Handles mousing on the peak of the curve
     */
    private class PeakMouseHandler extends PBasicInputEventHandler {
        PNode pNode;
        private double xMin;
        private double xMax;

        /**
         * The parameters are used to make sure that curve only responds to the mouse when
         * the mouse is near the curve's center point.
         * @param xMin  The minimum x that should respond to the mouse
         * @param xMax  The maximum x that should respond to the mouse
         */
        public PeakMouseHandler( double xMin, double xMax ) {
            this.xMin = xMin;
            this.xMax = xMax;
        }

        public void mouseEntered( PInputEvent event ) {
            double eventX = event.getPositionRelativeTo( centralCurve.getParent() ).getX();
            if( eventX >= xMin && eventX <= xMax ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.N_RESIZE_CURSOR ) );
            }
        }

        public void mouseExited( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( Cursor.getDefaultCursor() );
        }

        public void mouseDragged( PInputEvent event ) {
            double dy = event.getDelta().getHeight();
            setPeakLevel( peakLevel + dy );
            updateCentralCurve();
        }
    }
}
