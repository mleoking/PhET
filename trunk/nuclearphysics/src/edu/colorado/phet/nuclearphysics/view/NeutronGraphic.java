/**
 * Class: NeutronGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.nuclearphysics.model.Neutron;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.HashMap;

public class NeutronGraphic extends ParticleGraphic implements ImageObserver {

    //
    // Statics
    //
    private static Stroke outlineStroke = new BasicStroke( 0.5f );
//    private static
    private static Color color = Color.gray;
    private static BufferedImage neutronImage;
    private static AffineTransform atx = new AffineTransform();

    static {
        ImageLoader imgLoader = new ImageLoader();
        try {
            neutronImage = imgLoader.loadImage( "images/gray-xsml.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static HashMap graphicToModelMap = new HashMap();

    public static NeutronGraphic getGraphicForNeutron( Neutron neutron ) {
        return (NeutronGraphic)graphicToModelMap.get( neutron );
    }

    public NeutronGraphic() {
        super( color );
    }

    public NeutronGraphic( NuclearParticle particle ) {
        super( particle, NeutronGraphic.color );
        graphicToModelMap.put( particle, this );
    }

    public void paint( Graphics2D g, double x, double y ) {
        atx.setToTranslation( x, y );
        g.drawImage( neutronImage, atx, this );
        g.setColor( Color.black );
//        g.d
    }


    public void update() {
        super.update();
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}
