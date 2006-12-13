/* Copyright 2003-2004, University of Colorado */

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
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.*;

/**
 * BigNeutronGraphic
 * <p>
 * This class provides a bigger image of a neutron for use in the
 * ControlledFissionModule. It is a bit of a hack.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BigNeutronGraphic extends NeutronGraphic {
    private static BufferedImage myBufferedImage;
    static {
        AffineTransform atx = AffineTransform.getScaleInstance( 4, 4);
        AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        myBufferedImage = atxOp.filter( NeutronGraphic.getNeutronImage(), null );
    }

    protected static BufferedImage getNeutronImage() {
        return myBufferedImage;
    }

    public BigNeutronGraphic() {
        super();
    }

    public BigNeutronGraphic( NuclearParticle particle ) {
        super( particle );
    }

    public void paint( Graphics2D g, double x, double y ) {
        getAtx().setToTranslation( x, y );
        g.drawImage( getNeutronImage(), getAtx(), this );
        g.setColor( Color.black );
        g.setStroke( getOutlineStroke() );
        GraphicsUtil.setAntiAliasingOn( g );
    }
}

