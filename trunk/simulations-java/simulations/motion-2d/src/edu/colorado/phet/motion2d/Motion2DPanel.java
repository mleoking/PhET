package edu.colorado.phet.motion2d;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

public class Motion2DPanel extends JPanel
        implements MouseMotionListener, ActionListener, MouseListener {
    private Motion2DSimulationPanel simulationPanel;
    private MotionPanel motionPanel1;
    private int xNow;
    private int yNow;

    private int avgXMid, avgYMid;
    private int xVel, yVel;
    private Color green;
    private int nAInit;        //position-averaging radius
    private int nGroupInit;    //# of avg-positions averaged in computing v, a
    private int timeStep;    //time step in millisec
    private double velFactor;   //velocity vector multiplication factor
    private double accFactor;    //acceleration vector multiplication factor
    private int radius = 9; //Radius of ball
    JLabel btnLabel;
    JRadioButton vButton, aButton, bothButton, neitherButton;
    JButton moreButton;
    private int buttonFlag;
    public static final int SHOW_VEL = 1;
    public static final int SHOW_ACC = 2;
    public static final int SHOW_BOTH = 3;
    public static final int SHOW_NEITHER = 4;
    private Motion2DModel motion2DModel;
    private Motion2DControlFrame motion2DControlFrame;
    private Motion2DArrow motion2DArrow;
    private boolean antialias = true;
    private ButtonGroup buttonGroup;
    private WiggleMe wiggleMe;
    private Timer wiggleMeTimer;

    public Motion2DPanel( Motion2DSimulationPanel simulationPanel ) {
        this.simulationPanel = simulationPanel;
        green = new Color( 0, 150, 0 );
        JPanel northPanel = new JPanel();
        northPanel.setBackground( Color.orange );
        JPanel southPanel = new JPanel();
        southPanel.setBackground( Color.orange );
        nAInit = 10;
        nGroupInit = 5;
        timeStep = 10;
        velFactor = 5.0;
        accFactor = 6.0;
        xNow = 130;
        yNow = 100;

        motion2DModel = new Motion2DModel( nAInit, nGroupInit );//, this);
        motion2DControlFrame = new Motion2DControlFrame( this );
        motion2DControlFrame.setVisible( false );

        motionPanel1 = new MotionPanel( this, motion2DModel, simulationPanel.getWidth(), simulationPanel.getHeight() );
        motionPanel1.launchMotionPanel();

        motion2DArrow = new Motion2DArrow();
        motion2DModel.addPointAndUpdate( xNow, yNow );

        buttonFlag = SHOW_NEITHER;
        setBackground( Color.yellow );

        btnLabel = new JLabel( Motion2DResources.getString( "Motion2DPanel.VelocityAccelerationLabel" ) );
        btnLabel.setBackground( Color.yellow );
        vButton = new JRadioButton( Motion2DResources.getString( "Motion2DPanel.ShowVelocityRadioButton" ), false );
        aButton = new JRadioButton( Motion2DResources.getString( "Motion2DPanel.ShowAccelerationRadioButton" ), false );
        bothButton = new JRadioButton( Motion2DResources.getString( "Motion2DPanel.ShowBothRadioButton" ), true );
        neitherButton = new JRadioButton( Motion2DResources.getString( "Motion2DPanel.ShowNeitherRadioButton" ), false );
        buttonGroup = new ButtonGroup();
        setup( vButton );
        setup( aButton );
        setup( bothButton );
        setup( neitherButton );
        moreButton = new JButton( Motion2DResources.getString( "Motion2DPanel.MoreControlsButton" ) );

        northPanel.add( btnLabel );
        northPanel.add( vButton );
        northPanel.add( aButton );
        northPanel.add( bothButton );
        northPanel.add( neitherButton );
//        northPanel.add( hideMouseButton );
        southPanel.add( motionPanel1 );
        southPanel.add( moreButton );

        setLayout( new BorderLayout() );
        add( northPanel, BorderLayout.NORTH );
        add( southPanel, BorderLayout.SOUTH );

        vButton.addActionListener( this );
        aButton.addActionListener( this );
        bothButton.addActionListener( this );
//        hideMouseButton.addActionListener( this );
        moreButton.addActionListener( this );
        neitherButton.addActionListener( this );

        //myPanelTimer = new Timer(timeStep, new PanelTimerHandler());
        addMouseMotionListener( this );
        addMouseListener( this );

//        timer = new Timer( 30, new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                stepInTime();
//            }
//        } );
//
//        timer.start();
        simulationPanel.getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime();
            }
        } );
        bothButton.doClick();

        Point pt = new Point( 20, yNow );
        wiggleMe = new WiggleMe( this, pt, new Vector2D.Double( 0, 1 ), 20, 5,
                                 Motion2DResources.getString( "Motion2DPanel.WiggleMeText" ) );
