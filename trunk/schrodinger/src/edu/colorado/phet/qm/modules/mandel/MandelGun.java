/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.gun.HighIntensityBeam;
import edu.colorado.phet.qm.view.gun.HighIntensityGun;
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

public class MandelGun extends HighIntensityGun {

    public MandelGun( MandelPanel mandelPanel ) {
        super( mandelPanel );
        //todo piccolo
        Point origGunLoc = getGunLocation();
////        getGunImageGraphic().setLocation( origGunLoc.x-50,origGunLoc.y);
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
//        PhetImageGraphic gunGraphicLeft = new PhetImageGraphic( getSchrodingerPanel(), getGunImageResource() );
//        PhetImageGraphic gunGraphicRight = new PhetImageGraphic( getSchrodingerPanel(), getGunImageResource() );
//        addGraphic( gunGraphicLeft, Double.NEGATIVE_INFINITY );
//        addGraphic( gunGraphicRight, Double.NEGATIVE_INFINITY );
//
        leftGun.setOffset( new Point( origGunLoc.x - dx, origGunLoc.y ) );
        rightGun.setOffset( new Point( origGunLoc.x + dx, origGunLoc.y ) );
    }

    public int getFireButtonInsetDX() {
        return 0;
    }

    protected ImagePComboBox initComboBox() {
        Photon photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
//        Electron e = new Electron( this, "Electrons", "images/electron-thumb.jpg" );
//        Atom atom = new Atom( this, "Atoms", "images/atom-thumb.jpg" );

        final HighIntensityBeam[] beams = new HighIntensityBeam[]{
                new PhotonMandelBeam( this, photon )
        };
        setBeams( beams );

//            new ParticleBeam( e ),
//            new ParticleBeam( atom )};
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
        getComboBox().setVisible( false );
    }

}
