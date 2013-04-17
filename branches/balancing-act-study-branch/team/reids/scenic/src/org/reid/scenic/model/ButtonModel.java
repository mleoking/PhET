// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Should font and text be in this class?
 * Text might be internationalized, but may still be good to keep in the model.  What about the position?  One argument for MVC is that the view can be varied independently without
 * modifying the model, but it is conceptually nice to keep all button-related features together
 *
 * @author Sam Reid
 */
public class ButtonModel<T> {
    public final Font font;
    public final String text;
    public final double x;
    public final double y;
    public final boolean pressed;
    //is the mouse hovering over the button
    public final boolean hover;
    public final Function1<T, T> actionListener;

    public ButtonModel( Font font, String text, double x, double y, boolean pressed, boolean hover, Function1<T, T> actionListener ) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
        this.pressed = pressed;
        this.hover = hover;
        this.actionListener = actionListener;
    }

    public ButtonModel<T> pressed( boolean pressed ) {
        return new ButtonModel<T>( font, text, x, y, pressed, hover, actionListener );
    }

    public ButtonModel<T> hover( boolean hover ) {
        return new ButtonModel<T>( font, text, x, y, pressed, hover, actionListener );
    }

    public T apply( T model ) {
        return actionListener.apply( model );
    }
}