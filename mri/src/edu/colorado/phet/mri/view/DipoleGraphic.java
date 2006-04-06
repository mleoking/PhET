/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.BufferedImageUtils;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * DipoleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleGraphic extends RegisterablePNode implements SimpleObserver {

    private PImage baseDipoleGraphic;
    private Dipole dipole;
    private BufferedImage baseImage;

    /**
     * Constructor
     */
    public DipoleGraphic( Dipole dipole ) {
        this.dipole = dipole;
        dipole.addObserver( this );
        try {
            baseImage = ImageLoader.loadBufferedImage( MriConfig.DIPOLE_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        baseDipoleGraphic = new PImage( baseImage );
        addChild( baseDipoleGraphic );
        update();
    }


    public void update() {
        setRegistrationPoint( getBounds().getX() + getBounds().getWidth() / 2,
                              getBounds().getY() + getBounds().getHeight() / 2);
        setOffset( dipole.getPosition());
//        AffineTransform rTx = AffineTransform.getRotateInstance( dipole.getBaseOrientation() );
//        AffineTransformOp txOp = new AffineTransformOp( rTx, AffineTransformOp.TYPE_BILINEAR );

        BufferedImage bi = BufferedImageUtils.getRotatedImage( baseImage, dipole.getOrientation());
        baseDipoleGraphic.setImage( bi );
//        baseDipoleGraphic.setImage( txOp.filter( baseImage, null ));
    }
}
