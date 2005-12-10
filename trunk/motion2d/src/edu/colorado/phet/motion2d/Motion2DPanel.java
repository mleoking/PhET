package edu.colorado.phet.motion2d;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Motion2DPanel extends JPanel
        implements MouseMotionListener, ActionListener, MouseListener {
    private Motion2DApplet myGui;
    private JPanel northPanel, southPanel;
    private MotionPanel motionPanel1;
    private int xNow;
    private int yNow;

    private int avgXMid, avgYMid;
    private int xVel, yVel;
    private int xAcc, yAcc;
    private boolean mouseVisible;
    private Color myGreen;
    private int nAInit;        //position-averaging radius
    private int nGroupInit;    //# of avg-positions averaged in computing v, a
    private int timeStep;    //time step in millisec
    private double velFactor;   //velocity vector multiplication factor
    private double accFactor;    //acceleration vector multiplication factor
    private int radius = 9; //Radius of ball
    JLabel btnLabel;
    JRadioButton vButton, aButton, bothButton, neitherButton;
    JButton moreButton;
//    JButton hideMouseButton;
    private int buttonFlag;
    public static final int SHOW_VEL = 1;
    public static final int SHOW_ACC = 2;
    public static final int SHOW_BOTH = 3;
    public static final int SHOW_NEITHER = 4;
    private Motion2DAverages vaa;
    private VAScrolls vaMenu;
    private ArrowA arrow;
    private boolean antialias = true;
    private Timer timer;
    private ButtonGroup buttonGroup;
    private WiggleMe wiggleMe;
    private Timer wiggleMeTimer;

    public int getxNow() {
        return xNow;
    }

    public int getyNow() {
        return yNow;
    }

    public Motion2DPanel( Motion2DApplet myGui ) {
        this.myGui = myGui;
        myGreen = new Color( 0, 150, 0 );
        northPanel = new JPanel();
        northPanel.setBackground( Color.orange );
        southPanel = new JPanel();
        southPanel.setBackground( Color.orange );
        mouseVisible = false;
        nAInit = 10;
        nGroupInit = 5;
        timeStep = 10;
        velFactor = 5.0;
        accFactor = 6.0;
        xNow = 130;
        yNow = 100;

        vaa = new Motion2DAverages( nAInit, nGroupInit );//, this);
        vaMenu = new VAScrolls( vaa, this );
        vaMenu.setVisible( false );

        motionPanel1 = new MotionPanel( this, vaa, myGui.getWidth(), myGui.getHeight() );
        motionPanel1.launchMotionPanel();

        arrow = new ArrowA();
        vaa.addPoint( xNow, yNow );
        vaa.updateAvgXYs();

        buttonFlag = SHOW_NEITHER;
        setBackground( Color.yellow );

        btnLabel = new JLabel( SimStrings.get( "Motion2DPanel.VelocityAccelerationLabel" ) );
        btnLabel.setBackground( Color.yellow );
        vButton = new JRadioButton( SimStrings.get( "Motion2DPanel.ShowVelocityRadioButton" ), false );
        aButton = new JRadioButton( SimStrings.get( "Motion2DPanel.ShowAccelerationRadioButton" ), false );
        bothButton = new JRadioButton( SimStrings.get( "Motion2DPanel.ShowBothRadioButton" ), true );
        neitherButton = new JRadioButton( SimStrings.get( "Motion2DPanel.ShowNeitherRadioButton" ), false );
        buttonGroup = new ButtonGroup();
        setup( vButton );
        setup( aButton );
        setup( bothButton );
        setup( neitherButton );
//        hideMouseButton = new JButton( SimStrings.get( "Motion2DPanel.ShowMouseCursorButton" );
        moreButton = new JButton( SimStrings.get( "Motion2DPanel.MoreControlsButton" ) );

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
        //myPanelTimer.start();

        final SystemRunner sr = new SystemRunner();
//        myThread = new Thread( new SystemRunner() );
//        myThread.start();
        timer = new Timer( 25, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                sr.step();
            }
        } );

        timer.start();
        bothButton.doClick();

        Point pt = new Point( 20, yNow );
        wiggleMe = new WiggleMe( this, pt, new Vector2D.Double( 0, 1 ), 20, 5,
                                 SimStrings.get( "Motion2DPanel.WiggleMeText" ) );
