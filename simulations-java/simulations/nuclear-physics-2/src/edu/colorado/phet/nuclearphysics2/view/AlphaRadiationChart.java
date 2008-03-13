/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.util.DoubleArrowNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
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
    private static final Color   BORDER_COLOR = Color.DARK_GRAY;
    private static final float   BORDER_STROKE_WIDTH = 8f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.WHITE;
    private static final double  SCREEN_FRACTION_Y = 0.5d;
    private static final double  AXES_LINE_WIDTH = 2f;
    private static final double  ORIGIN_PROPORTION_X = 0.1d;
    private static final double  ORIGIN_PROPORTION_Y = 0.33d;
    private static final float   ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  ENERGY_LINE_STROKE = new BasicStroke( ENERGY_LINE_STROKE_WIDTH );
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.GREEN;
    private static final Color   POTENTIAL_ENERGY_LINE_COLOR = new Color(0x990099);
    private static final Color   LEGEND_BORDER_COLOR = Color.GRAY;
    private static final float   LEGEND_BORDER_STROKE_WIDTH = 4f;
    private static final Stroke  LEGEND_BORDER_STROKE = new BasicStroke( LEGEND_BORDER_STROKE_WIDTH );
    private static final Color   LEGEND_BACKGROUND_COLOR = new Color(0xffffe0);
    private static final double  LEGEND_SIZE_X = 190.0d;
    private static final double  LEGEND_SIZE_Y = 100.0d;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PPath _potentialEnergyWell;
    private DoubleArrowNode _xAxisOfGraph;
    private DoubleArrowNode _yAxisOfGraph;
    private PText _yAxisLabel;
    private PText _xAxisLabel;
    private PPath _legend;
    private PText _legendTitle;
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

    public AlphaRadiationChart() {

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
        
        // Add the text for the Y axis.  It was broken up into two pieces in
        // the original version of this sim, and the two pieces are still
        // maintained for backward compatibility with translations.
        _yAxisLabel = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.YAxisLabel1" ) +
                " " + NuclearPhysics2Resources.getString( "PotentialProfilePanel.YAxisLabel2" ));
        _yAxisLabel.setFont( new PhetDefaultFont( Font.PLAIN, 14 ) );
        _yAxisLabel.rotate( 1.5 * Math.PI );
        addChild( _yAxisLabel );
        
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
        
        // Add the title to the legend.
        _legendTitle = new PText( NuclearPhysics2Resources.getString( "PotentialProfilePanel.legend.title") + ":");
        _legendTitle.setFont( new PhetDefaultFont( Font.BOLD, 16 ) );
        _legend.addChild( _legendTitle );
        
        // Add other text and graphics to the legend.
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
        
        _yAxisLabel.setOffset( _graphOriginX - (1.5 * _yAxisLabel.getFont().getSize()), 
                _graphOriginY + (0.5 * (yAxisTipPt.getY() - _graphOriginY + _yAxisLabel.getWidth())));

        _xAxisLabel.setOffset( xAxisTipPt.getX() - _xAxisLabel.getWidth() - _xAxisOfGraph.getHeadHeight() - 10,
                _graphOriginY + 5);

        
        // Position the line that represents the total energy.
        
        _totalEnergyLine.removeAllPoints();
        _totalEnergyLine.addPoint( 0, _usableAreaOriginX + 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.07 );
        _totalEnergyLine.addPoint( 1, _usableAreaOriginX + _usableWidth - 3*BORDER_STROKE_WIDTH, _graphOriginY - _usableHeight * 0.07 );

        // Position the curve that represents the potential energy.

        _potentialEnergyWell.reset();
        
        Point2D leftPeakOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 0.85, 
                _graphOriginY - (0.20 * _usableHeight));
        Point2D leftBottomOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 0.85,
                _graphOriginY + (0.50 * _usableHeight));
        Point2D rightBottomOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.15,
                _graphOriginY + (0.50 * _usableHeight));
        Point2D rightPeakOfEnergyWell = new Point2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.15,
                _graphOriginY - (0.20 * _usableHeight));
        
        _potentialEnergyWell.append( new QuadCurve2D.Double(_usableAreaOriginX + 3 * BORDER_STROKE_WIDTH, 
                _graphOriginY - (0.03 * _usableHeight), (_usableAreaOriginX + (_usableWidth/2)) * 0.8,
                _graphOriginY - (0.05 * _usableHeight), (_usableAreaOriginX + (_usableWidth/2)) * 0.85, 
                _graphOriginY - (0.20 * _usableHeight)),
                false );
        _potentialEnergyWell.append( new Line2D.Double(leftPeakOfEnergyWell, leftBottomOfEnergyWell), false);
        _potentialEnergyWell.append( new Line2D.Double(leftBottomOfEnergyWell, rightBottomOfEnergyWell), false);
        _potentialEnergyWell.append( new Line2D.Double(rightBottomOfEnergyWell, rightPeakOfEnergyWell), false);
        _potentialEnergyWell.append( new QuadCurve2D.Double((_usableAreaOriginX + (_usableWidth/2)) * 1.15,
                _graphOriginY - (0.20 * _usableHeight), (_usableAreaOriginX + (_usableWidth/2)) * 1.2,
                _graphOriginY - (0.05 * _usableHeight), _usableAreaOriginX + _usableWidth - 3 * BORDER_STROKE_WIDTH,
                _graphOriginY - (0.03 * _usableHeight)), false );
        
        // Lay out the legend.
        double legendOriginX = _usableAreaOriginX + _usableWidth - LEGEND_SIZE_X - (2 * BORDER_STROKE_WIDTH);
        double legendOriginY = _usableAreaOriginY + _usableHeight - LEGEND_SIZE_Y - (2 * BORDER_STROKE_WIDTH);
        _legend.setPathTo( new RoundRectangle2D.Double( 
                legendOriginX,
                legendOriginY,
                LEGEND_SIZE_X,
                LEGEND_SIZE_Y,
                10,
                10 ) );
        
        _legendTitle.setOffset(legendOriginX + LEGEND_BORDER_STROKE_WIDTH, legendOriginY + LEGEND_BORDER_STROKE_WIDTH);
        
        _totalEnergyLegendLine.removeAllPoints();        
        _totalEnergyLegendLine.addPoint( 0, legendOriginX + 15, legendOriginY + 75 );
        _totalEnergyLegendLine.addPoint( 1, legendOriginX + 40, legendOriginY + 75 );
        _totalEnergyLabel.setOffset(legendOriginX + 50, legendOriginY + 65);
        
        _potentialEnergyLegendLine.removeAllPoints();        
        _potentialEnergyLegendLine.addPoint( 0, legendOriginX + 15, legendOriginY + 50 );
        _potentialEnergyLegendLine.addPoint( 1, legendOriginX + 40, legendOriginY + 50 );
        _potentialEnergyLabel.setOffset(legendOriginX + 50, legendOriginY + 40);
        
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
}