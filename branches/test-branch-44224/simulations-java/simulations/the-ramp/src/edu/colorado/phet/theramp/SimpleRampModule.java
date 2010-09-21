/*  */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 */

public class SimpleRampModule extends RampModule {
    public SimpleRampModule( PhetFrame phetFrame, IClock clock ) {
        super( TheRampStrings.getString( "module.introduction" ), phetFrame, clock );
    }

    protected RampControlPanel createRampControlPanel() {
        return new SimpleRampControlPanel( this );
    }

    protected RampPanel createRampPanel() {
        return new SimpleRampPanel( this );
    }

}
