/**
 * Class: TargetReadoutTool
 * Package: edu.colorado.phet.bernoulli.meter
 * Author: Another Guy
 * Date: Sep 26, 2003
 */
package edu.colorado.phet.cck3.phetgraphics_cck;

import edu.colorado.phet.common_cck.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;

public class TargetReadoutTool extends CCKCompositePhetGraphic {
    private RoundRectangle2D.Double bounds;
    private int crosshairRadius = 15;
    private int readoutWidth = 140;
    private int readoutHeight = 50;
    private Point location = new Point();
    private int width = crosshairRadius * 2 + readoutWidth + 30;
    private int height = readoutHeight + 20;
    private Font font = new Font( "dialog", Font.BOLD, 14 );
    private Stroke crossHairStroke = new BasicStroke( 1f );
    private Stroke holeStroke = new BasicStroke( 2f );
    private int boundsStrokeWidth = 2;
    private Stroke boundsStroke = new BasicStroke( boundsStrokeWidth );
    private PhetShapeGraphic background;
    private PhetShapeGraphic roundRect;
    private PhetShapeGraphic portHole;
    private PhetShapeGraphic upCrossHairGraphic;
    private PhetShapeGraphic readoutShapeGraphic;
    private PhetMultiLineTextGraphic textGraphic;
    private String[] text = new String[0];
    private PhetShapeGraphic rightCrossHairGraphic;

    public TargetReadoutTool( Component component ) {
        super( component );
        background = new PhetShapeGraphic( component, new Area(), new Color( 255, 255, 255, 128 ) );
        addGraphic( background );
        roundRect = new PhetShapeGraphic( component, new Area(), boundsStroke, Color.black );
        addGraphic( roundRect );
        portHole = new PhetShapeGraphic( component, new Area(), holeStroke, Color.black );
        addGraphic( portHole );
        rightCrossHairGraphic = new PhetShapeGraphic( component, new Area(), crossHairStroke, Color.black );
        addGraphic( rightCrossHairGraphic );
        upCrossHairGraphic = new PhetShapeGraphic( component, new Area(), crossHairStroke, Color.black );
        addGraphic( upCrossHairGraphic );
        readoutShapeGraphic = new PhetShapeGraphic( component, new Area(), Color.white, crossHairStroke, Color.black );
        addGraphic( readoutShapeGraphic );
        textGraphic = new PhetMultiLineTextGraphic( component, new String[]{"HELLO"}, font, 0, 0, Color.black );
        addGraphic( textGraphic );
        changed();
        setVisible( true );
    }

    public void changed() {
        // Compute the locations of things
        location.setLocation( (int)location.getX(), (int)location.getY() );
        Point upperLeft = new Point( location.x - crosshairRadius, location.y - crosshairRadius );
        Point readoutLocation = new Point( upperLeft.x + crosshairRadius * 2 + 10,
                                           upperLeft.y + crosshairRadius - readoutHeight / 2 );
        bounds = new RoundRectangle2D.Double( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                                              width, height,
                                              5, 5 );

        // Draw the outline of the whole thing and fill the background
        Ellipse2D.Double hole = new Ellipse2D.Double( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
        Area a = new Area( bounds );
        a.subtract( new Area( hole ) );
        background.setShape( a );

        Shape rr = new RoundRectangle2D.Double( upperLeft.x - 10, upperLeft.y + crosshairRadius - readoutHeight / 2 - 10,
                                                crosshairRadius * 2 + readoutWidth + 30,
                                                readoutHeight + 20,
                                                5, 5 );
        roundRect.setShape( rr );
        Ellipse2D.Double oval = new Ellipse2D.Double( upperLeft.x, upperLeft.y, crosshairRadius * 2, crosshairRadius * 2 );
        portHole.setShape( oval );

        Line2D.Double line = new Line2D.Double( upperLeft.x + crosshairRadius, upperLeft.y, upperLeft.x + crosshairRadius, upperLeft.y + crosshairRadius * 2 );
        Line2D.Double line2 = new Line2D.Double( upperLeft.x, upperLeft.y + crosshairRadius, upperLeft.x + crosshairRadius * 2, upperLeft.y + crosshairRadius );
        upCrossHairGraphic.setShape( line );
        rightCrossHairGraphic.setShape( line2 );

        readoutShapeGraphic.setShape( new Rectangle2D.Double( readoutLocation.x, readoutLocation.y, readoutWidth, readoutHeight ) );
        textGraphic.setText( text );
        textGraphic.setPosition( readoutLocation.x + 5, readoutLocation.y + readoutHeight / 2 );
        super.setBoundsDirty();
    }

    public void setLocation( int x, int y ) {
        if( this.location.x != x || this.location.y != y ) {
            this.location.setLocation( x, y );
            changed();
        }
    }

    public void translate( int dx, int dy ) {
        setLocation( location.x + dx, location.y + dy );
    }

    public Point getPoint() {
        return new Point( location );
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
