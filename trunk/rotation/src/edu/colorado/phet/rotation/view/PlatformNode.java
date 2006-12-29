package edu.colorado.phet.rotation.view;

import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:44:08 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class PlatformNode extends PNode {
    RingNode[] ringNode = {};
    private double ringRadius;
    private PNode contentNode;
    private double scale = 1.0 / 100;
    private double angle = 0.0;

    public PlatformNode() {
        ringRadius = 2.0;
        contentNode = new PNode();
        addRingNode( 2.0, Color.green );
        addRingNode( 1.5, Color.yellow );
        addRingNode( 1.0, Color.magenta );
        addRingNode( 0.5, Color.white );
        addRingNode( 0.01, Color.white );

        PhetPPath verticalCrossHair = new PhetPPath( new Line2D.Double( ringRadius, 0, ringRadius, ringRadius * 2 ), new BasicStroke( (float)( 2 * scale ) ), Color.black );
        contentNode.addChild( verticalCrossHair );

        PhetPPath horizontalCrossHair = new PhetPPath( new Line2D.Double( 0, ringRadius, ringRadius * 2, ringRadius ), new BasicStroke( (float)( 2 * scale ) ), Color.black );
        contentNode.addChild( horizontalCrossHair );

        double handleWidth = 10 * scale;
        double handleHeight = 10 * scale;
        PhetPPath handleNode = new PhetPPath( new Rectangle2D.Double( ringRadius * 2, ringRadius - handleHeight / 2, handleWidth, handleHeight ), Color.blue, new BasicStroke( (float)( 1 * scale ) ), Color.black );
        contentNode.addChild( handleNode );

        addChild( contentNode );

        contentNode.scale( 100 );
    }

    private void addRingNode( double radius, Color color ) {
        RingNode ringNode = new RingNode( ringRadius, ringRadius, radius, color );
        contentNode.addChild( ringNode );
    }

    public void setAngle( double angle ) {
        if( this.angle != angle ) {
            this.angle = angle;
            contentNode.setRotation( 0 );
            contentNode.setOffset( 0, 0 );
            contentNode.rotateAboutPoint( angle, ringRadius, ringRadius );
        }
    }

    class RingNode extends PNode {
        public RingNode( double x, double y, double radius, Color color ) {
            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( (float)( 1 * scale ) ), Color.black );
//            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 2.0f / 100.0f ,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,1.0f,new float[]{10.0f/100.0f,10.0f/100.0f},0.0f), Color.black );
//            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 2.0f / 100.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{30.0f / 100.0f, 30.0f / 100.0f}, 0.0f ), Color.black );
            addChild( path );
        }
    }
}
