// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This subclass of ButtonNode just uses a PText to draw the text-only label.  This class is provided to help avoid problems centering the label that occur sometimes when using HTMLNode, see #2780 and #2902
 *
 * @author Sam Reid
 */
public class TextButtonNode extends ButtonNode {
    private String text;
    private Font font;
    private Color enabledTextColor = Color.black;
    private Color disabledTextColor = Color.gray;

    /**
     * Creates a text button with the specified text and the default font and colors.
     *
     * @param text the text to show in the button
     */
    public TextButtonNode( String text ) {
        this( text, new PhetFont( DEFAULT_FONT_STYLE, 14 ) );
    }

    /**
     * Creates a text button with the specified text and font and the default colors.
     *
     * @param text the text to show in the button
     * @param font the font to use for the button text
     */
    public TextButtonNode( String text, final Font font ) {
        super( text, new ContentNode( text, font, Color.black ), new ContentNode( text, font, Color.gray ) );
        this.text = text;
        this.font = font;
        setContentNode( new ContentNode( this.text, this.font, enabledTextColor ) );
        setDisabledContentNode( new ContentNode( text, font, disabledTextColor ) );
    }

    //Inner class used for creating the text content to show in the button
    private static class ContentNode extends PText {
        public ContentNode( String text, Font font, Color color ) {
            super( text );
            setFont( font );
            setTextPaint( color );
        }
    }

    //Sets the text color to be used when the button is enabled
    public void setEnabledTextColor( Color enabledTextColor ) {
        this.enabledTextColor = enabledTextColor;
        setContentNode( new ContentNode( text, font, enabledTextColor ) );
    }

    //Sets the text color to be used when the button is disabled
    public void setDisabledTextColor( Color disabledTextColor ) {
        this.disabledTextColor = disabledTextColor;
        setDisabledContentNode( new ContentNode( text, font, disabledTextColor ) );
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public void setFont( final Font font ) {
        this.font = font;
        setContentNode( new ContentNode( text, font, Color.black ) );
        setDisabledContentNode( new ContentNode( text, font, Color.gray ) );
    }

    public static void main( String[] args ) {
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        TextButtonNode button2 = new TextButtonNode( "2. Test <br> Me Too" ) {{
            setFont( new PhetFont( 24 ) );
            setBackground( new Color( 0x99cccc ) );
        }};
        button2.setOffset( 200, 5 );
        button2.addActionListener( listener );

        TextButtonNode button3 = new TextButtonNode( "<html><center>3. Default Color<br>and Font<center></html>" );
        button3.setOffset( 5, 200 );
        button3.addActionListener( listener );

        TextButtonNode button4 = new TextButtonNode( "4. Default Font Size" ) {{
            setBackground( new Color( 0xcc3366 ) );
        }};
        button4.setOffset( 200, 200 );
        button4.addActionListener( listener );

        TextButtonNode button5 = new TextButtonNode( "5. Transparent" ) {{
            setBackground( new Color( 255, 0, 0, 100 ) );
        }};
        button5.setOffset( 200, 125 );
        button5.addActionListener( listener );

        final TextButtonNode button6 = new TextButtonNode( "6. Test Enabled" ) {{
            setBackground( new Color( 0, 200, 200 ) );
        }};
        button6.setOffset( 230, 300 );
        button6.addActionListener( listener );

        TextButtonNode button7 = new TextButtonNode( "7. Toggle Enabled ->" ) {{
            setFont( new PhetFont( Font.ITALIC, 16 ) );
            setForeground( Color.RED );
            setBackground( new Color( 200, 200, 0 ) );
            setToolTipText( "<html>click me to<br>disabled button 6</html>" );
        }};
        button7.setOffset( 10, 300 );
        button7.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
                button6.setEnabled( !button6.isEnabled() );
            }
        } );

        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( button2 );
        canvas.getLayer().addChild( button3 );
        canvas.getLayer().addChild( button4 );
        canvas.getLayer().addChild( button5 );
        canvas.getLayer().addChild( button6 );
        canvas.getLayer().addChild( button7 );

        JFrame frame = new JFrame( TextButtonNode.class.getName() );
        frame.setContentPane( canvas );
        frame.setSize( 475, 425 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}