package edu.colorado.phet.forces1d.force1d_tag_chart;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2004
 * Time: 6:25:44 AM
 */
public abstract class AbstractGrid {
    private double[] lines;
    public final static int HORIZONTAL = 1;
    public final static int VERTICAL = 2;

    private boolean visible = true;
    private Chart chart;
    private int orientation;
    private Stroke stroke;
    private Color color;
    private double spacing;
    private double crossesOtherAxisAt;

    protected AbstractGrid( Chart chart, int orientation, Stroke stroke, Color color, double tickSpacing, double crossesOtherAxisAt ) {
        this.chart = chart;
        this.orientation = orientation;
        this.stroke = stroke;
        this.color = color;
        this.spacing = tickSpacing;
        this.crossesOtherAxisAt = crossesOtherAxisAt;
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    public void setSpacing( double spacing ) {
        this.spacing = spacing;
        lines = null;
    }

    public void setCrossesOtherAxisAt( double crossesOtherAxisAt ) {
        this.crossesOtherAxisAt = crossesOtherAxisAt;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
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

    public double[] getGridLines( double origin, double min, double max, double spacing ) {
        if ( lines != null ) {
            return lines;
        }
        int n = (int) Math.ceil( ( min - origin ) / spacing );
        ArrayList results = new ArrayList();
        for ( double currentPoint = origin + n * spacing; currentPoint <= max; currentPoint += spacing ) {
            results.add( new Double( currentPoint ) );
        }
        double[] output = new double[results.size()];
        for ( int i = 0; i < output.length; i++ ) {
            output[i] = ( (Double) results.get( i ) ).doubleValue();
        }
        return output;
    }

    public void setGridlines( double[] lines ) {
        this.lines = lines;
    }
}
