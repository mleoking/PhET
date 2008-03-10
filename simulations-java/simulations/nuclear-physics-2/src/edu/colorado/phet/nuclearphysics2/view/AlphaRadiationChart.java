/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics2.util.DoubleArrowNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.nodes.PLine;


/**
 * AlphaRadiationChart - This class displays the chart at the bottom of the
 * Alpha Radiation tab in this sim.  The chart shows the interaction between
 * the alpha particles and the energy barrier.
 *
 * @author John Blanco
 */
public class AlphaRadiationChart extends PComposite {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.gray;
    private static final float   BORDER_STROKE_WIDTH = 10f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.white;
    private static final double  SCREEN_FRACTION_Y = 0.4d;
    private static final double  AXES_LINE_WIDTH = 2f;
    private static final double  ORIGIN_PROPORTION_X = 0.1d;
    private static final double  ORIGIN_PROPORTION_Y = 0.33d;
    private static final float   TOTAL_ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  TOTAL_ENERGY_LINE_STROKE = new BasicStroke( TOTAL_ENERGY_LINE_STROKE_WIDTH );
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.green;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PPath _potentialEnergyWell;
    private DoubleArrowNode _xAxisOfGraph;
    private DoubleArrowNode _yAxisOfGraph;
    
    // Variables used to calculate scaling when the size of the chart changes.
    private double _canvasWidth;
    private double _canvasHeight;

    // Variable used for positioning nodes within the graph.
    double _usableAreaOriginX;
    double _usableAreaOriginY;
    double _usableWidth;
    double _usableHeight;
    double _graphOriginX;
    double _graphOriginY;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaRadiationChart(double canvasWidth, double canvasHeight) {

        // Figure out the usable area and the graph origin.
        
        _usableAreaOriginX = BORDER_STROKE_WIDTH;
        _usableAreaOriginY = canvasHeight - ( canvasHeight * SCREEN_FRACTION_Y ) + BORDER_STROKE_WIDTH;
        _usableWidth       = canvasWidth - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = canvasHeight * SCREEN_FRACTION_Y - ( BORDER_STROKE_WIDTH * 2);
        _graphOriginX      = _usableWidth * ORIGIN_PROPORTION_X + _usableAreaOriginX;
        _graphOriginY      = _usableHeight * ORIGIN_PROPORTION_Y + _usableAreaOriginY;
        
        // Save the height and width for scaling calculations when we get resized.
        _canvasHeight = canvasHeight;
        _canvasWidth  = canvasWidth;

        // Create the border for this chart.
        
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );
        
        // Initialize the arrow nodes that will comprise the axes of the chart.
        // TODO: The locations are currently hacked to make them look right initially,
        // but they don't resize correctly, so this needs work.
        _xAxisOfGraph = new DoubleArrowNode( new Point2D.Double( _usableAreaOriginX + 0.03 * _usableWidth, _graphOriginY ), 
                new Point2D.Double( _usableAreaOriginX + 0.85 * _usableWidth, _graphOriginY ), 
                15, 10, AXES_LINE_WIDTH);
        _xAxisOfGraph.setPaint( Color.black );
        _xAxisOfGraph.setStrokePaint( Color.black );
        addChild( _xAxisOfGraph);
        
        _yAxisOfGraph = new DoubleArrowNode(new Point2D.Double( _graphOriginX, _usableAreaOriginY + (_usableHeight * 0.20d) ),
                new Point2D.Double(_graphOriginX, _usableAreaOriginY + (_usableHeight * 1.0d)), 
                15, 10, AXES_LINE_WIDTH);
        _yAxisOfGraph.setPaint( Color.black );
        _yAxisOfGraph.setStrokePaint( Color.black );
        addChild( _yAxisOfGraph);
                
        // Initialize attributes of the line that shows the total energy level.
        
        _totalEnergyLine = new PLine();
        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLine.setStroke( TOTAL_ENERGY_LINE_STROKE );
        addChild( _totalEnergyLine);
        
        // Initialize attributes of the curve that shows the potential energy well.
        _potentialEnergyWell = new PPath();
        _potentialEnergyWell.append( new QuadCurve2D.Double(0,100,75,100,100,80), false );
        _potentialEnergyWell.append( new Line2D.Double(100,80,100,180), false );
        _potentialEnergyWell.append( new Line2D.Double(100,180,200,180), false );
        _potentialEnergyWell.append( new Line2D.Double(200,180,200,80), false );
        _potentialEnergyWell.append( new QuadCurve2D.Double(200,80,225,100,300,100), false );
        _potentialEnergyWell.setStrokePaint( new Color(0xff00ff) );
        _potentialEnergyWell.setStroke( TOTAL_ENERGY_LINE_STROKE );
        addChild( _potentialEnergyWell);
        
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
        
