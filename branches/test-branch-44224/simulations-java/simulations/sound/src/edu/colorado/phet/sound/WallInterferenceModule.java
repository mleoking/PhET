/**
 * Class: WallInterferenceModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 16, 2004
 */
package edu.colorado.phet.sound;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.view.BufferedWaveMediumGraphic;
import edu.colorado.phet.sound.view.ReflectingWallGraphic;
import edu.colorado.phet.sound.view.SoundControlPanel;

public class WallInterferenceModule extends SingleSourceModule {

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
    private BufferedWaveMediumGraphic interferringWavefrontGraphic;
    private Point2D.Double p;
    private Point2D.Double pp;
    private final IClock clock;

    public WallInterferenceModule() {
        super( SoundResources.getString( "ModuleTitle.WallInterference" ) );

        soundModel = getSoundModel();
        this.clock = getClock();
        
        ApparatusPanel apparatusPanel = (ApparatusPanel)getSimulationPanel();

        // Set up the wall
        wallGraphic = new ReflectingWallGraphic( getSimulationPanel(), Color.blue,
                                                 SoundConfig.s_wavefrontBaseX + s_wallOffsetX,
                                                 SoundConfig.s_wavefrontBaseY + s_wallOffsetY,
                                                 s_wallThickness,
                                                 s_wallHeight,
                                                 s_initialWallAngle );
        apparatusPanel.addGraphic( wallGraphic, 8 );

        // Set up the interferring wavefront graphic
        interferringWavefrontGraphic = new BufferedWaveMediumGraphic( soundModel.getWaveMedium(), apparatusPanel );
        apparatusPanel.addGraphic( interferringWavefrontGraphic, 7 );
        positionInterferingWavefront();

        // Create a control panel element for the wall
        WallTiltControlPanel wallControlPanel = new WallTiltControlPanel();
        JPanel panel = new JPanel( new GridLayout( 2, 1 ) );
        panel.add( wallControlPanel );
        panel.add( new WallTranslateControlPanel() );
        ( (SoundControlPanel)getControlPanel() ).addPanel( panel );
        ( (SoundControlPanel)getControlPanel() ).setAmplitude( 1.0 );
        ( (SoundControlPanel)getControlPanel() ).addPanel( new PulsePanel() );

        apparatusPanel.invalidate();
        apparatusPanel.repaint();
    }

    /**
     *
     */
    private void positionInterferingWavefront() {
        // Set up the wavefront graphic for the reflected wave front. We make set its origin to be the apparent
        // position of the real wavefront source's reflection in the wall. Note that the angle must be set negative
        // because of the direction of the y axis in AWT.
        p = new Point2D.Double( SoundConfig.s_wavefrontBaseX, SoundConfig.s_wavefrontBaseY );
        Point2D pTest = MathUtil.reflectPointAcrossLine( p, new Point2D.Double( wallGraphic.getMidPoint().getX(),
                                                                                wallGraphic.getMidPoint().getY() ),
                                                         Math.toRadians( -wallAngle ) );
        pp = (Point2D.Double)pTest;
        interferringWavefrontGraphic.clear();
        interferringWavefrontGraphic.initLayout( new Point2D.Double( pp.getX(), pp.getY() ),
                                                 SoundConfig.s_wavefrontHeight,
                                                 SoundConfig.s_wavefrontRadius,
                                                 wallAngle * 2 );
        interferringWavefrontGraphic.setOpacity( 0.5f );
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

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

//    private class InteractiveWallGraphic extends DefaultInteractiveGraphic {
//        private ReflectingWallGraphic reflectingWallGraphic;
//        private Cursor savedCursor;
//
//        public InteractiveWallGraphic( ReflectingWallGraphic wallGraphic ) {
//            super( wallGraphic );
//            this.reflectingWallGraphic = wallGraphic;
//            addMouseInputListener( new MouseInputAdapter() {
//
//            } );
//        }
//
//        public void mouseEntered( MouseEvent e ) {
//            super.mouseEntered( e );
//            reflectingWallGraphic.setDisplayHelpOrnaments( true );
//            if( reflectingWallGraphic.getMidPoint().distance( e.getPoint() ) < 100 ) {
//                savedCursor = getApparatusPanel().getCursor();
//                getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
//            }
//        }
//
//        public void mouseExited( MouseEvent e ) {
//            super.mouseExited( e );
//            reflectingWallGraphic.setDisplayHelpOrnaments( false );
//            if( savedCursor != null ) {
//                getApparatusPanel().setCursor( savedCursor );
//            }
//        }
//    }

    public class WallTiltControlPanel extends JPanel {

        WallTiltControlPanel() {
            setLayout( new GridLayout( 1, 1 ) );
            setPreferredSize( new Dimension( 125, 100 ) );

            final JSlider wallAngleSlider = new JSlider( JSlider.VERTICAL,
                                                         10,
                                                         90,
                                                         (int)s_initialWallAngle );
            wallAngleSlider.setPreferredSize( new Dimension( 25, 100 ) );
            wallAngleSlider.setPaintTicks( true );
            wallAngleSlider.setMajorTickSpacing( 20 );
            wallAngleSlider.setMinorTickSpacing( 10 );
            wallAngleSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    WallInterferenceModule.this.setWallAngle( wallAngleSlider.getValue() );
                }
            } );

