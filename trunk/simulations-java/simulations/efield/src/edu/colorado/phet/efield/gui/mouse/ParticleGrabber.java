// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.gui.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.gui.media.EFieldResettable;
import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.System2D;
import edu.colorado.phet.efield.phys2d_efield.SystemRunner;

public class ParticleGrabber implements MouseListener, MouseMotionListener, EFieldResettable {

    public ParticleGrabber( ParticlePanel particlepanel, System2D system2d, SystemRunner systemrunner ) {
        systemRunner = systemrunner;
        particlePanel = particlepanel;
        selectedParticle = null;
        system2D = system2d;
        particleSelector = new ParticleSelector( particlepanel );
    }

    public void fireResetAction( ParticlePanel particlepanel ) {
        particlePanel = particlepanel;
    }

    public void mouseClicked( MouseEvent mouseevent ) {
    }

    public void mouseReleased( MouseEvent mouseevent ) {
        if ( selectedParticle != null ) {
            system2D.addParticle( selectedParticle );
        }
        selectedParticle = null;
    }

    public void mouseEntered( MouseEvent mouseevent ) {
    }

    public void mouseExited( MouseEvent mouseevent ) {
    }

    public void mousePressed( MouseEvent mouseevent ) {
        java.awt.Point point = mouseevent.getPoint();
        Particle particle = particleSelector.selectAt( point );
        selectedParticle = particle;
        if ( particle != null ) {
            system2D.remove( selectedParticle );
        }
    }

    public void mouseDragged( MouseEvent mouseevent ) {
        if ( selectedParticle == null ) {
            return;
        }
        selectedParticle.setPosition( new DoublePoint( mouseevent.getX(), mouseevent.getY() ) );
        particlePanel.repaint();
    }

    public void mouseMoved( MouseEvent mouseevent ) {
    }

    ParticlePanel particlePanel;
    Particle selectedParticle;
    System2D system2D;
    SystemRunner systemRunner;
    ParticleSelector particleSelector;
}
