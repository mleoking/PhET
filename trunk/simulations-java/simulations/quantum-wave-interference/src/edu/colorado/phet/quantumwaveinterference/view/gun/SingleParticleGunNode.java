/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ImagePComboBox;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Map;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 4:03:38 PM
 */

public class SingleParticleGunNode extends AbstractGunNode implements FireParticle {
    private PlainFireButton fireOne;
    private GunParticle gunParticle;
    private GunParticle[] gunParticles;
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

        setGunParticle( gunParticles[0] );
//        setOnGunControl( new PSwing( schrodingerPanel, fireOne ) );
        setOnGunControl( fireOne );
    }

    private GunControlPanel createGunControlPanel() {
        GunControlPanel gunControlPanel = new GunControlPanel( getSchrodingerPanel() );
        gunControlPanel.setFillNone();
        gunControlPanel.add( autoFireJCheckBox );
        return gunControlPanel;
    }

    protected Point getGunLocation() {
        if( gunParticle != null ) {
            return gunParticle.getGunLocation();
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
        gunParticle.fireParticle();
        notifyGunFired();
    }

    public GunParticle getGunParticle() {
        return gunParticle;
    }

    public void addMomentumChangeListener( MomentumChangeListener momentumChangeListener ) {
        for( int i = 0; i < gunParticles.length; i++ ) {
            gunParticles[i].addMomentumChangeListerner( momentumChangeListener );
        }
    }

    protected void setGunParticle( GunParticle particle ) {
        if( particle != gunParticle ) {
            getDiscreteModel().clearWavefunction();
            if( gunParticle != null ) {
                gunParticle.deactivate( this );
            }
            particle.activate( this );
            gunParticle = particle;
        }
        updateGunLocation();
        getSchrodingerModule().beamTypeChanged();
        notifyGunParticleTypeChanged();
    }

    private ArrayList listeners = new ArrayList();

    public static interface SingleParticleGunNodeListener {
        void gunParticleTypeChanged();
    }

    public void addSingleParticleGunNodeListener( SingleParticleGunNodeListener listener ) {
        listeners.add( listener );
    }

    public void notifyGunParticleTypeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            SingleParticleGunNodeListener listener = (SingleParticleGunNodeListener)listeners.get( i );
            listener.gunParticleTypeChanged();
        }
    }

    protected ImagePComboBox initComboBox() {
        Photon photon = new Photon( this, QWIResources.getString( "particles.photons" ), "quantum-wave-interference/images/photon-thumb.jpg" );
        PhotonBeam photonBeam = new PhotonBeam( this, photon );
        photonBeamParticle = new PhotonBeamParticle( this, QWIResources.getString( "particles.photons" ), photonBeam );

        gunParticles = new GunParticle[]{
                photonBeamParticle,
                DefaultGunParticle.createElectron( this ),
                DefaultGunParticle.createNeutron( this ),
                DefaultGunParticle.createHelium( this ),
        };

        final ImagePComboBox imageComboBox = new ImagePComboBox( gunParticles );
//        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setGunParticle( gunParticles[index] );
            }
        } );
        return imageComboBox;
    }

    protected void setGunControls( JComponent gunControls ) {
        gunControlPanel.setGunControls( gunControls );
    }

    public GunParticle[] getGunParticles() {
        return gunParticles;
    }

    public void reset() {
//        super.reset();
        photonBeamParticle.reset();
    }

    public Map getModelParameters() {
        Map sup = super.getModelParameters();
        sup.putAll( gunParticle.getModelParameters() );
        return sup;
    }

    public GunControlPanel getGunControlPanel() {
        return gunControlPanel;
    }

    public boolean isPhotonMode() {
        return gunParticle instanceof PhotonBeamParticle;
    }

    public boolean isFiring() {
        return gunParticle.isFiring();
    }
}
