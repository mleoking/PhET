/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicPhetPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.movingman.common.PhetLookAndFeel;
import edu.colorado.phet.movingman.common.WiggleMe;
import edu.colorado.phet.movingman.common.math.RangeToRange;
import edu.colorado.phet.movingman.misc.JEPFrame;
import edu.colorado.phet.movingman.motion.MotionSuite;
import edu.colorado.phet.movingman.plots.BoxedPlot;
import edu.colorado.phet.movingman.plots.CursorGraphic;
import edu.colorado.phet.movingman.plots.PlotAndText;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
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
    private static boolean addJEP = true;

    private ManGraphic manGraphic;
    private RangeToRange manPositionTransform;

    private MMTimer recordingMMTimer;
    private MMTimer playbackMMTimer;
    private MovingManLayout layout;

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
    private Color backgroundColor;
    private Model model;
    private PlotSet plotSet;
    private WiggleMe wiggleMe;

    public void repaint() {
        getApparatusPanel().repaint( 0, 0, getApparatusPanel().getWidth(), getApparatusPanel().getHeight() );
    }

    public void repaintBackground( Rectangle rect ) {
        backgroundGraphic.paintBufferedImage( rect );
        getApparatusPanel().repaint( rect );
    }

    public void setWiggleMeVisible( boolean b ) {
        if( !b ) {
            wiggleMe.setVisible( false );
            getApparatusPanel().removeGraphic( wiggleMe );
            getModel().removeModelElement( wiggleMe );
        }
        else {
            wiggleMe.setVisible( true );
            getApparatusPanel().addGraphic( wiggleMe, 100 );
            getModel().addModelElement( wiggleMe );
        }
    }

    public class PlotSet {
        private PlotAndText accelerationPlot;
        private PlotAndText positionPlot;
        private PlotAndText velocityPlot;

        public PlotSet() {
            double maxPositionView = 12;
            double maxVelocity = 25;
            double maxAccel = 10;
            double xshiftVelocity = model.numSmoothingPosition * TIMER_SCALE / 2;
            double xshiftAcceleration = ( model.numVelocitySmoothPoints + model.numSmoothingPosition ) * TIMER_SCALE / 2;

            Stroke plotStroke = new BasicStroke( 3.0f );
            Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -maxPositionView, maxTime - minTime, maxPositionView * 2 );

            final BoxedPlot positionGraphic = new BoxedPlot( "Position", MovingManModule.this, model.position.getSmoothedDataSeries(), recordingMMTimer, Color.blue,
                                                             plotStroke, positionInputBox, backgroundGraphic, 0 );
            positionGraphic.setPaintYLines( new double[]{5, 10} );
            Point textCoord = layout.getTextCoordinates( 0 );
            ValueGraphic positionString = new ValueGraphic( MovingManModule.this, recordingMMTimer, playbackMMTimer, model.position.getSmoothedDataSeries(), "Position=", "m", textCoord.x, textCoord.y, positionGraphic );

            backgroundGraphic.addGraphic( positionGraphic, 3 );
            getApparatusPanel().addGraphic( positionString, 7 );

            positionPlot = new PlotAndText( positionGraphic, positionString );

            Rectangle2D.Double velocityInputBox = new Rectangle2D.Double( minTime, -maxVelocity, maxTime - minTime, maxVelocity * 2 );
            final BoxedPlot velocityGraphic = new BoxedPlot( "Velocity", MovingManModule.this, model.velocity.getSmoothedDataSeries(), recordingMMTimer, Color.red, plotStroke, velocityInputBox, backgroundGraphic, xshiftVelocity );

            velocityGraphic.setPaintYLines( new double[]{10, 20} );
            ValueGraphic velocityString = new ValueGraphic( MovingManModule.this, recordingMMTimer, playbackMMTimer, model.velocity.getSmoothedDataSeries(), "Velocity=", "m/s", textCoord.x, textCoord.y, velocityGraphic );

            backgroundGraphic.addGraphic( velocityGraphic, 4 );
            getApparatusPanel().addGraphic( velocityString, 7 );
            velocityPlot = new PlotAndText( velocityGraphic, velocityString );

            Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, maxTime - minTime, maxAccel * 2 );
            BoxedPlot accelPlot = new BoxedPlot( "Acceleration", MovingManModule.this, model.acceleration.getSmoothedDataSeries(), recordingMMTimer, Color.black, plotStroke, accelInputBox, backgroundGraphic, xshiftAcceleration );
            backgroundGraphic.addGraphic( accelPlot, 5 );

            accelPlot.setPaintYLines( new double[]{25, 50} );
            ValueGraphic accelString = new ValueGraphic( MovingManModule.this, recordingMMTimer, playbackMMTimer, model.acceleration.getSmoothedDataSeries(), "Acceleration=", "m/s^2", textCoord.x, textCoord.y, accelPlot );
            getApparatusPanel().addGraphic( accelString, 5 );
            accelerationPlot = new PlotAndText( accelPlot, accelString );
        }

        public void setNumSmoothingPoints( int n ) {

            double xshiftVelocity = n * TIMER_SCALE / 2;
            double xshiftAcceleration = ( n + n ) * TIMER_SCALE / 2;
            velocityPlot.getPlot().setShift( xshiftVelocity );
            accelerationPlot.getPlot().setShift( xshiftAcceleration );
        }

        public void setPositionPlotMagnitude( double positionMagnitude ) {
            Rectangle2D.Double positionInputBox = new Rectangle2D.Double( minTime, -positionMagnitude, maxTime - minTime, positionMagnitude * 2 );
            positionPlot.getPlot().setInputRange( positionInputBox );
            backgroundGraphic.paintBufferedImage();
        }

        public void setVelocityPlotMagnitude( double maxVelocity ) {
            Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxVelocity, maxTime - minTime, maxVelocity * 2 );
            velocityPlot.getPlot().setInputRange( accelInputBox );
            backgroundGraphic.paintBufferedImage();
        }

        public void setAccelerationPlotMagnitude( double maxAccel ) {
            Rectangle2D.Double accelInputBox = new Rectangle2D.Double( minTime, -maxAccel, maxTime - minTime, maxAccel * 2 );
            accelerationPlot.getPlot().setInputRange( accelInputBox );
            backgroundGraphic.paintBufferedImage();
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
    }

    public class Model {
        private Man man;
        private AbstractClock clock;
        private SmoothDataSeries position;
        private SmoothDataSeries velocity;
        private SmoothDataSeries acceleration;
        private int numSmoothingPosition;
        private int numVelocitySmoothPoints;
        private int numAccSmoothPoints;

        public Model() {
            numSmoothingPosition = numSmoothingPoints;
            numVelocitySmoothPoints = numSmoothingPoints;
            numAccSmoothPoints = numSmoothingPoints;

            position = new SmoothDataSeries( numSmoothingPosition );
            velocity = new SmoothDataSeries( numVelocitySmoothPoints );
            acceleration = new SmoothDataSeries( numAccSmoothPoints );

            position.setDerivative( velocity );
            velocity.setDerivative( acceleration );
            man = new Man( 0, -maxManPosition, maxManPosition );
        }

        public void setNumSmoothingPoints( int n ) {
            position.setNumSmoothingPoints( n );
            velocity.setNumSmoothingPoints( n );
            acceleration.setNumSmoothingPoints( n );
        }

        public void reset() {
            man.reset();
            position.reset();
            velocity.reset();
            acceleration.reset();
        }

        public void step( double dt ) {
            position.addPoint( man.getX() );
            position.updateSmoothedSeries();
            position.updateDerivative( dt * TIMER_SCALE );
            velocity.updateSmoothedSeries();
            velocity.updateDerivative( dt * TIMER_SCALE );
            acceleration.updateSmoothedSeries();
        }

        public void setReplayTimeIndex( int timeIndex ) {
            if( timeIndex < position.numSmoothedPoints() && timeIndex >= 0 ) {
                double x = position.smoothedPointAt( timeIndex );
                man.setX( x );
            }
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

        public double getFinalManPosition() {
            if( position.getData().size() > 0 ) {
                return position.getData().getLastPoint();
            }
            else {
                return man.getX();
            }
        }
    }

    public MovingManModule( AbstractClock clock ) throws IOException {
        super( "The Moving Man" );
        model = new Model();
        model.clock = clock;
        ApparatusPanel mypanel = new ApparatusPanel() {
            public void repaint( Rectangle r ) {
                super.repaint( r );
            }
        };
        mypanel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );
        super.setApparatusPanel( mypanel );
        super.setModel( new BaseModel() );

        backgroundColor = new Color( 250, 190, 240 );
        backgroundGraphic = new BufferedGraphicForComponent( 0, 0, 800, 400, backgroundColor, getApparatusPanel() );

        manPositionTransform = new RangeToRange( -maxManPosition, maxManPosition, 50, 600 );
        manGraphic = new ManGraphic( this, model.man, 0, manPositionTransform );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                manGraphic.update();
            }
        } );
        getApparatusPanel().addGraphic( manGraphic, 1 );
        recordingMMTimer = new MMTimer( "Record", TIMER_SCALE );
        playbackMMTimer = new MMTimer( "Playback", TIMER_SCALE );
        timerGraphic = new TimeGraphic( this, recordingMMTimer, playbackMMTimer, 80, 40 );
        getApparatusPanel().addGraphic( timerGraphic, 1 );

        walkwayGraphic = new WalkWayGraphic( this, 11 );
        backgroundGraphic.addGraphic( walkwayGraphic, 0 );
        layout = new MovingManLayout();
        layout.setApparatusPanelHeight( 800 );
        layout.setApparatusPanelWidth( 400 );
        layout.setNumPlots( 3 );
        layout.relayout();
