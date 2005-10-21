/**
 * Class: StandingWaveModule
 * Class: edu.colorado.phet.microwave
 * User: Ron LeMaster
 * Date: Sep 19, 2003
 * Time: 2:26:42 PM
 */
package edu.colorado.phet.microwave;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.coreadditions.collision.Box2D;
import edu.colorado.phet.microwave.model.MicrowaveModel;
import edu.colorado.phet.common.view.util.SimStrings;

public class StandingWaveModule extends MicrowaveModule {

    public StandingWaveModule() {
        super( SimStrings.get( "ModuleTitle.StandingWaveModule" ) );
        MicrowaveModel model = getMicrowaveModel();
        Box2D oven = model.getOven();
        double freq = model.getFrequency();
    }

    public void activate( PhetApplication phetApplication ) {
    }

    public void deactivate( PhetApplication phetApplication ) {
    }
    //
    // Interfaces implemented
    //
    
    //
    // Static fields and methods
    //
    
    //
    // Inner classes
    //
}
