/**
 * Class: AlphaParticleGraphic
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Mar 4, 2004
 * Time: 8:57:31 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AlphaParticleGraphic extends NucleusGraphic {

    /**
     * Returns a standardized icon for an alpha particle
     *
     * @return
     */
    public static Icon getIcon() {
        double h = NuclearParticle.RADIUS * 4;
        double w = NuclearParticle.RADIUS * 4;
        BufferedImage alphaBi = new BufferedImage( (int)w, (int)h,
                                                   BufferedImage.TYPE_INT_ARGB );
        Graphics2D ga = (Graphics2D)alphaBi.getGraphics();
        ProtonGraphic pg = new ProtonGraphic();
        NeutronGraphic ng = new NeutronGraphic();
        pg.paint( ga, w * 0, h * 0 );
        ng.paint( ga, w * 0, h * 0.3 );
        pg.paint( ga, w * 0.3, h * 0.3 );
        ng.paint( ga, w * 0.3, h * 0. );
//        new AlphaParticleGraphic( new AlphaParticle( new Point2D.Double(), 0 ) ).paint( ga, NuclearParticle.RADIUS, NuclearParticle.RADIUS );
        ImageIcon alphaParticleImg = new ImageIcon( alphaBi );
        return alphaParticleImg;
    }

    public AlphaParticleGraphic( Nucleus nucleus ) {
        super( nucleus );
    }
}
