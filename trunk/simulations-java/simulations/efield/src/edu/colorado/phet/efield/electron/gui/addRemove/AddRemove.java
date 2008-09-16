// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui.addRemove;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.efield.electron.core.ParticleContainer;
import edu.colorado.phet.efield.electron.core.ParticleFactory;
import edu.colorado.phet.efield.electron.gui.ParticlePainter;
import edu.colorado.phet.efield.electron.gui.ParticlePanel;
import edu.colorado.phet.efield.electron.gui.media.EFieldResettable;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;
import edu.colorado.phet.efield.electron.phys2d_efield.System2D;
import edu.colorado.phet.efield.electron.utils.Debug;

// Referenced classes of package edu.colorado.phet.efield.electron.gui.addRemove:
//            SystemAdapter, PanelAdapter

public class AddRemove
        implements EFieldResettable {
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

    public void fireResetAction( System2D system2d, ParticlePanel particlepanel ) {
        containers = new Vector();
        add( new SystemAdapter( system2d ) );
        add( new PanelAdapter( particlepanel, painter ) );
        for ( int i = 0; i < system2d.numLaws(); i++ ) {
            if ( system2d.lawAt( i ) instanceof ParticleContainer ) {
                add( (ParticleContainer) system2d.lawAt( i ) );
            }
        }

    }

    public JPanel getJPanel() {
        JButton jbutton = new JButton( SimStrings.get( "AddRemove.AddButton" ) );
        jbutton.addActionListener( new Adder() );
        JButton jbutton1 = new JButton( SimStrings.get( "AddRemove.RemoveButton" ) );
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
            Debug.traceln( "Adding to: " + particlecontainer );
            particlecontainer.add( particle );
        }

        electrons.add( particle );
        paintMe.repaint();
    }

    public void remove() {
        if ( electrons.size() == 0 ) {
            return;
        }
        else {
            Particle particle = (Particle) electrons.lastElement();
            remove( particle );
            return;
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
