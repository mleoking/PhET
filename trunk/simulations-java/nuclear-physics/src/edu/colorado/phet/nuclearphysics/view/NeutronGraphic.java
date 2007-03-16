/**
 * Class: NeutronGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * Graphic for neutron used in fission panels
 */
public class NeutronGraphic extends ParticleGraphic implements ImageObserver {

    private static Stroke outlineStroke = new BasicStroke( 0.35f );
    private static Color color = Color.gray;
    private static BufferedImage neutronImage;
    private static AffineTransform atx = new AffineTransform();
//    private static Ellipse2D.Double circle;
//    private NuclearParticle myParticle;

    static {
        ImageLoader imgLoader = new ImageLoader();
        try {
            neutronImage = imgLoader.loadImage( "images/gray-xsml.gif" );
//            circle = new Ellipse2D.Double( 0,
//                                           0,
//                                           neutronImage.getWidth(),
//                                           neutronImage.getWidth() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    protected static BufferedImage getNeutronImage() {
        return neutronImage;
    }

    protected static AffineTransform getAtx() {
        return atx;
    }

    protected static Stroke getOutlineStroke() {
        return outlineStroke;
    }

    public NeutronGraphic() {
        super( color );
    }

    public NeutronGraphic( NuclearParticle particle ) {
        super( particle, NeutronGraphic.color );
//        this.myParticle = particle;
    }

    public void paint( Graphics2D g, double x, double y ) {
        atx.setToTranslation( x, y );
        g.drawImage( getNeutronImage(), atx, this );
//        g.drawImage( neutronImage, atx, this );
        g.setColor( Color.black );
        g.setStroke( outlineStroke );
//        AffineTransform orgTx = g.getTransform();
        GraphicsUtil.setAntiAliasingOn( g );
//        g.transform( atx );
//        g.draw( circle );

//        g.setTransform( orgTx );

//        g.setColor( Color.red );
//        if( myParticle != null ) {
//            g.drawLine( (int)myParticle.getPositionPrev().getX(), (int)myParticle.getPositionPrev().getY(),
//                        (int)myParticle.getPosition().getX(), (int)myParticle.getPosition().getY() );
//        }

    }

    public void update() {
        super.update();
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
