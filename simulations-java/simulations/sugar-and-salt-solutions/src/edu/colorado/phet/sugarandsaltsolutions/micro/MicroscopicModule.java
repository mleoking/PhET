// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;

/**
 * Module for "microscopic" tab of sugar and salt solutions module
 *
 * @author Sam Reid
 */
public class MicroscopicModule extends SugarAndSaltSolutionsModule {
    public MicroscopicModule() {
        super( "Microscopic" );
        setSimulationPanel( new PhetPCanvas() );
    }
}
