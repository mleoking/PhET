/*PhET, 2004.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.transforms.functions.RangeToRange;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Clock;
import edu.colorado.phet.common.model.ClockTickListener;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.*;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.movingman.application.motionsuites.MotionSuite;
import edu.colorado.phet.movingman.elements.*;
import edu.colorado.phet.movingman.elements.Timer;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:19:49 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManModule extends Module {
    private int minTime = 0;
    public static final int numSmoothingPoints = 10;
    private int maxManPosition = 10;
    public static final double TIMER_SCALE = 1.0 / 50;
    private int numResetPoints = 1;//number of points to use in the reset routine.
    private double maxTime = 20;//high time in seconds.
    private boolean paused = true;

    private Man man;
    private ManGraphic manGraphic;
    private RangeToRange manPositionTransform;

    private DefaultSmoothedDataSeries position;
    private DefaultSmoothedDataSeries velocity;
    private DefaultSmoothedDataSeries acceleration;

    private Timer recordingTimer;
    private Timer playbackTimer;
    private MovingManLayout layout;

    private PlotAndText accelerationPlot;
    private PlotAndText positionPlot;
    private PlotAndText velocityPlot;

    private Mode mode;//the current mode.

    private RecordMode dragMode;
    private PlaybackMode playbackMode;
    private MotionMode motionMode;

    private CursorGraphic cursorGraphic;
    private MovingManControlPanel movingManControlPanel;
    private TimeGraphic timerGraphic;
    private BufferedGraphicForComponent backgroundGraphic;
    private WalkWayGraphic walkwayGraphic;

    private Color purple = new Color( 200, 175, 250 );
    private PhetFrame frame;
    private ModelElement mainModelElement;
    private Observer crashObserver;
    private int numSmoothingPosition;
    private int numVelocitySmoothPoints;
    private int numAccSmoothPoints;
    private static boolean addJEP = true;
    private Color backgroundColor;

    public MovingManModule() throws IOException {
        super( "The Moving Man" );
//        ApparatusPanel mypanel = new RepaintDebugPanel();
        ApparatusPanel mypanel = new ApparatusPanel();
        mypanel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        super.setApparatusPanel( mypanel );
        final BaseModel model = new BaseModel() {
            public void clockTicked( Clock c, double dt ) {
                executeQueue();
                stepInTime( dt );
            }
        };
        super.setModel( model );

        numSmoothingPosition = numSmoothingPoints;
        numVelocitySmoothPoints = numSmoothingPoints;
        numAccSmoothPoints = numSmoothingPoints;

        position = new DefaultSmoothedDataSeries( numSmoothingPosition );
        velocity = new DefaultSmoothedDataSeries( numVelocitySmoothPoints );
        acceleration = new DefaultSmoothedDataSeries( numAccSmoothPoints );

        position.setDerivative( velocity );
        velocity.setDerivative( acceleration );

        backgroundColor = new Color( 250, 190, 240 );
        backgroundGraphic = new BufferedGraphicForComponent( 0, 0, 800, 400, backgroundColor, getApparatusPanel() );
        man = new Man( 0, -maxManPosition, maxManPosition );

        manPositionTransform = new RangeToRange( -maxManPosition, maxManPosition, 50, 600 );
        manGraphic = new ManGraphic( this, man, 0, manPositionTransform );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                manGraphic.update();
            }
        } );
        getApparatusPanel().addGraphic( manGraphic, 1 );
        recordingTimer = new Timer( "Record", TIMER_SCALE );
        playbackTimer = new Timer( "Playback", TIMER_SCALE );
        timerGraphic = new TimeGraphic( this, recordingTimer, playbackTimer, 80, 40 );
        getApparatusPanel().addGraphic( timerGraphic, 1 );

        walkwayGraphic = new WalkWayGraphic( this, 11 );
        backgroundGraphic.addGraphic( walkwayGraphic, 0 );
        layout = new MovingManLayout();
        layout.setApparatusPanelHeight( 800 );
        layout.setApparatusPanelWidth( 400 );
        layout.setNumPlots( 3 );
        layout.relayout();
        setupPlots();
        movingManControlPanel = new MovingManControlPanel( this );
        super.setControlPanel( movingManControlPanel );
        mainModelElement = new ModelElement() {
            public void stepInTime( double dt ) {
                if( !paused ) {
                    mode.stepInTime( dt );
                }
            }
        };
        getModel().addModelElement( mainModelElement );
        crashObserver = new Observer() {
            public void update( Observable o, Object arg ) {
                if( isMotionMode() ) {
                    double manx = ( man.getX() );
                    double manv = getVelocity();
                    if( manx >= maxManPosition && manv > 0 ) {
                        motionMode.collidedWithWall();
                    }
                    else if( manx <= -maxManPosition && manv < 0 ) {
                        motionMode.collidedWithWall();
                    }
                }
            }
        };
        man.addObserver( crashObserver );
        Color cursorColor = Color.black;
        cursorGraphic = new CursorGraphic( this, playbackTimer, cursorColor, null, layout.getPlotY( 0 ), layout.getTotalPlotHeight() );
        getApparatusPanel().addGraphic( cursorGraphic, 6 );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                relayoutApparatusPanel();
            }

            public void componentResized( ComponentEvent e ) {
                getModel().execute( new Command() {
                    public void doIt() {
                        relayoutApparatusPanel();
                    }
                } );
            }
        } );
        dragMode = new RecordMode( this );
        playbackMode = new PlaybackMode( this );
        motionMode = new MotionMode( this );
        setMode( dragMode );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                initMediaPanel();
            }

            public void componentResized( ComponentEvent e ) {
                getModel().execute( new Command() {
                    public void doIt() {
                        initMediaPanel();
                    }
                } );
            }
        } );
        getApparatusPanel().addGraphic( backgroundGraphic, 0 );

    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    private void setupPlots() {
        double maxPositionView = 12;
        double maxVelocity = 25;
        double maxAccel = 10;
        double xshiftVelocity = numSmoothingPosition * TIMER_SCALE / 2;
        double xshiftAcceleration = ( numVelocitySmoothPoints + numSmoothingPosition ) * TIMER_SCALE / 2;

        Stroke plotStroke = new BasicStroke( 3.0f );
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -maxPositionView, maxTime - minTime, maxPositionView * 2 );

        final BoxedPlot positionGraphic = new BoxedPlot( this, position.getSmoothedDataSeries(), recordingTimer, Color.blue,
                                                         plotStroke, positionInputBox, backgroundGraphic, 0 );
        GridLineGraphic positionGrid = new GridLineGraphic( positionGraphic, new BasicStroke( 1.0f ), Color.gray, 10, 13, Color.yellow, "Position" );
        positionGrid.setPaintYLines( new double[]{-10, -5, 0, 5, 10} );
        Point textCoord = layout.getTextCoordinates( 0 );
        ValueGraphic positionString = new ValueGraphic( this, recordingTimer, playbackTimer, position.getSmoothedDataSeries(), "Position=", "m", textCoord.x, textCoord.y, positionGraphic );

        backgroundGraphic.addGraphic( positionGrid, 2 );
        backgroundGraphic.addGraphic( positionGraphic, 3 );
        getApparatusPanel().addGraphic( positionString, 7 );

        positionPlot = new PlotAndText( positionGraphic, positionString, positionGrid );

        Rectangle2D.Double velocityInputBox = new Rectangle2D.Double( minTime, -maxVelocity, maxTime - minTime, maxVelocity * 2 );
        final BoxedPlot velocityGraphic = new BoxedPlot( this, velocity.getSmoothedDataSeries(), recordingTimer, Color.red, plotStroke, velocityInputBox, backgroundGraphic, xshiftVelocity );

        GridLineGraphic velocityGrid = new GridLineGraphic( velocityGraphic, new BasicStroke( 1.0f ), Color.gray, 10, 5, Color.yellow, "Velocity" );
        velocityGrid.setPaintYLines( new double[]{-20, -10, 0, 10, 20} );
        ValueGraphic velocityString = new ValueGraphic( this, recordingTimer, playbackTimer, velocity.getSmoothedDataSeries(), "Velocity=", "m/s", textCoord.x, textCoord.y, velocityGraphic );

        backgroundGraphic.addGraphic( velocityGraphic, 4 );
        backgroundGraphic.addGraphic( velocityGrid, 2 );
        getApparatusPanel().addGraphic( velocityString, 7 );
        velocityPlot = new PlotAndText( velocityGraphic, velocityString, velocityGrid );

        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, maxTime - minTime, maxAccel * 2 );
        BoxedPlot accelPlot = new BoxedPlot( this, acceleration.getSmoothedDataSeries(), recordingTimer, Color.black, plotStroke, accelInputBox, backgroundGraphic, xshiftAcceleration );
        backgroundGraphic.addGraphic( accelPlot, 5 );

        GridLineGraphic accelGrid = new GridLineGraphic( accelPlot, new BasicStroke( 1.0f ), Color.gray, 10, 5, Color.yellow, "Acceleration" );
        accelGrid.setPaintYLines( new double[]{-50, -25, 0, 25, 50} );
        backgroundGraphic.addGraphic( accelGrid, 2 );
        ValueGraphic accelString = new ValueGraphic( this, recordingTimer, playbackTimer, acceleration.getSmoothedDataSeries(), "Acceleration=", "m/s^2", textCoord.x, textCoord.y, accelPlot );
        getApparatusPanel().addGraphic( accelString, 5 );
        accelerationPlot = new PlotAndText( accelPlot, accelString, accelGrid );
    }

    public int getNumResetPoints() {
        return numResetPoints;
    }

    public boolean isPaused() {
        return paused;
    }

    ArrayList stateListeners = new ArrayList();

    public BufferedGraphicForComponent getBackground() {
        return backgroundGraphic;
    }


    interface StateListener {
        void stateChanged( MovingManModule module );
    }

    public void addStateListener( StateListener stateListener ) {
        stateListeners.add( stateListener );
    }

    public void setPaused( boolean paused ) {
        if( paused != this.paused ) {
            this.paused = paused;
            for( int i = 0; i < stateListeners.size(); i++ ) {
                StateListener stateListener = (StateListener)stateListeners.get( i );
                stateListener.stateChanged( this );
            }
            if( !paused ) {
                movingManControlPanel.motionStarted();
                movingManControlPanel.goPressed();
            }
        }
    }

    public Color getPurple() {
        return purple;
    }

    public void setNumSmoothingPoints( int n ) {
        position.setNumSmoothingPoints( n );
        velocity.setNumSmoothingPoints( n );
        acceleration.setNumSmoothingPoints( n );
        double xshiftVelocity = n * TIMER_SCALE / 2;
        double xshiftAcceleration = ( n + n ) * TIMER_SCALE / 2;
        velocityPlot.getPlot().setShift( xshiftVelocity );
        accelerationPlot.getPlot().setShift( xshiftAcceleration );
    }

    /**
     * Overrides.
     */
    public void activateInternal( PhetApplication app ) {
//        getModel().addObserver( getApparatusPanel() );
        this.activate( app );
    }

    public void deactivateInternal( PhetApplication app ) {
//        getModel().deleteObserver( getApparatusPanel() );
        this.deactivate( app );
    }

    public void setRightDirPositive( boolean rightPos ) {
        RangeToRange newTransform;
        double appPanelWidth = getApparatusPanel().getWidth();
        int inset = 50;
        if( rightPos ) {//as usual
            newTransform = new RangeToRange( -maxManPosition, maxManPosition, inset, appPanelWidth - inset );
            walkwayGraphic.setTreeX( -10 );
            walkwayGraphic.setHouseX( 10 );
        }
        else {
            newTransform = new RangeToRange( maxManPosition, -maxManPosition, inset, appPanelWidth - inset );
            walkwayGraphic.setTreeX( 10 );
            walkwayGraphic.setHouseX( -10 );
        }
        manGraphic.setTransform( newTransform );
        setManTransform( newTransform );
        setMode( dragMode );
        reset();
        setPaused( true );
    }

    public void repaintBackground() {
        backgroundGraphic.paintBufferedImage();
        getApparatusPanel().repaint();
    }

    public void setPositionPlotMagnitude( double positionMagnitude ) {
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -positionMagnitude, maxTime - minTime, positionMagnitude * 2 );
        positionPlot.getPlot().setInputBox( positionInputBox );
        backgroundGraphic.paintBufferedImage();
    }

    public void setVelocityPlotMagnitude( double maxVelocity ) {
        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxVelocity, maxTime - minTime, maxVelocity * 2 );
        velocityPlot.getPlot().setInputBox( accelInputBox );
        backgroundGraphic.paintBufferedImage();
    }

    public void setAccelerationPlotMagnitude( double maxAccel ) {
        Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, maxTime - minTime, maxAccel * 2 );
        accelerationPlot.getPlot().setInputBox( accelInputBox );
        backgroundGraphic.paintBufferedImage();
    }

    public RangeToRange getManPositionTransform() {
        return manPositionTransform;
    }

    private void initMediaPanel() {
        if( initMediaPanel ) {
            return;
        }
        final JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( getApparatusPanel() );
        JPanel jp = (JPanel)parent.getContentPane();
        BasicPhetPanel bpp = (BasicPhetPanel)jp;
        bpp.setAppControlPanel( movingManControlPanel.getMediaPanel() );
        initMediaPanel = true;
    }

    public ManGraphic getManGraphic() {
        return manGraphic;
    }

    public MotionMode getMotionMode() {
        return motionMode;
    }

    public PlotAndText getAccelerationPlot() {
        return accelerationPlot;
    }

    public PlotAndText getPositionPlot() {
        return positionPlot;
    }

    public PlotAndText getVelocityPlot() {
        return velocityPlot;
    }

    boolean initMediaPanel = false;

    public DefaultSmoothedDataSeries getPosition() {
        return position;
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

    private void relayoutApparatusPanel() {
        layout.relayout( this );
        Component c = getApparatusPanel();
        backgroundGraphic.setSize( c.getWidth(), c.getHeight() );
        backgroundGraphic.paintBufferedImage();
        getApparatusPanel().repaint();
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    private void setFrame( PhetFrame frame ) {
        this.frame = frame;
        movingManControlPanel.setFrame( frame );
    }

    public static void fixComponent( Container jc ) {
        jc.invalidate();
        jc.validate();
        jc.repaint();
    }

    public Man getMan() {
        return man;
    }

    public Timer getRecordingTimer() {
        return recordingTimer;
    }

    public void reset( double modelCoordinate ) {
        setReplayTimeIndex( 0 );
        man.reset();
        position.reset();
        velocity.reset();
        acceleration.reset();
        recordingTimer.reset();

        for( int i = 0; i < numResetPoints; i++ ) {
            double dt = 1;
            man.setX( modelCoordinate );
            recordingTimer.stepInTime( dt );
            position.addPoint( man.getX() );
            position.updateSmoothedSeries();
            position.updateDerivative( dt * TIMER_SCALE );
            velocity.updateSmoothedSeries();
            velocity.updateDerivative( dt * TIMER_SCALE );
            acceleration.updateSmoothedSeries();
        }
        cursorGraphic.setVisible( false );
        playbackTimer.setTime( 0 );
        getPositionString().update( null, null );
        getVelocityString().update( null, null );
        getAccelString().update( null, null );
        backgroundGraphic.paintBufferedImage();
        getApparatusPanel().repaint();
    }

    private void setReplayTimeIndex( int timeIndex ) {
        if( timeIndex < position.numSmoothedPoints() && timeIndex >= 0 ) {
            double x = position.smoothedPointAt( timeIndex );
            man.setX( x );
        }
    }

    public void setReplayTime( double requestedTime ) {
        /**Find the position for the time.*/
        int timeIndex = (int)( requestedTime / TIMER_SCALE );
        setReplayTimeIndex( timeIndex );
    }

    public void rewind() {
        playbackTimer.setTime( 0 );
        man.reset();
    }

    public void setRecordMode() {
        setMode( dragMode );
    }

    public void startPlaybackMode( double playbackSpeed ) {
        playbackMode.setPlaybackSpeed( playbackSpeed );
        setMode( playbackMode );
        setPaused( false );
    }

    public boolean isRecording() {
        return mode == dragMode && !isPaused();
    }

    public void setMotionMode( MotionSuite mac ) {
        motionMode.setMotionSuite( mac );
        setMode( motionMode );
        movingManControlPanel.motionStarted();
        setPaused( true );
    }

    public BoxedPlot getPositionGraphic() {
        return positionPlot.getPlot();
    }

    public BoxedPlot getAccelerationGraphic() {
        return accelerationPlot.getPlot();
    }

    public BoxedPlot getVelocityGraphic() {
        return velocityPlot.getPlot();
    }

    public ValueGraphic getPositionString() {
        return positionPlot.getText();
    }

    public ValueGraphic getVelocityString() {
        return velocityPlot.getText();
    }

    public ValueGraphic getAccelString() {
        return accelerationPlot.getText();
    }

    public CursorGraphic getCursorGraphic() {
        return cursorGraphic;
    }

    public void cursorMovedToTime( double requestedTime ) {
        if( requestedTime < 0 || requestedTime > recordingTimer.getTime() ) {
            return;
        }
        playbackTimer.setTime( requestedTime );
        int timeIndex = (int)( requestedTime / TIMER_SCALE );
        if( timeIndex < position.numSmoothedPoints() && timeIndex >= 0 ) {
            double x = position.smoothedPointAt( timeIndex );
            man.setX( x );
        }
    }

    public int getVisiblePlotCount() {
        int sum = 0;
        if( positionPlot.isVisible() ) {
            sum++;
        }
        if( velocityPlot.isVisible() ) {
            sum++;
        }
        if( accelerationPlot.isVisible() ) {
            sum++;
        }
        return sum;
    }

    public void setPositionGraphVisible( boolean selected ) {
        setPlotVisible( positionPlot, selected );
    }

    private void setPlotVisible( PlotAndText plot, boolean selected ) {
        plot.setVisible( selected );
        int plotCount = getVisiblePlotCount();
        layout.setNumPlots( plotCount );
        relayoutApparatusPanel();
        if( plotCount == 0 ) {
            cursorGraphic.setVisible( false );
        }
        else if( cursorGraphic.isVisible() ) {
            cursorGraphic.setVisible( true );
        }
    }

    public void setVelocityGraphVisible( boolean selected ) {
        setPlotVisible( velocityPlot, selected );
    }

    public void setAccelerationGraphVisible( boolean selected ) {
        setPlotVisible( accelerationPlot, selected );
    }

    public boolean isMotionMode() {
        return mode == motionMode;
    }

    public void setManTransform( RangeToRange transform ) {
        this.manPositionTransform = transform;
    }

    public void reset() {
        setPaused( true );
        reset( 0 );
        motionMode.reset();
    }

    public double getVelocity() {
        if( velocity == null ) {
            return 0;
        }
        if( velocity.numSmoothedPoints() == 0 ) {
            return 0;
        }
        else {
            return velocity.smoothedPointAt( velocity.numSmoothedPoints() - 1 );
        }
    }

    public DefaultSmoothedDataSeries getVelocityData() {
        return velocity;
    }

    public DefaultSmoothedDataSeries getAcceleration() {
        return acceleration;
    }

    public double getTimeScale() {
        return TIMER_SCALE;
    }

    public void setInitialPosition( double init ) {
        reset( init );
    }

    public void setMotionSuite( MotionSuite motionSuite ) {
        motionMode.setMotionSuite( motionSuite );
    }

    public void goPressed() {
        movingManControlPanel.goPressed();
    }

    public double getFinalManPosition() {
        if( position.getData().size() > 0 ) {
            return position.getData().getLastPoint();
        }
        else {
            return man.getX();
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public MovingManControlPanel getMovingManControlPanel() {
        return movingManControlPanel;
    }

    private static void addJEP( MovingManModule module ) {
        final JFrame frame = module.getFrame();
        JMenu misc = new JMenu( "Misc" );
        JMenuItem jep = new JMenuItem( "Expression Evaluator" );
        misc.add( jep );
        final JEPFrame jef = new JEPFrame( frame, module );
        jep.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                jef.setVisible( true );
            }
        } );
        frame.getJMenuBar().add( misc );
    }

    public double getMaxTime() {
        return maxTime;
    }

    public double getMaxManPosition() {
        return maxManPosition;
    }

    public Timer getPlaybackTimer() {
        return playbackTimer;
    }

    public boolean isDragMode() {
        return mode == dragMode;
    }

    public boolean isTakingData() {
        return !isPaused() && mode.isTakingData();
    }

    public void disableIdeaGraphics() {
        manGraphic.setShowIdea( false );
    }


    /**
     * User: Sam Reid
     * Date: Jul 1, 2004
     * Time: 3:33:37 AM
     * Copyright (c) Jul 1, 2004 by Sam Reid
     */
    public static class RepaintDebugGraphic implements Graphic, ClockTickListener {
        private int r = 255;
        private int g = 255;
        private int b = 255;
        private MovingManModule module;
        private ApparatusPanel panel;
        private boolean active = false;
        private Clock clock;

        public RepaintDebugGraphic( MovingManModule module, ApparatusPanel panel, Clock clock ) {
            this.module = module;
            this.panel = panel;
            this.clock = clock;
            setActive( true );
        }

        GraphicsState state = new GraphicsState();

        public void paint( Graphics2D gr ) {
            state.saveState( gr );
            gr.setColor( new Color( r, g, b ) );
            gr.fillRect( 0, 0, panel.getWidth(), panel.getHeight() );
            state.restoreState( gr );
        }

        public void clockTicked( Clock c, double dt ) {
            r = ( r - 1 + 255 ) % 255;
            g = ( g - 2 + 255 ) % 255;
            b = ( b - 3 + 255 ) % 255;
            module.repaintBackground();
        }

        public void setActive( boolean active ) {
            if( this.active == active ) {
                return;
            }
            this.active = active;
            if( active ) {
                clock.addClockTickListener( this );
                panel.addGraphic( this, -100 );
            }
            else {
                clock.removeClockTickListener( this );
                panel.removeGraphic( this );
            }
        }
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException {
//        SmoothUtilities.setAntialias( false );
        SmoothUtilities.setFractionalMetrics( false );
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        MovingManModule m = new MovingManModule();
        FrameSetup setup = new MaximizeFrame();
        ApplicationDescriptor desc = new ApplicationDescriptor( "The Moving Man", "The Moving Man Application.",
                                                                ".01-beta-x 8-6-2004", setup );
        PhetApplication tpa = new PhetApplication( desc, m );

        final PhetFrame frame = tpa.getApplicationView().getPhetFrame();
        m.setFrame( frame );
//        new JDialog( MovingManModule.frame, "axl", false ).setVisible( true );

        if( addJEP ) {
            addJEP( m );
        }
        tpa.startApplication( m );
        frame.setVisible( true );
        fixComponent( frame.getContentPane() );
        frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
        frame.invalidate();
        frame.validate();
        frame.repaint();
        m.repaintBackground();
        m.dragMode.initialize();
        m.cursorGraphic.setVisible( false );
        m.getApparatusPanel().repaint();
        m.cursorGraphic.setVisible( false );

        final Runnable dofix = new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 300 );
                    fixComponent( frame.getContentPane() );
                    fixComponent( frame );
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
            }

            public void windowIconified( WindowEvent e ) {
            }

            public void windowOpened( WindowEvent e ) {
                new Thread( dofix ).start();
            }
        } );
        new Thread( dofix ).start();
//        RepaintDebugGraphic rdp = new RepaintDebugGraphic( m, m.getApparatusPanel(), tpa.getApplicationModel().getClock() );
//        m.backgroundGraphic.addGraphic( rdp, -100 );

    }

}


