// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.modules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.model.CompositeBody;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.model.MoleculeA;
import edu.colorado.phet.reactionsandrates.model.MoleculeB;
import edu.colorado.phet.reactionsandrates.model.MoleculeBC;
import edu.colorado.phet.reactionsandrates.model.MoleculeC;
import edu.colorado.phet.reactionsandrates.model.SimpleMolecule;
import edu.colorado.phet.reactionsandrates.model.collision.ReactionSpring;
import edu.colorado.phet.reactionsandrates.model.collision.Spring;
import edu.colorado.phet.reactionsandrates.util.ModelElementGraphicManager;
import edu.colorado.phet.reactionsandrates.view.factories.SimpleMoleculeGraphicFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * SpringTestModule3
 * <p/>
 * Tests springs that conformto the energy profile of the reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringTestModule3 extends Module {
    private MoleculeA mA;
    private MoleculeB mB;
    private MoleculeC mC;

    public SpringTestModule3() {
        super( "Spring Test 3", new SwingClock( 40, 1 ), true );

        final MRModel model = new MRModel( getClock() ) {
            protected void stepInTime( double dt ) {
                double e0 = mA.getKineticEnergy() + mB.getKineticEnergy() + mC.getKineticEnergy();
                super.stepInTime( dt );
                double e1 = mA.getKineticEnergy() + mB.getKineticEnergy() + mC.getKineticEnergy();
                System.out.println( "e0 = " + e0 + "\te1 = " + e1 );
            }
        };

        // Make the box huge so we don't get any collisions with it
        model.getBox().setBounds( -2000, -2000, 4000, 4000 );
        PhetPCanvas simPanel = new PhetPCanvas();
        setSimulationPanel( simPanel );
        PNode canvas = new PNode();
        simPanel.addScreenChild( canvas );
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model, simPanel.getPhetRootNode() );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( model, canvas ) );
        megm.addGraphicFactory( new SpringTestModule3.SpringGraphicFactory( canvas ) );

        // Make springs that conform to the thresholds of the energy profile
        final EnergyProfile energyProfile = model.getReaction().getEnergyProfile();
        final double leftDPE = ( energyProfile.getPeakLevel() - energyProfile.getLeftLevel() ) * 10;
        final double rightDPE = energyProfile.getPeakLevel() - energyProfile.getRightLevel();

        {
            double v0 = 2;
            final double restingLength = energyProfile.getThresholdWidth() / 2;

            // Make a couple of molecules
            mA = new MoleculeA() {
                double prevX;

                public void stepInTime( double dt ) {
                    MutableVector2D v0 = new MutableVector2D( getVelocity() );
                    super.stepInTime( dt );
                    MutableVector2D v1 = new MutableVector2D( getVelocity() );
                    prevX = v1.getX();
                }

                public void setVelocity( MutableVector2D velocity ) {
                    super.setVelocity( velocity );
                }

                public void setVelocity( double vx, double vy ) {
                    super.setVelocity( vx, vy );
                }
            };
            mA.setPosition( 300, 200 );
            mA.setVelocity( v0, 0 );
            model.addModelElement( mA );


            mB = new MoleculeB();
//            mB.setPosition( mA.getPosition().getX() + restingLength, mA.getPosition().getY() );
            mB.setPosition( mA.getPosition().getX() + restingLength * 4, mA.getPosition().getY() );
//            mB.setPosition( mA.getPosition().getX() + 200, mA.getPosition().getY() );
            model.addModelElement( mB );
            mB.setVelocity( -v0, 0 );

            mC = new MoleculeC();
//            mC.setPosition( mB.getPosition().getX(),
//                            mA.getPosition().getY() + mB.getRadius() + mC.getRadius() );
            mC.setPosition( mB.getPosition().getX() + mB.getRadius() + mC.getRadius(),
                            mA.getPosition().getY() );
            model.addModelElement( mC );
            mC.setVelocity( -v0, 0 );

            final MoleculeBC mBC = new MoleculeBC( new SimpleMolecule[] { mB, mC } );
            model.addModelElement( mBC );

            CompositeBody cb = new CompositeBody();
            cb.addBody( mA );
            cb.addBody( mBC );

//            ReactionSpring rs = new ReactionSpring( 10, 100, new SimpleMolecule[] {mA, mB} );
//            model.addModelElement( rs );

            // A model element that will create a spring when the molecules are close enough to each other
//            model.addModelElement( new ModelElement() {
//                ReactionSpring cs = null;
//
//                public void stepInTime( double dt ) {
//                    double d = model.getReaction().getDistanceToCollision( mA.getFullMolecule(),
//                                                                         mBC.getFullMolecule() );
//                    if( cs == null && d <= restingLength ) {
//                        cs = new ReactionSpring( leftDPE,
//                                                 restingLength,
//                                                 restingLength,
//                                                 new SimpleMolecule[]{ mA, mB } );
//                        model.addModelElement( cs );
//                        System.out.println( "A" );
//                    }
//                    if( cs != null && d > restingLength ) {
//                        model.removeModelElement( cs );
//                        cs = null;
//                        System.out.println( "B" );
//                    }
//                }
//            } );
        }

    }


    private static class SpringGraphic extends PNode implements SimpleObserver {
        Line2D line = new Line2D.Double();
        private Spring spring;
        private PPath pNode;
        private PPath fixedEndNode;

        public SpringGraphic( Spring spring ) {
            this.spring = spring;
            spring.addObserver( this );

            pNode = new PPath( line );
            pNode.setStroke( new BasicStroke( 3 ) );
            pNode.setPaint( Color.green );
            pNode.setStrokePaint( Color.green );
            addChild( pNode );

            Point2D fixedEnd = spring.getExtent().getP1();
            fixedEndNode = new PPath( new Ellipse2D.Double( fixedEnd.getX() - 2,
                                                            fixedEnd.getY() - 2,
                                                            4, 4 ) );
            fixedEndNode.setPaint( Color.red );
            addChild( fixedEndNode );

            update();
        }

        public void update() {
            line.setLine( spring.getExtent().getP1(), spring.getExtent().getP2() );
//            System.out.println( "spring = " + spring.getExtent().getP1() + "\t"+ spring.getExtent().getP2() );
            pNode.setPathTo( line );
        }
    }


    class SpringGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected SpringGraphicFactory( PNode layer ) {
            super( ReactionSpring.class, layer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            PNode pNode = new PNode();
            ReactionSpring cs = (ReactionSpring) modelElement;
            SpringGraphic child0 = new SpringGraphic( cs.getComponentSprings()[0] );
            pNode.addChild( child0 );
            SpringGraphic child1 = new SpringGraphic( cs.getComponentSprings()[1] );
            pNode.addChild( child1 );
            return pNode;
        }
    }
}
