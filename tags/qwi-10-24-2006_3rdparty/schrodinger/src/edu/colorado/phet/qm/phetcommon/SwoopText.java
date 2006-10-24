/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 19, 2005
 * Time: 1:02:56 AM
 * Copyright (c) Sep 19, 2005 by Sam Reid
 */

public class SwoopText extends PNode {
    private String text;
    private ArrayList letters = new ArrayList();
    private Random random = new Random();
    private FontRenderContext fontRenderContext;
    private Font font;
    private PPath border;
    private double destX;
    private double destY;

    public SwoopText( String text, double destX, double destY ) {
        this.destX = destX;
        this.destY = destY;
        this.text = text;
        fontRenderContext = new FontRenderContext( new AffineTransform(), true, true );
        font = new Font( "Lucida Sans", Font.BOLD, 14 );

        Rectangle2D fullBounds = font.getStringBounds( text, fontRenderContext );
        fullBounds = RectangleUtils.expandRectangle2D( fullBounds, 5, 5 );
        border = new PPath( new Rectangle2D.Double( destX - 5, destY - 5, fullBounds.getWidth(), fullBounds.getHeight() ) );
        border.setStroke( null );
        border.setPaint( new Color( 100, 200, 120, 0 ) );
        addChild( border );

        for( int i = 0; i < text.length(); i++ ) {
            SwoopLetter swoopLetter = new SwoopLetter( text.charAt( i ) + "", font );
            letters.add( swoopLetter );
            addChild( swoopLetter );
            swoopLetter.setOffset( -swoopLetter.getFullBounds().getWidth() - 100, randomY() );
            Rectangle2D bounds = font.getStringBounds( text.substring( 0, i ), fontRenderContext );
            swoopLetter.setDestination( bounds.getWidth() + destX, destY );
            swoopLetter.setRotation( random.nextDouble() * Math.PI * 2 );
            swoopLetter.setScale( random.nextDouble() * 2.0 );
        }
    }

    public void animateAll() {
        border.animateToColor( new Color( 100, 200, 120, 128 ), 3000 );
        for( int i = 0; i < letters.size(); i++ ) {
            SwoopLetter swoopLetter = (SwoopLetter)letters.get( i );
            swoopLetter.animateToDestination();
        }

    }

    private double randomY() {
        return random.nextDouble() * 800;
    }

    static class SwoopLetter extends PNode {
        private double destX;
        private double destY;

        public SwoopLetter( String s, Font font ) {
            PText shadow = new PText( s );
            shadow.setFont( font );
            shadow.setOffset( 1, 1 );
            shadow.setTextPaint( Color.black );
            addChild( shadow );


            PText text = new PText( s );
            text.setFont( font );
            text.setTextPaint( Color.red );
            addChild( text );


        }

        public void setDestination( double x, double y ) {
            this.destX = x;
            this.destY = y;
        }

        public void animateToDestination() {
            animateToPositionScaleRotation( destX, destY, 1.0, 0, 4000 );
        }
    }

    public static void main( String[] args ) {
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        PPath path = new PPath( new Rectangle( 100, 100, 10, 10 ) );
        path.setPaint( Color.red );
        phetPCanvas.addWorldChild( path );
        SwoopText swoopText = new SwoopText( "Swoop/wiggle me!!", (int)( path.getFullBounds().getMaxX() + 10 ), (int)path.getFullBounds().getY() );
        phetPCanvas.addWorldChild( swoopText );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( phetPCanvas );
        frame.setSize( 400, 400 );
        frame.setVisible( true );

        swoopText.animateAll();
    }
}
