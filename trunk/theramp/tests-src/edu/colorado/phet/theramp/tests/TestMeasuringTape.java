///* Copyright 2004, Sam Reid */
//package edu.colorado.phet.theramp.tests;
//
//import edu.colorado.phet.common.model.clock.IClock;
//import edu.colorado.phet.common.model.clock.ClockEvent;
//import edu.colorado.phet.common.model.clock.ClockTickListener;
//import edu.colorado.phet.common.model.clock.SwingClock;
//import edu.colorado.phet.common.view.ApparatusPanel2;
//import edu.colorado.phet.common.view.BasicGraphicsSetup;
//
//import javax.swing.*;
//
///**
// * User: Sam Reid
// * Date: May 20, 2005
// * Time: 10:55:37 PM
// * Copyright (c) May 20, 2005 by Sam Reid
// */
//
//public class TestMeasuringTape {
//    public static void main( String[] args ) {
//        IClock clock = new SwingClock( 1, 30 );
//        final ApparatusPanel2 apparatusPanel2 = new ApparatusPanel2( clock );
//        clock.addClockListener( new ClockAdapter() {
//            public void clockTicked( ClockEvent event ) {
//                apparatusPanel2.handleUserInput();
//                apparatusPanel2.paint();
//            }
//        } );
//        apparatusPanel2.addGraphicsSetup( new BasicGraphicsSetup() );
//        clock.start();
//        //todo piccolo
//        MeasuringTape measuringTape = new MeasuringTape( apparatusPanel2, new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 10, 10 ), new Rectangle( 0, 0, 600, 600 ) ), new Point2D.Double( 5, 5 ) );
////        apparatusPanel2.addGraphic( measuringTape );
////        measuringTape.setLocation( 100, 100 );
//
//        JFrame frame = new JFrame( "" );
//        frame.setContentPane( apparatusPanel2 );
//        frame.setSize( 600, 600 );
//        frame.show();
//        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
//    }
//}
