/**
 * Class: MoleculeFactoryPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 24, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasStrings;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;

public abstract class MoleculeFactoryPanel extends JPanel {

    private int currNumMolecules;
    private IdealGasModule module;

    protected abstract Class getCurrentGasSpecies();
    protected abstract Point2D getNewMoleculeLocation();
    protected abstract Vector2D getNewMoleculeVelocity();

    MoleculeFactoryPanel( IdealGasModule module ) {
        super( new GridLayout( 1, 2 ) );
        this.module = module;
        this.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 40 ) );
        this.setLayout( new GridLayout( 2, 1 ) );

        this.add( new JLabel( IdealGasStrings.get( "MeasurementControlPanel.Number_of_particles" ) ));
        // Set up the spinner for controlling the number of particles in
        // the hollow sphere
        Integer value = new Integer( 0 );
        Integer min = new Integer( 0 );
        Integer max = new Integer( 1000 );
        Integer step = new Integer( 1 );
        SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
        final JSpinner particleSpinner = new JSpinner( model );
        particleSpinner.setPreferredSize( new Dimension( 20, 5 ) );
        this.add( particleSpinner );

        particleSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumParticlesInSphere( ( (Integer)particleSpinner.getValue() ).intValue() );
            }
        } );
    }

    private void setNumParticlesInSphere( int numParticles ) {
        int dn = numParticles - currNumMolecules;
        if( dn > 0 ) {
            for( int i = 0; i < dn; i++ ) {
                Class species = getCurrentGasSpecies();
                Point2D location = getNewMoleculeLocation();
                Vector2D velocity = getNewMoleculeVelocity();
                GasMolecule gm = null;
                if( species == HeavySpecies.class ) {
                    gm = new HeavySpecies( location, velocity, new Vector2D.Double() );
                }
                if( species == LightSpecies.class ) {
                    gm = new LightSpecies( location, velocity, new Vector2D.Double() );
                }
                PumpMoleculeCmd cmd = new PumpMoleculeCmd( module.getIdealGasModel(), gm,
                                                           module );
                cmd.doIt();
            }
        }
        else if( dn < 0 ) {
            for( int i = 0; i < -dn; i++ ) {
                module.removeGasMolecule();
            }
        }
        currNumMolecules += dn;
    }
}
