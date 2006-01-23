/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.mandel.MandelModule;
import edu.colorado.phet.qm.modules.single.SingleParticleModule;
//import edu.colorado.phet.qm.tests.TestGlassPane;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApplication extends PhetApplication {
    public static String TITLE = "Quantum Wave Interference";
    public static String DESCRIPTION = "Quantum Wave Interference";
    public static String VERSION = "0.34";

    static {
        PhetLookAndFeel.setLookAndFeel();
    }

    public SchrodingerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createFrameSetup() );

        addModule( new IntensityModule( this, createClock() ) );
        addModule( new SingleParticleModule( this, createClock() ) );
        addModule( new MandelModule( this, createClock() ) );
    }

    private static IClock createClock() {
        return new SwingClock( 30, 1 );
    }

    private static FrameSetup createFrameSetup() {
        return new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
    }

    public static void main( String[] args ) {
//        new PhetLookAndFeel().apply();
        SchrodingerApplication schrodingerApplication = new SchrodingerApplication( args );
        schrodingerApplication.startApplication();
        System.out.println( "SchrodingerApplication.main" );

//        testGlassPane( schrodingerApplication );
    }

//    private static void testGlassPane( SchrodingerApplication schrodingerApplication ) {
//        TestGlassPane testGlassPane = new TestGlassPane( schrodingerApplication.getPhetFrame() );
//        schrodingerApplication.getPhetFrame().setGlassPane( testGlassPane );
//        testGlassPane.setVisible( true );
//
////        JFrame frame2 = new JFrame();
////        PSwingCanvas pSwingCanvas = new PSwingCanvas();
////        pSwingCanvas.setSize( 600, 600 );
////        pSwingCanvas.setPreferredSize( new Dimension( 600, 600 ) );
////        pSwingCanvas.getLayer().addChild( new PSwing( pSwingCanvas, (JComponent)schrodingerApplication.getPhetFrame().getContentPane() ) );
////        frame2.setContentPane( pSwingCanvas );
////        frame2.setVisible( true );
////        pSwingCanvas.setPanEventHandler( null);
//
//    }

}
