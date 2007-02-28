/*
 * Class: HeavySpeciesGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.graphics.util.ResourceLoader;

import java.awt.*;

/**
 *
 */
public class HeavySpeciesGraphic extends GasMoleculeGraphic {

    public HeavySpeciesGraphic() {
        this.setRep( getImage() );
    }

    protected Image getImage() {
        if( s_particleImage == null ) {
            ResourceLoader loader = new ResourceLoader();
            s_particleImage = loader.loadImage( s_imageName ).getImage();
        }
        return s_particleImage;
    }

    //
    // Static fields and methods
    //
    static String s_imageName = IdealGasConfig.BLUE_PARTICLE_IMAGE_FILE;
    static Image s_particleImage;
}
