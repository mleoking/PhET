/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.BasicPhetPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.help.HelpPanel;
import edu.colorado.phet.common.view.plaf.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.common.BufferedGraphicForComponent;
import edu.colorado.phet.movingman.common.CircularBuffer;
import edu.colorado.phet.movingman.common.WiggleMe;
import edu.colorado.phet.movingman.misc.JEPFrame;
import edu.colorado.phet.movingman.plots.MMPlot;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

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
    private MovingManLayout layout;

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
    private boolean inited;
    private MMKeySuite keySuite;
    private ManGraphic.Listener wiggleMeListener;
    private ArrayList listeners = new ArrayList();
    private ApparatusPanel mypanel;
    public static final double TIME_SCALE = 1.0 / 50.0;
    private int numSmoothingPoints;
    private HelpItem closeHelpItem;

    public int getNumSmoothingPoints() {
        return numSmoothingPoints;
    }

    class DebugApparatusPanel extends ApparatusPanel {

        public void repaint( long tm, int x, int y, int width, int height ) {
            super.repaint( tm, x, y, width, height );
        }

        public void repaint() {
            super.repaint();
        }

        public void repaint( long tm ) {
            super.repaint( tm );
        }

        public void repaint( int x, int y, int width, int height ) {
//                rectList.add( new Rectangle( x, y, width, height ) );
            super.repaint( x, y, width, height );
        }

        protected void paintComponent( Graphics graphics ) {
            if( inited ) {
                super.paintComponent( graphics );
            }
//                Graphics2D g2 = (Graphics2D)graphics;
//                for (int i=0;i<rectList.size();i++){
//                    Rectangle rect=(Rectangle)rectList.get(i);
//                    g2.setColor( Color.green );
//                    g2.setStroke( new BasicStroke( 15 ) );
//                    g2.draw( rect );
//                }
//                rectList.clear();
        }

        public void paintComponents( Graphics g ) {
            if( inited ) {
                super.paintComponents( g );
            }
        }

        protected void paintChildren( Graphics g ) {
            setOpaque( true );
            setDoubleBuffered( true );
            if( inited ) {
                super.paintChildren( g );
            }
        }

        public void paint( Graphics g ) {
            if( inited ) {
                super.paint( g );
            }
        }

        public void paintImmediately( int x, int y, int w, int h ) {
            if( inited ) {
                super.paintImmediately( x, y, w, h );
            }
//               rectList.add (new Rectangle( x, y, w, h ));
        }

        public void repaint( Rectangle r ) {
            super.repaint( 0, r.x, r.y, r.width, r.height );
        }

        public void paintAll( Graphics g ) {
            if( inited ) {
                super.paintAll( g );
            }
        }

        public Component add( Component comp ) {
            KeyListener[] kl = comp.getKeyListeners();
            if( !Arrays.asList( kl ).contains( keySuite ) ) {
                comp.addKeyListener( keySuite );
            }
            return super.add( comp );
        }
    }

    public MovingManModule( AbstractClock clock ) throws IOException {
        super( "The Moving Man" );
        model = new MovingManModel( this, clock );
        mypanel = new DebugApparatusPanel();

        keySuite = new MMKeySuite( this );
        mypanel.addKeyListener( keySuite );

        super.setApparatusPanel( mypanel );
        mypanel.addGraphicsSetup( new BasicGraphicsSetup() );
        mypanel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        super.setModel( new BaseModel() );

        backgroundColor = new Color( 250, 190, 240 );
        backgroundGraphic = new BufferedGraphicForComponent( 0, 0, 800, 400, backgroundColor, getApparatusPanel() );

        manPositionTransform = new LinearTransform1d( -getMaxManPosition(), getMaxManPosition(), 50, 600 );
        manGraphic = new ManGraphic( this, model.getMan(), 0, manPositionTransform );

        getApparatusPanel().addGraphic( manGraphic, 1 );
        recordTimer = new MMTimer( "Record" );//, MovingManModel.TIMER_SCALE );
        playbackTimer = new MMTimer( "Playback" );//, MovingManModel.TIMER_SCALE );
        timerGraphic = new TimeGraphic( this, recordTimer, playbackTimer, 80, 40 );
        getApparatusPanel().addGraphic( timerGraphic, 1 );

        walkwayGraphic = new WalkWayGraphic( this, 11 );
        backgroundGraphic.addGraphic( walkwayGraphic, 0 );

//        layout.relayout();
        plotSet = new PlotSet( this );
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
                                 new ImmutableVector2D.Double( 0, 1 ), 15, .02, "Drag the Man" );
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
        };
        setWiggleMeVisible( true );

        manGraphic.addListener( this.wiggleMeListener );
        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getApparatusPanel().requestFocus();
            }
        } );

        closeHelpItem = new HelpItem( getApparatusPanel(), "Close this plot", 250, 450 );
        closeHelpItem.setForegroundColor( Color.red );
        closeHelpItem.setShadowColor( Color.black );
        addHelpItem( closeHelpItem );

        layout = new MovingManLayout( this );
        fireReset();
        plotSet.getVelocityPlot().addListener( new MMPlot.Listener() {
            public void nominalValueChanged( double value ) {
                if( manGraphic.isDragging() || getPositionPlot().isDragging() ) {
                }
                else {
                    manGraphic.setVelocity( value );
                }
            }
        } );
        getMan().addListener( new Man.Listener() {
//            SmoothDataSeries dataSeries = new SmoothDataSeries( 5 );
            CircularBuffer buffer = new CircularBuffer( 5 );
//            double lastValue = 0;

            public void positionChanged( double x ) {
//                if (dataSeries==null){
//                    dataSeries=new SmoothDataSeries( 5);
//                    datas
//                }
                buffer.addPoint( x );
//                buffer.addPoint( x );
//                if( manGraphic.isDragging() || getPositionPlot().isDragging() ) {
//                    DataSeries dataSeries=new DataSeries();
//                    dataSeries.setd
//                    double vel=new
//                    double vel = x - lastValue;
//                    manGraphic.setVelocity( vel );
//                    lastValue = x;
//                }
//                else {
//
//                }
            }

            public void velocityChanged( double velocity ) {
                manGraphic.setVelocity( velocity );
            }

            public void accelerationChanged( double acceleration ) {
            }
        } );
