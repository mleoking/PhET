// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.addRemove;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.efield.EFieldResources;
import edu.colorado.phet.efield.core.ParticleContainer;
import edu.colorado.phet.efield.core.ParticleFactory;
import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.gui.media.EFieldResettable;
import edu.colorado.phet.efield.phys2d_efield.Particle;

public class AddRemove implements EFieldResettable {
    public class Remover
            implements ActionListener {

        public void actionPerformed( ActionEvent actionevent ) {
            remove();
        }

        public Remover() {
        }
    }

    public class Adder
            implements ActionListener {

        public void actionPerformed( ActionEvent actionevent ) {
            addElectron();
        }

        public Adder() {
        }
    }


    public AddRemove( Vector vector, ParticleFactory particlefactory, Component component, ParticlePainter particlepainter ) {
        paintMe = component;
        electrons = vector;
        pf = particlefactory;
        containers = new Vector();
        painter = particlepainter;
    }

    public void add( ParticleContainer particlecontainer ) {
        containers.add( particlecontainer );
    }

    public void fireResetAction( ParticlePanel particlepanel ) {
        while ( electrons.size() > 0 ) {
            remove();
        }
    }

    public JPanel getJPanel() {
        JButton jbutton = new JButton( EFieldResources.getString( "AddRemove.AddButton" ) );
        jbutton.addActionListener( new Adder() );
        JButton jbutton1 = new JButton( EFieldResources.getString( "AddRemove.RemoveButton" ) );
        jbutton1.addActionListener( new Remover() );
        JPanel jpanel = new JPanel();
        jpanel.setLayout( new BoxLayout( jpanel, 1 ) );
        jpanel.add( jbutton );
        jpanel.add( jbutton1 );
        return jpanel;
    }

    public ParticleContainer containerAt( int i ) {
        return (ParticleContainer) containers.get( i );
    }

    public void addElectron() {
        Particle particle = pf.newParticle();
        for ( int i = 0; i < containers.size(); i++ ) {
            ParticleContainer particlecontainer = containerAt( i );
            particlecontainer.add( particle );
        }

        electrons.add( particle );
        paintMe.repaint();
    }

    public void remove() {
        if ( electrons.size() > 0 ) {
            Particle particle = (Particle) electrons.lastElement();
            remove( particle );
        }
    }

    public void remove( Particle particle ) {
        for ( int i = 0; i < containers.size(); i++ ) {
            containerAt( i ).remove( particle );
        }

        electrons.remove( particle );
        paintMe.repaint();
    }

    Vector containers;
    Vector electrons;
    ParticleFactory pf;
    Component paintMe;
    ParticlePainter painter;
}
