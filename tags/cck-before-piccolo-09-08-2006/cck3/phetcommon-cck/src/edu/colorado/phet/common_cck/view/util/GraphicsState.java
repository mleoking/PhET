/**
 * Class: GraphicsState
 * Package: edu.colorado.phet.common.view.util
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.common_cck.view.util;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A utilitye class for saving and restoring the state of Graphics2D objects
 */
public class GraphicsState {
    private Graphics2D graphics2D;
    private RenderingHints renderingHints;
    private Paint paint;
    private Color color;
    private Stroke stroke;
    private Composite composite;
    private AffineTransform transform;
    private Font font;
    private Shape clip;
    private Color background;

    public GraphicsState( Graphics2D graphics2D ) {
        this.graphics2D = graphics2D;
        renderingHints = graphics2D.getRenderingHints();
        paint = graphics2D.getPaint();
        color = graphics2D.getColor();
        stroke = graphics2D.getStroke();
        composite = graphics2D.getComposite();
        transform = graphics2D.getTransform();
        font = graphics2D.getFont();
        clip = graphics2D.getClip();
        background = graphics2D.getBackground();
    }

    public void restoreGraphics() {
        graphics2D.setRenderingHints( renderingHints );
        graphics2D.setPaint( paint );
        graphics2D.setColor( color );
        graphics2D.setStroke( stroke );
        graphics2D.setComposite( composite );
        graphics2D.setTransform( transform );
        graphics2D.setFont( font );
        graphics2D.setClip( clip );
        graphics2D.setBackground( background );
    }
}
