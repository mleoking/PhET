/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.resources;

import javax.swing.*;
import java.io.File;
import java.net.URL;

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
