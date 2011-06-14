// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.model.GasMolecule;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 *
 */
public class LightSpeciesGraphic extends GasMoleculeGraphic {

    static String s_imageName = IdealGasConfig.RED_PARTICLE_IMAGE_FILE;
    static BufferedImage s_particleImage;
    static BufferedImage myImage;
    static final Color COLOR_B = new Color( 252, 65, 40 );


    static {
        s_particleImage = IdealGasResources.getImage( s_imageName );
        AffineTransform atx = AffineTransform.getScaleInstance( 0.7, 0.7 );
        BufferedImageOp op = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        myImage = op.filter( s_particleImage, null );
    }

    /**
     * Sets the color of the graphic to a duotone based on a specified color. Scale is 1
     *
     * @param color
     */
    public static void setColor( Color color ) {
        GasMoleculeGraphic.setColor( color );
        s_particleImage = IdealGasResources.getImage( s_imageName );
        MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( color.getRed(), color.getGreen(), color.getBlue() ) );
        op.filter( s_particleImage, s_particleImage );
        myImage = s_particleImage;
    }


    public LightSpeciesGraphic( ApparatusPanel apparatusPanel, GasMolecule molecule ) {
        super( apparatusPanel, myImage, molecule );
    }
}
