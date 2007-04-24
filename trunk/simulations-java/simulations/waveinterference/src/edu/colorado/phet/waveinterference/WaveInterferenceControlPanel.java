/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:13:41 PM
 *
 */

public class WaveInterferenceControlPanel extends ControlPanel {
    public static final int PAD_FOR_RESET_BUTTON = 3;

    public WaveInterferenceControlPanel() {
    }

    public void addVerticalSpace() {
        addControlFullWidth( new VerticalSeparator() );
    }
}
