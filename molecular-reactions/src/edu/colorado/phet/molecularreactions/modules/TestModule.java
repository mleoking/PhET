/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.awt.*;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestModule extends MRModule {

    private Dimension size = new Dimension( 600, 500 );

    public TestModule() {

        // Test
//        testL( (MRModel)getModel() );
        testK( (MRModel)getModel() );
//        testJ( (MRModel)getModel() );
//        testI( (MRModel)getModel() );
//        testH( (MRModel)getModel() );
//        testG( (MRModel)getModel() );
//        testF( (MRModel)getModel() );
//        testE( (MRModel)getModel() );
//        testD( (MRModel)getModel() );
//        testC( (MRModel)getModel() );
//        testB( (MRModel)getModel() );
//        testDefinedMolecules( (MRModel)getModel() );
//        testCompisiteMoleculeA( model );
    }

    /**
     * Multiple molecules
     *
     * @param model
     */
    void testL( MRModel model ) {
        {
            model.setReaction( new A_AB_BC_C_Reaction( model ) );
            model.getEnergyProfile().setPeakLevel( 50 );
            {
                SimpleMolecule m1 = new MoleculeB();
                m1.setPosition( 180, 60 );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeA();
                m1a.setPosition( m1.getPosition().getX() + m1.getRadius() + m1a.getRadius(), 60 );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                              new Bond[]{new Bond( m1, m1a )} );
                cm.setOmega( 0 );
                cm.setVelocity( 0, 0 );
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 1.5, 0 );
                model.addModelElement( m2 );
            }

            {
                Molecule smA = new MoleculeA();
                smA.setPosition( 100, 150 );
                model.addModelElement( smA );
            }
        }
    }

    /**
     * Simple A_AB_BC_C reaction going back and forth
     *
     * @param model
     */
    void testK( MRModel model ) {
        {
            model.setReaction( new A_AB_BC_C_Reaction(model) );
            model.getEnergyProfile().setPeakLevel( 50 );
            {
                SimpleMolecule m1 = new MoleculeB();
                m1.setPosition( 180, 60 );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeA();
                m1a.setPosition( m1.getPosition().getX() + m1.getRadius() + m1a.getRadius(), 60 );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new MoleculeAB( new SimpleMolecule[]{m1, m1a},
                                                              new Bond[]{new Bond( m1, m1a )} );
                cm.setOmega( 0 );
                cm.setVelocity( 0, 0 );
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 230, m1.getPosition().getY() );
                m2.setVelocity( 3, 0 );
                model.addModelElement( m2 );
            }
        }
    }

    /**
     * For testing A_AB_BC_C reactions, and Reaction class in general
     *
     * @param model
     */
    void testJ( MRModel model ) {
        {
            model.setReaction( new A_AB_BC_C_Reaction(model) );
            model.getEnergyProfile().setPeakLevel( 50 );
            {
                SimpleMolecule m1 = new MoleculeA();
                m1.setPosition( 180, 60 );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeB();
                m1a.setPosition( 180 + m1a.getRadius() * 2 + 5, 60 );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                              new Bond[]{new Bond( m1, m1a )} );
                cm.setOmega( 0.1 );
                cm.setVelocity( 0, 4 );
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 1.5, 0 );
                model.addModelElement( m2 );
            }
            {
                SimpleMolecule m1 = new MoleculeB();
                m1.setPosition( 180, 120 );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeA();
                m1a.setPosition( 180 + m1a.getRadius() * 2 + 5, m1.getPosition().getY() );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                              new Bond[]{new Bond( m1, m1a )} );
                cm.setOmega( 0.1 );
                cm.setVelocity( 0, -0.4 );
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 1.5, 0 );
                model.addModelElement( m2 );
            }

        }
    }

    /**
     * For testing A_AB_BC_C reactions, and Reaction class in general
     *
     * @param model
     */
    void testI( MRModel model ) {
        {
            model.setReaction( new A_AB_BC_C_Reaction(model ) );
            model.getEnergyProfile().setPeakLevel( 50 );
            {
                SimpleMolecule m1 = new MoleculeA();
                m1.setPosition( 180, 60 );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeB();
                m1a.setPosition( 180 + m1a.getRadius() * 2 + 5, 60 );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                              new Bond[]{new Bond( m1, m1a )} );
            cm.setOmega( 0.1 );
            cm.setVelocity( 0, -0.4);
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 5, 0 );
                model.addModelElement( m2 );
            }
            {
                SimpleMolecule m1 = new MoleculeB();
                m1.setPosition( 180, 120 );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeA();
                m1a.setPosition( 180 + m1a.getRadius() * 2 + 5, m1.getPosition().getY() );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                              new Bond[]{new Bond( m1, m1a )} );
            cm.setOmega( 0.1 );
            cm.setVelocity( 0, -0.4);
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 1.5, 0 );
                model.addModelElement( m2 );
            }

        }
    }

    /**
     * For testing provisional bonds
     *
     * @param model
     */
    void testH( MRModel model ) {
        {
            model.getEnergyProfile().setPeakLevel( 50 );
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 180, 60 );
            m1.setVelocity( 0, 0 );
            model.addModelElement( m1 );
            SimpleMolecule m1a = new MoleculeB();
            m1a.setPosition( 180 + m1a.getRadius() * 2 + 15, 60 );
            m1a.setVelocity( 0, 0 );
            model.addModelElement( m1a );
        }
    }

    void testG( MRModel model ) {
        {
            model.getEnergyProfile().setPeakLevel( 50 );
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 180, 60 );
            m1.setVelocity( 0, 0 );
            model.addModelElement( m1 );
            SimpleMolecule m1a = new MoleculeB();
            m1a.setPosition( 180 + m1a.getRadius() * 2, 60 );
            m1a.setVelocity( 0, 0 );
            model.addModelElement( m1a );

            CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                          new Bond[]{new Bond( m1, m1a )} );
            cm.setOmega( 0.1 );
            cm.setVelocity( 0, -0.4 );

            model.addModelElement( cm );

