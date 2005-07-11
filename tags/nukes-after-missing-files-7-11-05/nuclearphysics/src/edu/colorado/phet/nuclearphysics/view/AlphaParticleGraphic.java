/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * AlphaParticleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
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
        JPanel dummyPanel = new JPanel();
        ProtonGraphic pg = new ProtonGraphic( dummyPanel );
        NeutronGraphic ng = new NeutronGraphic( dummyPanel );
        pg.paint( ga, w * 0, h * 0 );
        ng.paint( ga, w * 0, h * 0.3 );
        pg.paint( ga, w * 0.3, h * 0.3 );
        ng.paint( ga, w * 0.3, h * 0. );
        ImageIcon alphaParticleImg = new ImageIcon( alphaBi );
        return alphaParticleImg;
    }

    public AlphaParticleGraphic( Component component, Nucleus nucleus ) {
        super( component, nucleus );
    }
}
