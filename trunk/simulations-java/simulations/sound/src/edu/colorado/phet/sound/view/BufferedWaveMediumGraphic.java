/**
 * Class: WaveMediumGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common_sound.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.model.Wavefront;

/**
 * This variant of the WaveMediumGraphic is used for the movable speaker in the Two
 * Source Intereference module
 */
public class BufferedWaveMediumGraphic extends PhetImageGraphic implements SimpleObserver {

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

    private static BufferedImage createBufferedImage() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage( 800, 800 );
        //        return gc.createCompatibleImage( 300, 200 );
    }


    // Note that larger values for the stroke slow down performance considerably
    protected static Stroke s_defaultStroke = new BasicStroke( 1.0F );
    private Point2D.Double origin;
    private double height = SoundConfig.s_wavefrontHeight;
    private double stroke = 1;
    // Adjust this to control the dispersion angle of a spherical wavefront
    private double radius = SoundConfig.s_wavefrontRadius;
    private boolean isPlanar;
    private double[] amplitudes = new double[Wavefront.s_length];
    private BufferedImage buffImg;
    private Graphics2D g2DBuffImg;
    private AffineTransform nopAT = new AffineTransform();
    private float opacity = 1.0f;
    // The angle of the axis of the wavefront, measured counterclockwise from postive x
    private double rotationAngle = 0;
    private AffineTransform lineRotationXform;
    private WaveMedium waveMedium;

    // Tracks the point from which an element of the wavefront was generated
    private Point2D.Double[] arcCenters = new Point2D.Double[Wavefront.s_length];


    /**
     * todo: rename WaveMediumGraphic
     */
    public BufferedWaveMediumGraphic( WaveMedium waveMedium, Component component ) {
        super( component, createBufferedImage() );

        // Hook up to the WaveMedium we are observing
        this.waveMedium = waveMedium;
        waveMedium.addObserver( this );

        buffImg = super.getImage();
        g2DBuffImg = buffImg.createGraphics();
        g2DBuffImg.setColor( SoundConfig.MIDDLE_GRAY );
        g2DBuffImg.fill( new Rectangle( buffImg.getMinX(), buffImg.getMinY(), buffImg.getWidth(), buffImg.getHeight() ) );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
        g2DBuffImg.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
    }

    /**
     * @param origin
     * @param height
     * @param radius
     */
    public void initLayout( Point2D.Double origin, double height, double radius ) {
        initLayout( origin, height, radius, 0 );
    }

    /**
     * @param origin
     * @param height
     * @param radius
     * @param theta
     */
    public void initLayout( Point2D.Double origin, double height, double radius,
                            double theta ) {
        this.origin = origin;
        this.height = height;
        this.radius = radius;
        setRotationAngle( theta );

        // Initialize the arc centers
        for( int i = 0; i < arcCenters.length; i++ ) {
            arcCenters[i] = origin;
        }
    }

    /**
     *
     */
    public void clear() {
        g2DBuffImg.setColor( SoundConfig.MIDDLE_GRAY );
        g2DBuffImg.fill( new Rectangle( buffImg.getMinX(), buffImg.getMinY(), buffImg.getWidth(), buffImg.getHeight() ) );
    }

    private void setRotationAngle( double theta ) {
        this.rotationAngle = theta;
        lineRotationXform = AffineTransform.getRotateInstance( -Math.toRadians( rotationAngle ),
                                                               origin.getX(), origin.getY() );
    }

    /**
     * @return
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * @param opacity
     */
    public void setOpacity( float opacity ) {
        this.opacity = opacity;
    }

    /**
     *
     */
    public void paint( Graphics2D g ) {

        this.clear();

        // Set opacity
        Composite incomingComposite = g.getComposite();
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) );

        Stroke oldStroke = g.getStroke();
        g.setStroke( s_defaultStroke );
        Color oldColor = g.getColor();

        Point2D end1 = new Point2D.Float();
        Point2D end2 = new Point2D.Float();
        Line2D line = new Line2D.Float();

        float alpha = (float)( Math.asin( height / ( 2 * radius ) ) * ( 180 / Math.PI ) );
        Arc2D.Float arc = new Arc2D.Float();

        // Compute stuff we need to calculate the arc.
        double arcHt = height - 20;
        double rad2 = radius - 20;
        float theta = (float)Math.asin( arcHt / ( 2 * rad2 ) );

        // Advanced the centers of the arcs forward
        for( int i = arcCenters.length - 1; i > SoundConfig.PROPOGATION_SPEED; i-- ) {
            arcCenters[i] = arcCenters[i - SoundConfig.PROPOGATION_SPEED];
        }
        for( int i = 0; i < 50; i++ ) {
            arcCenters[i] = origin;
        }

        // Draw a line or arc for each value in the amplitude array of the wave front
        for( int i = 0; i < waveMedium.getMaxX(); i++ ) {

            // Negative in front of amplitude is to make black indicate high pressure, and white
            // indicate low pressure
            double amplitude = amplitudes[i];
            int colorIndex = (int)( ( -amplitude ) * ( s_lineColor.length / 2 )
                                    + s_lineColor.length / 2 );
            g.setColor( s_lineColor[colorIndex] );
            Point2D.Double arcCenter = arcCenters[i];
            if( this.isPlanar ) {

                // The hard-coded 15 here is to get the wave front to start right at the edge of the
                // speaker
                end1.setLocation( origin.getX() + 15 + i * stroke, origin.getY() - 22 );
                end2.setLocation( origin.getX() + 15 + i * stroke, origin.getY() + height + 22 );
                line.setLine( end1, end2 );

                g.draw( line );
            }
            else {
                if( arcCenter != null ) {
                    arc.setArc( arcCenter.getX() - rad2 - ( i * stroke ),
                                arcCenter.getY() - rad2 - ( i * stroke ),
                                //                arc.setArc( origin.getX() - rad2 - ( i * stroke ),
                                //                            origin.getY() - rad2 - ( i * stroke ),
                                rad2 * 2 + ( i * stroke * 2 ),
                                rad2 * 2 + ( i * stroke * 2 ),
                                -alpha + rotationAngle, alpha * 2, Arc2D.OPEN );

                    // Draw the arc in the buffered image
                    g2DBuffImg.setColor( s_lineColor[colorIndex] );
                    g2DBuffImg.draw( arc );

                    // Enable these lines to anti-alias the arcs. Note that this degrades performance
                    //                arc.x = arc.getX() - 1;
                    //                g.draw( arc );
                }
            }
        }
        // Draw a gray rectangle to cover all the waves that are behind the speaker, so they won't show
        // up when the speaker moves
        g2DBuffImg.setColor( SoundConfig.MIDDLE_GRAY );
        g2DBuffImg.fillRect( 0, 0, 190, buffImg.getHeight() );

        g.drawImage( buffImg, nopAT, null );

        g.setStroke( oldStroke );
        g.setColor( oldColor );
        g.setComposite( incomingComposite );
    }


    /**
     *
     */
    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin( Point2D.Double location ) {
        this.origin = new Point2D.Double( location.x, location.y );
    }

    /**
     *
     */
    public void update() {
        for( int i = 0; i < amplitudes.length; i++ ) {
            amplitudes[i] = waveMedium.getAmplitudeAt( (double)i );
        }
        this.repaint();
    }

    public void setPlanar( boolean planar ) {
        isPlanar = planar;
        clear();
    }

    protected double getStroke() {
        return stroke;
    }

    protected double getRadius() {
        return radius;
    }

    protected boolean isPlanar() {
        return isPlanar;
    }

    protected BufferedImage getBuffImg() {
        return buffImg;
    }

    protected double[] getAmplitudes() {
        return amplitudes;
    }

    /* TEST */
    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }
}