//            SimpleMolecule m2 = new MoleculeA();
//            m2.setPosition( 180, 150 );
//            m2.setVelocity( 0, -2 );
//            model.addModelElement( m2 );
//            m2.setSelectionStatus( Selectable.SELECTED );
        }
    }

    void testF( MRModel model ) {
        {
            model.getEnergyProfile().setPeakLevel( 50 );
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 180, 60 );
            m1.setVelocity( 0, 0 );
            model.addModelElement( m1 );
            SimpleMolecule m1a = new MoleculeB();
            m1a.setPosition( 180 + m1a.getRadius() * 2, 60 );
            m1a.setVelocity( 0, 0 );
            model.addModelElement( m1a );

            CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                          new Bond[]{new Bond( m1, m1a )} );
            cm.setOmega( -0.1 );

            model.addModelElement( cm );

            SimpleMolecule m2 = new MoleculeA();
            m2.setPosition( 180, 150 );
            m2.setVelocity( 0, -2 );
            model.addModelElement( m2 );
            m2.setSelectionStatus( Selectable.SELECTED );
        }
    }

    void testE( MRModel model ) {
        {
            model.getEnergyProfile().setPeakLevel( 50 );
            SimpleMolecule m1 = new MoleculeB();
            m1.setPosition( 180, 60 );
            m1.setVelocity( 0, 0 );
            model.addModelElement( m1 );
            SimpleMolecule m1a = new MoleculeB();
            m1a.setPosition( 180 + m1a.getRadius() * 2, 60 );
            m1a.setVelocity( 0, 0 );
            model.addModelElement( m1a );

            CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                          new Bond[]{new Bond( m1, m1a )} );

            model.addModelElement( cm );

            SimpleMolecule m2 = new MoleculeA();
            m2.setPosition( 80, 60 );
            m2.setVelocity( 2, 0 );
            model.addModelElement( m2 );
            m2.setSelectionStatus( Selectable.SELECTED );
        }
    }

    /**
     * Puts in a several molecules with components of different types and
     * simple molecules
     *
     * @param model
     */
    private void testD( MRModel model ) {
        {
            model.getEnergyProfile().setPeakLevel( 50 );
            SimpleMolecule m1 = new MoleculeB();
            m1.setPosition( 180, 60 );
            m1.setVelocity( 0, 0 );
            model.addModelElement( m1 );
            SimpleMolecule m1a = new MoleculeB();
            m1a.setPosition( 180 + m1a.getRadius() * 2, 60 );
            m1a.setVelocity( 0, 0 );
            model.addModelElement( m1a );

            CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                          new Bond[]{new Bond( m1, m1a )} );

            model.addModelElement( cm );

            SimpleMolecule m2 = new MoleculeA();
            m2.setPosition( 80, 60 );
            m2.setVelocity( 2, 0 );
            model.addModelElement( m2 );
            m2.setSelectionStatus( Selectable.SELECTED );
        }
        {
            model.getEnergyProfile().setPeakLevel( 300 );
            SimpleMolecule m1 = new MoleculeB();
            m1.setPosition( 60, 110 );
            m1.setVelocity( 5, 1 );
            model.addModelElement( m1 );
            SimpleMolecule m1a = new MoleculeB();
            m1a.setPosition( 60, m1a.getRadius() * 2 + 110 );
            m1a.setVelocity( 0, 0 );
            model.addModelElement( m1a );

            CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                          new Bond[]{new Bond( m1, m1a )} );
            model.addModelElement( cm );

            SimpleMolecule m2 = new MoleculeA();
            m2.setPosition( 185, 120 );
            m2.setVelocity( -4, 2 );
            model.addModelElement( m2 );
            m2.setSelectionStatus( Selectable.SELECTED );
        }
        {
//            model.getEnergyProfile().setPeakLevel( 300 );
//            SimpleMolecule m1 = new MoleculeA();
//            m1.setPosition( 110, 60 );
//            m1.setVelocity( 0, 0 );
//            model.addModelElement( m1 );
//            SimpleMolecule m1a = new MoleculeA();
//            m1a.setPosition( 110 + m1a.getRadius() * 2, 60 );
//            m1a.setVelocity( 0, 0 );
//            model.addModelElement( m1a );
//
//            CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
//                                                          new Bond[]{new Bond( m1, m1a )} );
//
//            model.addModelElement( cm );
//
//            SimpleMolecule m2 = new MoleculeB();
//            m2.setPosition( 115, 150 );
//            m2.setVelocity( 0, -3 );
//            model.addModelElement( m2 );
//            m2.setSelectionStatus( Selectable.SELECTED );
        }
    }

    /**
     * Puts in a compound molecule with components of different types and a
     * simple molecule of the other type
     *
     * @param model
     */
    private void testC( MRModel model ) {

        model.getEnergyProfile().setPeakLevel( 300 );
        SimpleMolecule m1 = new MoleculeB();
        m1.setPosition( 110, 60 );
        m1.setVelocity( 0, 0 );
        model.addModelElement( m1 );
        SimpleMolecule m1a = new MoleculeA();
        m1a.setPosition( 110 + m1a.getRadius() * 2, 60 );
        m1a.setVelocity( 0, 0 );
        model.addModelElement( m1a );

        CompositeMolecule cm = new CompositeMolecule( new SimpleMolecule[]{m1, m1a},
                                                      new Bond[]{new Bond( m1, m1a )} );
        model.addModelElement( cm );

        SimpleMolecule m2 = new MoleculeB();
        m2.setPosition( 115, 150 );
        m2.setVelocity( 0, -3 );
        model.addModelElement( m2 );
        m2.setSelectionStatus( Selectable.SELECTED );
    }

    private void testB( MRModel model ) {

        model.getEnergyProfile().setPeakLevel( 0 );
        SimpleMolecule m1 = new MoleculeA();
        m1.setPosition( 110, 60 );
        m1.setVelocity( 1, 0 );
        model.addModelElement( m1 );

        SimpleMolecule m2 = new MoleculeB();
        m2.setPosition( 180, 60 );
        m2.setVelocity( 0, 0 );
        model.addModelElement( m2 );
        m2.setSelectionStatus( Selectable.SELECTED );
    }

    private void testCompisiteMoleculeA( MRModel model ) {

        SimpleMolecule m1 = new MoleculeA();
        m1.setPosition( 140, 60 );
        m1.setVelocity( 3, 3 );
        model.addModelElement( m1 );

        SimpleMolecule m2 = new MoleculeB();
        m2.setPosition( 150, 60 );
        m2.setVelocity( 3, 3 );
        model.addModelElement( m2 );
        m2.setSelectionStatus( Selectable.SELECTED );

        CompositeMolecule compositeMolecule = new CompositeMolecule( new SimpleMolecule[]{m1, m2},
                                                                     new Bond[]{new Bond( m1, m2 )} );
        model.addModelElement( compositeMolecule );
    }

    private void testDefinedMolecules( MRModel model ) {
        {
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 120, 160 );
            m1.setVelocity( 5, 3 );
            model.addModelElement( m1 );
        }

        {
            SimpleMolecule m1 = new MoleculeA();
            m1.setPosition( 140, 60 );
            m1.setVelocity( 4, -3 );
            model.addModelElement( m1 );
        }

        {
            SimpleMolecule m1 = new MoleculeB();
            m1.setPosition( 80, 60 );
            m1.setVelocity( 2, -7 );
            model.addModelElement( m1 );
            m1.setSelectionStatus( Selectable.SELECTED );
        }
        {
            SimpleMolecule m1 = new MoleculeB();
            m1.setPosition( 20, 80 );
            m1.setVelocity( 4, -1 );
            model.addModelElement( m1 );
            m1.setSelectionStatus( Selectable.SELECTED );
        }
    }


    private void testSimpleToCompositeMolecules( MRModel model ) {

        // Set up two simple molecules to ram together
        SimpleMolecule sm1 = new SimpleMolecule( 7 );
        sm1.setMass( 49 );
        sm1.setPosition( 100, 110 );
        sm1.setVelocity( 5, 0 );
        model.addModelElement( sm1 );

        SimpleMolecule sm2 = new SimpleMolecule( 10 );
        sm2.setMass( 100 );
        sm2.setPosition( 200, 105 );
        sm2.setVelocity( -0, 0 );
        model.addModelElement( sm2 );

        SimpleMolecule rm3 = new SimpleMolecule( 5 );
        rm3.setMass( 25 );
        rm3.setPosition( 70, 100 );
//        rm3.setPosition( 200, 115 );
        model.addModelElement( rm3 );

    }

    private void testSimpleMoleculesA( MRModel model ) {

        SimpleMolecule rm = new SimpleMolecule( 7 );
        rm.setMass( 49 );
        rm.setPosition( 100, 105 );
        rm.setVelocity( 5, 0 );
        model.addModelElement( rm );

        SimpleMolecule rm2 = new SimpleMolecule( 10 );
        rm2.setMass( 100 );
        rm2.setPosition( 200, 100 );
        rm2.setVelocity( -5, 0 );
        model.addModelElement( rm2 );

        SimpleMolecule rm3 = new SimpleMolecule( 5 );
        rm3.setMass( 25 );
        rm3.setPosition( 200, 115 );
        model.addModelElement( rm3 );
    }


    private void testCompositeMoleculeA( MRModel model ) {

        SimpleMolecule rm2 = new SimpleMolecule( 10 );
        rm2.setMass( 100 );
        rm2.setPosition( 200, 100 );

        SimpleMolecule rm3 = new SimpleMolecule( 5 );
        rm3.setMass( 25 );
        rm3.setPosition( 200, 115 );

        CompositeMolecule cm1 = new CompositeMolecule( new SimpleMolecule[]{rm2, rm3},
                                                       new Bond[]{new Bond( rm2, rm3 )} );
        cm1.setVelocity( 1, 0 );
//        cm1.setVelocity( 0, 4);
//        cm1.setOmega( 0 );
        cm1.setOmega( -0.3 );
        model.addModelElement( cm1 );
    }
}
