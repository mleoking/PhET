/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 3:01:39 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.graphics.ShapeGraphic;
import edu.colorado.phet.graphics.MovableImageGraphic;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.idealgas.physics.HotAirBalloon;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;

import java.util.Observable;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.awt.image.ImageObserver;

public class HotAirBalloonGraphic extends ShapeGraphic implements ImageObserver {

    private Arc2D.Float rep = new Arc2D.Float( Arc2D.CHORD );
    private Image burner;
    private Image flames;
    private int flameHeight;

    public HotAirBalloonGraphic() {
        super( s_defaultColor, s_defaultStroke );
        init();
    }

    /**
     *
     */
    private void init() {
        this.setFill( s_defaultColor );
        this.setOpacity( 0.3f );
        this.setRep( rep );

        ResourceLoader loader = new ResourceLoader();
        burner = loader.loadImage( IdealGasConfig.STOVE_IMAGE_FILE ).getImage();
        flames = loader.loadImage( IdealGasConfig.HOT_AIR_BALLOON_FLAMES_IMAGE_FILE ).getImage();
    }

    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }

    /**
     *
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        this.setPosition( (CollidableBody)o );
        if( o instanceof HotAirBalloon ) {
            HotAirBalloon balloon = (HotAirBalloon)o;
            flameHeight = (int)balloon.s_heatSource * 2 / 3;
        }
    }

    /**
     *
     * @param body
     */
    protected void setPosition( Particle body ) {
        HotAirBalloon balloon = (HotAirBalloon)body;
        double start = -( 90 - ( balloon.getOpeningAngle() / 2 ));
        double end = 360 - balloon.getOpeningAngle();
        rep.setArc( balloon.getCenter().getX() - balloon.getRadius(),
                    balloon.getCenter().getY() - balloon.getRadius(),
                    balloon.getRadius() * 2, balloon.getRadius() * 2,
                    start, end,
                    Arc2D.CHORD );
    }

    public void paint( Graphics2D g ) {
        super.paint( g );

        HotAirBalloon balloon = (HotAirBalloon)this.getBody();
        Rectangle2D.Float opening = balloon.getOpening();
        int centerX = (int)( opening.getMaxX() + opening.getMinX() ) / 2;
        int stoveWidth = (int)(( opening.getMaxX() - opening.getMinX() )) * 2 / 3;
        int flameWidth = stoveWidth * 2 / 3;
        g.drawImage( flames,
                     centerX - flameWidth / 2, (int)opening.getMinY() - flameHeight,
                     flameWidth,
                     flameHeight,
                     this );
        g.drawImage( burner,
                     centerX - stoveWidth / 2, (int)opening.getMinY(),
                     stoveWidth,
                     (int)(opening.getMaxY() - opening.getMinY()) / 2,
                     this );
    }

    //
    // Static fields and methods
    //
    private static Stroke s_defaultStroke = new BasicStroke( 2.0F );
    private static Color s_defaultColor = Color.ORANGE;
//    private static Color s_defaultColor = Color.PINK;
}
