package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.components.CCKStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.clock.StopwatchPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 12:04:56 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */

public class StopwatchDecorator extends VerticalLayoutPanel {
    public StopwatchDecorator( IClock clock, double timeScale, String timeUnits ) {
        setBorder( new LineBorder( Color.black, 2, true ) );
        JLabel label = new JLabel( CCKStrings.getString( "stopwatch" ) );
        add( label );
        StopwatchPanel stopwatchPanel = new StopwatchPanel( clock );
        stopwatchPanel.setScaleFactor( timeScale );
        stopwatchPanel.setTimeUnits( timeUnits );
        add( stopwatchPanel );
        label.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        label.setBackground( stopwatchPanel.getBackground() );
        label.setOpaque( true );
    }
}
