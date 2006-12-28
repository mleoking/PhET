package edu.colorado.phet.rotation.view;

import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:44:08 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class PlatformNode extends PNode {
    private double angle;
    RingNode[] ringNode = {};
    private double ringRadius;

    public PlatformNode() {
        ringRadius = 2.0;
        addRingNode( 2.0, Color.green );
        addRingNode( 1.5, Color.yellow );
        addRingNode( 1.0, Color.magenta );
        addRingNode( 0.5, Color.white );
        addRingNode( 0.01, Color.white );

        PhetPPath verticalCrossHair = new PhetPPath( new Line2D.Double( ringRadius, 0, ringRadius, ringRadius * 2 ), new BasicStroke( 2.0f / 100.0f ), Color.black );
        addChild( verticalCrossHair );

        PhetPPath horizontalCrossHair = new PhetPPath( new Line2D.Double( 0, ringRadius, ringRadius * 2, ringRadius ), new BasicStroke( 2.0f / 100.0f ), Color.black );
        addChild( horizontalCrossHair );

        scale( 100 );
    }

    private void addRingNode( double radius, Color color ) {
        RingNode ringNode = new RingNode( ringRadius, ringRadius, radius, color );
        addChild( ringNode );
    }

    public void setAngle( double angle ) {
        this.angle = angle;
        setRotation( 0 );
        setOffset( 0, 0 );
        rotateAboutPoint( angle, ringRadius, ringRadius );
    }

    static class RingNode extends PNode {
        public RingNode( double x, double y, double radius, Color color ) {
//            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 1.0f / 100.0f ), Color.black );
//            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 2.0f / 100.0f ,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,1.0f,new float[]{10.0f/100.0f,10.0f/100.0f},0.0f), Color.black );
            PhetPPath path = new PhetPPath( new Ellipse2D.Double( x - radius, y - radius, radius * 2, radius * 2 ), color, new BasicStroke( 2.0f / 100.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{30.0f / 100.0f, 30.0f / 100.0f}, 0.0f ), Color.black );
            addChild( path );
        }
    }
}
