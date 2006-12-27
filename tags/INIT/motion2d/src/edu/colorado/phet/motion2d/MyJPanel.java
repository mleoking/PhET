package edu.colorado.phet.motion2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MyJPanel extends JPanel
        implements MouseMotionListener, ActionListener {
    private VelAccGui myGui;
    private JPanel northPanel, southPanel;
    private MotionPanel motionPanel1;
    private int xNow;
    int yNow;
    private int avgXMid, avgYMid;
    private int xVel, yVel;
    private int xAcc, yAcc;
    private boolean mouseVisible;
    private Color myGreen;
    private Thread myThread;
    private MouseEvent lastEvent;
    private int nPInit;		//# of positions in stack
    private int nAInit;		//position-averaging radius
    private int nGroupInit;	//# of avg-positions averaged in computing v, a
    private int timeStep;	//time step in millisec
    private int nbrOfUpdates;  //test
    private double velFactor;   //velocity vector multiplication factor
    private double accFactor;    //acceleration vector multiplication factor
    private int radius = 9; //Radius of ball
    JLabel btnLabel;
    JButton vButton, aButton, bothButton, neitherButton, moreButton, hideMouseButton;
    private int buttonFlag;
    public static final int SHOW_VEL = 1;
    public static final int SHOW_ACC = 2;
    public static final int SHOW_BOTH = 3;
    public static final int SHOW_NEITHER = 4;
    public static final int CURSOR_VISIBLE = 5;
    public static final int SHOW_SLIDERS = 6;
    private VelAccAvg vaa;
    private VAScrolls vaMenu;
    private ArrowA arrow;
//    private boolean antialias = false;
    private boolean antialias = true;
    private Timer timer;

    public MyJPanel( VelAccGui myGui ) {
        this.myGui = myGui;
        myGreen = new Color( 0, 150, 0 );
        northPanel = new JPanel();
        northPanel.setBackground( Color.orange );
        southPanel = new JPanel();
        southPanel.setBackground( Color.orange );
        mouseVisible = false;
        nAInit = 10;
        nGroupInit = 5;
        nPInit = 3 * nGroupInit + 2 * nAInit;
        timeStep = 10;
        velFactor = 5.0;
        accFactor = 6.0;
        xNow = 100;
        yNow = 100;

        vaa = new VelAccAvg( nAInit, nGroupInit );//, this);
        vaMenu = new VAScrolls( vaa, this );
        vaMenu.setVisible( false );

        motionPanel1 = new MotionPanel( this, vaa, myGui.getWidth(), myGui.getHeight() );
        motionPanel1.launchMotionPanel();

        arrow = new ArrowA();
        vaa.addPoint( xNow, yNow );
        vaa.updateAvgXYs();

        buttonFlag = SHOW_NEITHER;
        setBackground( Color.yellow );

        btnLabel = new JLabel( "Velocity or Acceleration?" );
        btnLabel.setBackground( Color.yellow );
        vButton = new JButton( "Show one" );
        aButton = new JButton( "Show the other" );
        bothButton = new JButton( "Show both" );
        neitherButton = new JButton( "Show neither" );
        hideMouseButton = new JButton( "Show mouse cursor" );
        moreButton = new JButton( "More controls" );

        northPanel.add( btnLabel );
        northPanel.add( vButton );
        northPanel.add( aButton );
        northPanel.add( bothButton );
        northPanel.add( neitherButton );
        northPanel.add( hideMouseButton );
        southPanel.add( motionPanel1 );
        southPanel.add( moreButton );

        setLayout( new BorderLayout() );
        add( northPanel, BorderLayout.NORTH );
        add( southPanel, BorderLayout.SOUTH );

        vButton.addActionListener( this );
        aButton.addActionListener( this );
        bothButton.addActionListener( this );
        hideMouseButton.addActionListener( this );
        moreButton.addActionListener( this );
        neitherButton.addActionListener( this );

        //myPanelTimer = new Timer(timeStep, new PanelTimerHandler());
        addMouseMotionListener( this );
        //myPanelTimer.start();

        final SystemRunner sr = new SystemRunner();
//        myThread = new Thread( new SystemRunner() );
//        myThread.start();
        timer = new Timer( 10, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                sr.step();
            }
        } );

        timer.start();
        bothButton.doClick();
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

        //else System.out.println("buttonFlag has invalid value.");

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
        else if( e.getSource() == hideMouseButton ) {
            //buttonFlag = CURSOR_VISIBLE;
            hideOrShowCursor();
        }
        else if( e.getSource() == neitherButton ) {
            buttonFlag = SHOW_NEITHER;
        }
        else if( e.getSource() == moreButton ) {
            //if(vaMenu == null){
            //	vaMenu = new edu.colorado.phet.motion2d.VAScrolls(vaa, this);
            //vaMenu.toFront();
            //}
            //else {
            vaMenu.setVisible( true );
            //vaMenu.toFront();
            //}
            moreButton.setEnabled( false );
            vaMenu.toFront();
        }

        //System.out.println(buttonFlag);
    }//end of actionPerformed method


    public void mouseDragged( MouseEvent e ) {
        this.lastEvent = e;
        setXYNow( e.getX(), e.getY() );
    }//end of mouseDragged method

    public void hideOrShowCursor() {
        if( mouseVisible ) {
            myGui.setCursor( myGui.hide );
            mouseVisible = false;
        }
        else {
            myGui.setCursor( myGui.show );
            mouseVisible = true;
        }
        if( hideMouseButton.getText().equals( "Show mouse cursor" ) ) {
            hideMouseButton.setText( "Hide mouse cursor" );
        }
        else {
            hideMouseButton.setText( "Show mouse cursor" );
        }
    }

    public void mouseMoved( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        //if (y < 35 || y > 454)
        if( y < 45 || y > 404 ) {
            myGui.setCursor( myGui.show );
        }
        else if( !mouseVisible ) {
            myGui.setCursor( myGui.hide );
        }
        //System.out.println("x = " + x + "  y = " + y);
    }

    public void setXNow( int xNow ) {
        this.xNow = xNow;
    }

    public void setYNow( int yNow ) {
        this.yNow = yNow;
    }

    public void setXYNow( int xNow, int yNow ) {
        this.xNow = xNow;
        this.yNow = yNow;
    }

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

    class SystemRunner implements Runnable {
        public void step() {
            if( motionPanel1.getMotionOnState() ) {
                motionPanel1.nextPosition();
                setXYNow( motionPanel1.getXNow(), motionPanel1.getYNow() );
            }
            vaa.addPoint( xNow, yNow );
            vaa.updateAvgXYs();
            repaint();
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