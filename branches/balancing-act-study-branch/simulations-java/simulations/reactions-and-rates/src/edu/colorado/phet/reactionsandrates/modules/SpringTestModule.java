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
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.model.MoleculeA;
import edu.colorado.phet.reactionsandrates.model.collision.Spring;
import edu.colorado.phet.reactionsandrates.util.ModelElementGraphicManager;
import edu.colorado.phet.reactionsandrates.view.factories.SimpleMoleculeGraphicFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * SpringTestModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringTestModule extends Module {

    public SpringTestModule() {
        super( "Spring Test", new SwingClock( 40, 1 ), true );

        MRModel model = new MRModel( getClock() ) {
            protected void stepInTime( double dt ) {
//                double e0 = spring.getPotentialEnergy() + mA.getKineticEnergy();
                super.stepInTime( dt );
//                double e1 = spring.getPotentialEnergy() + mA.getKineticEnergy();
//                System.out.println( "e0 = " + e0 + "\te1 = " + e1 );
            }
        };

        // Make the box huge so we don't get any collisions with it
        model.getBox().setBounds( 0, 0, 1000, 1000 );
        PhetPCanvas simPanel = new PhetPCanvas();
        setSimulationPanel( simPanel );
        PNode canvas = new PNode();
        simPanel.addScreenChild( canvas );
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model,
                                                                          simPanel.getPhetRootNode() );
        model.addListener( megm );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( model, canvas ) );

        {
            Point2D fixedPt = new Point2D.Double( 500, 200 );
            double restingLength = 100;

            // Make the molecule and attach it to the spring
            MoleculeA mA = new MoleculeA() {
                double prevX;

                public void stepInTime( double dt ) {
                    MutableVector2D v0 = new MutableVector2D( getVelocity() );
                    super.stepInTime( dt );
                    MutableVector2D v1 = new MutableVector2D( getVelocity() );
                    prevX = v1.getX();
                }
            };
            mA.setPosition( fixedPt.getX() - restingLength, 200 );
            mA.setVelocity( MRConfig.MAX_SPEED, 0 );
            model.addModelElement( mA );

            // Make the spring
            Spring spring = new Spring( mA.getKineticEnergy(), restingLength, restingLength, fixedPt, mA, mA.getVelocity() );
            model.addModelElement( spring );
            SpringGraphic springGraphic = new SpringGraphic( spring );
            canvas.addChild( springGraphic );
//            spring.attachBody( mA );
            PPath refLine = new PPath( new Line2D.Double( spring.getFixedEnd().getX() - spring.getRestingLength(),
                                                          0,
                                                          spring.getFixedEnd().getX() - spring.getRestingLength(),
                                                          500 ) );
            refLine.setPaint( Color.blue );
            canvas.addChild( refLine );
            PPath refLine2 = new PPath( new Line2D.Double( spring.getFixedEnd().getX() + spring.getRestingLength(),
                                                           0,
                                                           spring.getFixedEnd().getX() + spring.getRestingLength(),
                                                           500 ) );
            refLine.setPaint( Color.blue );
            canvas.addChild( refLine2 );
        }

        {
            // Make the spring
            Point2D fixedPt = new Point2D.Double( 500, 300 );
            double restingLength = 20;
            Spring spring = new Spring( .2, restingLength, fixedPt, Math.PI );
            model.addModelElement( spring );
            SpringGraphic springGraphic = new SpringGraphic( spring );
            canvas.addChild( springGraphic );

            // Make the molecule and attach it to the spring
            MoleculeA mA = new MoleculeA() {
                double prevX;

                public void stepInTime( double dt ) {
                    MutableVector2D v0 = new MutableVector2D( getVelocity() );
                    super.stepInTime( dt );
                    MutableVector2D v1 = new MutableVector2D( getVelocity() );
                    prevX = v1.getX();
                }
            };
            mA.setPosition( spring.getFreeEnd().getX() - 30, fixedPt.getY() );
            mA.setVelocity( -5, 0 );
            model.addModelElement( mA );
            spring.attachBody( mA );
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
}