//        plotSet.getPositionPlot().addListener( new MMPlot.Listener() {
//            double lastValue=0;
//            public void nominalValueChanged( double value ) {
//                if( manGraphic.isDragging() || getPositionPlot().isDragging() ) {
//                    double vel=value-lastValue;
//                    manGraphic.setVelocity( vel );
//                    System.out.println( "vel = " + vel );
//                    lastValue=value;
//                }
//                else {
//                }
//            }
//        } );
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
            GraphicsUtil.centerWindowOnScreen( imageFrame );
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
                                           SimStrings.get( "CCK3ControlPanel.ErrorLoadingHelpDialog" ), JOptionPane.ERROR_MESSAGE );
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
        backgroundGraphic.paintBufferedImage( rect );
        getApparatusPanel().repaint( rect );
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
            walkwayGraphic.setTreeX( -10 );
            walkwayGraphic.setHouseX( 10 );
        }
        else {
            newTransform = new LinearTransform1d( getMaxManPosition(), -getMaxManPosition(), inset, appPanelWidth - inset );
            walkwayGraphic.setTreeX( 10 );
            walkwayGraphic.setHouseX( -10 );
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
        BasicPhetPanel bpp = (BasicPhetPanel)jp;
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
//        JButton help = new JButton( "Help" );
        appPanel.add( hp, BorderLayout.EAST );
        bpp.setAppControlPanel( appPanel );
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
        layout.relayout();
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
        SmoothUtilities.setFractionalMetrics( false );
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        AbstractClock clock = new SwingTimerClock( 1, 30, true );
        clock.setDelay( 30 );
//        AbstractClock clock = new SwingTimerClock( 1, 30, false );
        final MovingManModule m = new MovingManModule( clock );
        FrameSetup setup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 800 ) );

        ApplicationModel desc = new ApplicationModel( "The Moving Man", "The Moving Man Application.",
                                                      ".02-beta-x 10-18-2004", setup, m, clock );
        PhetApplication tpa = new PhetApplication( desc );

        final PhetFrame frame = tpa.getApplicationView().getPhetFrame();
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

        m.inited = true;
        m.relayout();
        m.setNumSmoothingPoints( 12 );
    }

    private static void addJEP( final MovingManModule module ) {
        final JFrame frame = module.getFrame();
        JMenu misc = new JMenu( "Special Features" );
        misc.setMnemonic( 'S' );
        JMenuItem jep = new JMenuItem( "Expression Evaluator" );
        misc.add( jep );
        final JEPFrame jef = new JEPFrame( frame, module );
        jep.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                jef.setVisible( true );
            }
        } );

        final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem( "Invert X-Axis", false );
        jcbmi.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
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

}


