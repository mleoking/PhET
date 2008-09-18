/*
  cd E:\java\projects\phets\data
  C:\j2sdk1.4.0_01\bin\java phet.edu.colorado.phet.signal.SignalApplet
  java phet.edu.colorado.phet.signal.SignalApplet
*/

package edu.colorado.phet.signalcircuit;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.signalcircuit.phys2d.laws.Validate;


public class SignalCircuitSimulationPanel extends JPanel {

    public SignalCircuitSimulationPanel() {
        Signal s = new Signal( 600, 300, false );
        s.getSystem().addLaw( new Validate( this ) );

        setLayout( new BorderLayout() );
        add( s.getPanel(), BorderLayout.CENTER );

        add( s.getControlPanel(), BorderLayout.SOUTH );
        validate();
    }

}
