/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests.phetjcomponents;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.util.QuickProfiler;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: May 18, 2005
 * Time: 1:29:28 PM
 * Copyright (c) May 18, 2005 by Sam Reid
 */

public class TestManyPhetJComponents extends PhetGraphicsModule {
    private static final int numComponents = 500;
    private static Random random = new Random();

    /**
     * @param name
     * @param clock
     */
    public TestManyPhetJComponents( String name, IClock clock ) {
        super( name, clock );
        setApparatusPanel( new ApparatusPanel2( clock ) );
        setModel( new BaseModel() );
        for( int i = 0; i < numComponents; i++ ) {
            JButton but = new JButton( "button_" + i );
            PhetGraphic pjc = PhetJComponent.newInstance( getApparatusPanel(), but );
            pjc.setLocation( random.nextInt( 400 ), random.nextInt( 400 ) );
            getApparatusPanel().addGraphic( pjc );
        }
    }

    public static void main( String[] args ) {
        QuickProfiler main = new QuickProfiler( "main" );
        SwingClock clock = new SwingClock( 30, 1.0 );
        PhetApplication phetApplication = new PhetApplication( args, "title", "desc", "version", new FrameSetup.CenteredWithSize( 600, 600 ) );
        TestManyPhetJComponents module = new TestManyPhetJComponents( "name", clock );
        phetApplication.setModules( new PhetGraphicsModule[]{module} );
        phetApplication.startApplication();
        System.out.println( "time to make & display " + numComponents + ", phetJComponents= " + main + " ms" );

        QuickProfiler swing = new QuickProfiler( "frame" );
        JFrame f = new JFrame();
        JPanel conten = new JPanel();
        conten.setLayout( null );
        for( int i = 0; i < numComponents; i++ ) {
            JButton but = new JButton( "button_" + i );

            but.setLocation( random.nextInt( 400 ), random.nextInt( 400 ) );
            but.reshape( but.getX(), but.getY(), but.getPreferredSize().width, but.getPreferredSize().height );
            conten.add( but );
        }
        f.setContentPane( conten );
        f.setSize( 600, 600 );
        f.setVisible( true );
        System.out.println( "time to make & display " + numComponents + ", normal JButtons= " + swing + " ms" );
    }
}