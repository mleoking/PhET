/*
 * Class: LightSpeciesGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 */
public class LightSpeciesGraphic extends GasMoleculeGraphic {

    public LightSpeciesGraphic( Component component, GasMolecule molecule ) {
        super( component, s_particleImage, molecule );
    }

    //
    // Static fields and methods
    //
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
}
