/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Clock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.command.Command;
import edu.colorado.phet.common.view.*;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.MaximizeFrame;
import edu.colorado.phet.common.view.MaximizeFrame;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.math.transforms.functions.RangeToRange;
import edu.colorado.phet.movingman.application.motionandcontrols.MotionAndControls;
import edu.colorado.phet.movingman.common.plaf.LectureLookAndFeel2;
import edu.colorado.phet.movingman.elements.*;
import edu.colorado.phet.movingman.elements.Timer;
import edu.colorado.phet.movingman.elements.stepmotions.MotionState;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:19:49 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManModule extends Module {
    public static final double TIMER_SCALE = 1.0 / 50;
    private int numResetPoints = 1;
    double maxTime = 20;

    MotionState motionState = new MotionState();
    private double playbackSpeed;
    private Man man;
    private ManGraphic manGraphic;
    private RangeToRange manPositionTransform;
    DefaultSmoothedDataSeries position;
    DefaultSmoothedDataSeries velocity;
    DefaultSmoothedDataSeries acceleration;
    private Timer recordingTimer;
    private Timer playbackTimer;
    private MovingManLayout layout;
    PlotAndText accelerationPlot;
    PlotAndText positionPlot;
    PlotAndText velocityPlot;
    Mode mode;
    Mode recordMode;
    Mode playbackMode;
    Mode pauseMode;
    MotionMode motionMode;
    private CursorGraphic cursorGraphic;
    private MovingManControlPanel movingManControlPanel;
    private TimeGraphic timerGraphic;
    BufferedGraphicForComponent backgroundGraphic;
    private int maxManPosition;
    private int minTime;
    private int numSmoothingPoints = 10;
    private WalkWayGraphic walkwayGraphic;
    public static PhetFrame FRAME;
    private static boolean isLecture = false;

    public Color getPurple() {
        return purple;
    }

    private Color purple;

    public MotionState getMotionState() {
        return motionState;
    }

    public void setNumSmoothingPoints(int n) {
        position.setNumSmoothingPoints(n);
        velocity.setNumSmoothingPoints(n);
        acceleration.setNumSmoothingPoints(n);
        double xshiftVelocity = n * TIMER_SCALE / 2;
        double xshiftAcceleration = (n + n) * TIMER_SCALE / 2;
        velocityPlot.getPlot().setShift(xshiftVelocity);
        accelerationPlot.getPlot().setShift(xshiftAcceleration);
    }

    /**Overrides.*/
    public void activateInternal(PhetApplication app) {
//        System.out.println("Activate Internal" );
        getModel().addObserver(getApparatusPanel());
        this.activate(app);
    }

    /**Overrides.*/
    public void deactivateInternal(PhetApplication app) {
        getModel().deleteObserver(getApparatusPanel());
        this.deactivate(app);
    }

    int paintIndex = 0;

    public MovingManModule() {
        super("The Moving Man");
        ApparatusPanel mypanel = new ApparatusPanel();

        super.setApparatusPanel(mypanel);
        final BaseModel model = new BaseModel() {
            public void clockTicked(Clock c, double dt) {
//                super.clockTicked(c,dt);

                executeQueue();
                stepInTime(dt);
//                c.setAlive(false);
//                c.setRunning(false);
//                System.out.println("c = " + c);

//                if (paintTwice)
//                    updateObservers();
            }
        };
//        new javax.swing.Timer(20,new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                model.executeQueue();
//                model.stepInTime(1);
//            }
//        }).start();

        super.setModel(model);

        final int numSmoothingPosition = numSmoothingPoints;
        final int numVelocitySmoothPoints = numSmoothingPoints;
        final int numAccSmoothPoints = numSmoothingPoints;

        double xshiftVelocity = numSmoothingPosition * TIMER_SCALE / 2;
        double xshiftAcceleration = (numVelocitySmoothPoints + numSmoothingPosition) * TIMER_SCALE / 2;

        position = new DefaultSmoothedDataSeries(numSmoothingPosition);
        velocity = new DefaultSmoothedDataSeries(numVelocitySmoothPoints);
        acceleration = new DefaultSmoothedDataSeries(numAccSmoothPoints);

        position.setDerivative(velocity);
        velocity.setDerivative(acceleration);

        purple = new Color(200, 175, 250);
        Color backgroundColor = new Color(250, 190, 240);
        backgroundGraphic = new BufferedGraphicForComponent(0, 0, 800, 400, backgroundColor, getApparatusPanel());
        man = new Man(0);
        int y = 0;
        maxManPosition = 10;

        manPositionTransform = new RangeToRange(-maxManPosition, maxManPosition, 50, 600);
        manGraphic = new ManGraphic(this, man, y, manPositionTransform);
        getApparatusPanel().addGraphic(manGraphic, 1);
        recordingTimer = new Timer(TIMER_SCALE);
        playbackTimer = new Timer(TIMER_SCALE);
        timerGraphic = new TimeGraphic(recordingTimer, playbackTimer, 80, 40);
        getApparatusPanel().addGraphic(timerGraphic, 1);

        walkwayGraphic = new WalkWayGraphic(this, 11);
        backgroundGraphic.addGraphic(walkwayGraphic, 0);
        movingManControlPanel = new MovingManControlPanel(this);
        super.setControlPanel(movingManControlPanel);
        getModel().addModelElement(new ModelElement() {
            public void stepInTime(double dt) {
                mode.stepInTime(dt);
            }
        });
        man.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (isMotionMode()) {
                    double manx = (man.getX());
                    double manv = getVelocity();
                    if (manx >= 10 && manv > 0)
                        motionMode.collidedWithWall();
                    else if (manx <= -10 && manv < 0)
                        motionMode.collidedWithWall();

//                        if (manx >= 10) {
//                        motionMode.collidedWithWall();
//                    }
//                System.out.println("manx = " + manx);
                }
            }
        });
        //Time goes from 0..1
        //Man position goes from -10..10
        minTime = 0;
        double maxPositionView = 12;
        double maxVelocity = 25;
        double maxAccel = 10;
        layout = new MovingManLayout();
        layout.setApparatusPanelHeight(800);
        layout.setApparatusPanelWidth(400);
        layout.setNumPlots(3);
        layout.relayout();
