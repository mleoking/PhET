/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;
import edu.colorado.phet.nuclearphysics.model.DaughterCompositeNucleus;
import edu.colorado.phet.nuclearphysics.module.fissiononenucleus.FissionOneNucleusModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.nodes.PLine;


/**
 * This class displays a chart that depicts the potential energy profile for a
 * nucleus of Uranium 235 and animates the energy level when the nucleus
 * absorbs a neutron and fissions as a result.
 *
 * @author John Blanco
 */
public class FissionEnergyChart extends PComposite {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 8f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.WHITE;
    private static final double  SCREEN_FRACTION_Y = 0.5d;
    private static final double  AXES_LINE_WIDTH = 1f;
    private static final Color   AXES_LINE_COLOR = Color.GRAY;
    private static final double  ORIGIN_PROPORTION_X = 0.1d;
    private static final double  ORIGIN_PROPORTION_Y = 0.85d;
    private static final float   ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  ENERGY_LINE_STROKE = new BasicStroke( ENERGY_LINE_STROKE_WIDTH );
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.ORANGE;
    private static final Color   POTENTIAL_ENERGY_LINE_COLOR = Color.BLUE;
    private static final Color   LEGEND_BORDER_COLOR = Color.GRAY;
    private static final float   LEGEND_BORDER_STROKE_WIDTH = 4f;
    private static final Stroke  LEGEND_BORDER_STROKE = new BasicStroke( LEGEND_BORDER_STROKE_WIDTH );
    private static final Color   LEGEND_BACKGROUND_COLOR = new Color(0xffffe0);
    private static final double  LEGEND_SIZE_X = 190.0;
    private static final double  LEGEND_SIZE_Y = 65.0;
    private static final double  NUCLEI_SCALING_FACTOR = 0.15;
    private static final double  ARROW_HEAD_HEIGHT = 9;
    private static final double  ARROW_HEAD_WIDTH = 7;
    private static final double  TICK_MARK_LENGTH = 5;
    private static final float   TICK_MARK_WIDTH = 2;
    private static final Stroke  TICK_MARK_STROKE = new BasicStroke( TICK_MARK_WIDTH );
    private static final Font    TICK_MARK_LABEL_FONT = new PhetFont( Font.PLAIN, 12 );
    private static final Color   TICK_MARK_COLOR = AXES_LINE_COLOR;
    
    // Important Note: The Y-axis of this graph is not using real units,
    // such as MeV, because if it did the proportions would be too hard to
    // see.  It is thus "hollywooded" to look correct.  The following
    // constants control the scale of the Y-axis and the important points
    // within the graph in the Y dimension.
    private static final double Y_AXIS_TOTAL_POSITVE_SPAN = 100;
    private static final double BOTTOM_OF_ENERGY_WELL = 65;
    private static final double PEAK_OF_ENERGY_WELL = 80;
    
    // Constants the control dynamic chart behavior.
    private static final int NUM_UPWARD_STEPS_FOR_NUCLEUS = 5;
    
    // Possible state values for tracking the relevant state of the model.
    private static final int STATE_IDLE = 0;
    private static final int STATE_FISSIONING = 1;
    private static final int STATE_FISSIONED = 2;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PPath _potentialEnergyWell;
    private DoubleArrowNode _xAxisOfGraph;
    private ArrowNode _yAxisOfGraph;
    private PText _yAxisLabelHigh;
    private PText _yAxisLabelLow;
    private PText _xAxisLabel;
    private PPath _legend;
    private PText _potentialEnergyLabel;
    private PText _totalEnergyLabel;
    private PLine _potentialEnergyLegendLine;
    private PLine _totalEnergyLegendLine;
    private PNode _unfissionedNucleusImage;
    private PNode _largerDaughterNucleusImage;
    private PNode _smallerDaughterNucleusImage;
    private PPath _xAxisTickMark;
    private PText _xAxisTickMarkLabel;
    
    // Variables used for positioning nodes within the chart.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;
    double _energyWellWidth;
    
    // References to the model and the canvas.
    private FissionOneNucleusModel _model;
    private PhetPCanvas _canvas;
    
