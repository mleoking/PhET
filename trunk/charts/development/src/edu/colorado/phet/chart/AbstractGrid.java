/** Sam Reid*/
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:44 AM
 * Copyright (c) Sep 21, 2004 by Sam Reid
 */
public abstract class AbstractGrid extends CompositePhetGraphic {
    public final static int HORIZONTAL = 1;
    public final static int VERTICAL = 2;

    private Chart chart;
    private int orientation;
    private Stroke stroke;
    private Color color;
    private double spacing;
    private double crossesOtherAxisAt;
    private double[] lines;

    protected AbstractGrid( Chart chart, int orientation, Stroke stroke, Color color, double spacing, double crossesOtherAxisAt ) {
        this.chart = chart;
        this.orientation = orientation;
        this.stroke = stroke;
        this.color = color;
        this.spacing = spacing;
        this.crossesOtherAxisAt = crossesOtherAxisAt;
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

    public void setSpacing( double spacing ) {
//        System.out.println( "spacing = " + spacing );
        this.spacing = spacing;
//        lines = null;
        update();
    }

    public void setCrossesOtherAxisAt( double crossesOtherAxisAt ) {
        this.crossesOtherAxisAt = crossesOtherAxisAt;
        update();
    }

    public Stroke getStroke() {
        return stroke;
    }

    public int getOrientation() {
        return orientation;
    }

    public Color getColor() {
        return color;
    }

    public double getCrossesOtherAxisAt() {
        return crossesOtherAxisAt;
    }

    public Chart getChart() {
        return chart;
    }

    public double getSpacing() {
        return spacing;
    }

//    private static double[] getGridLinesSlowButCorrectVersion( double origin, double min, double max, double spacing ) {
//        ArrayList results = new ArrayList();
//        for( double currentPoint = origin; currentPoint <= max; currentPoint += spacing ) {
//            if( currentPoint >= min && currentPoint <= max ) {
//                results.add( new Double( currentPoint ) );
//            }
//        }
//        for( double currentPoint = origin - spacing; currentPoint >= min; currentPoint -= spacing ) {
//            if( currentPoint >= min && currentPoint <= max ) {
//                results.add( new Double( currentPoint ) );
//            }
//        }
//        Collections.sort( results );
//        double[] output = new double[results.size()];
//        for( int i = 0; i < output.length; i++ ) {
//            output[i] = ( (Double)results.get( i ) ).doubleValue();
//        }
//        return output;
//    }
//
//    private static double[] getGridLinesv3( double origin, double min, double max, double spacing ) {
//        int n = (int)( ( ( min - origin ) / spacing ) + 1 );
//        int numGridLines = (int)( ( max - min ) / spacing ) + 1;
//        double[] output = new double[numGridLines];
//        for( int i = 0; i < output.length; i++ ) {
//            output[i] = origin + spacing * ( i + n );
//        }
//        return output;
//    }

    private static double[] getGridLines( double origin, double min, double max, double spacing ) {
//        return new double[]{0,10};
//        System.out.println( "getting gridlines: spacing = " + spacing );
        int n = (int)Math.ceil( ( min - origin ) / spacing );
        if( n > 100 ) {
            new RuntimeException( "Lots of gridlines coming up, n=" + n ).printStackTrace();
        }
        ArrayList results = new ArrayList();
        for( double currentPoint = origin + n * spacing; currentPoint <= max; currentPoint += spacing ) {
            results.add( new Double( currentPoint ) );
//            System.out.println( "results.size() = " + results.size() );
        }
        double[] output = new double[results.size()];
        for( int i = 0; i < output.length; i++ ) {
            output[i] = ( (Double)results.get( i ) ).doubleValue();
        }

//        System.out.println( "output.length = " + output.length );

        return output;
    }

    public void setGridlines( double[] lines ) {
        if( lines.length > 100 ) {
            System.out.println( "AbstractGrid.lines.length = " + lines.length );
            new RuntimeException( "Lots of lines: lines.length=" + lines.length ).printStackTrace();
        }
        this.lines = lines;
        update();
    }

    public double[] getGridlines() {
        if( lines == null ) {
            setGridlines( createDefaultLines() );
        }
        return lines;
    }

    private double[] createDefaultLines() {
        if( getOrientation() == HORIZONTAL ) {
            return ( getGridLines( getCrossesOtherAxisAt(), chart.getRange().getMinX(), chart.getRange().getMaxX(), getSpacing() ) );
        }
        else {
            return ( getGridLines( getCrossesOtherAxisAt(), chart.getRange().getMinY(), chart.getRange().getMaxY(), getSpacing() ) );
        }

    }

}
