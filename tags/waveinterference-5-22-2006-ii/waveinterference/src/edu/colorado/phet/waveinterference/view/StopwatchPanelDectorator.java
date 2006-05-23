/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.clock.StopwatchPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 11:24:23 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class StopwatchPanelDectorator extends VerticalLayoutPanel {
    public StopwatchPanelDectorator( IClock clock, double timeScale, String timeUnits ) {
        setBorder( new LineBorder( Color.black, 2, true ) );
        JLabel label = new JLabel( "Stopwatch" );
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
