/**
 * Class: ParticleGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class ParticleGraphic extends PhetImageGraphic implements SimpleObserver {
    private NuclearParticle particle;
    private Point2D.Double position = new Point2D.Double();
    private Color color;
    private AffineTransform atx = new AffineTransform();

    protected ParticleGraphic( Color color ) {
        this.color = color;
    }

    protected ParticleGraphic( NuclearParticle particle, Color color ) {
        this.particle = particle;
        particle.addObserver( this );
        this.color = color;
    }

    public void setStransform( AffineTransform atx ) {
        this.atx = atx;
    }

    public void setParticle( NuclearParticle particle ) {
        if( this.particle != null ) {
            this.particle.removeObserver( this );
        }
        particle.addObserver( this );
        this.particle = particle;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.transform( atx );
        paint( g, position.getX(), position.getY() );
        gs.restoreGraphics();
    }

    public void paint( Graphics2D g, double x, double y ) {
        GraphicsState gs = new GraphicsState( g );
        g.transform( atx );
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
        gs.restoreGraphics();
    }

    protected void setColor( Color color ) {
        this.color = color;
    }

    public void update() {
        this.position.setLocation( particle.getPosition().getX(), particle.getPosition().getY() );
    }

    //
    // Statics
    //
    private static Stroke outlineStroke = new BasicStroke( 0.5f );

}
