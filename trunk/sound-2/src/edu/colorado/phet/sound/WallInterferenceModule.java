/**
 * Class: WallInterferenceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 16, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;
import edu.colorado.phet.sound.view.ReflectingWallGraphic;
import edu.colorado.phet.sound.view.SoundApparatusPanel;
import edu.colorado.phet.sound.view.WaveMediumGraphic;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;

import java.awt.*;
import java.awt.geom.Point2D;

public class WallInterferenceModule extends SoundModule implements SimpleObserver {


    //
    // Static fields and methods
    //
    private static double s_wallOffsetX = 170;
    private static double s_wallOffsetY = 300;
    private static double s_wallThickness = 650;
    private static double s_wallHeight = 2;
    private static double s_initialWallAngle = 60;


    private SoundModel soundModel;
    private ReflectingWallGraphic wallGraphic;
    private double wallAngle = s_initialWallAngle;
    private Point2D.Double wallLocation;
    private WaveMediumGraphic interferringWaverfrontGraphic;
    private Point2D.Double p;
    private Point2D.Double pp;

    public WallInterferenceModule( ApplicationModel appModel ) {
        super( appModel, "<html>Interference<br>by Reflection</html>" );

        soundModel = (SoundModel)getModel();

        // Start with a SingleSourceApparatus Panel. This gives us a speaker and
        // WaveMediumGraphic
        setApparatusPanel( new SingleSourceApparatusPanel( soundModel ) );

        // Set up the wall
        wallLocation = new Point2D.Double( s_wallOffsetX, s_wallOffsetY );
        wallGraphic = new ReflectingWallGraphic( getApparatusPanel(), Color.blue,
                                                 SoundConfig.s_wavefrontBaseX + wallLocation.getX(),
                                                 SoundConfig.s_wavefrontBaseY + wallLocation.getY(),
                                                 s_wallThickness,
                                                 s_wallHeight,
                                                 s_initialWallAngle );
        addGraphic( wallGraphic, 7 );

        // Set up the interferring wavefront graphic
        interferringWaverfrontGraphic = new WaveMediumGraphic( soundModel.getWaveMedium(),
                                                               getApparatusPanel() );
        WaveMedium wm = soundModel.getWaveMedium();
        wm.addObserver( interferringWaverfrontGraphic );
        this.addGraphic( interferringWaverfrontGraphic, 7 );
        positionInterferingWavefront();
        soundModel.getPrimaryWavefront().addObserver( this );
    }

    /**
     *
     */
    private void positionInterferingWavefront() {
        // Set up the wavefront graphic for the reflected wave front. We make set its origin to be the apparent
        // position of the real wavefront source's reflection in the wall.
        p = new Point2D.Double( SoundConfig.s_wavefrontBaseX, SoundConfig.s_wavefrontBaseY );
        pp = MathUtil.reflectPointHorizontal( p,
                                              new Point2D.Double( wallGraphic.getMidPoint().getX(),
                                                                  wallGraphic.getMidPoint().getY()),
                                              wallAngle );
        interferringWaverfrontGraphic.clear();
        interferringWaverfrontGraphic.initLayout( new Point2D.Double( pp.getX(), pp.getY() ),
                                                  SoundConfig.s_wavefrontHeight,
                                                  SoundConfig.s_wavefrontRadius,
                                                  wallAngle * 2 );
        interferringWaverfrontGraphic.setOpacity( 0.5f );
    }

    /**
     *
     * @param theta
     */
    public void setWallAngle( float theta ) {
        wallAngle = theta;
        wallGraphic.setAngle( wallAngle );
        positionInterferingWavefront();
    }

    /**
     *
     * @param x
     */
    public void setWallLocation( float x ) {
        wallLocation.setLocation( x, wallLocation.getY() );
        wallGraphic.setLocation( SoundConfig.s_wavefrontBaseX + x );
        positionInterferingWavefront();
    }

    public void update() {
    }
}
