/**
 * Class: ParticleGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;

public class ParticleGraphic implements Graphic {
    private NuclearParticle particle;
    private Color color;

    protected ParticleGraphic( Color color ) {
        this.color = color;
    }

    protected ParticleGraphic( NuclearParticle particle, Color color ) {
        this.particle = particle;
        this.color = color;
        this.radius = particle.getRadius();
    }

    public void setParticle( NuclearParticle particle ) {
        this.particle = particle;
        this.radius = particle.getRadius();
    }

    public void paint( Graphics2D g ) {
        paint( g, particle.getPosition().getX(), particle.getPosition().getY() );
//        GraphicsUtil.setAntiAliasingOn( g );
//        g.setColor( color );
//        g.fillArc( (int)( particle.getPosition().getX() - radius ),
//                   (int)( particle.getPosition().getY() - radius ),
//                   (int)( radius * 2 ), (int)( radius * 2 ), 0, 360 );
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

    //
    // Statics
    //
    private static double radius = 20;
    private static Stroke outlineStroke = new BasicStroke( 0.5f );

}
