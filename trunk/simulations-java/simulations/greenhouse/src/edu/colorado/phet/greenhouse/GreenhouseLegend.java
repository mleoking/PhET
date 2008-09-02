/**
 * Class: GreenhouseLegend
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Dec 1, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.greenhouse.phetcommon.view.util.GraphicsUtil;

public class GreenhouseLegend extends JPanel {

    GreenhouseLegend() {

        // Draw an IR photon and a sunlight photon
        BufferedImage irPhotonBI = new BufferedImage( 15, 15, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D) irPhotonBI.getGraphics();
        Photon irPhoton = new Photon( GreenhouseConfig.irWavelength, null );
        PhotonGraphic irPhotonGraphic = new PhotonGraphic( irPhoton );
//        for( int i = 0; i < PhotonGraphic.numTailPts; i ++ ) {
//            irPhoton.setLocation(irPhotonBI.getWidth() / 2, i * 3);
//            irPhotonGraphic.update();
//        }
//        irPhotonGraphic.setPhotonStroke( new BasicStroke( 3 ) );
        irPhotonGraphic.paint( g2 );
        ImageIcon irPhotonIcon = new ImageIcon( irPhotonGraphic.getImage() );
//        ImageIcon irPhotonIcon = new ImageIcon( irPhotonBI );

        BufferedImage sunlightPhotonBI = new BufferedImage( 15, 15, BufferedImage.TYPE_INT_ARGB );
        g2 = (Graphics2D) sunlightPhotonBI.getGraphics();
        Photon sunlightPhoton = new Photon( GreenhouseConfig.sunlightWavelength, null );
        PhotonGraphic sunlightPhotonGraphic = new PhotonGraphic( sunlightPhoton );
//        for( int i = 0; i < PhotonGraphic.numTailPts; i ++ ) {
//            sunlightPhoton.setLocation(sunlightPhotonBI.getWidth() / 2, i * 3);
//            sunlightPhotonGraphic.update();
//        }
//        sunlightPhotonGraphic.setPhotonStroke( new BasicStroke( 3 ) );
        sunlightPhotonGraphic.paint( g2 );
        ImageIcon sunlightPhotonIcon = new ImageIcon( sunlightPhotonGraphic.getImage() );
//        ImageIcon sunlightPhotonIcon = new ImageIcon( sunlightPhotonBI );

        setLayout( new GridBagLayout() );
        this.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), SimStrings.get( "GreenhouseLegend.LegendTitle" ) ) );
//            ImageIcon electronImg = new ImageIcon( ImageLoader.fetchImage( "greenhouse/images/small-yellow-electron.gif" ));
        int rowIdx = 0;
        try {
            JLabel sunlightLegend = new JLabel( SimStrings.get( "GreenhouseLegend.SunlightPhotonLabel" ),
                                                sunlightPhotonIcon, SwingConstants.LEFT );
            GraphicsUtil.addGridBagComponent( this, sunlightLegend,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.WEST );
            JLabel irLegend = new JLabel( SimStrings.get( "GreenhouseLegend.InfraredPhotonLabel" ),
                                          irPhotonIcon, SwingConstants.LEFT );
            GraphicsUtil.addGridBagComponent( this, irLegend,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.WEST );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }
}