//        Stroke plotStroke = new BasicStroke(2.0f);
        Stroke plotStroke = new BasicStroke(3.0f);
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double(minTime, -maxPositionView, maxTime - minTime, maxPositionView * 2);

        BoxedPlot positionGraphic = new BoxedPlot(this, position.getSmoothedDataSeries(), recordingTimer, Color.blue,
                plotStroke, positionInputBox, backgroundGraphic, 0);
        GridLineGraphic positionGrid = new GridLineGraphic(positionGraphic, new BasicStroke(1.0f), Color.gray, 10, 13, Color.yellow, "Position");
        positionGrid.setPaintYLines(new double[]{-10, -5, 0, 5, 10});
        Point textCoord = layout.getTextCoordinates(0);
        ValueGraphic positionString = new ValueGraphic(this, recordingTimer, playbackTimer, position.getSmoothedDataSeries(), "Position=", "m", textCoord.x, textCoord.y, positionGraphic);

        backgroundGraphic.addGraphic(positionGrid, 2);
        backgroundGraphic.addGraphic(positionGraphic, 3);
        getApparatusPanel().addGraphic(positionString, 7);

        positionPlot = new PlotAndText(positionGraphic, positionString, positionGrid);

        Rectangle2D.Double velocityInputBox = new Rectangle2D.Double(minTime, -maxVelocity, maxTime - minTime, maxVelocity * 2);
        BoxedPlot velocityGraphic = new BoxedPlot(this, velocity.getSmoothedDataSeries(), recordingTimer, Color.red, plotStroke, velocityInputBox, backgroundGraphic, xshiftVelocity);
        GridLineGraphic velocityGrid = new GridLineGraphic(velocityGraphic, new BasicStroke(1.0f), Color.gray, 10, 5, Color.yellow, "Velocity");
        velocityGrid.setPaintYLines(new double[]{-20, -10, 0, 10, 20});
        ValueGraphic velocityString = new ValueGraphic(this, recordingTimer, playbackTimer, velocity.getSmoothedDataSeries(), "Velocity=", "m/s", textCoord.x, textCoord.y, velocityGraphic);

        backgroundGraphic.addGraphic(velocityGraphic, 4);
        backgroundGraphic.addGraphic(velocityGrid, 2);
        getApparatusPanel().addGraphic(velocityString, 7);
        velocityPlot = new PlotAndText(velocityGraphic, velocityString, velocityGrid);

        Rectangle2D.Double accelInputBox = new Rectangle2D.Double(minTime, -maxAccel, maxTime - minTime, maxAccel * 2);
        BoxedPlot accelGraphic = new BoxedPlot(this, acceleration.getSmoothedDataSeries(), recordingTimer, Color.black, plotStroke, accelInputBox, backgroundGraphic, xshiftAcceleration);
        backgroundGraphic.addGraphic(accelGraphic, 5);

        GridLineGraphic accelGrid = new GridLineGraphic(accelGraphic, new BasicStroke(1.0f), Color.gray, 10, 5, Color.yellow, "Acceleration");
        accelGrid.setPaintYLines(new double[]{-50, -25, 0, 25, 50});
        backgroundGraphic.addGraphic(accelGrid, 2);
        ValueGraphic accelString = new ValueGraphic(this, recordingTimer, playbackTimer, acceleration.getSmoothedDataSeries(), "Acceleration=", "m/s^2", textCoord.x, textCoord.y, accelGraphic);
        getApparatusPanel().addGraphic(accelString, 5);
        accelerationPlot = new PlotAndText(accelGraphic, accelString, accelGrid);

        cursorGraphic = new CursorGraphic(this, playbackTimer, Color.black, null, layout.getPlotY(0), layout.getTotalPlotHeight());
        getApparatusPanel().addGraphic(cursorGraphic, 6);

        getApparatusPanel().addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                relayoutApparatusPanel();
            }

            public void componentResized(ComponentEvent e) {
                getModel().execute(new Command() {
                    public void doIt() {
                        relayoutApparatusPanel();
                    }
                });
            }
        });
        recordMode = new RecordMode(this);
        pauseMode = new PauseMode(this);
        playbackMode = new PlaybackMode(this);
        motionMode = new MotionMode(this);
        this.mode = recordMode;
        mode.initialize();

        getApparatusPanel().addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                initMediaPanel();
            }

            public void componentResized(ComponentEvent e) {
                getModel().execute(new Command() {
                    public void doIt() {
                        initMediaPanel();
                    }
                });
            }
        });
        getApparatusPanel().addGraphic(backgroundGraphic, 0);
    }

    /**For carl and Kathy's lecture on Thursday.*/
    public void setRightDirPositive(boolean rightPos) {
        RangeToRange newTransform;
        double appPanelWidth = getApparatusPanel().getWidth();
        int inset = 50;
        if (rightPos) {//as usual
            newTransform = new RangeToRange(-maxManPosition, maxManPosition, inset, appPanelWidth - inset);
            this.walkwayGraphic.setTreeX(-10);
            this.walkwayGraphic.setHouseX(10);
        } else {
            newTransform = new RangeToRange(maxManPosition, -maxManPosition, inset, appPanelWidth - inset);
            this.walkwayGraphic.setTreeX(10);
            this.walkwayGraphic.setHouseX(-10);
        }
        manGraphic.setTransform(newTransform);
        setManTransform(newTransform);
        reset();
    }

    public void repaintBackground() {
        backgroundGraphic.paintBufferedImage();
    }

    public void setVelocityPlotMagnitude(double maxVelocity) {
        Rectangle2D.Double accelInputBox = new Rectangle2D.Double(minTime, -maxVelocity, maxTime - minTime, maxVelocity * 2);
        velocityPlot.getPlot().setInputBox(accelInputBox);
        backgroundGraphic.paintBufferedImage();
    }

    public void setAccelerationPlotMagnitude(double maxAccel) {
        Rectangle2D.Double accelInputBox = new Rectangle2D.Double(minTime, -maxAccel, maxTime - minTime, maxAccel * 2);
        accelerationPlot.getPlot().setInputBox(accelInputBox);
        backgroundGraphic.paintBufferedImage();
    }

    public RangeToRange getManPositionTransform() {
        return manPositionTransform;
    }

    private void initMediaPanel() {
        if (initMediaPanel)
            return;
        final JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(getApparatusPanel());
        JPanel jp = (JPanel) parent.getContentPane();
//        O.d("JP=" + jp.getClass());
        BasicPhetPanel bpp = (BasicPhetPanel) jp;
        bpp.setAppControlPanel(movingManControlPanel.getMediaPanel());
        initMediaPanel = true;
//        parent.validate();
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

    private void setMode(Mode mode) {
        this.mode = mode;
        this.mode.initialize();
    }

    private void relayoutApparatusPanel() {
        layout.relayout(this);
        Component c = getApparatusPanel();
        backgroundGraphic.setSize(c.getWidth(), c.getHeight());
        backgroundGraphic.paintBufferedImage();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                    backgroundGraphic.paintBufferedImage();
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
            }
        }).start();
    }

    public void activate(PhetApplication app) {
    }

    public void deactivate(PhetApplication app) {
    }

    public static void main(String[] args) {

        LectureLookAndFeel2 LECTURE_LOOK_AND_FEEL;
        LECTURE_LOOK_AND_FEEL = new LectureLookAndFeel2();
        if (isLecture)
            try {
                UIManager.setLookAndFeel(LECTURE_LOOK_AND_FEEL);
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        MovingManModule m = new MovingManModule();
        FrameSetup setup = new MaximizeFrame();
        ApplicationDescriptor desc = new ApplicationDescriptor("The Moving Man", "The Moving Man Application.",
                ".01-beta-x 9-3-2003", setup);
        PhetApplication tpa = new PhetApplication(desc, m);
        tpa.startApplication(m);
        FRAME = tpa.getApplicationView().getPhetFrame();

//        if (args.length > 0) {
//            for (int i = 0; i < args.length; i++) {
//                if (args[i].toLowerCase().equals("-gi")) {
//                    String giUrl = args[i + 1];
//                    GILoader giLoader = new GILoader();
//                    GuidedInquiry gi = giLoader.loadGI(giUrl);
//                    Script script = new Script(gi);
//                    LaunchGuidedInquiryCmd lgiCmd = new
//                            LaunchGuidedInquiryCmd(tpa, script);
//                    lgiCmd.doIt();
//                }
//            }
//        }
        m.setPauseMode();
    }

    public Man getMan() {
        return man;
    }

    public Timer getRecordingTimer() {
        return recordingTimer;
    }

    public void reset(double modelCoordinate) {
        setReplayTimeIndex(0);
        man.reset();
        position.reset();
        velocity.reset();
        acceleration.reset();
        recordingTimer.reset();

        for (int i = 0; i < numResetPoints; i++) {
            double dt = 1;
            man.setX(modelCoordinate);
            recordingTimer.stepInTime(dt);
            position.addPoint(man.getX());
            position.updateSmoothedSeries();
            position.updateDerivative(dt * TIMER_SCALE);
            velocity.updateSmoothedSeries();
            velocity.updateDerivative(dt * TIMER_SCALE);
            acceleration.updateSmoothedSeries();
        }
        cursorGraphic.setVisible(false);
        playbackTimer.setTime(0);
        getPositionString().update(null, null);
        getVelocityString().update(null, null);
        getAccelString().update(null, null);
        backgroundGraphic.paintBufferedImage();
    }

    private void setReplayTimeIndex(int timeIndex) {
        if (timeIndex < position.numSmoothedPoints() && timeIndex >= 0) {
            double x = position.smoothedPointAt(timeIndex);
//            x /= this.manPositionScale;
            man.setX(x);
//            getApparatusPanel().repaint();//!!!!!!!!!
        }
    }

    public void setReplayTime(double requestedTime) {
        /**Find the position for the time.*/
        int timeIndex = (int) (requestedTime / TIMER_SCALE);
        setReplayTimeIndex(timeIndex);
    }

    public void rewind() {
        playbackTimer.setTime(0);
        man.reset();
    }

    public void setRecordMode() {
        setMode(recordMode);
    }

    public void setPlaybackMode(double playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
        setMode(playbackMode);
    }

    public boolean isRecording() {
        return mode == recordMode;
    }

    public boolean isPlaybackMode() {
        return mode == playbackMode;
    }

    public void setPauseMode() {
        setMode(pauseMode);
        movingManControlPanel.setPaused();
        cursorGraphic.updateYourself();
    }

    public void setMotionMode(MotionAndControls mac) {//StepMotion motion) {
        motionMode.setMotion(mac);
        setMode(motionMode);
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

    public void cursorMovedToTime(double requestedTime) {
        if (requestedTime < 0 || requestedTime > recordingTimer.getTime())
            return;
        playbackTimer.setTime(requestedTime);
        int timeIndex = (int) (requestedTime / TIMER_SCALE);
        if (timeIndex < position.numSmoothedPoints() && timeIndex >= 0) {
            double x = position.smoothedPointAt(timeIndex);
            man.setX(x);
        }
    }

    public MovingManControlPanel getMovingManControlPanel() {
        return movingManControlPanel;
    }

    public int getVisiblePlotCount() {
        int sum = 0;
        if (positionPlot.isVisible())
            sum++;
        if (velocityPlot.isVisible())
            sum++;
        if (accelerationPlot.isVisible())
            sum++;
        return sum;
    }


    public void setPositionGraphVisible(boolean selected) {
        setPlotVisible(positionPlot, selected);
    }

    private void setPlotVisible(PlotAndText plot, boolean selected) {
        plot.setVisible(selected);
        int plotCount = getVisiblePlotCount();
        layout.setNumPlots(plotCount);
        relayoutApparatusPanel();
        if (plotCount == 0)
            cursorGraphic.setVisible(false);
        else if (cursorGraphic.isVisible())
            cursorGraphic.setVisible(true);
    }

    public void setVelocityGraphVisible(boolean selected) {
        setPlotVisible(velocityPlot, selected);
    }

    public void setAccelerationGraphVisible(boolean selected) {
        setPlotVisible(accelerationPlot, selected);
    }

    public boolean isMotionMode() {
        return mode == motionMode;
    }

    public void setManTransform(RangeToRange transform) {
        this.manPositionTransform = transform;
    }

    public void reset() {
        reset(0);
    }

    public double getVelocity() {
        if (velocity == null)
            return 0;
        if (velocity.numSmoothedPoints() == 0)
            return 0;
        else
            return velocity.smoothedPointAt(velocity.numSmoothedPoints() - 1);
    }

    public DefaultSmoothedDataSeries getVelocityData() {
        return velocity;
    }

    public double getTimeScale() {
        return TIMER_SCALE;
    }

    public void setInitialPosition(double init) {
        reset(init);
    }

    public class MotionMode extends Mode {
        private StepMotion motion;
        private int numSmoothingPointsMotion = 3;
        private MotionAndControls mac;

        public MotionMode(MovingManModule module) {
            super(module, "Motion");
        }

        public void initialize() {
            cursorGraphic.setVisible(false);
            int timeIndex = position.numSmoothedPoints() - 1;
            setReplayTime(timeIndex);
            setAccelerationPlotMagnitude(4);
            setVelocityPlotMagnitude(4);
            positionPlot.getGrid().setPaintYLines(new double[]{-10, -5, 0, 5, 10});
            velocityPlot.getGrid().setPaintYLines(new double[]{-3, -1.5, 0, 1.5, 3});
            accelerationPlot.getGrid().setPaintYLines(new double[]{-3, -1.5, 0, 1.5, 3});
            setNumSmoothingPoints(numSmoothingPointsMotion);

            mac.initialize(getMan());
        }

        public void setLatestTime() {
            int timeIndex = position.numSmoothedPoints() - 1;
            setReplayTime(timeIndex);
        }

        public void setMotion(MotionAndControls mac) {
//                StepMotion motion) {
            this.motion = mac.getStepMotion();
            this.mac = mac;
        }

        public void stepInTime(double dt) {
            if (recordingTimer.getTime() >= maxTime) {
                timeFinished();
//                setPauseMode();
                return;
            }
            double x = motion.stepInTime(getMan(), dt);
            x = Math.min(x, maxManPosition);
            x = Math.max(x, -maxManPosition);
            if (x == maxManPosition) {
                motionState.setVelocity(0);
            }
            if (x == -maxManPosition) {
                motionState.setVelocity(0);
            }

            recordingTimer.stepInTime(dt);
            man.setX(x);
            position.addPoint(man.getX());
            position.updateSmoothedSeries();
            position.updateDerivative(dt * TIMER_SCALE);
            velocity.updateSmoothedSeries();
            velocity.updateDerivative(dt * TIMER_SCALE);
            acceleration.updateSmoothedSeries();
            if (recordingTimer.getTime() >= maxTime) {
                timeFinished();
//                setPauseMode();
                return;
            }
        }

        private void timeFinished() {
//            setPauseMode();
            movingManControlPanel.getAnotherPauseButton().doClick(100);
        }

        public void collidedWithWall() {
            mac.collidedWithWall();
        }
    }

    class RecordMode extends Mode {
        private int numRecordSmoothingPoints = 12;

        public RecordMode(MovingManModule module) {
            super(module, "Record");
        }

        public void initialize() {
            cursorGraphic.setVisible(false);
            int timeIndex = position.numSmoothedPoints() - 1;//smoothedPosition.size() - 1;
            setReplayTime(timeIndex);
            setAccelerationPlotMagnitude(75);
            setVelocityPlotMagnitude(25);
            positionPlot.getGrid().setPaintYLines(new double[]{-10, -5, 0, 5, 10});
            velocityPlot.getGrid().setPaintYLines(new double[]{-20, -10, 0, 10, 20});
            accelerationPlot.getGrid().setPaintYLines(new double[]{-50, -25, 0, 25, 50});
            setNumSmoothingPoints(numRecordSmoothingPoints);
            backgroundGraphic.paintBufferedImage();
        }

        public void stepInTime(double dt) {
            if (recordingTimer.getTime() >= maxTime) {
                setPauseMode();
                return;
            }
            recordingTimer.stepInTime(dt);
            position.addPoint(man.getX());
            position.updateSmoothedSeries();
            position.updateDerivative(dt * TIMER_SCALE);
            velocity.updateSmoothedSeries();
            velocity.updateDerivative(dt * TIMER_SCALE);
            acceleration.updateSmoothedSeries();
            if (recordingTimer.getTime() >= maxTime) {
                setPauseMode();
                return;
            }
        }
    }

    class PauseMode extends Mode {
        public PauseMode(MovingManModule module) {
            super(module, "Pause");
        }

        public void initialize() {
            cursorGraphic.setVisible(true);
        }

        public void stepInTime(double dt) {
        }
    }

    public class PlaybackMode extends Mode {
        public PlaybackMode(MovingManModule module) {
            super(module, "Playback");
        }

        public void initialize() {
            cursorGraphic.setVisible(true);
        }

        public void stepInTime(double dt) {
            playbackTimer.stepInTime(dt * playbackSpeed);
            if (playbackTimer.getTime() < recordingTimer.getTime()) {
                setReplayTime(playbackTimer.getTime());
            } else {
                setPauseMode();
                movingManControlPanel.setPaused();
            }
        }
    }
}


