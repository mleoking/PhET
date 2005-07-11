/**
 * Class: ParticleGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


public class ParticleGraphic extends PhetShapeGraphic implements SimpleObserver {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static Stroke outlineStroke = new BasicStroke( 0.5f );


    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private NuclearParticle particle;
    private Point2D.Double position = new Point2D.Double();
    private Color color;
    private double radius = 20;
    private AffineTransform atx = new AffineTransform();
    private Ellipse2D.Double shape = new Ellipse2D.Double( 0, 0, NuclearParticle.RADIUS * 2, NuclearParticle.RADIUS * 2 );

    protected ParticleGraphic( Component component, Color color ) {
        super( component );

        setColor( color );
        setShape( shape );
    }

    protected ParticleGraphic( Component component, NuclearParticle particle, Color color ) {
        super( component );
        this.particle = particle;
        particle.addObserver( this );
        this.color = color;
        this.radius = particle.getRadius();

        setColor( color );
        setShape( shape );
    }

    public void setStransform( AffineTransform atx ) {
        this.atx = atx;

        setTransform( atx );
    }

    public void setParticle( NuclearParticle particle ) {
        if( this.particle != null ) {
            this.particle.removeObserver( this );
        }
        particle.addObserver( this );
        this.particle = particle;
        this.radius = particle.getRadius();
    }

//    public void paint( Graphics2D g ) {
//        GraphicsState gs = new GraphicsState( g );
//        g.transform( atx );
//        paint( g, position.getX(), position.getY() );
//        gs.restoreGraphics();
//    }

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

    public void setColor( Color color ) {
        this.color = color;

        super.setColor( color );
    }

    public void update() {
        this.position.setLocation( particle.getPosition().getX(), particle.getPosition().getY() );

        super.setLocation( (int)position.getX(), (int)position.getY() );
    }
}
