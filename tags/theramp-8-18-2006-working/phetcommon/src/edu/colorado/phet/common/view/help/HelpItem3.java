/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 6, 2005
 * Time: 6:32:33 PM
 * Copyright (c) Mar 6, 2005 by Sam Reid
 */

public class HelpItem3 extends CompositePhetGraphic implements HelpTargetListener {
    private ShadowHTMLGraphic shadowHTMLGraphic;
    private PhetShapeGraphic textBackground;
    private HelpTarget helpTarget;
    private int arrowDX;
    private int arrowDY;
    private String text;
    private Color arrowColor = new Color( 200, 50, 75, 200 );
    private Color arrowBorderColor = new Color( 40, 10, 40, 210 );
    private Color textColor = new Color( 50, 75, 240 );
    private Color shadowColor = new Color( 0, 0, 30, 128 );
    private Color backgroundColor = new Color( 220, 200, 140, 225 );
    private Color borderColor = new Color( 110, 100, 70, 210 );
    private int arrowHeadSize = 10;
    private int tailWidth = 5;
    private Stroke textBorderStroke = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private Stroke arrowBorderStroke = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private PhetShapeGraphic arrowGraphic;
    private boolean helpEnabled = false;
    private int attachX;
    private int attachY;

    public HelpItem3( Component component, PhetGraphic target, int arrowDX, int arrowDY, String text ) {
        this( component, toTarget( target, arrowDX, arrowDY ), arrowDX, arrowDY, text );
    }

    private static HelpTarget toTarget( PhetGraphic target, int arrowDX, int arrowDY ) {
        if( arrowDX < 0 ) {
            return new PhetGraphicTarget.Right( target );
        }
        else if( arrowDX > 0 ) {
            return new PhetGraphicTarget.Left( target );
        }
        else if( arrowDY < 0 ) {
            return new PhetGraphicTarget.Bottom( target );
        }
        else {
            return new PhetGraphicTarget.Top( target );
        }
    }

    public HelpItem3( Component component, HelpTarget helpTarget, int arrowDX, int arrowDY, String text ) {
        super( component );
        this.helpTarget = helpTarget;
        this.arrowDX = arrowDX;
        this.arrowDY = arrowDY;
        this.text = text;
        Font font = new Font( "Lucida Sans", Font.PLAIN, 18 );

        shadowHTMLGraphic = new ShadowHTMLGraphic( component, text, font, textColor, 1, 1, shadowColor );

        Rectangle bounds = shadowHTMLGraphic.getBounds();
        Rectangle textBounds = RectangleUtils.expand( bounds, 2, 2 );

        textBackground = new PhetShapeGraphic( component, textBounds, backgroundColor, textBorderStroke, borderColor );

        addGraphic( textBackground );
        addGraphic( shadowHTMLGraphic );

        arrowGraphic = createArrow( arrowDX, arrowDY );
        attachArrow();
        addGraphic( arrowGraphic, -1 );

        helpTarget.addListener( this );
        setIgnoreMouse( true );
        targetLocationChanged();
    }

    private void attachArrow() {
        int attachX = 0;
        int attachY = 0;
        if( arrowDX == 0 ) {
            attachX = textBackground.getWidth() / 2;
        }
        else if( arrowDX > 0 ) {
            attachX = textBackground.getWidth();
        }
        if( arrowDY == 0 ) {
            attachY = textBackground.getHeight() / 2;
        }
        else if( arrowDY > 0 ) {
            attachY = textBackground.getHeight();
        }
        this.attachX = attachX;
        this.attachY = attachY;
//        System.out.println( "attachX = " + attachX );
//        System.out.println( "attachY = " + attachY );
        arrowGraphic.setLocation( attachX, attachY );
    }

    private PhetShapeGraphic createArrow( double dx, double dy ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( dx, dy ), arrowHeadSize, arrowHeadSize, tailWidth );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), arrowColor, arrowBorderStroke, arrowBorderColor );
        return arrowGraphic;
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public void setHelpEnabled( boolean helpEnabled ) {
        this.helpEnabled = helpEnabled;
        targetVisibilityChanged();
    }

    public void targetVisibilityChanged() {
        setVisible( helpTarget.isVisible() && isHelpEnabled() );
    }

    public void targetLocationChanged() {
        Point loc = new Point( helpTarget.getLocation() );
        loc.x -= arrowDX + attachX;
        loc.y -= arrowDY + attachY;
        setLocation( loc );
    }
}
