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
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.ReactionSpring;
import edu.colorado.phet.molecularreactions.model.collision.ReleasingReactionSpring;
import edu.colorado.phet.molecularreactions.model.collision.Spring;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.factories.SimpleMoleculeGraphicFactory;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * SpringTestModule3
 * <p/>
 * Tests springs that conformto the energy profile of the reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringTestModule5 extends Module {
    private MoleculeA mA;
    private MoleculeB mB;
    private MoleculeC mC;

    public SpringTestModule5() {
        super( "Spring Test 5", new SwingClock( 40, 1 ), true );

        final MRModel model = new MRModel( getClock() ) {
            protected void stepInTime( double dt ) {
//                double e0 = mA.getKineticEnergy() + mB.getKineticEnergy() + mC.getKineticEnergy();
                super.stepInTime( dt );
//                double e1 = mA.getKineticEnergy() + mB.getKineticEnergy() + mC.getKineticEnergy();
//                System.out.println( "e0 = " + e0 + "\te1 = " + e1 );
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
        megm.addGraphicFactory( new SpringTestModule5.SpringGraphicFactory( canvas ) );

        // Make model objects
        MoleculeA mA = new MoleculeA();
        Point2D.Double fixedEnd = new Point2D.Double( 300, 250 );
        Spring spring = new Spring( 1, 30, fixedEnd, 0);

        mA.setPosition( fixedEnd.getX() - mA.getRadius(), fixedEnd.getY() );
        spring.attachBodyAtSpringLength( mA, 0 );

        MoleculeB mB = new MoleculeB( );
        mB.setPosition( fixedEnd.getX() + mB.getRadius(), fixedEnd.getY() );

        ReactionSpring rSpring = new ReleasingReactionSpring( 500, 50, 100, new SimpleMolecule[]{ mA, mB } );

        model.addModelElement( mA );
        model.addModelElement( mB );
        model.addModelElement( rSpring );
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
            ReactionSpring cs = (ReactionSpring)modelElement;
            SpringTestModule5.SpringGraphic child0 = new SpringTestModule5.SpringGraphic( cs.getComponentSprings()[0] );
            pNode.addChild( child0 );
            SpringTestModule5.SpringGraphic child1 = new SpringTestModule5.SpringGraphic( cs.getComponentSprings()[1] );
            pNode.addChild( child1 );
            return pNode;
        }
    }
}
