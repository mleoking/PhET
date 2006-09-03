/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.tests;

/**
 * User: Sam Reid
 * Date: Apr 2, 2005
 * Time: 6:50:54 PM
 * Copyright (c) Apr 2, 2005 by Sam Reid
 */

public class MovingManModuleComments {

    //constructor
    //        addDirectionHandler();

//        addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                double dx=getMan().getDx();
//                if (dx!=0){
//                    getManGraphic().setVelocity( dx);
//                }
//            }
//        } );

//        addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                double dx=getManGraphic().getDx();
//                if (dx!=0){
//                    getManGraphic().setVelocity( dx );
//                }
//            }
//        } );

//        addModelElement( new ModelElement() {
//            CircularBuffer buffer = new CircularBuffer( 30 );
//
//            public void stepInTime( double dt ) {
//
//                buffer.addPoint( getMan().getDx() );
//                boolean allZero = true;
//                for( int i = 0; i < buffer.numPoints(); i++ ) {
//                    double x = buffer.pointAt( i );
//                    if( x != 0 ) {
//                        allZero = false;
//                        break;
//                    }
//                }
//                if( allZero ) {
//                    getManGraphic().setVelocity( 0.0 );
//                }
//            }
//        } );

    //    private void addDirectionHandler() {
//
//        getVelocityPlot().addListener( new MMTimePlot.TimeListener() {
//            public void nominalValueChanged( double value ) {
//                if( manGraphic.isDragging() || getPositionPlot().isDragging() || getVelocityPlot().isDragging() || stopped ) {
//                }
//                else {
//                    manGraphic.setVelocity( value );
//                }
//            }
//        } );
//        getManGraphic().addListener( new ManGraphic.TimeListener() {
//            double lastX;
//
//            public void manGraphicChanged() {
//                if( manGraphic.isDragging() || getPositionPlot().isDragging() ) {
//                    double x = getMan().getX();
//                    double dx = x - lastX;
//                    getManGraphic().setVelocity( dx );
//                    lastX = x;
//                }
//            }
//
//            public void mouseReleased() {
//                getManGraphic().setVelocity( 0 );
//                stopped = true;
//            }
//        } );
//
//        getVelocityPlot().getVerticalChartSlider().addListener( new VerticalChartSlider.TimeListener() {
//            public void valueChanged( double value ) {
//                if( getVelocityPlot().isDragging() ) {
//                    getManGraphic().setVelocity( value );
//                }
//            }
//        } );
//        getAccelerationPlot().getVerticalChartSlider().addListener( new VerticalChartSlider.TimeListener() {
//            public void valueChanged( double value ) {
//                if( getAccelerationPlot().isDragging() ) {
//                    stopped = false;
//                }
//            }
//        } );
//    }


    //    public void manCrashedPositive() {
//        if( velocityPlot.getVerticalChartSlider().getValue() > 0 ) {
//            velocityPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//        if( accelerationPlot.getVerticalChartSlider().getValue() > 0 ) {
//            accelerationPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//    }
//
//    public void manCrashedNegative() {
//        if( velocityPlot.getVerticalChartSlider().getValue() < 0 ) {
//            velocityPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//        if( accelerationPlot.getVerticalChartSlider().getValue() < 0 ) {
//            accelerationPlot.getVerticalChartSlider().setValue( 0.0 );
//        }
//    }
}
