/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 9:47:20 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class ProtractorHandleGraphic extends PhetPNode {
    private Tip tip;
    private Body body;
    private Base base;
    private int bodyWidth = 14;
    private int bodyHeight = 120;
    private int tipWidth = 16;
    private float tipHeight = 40;
    private int baseHeight = 10;
    private int baseWidth = bodyWidth + 4;

    public ProtractorHandleGraphic() {
        tip = new Tip();
        body = new Body();
        base = new Base();
        addChild( body );
        addChild( base );
        addChild( tip );
    }

    class Tip extends PNode {

        private PPath path;

        public Tip() {
            path = new PPath();
            path.moveTo( 0, 0 );
            path.lineTo( tipWidth / 2, tipHeight );
            path.lineTo( -tipWidth / 2, tipHeight );
            path.lineTo( 0, 0 );
            path.setPaint( new GradientPaint( 0, 0, Color.lightGray, tipWidth / 2, 0, Color.darkGray, true ) );
            addChild( path );
        }
    }

    class Body extends PNode {
        private PPath path;

        public Body() {
            path = new PPath();
            Rectangle2D.Double aShape = new Rectangle2D.Double( -bodyWidth / 2, tipHeight, bodyWidth, bodyHeight );
            path.setPathTo( aShape );
            path.setPaint( new GradientPaint( (float)aShape.getX(), (float)aShape.getY(), Color.gray, (float)aShape.getMaxX(), (float)aShape.getMaxY(), Color.blue, true ) );
            addChild( path );
        }
    }

    class Base extends PNode {
        private PPath path;

        public Base() {
            path = new PPath();
            Rectangle2D.Double shape = new Rectangle2D.Double( -baseWidth / 2, body.getFullBounds().getMaxY(), baseWidth, baseHeight );
            path.setPathTo( shape );
            path.setPaint( new GradientPaint( (float)shape.getX(), (float)shape.getY(), Color.blue, (float)shape.getMaxX(), (float)shape.getMaxY(), Color.white, true ) );
            path.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            path.setStrokePaint( new GradientPaint( (float)shape.getX(), (float)shape.getY(), Color.gray, (float)shape.getMaxX(), (float)shape.getMaxY(), Color.black, true ) );
            addChild( path );
        }

    }
}
