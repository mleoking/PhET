/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 3:01:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.HotAirBalloon;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class HotAirBalloonGraphic extends PhetGraphic implements SimpleObserver {

    private static float s_strokeWidth = 2.0F;
    private static Stroke s_defaultStroke = new BasicStroke( s_strokeWidth );
    private static Color s_defaultColor = Color.ORANGE;
    private static float s_balloonOpacity = 0.3f;

    private Arc2D.Double balloonShape = new Arc2D.Double( Arc2D.CHORD );
    private Rectangle bounds = new Rectangle();
    private BufferedImage burner;
    private BufferedImage flames;
    private int flameHeight;
    private HotAirBalloon balloon;
    private ImageObserver imgObs;

    public HotAirBalloonGraphic( Component component, HotAirBalloon balloon ) {
        super( component );
        this.balloon = balloon;
        balloon.addObserver( this );
        try {
            burner = ImageLoader.loadBufferedImage( IdealGasConfig.STOVE_IMAGE_FILE );
            flames = ImageLoader.loadBufferedImage( IdealGasConfig.HOT_AIR_BALLOON_FLAMES_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        imgObs = new ImageObserver() {
            public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
                return false;
            }
        };

        // Initialize the graphic
        update();

        super.setIgnoreMouse( true );
    }

    protected Rectangle determineBounds() {
        bounds.setBounds( (int)( balloonShape.getMinX() - s_strokeWidth / 2 ),
                          (int)( balloonShape.getMinY() - s_strokeWidth / 2 ),
                          (int)( balloonShape.getWidth() + 2 * s_strokeWidth ),
                          (int)( balloonShape.getHeight() + burner.getHeight() + s_strokeWidth / 2 ) );
        return bounds;
    }

    public void update() {
        this.setPosition();
        flameHeight = (int)balloon.getHeatSource() * 2 / 3;
        setBoundsDirty();
        repaint();
    }

    /**
     *
     */
    protected void setPosition() {
        double start = -( 90 - ( balloon.getOpeningAngle() / 2 ) );
        double end = 360 - balloon.getOpeningAngle();
        balloonShape.setArc( balloon.getCenter().getX() - balloon.getRadius(),
                             balloon.getCenter().getY() - balloon.getRadius(),
                             balloon.getRadius() * 2, balloon.getRadius() * 2,
                             start, end,
                             Arc2D.CHORD );
    }

    public void paint( Graphics2D g ) {

        saveGraphicsState( g );

        GraphicsUtil.setAntiAliasingOn( g );
        g.setStroke( s_defaultStroke );
        g.setColor( s_defaultColor );
        g.draw( balloonShape );
        GraphicsUtil.setAlpha( g, s_balloonOpacity );
        g.fill( balloonShape );

        GraphicsUtil.setAlpha( g, 1 );
        Rectangle2D opening = balloon.getOpening();
        int centerX = (int)( opening.getMaxX() + opening.getMinX() ) / 2;
        int stoveWidth = (int)( ( opening.getMaxX() - opening.getMinX() ) ) * 2 / 3;
        int flameWidth = stoveWidth * 2 / 3;
        g.drawImage( flames,
                     centerX - flameWidth / 2, (int)opening.getMinY() - flameHeight,
                     flameWidth,
                     flameHeight,
                     imgObs );
        g.drawImage( burner,
                     centerX - stoveWidth / 2, (int)opening.getMinY(),
                     stoveWidth,
                     (int)( opening.getMaxY() - opening.getMinY() ) / 2,
                     imgObs );

        restoreGraphicsState();
    }
}
