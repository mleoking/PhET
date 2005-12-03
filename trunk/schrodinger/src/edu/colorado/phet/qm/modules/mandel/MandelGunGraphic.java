/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.gun.HighIntensityBeam;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:35:37 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelGunGraphic extends HighIntensityGunGraphic {

    public MandelGunGraphic( MandelPanel mandelPanel ) {
        super( mandelPanel );
        Point origGunLoc = getGunLocation();
        int dx = 112;
        getGunImageGraphic().setVisible( false );
        PImage leftGun = null;
        PImage rightGun = null;
        try {
            leftGun = new PImage( ImageLoader.loadBufferedImage( GUN_RESOURCE ) );
            leftGun.setPickable( false );
            leftGun.setChildrenPickable( false );
            rightGun = new PImage( ImageLoader.loadBufferedImage( "images/gun2-ii.gif" ) );
            rightGun.setPickable( false );
            rightGun.setChildrenPickable( false );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        addChild( 0, leftGun );
        addChild( 0, rightGun );
        leftGun.setOffset( new Point( origGunLoc.x - dx, origGunLoc.y ) );
        rightGun.setOffset( new Point( origGunLoc.x + dx, origGunLoc.y ) );
        getComboBoxGraphic().setVisible( false );
    }

    public int getFireButtonInsetDX() {
        return 0;
    }

    protected ImagePComboBox initComboBox() {
        Photon photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );

        final HighIntensityBeam[] beams = new HighIntensityBeam[]{
                new PhotonMandelBeam( this, photon )
        };
        setBeams( beams );

        final ImagePComboBox imageComboBox = new ImagePComboBox( beams );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( beams[index] );
            }
        } );
        imageComboBox.setVisible( false );
        return imageComboBox;
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        getComboBoxGraphic().setVisible( false );
    }

}
