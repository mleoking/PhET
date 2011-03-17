// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsModule extends Module {
    public SugarAndSaltSolutionsModule( String name ) {
        super( name, new ConstantDtClock() );
        setModel( new BaseModel() );
        setSimulationPanel( new PhetPCanvas() );
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }
}
