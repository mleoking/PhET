/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
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
import edu.colorado.phet.movingman.misc.JEPFrame;
import edu.colorado.phet.movingman.plots.MMPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:19:49 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManModule extends Module {

    private static boolean addJEP = true;

    private MovingManModel model;
    private MovingManTimeModel timeModel;

    private MovingManControlPanel movingManControlPanel;
    private PhetFrame frame;

    private LinearTransform1d manPositionTransform;
    private PlotSet plotSet;

    private boolean initMediaPanel = false;
    private MMKeySuite keySuite;

    private MovingManApparatusPanel mypanel;
    private HelpItem closeHelpItem;

    // Localization
    public static final String localizedStringsPath = "localization/MovingManStrings";

    public MovingManModule( AbstractClock clock ) throws IOException {
        super( SimStrings.get( "ModuleTitle.MovingManModule" ), clock );
        timeModel = new MovingManTimeModel( this );
        super.setModel( new BaseModel() );
        model = new MovingManModel( this, clock );
        manPositionTransform = new LinearTransform1d( -getMaxManPosition(), getMaxManPosition(), 50, 600 );

        mypanel = new MovingManApparatusPanel( this );
        super.setApparatusPanel( mypanel );

        plotSet = new PlotSet( this );
        mypanel.initLayout();

        keySuite = new MMKeySuite( this );
        mypanel.addKeyListener( keySuite );
        mypanel.addGraphicsSetup( new BasicGraphicsSetup() );
        mypanel.setBorder( BorderFactory.createLineBorder( Color.black, 1 ) );

        movingManControlPanel = new MovingManControlPanel( this );
        getModel().addModelElement( timeModel.getMainModelElement() );

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


        clock.addClockTickListener( getModel() );


        closeHelpItem = new HelpItem( getApparatusPanel(), SimStrings.get( "MovingManModule.CloseHelpText" ), 250, 450 );
        closeHelpItem.setForegroundColor( Color.red );
        closeHelpItem.setShadowColor( Color.black );
        addHelpItem( closeHelpItem );

        timeModel.fireReset();
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
        timeModel.setRecordMode();
    }

    public int getNumSmoothingPoints() {
        return timeModel.getNumSmoothingPoints();
    }
//    private void addDirectionHandler() {
//
//        getVelocityPlot().addListener( new MMPlot.TimeListener() {
//            public void nominalValueChanged( double value ) {
//                if( manGraphic.isDragging() || getPositionPlot().isDragging() || getVelocityPlot().isDragging() || stopped ) {
//                }
//                else {
//                    manGraphic.setVelocity( value );
//                }
//            }
//        } );
//        getManGraphic().addListener( new ManGraphic.TimeListener() {
//            double lastX;
//
//            public void manGraphicChanged() {
//                if( manGraphic.isDragging() || getPositionPlot().isDragging() ) {
//                    double x = getMan().getX();
//                    double dx = x - lastX;
//                    getManGraphic().setVelocity( dx );
//                    lastX = x;
//                }
//            }
//
//            public void mouseReleased() {
//                getManGraphic().setVelocity( 0 );
//                stopped = true;
//            }
//        } );
//
//        getVelocityPlot().getVerticalChartSlider().addListener( new VerticalChartSlider.TimeListener() {
//            public void valueChanged( double value ) {
//                if( getVelocityPlot().isDragging() ) {
//                    getManGraphic().setVelocity( value );
//                }
//            }
//        } );
//        getAccelerationPlot().getVerticalChartSlider().addListener( new VerticalChartSlider.TimeListener() {
//            public void valueChanged( double value ) {
//                if( getAccelerationPlot().isDragging() ) {
//                    stopped = false;
//                }
//            }
//        } );
//    }

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
            JButton closeButton = getPositionPlot().getCloseButton();
            closeHelpItem.setLocation( closeButton.getLocation().x - 100, closeButton.getLocation().y + closeButton.getHeight() );
        }
        else {
            removeHelpItem( closeHelpItem );
        }
    }


    public Color getBackgroundColor() {
        return getMovingManApparatusPanel().getBackgroundColor();
    }

    public void repaintBackground( Rectangle rect ) {
        getMovingManApparatusPanel().repaintBackground( rect );
    }


    public void recordingFinished() {
        setPaused( true );
        timeModel.fireFinishedRecording();
    }

    public KeyListener getKeySuite() {
        return keySuite;
    }

    public void firePlaybackFinished() {
        timeModel.firePlaybackFinished();
    }

    public static double getTimeScale() {
        return MovingManTimeModel.TIME_SCALE;
    }

    public MovingManTimeModel getTimeModel() {

        return timeModel;
    }

    public void setWiggleMeVisible( boolean b ) {
        getMovingManApparatusPanel().setWiggleMeVisible( b );
    }

    public boolean isPaused() {
        return timeModel.isPaused();
    }

    public BufferedGraphicForComponent getBuffer() {
        return getMovingManApparatusPanel().getBuffer();
    }

    public MovingManModel getMovingManModel() {
        return model;
    }

    public void addListener( TimeListener timeListener ) {
        timeModel.addListener( timeListener );
    }

    public void setPaused( boolean paused ) {
        timeModel.setPaused( paused );
        getPositionPlot().requestTypingFocus();
    }

    public void setNumSmoothingPoints( int n ) {
        timeModel.setNumSmoothingPoints( n );
        model.setNumSmoothingPoints( n );
        plotSet.setNumSmoothingPoints( n );

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
        timeModel.setRecordMode();
        reset();
        setPaused( true );
    }

    public void repaintBackground() {
        getMovingManApparatusPanel().repaintBackground();
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
        return getMovingManApparatusPanel().getManGraphic();
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
        timeModel.setMode( mode );
        repaintBackground();
    }

    public void relayout() {
        getMovingManApparatusPanel().relayout();
        Component c = getApparatusPanel();
        if( c.getHeight() > 0 && c.getWidth() > 0 ) {
            getMovingManApparatusPanel().setTheSize( c.getWidth(), c.getHeight() );
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
        return timeModel.getRecordTimer();
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
        timeModel.rewind();
        getMan().reset();
    }


    public void setRecordMode() {
        //enter the text box values.
        enterTextBoxValues();
        timeModel.setRecordMode();
    }

    private void enterTextBoxValues() {
        plotSet.enterTextBoxValues();
    }

    public void startPlaybackMode( double playbackSpeed ) {

        timeModel.startPlaybackMode( playbackSpeed );
    }

    public boolean isRecording() {
        return timeModel.isRecording();
    }

    public void cursorMovedToTime( double requestedTime ) {
        if( requestedTime < 0 || requestedTime > timeModel.getRecordTimer().getTime() ) {
            return;
        }
        else {
            timeModel.getPlaybackTimer().setTime( requestedTime );
            int timeIndex = getTimeIndex( requestedTime );
            if( timeIndex < model.getPosition().numSmoothedPoints() && timeIndex >= 0 ) {
                double x = model.getPosition().smoothedPointAt( timeIndex );
                getMan().setX( x );
            }
            plotSet.cursorMovedToTime( requestedTime, timeIndex );
        }
    }

    private int getTimeIndex( double requestedTime ) {
        return timeModel.getTimeIndex( requestedTime );
    }

    public void setManTransform( LinearTransform1d transform ) {
        this.manPositionTransform = transform;
        getMovingManApparatusPanel().setManTransform( transform );
    }

    public void reset() {
        timeModel.reset();
        model.reset();
        setCursorsVisible( false );
        plotSet.reset();
        getMovingManApparatusPanel().paintBufferedImage();
        getApparatusPanel().repaint();
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
        return timeModel.getPlaybackTimer();
    }

    public boolean isRecordMode() {
        return timeModel.isRecordMode();
    }

    public boolean isTakingData() {
        return timeModel.isTakingData();
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
        m.timeModel.getRecordMode().initialize();
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
        return getMovingManApparatusPanel().getWalkwayGraphic();
    }

}


