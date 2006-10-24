/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.gun;

import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.QWIPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class SingleParticleGunNode extends AbstractGunNode implements FireParticle {
    private PlainFireButton fireOne;
    private GunParticle currentObject;
    private GunParticle[] gunItems;
    private AutoFire autoFire;

    private PhotonBeamParticle photonBeamParticle;
    protected final JCheckBox autoFireJCheckBox;
    private GunControlPanel gunControlPanel;

    public SingleParticleGunNode( final QWIPanel QWIPanel ) {
        super( QWIPanel );
        fireOne = new PlainFireButton( this, this );
        fireOne.addButtonEnableDisable();

        autoFire = new AutoFire( this, QWIPanel.getIntensityDisplay() );
        autoFireJCheckBox = new AutoFireCheckBox( autoFire );

        this.gunControlPanel = createGunControlPanel();
//        addChild( gunControlPanel.getPSwing() );

        setGunParticle( gunItems[0] );
//        setOnGunControl( new PSwing( schrodingerPanel, fireOne ) );
        setOnGunControl( fireOne );
    }

    private GunControlPanel createGunControlPanel() {
        GunControlPanel gunControlPanel = new GunControlPanel( getSchrodingerPanel() );
        gunControlPanel.setFillNone();
//        gunControlPanel.add( fireOne );
        gunControlPanel.add( autoFireJCheckBox );

        return gunControlPanel;
    }

    protected void layoutChildren() {
        super.layoutChildren();
//        gunControlPanel.getPSwing().setOffset( getGunImageGraphic().getWidth() - 10, getControlOffsetY() );
    }

    protected Point getGunLocation() {
        if( currentObject != null ) {
            return currentObject.getGunLocation();
        }
        else {
            return new Point();
        }
    }

    public void clearAndFire() {
        clearWavefunction();
        fireParticle();
        fireOne.setEnabled( false );
    }

    private void clearWavefunction() {
        getDiscreteModel().clearWavefunction();
    }

    public void fireParticle() {
        currentObject.fireParticle();
        notifyFireListeners();
    }

    public GunParticle getCurrentObject() {
        return currentObject;
    }

    public void addMomentumChangeListener( MomentumChangeListener momentumChangeListener ) {
        for( int i = 0; i < gunItems.length; i++ ) {
            gunItems[i].addMomentumChangeListerner( momentumChangeListener );
        }
    }

    protected void setGunParticle( GunParticle particle ) {
        if( particle != currentObject ) {
            getDiscreteModel().clearWavefunction();
            if( currentObject != null ) {
                currentObject.deactivate( this );
            }
            particle.activate( this );
            currentObject = particle;
        }
        updateGunLocation();
        getSchrodingerModule().beamTypeChanged();
    }

    protected ImagePComboBox initComboBox() {
        Photon photon = new Photon( this, QWIStrings.getString( "photons" ), "images/photon-thumb.jpg" );
        PhotonBeam photonBeam = new PhotonBeam( this, photon );
        photonBeamParticle = new PhotonBeamParticle( this, QWIStrings.getString( "photons" ), photonBeam );

        gunItems = new GunParticle[]{
                photonBeamParticle,
                DefaultGunParticle.createElectron( this ),
                DefaultGunParticle.createNeutron( this ),
                DefaultGunParticle.createHelium( this ),
        };

        final ImagePComboBox imageComboBox = new ImagePComboBox( gunItems );
//        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setGunParticle( gunItems[index] );
            }
        } );
        return imageComboBox;
    }

    protected void setGunControls( JComponent gunControls ) {
        gunControlPanel.setGunControls( gunControls );
    }

    public GunParticle[] getGunItems() {
        return gunItems;
    }

    public void reset() {
//        super.reset();
        photonBeamParticle.reset();
    }

    public Map getModelParameters() {
        Map sup = super.getModelParameters();
        sup.putAll( currentObject.getModelParameters() );
        return sup;
    }

    public GunControlPanel getGunControlPanel() {
        return gunControlPanel;
    }

    public boolean isPhotonMode() {
        return currentObject instanceof PhotonBeamParticle;
    }

    public boolean isFiring() {
        return currentObject.isFiring();
    }
}
