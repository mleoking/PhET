package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Class for rendering HTML Text.
 */
public class HTMLGraphic extends CompositePhetGraphic {
    private String html;
    private Font font;
    private Color color;
    private PhetImageGraphic imageGraphic;

    public HTMLGraphic( Component component, String html, Font font, Color color ) {
        super( component );
        this.html = html;
        this.font = font;
        this.color = color;
        imageGraphic = new PhetImageGraphic( component, null, 0, 0 );
        addGraphic( imageGraphic );
        update();
    }

    public void setHtml( String html ) {
        this.html = html;
        update();
    }

    public void setFont( Font font ) {
        this.font = font;
        update();
    }

    public void setColor( Color color ) {
        this.color = color;
        update();
    }

    public void update() {
        imageGraphic.setImage( HTMLRenderFrame.render( color, font, html ) );
        setBoundsDirty();
        autorepaint();
    }

    /**
     * * Static utility to transform html into BufferedImage for quick rendering.
     */
    private static class HTMLRenderFrame {
        static JFrame frame = new JFrame( "HTML Render Frame (PhET)" );
        static JPanel content = new JPanel();

        static {
            frame.setContentPane( content );
            frame.show();//has to be visible at least once to do rendering (java 1.4.2_07), SRR
            frame.hide();
        }

        /**
         * Static utility to transform html into BufferedImage for quick rendering.
         * This could fail on some systems.
         */
        public static BufferedImage render( Color color, Font font, String html ) {
            JLabel label = new JLabel( html ) {
                public boolean isShowing() {
                    return true;
                }
            };
            label.setFont( font );
            label.setForeground( color );
            content.add( label );
            frame.invalidate();
            frame.validate();
            frame.repaint();

            Dimension dim = label.getPreferredSize();
            BufferedImage image = new BufferedImage( dim.width, dim.height, BufferedImage.TYPE_INT_ARGB );
            final Graphics2D g = image.createGraphics();
            g.setColor( new Color( 255, 255, 255, 0 ) );//transparent background
            g.fill( new Rectangle( 0, 0, image.getWidth(), image.getHeight() ) );
            label.paintAll( g );
            return image;
        }
    }
}
