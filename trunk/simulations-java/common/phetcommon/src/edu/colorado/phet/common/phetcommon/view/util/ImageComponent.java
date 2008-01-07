package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;

/**
 * Created by: Sam
 * Jan 7, 2008 at 1:46:40 PM
 */
public class ImageComponent extends Component {
    private Image image;

    public ImageComponent( Image image ) {
        this.image = image;
        setSize( image.getWidth( this ), image.getHeight( this ) );
    }

    public void paint( Graphics g ) {
        super.paint( g );
        g.drawImage( image, 0, 0, this );
    }
}
