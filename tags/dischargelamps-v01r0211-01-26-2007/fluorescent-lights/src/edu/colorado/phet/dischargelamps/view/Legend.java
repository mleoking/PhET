/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.quantum.model.Photon;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Legend
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Legend extends JPanel {
    Icon electronIcon = null;
    Icon photonIcon = null;

    public Legend() {
        super( new GridBagLayout() );
        setBorder( new TitledBorder( SimStrings.get( "Legend.title" ) ));
        createIcons();
        layoutPanel();
    }

    private void layoutPanel() {
        GridBagConstraints iconGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 10, 5, 10 ),
                                                             0, 0 );
        GridBagConstraints labelGbc = new GridBagConstraints( 1, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.NORTHWEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 0, 10, 5, 10 ),
                                                              0, 0 );
        add( new JLabel( electronIcon ), iconGbc );
        add( new JLabel( SimStrings.get( "Legend.electron" ) ), labelGbc );
        add( new JLabel( photonIcon ), iconGbc );
        add( new JLabel( SimStrings.get( "Legend.photon" ) ), labelGbc );
    }

    private void createIcons() {
        try {
            BufferedImage electronImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
            electronIcon = new ImageIcon( electronImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        Photon photon = Photon.create( 400, new Point2D.Double(), new Vector2D.Double() );
        BufferedImage photonImage = PhotonGraphic.getInstance( this, photon ).getImage();
        photonIcon = new ImageIcon( photonImage );
    }
}
