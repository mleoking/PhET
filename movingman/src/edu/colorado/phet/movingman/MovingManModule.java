/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.ContentPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.movingman.common.BufferedGraphicForComponent;
import edu.colorado.phet.movingman.common.CircularBuffer;
import edu.colorado.phet.movingman.common.LinearTransform1d;
import edu.colorado.phet.movingman.common.WiggleMe;
import edu.colorado.phet.movingman.misc.JEPFrame;
import edu.colorado.phet.movingman.plots.MMPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
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
    private boolean paused = true;
    private static boolean addJEP = true;

    private ManGraphic manGraphic;
    private LinearTransform1d manPositionTransform;

    private MMTimer recordTimer;
    private MMTimer playbackTimer;


    private Mode mode;//the current mode.

    private RecordMode recordMode;
    private PlaybackMode playbackMode;
    private MovingManControlPanel movingManControlPanel;
    private TimeGraphic timerGraphic;
    private BufferedGraphicForComponent backgroundGraphic;
    private WalkWayGraphic walkwayGraphic;

    private Color purple = new Color( 200, 175, 250 );
    private PhetFrame frame;
    private ModelElement mainModelElement;
    private Color backgroundColor;
    private MovingManModel model;
    private PlotSet plotSet;
    private WiggleMe wiggleMe;
    private boolean initMediaPanel = false;

    private MMKeySuite keySuite;
    private ManGraphic.Listener wiggleMeListener;
    private ArrayList listeners = new ArrayList();
    private MovingManApparatusPanel mypanel;
    public static final double TIME_SCALE = 1.0 / 50.0;
    private int numSmoothingPoints;
    private HelpItem closeHelpItem;
    private boolean stopped = false;

    // Localization
    public static final String localizedStringsPath = "localization/MovingManStrings";

    public int getNumSmoothingPoints() {
        return numSmoothingPoints;
    }

    public MovingManModule( AbstractClock clock ) throws IOException {
        super( SimStrings.get( "ModuleTitle.MovingManModule" ), clock );
        model = new MovingManModel( this, clock );
        manPositionTransform = new LinearTransform1d( -getMaxManPosition(), getMaxManPosition(), 50, 600 );
        recordTimer = new MMTimer( SimStrings.get( "MovingManModule.RecordTimerLabel" ) );//, MovingManModel.TIMER_SCALE );
        playbackTimer = new MMTimer( SimStrings.get( "MovingManModule.PlaybackTimerLabel" ) );//, MovingManModel.TIMER_SCALE );
        mypanel = new MovingManApparatusPanel( this );
        super.setApparatusPanel( mypanel );
        backgroundColor = new Color( 250, 190, 240 );
        backgroundGraphic = new BufferedGraphicForComponent( 0, 0, 800, 400, backgroundColor, getApparatusPanel() );
        plotSet = new PlotSet( this );
        mypanel.initLayout();

        keySuite = new MMKeySuite( this );
        mypanel.addKeyListener( keySuite );


        mypanel.addGraphicsSetup( new BasicGraphicsSetup() );
        mypanel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        super.setModel( new BaseModel() );


        manGraphic = new ManGraphic( this, model.getMan(), 0, manPositionTransform );

        getApparatusPanel().addGraphic( manGraphic, 1 );
        timerGraphic = new TimeGraphic( this, recordTimer, playbackTimer, 80, 40 );
        getApparatusPanel().addGraphic( timerGraphic, 1 );

        walkwayGraphic = new WalkWayGraphic( this, 11 );
        backgroundGraphic.addGraphic( walkwayGraphic, 0 );

//        layout.relayout();

        movingManControlPanel = new MovingManControlPanel( this );
        mainModelElement = new ModelElement() {
            public void stepInTime( double dt ) {
                if( !paused ) {
                    mode.stepInTime( dt * TIME_SCALE );
                }
            }
        };
        getModel().addModelElement( mainModelElement );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                initMediaPanel();
                relayout();
            }

            public void componentResized( ComponentEvent e ) {
                getModel().execute( new Command() {
                    public void doIt() {
                        initMediaPanel();
                        relayout();
                    }
                } );
            }
        } );
        recordMode = new RecordMode( this );
        playbackMode = new PlaybackMode( this );
        setMode( recordMode );

        getApparatusPanel().addGraphic( backgroundGraphic, 0 );
        clock.addClockTickListener( getModel() );

        Point2D start = manGraphic.getRectangle().getLocation();
        start = new Point2D.Double( start.getX() + 50, start.getY() + 50 );
        wiggleMe = new WiggleMe( getApparatusPanel(), start,
                                 new ImmutableVector2D.Double( 0, 1 ), 15, .02, SimStrings.get( "MovingManModule.DragTheManText" ) );
        wiggleMe.setVisible( false );//TODO don't delete this line.
        addListener( new ListenerAdapter() {
            public void recordingStarted() {
                setWiggleMeVisible( false );
            }
        } );
        this.wiggleMeListener = new ManGraphic.Listener() {
            public void manGraphicChanged() {
                Point2D start = manGraphic.getRectangle().getLocation();
                start = new Point2D.Double( start.getX() - wiggleMe.getWidth() - 20, start.getY() + manGraphic.getRectangle().getHeight() / 2 );
                wiggleMe.setCenter( new Point( (int)start.getX(), (int)start.getY() ) );
            }

            public void mouseReleased() {
            }
        };
        setWiggleMeVisible( true );

        manGraphic.addListener( this.wiggleMeListener );
        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getApparatusPanel().requestFocus();
            }
        } );

        closeHelpItem = new HelpItem( getApparatusPanel(), SimStrings.get( "MovingManModule.CloseHelpText" ), 250, 450 );
        closeHelpItem.setForegroundColor( Color.red );
        closeHelpItem.setShadowColor( Color.black );
        addHelpItem( closeHelpItem );


        fireReset();