//        pt = new Point( (int)( xNow - wiggleMe.getWidth() ), yNow );

        wiggleMe.setCenter( pt );
        wiggleMeTimer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                wiggleMe.stepInTime( .01 );
            }
        } );
        wiggleMeTimer.start();
    }

    private void stepInTime() {
        if ( motionPanel1.getMotionOnState() ) {
            motionPanel1.nextPosition();
            setXYNow( motionPanel1.getXNow(), motionPanel1.getYNow() );
        }
        motion2DModel.addPointAndUpdate( xNow, yNow );
        repaint();
    }

    private void setup( JRadioButton button ) {
        buttonGroup.add( button );
        button.setBackground( Color.orange );
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        if ( antialias ) {
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        super.paintComponent( g );
        avgXMid = (int) motion2DModel.getAvgXMid();
        avgYMid = (int) motion2DModel.getAvgYMid();

        xVel = (int) ( ( velFactor / 2 ) * ( motion2DModel.getXVel() ) );
        yVel = (int) ( ( velFactor / 2 ) * ( motion2DModel.getYVel() ) );
        double xAcc = accFactor * ( motion2DModel.getXAcc() );
        double yAcc = accFactor * ( motion2DModel.getYAcc() );
        g.drawImage( simulationPanel.getBallImage(), avgXMid - radius, avgYMid - radius, 2 * radius, 2 * radius, this );

        if ( buttonFlag == SHOW_NEITHER ) {
        }
        else if ( buttonFlag == SHOW_VEL ) {
            g.setColor( green );
            motion2DArrow.setPosition( avgXMid, avgYMid, avgXMid + xVel, avgYMid + yVel );
            motion2DArrow.paint( g );
        }
        else if ( buttonFlag == SHOW_ACC ) {
            g.setColor( Color.blue );
            motion2DArrow.setPosition( avgXMid, avgYMid, avgXMid + xAcc, avgYMid + yAcc );
            motion2DArrow.paint( g );
        }
        else if ( buttonFlag == SHOW_BOTH ) {
            g.setColor( green );
            motion2DArrow.setPosition( avgXMid, avgYMid, avgXMid + xVel, avgYMid + yVel );
            motion2DArrow.paint( g );
            g.setColor( Color.blue );
            motion2DArrow.setPosition( avgXMid, avgYMid, avgXMid + xAcc, avgYMid + yAcc );
            motion2DArrow.paint( g );
        }
        if ( wiggleMe != null ) {
            wiggleMe.paint( g2 );
        }
    }//end of paintComponent method

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == vButton ) {
            buttonFlag = SHOW_VEL;
        }
        else if ( e.getSource() == aButton ) {
            buttonFlag = SHOW_ACC;
        }
        else if ( e.getSource() == bothButton ) {
            buttonFlag = SHOW_BOTH;
        }
//        else if( e.getSource() == hideMouseButton ) {
//            hideOrShowCursor();
//        }
        else if ( e.getSource() == neitherButton ) {
            buttonFlag = SHOW_NEITHER;
        }
        else if ( e.getSource() == moreButton ) {
            motion2DControlFrame.setVisible( true );
            moreButton.setEnabled( false );
            motion2DControlFrame.toFront();
        }

    }//end of actionPerformed method


    public void mouseDragged( MouseEvent e ) {
        hideCursor();
        removeWiggler();
        setXYNow( e.getX(), e.getY() );
    }//end of mouseDragged method

    private void removeWiggler() {
        if ( wiggleMe != null ) {
            wiggleMeTimer.stop();
            wiggleMe.setVisible( false );
            repaint( wiggleMe.getBounds() );
            wiggleMe = null;
        }
    }

    private void hideCursor() {
        simulationPanel.setCursor( simulationPanel.hide );
    }

    private void showCursor() {
        simulationPanel.setCursor( simulationPanel.show );
    }

    public void mouseMoved( MouseEvent e ) {
        Point pt = e.getPoint();
        Point cur = new Point( xNow, yNow );
        double dist = pt.distance( cur );
        if ( dist < simulationPanel.getBallImage().getWidth( this ) / 2.0 ) {
            simulationPanel.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        else {
            simulationPanel.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

    public void setXYNow( int xNow, int yNow ) {
        if ( xNow != this.xNow || this.yNow != yNow ) {
            this.xNow = xNow;
            this.yNow = yNow;
        }
    }

    public int getTimeStep() {
        return this.timeStep;
    }

    public void setTimeStep( int timeStep ) {
        this.timeStep = timeStep;
        motion2DControlFrame.getTimeStepBar().setValue( timeStep );
        simulationPanel.getClock().setDelay( timeStep );
    }

    public double getVelFactor() {
        return this.velFactor;
    }

    public void setVelFactor( double velFactor ) {
        this.velFactor = velFactor;
        motion2DControlFrame.getVelFactorBar().setValue( (int) velFactor );
    }

    public double getAccFactor() {
        return this.accFactor;
    }

    public void setAccFactor( double accFactor ) {
        this.accFactor = accFactor;
        motion2DControlFrame.getAccFactorBar().setValue( (int) accFactor );
    }

    public JButton getMoreButton() {
        return moreButton;
    }

    public void mouseClicked( MouseEvent e ) {
        showCursor();
    }

    public void mouseEntered( MouseEvent e ) {
        showCursor();
    }

    public void mouseExited( MouseEvent e ) {
        showCursor();
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
        showCursor();
    }

}//end of public class