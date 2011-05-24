// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventListener;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * @author Sam Reid
 */
public class ButtonNode extends PhetPNode {
    protected static final double COLOR_SCALING_FACTOR = 0.5; // scaling factor for creating brighter colors
    protected static final int DEFAULT_FONT_STYLE = Font.PLAIN; // #2846, using plain as the default style, add bold/italic emphasis explicitly
    protected final PNode parentNode; // intermediate parent for all nodes created herein
    protected final ArrayList<ActionListener> actionListeners;
    protected BufferedImage disabledImage;
    protected Color foreground;
    protected Color background;
    protected Color shadowColor;
    protected Color strokeColor;
    protected Color disabledForeground;
    protected Color disabledBackground;
    protected Color disabledShadowColor;
    protected Color disabledStrokeColor;
    protected boolean enabled;
    protected Insets margin;
    protected int cornerRadius; // radius on the corners of the button and shadow
    protected int shadowOffset; // horizontal and vertical offset of the shadow
    protected String toolTipText;
    private PPath backgroundNode;
    private boolean focus;
    private boolean armed; // semantics defined in ButtonEventListener javadoc
    private Paint mouseNotOverGradient;
    private Paint mouseOverGradient;
    private Paint armedGradient;
    protected PNode content;
    private String actionCommand = "TODO";//TODO

    public ButtonNode() {
        disabledImage = null;
        disabledShadowColor = new Color( 0, 0, 0, 0 ); // invisible
        disabledStrokeColor = new Color( 190, 190, 190 );
        strokeColor = Color.BLACK;
        enabled = true;
        cornerRadius = 8;
        parentNode = new PNode();
        shadowOffset = 3;
        disabledForeground = new Color( 180, 180, 180 ); // medium gray
        background = Color.GRAY;
        margin = new Insets( 3, 10, 3, 10 );
        disabledBackground = new Color( 210, 210, 210 ); // light gray
        actionListeners = new ArrayList<ActionListener>();
        toolTipText = null;
        foreground = Color.BLACK;
        shadowColor = new Color( 0f, 0f, 0f, 0.2f ); // translucent black
    }

    /*
    * Completely rebuilds the button when any property changes.
    * This is not as bad as it sounds, since there are only 4 nodes involved.
    * Yes, we could figure out which nodes are dependent on which properties,
    * and write code that update only the affected nodes. But such code would
    * be more complicated and more difficult to debug, maintain and test.
    */
    protected void update() {
        // All parts of the button are parented to this node, so that we don't blow away additional children that clients might add.
        parentNode.removeAllChildren();

        // button shape
        double backgroundWidth = content.getFullBoundsReference().getWidth() + margin.left + margin.right;
        double backgroundHeight = content.getFullBoundsReference().getHeight() + margin.top + margin.bottom;
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double( 0, 0, backgroundWidth, backgroundHeight, cornerRadius, cornerRadius );

        // gradients, used in button handler to indicate state changes
        mouseNotOverGradient = createMouseNotOverGradient( backgroundWidth, backgroundHeight );
        mouseOverGradient = createMouseOverGradient( backgroundWidth, backgroundHeight );
        armedGradient = createArmedGradient( backgroundWidth, backgroundHeight );

        // background
        backgroundNode = new PPath( buttonShape );
        backgroundNode.addInputEventListener( new CursorHandler() );
        if ( enabled ) {
            backgroundNode.setPaint( mouseNotOverGradient );
            backgroundNode.setStrokePaint( strokeColor );
        }
        else {
            backgroundNode.setPaint( createDisabledGradient( backgroundWidth, backgroundHeight ) );
            backgroundNode.setStrokePaint( disabledStrokeColor );
        }

        // shadow
        PPath shadowNode = new PPath( buttonShape );
        shadowNode.setPickable( false );
        shadowNode.setOffset( shadowOffset, shadowOffset );
        shadowNode.setStroke( null );
        shadowNode.setPaint( enabled ? shadowColor : disabledShadowColor );

        // text and image are children of background, to simplify interactivity
        backgroundNode.addChild( content );
        double x = margin.left - PNodeLayoutUtils.getOriginXOffset( content );
        double y = margin.top - PNodeLayoutUtils.getOriginYOffset( content );
        content.setOffset( x, y );

        // shadow behind background
        parentNode.addChild( shadowNode );
        parentNode.addChild( backgroundNode );

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        backgroundNode.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventListener() {

            public void setFocus( boolean focus ) {
                ButtonNode.this.setFocus( focus );
            }

            public void setArmed( boolean armed ) {
                ButtonNode.this.setArmed( armed );
            }

            public void fire() {
                notifyActionPerformed();
            }
        } );

