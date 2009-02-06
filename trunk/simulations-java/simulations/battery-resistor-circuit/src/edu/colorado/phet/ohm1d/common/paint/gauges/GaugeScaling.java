package edu.colorado.phet.ohm1d.common.paint.gauges;

import java.util.Vector;

import javax.swing.*;

public class GaugeScaling extends JPanel {
    ButtonGroup bg;
    Vector buttons;

    public GaugeScaling() {
        bg = new ButtonGroup();
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        buttons = new Vector();
    }

    public void add( Scaling s, boolean sel ) {
        JRadioButton jrb = new JRadioButton( s.getName() );
        bg.add( jrb );
        jrb.addActionListener( s );
        add( jrb );
        buttons.add( jrb );
        jrb.setSelected( sel );
    }
}
