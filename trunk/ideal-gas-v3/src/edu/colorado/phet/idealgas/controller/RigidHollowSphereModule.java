/**
 * Class: RigidHollowSphereModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 18, 2004
 * Time: 12:35:56 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereHollowSphereExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.IdealGasStrings;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Random;

public class RigidHollowSphereModule extends IdealGasModule {

    private static final float initialVelocity = 35;

    private HollowSphere sphere;
    private Class gasSpecies = HeavySpecies.class;

    public RigidHollowSphereModule( AbstractClock clock ) {
        super( clock, IdealGasStrings.get( "ModuleTitle.RigidHollowSphere"));
        double xOrigin = 200;
        double yOrigin = 250;
        double xDiag = 434;
        double yDiag = 397;

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereHollowSphereExpert( getIdealGasModel(), clock.getDt() ) );

        // Set the size of the box
        final Box2D box = getIdealGasModel().getBox();
        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );
        sphere = new HollowSphere( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
                                                       box.getMinY() + box.getHeight() / 2 ),
                                   new Vector2D.Double( 0, 0 ),
                                   new Vector2D.Double( 0, 0 ),
                                   100,
                                   50 );
        box.setMinimumWidth( sphere.getRadius() * 3 );

        new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
        getIdealGasModel().getBox().addContainedBody( sphere );
        addGraphic( new HollowSphereGraphic( getApparatusPanel(), sphere ), 20 );

        //        Constraint constraintSpec = new BoxMustContainParticle( box, sphere, getIdealGasModel() );
        //        sphere.addConstraint( constraintSpec );

        // Put some heavy gas outside the sphere
        for( int i = 0; i < 0; i++ ) {
            //        for( int i = 0; i < 100; i++ ) {
            double x = Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            double y = Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            double theta = Math.random() * Math.PI * 2;
            double vx = (double)Math.cos( theta ) * initialVelocity;
            double vy = (double)Math.sin( theta ) * initialVelocity;
            GasMolecule p1 = new HeavySpecies( new Point2D.Double( x, y ),
                                               new Vector2D.Double( vx, vy ),
                                               new Vector2D.Double( 0, 0 ) );
            new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
            //            constraintSpec = new BoxMustContainParticle( box, p1, getIdealGasModel() );
            //            p1.addConstraint( constraintSpec );
            //
            //            constraintSpec = new HollowSphereMustNotContainParticle( sphere, p1 );
            //            p1.addConstraint( constraintSpec );
        }

        // Put some heavy gas in the sphere
        GasMolecule p1 = null;
        int num = 0;
        //        int num = 6;
        //        int num = 4;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                double v = initialVelocity;
                double theta = Math.random() * Math.PI * 2;
                double vx = Math.cos( theta ) * v;
                double vy = Math.sin( theta ) * v;
                if( HeavySpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new HeavySpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ) );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                if( LightSpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new LightSpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ) );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                sphere.addContainedBody( p1 );

                //                constraintSpec = new BoxMustContainParticle( box, p1 );
                //                p1.addConstraint( constraintSpec );
                //
                //                constraintSpec = new HollowSphereMustContainParticle( sphere, p1 );
                //                p1.addConstraint( constraintSpec );
            }
        }

        // Turn on gravity
        //        getIdealGasApplication().setGravityEnabled( true );
        //        getIdealGasApplication().setGravity( 15 );


        // Add the specific controls we need to the control panel
        //        hsaControlPanel = new HollowSphereControlPanel( getIdealGasApplication() );
        //        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        //        mainControlPanel.add( hsaControlPanel );
        //        hsaControlPanel.setGasSpeciesClass( LightSpecies.class );

        JPanel controlPanel = new JPanel( new GridBagLayout() );
        controlPanel.setBorder( new TitledBorder( IdealGasStrings.get( "RigidHollowSphereControlPanel.controlsTitle" ) ) );

        //        JPanel speciesButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        //        speciesButtonPanel.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 100 ) );
        //        final JRadioButton heavySpeciesRB = new JRadioButton( IdealGasStrings.get( "Common.Heavy_Species" ) );
        //        heavySpeciesRB.setForeground( Color.blue );
        //        final JRadioButton lightSpeciesRB = new JRadioButton( IdealGasStrings.get( "Common.Light_Species" ) );
        //        lightSpeciesRB.setForeground( Color.red );
        //        final ButtonGroup speciesGroup = new ButtonGroup();
        //        speciesGroup.add( heavySpeciesRB );
        //        speciesGroup.add( lightSpeciesRB );
        //        speciesButtonPanel.add( heavySpeciesRB );
        //        heavySpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
        //        lightSpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
        //        speciesButtonPanel.add( lightSpeciesRB );
        //        heavySpeciesRB.setSelected( true );
        //        heavySpeciesRB.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent event ) {
        //                if( heavySpeciesRB.isSelected() ) {
        //                    gasSpecies = HeavySpecies.class;
        //                }
        //            }
        //        } );
        //
        //        lightSpeciesRB.addActionListener( new ActionListener() {
        //            public void actionPerformed( ActionEvent event ) {
        //                if( lightSpeciesRB.isSelected() ) {
        //                    gasSpecies = LightSpecies.class;
        //                }
        //            }
        //        } );
        //
        GridBagConstraints gbc = null;
        Insets insets = new Insets( 0, 0, 0, 0 );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, 0, 0 );
        controlPanel.add( new SpeciesSelectionPanel(), gbc );
        gbc.gridy = 1;
        controlPanel.add( new MoleculeFactoryPanel( this ), gbc );
        getControlPanel().add( controlPanel );
    }

    private class MoleculeFactoryPanel extends MoleculeFactoryPanel {
        Random random = new Random();

        MoleculeFactoryPanel( IdealGasModule module ) {
            super( module );

        }

        protected Class getCurrentGasSpecies() {
            return RigidHollowSphereModule.this.gasSpecies;
        }

        protected Point2D getNewMoleculeLocation() {
            double r = random.nextDouble() - GasMolecule.s_defaultRadius;
            double theta = random.nextDouble() * Math.PI * 2;
            Point2D.Double p = new Point2D.Double( sphere.getPosition().getX() + r * Math.cos( theta ),
                                                   sphere.getPosition().getY() + r * Math.sin( theta ) );
            return p;
        }

        protected Vector2D getNewMoleculeVelocity() {
            double s = 0;
            if( getCurrentGasSpecies() == HeavySpecies.class ) {
                s = getIdealGasModel().getHeavySpeciesAveSpeed();
                if( s == 0 ) {
                    s = Math.sqrt( 2 * IdealGasModel.DEFAULT_ENERGY / HeavySpecies.getMoleculeMass() );
                }
            }
            if( getCurrentGasSpecies() == LightSpecies.class ) {
                s = getIdealGasModel().getLightSpeciesAveSpeed();
                if( s == 0 ) {
                    s = Math.sqrt( 2 * IdealGasModel.DEFAULT_ENERGY / LightSpecies.getMoleculeMass() );
                }
            }
            double theta = random.nextDouble() * Math.PI * 2;
            return new Vector2D.Double( s * Math.cos( theta ), s * Math.sin( theta ) );
        }
    }

    private class SpeciesSelectionPanel extends JPanel {
        SpeciesSelectionPanel() {
            setLayout( new GridBagLayout() );
            final JRadioButton heavySpeciesRB = new JRadioButton( IdealGasStrings.get( "Common.Heavy_Species" ) );
            heavySpeciesRB.setForeground( Color.blue );
            final JRadioButton lightSpeciesRB = new JRadioButton( IdealGasStrings.get( "Common.Light_Species" ) );
            lightSpeciesRB.setForeground( Color.red );
            final ButtonGroup speciesGroup = new ButtonGroup();
            speciesGroup.add( heavySpeciesRB );
            speciesGroup.add( lightSpeciesRB );

            Insets insets = new Insets( 4,4,0,0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                             insets, 0, 0 );
            add( heavySpeciesRB, gbc );
//            heavySpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
//            lightSpeciesRB.setPreferredSize( new Dimension( 110, 15 ) );
            gbc.gridy = 1;
            add( lightSpeciesRB, gbc );

            heavySpeciesRB.setSelected( true );
            heavySpeciesRB.addActionListener( new ActionListener() {
                public void actionPerformed
                        ( ActionEvent
                        event ) {
                    if( heavySpeciesRB.isSelected() ) {
                        gasSpecies = HeavySpecies.class;
                    }
                }
            } );

            lightSpeciesRB.addActionListener( new ActionListener() {
                public void actionPerformed
                        ( ActionEvent
                        event ) {
                    if( lightSpeciesRB.isSelected() ) {
                        gasSpecies = LightSpecies.class;
                    }
                }
            } );

        }
    }
}
