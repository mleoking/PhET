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
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.Spring;
import edu.colorado.phet.molecularreactions.model.collision.ReactionSpring;
import edu.colorado.phet.molecularreactions.model.collision.ReleasingReactionSpring;
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
 * SpringTestModule3
 * <p/>
 * Tests springs that conformto the energy profile of the reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpringTestModule6 extends Module {
    private MoleculeA mA;
    private MoleculeB mB;
    private MoleculeC mC;

    public SpringTestModule6() {
        super( "Spring Test 6", new SwingClock( 40, 1 ), true );

        final MRModel model = new MRModel( getClock() );
        setModel( model );

        // Make the box huge so we don't get any collisions with it
        model.getBox().setBounds( -2000, -2000, 4000, 4000 );
        PhetPCanvas simPanel = new PhetPCanvas();
        setSimulationPanel( simPanel );
        PNode canvas = new PNode();
        simPanel.addScreenChild( canvas );
        ModelElementGraphicManager megm = new ModelElementGraphicManager( model, simPanel.getPhetRootNode() );
        megm.addGraphicFactory( new SimpleMoleculeGraphicFactory( canvas ) );
        megm.addGraphicFactory( new SpringTestModule6.SpringGraphicFactory( canvas ) );

        // Make model objects
        MoleculeA mA = new MoleculeA();
        Point2D.Double fixedEnd = new Point2D.Double( 300, 250 );

        mA.setPosition( fixedEnd.getX() - 200 - mA.getRadius(), fixedEnd.getY() );

        MoleculeB mB = new MoleculeB( );
        MoleculeC mC = new MoleculeC( );
        mB.setPosition( fixedEnd.getX() + mB.getRadius(), fixedEnd.getY() );
        mC.setPosition( fixedEnd.getX() + mB.getRadius() * 2 + mC.getRadius(), fixedEnd.getY() );
        MoleculeBC mBC = new MoleculeBC( new SimpleMolecule[]{mB, mC} );

        model.addModelElement( mA );
        model.addModelElement( mB );
        model.addModelElement( mC );
        model.addModelElement( mBC );

        mA.setVelocity( 3, 0 );
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
            SpringTestModule6.SpringGraphic child0 = new SpringTestModule6.SpringGraphic( cs.getComponentSprings()[0] );
            pNode.addChild( child0 );
            SpringTestModule6.SpringGraphic child1 = new SpringTestModule6.SpringGraphic( cs.getComponentSprings()[1] );
            pNode.addChild( child1 );
            return pNode;
        }
    }
}
