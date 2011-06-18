// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

//TODO The most general constructor should be ButtonNode(PNode), allowing any PNode to be put on a button.

/**
 * A Piccolo button with interface similar to JButton.
 * Supports both text (plaintext or HTML) and images.
 * Filled with a gradient paint, and has a drop shadow.
 * Origin is at the upper left of the bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author John Blanco (previous text-only version of ButtonNode)
 */
public class HTMLImageButtonNode extends ButtonNode {

    // position of text relative to image
    public static enum TextPosition {
        ABOVE, BELOW, LEFT, RIGHT
    }

    // settable properties
    private String text;
    protected BufferedImage image;
    private Font font;
    private TextPosition textPosition;
    private int imageTextGap; // space between image and text
    private BufferedImage disabledImage;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    public HTMLImageButtonNode() {
        this( null, (BufferedImage) null );
    }

    public HTMLImageButtonNode( String text ) {
        this( text, (BufferedImage) null );
    }

    public HTMLImageButtonNode( BufferedImage image ) {
        this( null, image );
    }

    public HTMLImageButtonNode( String text, BufferedImage image ) {
        //Initialize with empty content so we don't have to duplicate content creation code, the content is set at the end of the constructor
        super( text, new PNode(), new PNode() );

        this.text = text;
        this.image = image;

        // default settings
        font = new PhetFont( DEFAULT_FONT_STYLE, 14 );
        textPosition = TextPosition.RIGHT;
        imageTextGap = 5;

        updateContent();
    }

    //Create and set the content for enabled/disabled states
    private void updateContent() {
        setContentNode( createContentNode( text, font, getForeground(), getDisabledForeground(), true, image, image, textPosition, imageTextGap ) );
        setDisabledContentNode( createContentNode( text, font, getForeground(), getDisabledForeground(), false, image, image, textPosition, imageTextGap ) );
    }

    // Convenience constructor
    public HTMLImageButtonNode( String text, Color background ) {
        this( text );
        setBackground( background );
    }

    // Convenience constructor
    public HTMLImageButtonNode( String text, int fontSize, Color background ) {
        this( text, new PhetFont( DEFAULT_FONT_STYLE, fontSize ), background );
    }

    // Convenience constructor
    public HTMLImageButtonNode( String text, PhetFont font, Color background ) {
        this( text );
        setFont( font );
        setBackground( background );
    }

    // Convenience constructor
    public HTMLImageButtonNode( String text, int fontSize, Color foreground, Color background ) {
        this( text, new PhetFont( DEFAULT_FONT_STYLE, fontSize ), foreground, background );
    }

    // Convenience constructor
    public HTMLImageButtonNode( String text, PhetFont font, Color foreground, Color background ) {
        this( text );
        setFont( font );
        setForeground( foreground );
        setBackground( background );
    }

    //------------------------------------------------------------------------
    // Setters and getters
    //------------------------------------------------------------------------

    public void setText( String text ) {
        if ( text == null || !text.equals( this.text ) ) {
            this.text = text;
            updateContent();
        }
    }

    public String getText() {
        return text;
    }

