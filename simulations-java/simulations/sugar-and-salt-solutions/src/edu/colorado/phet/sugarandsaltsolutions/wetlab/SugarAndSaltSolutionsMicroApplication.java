// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.wetlab;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;

/**
 * This sim was written to accommodate a chemistry wet lab in Fall 2011, this sim has only one kit in the Macro tab.
 * Can probably be deleted after usage.  No need to internationalize this version--it is just used for one experiment.
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsMicroApplication {

    private static final String NAME = "sugar-and-salt-solutions-micro";

    //String key used to indicate that this is the customized wet lab version for fall 2011
    public static String SINGLE_MICRO_KIT = "-singleMicroKit";

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( append( args, SINGLE_MICRO_KIT ), SugarAndSaltSolutionsResources.PROJECT_NAME, SugarAndSaltSolutionsMicroApplication.NAME ), SugarAndSaltSolutionsApplication.class );
    }

    private static String[] append( String[] args, final String s ) {
        return new ArrayList<String>( Arrays.asList( args ) ) {{
            add( s );
        }}.toArray( new String[args.length + 1] );
    }
}