/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:44 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public abstract class AbstractGrid extends CompositePhetGraphic {
    private Orientation orientation;

    private Chart chart;
    private Stroke stroke;
    private Color color;

    private GridStrategy gridStrategy;

    protected AbstractGrid( Chart chart, Orientation orientation, Stroke stroke, Color color, double spacing, double crossesOtherAxisAt ) {
        this.chart = chart;
        this.orientation = orientation;
        this.stroke = stroke;
        this.color = color;
        this.gridStrategy = new GridStrategy.Relative( orientation, spacing, crossesOtherAxisAt );
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
        update();
    }

    public void setColor( Color color ) {
        this.color = color;
        update();
    }

    protected abstract void update();

    public GridStrategy getGridStrategy() {
        return gridStrategy;
    }

    public void setGridStrategy( GridStrategy gridStrategy ) {
        this.gridStrategy = gridStrategy;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public Color getColor() {
        return color;
    }

    public Chart getChart() {
        return chart;
    }

    public void setGridlines( double[] lines ) {
        setGridStrategy( new GridStrategy.Absolute( getOrientation(), lines ) );
        update();
    }

    protected double[] getVisibleGridlines() {
        double[] g = gridStrategy.getVisibleGridLines( chart );
//        System.out.println( "num gridlines=" + g.length );
        if( g.length > 1000 ) {
            double[] again = gridStrategy.getVisibleGridLines( chart );
        }
        return g;
    }

    public void setSpacing( double spacing ) {
        if( gridStrategy instanceof GridStrategy.Relative ) {
            ( (GridStrategy.Relative)gridStrategy ).setSpacing( spacing );
        }
        update();
    }

}
