/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 4:49:05 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.sound.graphics;

import edu.colorado.phet.graphics.ShapeGraphic;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.sound.controller.SoundConfig;
import edu.colorado.phet.sound.physics.WaveMedium;
import edu.colorado.phet.sound.physics.Wavefront;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.geom.*;
import java.util.Observable;

public class WavefrontGraphic extends ShapeGraphic implements ImageObserver {

    // TODO: This should be set by a call to initLayout, not here.
    // TODO: The -8 and +12 are to jimmie the thing into place. This should be fixed
    private Point2D.Float origin = new Point2D.Float( SoundConfig.s_wavefrontBaseX - 18, SoundConfig.s_wavefrontBaseY + 12);
    private float height = SoundConfig.s_wavefrontHeight;
    private float stroke = 1;
    // Adjust this to control the dispersion angle of a spherical wavefront
    private float radius = SoundConfig.s_wavefrontRadius;
    private boolean isPlanar;

    private float[] amplitudes = new float[Wavefront.s_length];

    private BufferedImage buffImg;
    Graphics2D g2DBuffImg;

    private float opacity = 1.0f;

    public WavefrontGraphic() {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        buffImg = gc.createCompatibleImage( 800, 800 );
        g2DBuffImg = buffImg.createGraphics();
        g2DBuffImg.setColor( new Color( 128, 128, 128 ) );
        g2DBuffImg.fill( new Rectangle( buffImg.getMinX(), buffImg.getMinY(), buffImg.getWidth(), buffImg.getHeight() ) );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_DISABLE);
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING,RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    /**
     *
     * @param origin
     * @param height
     * @param radius
     */
    public void initLayout( Point2D.Float origin, float height, float radius ) {
        this.origin = origin;
        this.height = height;
        this.radius = radius;
    }

    /**
     *
     * @return
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     *
     * @param opacity
     */
    public void setOpacity( float opacity ) {
        this.opacity = opacity;
    }

    /**
     *
     */
    public void paint( Graphics2D g ) {

        // Set opacity
        Composite incomingComposite = g.getComposite();
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) );

        Stroke oldStroke = g.getStroke();
        g.setStroke( s_defaultStroke );
        Color oldColor = g.getColor();

        WaveMedium waveMedium = (WaveMedium)this.getBody();

        Point2D end1 = new Point2D.Float();
        Point2D end2 = new Point2D.Float();
        Line2D line = new Line2D.Float();

        float alpha = (float)( Math.asin( height / ( 2 * radius ) ) * ( 180 / Math.PI ));
        Arc2D.Float arc = new Arc2D.Float();

        // Compute stuff we need to calculate the arc.
        // x and y are the
        float arcHt = height - 20;
        float rad2 = radius - 20;
        float theta = (float)Math.asin( arcHt / ( 2 * rad2 ) );
        float c = (float)( arcHt / ( 2 * Math.tan( theta ) ));
        float x = (float)( origin.getX() - c - rad2 );

        // HACK!!!
        float y = (float)( origin.getY() + ( arcHt / 2 ) - rad2 );

        // Draw a line or arc for each value in the amplitude array of the wave front
        for( int i = 0; i < waveMedium.getMaxX(); i++ ) {
//        for( int i = waveMedium.getMaxX() - 1; i >= 0; i-- ) {

            // The hard-coded 15 here is to get the wave front to start right at the edge of the
            // speaker
            end1.setLocation( origin.getX() + 15 + i * stroke, origin.getY() - 22 );
            end2.setLocation( origin.getX() + 15 + i * stroke, origin.getY() + height + 22 );
            line.setLine( end1, end2 );

            arc.setArc( x - ( i * stroke ),
                        y - ( i * stroke ),
                        rad2 * 2 + ( i * stroke * 2 ),
                        rad2 * 2 + ( i * stroke * 2 ),
                        -alpha, alpha * 2, Arc2D.OPEN );

            // Negative in front of amplitude is to make black indicate high pressure, and white
            // indicate low pressure
            float amplitude = amplitudes[i];
            int colorIndex = (int)( ( -amplitude ) * ( s_lineColor.length / 2 )
                    + s_lineColor.length / 2 );
            g.setColor( s_lineColor[colorIndex] );
            if( this.isPlanar ) {
                g.draw( line );
            }
            else {
                // The following line draws the arc directly on the screen
//                g.draw( arc );

                // The following lines draw the are in the buffered image
                g2DBuffImg.setColor( s_lineColor[colorIndex] );
                g2DBuffImg.draw( arc );

                // Enable these lines to anti-alias the arcs. Note that this degrades performance
//                arc.x = arc.getX() - 1;
//                g.draw( arc );
            }
        }
        g.drawImage( buffImg, nopAT, null );

        g.setStroke( oldStroke );
        g.setColor( oldColor );
        g.setComposite( incomingComposite );
    }

    private AffineTransform nopAT = new AffineTransform();

    /**
     *
     */
    public Point2D.Float getOrigin() {
        return origin;
    }

    //
    // Abstract methods
    //

    protected void setPosition( Particle body ) {
    }

    /**
     * We copy out the amplitudes of the wave medium. If we wait until the paint() to get them, we get
     * into threading issues, and the display is jerky
     */
    boolean auxTest = false;

    /**
     *
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if( o instanceof WaveMedium ) {
            WaveMedium waveMedium = (WaveMedium)o;
            for( int i = 0; i < amplitudes.length; i++ ) {
                amplitudes[i] = waveMedium.getAmplitudeAt( (float)i );
            }
        }
    }

    public void setPlanar( boolean planar ) {
        isPlanar = planar;
    }

    protected float getHeight() {
        return height;
    }

    protected float getStroke() {
        return stroke;
    }

    protected float getRadius() {
        return radius;
    }

    protected boolean isPlanar() {
        return isPlanar;
    }

    protected BufferedImage getBuffImg() {
        return buffImg;
    }

    protected float[] getAmplitudes() {
        return amplitudes;
    }

    /* TEST */
    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }


    //
    // Static fields and methods
    //

    // An array of gray scale values
    static Color[] s_lineColor = new Color[256];

    static {
        for( int i = 0; i < s_lineColor.length; i++ ) {
            s_lineColor[i] = new Color( i, i, i );
        }
    }

    // Note that larger values for the stroke slow down performance considerably
    protected static Stroke s_defaultStroke = new BasicStroke( 1.0F );

}
