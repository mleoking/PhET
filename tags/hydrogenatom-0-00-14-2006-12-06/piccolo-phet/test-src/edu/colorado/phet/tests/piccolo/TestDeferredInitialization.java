package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.DeferredInitializationModule;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jun 5, 2006
 * Time: 11:56:17 AM
 * Copyright (c) Jun 5, 2006 by Sam Reid
 */

public class TestDeferredInitialization extends PiccoloPhetApplication {
    public TestDeferredInitialization( String[] args ) {
        super( args, "Test Deferred Initialization", "test", "0.01" );
        DeferredInitializationModule deferredInitializationModule = new TestDeferredModuleA( "Module A" );
        DeferredInitializationModule deferredInitializationModule2 = new TestDeferredModuleA( "Module B" );
        addModule( deferredInitializationModule );
        addModule( deferredInitializationModule2 );
    }

    static class TestDeferredModuleA extends DeferredInitializationModule {
        public TestDeferredModuleA( String name ) {
            super( name, new SwingClock( 30, 1 ) );
            setSimulationPanel( new JLabel( name ) );
        }

        protected void init() {
            try {
                Thread.sleep( 5000 );
                System.out.println( "Finished TestDeferredInitialization$TestDeferredModuleA.init" );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }

        }
    }

    public static void main( String[] args ) {
        new TestDeferredInitialization( args ).startApplication();
    }
}