//        setupPlots();
        plotSet = new PlotSet();
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
                    double manx = ( model.man.getX() );
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
        model.man.addObserver( crashObserver );
        Color cursorColor = Color.black;
        cursorGraphic = new CursorGraphic( this, playbackMMTimer, cursorColor, layout.getPlotY( 0 ), layout.getTotalPlotHeight() );
        getApparatusPanel().addGraphic( cursorGraphic, 6 );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                initMediaPanel();
                relayoutApparatusPanel();
            }

            public void componentResized( ComponentEvent e ) {
                getModel().execute( new Command() {
                    public void doIt() {
                        initMediaPanel();
                        relayoutApparatusPanel();
                    }
                } );
            }
        } );
        dragMode = new RecordMode( this );
        playbackMode = new PlaybackMode( this );
        motionMode = new MotionMode( this );
        setMode( dragMode );

        getApparatusPanel().addGraphic( backgroundGraphic, 0 );
        clock.addClockTickListener( getModel() );

        Point2D start = manGraphic.getRectangle().getLocation();
        start = new Point2D.Double( start.getX() + 50, start.getY() + 50 );
        wiggleMe = new WiggleMe( getApparatusPanel(), start,
                                 new ImmutableVector2D.Double( 0, 1 ), 15, .02, "Drag the Man" );
        setWiggleMeVisible( true );
    }

    public Color getBackgroundColor() {
        return backgroundColor;
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
        model.setNumSmoothingPoints( n );
        plotSet.setNumSmoothingPoints( n );
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
        plotSet.setPositionPlotMagnitude( positionMagnitude );
    }

    public void setVelocityPlotMagnitude( double maxVelocity ) {
        plotSet.setVelocityPlotMagnitude( maxVelocity );
    }

    public void setAccelerationPlotMagnitude( double maxAccel ) {
        plotSet.setAccelerationPlotMagnitude( maxAccel );
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
        return plotSet.accelerationPlot;
    }

    public PlotAndText getPositionPlot() {
        return plotSet.positionPlot;
    }

    public PlotAndText getVelocityPlot() {
        return plotSet.velocityPlot;
    }

    boolean initMediaPanel = false;

    public SmoothDataSeries getPosition() {
        return model.position;
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
        movingManControlPanel.setFrame( frame );
    }

    public static void fixComponent( Container jc ) {
        jc.invalidate();
        jc.validate();
        jc.repaint();
    }

    public Man getMan() {
        return model.man;
    }

    public MMTimer getRecordingTimer() {
        return recordingMMTimer;
    }

    public void reset( double modelCoordinate ) {
        setReplayTimeIndex( 0 );
        model.reset();
        recordingMMTimer.reset();
        for( int i = 0; i < numResetPoints; i++ ) {
            double dt = 1;
            model.man.setX( modelCoordinate );
            recordingMMTimer.stepInTime( dt );
            model.step( dt );
        }
        cursorGraphic.setVisible( false );
        playbackMMTimer.setTime( 0 );
        getPositionString().update( null, null );
        getVelocityString().update( null, null );
        getAccelString().update( null, null );
        backgroundGraphic.paintBufferedImage();
        getApparatusPanel().repaint();
    }

    private void setReplayTimeIndex( int timeIndex ) {
        model.setReplayTimeIndex( timeIndex );
    }

    public void setReplayTime( double requestedTime ) {
        /**Find the position for the time.*/
        int timeIndex = (int)( requestedTime / TIMER_SCALE );
        setReplayTimeIndex( timeIndex );
    }

    public void rewind() {
        playbackMMTimer.setTime( 0 );
        model.man.reset();
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
        return plotSet.positionPlot.getPlot();
    }

    public BoxedPlot getAccelerationGraphic() {
        return plotSet.accelerationPlot.getPlot();
    }

    public BoxedPlot getVelocityGraphic() {
        return plotSet.velocityPlot.getPlot();
    }

    public ValueGraphic getPositionString() {
        return plotSet.positionPlot.getText();
    }

    public ValueGraphic getVelocityString() {
        return plotSet.velocityPlot.getText();
    }

    public ValueGraphic getAccelString() {
        return plotSet.accelerationPlot.getText();
    }

    public CursorGraphic getCursorGraphic() {
        return cursorGraphic;
    }

    public void cursorMovedToTime( double requestedTime ) {
        if( requestedTime < 0 || requestedTime > recordingMMTimer.getTime() ) {
            return;
        }
        playbackMMTimer.setTime( requestedTime );
        int timeIndex = (int)( requestedTime / TIMER_SCALE );
        if( timeIndex < model.position.numSmoothedPoints() && timeIndex >= 0 ) {
            double x = model.position.smoothedPointAt( timeIndex );
            model.man.setX( x );
        }
    }

    public int getVisiblePlotCount() {
        return plotSet.getVisiblePlotCount();
    }

    public void setPositionGraphVisible( boolean selected ) {
        setPlotVisible( plotSet.positionPlot, selected );
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
        setPlotVisible( plotSet.velocityPlot, selected );
    }

    public void setAccelerationGraphVisible( boolean selected ) {
        setPlotVisible( plotSet.accelerationPlot, selected );
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
        return model.getVelocity();
    }

    public SmoothDataSeries getVelocityData() {
        return model.velocity;
    }

    public SmoothDataSeries getAcceleration() {
        return model.acceleration;
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
        return model.getFinalManPosition();
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

    public MMTimer getPlaybackTimer() {
        return playbackMMTimer;
    }

    public boolean isDragMode() {
        return mode == dragMode;
    }

    public boolean isTakingData() {
        return !isPaused() && mode.isTakingData();
    }

//    public void disableIdeaGraphics() {
//        manGraphic.setShowIdea( false );
//    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException {
        SmoothUtilities.setFractionalMetrics( false );
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        AbstractClock clock = new SwingTimerClock( 1, 30, true );
//        AbstractClock clock = new SwingTimerClock( 1, 30, false );
        MovingManModule m = new MovingManModule( clock );
        FrameSetup setup = new FrameSetup.MaxExtent();

        ApplicationModel desc = new ApplicationModel( "The Moving Man", "The Moving Man Application.",
                                                      ".02-beta-x 10-18-2004", setup, m, clock );
        PhetApplication tpa = new PhetApplication( desc );

        final PhetFrame frame = tpa.getApplicationView().getPhetFrame();
        m.setFrame( frame );
        tpa.getApplicationView().getBasicPhetPanel().add( m.getControlPanel(), BorderLayout.WEST );
        if( addJEP ) {
            addJEP( m );
        }
        tpa.startApplication();
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


