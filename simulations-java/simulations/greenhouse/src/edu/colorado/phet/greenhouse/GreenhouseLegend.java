// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.greenhouse;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.photonabsorption.model.Photon;
import edu.colorado.phet.greenhouse.view.PhotonGraphic;

public class GreenhouseLegend extends PhetTitledPanel {

    GreenhouseLegend() {
        this( PhetTitledBorder.DEFAULT_FONT );
    }
    
    public GreenhouseLegend( Font titleFont ){
        super( GreenhouseResources.getString( "GreenhouseLegend.LegendTitle" ), titleFont );

        // Draw an IR photon and a sunlight photon
        BufferedImage irPhotonBI = new BufferedImage( 15, 15, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D) irPhotonBI.getGraphics();
        Photon irPhoton = new Photon( GreenhouseConfig.irWavelength, null );
        PhotonGraphic irPhotonGraphic = new PhotonGraphic( irPhoton );
        irPhotonGraphic.paint( g2 );
        ImageIcon irPhotonIcon = new ImageIcon( irPhotonGraphic.getImage() );

        BufferedImage sunlightPhotonBI = new BufferedImage( 15, 15, BufferedImage.TYPE_INT_ARGB );
        g2 = (Graphics2D) sunlightPhotonBI.getGraphics();
        Photon sunlightPhoton = new Photon( GreenhouseConfig.sunlightWavelength, null );
        PhotonGraphic sunlightPhotonGraphic = new PhotonGraphic( sunlightPhoton );
        sunlightPhotonGraphic.paint( g2 );
        ImageIcon sunlightPhotonIcon = new ImageIcon( sunlightPhotonGraphic.getImage() );

        setLayout( new GridBagLayout() );
        try {
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0,
                    GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL,
                    new Insets( 0, 0, 0, 0 ), 0, 20 );

            JLabel sunlightLegend = new JLabel( GreenhouseResources.getString( "GreenhouseLegend.SunlightPhotonLabel" ),
                                                sunlightPhotonIcon, SwingConstants.LEFT );
            add(sunlightLegend, gbc);

            JLabel irLegend = new JLabel( GreenhouseResources.getString( "GreenhouseLegend.InfraredPhotonLabel" ),
                                          irPhotonIcon, SwingConstants.LEFT );

            add(irLegend, gbc);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
