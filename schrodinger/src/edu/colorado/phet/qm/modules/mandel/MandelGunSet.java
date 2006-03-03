/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.gun.HighIntensityBeam;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:35:37 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelGunSet extends HighIntensityGunGraphic {
    private MandelGun leftGun;
    private MandelGun rightGun;

    public MandelGunSet( MandelSchrodingerPanel mandelSchrodingerPanel ) {
        super( mandelSchrodingerPanel );
        leftGun = new MandelGun( "images/mandel-gun.gif", mandelSchrodingerPanel );
        leftGun.setControlsOffset( -leftGun.getGunControlPanelPSwing().getFullBounds().getWidth() + 50, 0 );
        rightGun = new MandelGun( "images/gun2-ii.gif", mandelSchrodingerPanel );
        rightGun.setControlsOffset( rightGun.getGunImageGraphic().getFullBounds().getWidth(), 0 );
        getOnGunGraphic().setVisible( false );
        getOnGunGraphic().setPickable( false );
        getOnGunGraphic().setChildrenPickable( false );

        getGunImageGraphic().setVisible( false );
        getGunImageGraphic().setPickable( false );
        getGunImageGraphic().setChildrenPickable( false );

        addChild( leftGun );
        addChild( rightGun );
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

}
