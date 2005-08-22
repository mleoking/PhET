package edu.colorado.phet.chart;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 20, 2005
 * Time: 2:15:01 PM
 * Copyright (c) May 20, 2005 by Sam Reid
 */
public interface GridStrategy {
    double[] getVisibleGridLines( Chart chart );//todo should this determine what's in and out of bounds?

    public static class Absolute implements GridStrategy {
        double[] gridLines;
        private Orientation orientation;

        public Absolute( Orientation orientation, double[] gridLines ) {
            this.orientation = orientation;
            setGridLines( gridLines );
        }

        public double[] getVisibleGridLines( Chart chart ) {
            return chart.getLinesInBounds( orientation, gridLines );
        }

        public void setGridLines( double[] gridLines ) {
            this.gridLines = gridLines;
            System.out.println( "gridLines.length = " + gridLines.length );
        }
    }

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
            if( orientation.isVertical() ) {
                return ( getGridLinesSlowButCorrectVersion( crossesOtherAxisAt, chart.getRange().getMinX(), chart.getRange().getMaxX(), spacing ) );
            }
            else if( orientation.isHorizontal() ) {
                return ( getGridLinesSlowButCorrectVersion( crossesOtherAxisAt, chart.getRange().getMinY(), chart.getRange().getMaxY(), spacing ) );
            }
            else {
                throw new RuntimeException( "Illegal Orientation" );
            }
        }

        double getCrossesOtherAxisAt() {
            return crossesOtherAxisAt;
        }

//        private static double[] getGridLines( double val, double min, double max, double spacing ) {
////            int n = (int)Math.ceil( ( min - val ) / spacing );
//            int n = (int)Math.ceil( ( max-min) / spacing );
//            if( n > 100 ) {
//                new RuntimeException( "Lots of gridlines coming up, n=" + n ).printStackTrace();
//            }
//            ArrayList results = new ArrayList();
//            for( double currentPoint = val + n * spacing; currentPoint <= max; currentPoint += spacing ) {
//                results.add( new Double( currentPoint ) );
//            }
//            double[] output = new double[results.size()];
//            for( int i = 0; i < output.length; i++ ) {
//                output[i] = ( (Double)results.get( i ) ).doubleValue();
//            }
//            return output;
//        }

        private static double[] getGridLinesSlowButCorrectVersion( double origin, double min, double max, double spacing ) {
            int n = (int)Math.ceil( ( max - min ) / spacing );
            if( n > 100 ) {
                System.out.println( "WARNING: GridStrategy.Relative.getGridLinesSlowButCorrectVersion - large number of gridlines: " + n );
//                new RuntimeException( "Lots of gridlines coming up, n=" + n ).printStackTrace();
            }
            ArrayList results = new ArrayList();
            for( double currentPoint = origin; currentPoint <= max; currentPoint += spacing ) {
                if( currentPoint >= min && currentPoint <= max ) {
                    results.add( new Double( currentPoint ) );
                }
            }
            for( double currentPoint = origin - spacing; currentPoint >= min; currentPoint -= spacing ) {
                if( currentPoint >= min && currentPoint <= max ) {
                    results.add( new Double( currentPoint ) );
                }
            }
//        Collections.sort( results );
            double[] output = new double[results.size()];
            for( int i = 0; i < output.length; i++ ) {
                output[i] = ( (Double)results.get( i ) ).doubleValue();
            }
            return output;
        }
    }
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

//    private static double[] getGridLines( double origin, double min, double max, double spacing ) {
//        int n = (int)Math.ceil( ( min - origin ) / spacing );
//        if( n > 100 ) {
//            new RuntimeException( "Lots of gridlines coming up, n=" + n ).printStackTrace();
//        }
//        ArrayList results = new ArrayList();
//        for( double currentPoint = origin + n * spacing; currentPoint <= max; currentPoint += spacing ) {
//            results.add( new Double( currentPoint ) );
//        }
//        double[] output = new double[results.size()];
//        for( int i = 0; i < output.length; i++ ) {
//            output[i] = ( (Double)results.get( i ) ).doubleValue();
//        }
//        return output;
//    }