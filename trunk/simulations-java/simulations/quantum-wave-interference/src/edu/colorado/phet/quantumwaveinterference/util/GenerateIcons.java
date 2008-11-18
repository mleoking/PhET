package edu.colorado.phet.quantumwaveinterference.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.util.PhotonImageFactory;

public class GenerateIcons {
    public static void main( String[] args ) throws IOException {
        Image photonImage = PhotonImageFactory.createPhotonImage( 700, 24 );
        ImageIO.write( BufferedImageUtils.toBufferedImage( photonImage ), "PNG", new File( "C:\\Users\\Owner\\Desktop\\file.png" ) );
    }
}
