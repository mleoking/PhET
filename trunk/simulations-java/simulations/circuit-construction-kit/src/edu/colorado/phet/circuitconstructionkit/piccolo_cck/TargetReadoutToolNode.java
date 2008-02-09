package edu.colorado.phet.circuitconstructionkit.piccolo_cck;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolox.nodes.PComposite;

import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;

/**
 * Ported from TargetReadoutTool
 *
 * @author Sam Reid
 */
public class TargetReadoutToolNode extends PComposite {
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
    private ShadowHTMLNode textNode;
    private String[] text = new String[0];
    private PhetPPath rightCrossHairGraphic;

    public TargetReadoutToolNode() {
        background = new PhetPPath( new Color( 255, 255, 255, 128 ) );
//        background = new PhetPPath( new Color( 255, 255, 255) );

        roundRect = new PhetPPath( boundsStroke, Color.black );
        portHole = new PhetPPath( holeStroke, Color.black );
        rightCrossHairGraphic = new PhetPPath( crossHairStroke, Color.black );
        upCrossHairGraphic = new PhetPPath( crossHairStroke, Color.black );
        readoutShapeGraphic = new PhetPPath( Color.white, crossHairStroke, Color.black );
        textNode = new ShadowHTMLNode( "HELLO" );
        textNode.setShadowColor( Color.lightGray );
        textNode.setShadowOffset( 0.5, 0.5 );

        addChild( background );
        addChild( roundRect );
        addChild( portHole );
        addChild( rightCrossHairGraphic );
        addChild( upCrossHairGraphic );
        addChild( readoutShapeGraphic );
        addChild( textNode );

        update();
    }

    private void update() {
        // Compute the locations of things
        Point upperLeft = new Point( -crosshairRadius, -crosshairRadius );
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
        textNode.setHtml( text );
        textNode.setOffset( readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 - textNode.getFullBounds().getHeight() / 2 );
    }

    public void setText( String[] text ) {
        if( !Arrays.asList( text ).equals( Arrays.asList( this.text ) ) ) {
            this.text = text;
            update();
        }
    }

    public void setText( String text ) {
        setText( new String[]{text} );
    }

}
