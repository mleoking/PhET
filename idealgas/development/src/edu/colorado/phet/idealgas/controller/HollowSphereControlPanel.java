/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 4, 2003
 * Time: 1:33:50 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.idealgas.graphics.HollowSphereGraphic;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class HollowSphereControlPanel extends JPanel {

    IdealGasApplication application;
    Class gasSpeciesClass;
    int prevNumParticles;

    public HollowSphereControlPanel( IdealGasApplication application ) {
        this.application = application;
        init();
    }

    private void init() {
        addHollowSphereControls();
    }

    public void setGasSpeciesClass( Class gasSpeciesClass ) {
        this.gasSpeciesClass = gasSpeciesClass;
    }

    /**
     * Create a panel for controlling the stove
     */
    private void addHollowSphereControls() {

        this.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 80 ));
        this.setLayout( new GridLayout( 2, 1 ));

        this.add( new JLabel( "Number of particles" ));
        // Set up the spinner for controlling the number of particles in
        // the hollow sphere
        HollowSphere sphere = (HollowSphere)application.getPhetMainPanel().
                getApparatusPanel().
                getGraphicOfType( HollowSphereGraphic.class ).getBody();
        prevNumParticles = sphere.numContainedBodies();
        Integer value = new Integer( sphere.numContainedBodies() );
        Integer min = new Integer(0);
        Integer max = new Integer(100);
        Integer step = new Integer(1);
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        final JSpinner particleSpinner = new JSpinner( model );
        this.add( particleSpinner );

        particleSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setNumParticlesInSphere( particleSpinner.getValue() );
            }
        } );

        this.setBorder( new TitledBorder( "Balloon" ) );
    }

    /**
     *
     */
    private void setNumParticlesInSphere( Object obj1 ) {
        int numParticles = ((Integer)obj1).intValue();
        HollowSphere sphere = (HollowSphere)application.getPhetMainPanel().
                getApparatusPanel().
                getGraphicOfType( HollowSphereGraphic.class ).getBody();

        Command cmd = null;
        if( numParticles > prevNumParticles ) {
            cmd = new AddParticlesToHollowSphereCmd( application,
                                                        sphere,
                                                        numParticles - prevNumParticles );
        }
        else if( numParticles < prevNumParticles ) {
            cmd = new RemoveParticlesFromHollowSphereCmd( application,
                                                        sphere,
                                                        prevNumParticles - numParticles );
        }
        prevNumParticles = numParticles;
        PhetApplication.instance().getPhysicalSystem().addPrepCmd( cmd );
    }
}