    // State variable for compact tracking of model state.
    private int _fissionState = STATE_IDLE;
    
    // Original number of neutrons in nucleus, which is used for determining
    // what happened when we receive notification of an atomic weight change.
    private int _origNumNeturons;
    
    // Random number generator, used to create the appearance of 'jitter' in
    // the motion of the nucleus.
    Random _rand = new Random();
    
    // Reference to the daughter nucleus that exists after fission occurs.
    DaughterCompositeNucleus _daughterNucleus;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public FissionEnergyChart(FissionOneNucleusModel model, PhetPCanvas canvas) {
        
        _model = model;
        _canvas = canvas;
        _origNumNeturons = _model.getAtomicNucleus().getNumNeutrons();
        setPickable( false );
        
        // Register as a clock listener, since we need to be clocked in order
        // to do the nucleus animation during the fissioning process.
        _model.getClock().addClockListener( new ClockAdapter(){
            
            /**
             * Clock tick handler - causes the model to move forward one
             * increment in time.
             */
            public void clockTicked(ClockEvent clockEvent){
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset(ClockEvent clockEvent){
                // Ignore this reset and count on the main model to reset us.
            }
        });
        
        // Register as a listener to the nucleus so that we can see when it
        // decays and when it is reset.
        _model.getAtomicNucleus().addListener( new AtomicNucleus.Adapter(){
            public void nucleusChangeEvent(AtomicNucleus atomicNucleus, int numProtons, int numNeutrons, 
                    ArrayList byProducts){
                if (byProducts != null){
                    for (int i = 0; i < byProducts.size(); i++){
                        if (byProducts.get( i ) instanceof DaughterCompositeNucleus){
                            _daughterNucleus = (DaughterCompositeNucleus)byProducts.get(i);
                        }
                    }
                    assert _daughterNucleus != null;
                    _fissionState = STATE_FISSIONED;
                    _unfissionedNucleusImage.setVisible( false );
                    _largerDaughterNucleusImage.setVisible( true );
                    _smallerDaughterNucleusImage.setVisible( true );
                    updateNucleiPositions();
                }
                else if (numNeutrons > _origNumNeturons){
                    // This event signifies the capture of a neutron and thus
                    // the start of the fissioning process.
                    _fissionState = STATE_FISSIONING;
                    updateNucleiPositions();
                }
                else if (numNeutrons == _origNumNeturons){
                    // This event signifies the nucleus being reset to its
                    // original configuration.
                    _fissionState = STATE_IDLE;
                    _daughterNucleus = null;
                    _unfissionedNucleusImage.setVisible( true );
                    _largerDaughterNucleusImage.setVisible( false );
                    _smallerDaughterNucleusImage.setVisible( false );
                    updateNucleiPositions();
                }
                else {
                    // This should never happen, debug it if it does.
                    System.err.println("Error: Unable to interpret decay event.");
                    assert false;
                }
            }
        });

        // Create the border for this chart.
        
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );
        
        // Initialize the arrow nodes that will comprise the axes of the
        // chart.  The initial sizes and positions are arbitrary, and the
        // real sizes and positions will be set when the bounds are updated.

        _xAxisOfGraph = new DoubleArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, AXES_LINE_WIDTH);
        _xAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _xAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        addChild( _xAxisOfGraph);
        