        if ((canvasWidth == _canvasWidth) && (canvasHeight == _canvasHeight))
        {
            // The size hasn't really changed, so don't do anything.
            return;
        }
        
        // Recalculate the usable area and origin for the chart.
        
        _usableAreaOriginX = BORDER_STROKE_WIDTH;
        _usableAreaOriginY = canvasHeight - ( canvasHeight * SCREEN_FRACTION_Y ) + BORDER_STROKE_WIDTH;
        _usableWidth       = canvasWidth - ( BORDER_STROKE_WIDTH * 2 );
        _usableHeight      = canvasHeight * SCREEN_FRACTION_Y - ( BORDER_STROKE_WIDTH * 2);
        _graphOriginX      = _usableWidth * ORIGIN_PROPORTION_X + _usableAreaOriginX;
        _graphOriginY      = _usableHeight * ORIGIN_PROPORTION_Y + _usableAreaOriginY;
        
        // Figure out the vertical and horizontal scaling that has occurred.
        double horizScalingFactor = canvasWidth/_canvasWidth;
        double vertScalingFactor  = canvasHeight/_canvasHeight;
        
        // Save height and width for the next update.
        _canvasWidth = canvasWidth;
        _canvasHeight = canvasHeight;
        
        // Set up the border for the graph.
        
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                _usableAreaOriginX,
                _usableAreaOriginY,
                _usableWidth,
                _usableHeight,
                20,
                20 ) );
        
        // Position the axes for the graph.
        
        //_xAxisOfGraph.scaleAboutPoint( horizScalingFactor, new Point2D.Double(100, 100) );
        //_xAxisOfGraph.setOffset( _usableAreaOriginX + 0.03 * _usableWidth, _graphOriginY );
        //_xAxisOfGraph.moveTo( (float)(_usableAreaOriginX + 0.03 * _usableWidth), (float)_graphOriginY );
        //_xAxisOfGraph.setOffset( 0, 0 );
        //_xAxisOfGraph.scale(horizScalingFactor);
        

        // Position the Total Energy line.
        _totalEnergyLine.removeAllPoints();
        _totalEnergyLine.addPoint( 0, _usableAreaOriginX + BORDER_STROKE_WIDTH, _usableAreaOriginY + _usableHeight/3 );
        _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - BORDER_STROKE_WIDTH, _usableAreaOriginY + _usableHeight/3 );

        // Position the curve that represents the potential energy.
        _potentialEnergyWell.reset();
        Point2D leftmostPoint = new Point2D.Double(_usableAreaOriginX, _graphOriginY - (0.05 * _usableHeight));
        Point2D leftPeakOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 0.8, 
                _graphOriginY - (0.20 * _usableHeight));
        Point2D leftCurveControlPoint = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 0.7, 
                _graphOriginY - (0.10 * _usableHeight));
        Point2D leftBottomOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 0.8,
                _graphOriginY + (0.50 * _usableHeight));
        Point2D rightBottomOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.2,
                _graphOriginY + (0.50 * _usableHeight));
        Point2D rightPeakOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.2,
                _graphOriginY - (0.20 * _usableHeight));
        Point2D rightCurveControlPoint = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.3, 
                _graphOriginY - (0.10 * _usableHeight));
        Point2D rightmostPoint = new Point2D.Double(_usableAreaOriginX + _usableWidth, 
                _graphOriginY - (0.05 * _usableHeight));
        
        _potentialEnergyWell.append( new QuadCurve2D.Double(_usableAreaOriginX, _graphOriginY - (0.05 * _usableHeight), 
                (_usableAreaOriginX + (_usableWidth/2)) * 0.7, _graphOriginY - (0.10 * _usableHeight),
                (_usableAreaOriginX + (_usableWidth/2)) * 0.8, _graphOriginY - (0.20 * _usableHeight)),
                false );
        _potentialEnergyWell.append( new Line2D.Double(leftPeakOfEnergyWell, leftBottomOfEnergyWell), false);
        _potentialEnergyWell.append( new Line2D.Double(leftBottomOfEnergyWell, rightBottomOfEnergyWell), false);
        _potentialEnergyWell.append( new Line2D.Double(rightBottomOfEnergyWell, rightPeakOfEnergyWell), false);
        _potentialEnergyWell.append( new QuadCurve2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.2,
                _graphOriginY - (0.20 * _usableHeight), (_usableAreaOriginX + (_usableWidth/2)) * 1.3,
                _graphOriginY - (0.10 * _usableHeight), _usableAreaOriginX + _usableWidth,
                _graphOriginY - (0.05 * _usableHeight)), false );

    }

    public void componentResized( double width, double height ) {
        updateBounds( width, height );
    }
}