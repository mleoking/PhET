// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;

/**
 * Author: Sam Reid
 * Apr 12, 2007, 11:48:01 PM
 */
public class CircuitConstructionKitDCApplication extends CircuitConstructionKitApplication {
    public static String[] args;

    public CircuitConstructionKitDCApplication( PhetApplicationConfig config ) {
        super( config, false, false );
    }

    public static void main( String[] args ) {
        CircuitConstructionKitDCApplication.args = args;
        new PhetApplicationLauncher().launchSim( new CircuitConstructionKitApplicationConfig( args, "circuit-construction-kit", "circuit-construction-kit-dc" ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new CircuitConstructionKitDCApplication( config );
            }
        } );
    }

    public static boolean isStanfordStudy() {
        return Arrays.asList( CircuitConstructionKitDCApplication.args ).contains( "stanford-a" ) ||
               Arrays.asList( CircuitConstructionKitDCApplication.args ).contains( "stanford-b" );
    }

    public static boolean isStanfordStudyA() {
        return Arrays.asList( CircuitConstructionKitDCApplication.args ).contains( "stanford-a" );
    }
}