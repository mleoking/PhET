/**
 * Class: MoleculeFactoryPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Oct 1, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.model.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class MoleculeFactoryPanel extends JPanel {
    private Random random = new Random();
    private int currNumMolecules;
    private IdealGasModule module;
    private HollowSphere balloon;
    private Class gasSpecies;
    private JSpinner particleSpinner;

    MoleculeFactoryPanel( IdealGasModule module, HollowSphere balloon, Class gasSpecies ) {
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
        particleSpinner = new JSpinner( model );
        particleSpinner.setPreferredSize( new Dimension( 50, 20 ) );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        this.add( particleSpinner, gbc );

        particleSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumParticles( ( (Integer)particleSpinner.getValue() ).intValue() );
            }
        } );
        this.balloon = balloon;
        this.gasSpecies = gasSpecies;
    }

    public void setGasSpecies( Class gasSpecies ) {
        this.gasSpecies = gasSpecies;
    }

    protected void pumpMolecule( GasMolecule molecule ) {
        PumpMoleculeCmd cmd = new PumpMoleculeCmd( module.getIdealGasModel(), molecule,
                                                   module );
        cmd.doIt();
        balloon.addContainedBody( molecule );
    }

    protected Class getCurrentGasSpecies() {
        return gasSpecies;
    }

    protected void reset() {
        particleSpinner.setEnabled( false );
        particleSpinner.setValue( new Integer( 0 ));
        particleSpinner.setEnabled( true );
    }

    protected Point2D getNewMoleculeLocation() {
        double r = random.nextDouble() - GasMolecule.s_defaultRadius;
        double theta = random.nextDouble() * Math.PI * 2;
        Point2D.Double p = new Point2D.Double( balloon.getPosition().getX() + r * Math.cos( theta ),
                                               balloon.getPosition().getY() + r * Math.sin( theta ) );
        return p;
    }

    protected Vector2D getNewMoleculeVelocity() {
        double s = 0;
        if( getCurrentGasSpecies() == HeavySpecies.class ) {
            s = module.getIdealGasModel().getHeavySpeciesAveSpeed();
            if( s == 0 ) {
                s = Math.sqrt( 2 * IdealGasModel.DEFAULT_ENERGY / HeavySpecies.getMoleculeMass() );
            }
        }
        if( getCurrentGasSpecies() == LightSpecies.class ) {
            s = module.getIdealGasModel().getLightSpeciesAveSpeed();
            if( s == 0 ) {
                s = Math.sqrt( 2 * IdealGasModel.DEFAULT_ENERGY / LightSpecies.getMoleculeMass() );
            }
        }
        double theta = random.nextDouble() * Math.PI * 2;
        return new Vector2D.Double( s * Math.cos( theta ), s * Math.sin( theta ) );
    }

    protected void setNumParticles( int numParticles ) {
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
                pumpMolecule( gm );
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
