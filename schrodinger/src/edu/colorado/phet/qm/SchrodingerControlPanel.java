/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerControlPanel extends ControlPanel {
    private SchrodingerModule module;

    public SchrodingerControlPanel( final SchrodingerModule module ) {
        super( module );
        this.module = module;
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        addControl( reset );

        VerticalLayoutPanel particleLauncher = new VerticalLayoutPanel();
        particleLauncher.setBorder( BorderFactory.createTitledBorder( "Particle Launcher" ) );


        JButton fireParticle = new JButton( "Fire Particle" );
        fireParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );
        particleLauncher.add( fireParticle );
        addControlFullWidth( particleLauncher );
    }

    private void fireParticle() {
        //add the specified wavefunction everywhere, then renormalize..?
        //clear the old wavefunction.
        DiscreteModel model = getDiscreteModel();
        InitialWavefunction initialWavefunction = new GaussianWave( model.getXMesh(), model.getYMesh(),
                                                                    new Point( (int)( model.getXMesh() * 0.85 ), model.getYMesh() / 2 ),
                                                                    new Vector2D.Double( -10, 0 ), 0.01 );
        module.fireParticle( initialWavefunction );
    }

    private DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }
}
