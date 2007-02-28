package edu.colorado.phet.graphics.gauge;

import javax.swing.*;
import java.util.Vector;

public class GaugeScaling extends JPanel {
    ButtonGroup bg;
    Vector buttons;

    public GaugeScaling() {
        bg = new ButtonGroup();
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        buttons = new Vector();
    }

    public JRadioButton buttonAt( int i ) {
        return (JRadioButton)buttons.get( i );
    }

    public void add( Scaling s ) {
        JRadioButton jrb = new JRadioButton( s.getName() );
        bg.add( jrb );
        jrb.addActionListener( s );
        add( jrb );
        buttons.add( jrb );
    }
}
