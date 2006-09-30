package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.ShadowHTMLGraphic;

import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;

/**
 * Ported from TargetReadoutTool
 *
 * @author Sam Reid
 */
public class TargetReadoutToolNode extends PhetPNode {
    private RoundRectangle2D.Double bounds;
    private int crosshairRadius = 15;
    private int readoutWidth = 140;
    private int readoutHeight = 50;
    private int width = crosshairRadius * 2 + readoutWidth + 30;
    private int height = readoutHeight + 20;
    private Stroke crossHairStroke = new BasicStroke( 1f );
    private Stroke holeStroke = new BasicStroke( 2f );
    private int boundsStrokeWidth = 2;
    private Stroke boundsStroke = new BasicStroke( boundsStrokeWidth );
    private PhetPPath background;
    private PhetPPath roundRect;
    private PhetPPath portHole;
    private PhetPPath upCrossHairGraphic;
    private PhetPPath readoutShapeGraphic;
    private ShadowHTMLGraphic textGraphic;
    private String[] text = new String[0];
    private PhetPPath rightCrossHairGraphic;

    public TargetReadoutToolNode() {
        background = new PhetPPath( new Color( 255, 255, 255, 128 ) );
        addChild( background );
        roundRect = new PhetPPath( boundsStroke, Color.black );
        addChild( roundRect );
        portHole = new PhetPPath( holeStroke, Color.black );
        addChild( portHole );
        rightCrossHairGraphic = new PhetPPath( crossHairStroke, Color.black );
        addChild( rightCrossHairGraphic );
        upCrossHairGraphic = new PhetPPath( new Area(), crossHairStroke, Color.black );
        addChild( upCrossHairGraphic );
        readoutShapeGraphic = new PhetPPath( new Area(), Color.white, crossHairStroke, Color.black );
        addChild( readoutShapeGraphic );
        textGraphic = new ShadowHTMLGraphic( "HELLO" );
        textGraphic.setShadowColor( Color.lightGray );
        textGraphic.setShadowOffset( 0.5, 0.5 );
        addChild( textGraphic );
        changed();
    }

    public void changed() {
        // Compute the locations of things
        Point upperLeft = new Point( - crosshairRadius, - crosshairRadius );
        Point readoutLocation = new Point( upperLeft.x + crosshairRadius * 2 + 10,
                                           upperLeft.y + crosshairRadius - readoutHeight / 2 );
        bounds = new RoundRectangle2D.Double( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                                              width, height,
                                              5, 5 );

        // Draw the outline of the whole thing and fill the background
        Ellipse2D.Double hole = new Ellipse2D.Double( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
        Area a = new Area( bounds );
        a.subtract( new Area( hole ) );
        background.setPathTo( a );

        Shape rr = new RoundRectangle2D.Double( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                                                crosshairRadius * 2 + readoutWidth + 30,
                                                readoutHeight + 20,
                                                5, 5 );
        roundRect.setPathTo( rr );
        Ellipse2D.Double oval = new Ellipse2D.Double( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
        portHole.setPathTo( oval );

        Line2D.Double line = new Line2D.Double( upperLeft.x + crosshairRadius, upperLeft.y, upperLeft.x + crosshairRadius, upperLeft.y + crosshairRadius * 2 );
        Line2D.Double line2 = new Line2D.Double( upperLeft.x, upperLeft.y + crosshairRadius, upperLeft.x + crosshairRadius * 2, upperLeft.y + crosshairRadius );
        upCrossHairGraphic.setPathTo( line );
        rightCrossHairGraphic.setPathTo( line2 );

        readoutShapeGraphic.setPathTo( new Rectangle2D.Double( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight ) );
        textGraphic.setHtml( text );
        textGraphic.setOffset( readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 - textGraphic.getFullBounds().getHeight() / 2 );
    }

    public void setText( String[] text ) {
        if( !Arrays.asList( text ).equals( Arrays.asList( this.text ) ) ) {
            this.text = text;
            changed();
        }
    }

    public void setText( String text ) {
        setText( new String[]{text} );
    }

}
