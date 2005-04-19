/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ContentPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.movingman.common.CircularBuffer;
import edu.colorado.phet.movingman.common.LinearTransform1d;
import edu.colorado.phet.movingman.misc.JEPFrame;
import edu.colorado.phet.movingman.model.*;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.plots.PlotSet;
import edu.colorado.phet.movingman.view.ManGraphic;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;
import edu.colorado.phet.movingman.view.WalkWayGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:19:49 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManModule extends Module {

    private static boolean addJEP = true;
    private PhetFrame frame;

    private MovingManModel movingManModel;
    private MovingManControlPanel movingManControlPanel;
    private boolean initMediaPanel = false;

    private MovingManApparatusPanel movingManApparatusPanel;
//    private HelpItem2 closeHelpItem;

    // Localization
    public static final String localizedStringsPath = "localization/MovingManStrings";
    private boolean soundEnabled = true;

    public MovingManModule( AbstractClock clock ) throws IOException {
        super( SimStrings.get( "ModuleTitle.MovingManModule" ), clock );

        super.setModel( new BaseModel() );
        movingManModel = new MovingManModel( this, clock );
        movingManApparatusPanel = new MovingManApparatusPanel( this );
        super.setApparatusPanel( movingManApparatusPanel );
        movingManControlPanel = new MovingManControlPanel( this );
        getModel().addModelElement( movingManModel.getMainModelElement() );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                initMediaPanel();
                relayout();
            }

            public void componentResized( ComponentEvent e ) {
                initMediaPanel();
                relayout();
            }
        } );
        clock.addClockTickListener( getModel() );//todo is this redundant now?
//        closeHelpItem=new HelpItem2( getApparatusPanel(),SimStrings.get( "MovingManModule.CloseHelpText" ));
//        closeHelpItem.pointDownAt( );

//        closeHelpItem = new HelpItem( getApparatusPanel(), SimStrings.get( "MovingManModule.CloseHelpText" ), 250, 450 );
//        closeHelpItem.setForegroundColor( Color.red );
//        closeHelpItem.setShadowColor( Color.black );
//        addHelpItem( closeHelpItem );

        movingManModel.fireReset();

        getVelocityPlot().addListener( new PlotDeviceListenerAdapter() {
            CircularBuffer circularBuffer = new CircularBuffer( 20 );

            public void nominalValueChanged( double value ) {
//                circularBuffer.addPoint( );
                if( value == 0 ) {
                    getMovingManApparatusPanel().getManGraphic().setVelocity( 0.0 );
                }
            }

            public void sliderDragged( double dragValue ) {//todo this looks suspicious
                double value = getVelocityPlot().getSliderValue();
                if( value == 0 ) {
                    getMovingManApparatusPanel().getManGraphic().setVelocity( 0.0 );
                }
            }

        } );

        getManGraphic().addListener( new ManGraphic.Listener() {
            public void manGraphicChanged() {
            }

            public void mouseReleased() {
                getManGraphic().setVelocity( 0 );
            }
        } );
        movingManModel.setRecordMode();

    }

    public int getNumSmoothingPoints() {
        return movingManModel.getNumSmoothingPoints();
    }


    public void showMegaHelp() {
        showHelpImage( "images/mm-mh.gif" );
    }

    public void showHelpImage( String imageName ) {
        final JFrame imageFrame = new JFrame();
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( imageName );

            JLabel label = new JLabel( new ImageIcon( image ) );
            imageFrame.setContentPane( label );
            imageFrame.pack();
            SwingUtils.centerWindowOnScreen( imageFrame );
            imageFrame.setVisible( true );
            imageFrame.addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e ) {
                    imageFrame.dispose();
                }
            } );
            imageFrame.setResizable( false );
        }
        catch( IOException e ) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace( new PrintWriter( sw ) );
            JOptionPane.showMessageDialog( getApparatusPanel(), sw.getBuffer().toString(),
                                           SimStrings.get( "MovingManModule.ErrorLoadingHelpDialog" ), JOptionPane.ERROR_MESSAGE );
        }

    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        getMovingManApparatusPanel().setHelpEnabled( h );
