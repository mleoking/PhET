package edu.colorado.phet.batteryvoltage.common.electron.gui.mouse2;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.PropagatingParticle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;

public class ParticleGrabber implements MouseListener, MouseMotionListener {
    PropagatingParticle selected;

    ParticleSelector ps;
    Component repaint;
    Propagator carryPropagator;

    public ParticleGrabber( Component repaint, ParticleSelector ps, Propagator carryPropagator ) {
        this.carryPropagator = carryPropagator;
        this.repaint = repaint;
        selected = null;
        this.ps = ps;
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public Particle getSelected() {
        return selected;
    }

    public void mouseReleased( MouseEvent me ) {
        selected = null;
//  	if (selected!=null)
//  	    sys.addParticle(selected);
//  	selected=null;
    }

    public void mouseEntered( MouseEvent me ) {
    }

    public void mouseExited( MouseEvent me ) {
    }

    /**
     * Grab the topmost edu.colorado.phet.electron under the mouse, if any.
     */
    public void mousePressed( MouseEvent me ) {
        if ( me.isControlDown() ) {
            this.selected = (PropagatingParticle) ps.selectClosestTo( me.getPoint() );
        }
        else {
            Point pt = me.getPoint();
            PropagatingParticle p = (PropagatingParticle) ps.selectAt( pt );
            selected = p;

            //util.Debug.traceln("Grabbed: "+p);
        }
        if ( selected != null ) {
            selected.setPropagator( carryPropagator );
        }
    }

    /**
     * Motion.
     */
    public void mouseDragged( MouseEvent me ) {
        if ( selected == null ) {
            return;
        }
        selected.setPosition( new DoublePoint( me.getX(), me.getY() ) );
        repaint.repaint();
    }

    public void mouseMoved( MouseEvent me ) {
    }
}