        _yAxisOfGraph = new ArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, AXES_LINE_WIDTH);
        _yAxisOfGraph.setPaint( AXES_LINE_COLOR );
        _yAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
        addChild( _yAxisOfGraph);
                
        // Initialize attributes of the line that shows the total energy level.
        
        _totalEnergyLine = new PLine();
        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLine.setStroke( ENERGY_LINE_STROKE );
        addChild( _totalEnergyLine);
        
        // Initialize attributes of the curve that shows the potential energy well.
        
        _potentialEnergyWell = new PPath(){
            // Override the rendering hints so that the segmented line can be
            // drawn smoothly.
            public void paint(PPaintContext paintContext){
                Graphics2D g2 = paintContext.getGraphics();
                RenderingHints oldHints = g2.getRenderingHints();
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                super.paint( paintContext );
                g2.setRenderingHints( oldHints );
            }
        };
        _potentialEnergyWell.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyWell.setStroke( ENERGY_LINE_STROKE );
        addChild( _potentialEnergyWell);
        
        // Add the text for the Y axis.
        _yAxisLabelHigh = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_Y_AXIS_LABEL_3);
        _yAxisLabelHigh.setFont( new PhetFont( Font.PLAIN, 14 ) );
        _yAxisLabelHigh.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabelHigh );
        _yAxisLabelLow = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_Y_AXIS_LABEL_4);
        _yAxisLabelLow.setFont( new PhetFont( Font.PLAIN, 14 ) );
        _yAxisLabelLow.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabelLow );
        
        // Add the text for the X axis.
        _xAxisLabel = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_X_AXIS_LABEL );
        _xAxisLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
        addChild( _xAxisLabel );
        
        // Add the tick mark and its label for the X axis.
        _xAxisTickMark = new PPath();
        _xAxisTickMark.setStroke( TICK_MARK_STROKE );
        _xAxisTickMark.setStrokePaint( TICK_MARK_COLOR );
        addChild( _xAxisTickMark );
        _xAxisTickMarkLabel = new PText("0");
        _xAxisTickMarkLabel.setFont( TICK_MARK_LABEL_FONT );
        addChild( _xAxisTickMarkLabel );
        
        // Create the legend (i.e. key) node for the chart.
        _legend = new PPath();
        _legend.setStroke( LEGEND_BORDER_STROKE );
        _legend.setStrokePaint( LEGEND_BORDER_COLOR );
        _legend.setPaint( LEGEND_BACKGROUND_COLOR );
        addChild( _legend );
        
        // Add text and graphics to the legend.
        _potentialEnergyLegendLine = new PLine ();
        _potentialEnergyLegendLine.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyLegendLine.setStroke( ENERGY_LINE_STROKE );
        _legend.addChild( _potentialEnergyLegendLine );
        
        _potentialEnergyLabel = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_POTENTIAL_ENERGY );
        _potentialEnergyLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
        _legend.addChild( _potentialEnergyLabel );
        
        _totalEnergyLegendLine = new PLine ();
        _totalEnergyLegendLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLegendLine.setStroke( ENERGY_LINE_STROKE );
        _legend.addChild( _totalEnergyLegendLine );
        
        _totalEnergyLabel = new PText( NuclearPhysicsStrings.POTENTIAL_PROFILE_TOTAL_ENERGY );
        _totalEnergyLabel.setFont( new PhetFont( Font.PLAIN, 14 ) );
        _legend.addChild( _totalEnergyLabel );
        
        // Add the images for the nuclei.  Not all are initially visible.
        
        _unfissionedNucleusImage = NuclearPhysicsResources.getImageNode("Uranium Nucleus Small.png");
        _unfissionedNucleusImage.setScale( NUCLEI_SCALING_FACTOR );
        addChild(_unfissionedNucleusImage);
        
        _largerDaughterNucleusImage = NuclearPhysicsResources.getImageNode("Larger Daughter Nucleus.png");
        _largerDaughterNucleusImage.setScale(  NUCLEI_SCALING_FACTOR );
        _largerDaughterNucleusImage.setVisible( false );
        addChild(_largerDaughterNucleusImage);
        
        _smallerDaughterNucleusImage = NuclearPhysicsResources.getImageNode("Smaller Daughter Nucleus.png");
        _smallerDaughterNucleusImage.setScale(  NUCLEI_SCALING_FACTOR );
        _smallerDaughterNucleusImage.setVisible( false );
        addChild(_smallerDaughterNucleusImage);
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation is changed.
     * 
     * @param canvasWidth - The overall width in pixels of the canvas on which
     * this chart appears.
     * @param canvasHeight - The overall height in pixels of the canvas on
     * which this chart appears.  The chart will only consume part of this
     * height.
     */
    private void updateBounds( double canvasWidth, double canvasHeight ) {
        
        // Recalculate the usable area and origin for the chart.
        
        _usableAreaOriginX = BORDER_STROKE_WIDTH;
        _usableAreaOriginY = canvasHeight - ( canvasHeight * SCREEN_FRACTION_Y ) + BORDER_STROKE_WIDTH;
        _usableWidth       = canvasWidth - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = canvasHeight * SCREEN_FRACTION_Y - ( BORDER_STROKE_WIDTH * 2);
        _graphOriginX      = _usableWidth * ORIGIN_PROPORTION_X + _usableAreaOriginX;
        _graphOriginY      = _usableHeight * ORIGIN_PROPORTION_Y + _usableAreaOriginY;
        
        // Recalculate the width of the energy well.
        
        double nucleusDiameter = _model.getAtomicNucleus().getDiameter();
        PDimension nucleusDiameterDim = new PDimension(nucleusDiameter, nucleusDiameter);
        _canvas.getPhetRootNode().worldToScreen( nucleusDiameterDim );
        _energyWellWidth = nucleusDiameterDim.getWidth();
        
        // Set up the border for the graph.
        
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                _usableAreaOriginX,
                _usableAreaOriginY,
                _usableWidth,
                _usableHeight,
                20,
                20 ) );
        
        // Position the axes for the graph.
        Point2D xAxisTailPt = new Point2D.Double( _usableAreaOriginX + BORDER_STROKE_WIDTH, _graphOriginY );
        Point2D xAxisTipPt = new Point2D.Double( _usableAreaOriginX + _usableWidth - BORDER_STROKE_WIDTH, _graphOriginY );
        _xAxisOfGraph.setTipAndTailLocations( xAxisTailPt, xAxisTipPt );
        Point2D yAxisTailPt = new Point2D.Double( _graphOriginX, _usableAreaOriginY + BORDER_STROKE_WIDTH );
        Point2D yAxisTipPt = new Point2D.Double( _graphOriginX, _usableAreaOriginY + _usableHeight - BORDER_STROKE_WIDTH );
        _yAxisOfGraph.setTipAndTailLocations( yAxisTailPt, yAxisTipPt );
        
        // Position the labels for the axes.

        _yAxisLabelLow.setOffset( _graphOriginX - (1.5 * _yAxisLabelLow.getFont().getSize()), 
                _graphOriginY - 0.12 * (_graphOriginY - _usableAreaOriginY));
        _yAxisLabelHigh.setOffset( _yAxisLabelLow.getOffset().getX() - (1.0 * _yAxisLabelLow.getFont().getSize()), 
                _graphOriginY - 0.12 * (_graphOriginY - _usableAreaOriginY));

        _xAxisLabel.setOffset( xAxisTipPt.getX() - _xAxisLabel.getWidth() - _xAxisOfGraph.getHeadHeight() - 10,
                _graphOriginY + 5);

        // Position the tick marks and labels.
        _xAxisTickMark.setPathTo( new Line2D.Double(_usableAreaOriginX/2 + _usableWidth/2, _graphOriginY, 
                _usableAreaOriginX/2 + _usableWidth/2, _graphOriginY - TICK_MARK_LENGTH));
        _xAxisTickMarkLabel.setOffset( _usableAreaOriginX/2 + _usableWidth/2 - (_xAxisTickMarkLabel.getWidth()/2), 
                _graphOriginY + (_xAxisTickMarkLabel.getHeight()/2) );
        
        // Position the line that represents the total energy.
        _totalEnergyLine.removeAllPoints();
        _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.40 );
        _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.40 );

        // Position the curve that represents the potential energy.
        drawPotentialEnergyWell();
        
        // Lay out the legend.  It will appear just above the x axis on the
        // far right.
        double legendOriginX = _usableWidth - LEGEND_SIZE_X - BORDER_STROKE_WIDTH;
        double legendOriginY = _graphOriginY - LEGEND_SIZE_Y - (0.18 * _usableHeight);
        
        _legend.setPathTo( new RoundRectangle2D.Double( 
                legendOriginX,
                legendOriginY,
                LEGEND_SIZE_X,
                LEGEND_SIZE_Y,
                10,
                10 ) );
        
        _potentialEnergyLegendLine.removeAllPoints();
        _potentialEnergyLegendLine.addPoint( 0, legendOriginX + 15, legendOriginY + 25 );
        _potentialEnergyLegendLine.addPoint( 1, legendOriginX + 40, legendOriginY + 25 );
        _potentialEnergyLabel.setOffset(legendOriginX + 50, legendOriginY + 15);
        
        _totalEnergyLegendLine.removeAllPoints();        
        _totalEnergyLegendLine.addPoint( 0, legendOriginX + 15, legendOriginY + 45 );
        _totalEnergyLegendLine.addPoint( 1, legendOriginX + 40, legendOriginY + 45 );
        _totalEnergyLabel.setOffset(legendOriginX + 50, legendOriginY + 35);
        
        // Update the positions of the nuclei.
        updateNucleiPositions();
        
    }

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param width - Width, in pixels, of the canvas on which this chart appears.
     * @param height - Height, in pixels, of the canvas on which this chart appears.
     */
    public void componentResized( double width, double height ) {
        updateBounds( width, height );
    }
    
    /**
     * This method draws the line that represents the potential energy well
     * for the nucleus.
     */
    private void drawPotentialEnergyWell(){
        
        // Clear the existing curve.
        _potentialEnergyWell.reset();
                
        double startX     = _usableAreaOriginX + (3 * BORDER_STROKE_WIDTH);
        double centerX    = _usableAreaOriginX/2 + _usableWidth/2;
        double endX       = _usableAreaOriginX + _usableWidth - (3*BORDER_STROKE_WIDTH);
        double xScreenPos = startX;

        // The following multiplier is used to scale the left and right tails
        // of the curve to values that make the visual representation reasonable.
        double tailMultiplier = (_energyWellWidth * PEAK_OF_ENERGY_WELL / 2);
        
        // Define the crossover zone between the calculation for the tails
        // and the calculation for the energy well.  This is arbitrarily
        // chosen to make the curve look good.
        double crossoverDistanceFromCenter = _energyWellWidth * 0.6;
        double crossoverZoneWidth = _energyWellWidth / 4;
        
        // Move to the starting point for the curve.
        double yGraphPos = (1/(centerX - xScreenPos)) * tailMultiplier;
        _potentialEnergyWell.moveTo( (float)xScreenPos, (float)convertGraphToScreenY( yGraphPos ) ); 

        // Draw the curve.
        for (xScreenPos = xScreenPos + 1; xScreenPos < endX; ){
            
            double xGraphPos = xScreenPos - centerX;
                
            if (xScreenPos < centerX - crossoverDistanceFromCenter - crossoverZoneWidth/2){
                // Left side (tail) of the curve.
                yGraphPos = (1/-xGraphPos) * tailMultiplier;
                _potentialEnergyWell.lineTo( (float)xScreenPos, (float)convertGraphToScreenY( yGraphPos ));
                xScreenPos+=5;
            }
            else if (xScreenPos < centerX - crossoverDistanceFromCenter + crossoverZoneWidth/2){
                // Crossing into the well.
                double wellWeightingFactor = 
                        computeWellWeightingFactor( centerX, xScreenPos, crossoverDistanceFromCenter, crossoverZoneWidth );
                yGraphPos = (((1/-xGraphPos) * tailMultiplier) * (1-wellWeightingFactor)) + 
                        (calculateWellValue( xGraphPos ) * (wellWeightingFactor));
                _potentialEnergyWell.lineTo( (float)xScreenPos, (float)convertGraphToScreenY( yGraphPos ));
                xScreenPos++;
            }
            else if (xScreenPos < centerX + crossoverDistanceFromCenter - crossoverZoneWidth/2){
                // Inside the well.
                yGraphPos = calculateWellValue( xGraphPos );
                _potentialEnergyWell.lineTo( (float)xScreenPos, (float)convertGraphToScreenY( yGraphPos ));
                xScreenPos++;
            }
            else if (xScreenPos < centerX + crossoverDistanceFromCenter + crossoverZoneWidth/2){
                // Crossing out of the well.
                double wellWeightingFactor = 
                    computeWellWeightingFactor( centerX, xScreenPos, crossoverDistanceFromCenter, crossoverZoneWidth );
                yGraphPos = (((1/xGraphPos) * tailMultiplier) * (1-wellWeightingFactor)) + 
                    (calculateWellValue( xGraphPos ) * (wellWeightingFactor));
                _potentialEnergyWell.lineTo( (float)xScreenPos, (float)convertGraphToScreenY( yGraphPos ));
                xScreenPos++;
            }
            else if (xScreenPos < endX){
                // Right side (tail) of the curve.
                yGraphPos = (1/xGraphPos) * tailMultiplier;
                _potentialEnergyWell.lineTo( (float)xScreenPos, (float)convertGraphToScreenY( yGraphPos ));
                xScreenPos+=5;
            }
            else{
                // Just increment the screen position so we will fall out of the loop.
                xScreenPos+=10;
            }
        }
    }

    /**
     * Compute the weighting factor that is used to "crossfade" between the tail
     * portion of the graph and the center energy well.
     * 
     * @param centerX
     * @param xScreenPos
     * @param crossoverDistanceFromCenter
     * @param crossoverZoneWidth
     * @return
     */
    private double computeWellWeightingFactor( double centerX, double xScreenPos, 
            double crossoverDistanceFromCenter, double crossoverZoneWidth ) {
        // The computation is not linear - it is made to be weighted more heavily on either end.
        double linearFactor = 1 - ((Math.abs(centerX - xScreenPos) -
                crossoverDistanceFromCenter + crossoverZoneWidth/2) / crossoverZoneWidth);
        return (-Math.cos(Math.PI * linearFactor) + 1) / 2;
    }

    private double calculateWellValue( double xGraphPos ) {
        return BOTTOM_OF_ENERGY_WELL + ((Math.cos(((xGraphPos*2/_energyWellWidth) - 1) * Math.PI) + 1) * 
              (PEAK_OF_ENERGY_WELL - BOTTOM_OF_ENERGY_WELL)/2);
    }
    
    /**
     * Move the nuclei to the appropriate position(s) on the chart.
     */
    private void updateNucleiPositions(){

        double xPos, yPos;
        
        switch (_fissionState){
        case STATE_IDLE:
            // Position the unfissioned nucleus image at the bottom of the
            // energy well.
            _unfissionedNucleusImage.setVisible( true );
            xPos = _usableAreaOriginX/2 + _usableWidth/2 - _unfissionedNucleusImage.getFullBounds().width / 2;
            yPos = convertGraphToScreenY( BOTTOM_OF_ENERGY_WELL ) - _unfissionedNucleusImage.getFullBounds().height / 2;
            _unfissionedNucleusImage.setOffset( xPos, yPos );
            
            // Position the total energy line, also at the bottom of the well.
            _totalEnergyLine.removeAllPoints();
            _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3 * BORDER_STROKE_WIDTH, yPos + 
                    _unfissionedNucleusImage.getFullBounds().height / 2 );
            _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3 * BORDER_STROKE_WIDTH, 
                    yPos + _unfissionedNucleusImage.getFullBounds().height / 2);

            break;
        
        case STATE_FISSIONING:
            // Move the unfissioned nucleus up toward the top of the energy
            // well.  Jitter it to create the impression of instability.

            xPos = _usableAreaOriginX/2 + _usableWidth/2 - _unfissionedNucleusImage.getFullBounds().width / 2;
            
            // Cause the nucleus to move upward.
            double nucleusBasePosY = convertGraphToScreenY( BOTTOM_OF_ENERGY_WELL );
            double nucleusTopPosY = convertGraphToScreenY( PEAK_OF_ENERGY_WELL ) - _unfissionedNucleusImage.getFullBoundsReference().height/2;
            if (_unfissionedNucleusImage.getOffset().getY() > nucleusTopPosY){
                yPos = _unfissionedNucleusImage.getOffset().getY() + 
                        ((nucleusTopPosY - nucleusBasePosY) / NUM_UPWARD_STEPS_FOR_NUCLEUS);
                if (yPos < nucleusTopPosY){
                    yPos = nucleusTopPosY;
                }
            }
            else {
                yPos = nucleusTopPosY;
            }
            
            // Create a bit of jitter along the x-axis to create a look of
            // instability.
            xPos += xPos * (_rand.nextDouble() - 0.5) * 0.10;
            _unfissionedNucleusImage.setOffset( xPos, yPos );
            
            // Move the total energy line up with the nucleus.
            _totalEnergyLine.removeAllPoints();
            _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3 * BORDER_STROKE_WIDTH, yPos + 
                    _unfissionedNucleusImage.getFullBounds().height / 2 );
            _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3 * BORDER_STROKE_WIDTH, 
                    yPos + _unfissionedNucleusImage.getFullBounds().height / 2);

            break;
        
        case STATE_FISSIONED:
            // Move the daughter nuclei based on their current distance from
            // the origin in the model.
            
            // Y position is the same for both nuclei - at the top of the energy well.
            yPos = convertGraphToScreenY( PEAK_OF_ENERGY_WELL );
            
            // Figure out X position of the larger daughter nucleus.
            Point2D nucleusPosition = _model.getAtomicNucleus().getPositionReference();
            PDimension nucleusDistanceDim = new PDimension(nucleusPosition.getX(), nucleusPosition.getX());
            _canvas.getPhetRootNode().worldToScreen( nucleusDistanceDim );
            xPos = _usableAreaOriginX/2 + _usableWidth/2 + nucleusDistanceDim.getWidth();
            if ((xPos < _usableAreaOriginX + _usableWidth) && (xPos > _usableAreaOriginX)){
                // Set the position for this image.
                _largerDaughterNucleusImage.setOffset( xPos - _largerDaughterNucleusImage.getFullBounds().width / 2, 
                        yPos - _largerDaughterNucleusImage.getFullBounds().height / 2 );
            }
            else{
                // Don't bother showing and updating the nucleus if it is off the chart.
                _largerDaughterNucleusImage.setVisible( false );
            }
            
            // Figure out X position of the smaller daughter nucleus.
            if (_daughterNucleus != null){
                nucleusPosition = _daughterNucleus.getPositionReference();
                nucleusDistanceDim = new PDimension(nucleusPosition.getX(), nucleusPosition.getX());
                _canvas.getPhetRootNode().worldToScreen( nucleusDistanceDim );
                xPos = _usableAreaOriginX/2 + _usableWidth/2 + nucleusDistanceDim.getWidth();
                if ((xPos < _usableAreaOriginX + _usableWidth) && (xPos > _usableAreaOriginX)){
                    // Set the position for this image.
                    _smallerDaughterNucleusImage.setOffset( xPos - _largerDaughterNucleusImage.getFullBounds().width / 2, 
                            yPos - _largerDaughterNucleusImage.getFullBounds().height / 2  );
                }
                else {
                    // Don't bother showing and updating the nucleus if it is off the chart.
                    _smallerDaughterNucleusImage.setVisible( false );
                }
            }
            
            // Position the total energy line at the top of the well.
            _totalEnergyLine.removeAllPoints();
            _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3 * BORDER_STROKE_WIDTH, 
                    convertGraphToScreenY( PEAK_OF_ENERGY_WELL ) );
            _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3 * BORDER_STROKE_WIDTH, 
                    convertGraphToScreenY( PEAK_OF_ENERGY_WELL ) );

            break;
        }
    }
    
    /**
     * Handle a tick of the simulation clock, which for this class will
     * sometimes mean the the graphical representations of the atomic nuclei
     * will need to be updated.
     * 
     * @param ce
     */
    private void handleClockTicked(ClockEvent ce){
        if (_fissionState == STATE_FISSIONING || _fissionState == STATE_FISSIONED){
            updateNucleiPositions();
        }
    }
    
    /**
     * Convert a Y axis value in graph units to a screen value.
     * @param yPositionGraph
     * @return
     */
    private double convertGraphToScreenY(double yPositionGraph){
        return (_graphOriginY - (yPositionGraph * ((_graphOriginY - _usableAreaOriginY)/Y_AXIS_TOTAL_POSITVE_SPAN)));
    }
    
}
