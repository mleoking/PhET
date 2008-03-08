/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

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

    // Constants for controlling look and feel.
    private static final Color   BORDER_COLOR = Color.gray;
    private static final float   BORDER_STROKE_WIDTH = 10f;
    private static final Stroke  BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color   BACKGROUND_COLOR = Color.white;
    private static final double  SCREEN_FRACTION_Y = 0.4d;
    private static final float   TOTAL_ENERGY_LINE_STROKE_WIDTH = 2f;
    private static final Stroke  TOTAL_ENERGY_LINE_STROKE = new BasicStroke( TOTAL_ENERGY_LINE_STROKE_WIDTH );
    private static final Color   TOTAL_ENERGY_LINE_COLOR = Color.green;

    // References to the various components of the chart.
    private PPath _borderNode;
    private PLine _totalEnergyLine;
    private PLine _potentialEnergyWell;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaRadiationChart(double canvasWidth, double canvasHeight) {

        // Create the border for this chart.
        
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );
        
        
        // Add the line that shows the total energy level.
        
        _totalEnergyLine = new PLine();
        _totalEnergyLine.setStrokePaint( TOTAL_ENERGY_LINE_COLOR );
        _totalEnergyLine.setStroke( TOTAL_ENERGY_LINE_STROKE );
        _totalEnergyLine.addPoint( 0, BORDER_STROKE_WIDTH, 20 );
        _totalEnergyLine.addPoint( 1, canvasWidth, 20 );
        addChild( _totalEnergyLine);
        
        // Add the line that shows the potential energy well.
        
        _potentialEnergyWell = new PLine();
        _potentialEnergyWell.setStrokePaint( Color.pink );
        _potentialEnergyWell.setStroke( TOTAL_ENERGY_LINE_STROKE );
        addChild( _potentialEnergyWell);
        

        updateBounds( canvasWidth, canvasHeight );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    private void updateBounds( double canvasWidth, double canvasHeight ) {
        
        double chartOriginX = BORDER_STROKE_WIDTH;
        double chartOriginY = canvasHeight-(canvasHeight*SCREEN_FRACTION_Y);
        double chartWidth   = canvasWidth-(BORDER_STROKE_WIDTH*2);
        double chartHeight  = canvasHeight*SCREEN_FRACTION_Y-BORDER_STROKE_WIDTH;
        
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                chartOriginX,
                chartOriginY,
                chartWidth,
                chartHeight,
                20,
                20 ) );
        
        _totalEnergyLine.removeAllPoints();
        _totalEnergyLine.addPoint( 0, chartOriginX + BORDER_STROKE_WIDTH, chartOriginY + chartHeight/3 );
        _totalEnergyLine.addPoint( 1, chartOriginX + chartWidth - BORDER_STROKE_WIDTH, chartOriginY + chartHeight/3 );

        // TODO: This needs refinement.
        _potentialEnergyWell.removeAllPoints();
        _potentialEnergyWell.addPoint( 0, chartWidth/2 - 0.1*chartWidth, chartOriginY + chartHeight/4 );
        _potentialEnergyWell.addPoint( 1, chartWidth/2 - 0.1*chartWidth, chartOriginY + 200 );
        _potentialEnergyWell.addPoint( 2, chartWidth/2 + 0.1*chartWidth, chartOriginY + 200 );
        _potentialEnergyWell.addPoint( 3, chartWidth/2 + 0.1*chartWidth, chartOriginY + chartHeight/4 );
    }

    public void componentResized( double width, double height ) {
        updateBounds( width, height );
    }
}