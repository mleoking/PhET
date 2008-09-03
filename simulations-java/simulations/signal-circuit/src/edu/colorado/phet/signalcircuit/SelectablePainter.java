package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.paint.particle.ParticlePainter;
import edu.colorado.phet.signalcircuit.phys2d.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectablePainter implements ParticlePainter, ActionListener {
    ParticlePainter on;
    ParticlePainter off;
    boolean isOn;
    JCheckBox source;

    public SelectablePainter( ParticlePainter on, ParticlePainter off, boolean isOn, JCheckBox source ) {
        this.off = off;
        this.on = on;
        this.isOn = isOn;
        this.source = source;
    }

    public void actionPerformed( ActionEvent ae ) {
        this.isOn = !source.isSelected();
    }

    public void paint( Particle p, Graphics2D g ) {
        if( isOn ) {
            on.paint( p, g );
        }
        else {
            off.paint( p, g );
        }
    }

}
