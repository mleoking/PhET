// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class for testing the centering of text in the ButtonNode class, see #2780.
 * <p/>
 * Author: John Blanco
 */
public class TestButtonTextCentering {

    private static final Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    public static void test1() {

        PDebug.debugBounds = false;
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        ButtonNode button1 = new ButtonNode( "xxx", new PhetFont( Font.BOLD, 18 ), Color.ORANGE );
        button1.setOffset( 500, 500 );
        button1.addActionListener( listener );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );
        canvas.addWorldChild( button1 );

        JFrame frame = new JFrame( ButtonNode.class.getName() );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }

    public static void test2() {
        JFrame frame = new JFrame( ButtonNode.class.getName() );
        frame.setContentPane( new JPanel(){
            @Override protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D)g;
                g2.setFont( new PhetFont(Font.BOLD, 18 ) );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                String text = "Zorg Foury";
                Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds( text, g2 );
                g2.draw( new Rectangle2D.Double(100, 100 - stringBounds.getHeight() + g2.getFontMetrics().getDescent(), stringBounds.getWidth(), stringBounds.getHeight()));
                g2.drawString( text, 100, 100 );
            }
        });
        frame.setSize( 250, 200 );
        frame.setVisible( true );
    }

    public static void test3() {
        PDebug.debugBounds = true;

        PCanvas canvas = new PCanvas();
        String text = "This is a <br>test.";
        final Font font = new PhetFont( 18, true );
        HTMLNode htmlNode = new HTMLNode( text ){{
            setFont( font );
            setOffset(20, 20);
        }};
        canvas.getLayer().addChild( htmlNode );
        PText textNode = new PText( text ){{
            setFont( font );
            setOffset(20, 100);
        }};
        canvas.getLayer().addChild( textNode );

        ButtonNode button1 = new ButtonNode( "xxx", new PhetFont( Font.BOLD, 18 ), Color.ORANGE );
        button1.setOffset( 100, 200 );
        canvas.getLayer().addChild( button1 );


        JFrame frame = new JFrame( ButtonNode.class.getName() );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );


    }

    public static void main( String[] args ) {
        test3();
    }
}
