/**
 * Class: ParticleGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.Point2D;

public class ParticleGraphic implements Graphic, SimpleObserver {
    private NuclearParticle particle;
    private Point2D.Double position = new Point2D.Double();
    private Color color;

    protected ParticleGraphic( Color color ) {
        this.color = color;
    }

    protected ParticleGraphic( NuclearParticle particle, Color color ) {
        this.particle = particle;
        particle.addObserver( this );
        this.color = color;
        this.radius = particle.getRadius();
    }

    public void setParticle( NuclearParticle particle ) {
        if( this.particle != null ) {
            this.particle.removeObserver( this );
        }
        particle.addObserver( this );
        this.particle = particle;
        this.radius = particle.getRadius();
    }

    public void paint( Graphics2D g ) {
        paint( g, position.getX(), position.getY() );
    }

    public void paint( Graphics2D g, double x, double y ) {
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( color );
        g.fillArc( (int)x,
                   (int)y,
                   (int)( NuclearParticle.RADIUS * 2 ), (int)( NuclearParticle.RADIUS * 2 ), 0, 360 );
        g.setColor( Color.black );
        g.setStroke( outlineStroke );
        g.drawArc( (int)x,
                   (int)y,
                   (int)( NuclearParticle.RADIUS * 2 ), (int)( NuclearParticle.RADIUS * 2 ), 0, 360 );
    }

    protected void setColor( Color color ) {
        this.color = color;
    }

    public void update() {
        this.position.setLocation( particle.getLocation().getX(), particle.getLocation().getY() );
    }

    //
    // Statics
    //
    private static double radius = 20;
    private static Stroke outlineStroke = new BasicStroke( 0.5f );

}
