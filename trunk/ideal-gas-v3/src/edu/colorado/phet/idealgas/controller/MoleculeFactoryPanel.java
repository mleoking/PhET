/**
 * Class: MoleculeFactoryPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 24, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;
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
        super( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        this.module = module;

        JLabel label = new JLabel( SimStrings.get( "MeasurementControlPanel.Number_of_particles" ) );
        this.add( label, gbc );
        // Set up the spinner for controlling the number of particles in
        // the hollow sphere
        Integer value = new Integer( 0 );
        Integer min = new Integer( 0 );
        Integer max = new Integer( 1000 );
        Integer step = new Integer( 1 );
        SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
        final JSpinner particleSpinner = new JSpinner( model );
        particleSpinner.setPreferredSize( new Dimension( 50, 20 ) );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        this.add( particleSpinner, gbc );
//        this.add( particleSpinner );

        particleSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumParticles( ( (Integer)particleSpinner.getValue() ).intValue() );
            }
        } );
    }

    private void setNumParticles( int numParticles ) {
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
