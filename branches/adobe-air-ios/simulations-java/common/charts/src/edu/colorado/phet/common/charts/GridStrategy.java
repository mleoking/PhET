// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.charts;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 20, 2005
 * Time: 2:15:01 PM
 */
public interface GridStrategy {
    double[] getVisibleGridLines( Chart chart );//todo should this determine what's in and out of bounds?

    public static class Relative implements GridStrategy {
        private Orientation orientation;
        private double spacing;
        private double crossesOtherAxisAt;

        public Relative( Orientation orientation, double spacing, double crossesOtherAxisAt ) {
            this.orientation = orientation;
            this.spacing = spacing;
            this.crossesOtherAxisAt = crossesOtherAxisAt;
        }

        public Orientation getOrientation() {
            return orientation;
        }

        public double getSpacing() {
            return spacing;
        }

        public void setSpacing( double spacing ) {
            this.spacing = spacing;
        }

        public double[] getVisibleGridLines( Chart chart ) {
            if ( orientation.isVertical() ) {
                return ( getGridLinesSlowButCorrectVersion( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), spacing ) );
            }
            else if ( orientation.isHorizontal() ) {
                return ( getGridLinesSlowButCorrectVersion( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), spacing ) );
            }
            else {
                throw new RuntimeException( "Illegal Orientation" );
            }
        }

        double getCrossesOtherAxisAt() {
            return crossesOtherAxisAt;
        }

        private static double[] getGridLinesSlowButCorrectVersion( double origin, double min, double max, double spacing ) {
            int n = (int) Math.ceil( ( max - min ) / spacing );
            if ( n > 100 ) {
                System.out.println( "WARNING: GridStrategy.Relative.getGridLinesSlowButCorrectVersion - large number of gridlines: " + n );
//                new RuntimeException( "Lots of gridlines coming up, n=" + n ).printStackTrace();
            }
            ArrayList results = new ArrayList();
            for ( double currentPoint = origin; currentPoint <= max; currentPoint += spacing ) {
                if ( currentPoint >= min && currentPoint <= max ) {
                    results.add( new Double( currentPoint ) );
                }
            }
            for ( double currentPoint = origin - spacing; currentPoint >= min; currentPoint -= spacing ) {
                if ( currentPoint >= min && currentPoint <= max ) {
                    results.add( new Double( currentPoint ) );
                }
            }
//        Collections.sort( results );
            double[] output = new double[results.size()];
            for ( int i = 0; i < output.length; i++ ) {
                output[i] = ( (Double) results.get( i ) ).doubleValue();
            }
            return output;
        }
    }
}