/**
 * Class: MeasureControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 9, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;

public class MeasureControlPanel extends SoundControlPanel {

    public MeasureControlPanel( Module module, AbstractClock clock ) {
        super( module );
        ClockPanelLarge cpl = new ClockPanelLarge( clock );
        addPanel( cpl );
    }
}
