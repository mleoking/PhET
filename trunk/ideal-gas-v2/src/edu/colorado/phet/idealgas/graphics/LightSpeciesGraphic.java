/*
 * Class: LightSpeciesGraphic
 * Package: edu.colorado.phet.graphics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.common.view.util.ImageLoader;
//import edu.colorado.phet.graphics.util.ResourceLoader;

import java.awt.*;
import java.io.IOException;

/**
 *
 */
public class LightSpeciesGraphic extends GasMoleculeGraphic {

    public LightSpeciesGraphic() {
        this.setRep( getImage() );
    }

    protected Image getImage() {
//    private Image getImage() {
        if( s_particleImage == null ) {
//            ResourceLoader loader = new ResourceLoader();
//            s_particleImage = loader.loadImage( s_imageName ).getImage();
            try {
                s_particleImage = ImageLoader.loadBufferedImage( s_imageName );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return s_particleImage;
    }

    //
    // Static fields and methods
    //
    static String s_imageName = IdealGasConfig.RED_PARTICLE_IMAGE_FILE;
    static Image s_particleImage;
}