//        Point plotLocation = null;
        if( getPositionPlot().isVisible() ) {
//            JButton closeButton = getPositionPlot().getCloseButton();
//            closeHelpItem.setLocation( closeButton.getLocation().x - 100, closeButton.getLocation().y + closeButton.getHeight() );
        }
        else {
//            removeHelpItem( closeHelpItem );
        }
    }


    public Color getBackgroundColor() {
        return getMovingManApparatusPanel().getBackgroundColor();
    }

    public void repaintBackground( Rectangle rect ) {
//        getMovingManApparatusPanel().repaintBackground( rect );
    }


    public void recordingFinished() {
        setPaused( true );
        getTimeModel().fireFinishedRecording();
    }

    public void firePlaybackFinished() {
        getTimeModel().firePlaybackFinished();
    }

    public static double getTimeScale() {
        return MovingManTimeModel.TIME_SCALE;
    }

    public MovingManTimeModel getTimeModel() {
        return movingManModel.getTimeModel();
    }

    public void setWiggleMeVisible( boolean b ) {
        getMovingManApparatusPanel().setWiggleMeVisible( b );
    }

    public boolean isPaused() {
        return getTimeModel().isPaused();
    }

//    public BufferedPhetGraphic2 getBuffer() {
//        return getMovingManApparatusPanel().getBuffer();
//    }

    public MovingManModel getMovingManModel() {
        return movingManModel;
    }

    public void addListener( TimeListener timeListener ) {
        getTimeModel().addListener( timeListener );
    }

    public void setPaused( boolean paused ) {
        getTimeModel().setPaused( paused );
        getPositionPlot().requestTypingFocus();
    }

    public void setSmoothingSmooth() {
        setNumSmoothingPoints( 8 );
    }

    public void setSmoothingSharp() {
        setNumSmoothingPoints( 2 );
    }

    private void setNumSmoothingPoints( int numSmoothingPoints ) {
        System.out.println( "Num smoothing points=" + numSmoothingPoints );
//        numSmoothingPoints = 8;
//        System.out.println( "Smoothing point override." );
//        numSmoothingPoints=4;
//        System.out.println( "numSmoothingPoints = " + numSmoothingPoints );
        getTimeModel().setNumSmoothingPoints( numSmoothingPoints );
        movingManModel.setNumSmoothingPoints( numSmoothingPoints );
//        getPlotSet().setNumSmoothingPoints( numSmoothingPoints );
    }

    public void setRightDirPositive( boolean rightPos ) {
        LinearTransform1d newTransform;
        double appPanelWidth = getApparatusPanel().getWidth();
        int inset = 50;
        WalkWayGraphic walkwayGraphic = getWalkwayGraphic();
        if( rightPos ) {//as usual
            newTransform = new LinearTransform1d( -getMaxManPosition(), getMaxManPosition(), inset, appPanelWidth - inset );
//            todo positions are broken.

            walkwayGraphic.setTreeX( -10 );
            walkwayGraphic.setHouseX( 10 );
        }
        else {
            newTransform = new LinearTransform1d( getMaxManPosition(), -getMaxManPosition(), inset, appPanelWidth - inset );
            walkwayGraphic.setTreeX( 10 );
            walkwayGraphic.setHouseX( -10 );
        }

        setManTransform( newTransform );
        getTimeModel().setRecordMode();
        reset();
        setPaused( true );

//        getMovingManApparatusPanel().getPlotSet().setCursorsVisible( true );
    }

    public void repaintBackground() {
        getMovingManApparatusPanel().repaintBackground();
    }

    public LinearTransform1d getManPositionTransform() {
        return movingManApparatusPanel.getManPositionTransform();
    }

    private void initMediaPanel() {
        if( initMediaPanel ) {
            return;
        }
        final JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( getApparatusPanel() );
        JPanel jp = (JPanel)parent.getContentPane();
        ContentPanel contentPanel = (ContentPanel)jp;
        final JPanel appPanel = new JPanel( new BorderLayout() );
        final JComponent playbackPanel = movingManControlPanel.getPlaybackPanel();
        appPanel.add( playbackPanel, BorderLayout.CENTER );
        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        final JLabel phetIconLabel = new JLabel( imageIcon );
        appPanel.add( phetIconLabel, BorderLayout.WEST );
        HelpPanel hp = new HelpPanel( this );

        appPanel.add( hp, BorderLayout.EAST );
//        contentPanel.setsetAppControlPanel( appPanel );//todo fix
        contentPanel.setAppControlPanel( appPanel );
        initMediaPanel = true;
    }

    public ManGraphic getManGraphic() {
        return getMovingManApparatusPanel().getManGraphic();
    }

    public PlotSet getPlotSet() {
        return movingManApparatusPanel.getPlotSet();
    }

    public PlotDevice getAccelerationPlot() {
        return getPlotSet().getAccelerationPlot();
    }

    public PlotDevice getPositionPlot() {
        return getPlotSet().getPositionPlot();
    }

    public PlotDevice getVelocityPlot() {
        return getPlotSet().getVelocityPlot();
    }

    public DataSuite getPosition() {
        return movingManModel.getPositionDataSuite();
    }

    public void setMode( Mode mode ) {
        getTimeModel().setMode( mode );
        repaintBackground();
    }

    public void relayout() {
        getMovingManApparatusPanel().relayout();
        Component c = getApparatusPanel();
        if( c.getHeight() > 0 && c.getWidth() > 0 ) {
//            getMovingManApparatusPanel().setTheSize( c.getWidth(), c.getHeight() );
            getApparatusPanel().repaint();
        }
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    private void setFrame( PhetFrame frame ) {
        this.frame = frame;
    }

    public static void fixComponent( Container jc ) {
        jc.invalidate();
        jc.validate();
        jc.repaint();
    }

    public Man getMan() {
        return movingManModel.getMan();
    }

    public MMTimer getRecordingTimer() {
        return getTimeModel().getRecordTimer();
    }


    public void setReplayTime( double requestedTime ) {

//        /**Find the position for the time.*/
//        int timeIndex = getTimeIndex( requestedTime );
//        movingManModel.setReplayTimeIndex( timeIndex );
        if( requestedTime < 0 || requestedTime > getTimeModel().getRecordTimer().getTime() ) {
            return;
        }
        else {
            getTimeModel().getPlaybackTimer().setTime( requestedTime );
//            int timeIndex = getTimeIndex( requestedTime );
//            if( timeIndex < movingManModel.getPositionDataSuite().numSmoothedPoints() && timeIndex >= 0 ) {
//                double x = movingManModel.getPositionDataSuite().smoothedPointAt( timeIndex );
//                getMan().setPosition( x );

            movingManModel.setReplayTime( requestedTime );
            
//            }
//            getPlotSet().cursorMovedToTime( requestedTime, timeIndex );
//            getPlotSet().cursorMovedToTime( requestedTime, timeIndex );
//            getPlotSet().cursorMovedToTime( requestedTime, timeIndex );
        }
    }

    public void rewind() {
        getTimeModel().rewind();
        getMan().reset();
    }

    public void setRecordMode() {
        //enter the text box values.
        enterTextBoxValues();
        getTimeModel().setRecordMode();
    }

    private void enterTextBoxValues() {
        getPlotSet().enterTextBoxValues();
    }

    public void startPlaybackMode( double playbackSpeed ) {
        getTimeModel().startPlaybackMode( playbackSpeed );
    }

    public boolean isRecording() {
        return getTimeModel().isRecording();
    }

//    public void cursorMovedToTime( double requestedTime ) {
//        if( requestedTime < 0 || requestedTime > getTimeModel().getRecordTimer().getTime() ) {
//            return;
//        }
//        else {
//            getTimeModel().getPlaybackTimer().setTime( requestedTime );
//            int timeIndex = getTimeIndex( requestedTime );
//            if( timeIndex < movingManModel.getPositionDataSuite().numSmoothedPoints() && timeIndex >= 0 ) {
//                double x = movingManModel.getPositionDataSuite().smoothedPointAt( timeIndex );
//                getMan().setPosition( x );
//            }
//            getPlotSet().cursorMovedToTime( requestedTime, timeIndex );
//        }
//    }

    private int getTimeIndex( double requestedTime ) {
        return getTimeModel().getTimeIndex( requestedTime );
    }

    public void setManTransform( LinearTransform1d transform ) {

        getMovingManApparatusPanel().setManTransform( transform );
    }


    public void setSoundEnabled( boolean soundEnabled ) {
        if( soundEnabled != this.soundEnabled ) {
            this.soundEnabled = soundEnabled;
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.soundOptionChanged( soundEnabled );
            }
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }


    public static interface Listener {
        public void reset();

        public void soundOptionChanged( boolean soundEnabled );
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void reset() {
        movingManModel.reset();
        movingManApparatusPanel.reset();
        notifyReset();
    }

    private void notifyReset() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.reset();
        }
    }

    public DataSuite getVelocityData() {
        return movingManModel.getVelocitySeries();
    }

    public DataSuite getAcceleration() {
        return movingManModel.getAccelerationDataSuite();
    }

    public JFrame getFrame() {
        return frame;
    }

    public double getMaxTime() {
        return movingManModel.getMaxTime();
    }

    public double getMaxManPosition() {
        return movingManModel.getMaxManPosition();
    }

    public MMTimer getPlaybackTimer() {
        return getTimeModel().getPlaybackTimer();
    }

    public boolean isRecordMode() {
        return getTimeModel().isRecordMode();
    }

    public boolean isTakingData() {
        return getTimeModel().isTakingData();
    }


    private void setInited( boolean b ) {
        getMovingManApparatusPanel().setInited( b );
    }

    public MovingManApparatusPanel getMovingManApparatusPanel() {
        return (MovingManApparatusPanel)getApparatusPanel();
    }

    private static void addJEP( final MovingManModule module ) {
        final JFrame frame = module.getFrame();
        JMenu misc = new JMenu( SimStrings.get( "MovingManModule.SpecialFeaturesMenu" ) );
        misc.setMnemonic( SimStrings.get( "MovingManModule.SpecialFeaturesMenuMnemonic" ).charAt( 0 ) );
        JMenuItem jep = new JMenuItem( SimStrings.get( "MovingManModule.ExprEvalMenuItem" ) );
        misc.add( jep );
        final JEPFrame jef = new JEPFrame( frame, module );
        jep.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                jef.setVisible( true );
            }
        } );

        final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem( SimStrings.get( "MovingManModule.InvertXAxisMenuItem" ), false );
        jcbmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRightDirPositive( !jcbmi.isSelected() );
            }
        } );
        misc.add( jcbmi );
        frame.getJMenuBar().add( misc );
    }

    public void step( double dt ) {
        movingManModel.step( dt );
        getPlotSet().updateSliders();
    }

    public double getMinTime() {
        return movingManModel.getMinTime();
    }

    public WalkWayGraphic getWalkwayGraphic() {
        return getMovingManApparatusPanel().getWalkwayGraphic();
    }


    public static void main( final String[] args ) throws Exception {
        edu.colorado.phet.common.view.PhetLookAndFeel plaf = new edu.colorado.phet.common.view.PhetLookAndFeel();
        plaf.apply();
        edu.colorado.phet.common.view.PhetLookAndFeel.setLookAndFeel();

        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            Locale.setDefault( new Locale( applicationLocale ) );
        }
        String argsKey = "user.language=";
        if( args.length > 0 && args[0].startsWith( argsKey ) ) {
            String locale = args[0].substring( argsKey.length(), args[0].length() );
            Locale.setDefault( new Locale( locale ) );
        }

        SimStrings.setStrings( localizedStringsPath );
        //Putting in swing thread avoids concurrentmodification exception in GraphicLayerSet.
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                try {
                    runMain( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private static void runMain( String[] args ) throws IOException {
        AbstractClock clock = new SwingTimerClock( 1, 30, true );
        clock.setDelay( 30 );
        final MovingManModule m = new MovingManModule( clock );
        FrameSetup setup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 800 ) );

        ApplicationModel desc = new ApplicationModel( SimStrings.get( "MovingManApplication.title" ),
                                                      SimStrings.get( "MovingManApplication.description" ),
                                                      SimStrings.get( "MovingManApplication.version" ), setup, m, clock );
        desc.setName( "movingman" );
        PhetApplication tpa = new PhetApplication( desc, args );

        final PhetFrame frame = tpa.getPhetFrame();
        m.setFrame( frame );
        if( m.getControlPanel() != null ) {
//            tpa.getApplicationView().getBasicPhetPanel().add( m.getControlPanel(), BorderLayout.WEST );
        }
        if( addJEP ) {
            addJEP( m );
        }
//        RepaintDebugGraphic rdp = new RepaintDebugGraphic( m, m.getApparatusPanel(), clock );
//        m.backgroundGraphic.addGraphic( rdp, -100 );
//        m.backgroundGraphic.addGraphic( rdp, 100 );

        tpa.startApplication();
        fixComponent( frame.getContentPane() );

        frame.invalidate();
        frame.validate();
        frame.repaint();
        m.repaintBackground();
        m.getTimeModel().getRecordMode().initialize();
        m.getApparatusPanel().repaint();

        final Runnable dofix = new Runnable() {
            public void run() {
                fixComponent( frame.getContentPane() );
                fixComponent( frame );
                m.repaintBackground();
            }
        };
        frame.addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                new Thread( dofix ).start();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        frame.addWindowStateListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ) {
                new Thread( dofix ).start();
            }

            public void windowActivated( WindowEvent e ) {
                new Thread( dofix ).start();
            }

            public void windowGainedFocus( WindowEvent e ) {
                new Thread( dofix ).start();
            }
        } );
        frame.addWindowListener( new WindowListener() {
            public void windowActivated( WindowEvent e ) {
                new Thread( dofix ).start();
            }

            public void windowClosed( WindowEvent e ) {
            }

            public void windowClosing( WindowEvent e ) {
            }

            public void windowDeactivated( WindowEvent e ) {
            }

            public void windowDeiconified( WindowEvent e ) {
                new Thread( dofix ).start();
            }

            public void windowIconified( WindowEvent e ) {
            }

            public void windowOpened( WindowEvent e ) {
                new Thread( dofix ).start();
            }
        } );
        dofix.run();

        m.setInited( true );
        m.relayout();
        m.setSmoothingSmooth();
    }

    public void initialize() {
        movingManApparatusPanel.initialize();
    }

    public void confirmAndApplyReset() {
        MovingManModule module = this;
        boolean paused = module.isPaused();
        module.setPaused( true );
        int option = JOptionPane.showConfirmDialog( module.getApparatusPanel(),
                                                    SimStrings.get( "MMPlot.ClearConfirmText" ),
                                                    SimStrings.get( "MMPlot.ClearConfirmButton" ),
                                                    JOptionPane.YES_NO_CANCEL_OPTION );
        if( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
            module.reset();
        }
        else if( option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION ) {
            module.setPaused( paused );
        }
    }
}


