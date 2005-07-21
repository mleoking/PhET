/**
 * Class: SingleSourceListenModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.sound.view.ClockPanelLarge;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.sound.view.MeasureControlPanel;
import edu.colorado.phet.sound.view.MeterStickGraphic;
import edu.colorado.phet.sound.view.VerticalGuideline;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Provides a single speaker and instruments for making measurements on waves.
 */
public class SingleSourceMeasureModule extends SingleSourceModule {

    private static final int s_guidelineBaseX = 70;
    private JDialog stopwatchDlg;
    private AbstractClock clock;
    private boolean closkWasPausedOnActivate;

    /**
     * @param appModel
     */
    protected SingleSourceMeasureModule( ApplicationModel appModel ) {
        super( appModel, SimStrings.get( "ModuleTitle.SingleSourceMeasure" ) );

        clock = appModel.getClock();

        // Add the ruler
        try {
            BufferedImage bi = ImageLoader.loadBufferedImage( SoundConfig.METER_STICK_IMAGE_FILE );
            PhetImageGraphic ruler = new PhetImageGraphic( getApparatusPanel(), bi );
            ruler.setPosition( SoundConfig.s_meterStickBaseX, SoundConfig.s_meterStickBaseY );
            MeterStickGraphic meterStickGraphic = new MeterStickGraphic( getApparatusPanel(), ruler,
                                                                         new Point2D.Double( 200, 100 ) );
            this.addGraphic( meterStickGraphic, 9 );

            // Add help items
            HelpItem help1 = new HelpItem( SimStrings.get( "SingleSourceMeasureModule.Help1" ),
                                           200, 100 + ruler.getImage().getHeight(),
                                           HelpItem.RIGHT, HelpItem.BELOW );
            help1.setForegroundColor( Color.white );
            addHelpItem( help1 );

            HelpItem help2 = new HelpItem( SimStrings.get( "SingleSourceMeasureModule.Help2" ),
                                           s_guidelineBaseX + 30, 70,
                                           HelpItem.RIGHT, HelpItem.ABOVE );
            help2.setForegroundColor( Color.white );
            addHelpItem( help2 );
        }
        catch( IOException e ) {
            e.printStackTrace();

        }
        VerticalGuideline guideline1 = new VerticalGuideline( getApparatusPanel(), Color.blue, s_guidelineBaseX );
        this.addGraphic( guideline1, 10 );
        VerticalGuideline guideline2 = new VerticalGuideline( getApparatusPanel(), Color.blue, s_guidelineBaseX + 20 );
        this.addGraphic( guideline2, 10 );

        // Control Panel
        setControlPanel( new MeasureControlPanel( this, appModel.getClock() ) );

        // Stopwatch window
        stopwatchDlg = new JDialog( appModel.getFrame(), "Stopwatch", false );
        ClockPanelLarge clockPanel = new ClockPanelLarge( getModel() );
//        ClockPanelLarge clockPanel = new ClockPanelLarge( appModel.getClock() );
        clockPanel.addListener( new StopwatchListener( this ) );
        stopwatchDlg.setContentPane( clockPanel );
        stopwatchDlg.setLocation( 0, 0 );
        stopwatchDlg.setLocationRelativeTo( appModel.getFrame() );
        stopwatchDlg.setUndecorated( true );
        stopwatchDlg.setResizable( false );
        stopwatchDlg.getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
        stopwatchDlg.pack();
        stopwatchDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        closkWasPausedOnActivate = clock.isPaused();
        if( !closkWasPausedOnActivate ) {
            clock.setPaused( true );
        }
        stopwatchDlg.setVisible( true );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        setStopwatchVisible( false );
        if( clock.isPaused() != closkWasPausedOnActivate ) {
            clock.setPaused( closkWasPausedOnActivate );
        }
    }

    public void setStopwatchVisible( boolean isVisible ) {
        stopwatchDlg.setVisible( isVisible );
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
            if( event.isReset() ) {
                module.resetWaveMediumGraphic();
                if( !clock.isPaused() ) {
                    clock.setPaused( true );
                }
                clock.tickOnce();
            }
            else {
                if( event.isRunning() && clock.isPaused() ) {
                    SingleSourceMeasureModule.this.clock.setPaused( false );
                }
                else if( !clock.isPaused() ) {
                    SingleSourceMeasureModule.this.clock.setPaused( true );
                }
            }
        }
    }
}
