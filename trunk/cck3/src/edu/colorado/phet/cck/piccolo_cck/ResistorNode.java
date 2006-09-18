package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Resistor;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public class ResistorNode extends PhetPNode {
    private PImage pImage;
    private CCKModel model;
    private Resistor resistor;
    private CircuitInteractionModel circuitInteractionModel;

    public ResistorNode( final CCKModel model, final Resistor resistor ) {
        this.model = model;
        this.resistor = resistor;
        this.circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );
        pImage = new PImage( CCKImageSuite.getInstance().getLifelikeSuite().getResistorImage() );
        addChild( pImage );
        update();
        resistor.addObserver( new SimpleObserver() {
            public void update() {
                ResistorNode.this.update();
            }
        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( ResistorNode.this.getParent() );
                Point2D dragPt = event.getPositionRelativeTo( ResistorNode.this.getParent() );
                circuitInteractionModel.translate( resistor, dragPt );
            }

            public void mousePressed( PInputEvent event ) {
                model.getCircuit().setSelection( resistor );
            }
        } );
    }

    private void update() {
        double resistorLength = resistor.getStartPoint().distance( resistor.getEndPoint() );
        double imageLength = pImage.getFullBounds().getWidth();
        double scale = resistorLength / imageLength;
        double angle = new Vector2D.Double( resistor.getStartPoint(), resistor.getEndPoint() ).getAngle();
        setTransform( new AffineTransform() );
        setScale( scale );
        setOffset( resistor.getStartPoint() );
        rotate( angle );
        translate( 0, -pImage.getFullBounds().getHeight() / 2 );
    }

}