//        addDirectionHandler();

//        addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                double dx=getMan().getDx();
//                if (dx!=0){
//                    getManGraphic().setVelocity( dx);
//                }
//            }
//        } );

//        addModelElement( new ModelElement() {
//            public void stepInTime( double dt ) {
//                double dx=getManGraphic().getDx();
//                if (dx!=0){
//                    getManGraphic().setVelocity( dx );
//                }
//            }
//        } );

//        addModelElement( new ModelElement() {
//            CircularBuffer buffer = new CircularBuffer( 30 );
//
//            public void stepInTime( double dt ) {
//
//                buffer.addPoint( getMan().getDx() );
//                boolean allZero = true;
//                for( int i = 0; i < buffer.numPoints(); i++ ) {
//                    double x = buffer.pointAt( i );
//                    if( x != 0 ) {
//                        allZero = false;
//                        break;
//                    }
//                }
//                if( allZero ) {
//                    getManGraphic().setVelocity( 0.0 );
//                }
//            }
//        } );
        getVelocityPlot().addListener( new MMPlot.Listener() {
            CircularBuffer circularBuffer = new CircularBuffer( 20 );

            public void nominalValueChanged( double value ) {
//                circularBuffer.addPoint( );
                if( value == 0 ) {
                    manGraphic.setVelocity( 0.0 );
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
    }

    private void addDirectionHandler() {

        getVelocityPlot().addListener( new MMPlot.Listener() {
            public void nominalValueChanged( double value ) {
                if( manGraphic.isDragging() || getPositionPlot().isDragging() || getVelocityPlot().isDragging() || stopped ) {
                }
                else {
                    manGraphic.setVelocity( value );
                }
            }
        } );
        getManGraphic().addListener( new ManGraphic.Listener() {
            double lastX;

            public void manGraphicChanged() {
                if( manGraphic.isDragging() || getPositionPlot().isDragging() ) {
                    double x = getMan().getX();
                    double dx = x - lastX;
                    getManGraphic().setVelocity( dx );
                    lastX = x;
                }
            }

            public void mouseReleased() {
                getManGraphic().setVelocity( 0 );
                stopped = true;
            }
        } );

        getVelocityPlot().getVerticalChartSlider().addListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                if( getVelocityPlot().isDragging() ) {
                    getManGraphic().setVelocity( value );
                }
            }
        } );
        getAccelerationPlot().getVerticalChartSlider().addListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                if( getAccelerationPlot().isDragging() ) {
                    stopped = false;
                }
            }
        } );
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
        setWiggleMeVisible( h );
        Point plotLocation = null;
        if( getPositionPlot().isVisible() ) {
            JButton closeButton = getPositionPlot().getCloseButton();
            closeHelpItem.setLocation( closeButton.getLocation().x - 100, closeButton.getLocation().y + closeButton.getHeight() );
        }
        else {
            removeHelpItem( closeHelpItem );
        }
    }

    private void fireReset() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.reset();
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    private void firePause() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            if( mode == recordMode ) {
                listener.recordingPaused();
            }
            else {
                listener.playbackPaused();
            }
        }
    }

    public void repaintBackground( Rectangle rect ) {
        if( backgroundGraphic != null ) {
            backgroundGraphic.paintBufferedImage( rect );
            getApparatusPanel().repaint( rect );
        }
    }

    public void setWiggleMeVisible( boolean b ) {
        if( b == wiggleMe.isVisible() ) {
            return;
        }
        if( !b ) {
            wiggleMe.setVisible( false );
            getApparatusPanel().removeGraphic( wiggleMe );
            getModel().removeModelElement( wiggleMe );
            manGraphic.removeListener( wiggleMeListener );
        }
        else {
            wiggleMe.setVisible( true );
            getApparatusPanel().addGraphic( wiggleMe, 100 );
            getModel().addModelElement( wiggleMe );
            manGraphic.addListener( wiggleMeListener );
        }
    }

    public void firePlaybackFinished() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.playbackFinished();
        }
    }

    public void fireFinishedRecording() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.recordingFinished();
        }
    }

    public void recordingFinished() {
        setPaused( true );
        fireFinishedRecording();
    }

    public KeyListener getKeySuite() {
        return keySuite;
    }

    public static interface Listener {
        void recordingStarted();

        void recordingPaused();

        void recordingFinished();

        void playbackStarted();

        void playbackPaused();

        void playbackFinished();

        void reset();

        void rewind();
    }

    public static class ListenerAdapter implements Listener {

        public void recordingStarted() {
        }

        public void recordingPaused() {
        }

        public void recordingFinished() {
        }

        public void playbackStarted() {
        }

        public void playbackPaused() {
        }

        public void playbackFinished() {
        }

        public void reset() {
        }

        public void rewind() {
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public BufferedGraphicForComponent getBackground() {
        return backgroundGraphic;
    }

    public MovingManModel getMovingManModel() {
        return model;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setPaused( boolean paused ) {
        if( paused != this.paused ) {
            this.paused = paused;
            if( paused ) {
                firePause();
            }
            else if( isRecording() ) {
                fireRecordStarted();
            }
            else if( isPlayback() ) {
                firePlaybackStarted();
            }
            getPositionPlot().requestTypingFocus();
        }
    }

    private void firePlaybackStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.playbackStarted();
        }
    }

    private boolean isPlayback() {
        return mode == playbackMode;
    }

    private void fireRecordStarted() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.recordingStarted();
        }
    }

    public Color getPurple() {
        return purple;
    }

    public void setNumSmoothingPoints( int n ) {
        this.numSmoothingPoints = n;
        model.setNumSmoothingPoints( n );
        plotSet.setNumSmoothingPoints( n );
    }

    public void setRightDirPositive( boolean rightPos ) {
        LinearTransform1d newTransform;
        double appPanelWidth = getApparatusPanel().getWidth();
        int inset = 50;
        if( rightPos ) {//as usual
            newTransform = new LinearTransform1d( -getMaxManPosition(), getMaxManPosition(), inset, appPanelWidth - inset );
            //todo positions are broken.
//            walkwayGraphic.setTreeX( -10 );
//            walkwayGraphic.setHouseX( 10 );
        }
        else {
            newTransform = new LinearTransform1d( getMaxManPosition(), -getMaxManPosition(), inset, appPanelWidth - inset );
//            walkwayGraphic.setTreeX( 10 );
//            walkwayGraphic.setHouseX( -10 );
        }
        manGraphic.setTransform( newTransform );
        setManTransform( newTransform );
        setMode( recordMode );
        reset();
        setPaused( true );
    }

    public void repaintBackground() {
        backgroundGraphic.paintBufferedImage();
        getApparatusPanel().repaint();
    }

    public LinearTransform1d getManPositionTransform() {
        return manPositionTransform;
    }

    private void initMediaPanel() {
        if( initMediaPanel ) {
            return;
        }
        final JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( getApparatusPanel() );
        JPanel jp = (JPanel)parent.getContentPane();
        ContentPanel contentPanel = (ContentPanel)jp;
        final JPanel appPanel = new JPanel( new BorderLayout() );
//        appPanel.setLayout( new FlowLayout() );
        final JComponent playbackPanel = movingManControlPanel.getPlaybackPanel();
        appPanel.add( playbackPanel, BorderLayout.CENTER );
//        final Runnable relayout = new Runnable() {
//            public void run() {
//                int width = appPanel.getWidth();
//                int dw = width - playbackPanel.getWidth();
//                playbackPanel.reshape( dw / 2, 0, playbackPanel.getPreferredSize().width, playbackPanel.getPreferredSize().height );
//            }
//        };
        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        final JLabel phetIconLabel = new JLabel( imageIcon );
//        final Runnable relayout=new Runnable() {
//            public void run() {
//                phetIconLabel.reshape( 0,0,phetIconLabel.getPreferredSize().width, phetIconLabel.getPreferredSize().height );
//            }
//        };
//        appPanel.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                relayout.run();
//            }
//
//            public void componentShown( ComponentEvent e ) {
//                relayout.run();
//            }
//        } );

        appPanel.add( phetIconLabel, BorderLayout.WEST );
        HelpPanel hp = new HelpPanel( this );
//        JButton help = new JButton( SimStrings.get( "MovingManModule.HelpButton" );
        appPanel.add( hp, BorderLayout.EAST );
//        contentPanel.setsetAppControlPanel( appPanel );//todo fix
        contentPanel.setAppControlPanel( appPanel );
//        relayout.run();
        initMediaPanel = true;
    }

    public ManGraphic getManGraphic() {
        return manGraphic;
    }

    public MMPlot getAccelerationPlot() {
        return plotSet.getAccelerationPlot();
    }

    public MMPlot getPositionPlot() {
        return plotSet.getPositionPlot();
    }

    public MMPlot getVelocityPlot() {
        return plotSet.getVelocityPlot();
    }

    public SmoothDataSeries getPosition() {
        return model.getPosition();
    }

    public void setMode( Mode mode ) {
        boolean same = mode == this.mode;
        if( !same ) {
            this.mode = mode;
            this.mode.initialize();
            System.out.println( "Changed mode to: " + mode.getName() );
            repaintBackground();
        }
    }

    public void relayout() {
        getMovingManApparatusPanel().relayout();
        Component c = getApparatusPanel();
        if( c.getHeight() > 0 && c.getWidth() > 0 ) {
            backgroundGraphic.setSize( c.getWidth(), c.getHeight() );
            backgroundGraphic.paintBufferedImage();
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
        return model.getMan();
    }

    public MMTimer getRecordingTimer() {
        return recordTimer;
    }

    public void setCursorsVisible( boolean visible ) {
        plotSet.setCursorsVisible( visible );
    }

    public void setReplayTime( double requestedTime ) {
        /**Find the position for the time.*/
        int timeIndex = getTimeIndex( requestedTime );
        model.setReplayTimeIndex( timeIndex );
        cursorMovedToTime( requestedTime );
    }

    public void rewind() {
        playbackTimer.setTime( 0 );
        getMan().reset();
        fireRewind();
    }

    private void fireRewind() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.rewind();
        }
    }

    public void setRecordMode() {
        //enter the text box values.
        enterTextBoxValues();
        setMode( recordMode );
    }

    private void enterTextBoxValues() {
        plotSet.enterTextBoxValues();
    }

    public void startPlaybackMode( double playbackSpeed ) {
        stopped = false;
        playbackMode.setPlaybackSpeed( playbackSpeed );
        setMode( playbackMode );
        setPaused( false );
    }

    public boolean isRecording() {
        return mode == recordMode && !isPaused();
    }

    public void cursorMovedToTime( double requestedTime ) {
        if( requestedTime < 0 || requestedTime > recordTimer.getTime() ) {
            return;
        }
        else {
            playbackTimer.setTime( requestedTime );
            int timeIndex = getTimeIndex( requestedTime );
            if( timeIndex < model.getPosition().numSmoothedPoints() && timeIndex >= 0 ) {
                double x = model.getPosition().smoothedPointAt( timeIndex );
                getMan().setX( x );
            }
            plotSet.cursorMovedToTime( requestedTime, timeIndex );
        }
    }

    private int getTimeIndex( double requestedTime ) {
        return (int)( requestedTime / TIME_SCALE );
    }

    public void setManTransform( LinearTransform1d transform ) {
        this.manPositionTransform = transform;
    }

    public void reset() {
        setPaused( true );
        model.reset();
        recordTimer.reset();
        playbackTimer.reset();
        setCursorsVisible( false );
        plotSet.reset();
        backgroundGraphic.paintBufferedImage();
        getApparatusPanel().repaint();

        fireReset();
    }

    public SmoothDataSeries getVelocityData() {
        return model.getVelocitySeries();
    }

    public SmoothDataSeries getAcceleration() {
        return model.getAcceleration();
    }

    public JFrame getFrame() {
        return frame;
    }

    public double getMaxTime() {
        return model.getMaxTime();
    }

    public double getMaxManPosition() {
        return model.getMaxManPosition();
    }

    public MMTimer getPlaybackTimer() {
        return playbackTimer;
    }

    public boolean isRecordMode() {
        return mode == recordMode;
    }

    public boolean isTakingData() {
        return !isPaused() && mode.isTakingData();
    }

    public static void main( String[] args ) throws Exception {
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
//        SmoothUtilities.setFractionalMetrics( false );
//        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        AbstractClock clock = new SwingTimerClock( 1, 30, true );
        clock.setDelay( 30 );
//        AbstractClock clock = new SwingTimerClock( 1, 30, false );
        final MovingManModule m = new MovingManModule( clock );
        FrameSetup setup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 800 ) );

        ApplicationModel desc = new ApplicationModel( SimStrings.get( "MovingManApplication.title" ),
                                                      SimStrings.get( "MovingManApplication.description" ),
                                                      SimStrings.get( "MovingManApplication.version" ), setup, m, clock );
        PhetApplication tpa = new PhetApplication( desc );

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
        m.recordMode.initialize();
        m.getApparatusPanel().repaint();

        final Runnable dofix = new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 300 );
                    fixComponent( frame.getContentPane() );
                    fixComponent( frame );
                    Thread.sleep( 1000 );
                    fixComponent( frame.getContentPane() );
                    fixComponent( frame );
                    m.repaintBackground();
                }
                catch( InterruptedException e1 ) {
                    e1.printStackTrace();
                }
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
        new Thread( dofix ).start();

        m.setInited( true );
        m.relayout();
        m.setNumSmoothingPoints( 12 );
    }

    private void setInited( boolean b ) {
        getMovingManApparatusPanel().setInited( b );
    }

    private MovingManApparatusPanel getMovingManApparatusPanel() {
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
        model.step( dt );
        plotSet.updateSliders();
    }

    public double getMinTime() {
        return model.getMinTime();
    }

    public WalkWayGraphic getWalkwayGraphic() {
        return walkwayGraphic;
    }

}


