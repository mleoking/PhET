package edu.colorado.phet.common.gui.grabber;

import edu.colorado.phet.common.gui.ParticlePanel;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.System2D;
import edu.colorado.phet.common.phys2d.SystemRunner;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ParticleGrabber implements MouseListener, MouseMotionListener {
    ParticlePanel pp;
    Particle selected;
    System2D sys;
    SystemRunner run;
    ParticleSelector ps;

    public ParticleGrabber( ParticlePanel pp, System2D sys, SystemRunner run ) {
        this.run = run;
        this.pp = pp;
        selected = null;
        this.sys = sys;
        this.ps = new ParticleSelector( pp );
    }

    public void fireResetAction( System2D sys, ParticlePanel pp ) {
        this.sys = sys;
        this.pp = pp;
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public void mouseReleased( MouseEvent me ) {
        if( selected != null ) {
            sys.addParticle( selected );
        }
        selected = null;
    }

    public void mouseEntered( MouseEvent me ) {
    }

    public void mouseExited( MouseEvent me ) {
    }

    /**
     * Grab the topmost edu.colorado.phet.common under the grabber, if any.
     */
    public void mousePressed( MouseEvent me ) {
        Point pt = me.getPoint();
        Particle p = ps.selectAt( pt );
        selected = p;
        if( p != null ) {
            sys.remove( selected );
        }
    }

    /**
     * Motion.
     */
    public void mouseDragged( MouseEvent me ) {
        if( selected == null ) {
            return;
        }
        selected.setPosition( new DoublePoint( me.getX(), me.getY() ) );
        if( run.isActiveAndRunning() ) {
        }
        else {
            pp.repaint();
        }
    }

    public void mouseMoved( MouseEvent me ) {
    }
}
