/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 3:56:06 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.graphics;

import java.awt.*;

/**
 * Abstract base class for PhetGraphics that are implemented with Java Shapes.
 */
public abstract class ShapeGraphic extends PhetGraphic {

    private Color strokeColor = s_defaultColor;
    private Stroke stroke = s_defaultStoke;
    private float alpha = s_defaultAlpha;
    private Color fill = s_defaultFill;

    /**
     *
     */
    protected ShapeGraphic() {
        init( s_defaultColor, s_defaultStoke );
    }

    /**
     *
     * @param color
     * @param stroke
     */
    protected ShapeGraphic( Color color, Stroke stroke ) {
        init( color, stroke );
    }

    /**
     *
     * @param color
     * @param stroke
     */
    protected void init( Color color, Stroke stroke ) {
        this.strokeColor = color;
        this.stroke = stroke;
    }

    protected void setOpacity( float alpha ) {
        this.alpha = alpha;
    }

    protected void setFill( Color fill ) {
        this.fill = fill;
    }

    protected Color getColor() {
        return strokeColor;
    }

    protected void setColor( Color color ) {
        this.strokeColor = color;
    }

    /**
     *
     * @param g
     */
    public void paint( Graphics2D g ) {
        Color oldColor = g.getColor();
        g.setColor( fill );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ));
        g.fill( (Shape)this.getRep() );

        g.setColor( strokeColor );
        g.setStroke( stroke );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ));
        g.draw( (Shape)this.getRep() );

        g.setColor( oldColor );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1.0f ));
    }

    //
    // Static fields and methods
    //
    private static Color s_defaultColor = Color.black;
    private static Color s_defaultFill = Color.white;
    private static Stroke s_defaultStoke = new BasicStroke( 1.0F );
    private static float s_defaultAlpha = 1.0f;
}
