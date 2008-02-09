/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.clock.StopwatchPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:24:23 PM
 */

public class StopwatchPanelDectorator extends VerticalLayoutPanel {
    private StopwatchPanel stopwatchPanel;

    public StopwatchPanelDectorator( IClock clock, double timeScale, String timeUnits ) {
        setBorder( new LineBorder( Color.black, 2, true ) );
        JLabel label = new JLabel( WIStrings.getString( "controls.stopwatch" ) );
        add( label );
        stopwatchPanel = new StopwatchPanel( clock );
        stopwatchPanel.setScaleFactor( timeScale );
        stopwatchPanel.setTimeUnits( timeUnits );
        add( stopwatchPanel );
        label.setFont( new PhetDefaultFont( Font.BOLD, 14 ) );
        label.setBackground( stopwatchPanel.getBackground() );
        label.setOpaque( true );
    }

    public void reset() {
        stopwatchPanel.reset();
    }
}
