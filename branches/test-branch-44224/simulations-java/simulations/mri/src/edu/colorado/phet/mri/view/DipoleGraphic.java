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

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.RegisterablePNode;
import edu.colorado.phet.mri.MriResources;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.SampleMaterial;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * DipoleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DipoleGraphic extends RegisterablePNode implements SimpleObserver {

    public static BufferedImage getDipoleImage( SampleMaterial sampleMaterial ) {
        String imageName = null;
        if( sampleMaterial == SampleMaterial.HYDROGEN ) {
            imageName = "dipole-5-hydrogen.gif";
        }
        else if( sampleMaterial == SampleMaterial.NITROGEN ) {
            imageName = "dipole-5-nitrogen.gif";
        }
        else if( sampleMaterial == SampleMaterial.SODIUM ) {
            imageName = "dipole-5-sodium.gif";
        }
        else if( sampleMaterial == SampleMaterial.CARBON_13 ) {
            imageName = "dipole-5-carbon.gif";
        }
        else if( sampleMaterial == SampleMaterial.OXYGEN ) {
            imageName = "dipole-5-oxygen.gif";
        }
        else if( sampleMaterial == SampleMaterial.SULFUR ) {
            imageName = "dipole-5-sulfur.gif";
        }
        else if( sampleMaterial == SampleMaterial.UNKNOWN ) {
            imageName = "dipole-5-unknown.gif";
        }
        return MriResources.getImage( imageName );
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
