/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.common;

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

public class HelpItem2 extends CompositePhetGraphic {
    private ShadowHTMLGraphic shadowHTMLGraphic;
    private PhetShapeGraphic textBackground;
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

    public HelpItem2( Component component, String text ) {
        super( component );
        this.text = text;
        Font font = new Font( "Lucida Sans", Font.PLAIN, 18 );

        shadowHTMLGraphic = new ShadowHTMLGraphic( component, text, font, textColor, 1, 1, shadowColor );

        Rectangle bounds = shadowHTMLGraphic.getBounds();
        Rectangle textBounds = RectangleUtils.expand( bounds, 2, 2 );

        textBackground = new PhetShapeGraphic( component, textBounds, backgroundColor, textBorderStroke, borderColor );

        addGraphic( textBackground );
        addGraphic( shadowHTMLGraphic );
    }


    public void addArrowPointingLeft( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( -arrowLength, 0 ), arrowHeadSize, arrowHeadSize, tailWidth );

        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), arrowColor, arrowBorderStroke, arrowBorderColor );
        addGraphic( arrowGraphic );
        new RelativeLocationSetter.Left().layout( textBackground, arrowGraphic );
    }

    public void addArrowPointingUp( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( 0, -arrowLength ), arrowHeadSize, arrowHeadSize, tailWidth );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), arrowColor, arrowBorderStroke, arrowBorderColor );
        addGraphic( arrowGraphic );
        new RelativeLocationSetter.Top().layout( textBackground, arrowGraphic );
    }

    public void pointLeftAt( RelativeLocationSetter.Target target, int arrowLength ) {
        addArrowPointingLeft( arrowLength );
        RelativeLocationSetter.follow( target, new RelativeLocationSetter.MovablePhetGraphic( this ), new RelativeLocationSetter.Right( 1 ) );
    }

    public void pointLeftAt( PhetGraphic target, int arrowLength ) {
        pointLeftAt( new RelativeLocationSetter.PhetGraphicTarget( target ), arrowLength );
        addArrowPointingLeft( arrowLength );
        RelativeLocationSetter.follow( target, this, new RelativeLocationSetter.Right( 1 ) );
    }

    public void pointUpAt( PhetGraphic target, int arrowLength ) {
        addArrowPointingUp( arrowLength );
        RelativeLocationSetter.follow( target, this, new RelativeLocationSetter.Bottom( 1 ) );
    }

    public void pointDownAt( PhetGraphic blockGraphic, int arrowLength ) {
        addArrowPointingDown( arrowLength );
        RelativeLocationSetter.follow( blockGraphic, this, new RelativeLocationSetter.Top( 1 ) );
    }

    private void addArrowPointingDown( int arrowLength ) {
        Arrow arrow = new Arrow( new Point2D.Double(), new Point2D.Double( 0, arrowLength ), arrowHeadSize, arrowHeadSize, tailWidth );
        PhetShapeGraphic arrowGraphic = new PhetShapeGraphic( getComponent(), arrow.getShape(), arrowColor, arrowBorderStroke, arrowBorderColor );
        addGraphic( arrowGraphic );
        new RelativeLocationSetter.Bottom().layout( textBackground, arrowGraphic );
    }
}
