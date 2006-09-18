package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public class ComponentNode extends BranchNode {
    private CCKModel model;
    private PImage pImage;
    private CircuitComponent resistor;
    private CircuitInteractionModel circuitInteractionModel;
    private PPath highlightNode;

    public ComponentNode( final CCKModel model, final CircuitComponent circuitComponent, BufferedImage image ) {
        this.model = model;
        this.resistor = circuitComponent;
        this.circuitInteractionModel = new CircuitInteractionModel( model.getCircuit() );

        highlightNode = new PPath();
        highlightNode.setStrokePaint( Color.yellow );
        highlightNode.setStroke( new BasicStroke( 3f ) );
        addChild( highlightNode );

        pImage = new PImage( image );
        addChild( pImage );

        update();
        circuitComponent.addObserver( new SimpleObserver() {
            public void update() {
                ComponentNode.this.update();
            }
        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                circuitInteractionModel.translate( circuitComponent, event.getPositionRelativeTo( ComponentNode.this.getParent() ) );
            }

            public void mouseReleased( PInputEvent event ) {
                circuitInteractionModel.dropBranch( circuitComponent );
            }

            public void mousePressed( PInputEvent event ) {
                model.getCircuit().setSelection( circuitComponent );
            }
        } );
    }

    private void update() {
        Rectangle2D aShape = new Rectangle2D.Double( 0, 0, pImage.getFullBounds().getWidth(), pImage.getFullBounds().getHeight() );
        aShape = RectangleUtils.expand( aShape, 2, 2 );
        highlightNode.setPathTo( aShape );
        highlightNode.setVisible( resistor.isSelected() );

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

    public Branch getBranch() {
        return resistor;
    }
}
