/**
 * Class: WaveMediumGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.model.Wavefront;

/**
 * This variant of WaveMediumGraphic is the one used in the non-interefernce modules of the sound simulation.
 */
public class WaveMediumGraphic extends PhetImageGraphic implements SimpleObserver {

    // An array of gray scale values
    private static Color[] s_lineColor = new Color[256];

    static {
        for( int i = 0; i < s_lineColor.length; i++ ) {
            s_lineColor[i] = new Color( i, i, i );
        }
    }

    // Note that larger values for the stroke slow down performance considerably
    private static Stroke s_defaultStroke = new BasicStroke( 1.0F );


    // TODO: This should be set by a call to initLayout, not here.
    private Point2D.Double origin = new Point2D.Double( SoundConfig.s_wavefrontBaseX, SoundConfig.s_wavefrontBaseY );
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
    private RgbReporter rgbReporter;

    private int grayScaleZero = s_lineColor.length / 2;

    static public boolean drawTest;

    private static BufferedImage createBufferedImage() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage( 800, 800 );
        //        return gc.createCompatibleImage( 300, 200 );
    }

    /**
     * todo: rename WaveMediumGraphic
     */
    public WaveMediumGraphic( WaveMedium waveMedium, Component component, RgbReporter rgbReporter ) {
        super( component, createBufferedImage() );

        // Hook up to the WaveMedium we are observing
        this.waveMedium = waveMedium;
        this.rgbReporter = rgbReporter;
        waveMedium.addObserver( this );

        buffImg = super.getImage();
        g2DBuffImg = buffImg.createGraphics();
        g2DBuffImg.setColor( SoundConfig.MIDDLE_GRAY );
        g2DBuffImg.setColor( new Color( 0, 0, 0, 0 ) );
        //        Color transparent = new Color(0, 0, 0, 0);
        //            g2d.setColor(transparent);
        //            g2d.setComposite(AlphaComposite.Src);
        //            g2d.fill(new Rectangle2D.Float(20, 20, 100, 20));

        g2DBuffImg.fill( new Rectangle( buffImg.getMinX(), buffImg.getMinY(), buffImg.getWidth(), buffImg.getHeight() ) );
        setGraphicsHints( g2DBuffImg );

        // Set the clip
        GeneralPath clip = new GeneralPath();
        clip.moveTo( (float)origin.getX(), (float)origin.getY() );
        double x1 = waveMedium.getMaxX() + origin.getX();
        double alpha = Math.asin( height / ( 2 * radius ) );
        double dy = waveMedium.getMaxX() * Math.tan( alpha );
        clip.lineTo( (float)x1, (float)( origin.getY() - dy ) );
        clip.lineTo( (float)x1, (float)( origin.getY() + dy ) );
        clip.closePath();
        //        g2DBuffImg.setClip( clip );
    }

    /**
     * Gets the color corresponding to a particular amplitude at a particular point. The idea is to
     * match the zero pressure point in the wave medium to the background color reported by the
     * rgbReporter
     *
     * @param amplitude
     * @param x
     * @param y
     * @return
     */
    Color getColorForAmplitude( double amplitude, int x, int y ) {
        int zeroGray = rgbReporter.rgbAt( x, y );
        double normalizedAmplitude = amplitude / SoundConfig.s_maxAmplitude * zeroGray;
        int colorIndex = Math.min( (int)( ( normalizedAmplitude ) + zeroGray ), s_lineColor.length - 1 );
        return s_lineColor[colorIndex];
    }

    private void setGraphicsHints( Graphics2D g2 ) {
        g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
        //        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g2.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE );
        g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
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
     *
     */
    public void initLayout( Point2D.Double origin, double height, double radius,
                            double theta ) {
        this.origin = origin;
        this.height = height;
        this.radius = radius;
        setRotationAngle( theta );
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

        this.setGraphicsHints( g );

        // Set opacity
        Composite incomingComposite = g.getComposite();
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) );

        g.setStroke( s_defaultStroke );

        Point2D end1 = new Point2D.Float();
        Point2D end2 = new Point2D.Float();
        Line2D line = new Line2D.Float();

        float alpha = (float)( Math.asin( height / ( 2 * radius ) ) * ( 180 / Math.PI ) );
        Arc2D.Float arc = new Arc2D.Float();

        // Compute stuff we need to calculate the arc.
        double arcHt = height - 20;
        double rad2 = radius - 20;

        // TODO: For some reason, this has to be set here. If you just do it in setRotationAngle()
        // it doesn't work right
        lineRotationXform = AffineTransform.getRotateInstance( -Math.toRadians( rotationAngle ),
                                                               origin.getX(), origin.getY() );
        if( drawTest ) {
            int dx = 2;
            int dy = 2;
            Graphics2D gBi = (Graphics2D)buffImg.getGraphics();
            for( int x = (int)rad2; x < waveMedium.getMaxX(); x += dx ) {
                double yStart = x * Math.tan( Math.toRadians( -alpha + rotationAngle ) );
                double yStop = x * Math.tan( Math.toRadians( alpha + rotationAngle ) );
                for( int y = (int)yStart; y < (int)yStop; y += dy ) {
                    double distSq = ( x * x ) + ( y * y );
                    int dist = (int)Math.sqrt( distSq );
                    if( dist < waveMedium.getMaxX() ) {
                        double normalizedAmplitude = amplitudes[dist] / SoundConfig.s_maxAmplitude * grayScaleZero;
//                        double normalizedAmplitude = amplitudes[dist] / SoundConfig.s_maxAmplitude * getGrayScaleZero();
                        int colorIndex = (int)( ( normalizedAmplitude ) + s_lineColor.length / 2 );
                        gBi.setColor( s_lineColor[colorIndex] );
//                        gBi.setColor( getColorForAmplitude( amplitudes[dist], x, y ) );
                        gBi.fillRect( (int)origin.getX() + x, (int)origin.getY() + y, dx, dy );
                    }
                }
            }
            g.drawImage( buffImg, nopAT, null );
        }
        else {

            // Draw a line or arc for each value in the amplitude array of the wave front
            for( int i = 0; i < waveMedium.getMaxX(); i++ ) {

                // Negative in front of amplitude is to make black indicate high pressure, and white
                // indicate low pressure
//                double normalizedAmplitude = amplitudes[i] / SoundConfig.s_maxAmplitude * getGrayScaleZero();
//                int colorIndex = (int)( ( normalizedAmplitude ) + grayScaleZero );
//                g.setColor( s_lineColor[colorIndex] );
                g.setColor( getColorForAmplitude( amplitudes[i], (int)( i * stroke ), 0 ) );
                if( this.isPlanar ) {
                    end1.setLocation( origin.getX() + rad2 + ( i * stroke ), origin.getY() - height / 2 );
                    end2.setLocation( origin.getX() + rad2 + ( i * stroke ), origin.getY() + height / 2 );
                    line.setLine( end1, end2 );
                    Shape xformedLine = lineRotationXform.createTransformedShape( line );
                    g.draw( xformedLine );
//                                        g2DBuffImg.draw( xformedLine );
                }
                else {
                    arc.setArc( origin.getX() - rad2 - ( i * stroke ),
                                origin.getY() - rad2 - ( i * stroke ),
                                rad2 * 2 + ( i * stroke * 2 ),
                                rad2 * 2 + ( i * stroke * 2 ),
                                -alpha + rotationAngle, alpha * 2, Arc2D.OPEN );
                    g.draw( arc );
//                                        g2DBuffImg.draw( arc );
                }
            }
        }
//                g.drawImage( buffImg, nopAT, null );
        g.setComposite( incomingComposite );
    }

    /**
     *
     */
    public Point2D.Double getOrigin() {
        return origin;
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

}
