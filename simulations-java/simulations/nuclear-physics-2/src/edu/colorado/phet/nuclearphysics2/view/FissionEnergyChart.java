/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.util.DoubleArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PLine;


/**
 * This class displays a chart that depicts the energy curve for a nuclear
 * fission of a single nucleus.
 *
 * @author John Blanco
 */
public class FissionEnergyChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants for controlling the appearance of the chart.
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 8f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.WHITE;
    private static final double  SCREEN_FRACTION_Y = 0.5d;
    private static final double  AXES_LINE_WIDTH = 2f;
    private static final double  ORIGIN_PROPORTION_X = 0.1d;
    private static final double  ORIGIN_PROPORTION_Y = 0.85d;
    private static final float   ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  ENERGY_LINE_STROKE = new BasicStroke( ENERGY_LINE_STROKE_WIDTH );
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.GREEN;
    private static final Color   POTENTIAL_ENERGY_LINE_COLOR = new Color(0x990099);
    private static final Color   LEGEND_BORDER_COLOR = Color.GRAY;
    private static final float   LEGEND_BORDER_STROKE_WIDTH = 4f;
    private static final Stroke  LEGEND_BORDER_STROKE = new BasicStroke( LEGEND_BORDER_STROKE_WIDTH );
    private static final Color   LEGEND_BACKGROUND_COLOR = new Color(0xffffe0);
    private static final double  LEGEND_SIZE_X = 190.0;
    private static final double  LEGEND_SIZE_Y = 65.0;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PPath _potentialEnergyWell;
    private DoubleArrowNode _xAxisOfGraph;
    private DoubleArrowNode _yAxisOfGraph;
    private PText _yAxisLabelHigh;
    private PText _yAxisLabelLow;
    private PText _xAxisLabel;
    private PPath _legend;
    private PText _potentialEnergyLabel;
    private PText _totalEnergyLabel;
    private PLine _potentialEnergyLegendLine;
    private PLine _totalEnergyLegendLine;
    
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

    public FissionEnergyChart() {

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
                15, 10, AXES_LINE_WIDTH);
        _xAxisOfGraph.setPaint( Color.black );
        _xAxisOfGraph.setStrokePaint( Color.black );
        addChild( _xAxisOfGraph);
        
        _yAxisOfGraph = new DoubleArrowNode( new Point2D.Double( 0, 0), new Point2D.Double( 100, 100), 
                15, 10, AXES_LINE_WIDTH);
        _yAxisOfGraph.setPaint( Color.black );
        _yAxisOfGraph.setStrokePaint( Color.black );
        addChild( _yAxisOfGraph);
                
        // Initialize attributes of the line that shows the total energy level.
        
        _totalEnergyLine = new PLine();
        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLine.setStroke( ENERGY_LINE_STROKE );
        addChild( _totalEnergyLine);
        
        // Initialize attributes of the curve that shows the potential energy well.
        
        _potentialEnergyWell = new PPath();
        _potentialEnergyWell.setStrokePaint( POTENTIAL_ENERGY_LINE_COLOR );
        _potentialEnergyWell.setStroke( ENERGY_LINE_STROKE );
        addChild( _potentialEnergyWell);
        
        // Add the text for the Y axis.
        _yAxisLabelHigh = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.YAxisLabel3" ));
        _yAxisLabelHigh.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _yAxisLabelHigh.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabelHigh );
        _yAxisLabelLow = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.YAxisLabel4" ));
        _yAxisLabelLow.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _yAxisLabelLow.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabelLow );
        
        // Add the text for the X axis.
        _xAxisLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.XAxisLabel" ));
        _xAxisLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        addChild( _xAxisLabel );
        
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
        
        _potentialEnergyLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.legend.PotentialEnergy") );
        _potentialEnergyLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _legend.addChild( _potentialEnergyLabel );
        
        _totalEnergyLegendLine = new PLine ();
        _totalEnergyLegendLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLegendLine.setStroke( ENERGY_LINE_STROKE );
        _legend.addChild( _totalEnergyLegendLine );
        
        _totalEnergyLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.legend.TotalEnergy") );
        _totalEnergyLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _legend.addChild( _totalEnergyLabel );
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
                _graphOriginY - 0.04 * (_graphOriginY - _usableAreaOriginY));
        _yAxisLabelHigh.setOffset( _yAxisLabelLow.getOffset().getX() - (1.0 * _yAxisLabelLow.getFont().getSize()), 
                _graphOriginY - 0.04 * (_graphOriginY - _usableAreaOriginY));

        _xAxisLabel.setOffset( xAxisTipPt.getX() - _xAxisLabel.getWidth() - _xAxisOfGraph.getHeadHeight() - 10,
                _graphOriginY + 5);

        
        // Position the line that represents the total energy.
        
        _totalEnergyLine.removeAllPoints();
        _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.40 );
        _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.40 );

        // Position the curve that represents the potential energy.
        drawPotentialEnergyWell();
        
        // Lay out the legend.  It will appear just above the x axis on the
        // far right.
        double legendOriginX = _usableWidth - LEGEND_SIZE_X - BORDER_STROKE_WIDTH;
        double legendOriginY = _graphOriginY - LEGEND_SIZE_Y - (0.15 * _usableHeight);
        
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
    
    //-----------------------------------------------------------------------
    // Below are some parameters that can be used to tweak the potential
    // energy curve without needing to dig too deeply into the code that
    // actually draws the curve.
    //-----------------------------------------------------------------------

    // Amount of the visible x axis that the curve spans.
    public static final double CURVE_WIDTH_FACTOR = 0.85;
    
    // Overall height of the curve.
    public static final double CURVE_HEIGHT_FACTOR = 0.9;
    
    // Width of the energy well as a proportion of the usable width of the
    // chart.  Note that, at least for now, this must be manually coordinated
    // with the width of the nucleus on the canvas.
    public static final double ENERGY_WELL_WIDTH_FACTOR = 0.08;
    
    // Depth of the energy well as a function of usable height of the chart.
    public static final double ENERGY_WELL_DEPTH_FACTOR = 0.45;
    
    /**
     * This method draws the line that represents the potential energy well
     * for the nucleus.  It is being made into its own method because it is
     * complex and will simplify reading the rest of the update code, and
     * because at some point it may be refactored to make it more "real" as
     * opposed to an approximated drawing.
     */
    private void drawPotentialEnergyWell(){
        
        // Clear the existing curve.
        _potentialEnergyWell.reset();
                
        double x1, y1, ctrlx1, ctrly1, x2, y2, ctrlx2, ctrly2;
        
        // Figure out the positioning of the first curve.
        
        x1 = _usableAreaOriginX + ((1.0 - CURVE_WIDTH_FACTOR) * _usableWidth);
        y1 = _graphOriginY;
        ctrlx1 = x1 + (0.2 * _usableWidth);
        ctrly1 = y1;
        x2 = _usableWidth/2 - (ENERGY_WELL_WIDTH_FACTOR * _usableWidth);
        y2 = _usableAreaOriginY + (1.0 - CURVE_HEIGHT_FACTOR) * _usableHeight;
        ctrlx2 = x2 - (0.05 * _usableWidth);
        ctrly2 = y2;
        
        CubicCurve2D leftmostCurve = new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
        
        _potentialEnergyWell.append( leftmostCurve, true );
        
        // Calculate the position of the 2nd curve, i.e. the one that goes
        // from the left peak down to the center of the energy well.
        
        x1 = x2;
        y1 = y2;
        ctrlx1 = x1 + (0.03 * _usableWidth);
        ctrly1 = y1;
        x2 = _usableWidth/2;
        y2 = _usableAreaOriginY + (ENERGY_WELL_DEPTH_FACTOR * _usableHeight);
        ctrlx2 = x2 - (0.03 * _usableWidth);
        ctrly2 = y2;

        CubicCurve2D leftCenterCurve = new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
        
        _potentialEnergyWell.append( leftCenterCurve, true );
        
        // Calculate the position of the 3rd curve, which is the one that
        // goes from the bottom of the energy well up to the right peak.
        
        x1 = x2;
        y1 = y2;
        ctrlx1 = x1 + (0.03 * _usableWidth);
        ctrly1 = y1;
        x2 = _usableWidth/2 + (ENERGY_WELL_WIDTH_FACTOR * _usableWidth);
        y2 = _usableAreaOriginY + (1.0 - CURVE_HEIGHT_FACTOR) * _usableHeight;
        ctrlx2 = x2 - (0.03 * _usableWidth);
        ctrly2 = y2;

        CubicCurve2D rightCenterCurve = new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
        
        _potentialEnergyWell.append( rightCenterCurve, true );
        
        // Calculate the position of the 4th and last curve, which is the one
        // that goes from the right peak of the energy well back to the x axis.

        x1 = x2;
        y1 = y2;
        ctrlx1 = x1 + (0.05 * _usableWidth);
        ctrly1 = y1;
        x2 = _usableAreaOriginX + (CURVE_WIDTH_FACTOR * _usableWidth);
        y2 = _graphOriginY;
        ctrlx2 = x2 - (0.20 * _usableWidth);
        ctrly2 = y2;

        CubicCurve2D rightmostCurve = new CubicCurve2D.Double(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2);
        
        _potentialEnergyWell.append( rightmostCurve, true );
        
    }
}
