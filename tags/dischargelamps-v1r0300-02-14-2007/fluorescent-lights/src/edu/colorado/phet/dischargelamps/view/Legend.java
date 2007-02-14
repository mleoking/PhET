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
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.DischargeLampAtom;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.model.HydrogenProperties;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.quantum.model.Photon;
import edu.colorado.phet.quantum.view.AnnotatedAtomGraphic;

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
    Icon atomIcon = null;

    public Legend() {
        super( new GridBagLayout() );
        setBorder( new TitledBorder( SimStrings.get( "Legend.title" ) ));
        createIcons();
        layoutPanel();
    }

    private void layoutPanel() {
        GridBagConstraints iconGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 60, 5, 10 ),
                                                             0, 0 );
        GridBagConstraints labelGbc = new GridBagConstraints( 1, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.WEST,
                                                              GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 5, 10 ),
                                                              0, 0 );
        add( new JLabel( atomIcon ), iconGbc );
        add( new JLabel( SimStrings.get( "Legend.atom" ) ), labelGbc );
        add( new JLabel( electronIcon ), iconGbc );
        add( new JLabel( SimStrings.get( "Legend.electron" ) ), labelGbc );
        add( new JLabel( photonIcon ), iconGbc );
        add( new JLabel( SimStrings.get( "Legend.photon" ) ), labelGbc );
    }

    private void createIcons() {

        // Make the electron icon
        try {
            BufferedImage electronImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.ELECTRON_IMAGE_FILE_NAME );
            electronIcon = new ImageIcon( electronImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Make the photon icon
        Photon photon = Photon.create( 400, new Point2D.Double(), new Vector2D.Double() );
        BufferedImage photonImage = PhotonGraphic.getInstance( this, photon ).getImage();
        photonIcon = new ImageIcon( photonImage );

        // Make the atom icon
        DischargeLampAtom atom = new DischargeLampAtom( new DischargeLampModel(),
                                                        new HydrogenProperties() );
        AnnotatedAtomGraphic atomGraphic = new AnnotatedAtomGraphic( this, atom );
        BufferedImage atomBI = new BufferedImage( atomGraphic.getWidth(), atomGraphic.getHeight(),
                                                  BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2BI = (Graphics2D)atomBI.getGraphics();
        g2BI.translate( atomGraphic.getWidth() / 2, atomGraphic.getHeight() / 2);
        g2BI.scale( .8, .8 );
        atomGraphic.paint( g2BI );
        atomIcon = new ImageIcon( atomBI );
    }
}