    public void setImage( BufferedImage image ) {
        if ( image == null || !image.equals( this.image ) ) {
            this.image = image;
            updateContent();
        }
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image to be used when the button is disabled.
     * If an image has not been provided for the enabled state, then this disabled image is ignored.
     * If no disabled image is provided, the default behavior is to generate a grayscale version
     * of the enabled image.
     *
     * @param disabledImage
     */
    public void setDisabledImage( BufferedImage disabledImage ) {
        if ( disabledImage == null || !disabledImage.equals( this.disabledImage ) ) {
            this.disabledImage = disabledImage;
            updateContent();
        }
    }

    public BufferedImage getDisabledImage() {
        return disabledImage;
    }

    public void setFont( Font font ) {
        if ( !font.equals( this.font ) ) {
            this.font = font;
            updateContent();
        }
    }

    public Font getFont() {
        return font;
    }


    public void setTextPosition( TextPosition textPosition ) {
        if ( textPosition != this.textPosition ) {
            this.textPosition = textPosition;
            updateContent();
        }
    }

    public TextPosition getTextPosition() {
        return textPosition;
    }

    public void setImageTextGap( int imageTextGap ) {
        if ( imageTextGap != this.imageTextGap ) {
            this.imageTextGap = imageTextGap;
            updateContent();
        }
    }

    public int getImageTextGap() {
        return imageTextGap;
    }

    //------------------------------------------------------------------------
    // Listeners
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Node creation utilities
    //------------------------------------------------------------------------

    // Creates an HTML node that corresponds to the enabled state.
    private static PNode createTextNode( String text, Font font, Color textColor, Color disabledTextColor, boolean enabled ) {
        PNode node = null;
        if ( text != null ) {
            final HTMLNode htmlNode = new HTMLNode( text, enabled ? textColor : disabledTextColor );
            htmlNode.setFont( font );
            htmlNode.setPickable( false );
            node = htmlNode;
        }
        else {
            node = new PNode(); // if we have no text, return an empty node as a placeholder
        }
        return node;
    }

    // Creates an image node that corresponds to the enabled state.
    private static PNode createImageNode( BufferedImage image, BufferedImage disabledImage, boolean enabled ) {
        PNode node = null;
        if ( image != null ) {
            if ( enabled ) {
                node = new PImage( image );
            }
            else {
                if ( disabledImage != null ) {
                    node = new PImage( disabledImage );
                }
                else {
                    node = new PImage( createGrayscaleImage( image ) ); // if we have no disabledImage, generate one
                }
            }
        }
        else {
            node = new PNode(); // if we have no image, return an empty node as a placeholder
        }
        return node;
    }

    // Converts an image to grayscale, to denote that the button is disabled.
    //TODO This needs tweaking to better match the disabled look of other parts of the button. Address when it becomes an issue.
    private static BufferedImage createGrayscaleImage( BufferedImage colorImage ) {
        ColorSpace cs = ColorSpace.getInstance( ColorSpace.CS_GRAY );
        ColorConvertOp op = new ColorConvertOp( cs, null );
        return op.filter( colorImage, null );
    }

    // text and image, with an intermediate parent
    private static PNode createContentNode( String text, Font font, Color foreground, Color disabledForeground, boolean enabled, BufferedImage image, BufferedImage disabledImage, TextPosition textPosition, double imageTextGap ) {
        PNode textNode = createTextNode( text, font, foreground, disabledForeground, enabled );
        PNode imageNode = createImageNode( image, disabledImage, enabled );
        PComposite content = new PComposite();
        content.addChild( textNode );
        content.addChild( imageNode );

        // layout text and image
        double textX, imageX = 0;
        double textY, imageY = 0;
        PBounds tb = textNode.getFullBoundsReference();
        PBounds ib = imageNode.getFullBoundsReference();
        if ( textPosition == TextPosition.ABOVE ) {
            textX = 0;
            imageX = tb.getCenterX() - ( ib.getWidth() / 2 );
            textY = 0;
            imageY = tb.getMaxY() + imageTextGap;
        }
        else if ( textPosition == TextPosition.BELOW ) {
            imageX = 0;
            textX = ib.getCenterX() - ( tb.getWidth() / 2 );
            imageY = 0;
            textY = ib.getMaxY() + imageTextGap;
        }
        else if ( textPosition == TextPosition.LEFT ) {
            textX = 0;
            imageX = tb.getMaxX() + imageTextGap;
            textY = 0;
            imageY = tb.getCenterY() - ( ib.getHeight() / 2 );
        }
        else if ( textPosition == TextPosition.RIGHT ) {
            imageX = 0;
            textX = ib.getMaxX() + imageTextGap;
            imageY = 0;
            textY = ib.getCenterY() - ( tb.getHeight() / 2 );
        }
        else {
            throw new UnsupportedOperationException( "unsupported text position: " + textPosition );
        }
        textNode.setOffset( textX, textY );
        imageNode.setOffset( imageX, imageY );
        return content;
    }
    //------------------------------------------------------------------------
    // Test Harness
    //------------------------------------------------------------------------

    public static void main( String[] args ) {

        PhetResources resources = new PhetResources( "phetcommon" );
        BufferedImage image = resources.getImage( "buttons/minimizeButton.png" );

        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        HTMLImageButtonNode button1 = new HTMLImageButtonNode( image ) {{
            setCornerRadius( 20 );
            setImageTextGap( 20 );
            setMargin( 20, 10, 20, 10 );
            setFont( new PhetFont( 16 ) );
            setBackground( Color.GREEN );
        }};
        button1.setOffset( 5, 5 );
        button1.addActionListener( listener );

        HTMLImageButtonNode button2 = new HTMLImageButtonNode( "<html>2. Test <br> Me Too</html>", image ) {{
            setTextPosition( TextPosition.BELOW );
            setFont( new PhetFont( 24 ) );
            setBackground( new Color( 0x99cccc ) );
        }};
        button2.setOffset( 200, 5 );
        button2.addActionListener( listener );

        HTMLImageButtonNode button3 = new HTMLImageButtonNode( "<html><center>3. Default Color<br>and Font<center></html>" );
        button3.setOffset( 5, 200 );
        button3.addActionListener( listener );

        HTMLImageButtonNode button4 = new HTMLImageButtonNode( "4. Default Font Size" ) {{
            setBackground( new Color( 0xcc3366 ) );
        }};
        button4.setOffset( 200, 200 );
        button4.addActionListener( listener );

        HTMLImageButtonNode button5 = new HTMLImageButtonNode( "5. Transparent" ) {{
            setBackground( new Color( 255, 0, 0, 100 ) );
        }};
        button5.setOffset( 200, 125 );
        button5.addActionListener( listener );

        final HTMLImageButtonNode button6 = new HTMLImageButtonNode( "6. Test Enabled", image ) {{
            setBackground( new Color( 0, 200, 200 ) );
            setTextPosition( TextPosition.ABOVE );
        }};
        button6.setOffset( 230, 300 );
        button6.addActionListener( listener );

        HTMLImageButtonNode button7 = new HTMLImageButtonNode( "7. Toggle Enabled ->" ) {{
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

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( button1 );
        canvas.addScreenChild( button2 );
        canvas.addScreenChild( button3 );
        canvas.addScreenChild( button4 );
        canvas.addScreenChild( button5 );
        canvas.addScreenChild( button6 );
        canvas.addScreenChild( button7 );

        JFrame frame = new JFrame( HTMLImageButtonNode.class.getName() );
        frame.setContentPane( canvas );
        frame.setSize( 475, 425 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
