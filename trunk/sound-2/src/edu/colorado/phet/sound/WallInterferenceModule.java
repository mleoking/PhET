/**
 * Class: WallInterferenceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 16, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.view.ReflectingWallGraphic;
import edu.colorado.phet.sound.view.SingleSourceApparatusPanel;
import edu.colorado.phet.sound.view.WaveMediumGraphic;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class WallInterferenceModule extends SoundModule {


    //
    // Static fields and methods
    //
    private static double s_wallOffsetX = 170;
    private static double s_wallOffsetY = 300;
    private static double s_wallThickness = 650;
    private static double s_wallHeight = 6;
    private static double s_initialWallAngle = 60;


    private SoundModel soundModel;
    private ReflectingWallGraphic wallGraphic;
    private double wallAngle = s_initialWallAngle;
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
        wallGraphic = new ReflectingWallGraphic( getApparatusPanel(), Color.blue,
                                                 SoundConfig.s_wavefrontBaseX + s_wallOffsetX,
                                                 SoundConfig.s_wavefrontBaseY + s_wallOffsetY,
                                                 s_wallThickness,
                                                 s_wallHeight,
                                                 s_initialWallAngle );

        DefaultInteractiveGraphic interactiveWallGraphic = new InteractiveWallGraphic( wallGraphic );
        //        interactiveWallGraphic.addMouseInputListener( new );
        interactiveWallGraphic.addCursorHandBehavior();
        interactiveWallGraphic.addTranslationBehavior( new WallTranslator( wallGraphic ) );
        addGraphic( interactiveWallGraphic, 8 );

        // Set up the interferring wavefront graphic
        interferringWaverfrontGraphic = new WaveMediumGraphic( soundModel.getWaveMedium(),
                                                               getApparatusPanel(), this );
        this.addGraphic( interferringWaverfrontGraphic, 7 );
        positionInterferingWavefront();

        // Create a control panel element for the wall
        WallTiltControlPanel wallControlPanel = new WallTiltControlPanel();
        JPanel panel = new JPanel( new GridLayout( 2, 1 ) );
        panel.add( wallControlPanel );
        panel.add( new WallTranslateControlPanel() );
        PhetControlPanel controlPanel = new PhetControlPanel( this, panel );
        setControlPanel( controlPanel );
    }

    private class InteractiveWallGraphic extends DefaultInteractiveGraphic {
        private ReflectingWallGraphic reflectingWallGraphic;
        private Cursor savedCursor;

        public InteractiveWallGraphic( ReflectingWallGraphic wallGraphic ) {
            super( wallGraphic );
            this.reflectingWallGraphic = wallGraphic;
            addMouseInputListener( new MouseInputAdapter() {

            } );
        }

        public void mouseEntered( MouseEvent e ) {
            super.mouseEntered( e );
            reflectingWallGraphic.setDisplayHelpOrnaments( true );
            if( reflectingWallGraphic.getMidPoint().distance( e.getPoint() ) < 100 ) {
                savedCursor = getApparatusPanel().getCursor();
                getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
            }
            //            else {
            //                savedCursor = getApparatusPanel().getCursor();
            //                getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.NE_RESIZE_CURSOR ));
            //            }
        }

        public void mouseExited( MouseEvent e ) {
            super.mouseExited( e );
            reflectingWallGraphic.setDisplayHelpOrnaments( false );
            if( savedCursor != null ) {
                getApparatusPanel().setCursor( savedCursor );
            }
        }
    }

    public class WallTiltControlPanel extends JPanel {

        WallTiltControlPanel() {
            setLayout( new GridLayout( 1, 1 ) );
            setPreferredSize( new Dimension( 125, 100 ) );

            final JSlider wallAngleSlider = new JSlider( JSlider.VERTICAL,
                                                         10,
                                                         90,
                                                         40 );
            wallAngleSlider.setPreferredSize( new Dimension( 25, 100 ) );
            wallAngleSlider.setPaintTicks( true );
            wallAngleSlider.setMajorTickSpacing( 10 );
            wallAngleSlider.setMinorTickSpacing( 5 );
            wallAngleSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    WallInterferenceModule.this.setWallAngle( wallAngleSlider.getValue() );
                }
            } );

            add( wallAngleSlider );

            Border amplitudeBorder = new TitledBorder( "Wall Angle" );
            setBorder( amplitudeBorder );
        }
    }

    public class WallTranslateControlPanel extends JPanel {

        WallTranslateControlPanel() {
            setLayout( new GridLayout( 1, 1 ) );
            setPreferredSize( new Dimension( 125, 70 ) );

            final JSlider wallTranslationSlider = new JSlider( JSlider.VERTICAL,
                                                               0,
                                                               400,
                                                               100 );
            wallTranslationSlider.setPreferredSize( new Dimension( 25, 100 ) );
            wallTranslationSlider.setPaintTicks( true );
            wallTranslationSlider.setMajorTickSpacing( 10 );
            wallTranslationSlider.setMinorTickSpacing( 5 );
            wallTranslationSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setWallLocation( wallTranslationSlider.getValue() );
                }
            } );

            add( wallTranslationSlider );

            Border amplitudeBorder = new TitledBorder( "Wall Position" );
            setBorder( amplitudeBorder );
        }
    }

    private class WallTranslator implements Translatable {
        private ReflectingWallGraphic graphic;

        WallTranslator( ReflectingWallGraphic graphic ) {
            this.graphic = graphic;
        }

        public void translate( double dx, double dy ) {
            //            double beta = Math.atan2( dy, dx );
            //            dx *= Math.sin( beta );
            //            dx = Math.sqrt( dx*dx + dy*dy) * MathUtil.getSign( dx );

            dx -= SoundConfig.s_wavefrontBaseX;
            setWallLocation( (float)( wallGraphic.getLocation() + dx ) );

            //            Point2D.Double mp = wallGraphic.getMidPoint();
            //            double theta = Math.atan2( dy, dx );
            //            System.out.println( "theta = " + theta );
            //            setWallAngle( (float)Math.toDegrees( theta ));

            //            WallInterferenceModule.this.setWallLocation( (float)( graphic.getPosition().getX() + dx ));

            //            Point position = new Point( graphic.getPosition() );
            //            graphic.setLocation( position.x + dx );
            //            positionInterferingWavefront();
        }
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
                                                                  wallGraphic.getMidPoint().getY() ),
                                              wallAngle );
        interferringWaverfrontGraphic.clear();
        interferringWaverfrontGraphic.initLayout( new Point2D.Double( pp.getX(), pp.getY() ),
                                                  SoundConfig.s_wavefrontHeight,
                                                  SoundConfig.s_wavefrontRadius,
                                                  wallAngle * 2 );
        interferringWaverfrontGraphic.setOpacity( 0.5f );
    }

    /**
     * @param theta
     */
    public void setWallAngle( float theta ) {
        wallAngle = theta;
        wallGraphic.setAngle( wallAngle );
        positionInterferingWavefront();
    }

    /**
     * @param x
     */
    public void setWallLocation( float x ) {
        wallGraphic.setLocation( SoundConfig.s_wavefrontBaseX + x );
        positionInterferingWavefront();
    }
}
