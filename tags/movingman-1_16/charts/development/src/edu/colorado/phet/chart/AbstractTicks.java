/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;

import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Oct 8, 2004
 * Time: 11:37:47 PM
 * Copyright (c) Oct 8, 2004 by Sam Reid
 */
public abstract class AbstractTicks extends AbstractGrid {
    private int tickHeight = 6;
    private NumberFormat format = new DecimalFormat( "#.#" );
    private Font font = new Font( "Lucida Sans", 0, 12 );
    private FontMetrics fontMetrics;
    private boolean showLabels = true;
    private GraphicLayerSet tickGraphics;
    private GraphicLayerSet labelGraphics;

    public AbstractTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing ) {
        this( chart, orientation, stroke, color, tickSpacing, 0 );
    }

    public AbstractTicks( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
        fontMetrics = chart.getComponent().getFontMetrics( font );
        tickGraphics = new GraphicLayerSet( chart.getComponent() );
        labelGraphics = new GraphicLayerSet( chart.getComponent() );
        addGraphic( tickGraphics );
        addGraphic( labelGraphics );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
        update();
    }

    protected void update() {
        labelGraphics.clear();
        tickGraphics.clear();
        Chart chart = getChart();
        if( getOrientation() == HORIZONTAL ) {
            double[] gridLines = getGridLines( getCrossesOtherAxisAt(), chart.getRange().getMinX(), chart.getRange().getMaxX(), getSpacing() );
            for( int i = 0; i < gridLines.length; i++ ) {
                double gridLineX = gridLines[i];
                int x = chart.transformX( gridLineX );
                int y = getHorizontalTickY();
                Line2D.Double line = new Line2D.Double( x, y - tickHeight / 2, x, y + tickHeight / 2 );
                PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
                tickGraphics.addGraphic( lineGraphic );

                String string = format.format( gridLineX );
                PhetTextGraphic labelGraphic = new PhetTextGraphic( chart.getComponent(), font, string, getColor() );
                labelGraphic.setLocation( x - labelGraphic.getWidth() / 2, y + tickHeight / 2 );
                labelGraphics.addGraphic( labelGraphic );
            }
        }
        else if( getOrientation() == VERTICAL ) {
            double[] gridLines = getGridLines( getCrossesOtherAxisAt(), chart.getRange().getMinY(), chart.getRange().getMaxY(), getSpacing() );
            for( int i = 0; i < gridLines.length; i++ ) {
                double gridLineY = gridLines[i];
                int x = getVerticalTickX();
                int y = chart.transformY( gridLineY );
                Line2D.Double line = new Line2D.Double( x - tickHeight / 2, y, x + tickHeight / 2, y );
                PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
                tickGraphics.addGraphic( lineGraphic );

                String string = format.format( gridLineY );
                PhetTextGraphic labelGraphic = new PhetTextGraphic( chart.getComponent(), font, string, getColor() );
                labelGraphic.setLocation( x - tickHeight / 2 - labelGraphic.getWidth(), y - labelGraphic.getHeight() / 2 );
                labelGraphics.addGraphic( labelGraphic );
            }
        }
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels( boolean showLabels ) {
        this.showLabels = showLabels;
        update();
    }

    public void setFont( Font font ) {
        this.font = font;
        update();
    }

    public void setFormatter( DecimalFormat formatter ) {
        this.format = formatter;
        update();
    }

    public void setTickHeight( int tickHeight ) {
        this.tickHeight = tickHeight;
        update();
    }

    public void setNumberFormat( NumberFormat numberFormat ) {
        this.format = numberFormat;
        update();
    }

    public void setLabelsVisible( boolean visible ) {
        showLabels = visible;
        update();
    }

    public abstract int getVerticalTickX();

    public abstract int getHorizontalTickY();

}
