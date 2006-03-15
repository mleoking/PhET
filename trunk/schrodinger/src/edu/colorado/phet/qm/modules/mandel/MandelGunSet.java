/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.gun.HighIntensityBeam;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.BlueGunDetails;
import edu.colorado.phet.qm.view.piccolo.PinkGunDetails;

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
    private Photon photon;
    private PhotonMandelBeam photonMandelBeam;

    public MandelGunSet( MandelSchrodingerPanel mandelSchrodingerPanel ) {
        super( mandelSchrodingerPanel );
        leftGun = new MandelGun( "images/mandel-gun.gif", mandelSchrodingerPanel );
        leftGun.setControlsOffset( -leftGun.getGunControlPanelPSwing().getFullBounds().getWidth() + 50, 0 );
        leftGun.setControlBackgroundColor( BlueGunDetails.gunBackgroundColor );
        leftGun.translateOnGunControls( 0, 5 );
        rightGun = new MandelGun( "images/gun2-ii.gif", mandelSchrodingerPanel );
        rightGun.setControlsOffset( rightGun.getGunImageGraphic().getFullBounds().getWidth(), 0 );
        rightGun.setControlBackgroundColor( PinkGunDetails.backgroundColor );
        rightGun.translateOnGunControls( 10, 5 );
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

    public void setBeamParameters( MandelModule.BeamParam leftParam, MandelModule.BeamParam rightParam ) {
        photonMandelBeam.setBeamParameters( leftParam, rightParam );
    }

    protected ImagePComboBox initComboBox() {
        photon = new Photon( this, "Photons", "images/photon-thumb.jpg" );
        photonMandelBeam = new PhotonMandelBeam( this, photon );
        final HighIntensityBeam[] beams = new HighIntensityBeam[]{photonMandelBeam};
        setBeams( beams );

        final ImagePComboBox imageComboBox = new ImagePComboBox( beams );
//        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( beams[index] );
            }
        } );
        imageComboBox.setVisible( false );
        return imageComboBox;
    }

    public MandelGun getLeftGun() {
        return leftGun;
    }

    public MandelGun getRightGun() {
        return rightGun;
    }

}
