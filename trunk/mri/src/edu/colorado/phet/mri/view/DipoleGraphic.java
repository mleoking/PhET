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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.SampleMaterial;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * DipoleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleGraphic extends RegisterablePNode implements SimpleObserver {

    public static BufferedImage getDipoleImage( SampleMaterial sampleMaterial ) {
        BufferedImage bImg = null;
        String imageName = null;
        if( sampleMaterial == SampleMaterial.HYDROGEN ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-hydrogen.gif";
        }
        if( sampleMaterial == SampleMaterial.NITROGEN ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-nitrogen.gif";
        }
        if( sampleMaterial == SampleMaterial.SODIUM ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-sodium.gif";
        }
        if( sampleMaterial == SampleMaterial.CARBON_13 ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-carbon.gif";
        }
        if( sampleMaterial == SampleMaterial.OXYGEN ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-oxygen.gif";
        }
        if( sampleMaterial == SampleMaterial.SULFUR ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-sulfur.gif";
        }
        if( sampleMaterial == SampleMaterial.UNKNOWN ) {
            imageName = MriConfig.IMAGE_PATH + "dipole-5-unknown.gif";
        }
        try {
            bImg = ImageLoader.loadBufferedImage( imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return bImg;
    }


    private PImage baseDipoleGraphic;
    private Dipole dipole;
    private BufferedImage baseImage;

    /**
     * Constructor
     */
    public DipoleGraphic( Dipole dipole, SampleMaterial sampleMaterial ) {
        this.dipole = dipole;
        dipole.addObserver( this );
        baseImage = getDipoleImage( sampleMaterial );
        double scale = 1;
        AffineTransform scaleTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp atxOp = new AffineTransformOp( scaleTx, new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION,
                                                                                      RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY ) );
        baseImage = atxOp.filter( baseImage, null );
        baseDipoleGraphic = new PImage( baseImage );
        addChild( baseDipoleGraphic );
        update();
    }

    public void update() {
        BufferedImage newImage = BufferedImageUtils.getRotatedImage( baseImage, dipole.getOrientation() );
        baseDipoleGraphic.setImage( newImage );
        setRegistrationPoint( newImage.getWidth() / 2, newImage.getHeight() / 2 );
        setOffset( dipole.getPosition() );
    }
}
