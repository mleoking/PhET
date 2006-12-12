/*
 * Class: HeavySpeciesGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.GasMolecule;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class HeavySpeciesGraphic extends GasMoleculeGraphic {

    static String s_imageName = IdealGasConfig.BLUE_PARTICLE_IMAGE_FILE;
    static BufferedImage s_particleImage;

    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( 90, 90, 255 ));
//            MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( 120, 120, 255 ));
            op.filter( s_particleImage, s_particleImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }


    /**
     * Sets the color of the graphic to a duotone based on a specified color
     * @param color
     */
    public static void setColor( Color color ) {
        GasMoleculeGraphic.setColor( color );
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            MakeDuotoneImageOp op = new MakeDuotoneImageOp( new Color( color.getRed(), color.getGreen(), color.getBlue() ));
            op.filter( s_particleImage, s_particleImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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
