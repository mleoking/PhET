/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
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
    private NumberFormat numberFormat = new DecimalFormat( "#.#" );
    private Font font = new Font( "Lucida Sans", 0, 12 );
    private boolean showLabels = true;
    private GraphicLayerSet tickGraphics;
    private GraphicLayerSet labelGraphics;
    private LabelTable labelTable;
    private boolean rangeLabelsVisible = false;
    private NumberFormat rangeLabelsNumberFormat = new DecimalFormat( "#.#" );

    public AbstractTicks( Chart chart, Orientation orientation, Stroke stroke, Color color, double
            tickSpacing ) {
        this( chart, orientation, stroke, color, tickSpacing, 0 );
    }

    public AbstractTicks( Chart chart, Orientation orientation, Stroke stroke, Color color, double
            tickSpacing, double crossesOtherAxisAt ) {
        super( chart, orientation, stroke, color, tickSpacing, crossesOtherAxisAt );
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

        double[] gridLines = getVisibleGridlines();
        if( getOrientation().isVertical() ) {
            addVerticalStuff( gridLines );
        }
        else if( getOrientation().isHorizontal() ) {
            addHorizontalStuff( gridLines );
        }

    }

    private void addHorizontalStuff( double[] gridLines ) {
        Chart chart = getChart();
        for( int i = 0; i < gridLines.length; i++ ) {
            double gridLineY = gridLines[i];
            if( chart.getRange().containsY( gridLineY ) ) {
                addHorizontalTick( numberFormat, gridLineY );
            }
        }
        if( labelTable != null ) {
            double[] keys = labelTable.keys();
            for( int i = 0; i < keys.length; i++ ) {
                double gridLineY = keys[i];
                if( chart.getRange().containsY( gridLineY ) ) {

                    PhetGraphic labelGraphic = getVerticalLabelFromTable( gridLineY );
                    labelGraphic.setVisible( showLabels );
                    labelGraphics.addGraphic( labelGraphic );
                }
            }
        }
        if( rangeLabelsVisible ) {
            addHorizontalTick( rangeLabelsNumberFormat, chart.getRange().getMinY() );
            addHorizontalTick( rangeLabelsNumberFormat, chart.getRange().getMaxY() );
        }
    }

    private void addHorizontalTick( NumberFormat numberFormat, double gridLineY ) {
        Chart chart = getChart();
        int x = getVerticalTickX();
        int y = chart.transformY( gridLineY );
        Line2D.Double line = new Line2D.Double( x - tickHeight / 2, y, x + tickHeight / 2, y );
        PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
        tickGraphics.addGraphic( lineGraphic );

        if( labelTable == null ) {
            String string = numberFormat.format( gridLineY );
            PhetTextGraphic labelGraphic = new PhetTextGraphic( chart.getComponent(), font,
                                                                string, getColor() );
            labelGraphic.setLocation( x - tickHeight / 2 - labelGraphic.getWidth(), y - labelGraphic.getHeight() / 2 );
            labelGraphic.setVisible( showLabels );
            labelGraphics.addGraphic( labelGraphic );
        }
    }

    public PhetGraphic getHorizontalLabelFromTable( double gridLineX ) {
        int x = getChart().transformX( gridLineX );
        int y = getHorizontalTickY();

        PhetGraphic labelGraphic = labelTable.get( gridLineX );
        labelGraphic.setLocation( x - labelGraphic.getWidth() / 2, y + tickHeight + 1 );
        return labelGraphic;
    }

    private void addVerticalStuff( double[] gridLines ) {
        Chart chart = getChart();
        for( int i = 0; i < gridLines.length; i++ ) {
            double gridLineX = gridLines[i];
            if( chart.getRange().containsX( gridLineX ) ) {
                addVerticalTick( numberFormat, gridLineX );
            }
        }
        if( labelTable != null ) {
            double[] keys = labelTable.keys();
            for( int i = 0; i < keys.length; i++ ) {
                double gridLineX = keys[i];
                if( chart.getRange().containsX( gridLineX ) ) {
                    PhetGraphic labelGraphic = getHorizontalLabelFromTable( gridLineX );

                    labelGraphic.setVisible( showLabels );
                    labelGraphics.addGraphic( labelGraphic );
                }
            }
        }
        if( rangeLabelsVisible ) {
            addVerticalTick( rangeLabelsNumberFormat, chart.getRange().getMinX() );
            addVerticalTick( rangeLabelsNumberFormat, chart.getRange().getMaxX() );
        }
    }

    private void addVerticalTick( NumberFormat numberFormat, double gridLineX ) {
        Chart chart = getChart();
        int x = chart.transformX( gridLineX );
        int y = getHorizontalTickY();
        Line2D.Double line = new Line2D.Double( x, y - tickHeight / 2, x, y + tickHeight / 2);
        PhetShapeGraphic lineGraphic = new PhetShapeGraphic( chart.getComponent(), line, getStroke(), getColor() );
        tickGraphics.addGraphic( lineGraphic );

        if( labelTable == null ) {
            String string = numberFormat.format( gridLineX );
            PhetTextGraphic labelGraphic = new PhetTextGraphic( chart.getComponent(), font, string, getColor() );
            labelGraphic.setLocation( x - labelGraphic.getWidth() / 2, y + tickHeight / 2 + 4 );
            labelGraphic.setVisible( showLabels );
            labelGraphics.addGraphic( labelGraphic );
        }
    }

    public PhetGraphic getVerticalLabelFromTable( double gridLineY ) {
        int x = getVerticalTickX();
        int y = getChart().transformY( gridLineY );
        PhetGraphic labelGraphic = labelTable.get( gridLineY );
        labelGraphic.setLocation( x - tickHeight / 2 - labelGraphic.getWidth(), y - labelGraphic.getHeight() / 2 );
        return labelGraphic;
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
        this.numberFormat = formatter;
        update();
    }

    public void setTickHeight( int tickHeight ) {
        this.tickHeight = tickHeight;
        update();
    }

    public void setNumberFormat( NumberFormat numberFormat ) {
        this.numberFormat = numberFormat;
        update();
    }

    public void setLabelsVisible( boolean visible ) {
        showLabels = visible;
        update();
    }

    public abstract int getVerticalTickX();

    public abstract int getHorizontalTickY();

    public void setLabels( LabelTable labelTable ) {
        this.labelTable = labelTable;
        update();
    }

    public void setRangeLabelsVisible( boolean rangeLabelsVisible ) {
        this.rangeLabelsVisible = rangeLabelsVisible;
        update();
    }

    public void setRangeLabelsNumberFormat( NumberFormat numberFormat ) {
        rangeLabelsNumberFormat = numberFormat;
        update();
    }

}
