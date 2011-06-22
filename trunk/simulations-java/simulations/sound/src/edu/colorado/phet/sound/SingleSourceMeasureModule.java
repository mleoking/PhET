// Copyright 2002-2011, University of Colorado

/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.help.HelpItem;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.sound.view.ClockPanelLarge;
import edu.colorado.phet.sound.view.MeasureControlPanel;
import edu.colorado.phet.sound.view.MeterStickGraphic;
import edu.colorado.phet.sound.view.VerticalGuideline;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Provides a single speaker and instruments for making measurements on waves.
 */
public class SingleSourceMeasureModule extends SingleSourceModule {

    private static final int s_guidelineBaseX = 70;
    private JDialog stopwatchDlg;
    private final IClock clock;
    private final Point dragPoint = new Point();

    /**
     * @param application
     */
    protected SingleSourceMeasureModule( PhetApplication application ) {
        super( SoundResources.getString( "ModuleTitle.SingleSourceMeasure" ) );

        this.clock = getClock();

        ApparatusPanel apparatusPanel = (ApparatusPanel) getSimulationPanel();

        // Add the ruler
        try {

            //To provide internationalization, use the same image that was provided with the sim to ensure measurements are correct
            //Then draw on top of it with a translatable PText
            final BufferedImage bi = ImageLoader.loadBufferedImage( SoundConfig.METER_STICK_IMAGE_FILE );
            PText unitsLabel = new PText( SoundResources.getString( "Units.Meters" ) ) {{

                //Right justify with the last number in the image, and move the baseline down a little bit so it lines up with the image in English
                setOffset( 245, bi.getHeight() / 2 - getFullBounds().getHeight() / 2 + 2 );
            }};
            Graphics2D g2 = bi.createGraphics();
            unitsLabel.fullPaint( new PPaintContext( g2 ) );
            g2.dispose();

            PhetImageGraphic ruler = new PhetImageGraphic( apparatusPanel, bi );
            ruler.setLocation( SoundConfig.s_meterStickBaseX, SoundConfig.s_meterStickBaseY );
            MeterStickGraphic meterStickGraphic = new MeterStickGraphic( apparatusPanel,
                                                                         ruler,
                                                                         new Point2D.Double( SoundConfig.s_meterStickBaseX,
                                                                                             SoundConfig.s_meterStickBaseY ) );
            apparatusPanel.addGraphic( meterStickGraphic, 9 );

            // Add help items
            HelpItem help1 = new HelpItem( apparatusPanel,
                                           SoundResources.getString( "SingleSourceMeasureModule.Help1" ),
                                           SoundConfig.s_meterStickBaseX,
                                           SoundConfig.s_meterStickBaseY + ruler.getImage().getHeight(),
                                           HelpItem.RIGHT, HelpItem.BELOW );
            help1.setForegroundColor( Color.white );
            addHelpItem( help1 );

            HelpItem help2 = new HelpItem( apparatusPanel,
                                           SoundResources.getString( "SingleSourceMeasureModule.Help2" ),
                                           s_guidelineBaseX + 30, 70,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help2.setForegroundColor( Color.white );
            addHelpItem( help2 );
        }
        catch ( IOException e ) {
            e.printStackTrace();

        }
        VerticalGuideline guideline1 = new VerticalGuideline( apparatusPanel, Color.blue, s_guidelineBaseX );
        apparatusPanel.addGraphic( guideline1, 10 );
        VerticalGuideline guideline2 = new VerticalGuideline( apparatusPanel, Color.blue, s_guidelineBaseX + 20 );
        apparatusPanel.addGraphic( guideline2, 10 );

        // Control Panel
        setControlPanel( new MeasureControlPanel( this ) );

        // Stopwatch window
        stopwatchDlg = new JDialog( application.getPhetFrame(), "Stopwatch", false );
        ClockPanelLarge clockPanel = new ClockPanelLarge( getSoundModel() );
        clockPanel.addListener( new StopwatchListener( this ) );
        stopwatchDlg.setContentPane( clockPanel );
        stopwatchDlg.setLocation( 0, 0 );
        stopwatchDlg.setLocationRelativeTo( application.getPhetFrame() );
        stopwatchDlg.setUndecorated( true );
        stopwatchDlg.setResizable( false );
        stopwatchDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
        stopwatchDlg.pack();
        stopwatchDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );

        // Avoid creating a point with each mousePressed() call

        //see http://www.ibm.com/developerworks/java/library/j-mer0717/
        stopwatchDlg.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                dragPoint.x = e.getX();
                dragPoint.y = e.getY();
            }
        } );
        stopwatchDlg.addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Point p = stopwatchDlg.getLocation();
                stopwatchDlg.setLocation( p.x + e.getX() - dragPoint.x, p.y + e.getY() - dragPoint.y );
            }
        } );
        clockPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

    }

    public boolean hasHelp() {
        return true;
    }

    public void activate() {
        super.activate();
        stopwatchDlg.setVisible( true );
    }

    public void deactivate() {
        super.deactivate();
        setStopwatchVisible( false );
    }

    public void setStopwatchVisible( boolean isVisible ) {
        stopwatchDlg.setVisible( isVisible );
    }

    public void clearWaves() {
        resetWaveMediumGraphic();

        //if the sim is paused, needs to sync.  This small change in time shows up on the stopwatch,
        // but hopefully is not too disturbing.  This line is necessary to update the graphics while the sim is paused.
        if ( clock.isPaused() ) { clock.stepClockWhilePaused(); }
    }

    //-----------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------
    public class StopwatchListener implements ClockPanelLarge.ClockPanelListener {
        SingleSourceMeasureModule module;

        public StopwatchListener( SingleSourceMeasureModule module ) {
            this.module = module;
        }

        public void clockPaneEventOccurred( ClockPanelLarge.ClockPanelEvent event ) {
            if ( event.isReset() ) {
                module.resetWaveMediumGraphic();
                if ( !clock.isPaused() ) {
                    clock.pause();
                }
                clock.stepClockWhilePaused();

                //this temporary workaround is to resolve a bug reported just before 7-19-2007
                //On the Measure tab. the Reset on the Stopwatch doesn't clear unless you click twice.
                clock.stepClockWhilePaused();

            }
            else {
                if ( event.isRunning() && clock.isPaused() ) {
                    SingleSourceMeasureModule.this.clock.start();
                }
                else if ( !clock.isPaused() ) {
                    SingleSourceMeasureModule.this.clock.pause();
                }
            }
        }
    }
}
