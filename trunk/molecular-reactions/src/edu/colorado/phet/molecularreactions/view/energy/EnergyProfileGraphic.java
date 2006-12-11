/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * EnergyCurve
 * <p/>
 * The curve is made up of three PPaths: One for the flat line to on the left,
 * one for the curve in the center, and one for the flat line on the right.
 * They are drawn and handled separately, and they each have their own mouse handlers.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class EnergyProfileGraphic extends PNode {

    public static final BasicStroke LINE_STROKE = new BasicStroke( 3 );


    private double peakLevel;
    private double modelToViewScale;
    private PPath leftFloor;
    private PPath rightFloor;
    private PPath centralCurve;
    double x1;  // the x coord of the junction between the left floor and the central curve
    double x2;  // the x coord of the peak of the central curve
    double x3;  // the x coord of the junction between the central curve and the right floor
    double peakGrabTolerance = 10;
    private EnergyProfile energyProfile;
    private Dimension size;
    private boolean manipulable;
    private PNode leftFloorMouseIndicator;
    private PNode rightFloorMouseIndicator;
    private PNode centralMouseIndicator;
    private PText potentialEnergyLegend;

    /**
     * @param size  size of the area in which the curve is to be drawn
     * @param color
     */
    EnergyProfileGraphic( EnergyProfile energyProfile, Dimension size, Color color ) {
        this.energyProfile = energyProfile;
        this.modelToViewScale = size.getHeight() / MRConfig.MAX_REACTION_THRESHOLD;
        this.size = size;

        energyProfile.addChangeListener( new EnergyProfileChangeListener() );
        double width = size.getWidth();
        x2 = width * 0.5;
        x1 = x2 - energyProfile.getThresholdWidth() / 2;
        x3 = x2 + energyProfile.getThresholdWidth() / 2;

        leftFloor = new PPath();
        leftFloor.setPathTo( new Line2D.Double( 0, 0, x1, 0 ) );
        addChild( leftFloor );
        leftFloor.setStrokePaint( color );
        leftFloor.setStroke( LINE_STROKE );
        leftFloor.addInputEventListener( new FloorMouseHandler( leftFloor ) );

        rightFloor = new PPath();
        rightFloor.setPathTo( new Line2D.Double( x3, 0, width, 0 ) );
        addChild( rightFloor );
        rightFloor.setStrokePaint( color );
        rightFloor.setStroke( LINE_STROKE );
        rightFloor.addInputEventListener( new FloorMouseHandler( rightFloor ) );

        centralCurve = new PPath();
        addChild( centralCurve );
        centralCurve.setStrokePaint( color );
        centralCurve.setStroke( LINE_STROKE );
        centralCurve.addInputEventListener( new PeakMouseHandler( x2 - 5, x2 + 5 ) );

        // Double-headed arrows to indicate when the curve is manipulable
        createMouseIndicatorArrows();

        // Legends
        Font defaultFont = UIManager.getFont( "Label.font" );
        Font labelFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 1 );

        potentialEnergyLegend = new PText( SimStrings.get( "EnergyView.Legend.potentialEnergy" ) );
        potentialEnergyLegend.setFont( labelFont );
        potentialEnergyLegend.setTextPaint( MRConfig.ENERGY_PANE_TEXT_COLOR );
        addChild( potentialEnergyLegend );


        update( energyProfile );
    }

    private void createMouseIndicatorArrows() {
        // Arrow dimensions
        double a = 10;
        double b = 6;
        double c = 8;
        double d = 2;
        Arrow downArrow = new Arrow( new Point2D.Double( 0, 0 ),
                                     new Point2D.Double( 0, a ),
                                     b, c, d );
        Arrow upArrow = new Arrow( new Point2D.Double( 0, 0 ),
                                   new Point2D.Double( 0, -a ),
                                   b, c, d );
        GeneralPath path = downArrow.getShape();
        path.append( upArrow.getShape(), false );

        leftFloorMouseIndicator = createMouseIndicatorNode( path );
        rightFloorMouseIndicator = createMouseIndicatorNode( path );
        centralMouseIndicator = createMouseIndicatorNode( path );
    }

    private PNode createMouseIndicatorNode( GeneralPath path ) {
        PPath mouseIndicator = new PPath( path );
        mouseIndicator.setStrokePaint( MRConfig.ENERGY_PANE_TEXT_COLOR );
        addChild( mouseIndicator );
        mouseIndicator.setPickable( false );
        mouseIndicator.setVisible( false );
        return mouseIndicator;
    }

    private void update( EnergyProfile energyProfile ) {
        leftFloor.setOffset( leftFloor.getOffset().getX(),
                             size.getHeight() - energyProfile.getLeftLevel() * modelToViewScale );
        rightFloor.setOffset( rightFloor.getOffset().getX(),
                              size.getHeight() - energyProfile.getRightLevel() * modelToViewScale );
        peakLevel = size.getHeight() - energyProfile.getPeakLevel() * modelToViewScale;

        leftFloorMouseIndicator.setOffset( x1 / 2,
                                           leftFloor.getOffset().getY() );
        rightFloorMouseIndicator.setOffset( x3 + x1 / 2,
                                            rightFloor.getOffset().getY() );
        centralMouseIndicator.setOffset( x2, peakLevel );

        potentialEnergyLegend.setOffset( x3 + x1 - potentialEnergyLegend.getFullBounds().getWidth(),
                                         rightFloor.getOffset().getY() + 5 );
        updateCentralCurve();
    }

    /**
     * Updates the central curve
     */
    private void updateCentralCurve() {
        DoubleGeneralPath centralPath = new DoubleGeneralPath();
        double leftY = leftFloor.getOffset().getY();
        double rightY = rightFloor.getOffset().getY();
        centralPath.moveTo( x1, leftY );
        centralPath.curveTo( x1 + ( x2 - x1 ) * 0.33, leftY,
                             x1 + ( x2 - x1 ) * 0.66, peakLevel,
                             x2, peakLevel );
        centralPath.curveTo( x2 + ( x3 - x2 ) * 0.33, peakLevel,
                             x2 + ( x3 - x2 ) * 0.66, rightY,
                             x3, rightY );
        centralCurve.setPathTo( centralPath.getGeneralPath() );
    }

    public void setManipulable( boolean manipulable ) {
        this.manipulable = manipulable;

        // If the curve is manipulable, show arrows on it to let the user know
        leftFloorMouseIndicator.setVisible( manipulable );
        rightFloorMouseIndicator.setVisible( manipulable );
        centralMouseIndicator.setVisible( manipulable );
    }

    public void setLegendVisible( boolean visible ) {
        this.potentialEnergyLegend.setVisible( visible );
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
            if( manipulable ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.N_RESIZE_CURSOR ) );
            }
        }

        public void mouseExited( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( Cursor.getDefaultCursor() );
        }

        /**
         * Set the level in the model.
         *
         * @param event
         */
        public void mouseDragged( PInputEvent event ) {
            double eventY = event.getPositionRelativeTo( centralCurve.getParent() ).getY();
            if( eventY >= 0 && eventY <= size.height && manipulable ) {
                double dy = event.getDelta().getHeight();
                double eventX = event.getPositionRelativeTo( centralCurve.getParent() ).getX();
                if( eventX <= x1 ) {
                    double level = Math.min( size.height / modelToViewScale,
                                             Math.max( 0,
                                                       energyProfile.getLeftLevel() - dy / modelToViewScale ) );
                    energyProfile.setLeftLevel( level );
                }
                else if( eventX >= x3 ) {
                    double level = Math.min( size.height / modelToViewScale,
                                             Math.max( 0,
                                                       energyProfile.getRightLevel() - dy / modelToViewScale ) );
                    energyProfile.setRightLevel( level );
                }
            }
        }
    }

    private class Legend extends PNode {


        public Legend() {
            Font defaultFont = UIManager.getFont( "Label.font" );
            Font labelFont = new Font( defaultFont.getName(), Font.BOLD, defaultFont.getSize() + 1 );
            PText totalEnergyText = new PText( SimStrings.get( "EnergyView.Legend.totalEnergy" ) );
            totalEnergyText.setFont( labelFont );
            totalEnergyText.setTextPaint( MRConfig.ENERGY_PANE_TEXT_COLOR );
            addChild( totalEnergyText );
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
         *
         * @param xMin The minimum x that should respond to the mouse
         * @param xMax The maximum x that should respond to the mouse
         */
        public PeakMouseHandler( double xMin, double xMax ) {
            this.xMin = xMin;
            this.xMax = xMax;
        }

        public void mouseEntered( PInputEvent event ) {
            double eventX = event.getPositionRelativeTo( centralCurve.getParent() ).getX();
            if( eventX >= xMin && eventX <= xMax && manipulable ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.N_RESIZE_CURSOR ) );
            }
        }

        public void mouseExited( PInputEvent event ) {
            PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
            ppc.setCursor( Cursor.getDefaultCursor() );
        }

        public void mouseDragged( PInputEvent event ) {
            double eventY = event.getPositionRelativeTo( centralCurve.getParent() ).getY();
            if( eventY >= 0 && eventY <= size.height && manipulable ) {
                double dy = event.getDelta().getHeight();
                double level = Math.min( size.height / modelToViewScale,
                                         Math.max( 0,
                                                   energyProfile.getPeakLevel() - dy / modelToViewScale ) );
                energyProfile.setPeakLevel( level );
            }
        }
    }

    /**
     * Hanldes changes in the model
     */
    private class EnergyProfileChangeListener implements ChangeListener {
        public void stateChanged( ChangeEvent e ) {
            EnergyProfile energyProfile = (EnergyProfile)e.getSource();
            update( energyProfile );
        }
    }
}
