/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.view;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.util.persistence.PersistentParticle;
import edu.colorado.phet.common.util.persistence.PersistentParticle;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * ParticleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class ParticleGraphic implements Graphic, SimpleObserver {
public class ParticleGraphic extends PhetShapeGraphic implements SimpleObserver {
    private Particle particle;
    private Color color;

    public ParticleGraphic() {
        super( null );
    }

    public ParticleGraphic( Component component, Particle particle, Color color ) {
//    public ParticleGraphic( Component component, PersistentParticle particle, Color color ) {
        super( component );
        this.particle = particle;
        this.color = color;
        particle.addObserver( this );
    }

    protected Rectangle determineBounds() {
        return new Rectangle( (int)particle.getPosition().getX(), (int)particle.getPosition().getY(), 5, 5 );
    }

    public void paint( Graphics2D g2 ) {
        g2.setColor( color );
        g2.fillArc( (int)particle.getPosition().getX(), (int)particle.getPosition().getY(), 5, 5, 0, 360 );
    }

    public void update() {
        this.setLocation( new Point( (int)particle.getPosition().getX(), (int)particle.getPosition().getY() ) );
        setBoundsDirty();
        repaint();
    }

    public Particle getParticle() {
//    public PersistentParticle getParticle() {
        return particle;
    }

    public void setParticle( Particle particle ) {
//    public void setParticle( PersistentParticle particle ) {
        this.particle = particle;
        particle.addObserver( this );
    }

    public Color getColor() {
        return color;
    }

    public void setColor( Color color ) {
        this.color = color;
    }

    //////////////////////////////////
    // For debug
    //
    public void setLocation( Point p ) {
        try {
            super.setLocation( p );
        }
        catch ( Throwable t ) {
            System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        }
    }
}
