// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view.energy;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 * EnergyCurve
 * <p/>
 * The curve is made up of three PPaths: One for the flat line to on the left,
 * one for the curve in the center, and one for the flat line on the right.
 * They are drawn and handled separately, and they each have their own mouse handlers.
 *
 * @author Ron LeMaster
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
     * @param energyProfile The energy profile to which this graphic will be bound.
     * @param size          size of the area in which the curve is to be drawn
     * @param color         Color of the curve
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
        Font labelFont = new PhetFont( PhetFont.getDefaultFontSize()+1,true );

        potentialEnergyLegend = new PText( MRConfig.RESOURCES.getLocalizedString( "EnergyView.Legend.potentialEnergy" ) );
        potentialEnergyLegend.setFont( labelFont );
        potentialEnergyLegend.setTextPaint( MRConfig.POTENTIAL_ENERGY_COLOR );
        addChild( potentialEnergyLegend );

        update( energyProfile );
    }

    /**
     * Retrieves the intersection of the energy profile with the specified
     * horizontal line.
     *
     * @param y            The y value of the line.
     * @param defaultValue A default value to use in case of no intersection.
     * @return The x coordinate of the intersection, or the default value if
     *         there was no intersection.
     */
    public double getIntersectionWithHorizontal( double y, double defaultValue ) {
        double[] coords = new double[6];

        GeneralPath path = centralCurve.getPathReference();

        PathIterator pathIterator = path.getPathIterator( null, 0.5 );

        double startX = 0.0, startY = 0.0;

        while( !pathIterator.isDone() ) {
            int code = pathIterator.currentSegment( coords );

            if( code == PathIterator.SEG_MOVETO ) {
                startX = coords[0];
                startY = coords[1];
            }
            else if( code == PathIterator.SEG_LINETO ) {
                double endX = coords[0];
                double endY = coords[1];

                double midX = startX + ( endX - startX ) / 2;

                Line2D.Double line = new Line2D.Double( startX, startY, endX, endY );

                if( line.getBounds().contains( midX, y ) ) {
                    return midX;
                }

                startX = endX;
                startY = endY;
            }
            else {
                assert code == PathIterator.SEG_CLOSE;
            }

            pathIterator.next();
        }

        return defaultValue;
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
         * @param event Input event
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

                constrainPeak();
            }
        }
    }

    private void constrainPeak() {
        double level = energyProfile.getPeakLevel();

        double max = Math.max( energyProfile.getRightLevel(), energyProfile.getLeftLevel() );

        energyProfile.setPeakLevel( Math.max( level, max ) );
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

                constrainPeak();
            }
        }
    }

    /**
     * Hanldes changes in the model
     */
    private class EnergyProfileChangeListener implements EnergyProfile.ChangeListener {
        public void stateChanged( ChangeEvent e ) {
            EnergyProfile energyProfile = (EnergyProfile)e.getSource();
            update( energyProfile );
        }
    }
}
