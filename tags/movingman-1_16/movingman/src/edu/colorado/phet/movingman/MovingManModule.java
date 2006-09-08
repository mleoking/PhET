/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ContentPanel;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.help.HelpPanel;
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
import edu.colorado.phet.movingman.view.GoPauseClearPanel;
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

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:19:49 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManModule extends Module {
    private PhetFrame frame;

    private MovingManModel movingManModel;
    private MovingManControlPanel movingManControlPanel;
    private MMHelpSuite helpSuite;
    private boolean initMediaPanel = false;

    private MovingManApparatusPanel movingManApparatusPanel;

    private boolean soundEnabled = true;


    public MovingManModule( AbstractClock clock ) throws IOException {
        super( SimStrings.get( "ModuleTitle.MovingManModule" ), clock );

        super.setModel( new BaseModel() );
        movingManModel = new MovingManModel( this, clock );
        movingManApparatusPanel = new MovingManApparatusPanel( this );
        helpSuite = new MMHelpSuite( this );
        helpSuite.init( this );
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

        movingManModel.fireReset();

        getVelocityPlot().addListener( new PlotDeviceListenerAdapter() {
            CircularBuffer circularBuffer = new CircularBuffer( 20 );

            public void nominalValueChanged( double value ) {
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

//    public int getNumSmoothingPoints() {
//        return movingManModel.getNumSmoothingPoints();
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
        helpSuite.setHelpEnabled( h );
    }


    public Color getBackgroundColor() {
        return getMovingManApparatusPanel().getBackgroundColor();
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
        movingManModel.setSmoothingSmooth();
    }

    public void setSmoothingSharp() {
        movingManModel.setSmoothingSharp();
    }

    public void setRightDirPositive( boolean rightPos ) {
        if( rightPos ) {//as usual
            getManPositionTransform().setInput( -getMaxManPosition(), getMaxManPosition() );
        }
        else {
            getManPositionTransform().setInput( getMaxManPosition(), -getMaxManPosition() );
        }

        setManTransform( getManPositionTransform() );
        getTimeModel().setRecordMode();
        reset();
        setPaused( true );
    }

    public boolean hasMegaHelp() {
        return true;
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
        final JPanel jp = (JPanel)parent.getContentPane();

        ContentPanel contentPanel = (ContentPanel)jp;
        final JPanel appPanel = new JPanel( new BorderLayout() );
        final JComponent playbackPanel = movingManControlPanel.getPlaybackPanel();
        appPanel.add( playbackPanel, BorderLayout.CENTER );
        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        final JLabel phetIconLabel = new JLabel( imageIcon );
//        appPanel.add( phetIconLabel, BorderLayout.WEST );
        HelpPanel hp = new HelpPanel( this );

        appPanel.add( hp, BorderLayout.EAST );
//        contentPanel.setsetAppControlPanel( appPanel );//todo fix
        contentPanel.setAppControlPanel( appPanel );
//
//        Component gp = parent.getGlassPane();
//        if( gp instanceof JComponent ) {
//            final JComponent glass = (JComponent)gp;
//            glass.setLayout( null );
//
//            glass.add( phetIconLabel );
//            glass.addComponentListener( new ComponentAdapter() {
//                public void componentResized( ComponentEvent e ) {
////                appPanel.setPreferredSize( new Dimension( jp.getWidth() - 2, appPanel.getPreferredSize().height ) );
//                    phetIconLabel.reshape( 2, glass.getHeight() - 2, phetIconLabel.getPreferredSize().width, phetIconLabel.getPreferredSize().height );
//                }
//            } );
//        }

        initMediaPanel = true;
    }

//    private void initMediaPanel() {
//        if( initMediaPanel ) {
//            return;
//        }
//        final JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( getApparatusPanel() );
//        JPanel jp = (JPanel)parent.getContentPane();
//        ContentPanel contentPanel = (ContentPanel)jp;
//        final JPanel appPanel = new JPanel( new GridBagLayout() );
//        GridBagConstraints centerConstraints=new GridBagConstraints( 1,0,1,1,10,10,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets( 2,2,2,2),1,1 );
//        final JComponent playbackPanel = movingManControlPanel.getPlaybackPanel();
//        appPanel.add( playbackPanel, centerConstraints);
//        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
//        final JLabel phetIconLabel = new JLabel( imageIcon );
//        GridBagConstraints logoConstraints=new GridBagConstraints( 0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 2,2,2,2),1,1 );
//        GridBagConstraints helpConstraints=new GridBagConstraints( 1,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets( 2,2,2,2),1,1 );
//
//        appPanel.add( phetIconLabel, logoConstraints);
//        HelpPanel hp = new HelpPanel( this );
//
//        appPanel.add( hp, helpConstraints);
////        contentPanel.setsetAppControlPanel( appPanel );//todo fix
//        contentPanel.setAppControlPanel( appPanel );
//        initMediaPanel = true;
//    }

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

    void setFrame( PhetFrame frame ) {
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

//    private int getTimeIndex( double requestedTime ) {
//        return getTimeModel().getTimeIndex( requestedTime );
//    }

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

    public Man.Direction getDirection() {
        return getManGraphic().getDirection();
    }

    public void setReplayManDirection( double direction ) {
        getManGraphic().setDirection( direction );
    }

    public boolean isSmoothingSmooth() {
        return movingManModel.isSmoothingSmooth();
    }

    public boolean isAtEndOfTime() {
        return movingManModel.getTimeModel().isAtEndOfTime();
    }

    public void requestEditInTextBox( GoPauseClearPanel goPauseClearPanel ) {
        movingManApparatusPanel.requestEditInTextBox( goPauseClearPanel );
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


    void setInited( boolean b ) {
        getMovingManApparatusPanel().setInited( b );
    }

    public MovingManApparatusPanel getMovingManApparatusPanel() {
        return movingManApparatusPanel;
    }

    static void addMiscMenu( final MovingManModule module ) {
        final JFrame frame = module.getFrame();
        JMenu miscMenu = new JMenu( SimStrings.get( "MovingManModule.SpecialFeaturesMenu" ) );
        miscMenu.setMnemonic( SimStrings.get( "MovingManModule.SpecialFeaturesMenuMnemonic" ).charAt( 0 ) );
        JMenuItem jep = new JMenuItem( SimStrings.get( "MovingManModule.ExprEvalMenuItem" ) );
        miscMenu.add( jep );
        final JEPFrame jef = new JEPFrame( frame, module );
        jep.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                jef.setVisible( true );
            }
        } );

        final JCheckBoxMenuItem jcbmi = new JCheckBoxMenuItem( SimStrings.get( "MovingManModule.InvertXAxisMenuItem" ), false );
        jcbmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean ok = module.confirmClear();
                if( ok ) {
                    module.setRightDirPositive( !jcbmi.isSelected() );
                }
//                module.setRightDirPositive( !jcbmi.isSelected() );
            }
        } );
        miscMenu.add( jcbmi );

//        miscMenu.addSeparator();
//        final JCheckBoxMenuItem showVelocityVector = new JCheckBoxMenuItem( "Show Velocity", false );
//        ActionListener velListener = new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.setShowVelocityVector( showVelocityVector.isSelected() );
//            }
//        };
//        showVelocityVector.addActionListener( velListener );
//        velListener.actionPerformed( null );
//        miscMenu.add( showVelocityVector );

//        final JCheckBoxMenuItem showAccVector = new JCheckBoxMenuItem( "Show Acceleration", false );
//        ActionListener accListetner = new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                module.setShowAccelerationVector( showAccVector.isSelected() );
//            }
//        };
//        showAccVector.addActionListener( accListetner );
//        accListetner.actionPerformed( null );
//        miscMenu.add( showAccVector );

        miscMenu.addSeparator();
        module.addBoundaryConditionButtons( miscMenu );

        miscMenu.addSeparator();
        module.addTimeScaleChooser( miscMenu );

        miscMenu.addSeparator();
        JMenuItem optionsItem = new JMenuItem( "Model Options" );
        optionsItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getMovingManModel().showControls();
            }
        } );
        miscMenu.add( optionsItem );

        frame.getJMenuBar().add( miscMenu );
    }

    private void addTimeScaleChooser( JMenu miscMenu ) {
        boolean useDynamicTime = false;
        final JCheckBoxMenuItem dynamicTimeScale = new JCheckBoxMenuItem( "RealTime(tm)", useDynamicTime );
        dynamicTimeScale.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setDynamicTime( dynamicTimeScale.isSelected() );
            }
        } );
        miscMenu.add( dynamicTimeScale );
        setDynamicTime( useDynamicTime );
    }

    private void setDynamicTime( boolean dynamicTime ) {
        movingManModel.getTimeModel().setDynamicTime( dynamicTime );
        if( dynamicTime ) {
            getClock().setTickConverter( new AbstractClock.RealTime() );
        }
        else {
            getClock().setStaticTickConverter();
        }
    }

    private void addBoundaryConditionButtons( JMenu miscMenu ) {
        JRadioButtonMenuItem closed = new JRadioButtonMenuItem( "Walls", true );
        JRadioButtonMenuItem open = new JRadioButtonMenuItem( "Free Range", false );
        open.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setBoundaryConditionsOpen();
            }
        } );
        closed.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setBoundaryConditionsClosed();
            }
        } );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( closed );
        buttonGroup.add( open );
        miscMenu.add( closed );
        miscMenu.add( open );
    }

    public void setBoundaryConditionsClosed() {
        movingManModel.setBoundaryConditionsClosed();
        movingManApparatusPanel.setBoundaryConditionsClosed();
    }

    public void setBoundaryConditionsOpen() {
        movingManModel.setBoundaryConditionsOpen();
        movingManApparatusPanel.setBoundaryConditionsOpen();
    }

    public void setShowAccelerationVector( boolean showAccelerationVector ) {
        movingManApparatusPanel.setShowAccelerationVector( showAccelerationVector );
    }

    public void setShowVelocityVector( boolean showVelocityVector ) {
        movingManApparatusPanel.setShowVelocityVector( showVelocityVector );
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

    public void initialize() {
        movingManApparatusPanel.initialize();
    }

    public boolean confirmClear() {
        double recTime = getMovingManModel().getTimeModel().getRecordMode().getTimer().getTime();
        if( recTime == 0.0 ) {
            return true;
        }
        setPaused( true );
        int option = JOptionPane.showConfirmDialog( getApparatusPanel(),
                                                    SimStrings.get( "MMPlot.ClearConfirmText" ),
                                                    SimStrings.get( "MMPlot.ClearConfirmButton" ),
                                                    JOptionPane.YES_NO_CANCEL_OPTION );
        if( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
            return true;
        }
        else {
            return false;
        }
    }

    public void confirmAndApplyReset() {
        MovingManModule module = this;
        boolean paused = module.isPaused();
        module.setPaused( true );

        boolean ok = confirmClear();
        if( ok ) {
            module.reset();
        }
        else {
            module.setPaused( paused );
        }
//
//        int option = JOptionPane.showConfirmDialog( module.getApparatusPanel(),
//                                                    SimStrings.get( "MMPlot.ClearConfirmText" ),
//                                                    SimStrings.get( "MMPlot.ClearConfirmButton" ),
//                                                    JOptionPane.YES_NO_CANCEL_OPTION );
//        if( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
//            module.reset();
//        }
//        else if( option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION ) {
//            module.setPaused( paused );
//        }
    }
}


