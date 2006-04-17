package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Border factory. Modelled after the MetalBorders, from which it inherits. The
 * <code>paintBorder()</code> method is overridden to turn on anti-aliasing.
 * This is very similar to what's done in UI delegates.
 */
public class SmoothBorders
        extends MetalBorders {
    private static Border buttonBorder;
    private static Border frameBorder;
    private static Border internalFrameBorder;
    private static Border optionDialogBorder;
    private static Border paletteBorder;

    public static Border getButtonBorder() {
        if( null == buttonBorder ) {
            buttonBorder = new BorderUIResource.CompoundBorderUIResource(
                    new ButtonBorder(), new BasicBorders.MarginBorder() );
        }

        return buttonBorder;
    }

    public static Border getFrameBorder() {
        if( null == frameBorder ) {
            frameBorder = new FrameBorder();
        }

        return frameBorder;
    }

    public static Border getInternalFrameBorder() {
        if( null == internalFrameBorder ) {
            internalFrameBorder = new InternalFrameBorder();
        }

        return internalFrameBorder;
    }

    public static Border getOptionDialogBorder() {
        if( null == optionDialogBorder ) {
            optionDialogBorder = new OptionDialogBorder();
        }

        return optionDialogBorder;
    }

    public static Border getPaletteBorder() {
        if( null == paletteBorder ) {
            paletteBorder = new PaletteBorder();
        }

        return paletteBorder;
    }

    public static class ButtonBorder
            extends MetalBorders.ButtonBorder {
        public void paintBorder( final Component c, final Graphics g,
                                 final int x, final int y, final int w, final int h ) {
            SmoothUtilities.configureGraphics( g );
            super.paintBorder( c, g, x, y, w, h );
        }
    }

    public static class FrameBorder
            extends AbstractBorder
            implements UIResource {
        private final AbstractBorder delegate = createDelegate();

        // JDK6
//        public int getBaseline(final Component c, final int width,
//                final int height) {
//            return delegate.getBaseline(c, width, height);
//        }

        // JDK6
//        public BaselineResizeBehavior getBaselineResizeBehavior(
//                final Component c) {
//            return delegate.getBaselineResizeBehavior(c);
//        }
//

        public Insets getBorderInsets( final Component c ) {
            return delegate.getBorderInsets( c );
        }

        public Insets getBorderInsets( final Component c, final Insets insets ) {
            return delegate.getBorderInsets( c, insets );
        }

        public Rectangle getInteriorRectangle( final Component c, final int x,
                                               final int y, final int width,
                                               final int height ) {
            return delegate.getInteriorRectangle( c, x, y, width, height );
        }

        public boolean isBorderOpaque() {
            return delegate.isBorderOpaque();
        }

        public void paintBorder( final Component c, final Graphics g,
                                 final int x, final int y, final int w, final int h ) {
            SmoothUtilities.configureGraphics( g );
            delegate.paintBorder( c, g, x, y, w, h );
        }

        private static AbstractBorder createDelegate() {
            try {
                // JDK has package protected frame class -- ugh.
                final Class delegateClass = Class.forName(
                        "javax.swing.plaf.metal.MetalBorders$FrameBorder" );

                final Constructor defaultConstructor
                        = delegateClass.getDeclaredConstructor( null );

                defaultConstructor.setAccessible( true );

                return (AbstractBorder)defaultConstructor.newInstance( null );

            }
            catch( final Exception e ) {
                // Improbable, but at least make it possible for the caller to
                // see what happened.
                Logger.global.logp( Level.WARNING, FrameBorder.class.toString(),
                                    "createDelegate()",
                                    "Cannot create MetalBorders.FrameBorder delegate:", e );
                throw new RuntimeException( e );
            }
        }
    }

    public static class InternalFrameBorder
            extends MetalBorders.InternalFrameBorder {
        public void paintBorder( final Component c, final Graphics g,
                                 final int x, final int y, final int w, final int h ) {
            SmoothUtilities.configureGraphics( g );
            super.paintBorder( c, g, x, y, w, h );
        }
    }

    public static class OptionDialogBorder
            extends MetalBorders.OptionDialogBorder {
        public void paintBorder( final Component c, final Graphics g,
                                 final int x, final int y, final int w, final int h ) {
            SmoothUtilities.configureGraphics( g );
            super.paintBorder( c, g, x, y, w, h );
        }
    }

    public static class PaletteBorder
            extends MetalBorders.PaletteBorder {
        public void paintBorder( final Component c, final Graphics g,
                                 final int x, final int y, final int w, final int h ) {
            SmoothUtilities.configureGraphics( g );
            super.paintBorder( c, g, x, y, w, h );
        }
    }
}