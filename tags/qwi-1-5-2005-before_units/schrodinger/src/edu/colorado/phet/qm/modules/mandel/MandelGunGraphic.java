/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.gun.HighIntensityBeam;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:35:37 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelGunGraphic extends HighIntensityGunGraphic {
    public PImage leftGun;
    public PImage rightGun;

    public MandelGunGraphic( MandelSchrodingerPanel mandelSchrodingerPanel ) {
        super( mandelSchrodingerPanel );
        getGunImageGraphic().setVisible( false );
        try {
            leftGun = new PImage( ImageLoader.loadBufferedImage( "images/mandel-gun.gif" ) );
            leftGun.setPickable( false );
            leftGun.setChildrenPickable( false );
            BufferedImage newImage = ImageLoader.loadBufferedImage( "images/gun2-ii.gif" );
            newImage = BufferedImageUtils.flipX( newImage );
            rightGun = new PImage( newImage );
            rightGun.setPickable( false );
            rightGun.setChildrenPickable( false );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        addChild( 0, leftGun );
        addChild( 0, rightGun );
//        leftGun.setOffset( new Point2D.Double( origGunLoc.x - dx, origGunLoc.y ) );
//        rightGun.setOffset( new Point2D.Double( origGunLoc.x + dx, origGunLoc.y ) );
        getComboBoxGraphic().setVisible( false );
        layoutChildren();
    }

    protected void layoutChildren() {
        super.layoutChildren();
        Point2D origGunLoc = getGunLocation();
        double dx = getGunOffsetDx();
        if( leftGun != null && rightGun != null ) {
            double dy = 5;
            leftGun.setOffset( new Point2D.Double( origGunLoc.getX() - dx, origGunLoc.getY() - dy ) );
            rightGun.setOffset( new Point2D.Double( origGunLoc.getX() + dx, origGunLoc.getY() - dy ) );
        }
        if( getGunControls() != null ) {
            getGunControls().setOffset( getGunControlPSwing().getFullBounds().getX(), getGunControlPSwing().getFullBounds().getMaxY() );
        }
    }

    protected double getControlOffsetX() {
        if( rightGun != null ) {
            return rightGun.getFullBounds().getMaxX();
        }
        else {
            return 0;
        }
    }

    private double getGunOffsetDx() {
        double width = getSchrodingerPanel().getWavefunctionGraphic().getGlobalFullBounds().getWidth();
        return width / 2 - width * DoublePhotonWave.getFractionalInset();
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
