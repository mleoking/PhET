/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.phetjcomponents;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
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

public class TestManyPhetJComponents extends Module {
    private int numComponents = 500;
    private static Random random = new Random();

    /**
     * @param name
     * @param clock
     */
    public TestManyPhetJComponents( String name, AbstractClock clock ) {
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
        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        TestManyPhetJComponents module = new TestManyPhetJComponents( "name", clock );
        PhetApplication phetApplication = new PhetApplication( new ApplicationModel( "window title", "desc", "version", new FrameSetup.CenteredWithSize( 600, 600 ), module, clock ), args );
        phetApplication.startApplication();
    }
}
