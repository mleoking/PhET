/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * ImageResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ThumbnailResource extends SimResource {
    private ImageIcon imageIcon;

    public ThumbnailResource( URL url, File localRoot ) {
        super( url, localRoot );
        imageIcon = new ImageIcon( getLocalFile().getAbsolutePath() );
    }

    public ImageIcon getImageIcon() {
        return imageIcon;
    }
}
