package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Sep 15, 2006
 * Time: 9:40:58 PM
 * Copyright (c) Sep 15, 2006 by Sam Reid
 */

public class JunctionNode extends PhetPNode {
    private double strokeWidthModelCoords = CCKModel.JUNCTION_GRAPHIC_STROKE_WIDTH;
    private Stroke shapeStroke = new BasicStroke( 2 );
    private Stroke highlightStroke = new BasicStroke( 4 );
    private CCKModel cckModel;
    private Junction junction;
    private PPath shapePNode;
    private PPath highlightPNode;

    public JunctionNode( CCKModel cckModel, final Junction junction ) {
        this.cckModel = cckModel;
        this.junction = junction;
        shapePNode = new PPath();
        shapePNode.setStroke( shapeStroke );
        highlightPNode = new PPath();
        highlightPNode.setStroke( new BasicStroke( (float)( 3 / 80.0 ) ) );
        highlightPNode.setStrokePaint( Color.yellow );

        addChild( shapePNode );
        addChild( highlightPNode );

        junction.addObserver( new SimpleObserver() {
            public void update() {
                JunctionNode.this.update();
            }
        } );
        shapePNode.setStrokePaint( Color.red );
        shapePNode.setPaint( new Color( 0, 0, 0, 0 ) );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension dim = event.getDeltaRelativeTo( JunctionNode.this );
                junction.translate( dim.width, dim.height );
            }

            public void mousePressed( PInputEvent event ) {
                junction.setSelected( true );
            }
        } );

        addInputEventListener( new CursorHandler() );
        update();
    }

    private Stroke createStroke( double strokeWidth ) {
        float scale = (float)80.0;
        float[] dash = new float[]{3 / scale, 6 / scale};
        return (Stroke)new BasicStroke( (float)strokeWidth, BasicStroke.CAP_SQUARE, BasicStroke.CAP_BUTT, 3, dash, 0 );
    }

    private void update() {
        shapePNode.setPathTo( createCircle( CCKModel.JUNCTION_RADIUS * 1.1 ) );
        shapePNode.setStroke( createStroke( strokeWidthModelCoords * 2 ) );

        highlightPNode.setPathTo( createCircle( CCKModel.JUNCTION_RADIUS * 1.6 ) );
        highlightPNode.setStroke( new BasicStroke( (float)( 3.0 / 80.0 ) ) );
        highlightPNode.setVisible( junction.isSelected() );
    }

    private Ellipse2D createCircle( double radius ) {
        Ellipse2D.Double circle = new Ellipse2D.Double();
        circle.setFrameFromCenter( junction.getX(), junction.getY(), junction.getX() + radius, junction.getY() + radius );
        return circle;
    }
}
