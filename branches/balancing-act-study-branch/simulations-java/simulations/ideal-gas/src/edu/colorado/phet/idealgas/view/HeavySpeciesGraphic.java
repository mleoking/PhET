// Copyright 2002-2011, University of Colorado

/*
 * Class: HeavySpeciesGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.phetcommon.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasResources;
import edu.colorado.phet.idealgas.model.GasMolecule;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 */
public class HeavySpeciesGraphic extends GasMoleculeGraphic {

    static String s_imageName = IdealGasConfig.BLUE_PARTICLE_IMAGE_FILE;
    static BufferedImage s_particleImage;

    static {
        s_particleImage = IdealGasResources.getImage( s_imageName );
        MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( 90, 90, 255 ) );
//            MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( 120, 120, 255 ));
        op.filter( s_particleImage, s_particleImage );
    }


    /**
     * Sets the color of the graphic to a duotone based on a specified color
     *
     * @param color
     */
    public static void setColor( Color color ) {
        GasMoleculeGraphic.setColor( color );
        s_particleImage = IdealGasResources.getImage( s_imageName );
        MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( color.getRed(), color.getGreen(), color.getBlue() ) );
        op.filter( s_particleImage, s_particleImage );
    }

    /**
     * Constructor
     *
     * @param apparatusPanel
     * @param molecule
     */
    public HeavySpeciesGraphic( ApparatusPanel apparatusPanel, GasMolecule molecule ) {
        super( apparatusPanel, s_particleImage, molecule );
    }
}
