/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.workenergy.controlpanel;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Control panel template.
 */
public class WorkEnergyControlPanel extends VerticalLayoutPanel {
    public static Color BACKGROUND = new Color( 232, 242, 152 );
    public static Color FOREGROUND = Color.black;
    public static final Font CONTROL_FONT = new PhetFont( 18, true );

    public WorkEnergyControlPanel() {
        super();

        final LogoPanel panel = new LogoPanel();
        panel.setBackground( BACKGROUND );
        addControl( panel );
        addControlFullWidth( new JCheckBox( "Energy Pie Chart" ) {{
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            setFont( CONTROL_FONT );
        }} );
        addControlFullWidth( new JCheckBox( "Energy Bar Chart" ) {{
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            setFont( CONTROL_FONT );
        }} );
        addControlFullWidth( new JCheckBox( "Ruler" ) {{
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            setFont( CONTROL_FONT );
        }} );
        setBackground( BACKGROUND );
//        setBackground( new Color( 0, 0, 0, 0 ) );
//        setOpaque( false );
    }

    private void addControlFullWidth( JComponent component ) {
        add( component );
    }

    private void addControl( JComponent panel ) {
        add( panel );
    }

}
