/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.phetcommon.ImagePComboBox;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.DefaultGunParticle;
import edu.colorado.phet.qm.view.gun.HighIntensityBeam;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.ParticleBeam;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:08:56 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGGun extends HighIntensityGunGraphic {
    public DGGun( SchrodingerPanel schrodingerPanel ) {
        super( schrodingerPanel );
    }

    protected ImagePComboBox initComboBox() {
        final HighIntensityBeam[] mybeams = new HighIntensityBeam[]{
                new ParticleBeam( DefaultGunParticle.createElectron( this ) )
        };
        setBeams( mybeams );
        final ImagePComboBox imageComboBox = new ImagePComboBox( mybeams );
        imageComboBox.setBorder( BorderFactory.createTitledBorder( "Gun Type" ) );
        imageComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                int index = imageComboBox.getSelectedIndex();
                setupObject( mybeams[index] );
            }
        } );
        return imageComboBox;
    }
}