            add( wallAngleSlider );

            Border amplitudeBorder = new TitledBorder( SoundResources.getString( "WallInterferenceModule.Angle" ) );
            setBorder( amplitudeBorder );
        }
    }

    public class WallTranslateControlPanel extends JPanel {

        WallTranslateControlPanel() {
            setLayout( new GridLayout( 1, 1 ) );
            setPreferredSize( new Dimension( 125, 70 ) );

            final JSlider wallTranslationSlider = new JSlider( JSlider.HORIZONTAL,
                                                               0,
                                                               400,
                                                               (int)s_wallOffsetX );
            wallTranslationSlider.setPreferredSize( new Dimension( 25, 100 ) );
            wallTranslationSlider.setPaintTicks( true );
            wallTranslationSlider.setMajorTickSpacing( 50 );
            wallTranslationSlider.setMinorTickSpacing( 25 );
            wallTranslationSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setWallLocation( wallTranslationSlider.getValue() );
                }
            } );

            add( wallTranslationSlider );

            Border amplitudeBorder = new TitledBorder( SoundResources.getString( "WallInterferenceModule.Position" ) );
            setBorder( amplitudeBorder );
        }
    }

//    private class WallTranslator implements Translatable {
//        private ReflectingWallGraphic graphic;
//
//        WallTranslator( ReflectingWallGraphic graphic ) {
//            this.graphic = graphic;
//        }
//
//        public void translate( double dx, double dy ) {
//            dx -= SoundConfig.s_wavefrontBaseX;
//            setWallLocation( (float)( wallGraphic.getLocation() + dx ) );
//        }
//    }

    private class PulsePanel extends JPanel {
        private JButton pulseBtn;
        private Double savedAmplitude;

        public PulsePanel() {
            super( new GridBagLayout() );

            setBorder( BorderFactory.createTitledBorder( new EtchedBorder(),
                                                         SoundResources.getString( "WallInterferenceMode.modeCtrlTitle" ) ) );

            ButtonGroup btnGrp = new ButtonGroup();
            final JRadioButton continuousModeBtn = new JRadioButton( SoundResources.getString( "WallInterferenceMode.continuous" ) );
            final JRadioButton pulseModeBtn = new JRadioButton( SoundResources.getString( "WallInterferenceMode.pulse" ) );
            btnGrp.add( continuousModeBtn );
            btnGrp.add( pulseModeBtn );

            JPanel buttonPanel = new JPanel( new GridBagLayout() );
            buttonPanel.setBorder( BorderFactory.createTitledBorder( new EtchedBorder(),
                                                                     SoundResources.getString( "WallInterferenceMode.modeTitle" ) ) );
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets( 0, 20, 0, 20 );
            buttonPanel.add( continuousModeBtn, gbc );
            gbc.gridx++;
            buttonPanel.add( pulseModeBtn, gbc );

            pulseBtn = new JButton( SoundResources.getString( "WallInterferenceMode.fire" ) );
            pulseBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    producePulse();
                }
            } );

            gbc.gridy++;
            buttonPanel.add( pulseBtn, gbc );


            gbc.insets = new Insets( 0, 0, 0, 0 );
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            add( buttonPanel, gbc );
            gbc.gridy++;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            add( pulseBtn, gbc );

            continuousModeBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( continuousModeBtn.isSelected() ) {
                        setPulseMode( false );
                    }
                }
            } );

            pulseModeBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( pulseModeBtn.isSelected() ) {
                        setPulseMode( true );
                    }
                }
            } );
            setPulseMode( false );
            continuousModeBtn.setSelected( true );
        }

        private void setPulseMode( boolean isEnabled ) {
            pulseBtn.setEnabled( isEnabled );
            if( isEnabled ) {
                savedAmplitude = new Double( soundModel.getPrimaryWavefront().getMaxAmplitude() );
                soundModel.getPrimaryWavefront().clear();
                soundModel.getPrimaryWavefront().setMaxAmplitude( 0 );
            }
            else {
                if( savedAmplitude != null ) {
                    soundModel.getPrimaryWavefront().setMaxAmplitude( savedAmplitude.doubleValue() );
                }
            }
        }

        private void producePulse() {
            Runnable pulser = new Runnable() {
                public void run() {
                    soundModel.getPrimaryWavefront().setMaxAmplitude( savedAmplitude.doubleValue() );
                    double startTime = clock.getSimulationTime();
                    double cycleTime = 6 * 1 / soundModel.getPrimaryWavefront().getFrequency();
                    soundModel.getPrimaryWavefront().getAmplitude();
                    while( clock.getSimulationTime() - startTime < cycleTime ) {
                        // wait loop
                        try {
                            Thread.sleep( 10 );
                        }
                        catch( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                    soundModel.getPrimaryWavefront().setMaxAmplitude( 0 );
                }
            };
            Thread t = new Thread( pulser );
            t.start();
        }
    }
}