        // tool tip
        if ( toolTipText != null ) {
            parentNode.addChild( new ToolTipNode( toolTipText, this ) );
        }

        // ignore events when disabled
        setPickable( enabled );
        setChildrenPickable( enabled );
    }

    /**
     * Determines whether the button looks like it has focus (ie, is highlighted).
     *
     * @param focus
     */
    protected void setFocus( boolean focus ) {
        if ( focus != this.focus ) {
            this.focus = focus;
            updateAppearance();
        }
    }

    /**
     * Determines whether the button looks like it is armed (ie, is pressed).
     *
     * @param armed
     */
    protected void setArmed( boolean armed ) {
        if ( armed != this.armed ) {
            this.armed = armed;
            updateAppearance();
        }
    }

    // Updates appearance (gradient and offset) based on armed and focus state if enabled
    private void updateAppearance() {
        /*
            Only update with this type of "pressed"/"unpressed" appearance if we are enabled.
            This will prevent the button with an "enabled/unpressed" background if it is set to disabled before it is unarmed.
         */
        if ( enabled ) {
            if ( armed ) {
                backgroundNode.setPaint( armedGradient );
                backgroundNode.setOffset( shadowOffset, shadowOffset );
            }
            else {
                backgroundNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                backgroundNode.setOffset( 0, 0 );
            }
        }
    }

    public void setBackground( Color background ) {
        if ( !background.equals( this.background ) ) {
            this.background = background;
            update();
        }
    }

    public Color getBackground() {
        return background;
    }

    public void setForeground( Color foreground ) {
        if ( !foreground.equals( this.foreground ) ) {
            this.foreground = foreground;
            update();
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setShadowColor( Color shadowColor ) {
        if ( !shadowColor.equals( this.shadowColor ) ) {
            this.shadowColor = shadowColor;
            update();
        }
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public void setStrokeColor( Color strokeColor ) {
        if ( !strokeColor.equals( this.strokeColor ) ) {
            this.strokeColor = strokeColor;
            update();
        }
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setDisabledBackground( Color disabledBackground ) {
        if ( !disabledBackground.equals( this.disabledBackground ) ) {
            this.disabledBackground = disabledBackground;
            update();
        }
    }

    public Color getDisabledBackground() {
        return disabledBackground;
    }

    public void setDisabledForeground( Color disabledForeground ) {
        if ( !disabledForeground.equals( this.disabledForeground ) ) {
            this.disabledForeground = disabledForeground;
            update();
        }
    }

    public Color getDisabledForeground() {
        return disabledForeground;
    }

    public void setDisabledShadowColor( Color disabledShadowColor ) {
        if ( !disabledShadowColor.equals( this.disabledShadowColor ) ) {
            this.disabledShadowColor = disabledShadowColor;
            update();
        }
    }

    public Color getDisabledShadowColor() {
        return disabledShadowColor;
    }

    public void setDisabledStrokeColor( Color disabledStrokeColor ) {
        if ( !disabledStrokeColor.equals( this.disabledStrokeColor ) ) {
            this.disabledStrokeColor = disabledStrokeColor;
            update();
        }
    }

    public Color getDisabledStrokeColor() {
        return disabledStrokeColor;
    }

    public void setEnabled( boolean enabled ) {
        if ( enabled != this.enabled ) {
            this.enabled = enabled;
            update();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setMargin( Insets margin ) {
        if ( !margin.equals( this.margin ) ) {
            this.margin = new Insets( margin.top, margin.left, margin.bottom, margin.right );
            update();
        }
    }

    // Convenience method
    public void setMargin( int top, int left, int bottom, int right ) {
        setMargin( new Insets( top, left, bottom, right ) );
    }

    public Insets getMargin() {
        return new Insets( margin.top, margin.left, margin.bottom, margin.right );
    }

    /**
     * Provides very basic, default tool tip behavior.
     * If you want more control over tool tips, use ToolTipNode directly.
     *
     * @param toolTipText
     */
    public void setToolTipText( String toolTipText ) {
        if ( toolTipText == null || !toolTipText.equals( this.toolTipText ) ) {
            this.toolTipText = toolTipText;
            update();
        }
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setCornerRadius( int cornerRadius ) {
        if ( cornerRadius != this.cornerRadius ) {
            this.cornerRadius = cornerRadius;
            update();
        }
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setShadowOffset( int shadowOffset ) {
        if ( shadowOffset != this.shadowOffset ) {
            this.shadowOffset = shadowOffset;
            update();
        }
    }

    public int getShadowOffset() {
        return shadowOffset;
    }

    public void addActionListener( ActionListener listener ) {
        actionListeners.add( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        actionListeners.remove( listener );
    }

    private void notifyActionPerformed() {
        ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, actionCommand ); // use Swing convention from AbstractButton.fireActionPerformed
        for ( ActionListener actionListener : new ArrayList<ActionListener>( actionListeners ) ) {
            actionListener.actionPerformed( event );
        }
    }

    // When the mouse is not over the node, its specified background color is used.
    protected Paint createMouseNotOverGradient( double width, double height ) {
        return createGradient( createBrighterColor( background ), background, width, height );
    }

    // When the mouse is over the node but not pressed, the button gets brighter.
    protected Paint createMouseOverGradient( double width, double height ) {
        return createGradient( createBrighterColor( createBrighterColor( background ) ), createBrighterColor( background ), width, height );
    }

    // When the button is armed (pressed), the color is similar to mouse-not-over, but the button is brighter at the bottom.
    protected Paint createArmedGradient( double width, double height ) {
        return createGradient( background, createBrighterColor( background ), width, height );
    }

    // When the button is disabled, we use a different color to indicate disabled.
    private Paint createDisabledGradient( double width, double height ) {
        return createGradient( createBrighterColor( disabledBackground ), disabledBackground, width, height );
    }

    /*
    * Creates a gradient that vertically goes from topColor to bottomColor.
    * @param topColor
    * @param bottomColor
    * @return Paint
    */
    private Paint createGradient( Color topColor, Color bottomColor, double width, double height ) {
        if ( useGradient() ) {
            return new GradientPaint( (float) width / 2f, 0f, topColor, (float) width * 0.5f, (float) height, bottomColor );
        }
        else {
            return bottomColor;
        }
    }

    // See Unfuddle Ticket #553, GradientPaint crashes on Mac OS.
    //TODO GradientPaint crashes only for some versions of Mac OS. Return false only for those versions?
    private boolean useGradient() {
        return !PhetUtilities.isMacintosh();
    }


    //------------------------------------------------------------------------
    // Gradient creation utilities
    //------------------------------------------------------------------------

    // Creates a brighter color. Unlike Color.brighter, this algorithm preserves transparency.
    private static Color createBrighterColor( Color origColor ) {
        int red = origColor.getRed() + (int) Math.round( ( 255 - origColor.getRed() ) * COLOR_SCALING_FACTOR );
        int green = origColor.getGreen() + (int) Math.round( ( 255 - origColor.getGreen() ) * COLOR_SCALING_FACTOR );
        int blue = origColor.getBlue() + (int) Math.round( ( 255 - origColor.getBlue() ) * COLOR_SCALING_FACTOR );
        int alpha = origColor.getAlpha(); // preserve transparency of original color, see #2123
        return new Color( red, green, blue, alpha );
    }

}
