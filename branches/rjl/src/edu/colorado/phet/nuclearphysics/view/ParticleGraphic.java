/**
 * Class: ParticleGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Particle;

import java.awt.*;

public class ParticleGraphic implements Graphic {
    private Particle particle;
    private Color color;
    private double radius = 20;

    protected ParticleGraphic( Particle particle, Color color ) {
        this.particle = particle;
        this.color = color;
    }

    public void paint( Graphics2D g ) {
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( color );
        g.fillArc( (int)( particle.getPosition().getX() - radius ),
                   (int)( particle.getPosition().getY() - radius ),
                   (int)( radius * 2 ), (int)( radius * 2 ), 0, 360 );
    }

    protected void setColor( Color color ) {
        this.color = color;
    }
}
