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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.Spring;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.factories.SimpleMoleculeGraphicFactory;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.*;

/**
 * SpringTestModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringTestModule2 extends Module {

    public SpringTestModule2() {
        super( "Spring Test2", new SwingClock( 40, 1 ), true );

        MRModel model = new MRModel( getClock() ) {
            protected void stepInTime( double dt ) {
//                double e0 = spring.getPotentialEnergy() + mA.getKineticEnergy();
                super.stepInTime( dt );
//                double e1 = spring.getPotentialEnergy() + mA.getKineticEnergy();
//                System.out.println( "e0 = " + e0 + "\te1 = " + e1 );
            }
        };

        // Make the box huge so we don't get any collisions with it
        model.getBox().setBounds( -2000, -2000, 4000, 4000 );
        PhetPCanvas simPanel = new PhetPCanvas();
        setSimulationPanel( simPanel );
        PNode canvas = new PNode();
        simPanel.addScreenChild( canvas );
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model,
                                                                          simPanel.getPhetRootNode() );
        model.addListener( megm );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( canvas ) );
        megm.addGraphicFactory( new SpringGraphicFactory( Spring.class, canvas ));

        {
            // Make a couple of molecules
            MoleculeA mA = new MoleculeA() {
                double prevX;

                public void stepInTime( double dt ) {
                    Vector2D v0 = new Vector2D.Double( getVelocity() );
                    super.stepInTime( dt );
                    Vector2D v1 = new Vector2D.Double( getVelocity() );
                    prevX = v1.getX();
                }

                public void setVelocity( Vector2D velocity ) {
                    super.setVelocity( velocity );
                }

                public void setVelocity( double vx, double vy ) {
                    super.setVelocity( vx, vy );
                }
            };
            mA.setPosition( 300, 200 );
            mA.setVelocity( 15, 0 );
            model.addModelElement( mA );


            MoleculeB mB = new MoleculeB();
            mB.setPosition( mA.getPosition().getX() + 200, mA.getPosition().getY() );
            model.addModelElement( mB );
            mB.setVelocity( -15, 0 );

            MoleculeC mC = new MoleculeC();
            mC.setPosition( mB.getPosition().getX() + mB.getRadius() + mC.getRadius(),
                            mA.getPosition().getY() );
            model.addModelElement( mC );
            mC.setVelocity( -15, 0 );

            MoleculeBC mBC = new MoleculeBC( new SimpleMolecule[]{mB, mC} );
            model.addModelElement( mBC );

            CompositeBody cb = new CompositeBody();
            cb.addBody( mA );
            cb.addBody( mBC );

            Point2D fixedPt = cb.getCM();
            Spring sA = new Spring( 3,
                                    fixedPt.distance( mA.getPosition() ),
                                    fixedPt,
                                    Math.PI );
            model.addModelElement( sA );
            sA.attachBody( mA );

            Spring sBC = new Spring( 3,
                                     fixedPt.distance( mBC.getPosition()),
                                     fixedPt,
                                     0 );
            model.addModelElement( sBC );
            sBC.attachBody( mBC );

            // Make the spring
//            Point2D fixedPt = new Point2D.Double( 500, 200 );
//            double restingLength = 100;
//            Spring spring = new Spring( 1, restingLength, fixedPt, Math.PI );
//            model.addModelElement( spring );
//            SpringTestModule2.SpringGraphic springGraphic = new SpringTestModule2.SpringGraphic( spring );
//            canvas.addChild( springGraphic );
//
//            // Make the molecule and attach it to the spring
//            spring.attachBody( mA );
//            PPath refLine = new PPath( new Line2D.Double( spring.getFixedEnd().getX() - spring.getRestingLength(),
//                                                          0,
//                                                          spring.getFixedEnd().getX() - spring.getRestingLength(),
//                                                          500 ) );
//            refLine.setPaint( Color.blue );
//            canvas.addChild( refLine );
//            PPath refLine2 = new PPath( new Line2D.Double( spring.getFixedEnd().getX() + spring.getRestingLength(),
//                                                           0,
//                                                           spring.getFixedEnd().getX() + spring.getRestingLength(),
//                                                           500 ) );
//            refLine.setPaint( Color.blue );
//            canvas.addChild( refLine2 );
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
            line.setLine( spring.getExtent() );
            pNode.setPathTo( line );
        }
    }


    class SpringGraphicFactory extends ModelElementGraphicManager.GraphicFactory {

        protected SpringGraphicFactory( Class modelElementClass, PNode layer ) {
            super( modelElementClass, layer );
        }

        public PNode createGraphic( ModelElement modelElement ) {
            return new SpringGraphic( (Spring)modelElement );
        }
    }
}
