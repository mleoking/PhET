/** Sam Reid*/
package edu.colorado.phet.movingman.common;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 29, 2004
 * Time: 3:45:00 PM
 * Copyright (c) Aug 29, 2004 by Sam Reid
 */
public class GraphicsSetup {
    private Stroke stroke;
    private Paint paint;
    private Shape clip;
    private RenderingHints rh;
    private Font font;
    private Composite composite;
    private Color background;
    private Color color;

    public void saveState( Graphics2D g2 ) {
        stroke = g2.getStroke();
        paint = g2.getPaint();
        clip = g2.getClip();
        rh = g2.getRenderingHints();
        font = g2.getFont();
        composite = g2.getComposite();
        background = g2.getBackground();
        color = g2.getColor();
    }

    public void restoreState( Graphics2D g2 ) {
        g2.setStroke( stroke );
        g2.setPaint( paint );
        g2.setClip( clip );
        g2.setFont( font );
        g2.setRenderingHints( rh );
        g2.setComposite( composite );
        g2.setBackground( background );
        g2.setColor( color );
    }
}
