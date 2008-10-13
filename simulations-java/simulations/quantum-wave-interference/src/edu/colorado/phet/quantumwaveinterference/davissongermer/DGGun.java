/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.phetcommon.ImagePComboBox;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.gun.*;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:08:56 PM
 */

public class DGGun extends IntensityGunNode implements FireParticle {
    private DGParticle dgParticle;
    private FireButton fireOne;
    private DGGunControlPanel dgGunControlPanel;

    public DGGun( QWIPanel QWIPanel ) {
        super( QWIPanel );
    }

    public DGParticle getDgParticle() {
        return dgParticle;
    }

    protected ImagePComboBox initComboBox() {
        dgParticle = new DGParticle( this );
        final IntensityBeam[] mybeams = new IntensityBeam[]{
                new ParticleBeam( dgParticle )
        };
        setBeams( mybeams );
        final ImagePComboBox imageComboBox = new ImagePComboBox( mybeams );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( QWIResources.getString( "gun.type" ) ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( mybeams[index] );
            }
        } );
        return imageComboBox;
    }

    public void clearAndFire() {
        fireParticle();
    }

    protected GunControlPanel createGunControlPanel() {
        if( fireOne == null ) {
            fireOne = new FireButton( this );
        }
        dgGunControlPanel = new DGGunControlPanel( getSchrodingerPanel() );
        dgGunControlPanel.setFillNone();
        dgGunControlPanel.add( fireOne );
        dgGunControlPanel.setFillHorizontal();
        dgGunControlPanel.add( getIntensitySlider() );//todo unprotect data
        dgGunControlPanel.add( getAlwaysOnCheckBox() );//todo unprotect data
        return dgGunControlPanel;
    }

    public void fireParticle() {
        double intensityScale = dgParticle.getIntensityScale();
        dgParticle.setIntensityScale( 1.0 );
        dgParticle.fireParticle();
        dgParticle.setIntensityScale( intensityScale );
        notifyGunFired();
    }

//    public double getVelocityRealUnits() {
//        return dgGunControlPanel.getVelocityRealUnits();
//    }

}
