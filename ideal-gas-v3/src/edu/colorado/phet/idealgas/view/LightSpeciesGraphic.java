/*
 * Class: LightSpeciesGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.GasMolecule;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class LightSpeciesGraphic extends GasMoleculeGraphic {

    static String s_imageName = IdealGasConfig.RED_PARTICLE_IMAGE_FILE;
    static BufferedImage s_particleImage;

    static {
        try {
            s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public LightSpeciesGraphic( ApparatusPanel apparatusPanel, GasMolecule molecule ) {
        super( apparatusPanel, s_particleImage, molecule );
    }
}