//        pt = new Point( (int)( xNow - wiggleMe.getWidth() ), yNow );

        wiggleMe.setCenter( pt );
        wiggleMeTimer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                wiggleMe.stepInTime( .01 );
            }
        } );
        wiggleMeTimer.start();
    }

    private void setup( JRadioButton button ) {
        buttonGroup.add( button );
        button.setBackground( Color.orange );
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        if( antialias ) {
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        super.paintComponent( g );
        avgXMid = (int)vaa.getAvgXMid();
        avgYMid = (int)vaa.getAvgYMid();

        xVel = (int)( ( velFactor / 2 ) * ( vaa.getXVel() ) );
        yVel = (int)( ( velFactor / 2 ) * ( vaa.getYVel() ) );
        xAcc = (int)( accFactor * ( vaa.getXAcc() ) );
        yAcc = (int)( accFactor * ( vaa.getYAcc() ) );
        g.drawImage( myGui.ballImage, avgXMid - radius, avgYMid - radius, 2 * radius, 2 * radius, this );

        if( buttonFlag == SHOW_NEITHER ) {
        }
        else if( buttonFlag == SHOW_VEL ) {
            g.setColor( myGreen );
            arrow.setPosition( avgXMid, avgYMid, avgXMid + xVel, avgYMid + yVel );
            arrow.paint( g );
        }
        else if( buttonFlag == SHOW_ACC ) {
            g.setColor( Color.blue );
            arrow.setPosition( avgXMid, avgYMid, avgXMid + xAcc, avgYMid + yAcc );
            arrow.paint( g );
        }
        else if( buttonFlag == SHOW_BOTH ) {
            g.setColor( myGreen );
            arrow.setPosition( avgXMid, avgYMid, avgXMid + xVel, avgYMid + yVel );
            arrow.paint( g );
            g.setColor( Color.blue );
            arrow.setPosition( avgXMid, avgYMid, avgXMid + xAcc, avgYMid + yAcc );
            arrow.paint( g );
        }
        if( wiggleMe != null ) {
            wiggleMe.paint( g2 );
        }
    }//end of paintComponent method

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == vButton ) {
            buttonFlag = SHOW_VEL;
        }
        else if( e.getSource() == aButton ) {
            buttonFlag = SHOW_ACC;
        }
        else if( e.getSource() == bothButton ) {
            buttonFlag = SHOW_BOTH;
        }
//        else if( e.getSource() == hideMouseButton ) {
//            hideOrShowCursor();
//        }
        else if( e.getSource() == neitherButton ) {
            buttonFlag = SHOW_NEITHER;
        }
        else if( e.getSource() == moreButton ) {
            vaMenu.setVisible( true );
            moreButton.setEnabled( false );
            vaMenu.toFront();
        }

    }//end of actionPerformed method


    public void mouseDragged( MouseEvent e ) {
        hideCursor();
        removeWiggler();
        setXYNow( e.getX(), e.getY() );
    }//end of mouseDragged method

    private void removeWiggler() {
        if( wiggleMe != null ) {
            wiggleMeTimer.stop();
            wiggleMe.setVisible( false );
            repaint( wiggleMe.getBounds() );
            wiggleMe = null;
        }
    }

    private void hideCursor() {
        myGui.setCursor( myGui.hide );
    }

    private void showCursor() {
        myGui.setCursor( myGui.show );
    }

    public void hideOrShowCursor() {
        if( mouseVisible ) {
            myGui.setCursor( myGui.hide );
            mouseVisible = false;
        }
        else {
            myGui.setCursor( myGui.show );
            mouseVisible = true;
        }
    }

    public void mouseMoved( MouseEvent e ) {
        Point pt = e.getPoint();
        Point cur = new Point( xNow, yNow );
        double dist = pt.distance( cur );
        if( dist < myGui.ballImage.getWidth( this ) / 2.0 ) {
            myGui.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        else {
            myGui.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

    public void setXYNow( int xNow, int yNow ) {
        if( xNow != this.xNow || this.yNow != yNow ) {
            this.xNow = xNow;
            this.yNow = yNow;
//            repaint(getPlayRect());
        }
    }
//
//    private Rectangle getPlayRect() {
//        return new Rectangle( 0,northPanel.getY()+northPanel.getHeight()+2,getWidth(),getHeight()-northPanel.getHeight()-southPanel.getHeight()-2);
//    }

    public int getButtonFlag() {
        return this.buttonFlag;
    }

    public void setButtonFlag( int flag ) {
        this.buttonFlag = flag;
    }

    public int getTimeStep() {
        return this.timeStep;
    }

    public void setTimeStep( int timeStep ) {
        this.timeStep = timeStep;
        vaMenu.getTimeStepBar().setValue( timeStep );
        timer.setDelay( timeStep );
        //myPanelTimer.setDelay(timeStep);
        //motionPanel1.setTimerStep();
    }

    public double getVelFactor() {
        return this.velFactor;
    }

    public void setVelFactor( double velFactor ) {
        this.velFactor = velFactor;
        vaMenu.getVelFactorBar().setValue( (int)velFactor );
    }

    public double getAccFactor() {
        return this.accFactor;
    }

    public void setAccFactor( double accFactor ) {
        this.accFactor = accFactor;
        vaMenu.getAccFactorBar().setValue( (int)accFactor );
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

    class SystemRunner implements Runnable {
        public void step() {
            if( motionPanel1.getMotionOnState() ) {
                motionPanel1.nextPosition();
                setXYNow( motionPanel1.getXNow(), motionPanel1.getYNow() );
            }
            boolean changed1 = vaa.addPoint( xNow, yNow );
            boolean changed2 = vaa.updateAvgXYs();
            if( changed1 || changed2 ) {
                repaint();
            }
//            repaint();
        }

        public void run() {
            while( true ) {
                step();
                try {
                    Thread.sleep( timeStep );
                }
                catch( InterruptedException ie ) {
                }
            }
        }
    }

}//end of public class