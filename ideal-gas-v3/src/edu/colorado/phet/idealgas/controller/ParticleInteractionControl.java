/**
 * Class: ParticleInteractionControl
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 30, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.GasMolecule;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ParticleInteractionControl extends JPanel {
        ParticleInteractionControl() {
            final JCheckBox noSphereSphereCollisionCB = new JCheckBox( SimStrings.get( "MeasurementControlPanel.No_particle_interactions" ) );
            this.add( noSphereSphereCollisionCB );
            noSphereSphereCollisionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    GasMolecule.enableParticleParticleInteractions( !noSphereSphereCollisionCB.isSelected() );
                }
            } );

            // Set default state
            noSphereSphereCollisionCB.setSelected( false );
//            noSphereSphereCollisionCB.setSelected( true );
            GasMolecule.enableParticleParticleInteractions( !noSphereSphereCollisionCB.isSelected() );
        }
    }
