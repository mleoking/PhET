package edu.colorado.phet.motion2d;

//Helper class for Velocity Accelation
//Buttons for displaying canned motion

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MotionPanel extends JPanel implements ActionListener //, Runnable
{
    private Motion2DPanel myJP;
    private JRadioButton constAIRadBtn, constAIIRadBtn, sHMRadBtn, circularRadBtn, stopRadBtn;
    private double amplitude;        //amplitude of motion for either SHM or constant acceleration
    private double tNow;            //motion time
    private boolean goingRight;      //used in const acc motion
    private boolean motionOn;
    private int buttonFlag;
    private final int CONST_AI = 0;
    private final int CONST_AII = 1;
    private final int SHM = 2;
    private final int CIRCULAR = 3;
    private final int STOP = 4;
    private int xNow, yNow;
    private double xDoubleNow;  //used for constant acceleration motion
    private int width = -1;// = edu.colorado.phet.motion2d.Motion2DSimulationPanel.width;
    private int height = -1;// = edu.colorado.phet.motion2d.Motion2DSimulationPanel.height;

    public MotionPanel( final Motion2DPanel myJP, Motion2DModel vaa, int width, int height ) {
        this.width = width;
        this.height = height;
        this.myJP = myJP;
        motionOn = false;
        setBackground( Color.orange );

        constAIRadBtn = new JRadioButton( SimStrings.getInstance().getString( "MotionPanel.LinearAccIRadioButton" ), false );
        constAIIRadBtn = new JRadioButton( SimStrings.getInstance().getString( "MotionPanel.LinearAccIIRadioButton" ), false );
        sHMRadBtn = new JRadioButton( SimStrings.getInstance().getString( "MotionPanel.SimpleHarmonicRadioButton" ), false );
        circularRadBtn = new JRadioButton( SimStrings.getInstance().getString( "MotionPanel.CircularRadioButton" ), false );
        stopRadBtn = new JRadioButton( SimStrings.getInstance().getString( "MotionPanel.StopRadioButton" ), true );

        constAIRadBtn.setBackground( Color.orange );
        constAIIRadBtn.setBackground( Color.orange );
        sHMRadBtn.setBackground( Color.orange );
        circularRadBtn.setBackground( Color.orange );
        stopRadBtn.setBackground( Color.orange );
        amplitude = 175;
        myJP.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                MotionPanel.this.width = myJP.getWidth();
                MotionPanel.this.height = myJP.getHeight();
            }
        } );
    }

    public void launchMotionPanel() {
        constAIRadBtn.addActionListener( this );
        constAIIRadBtn.addActionListener( this );
        sHMRadBtn.addActionListener( this );
        circularRadBtn.addActionListener( this );
        stopRadBtn.addActionListener( this );

        ButtonGroup group = new ButtonGroup();
        group.add( constAIRadBtn );
        group.add( constAIIRadBtn );
        group.add( sHMRadBtn );
        group.add( circularRadBtn );
        group.add( stopRadBtn );

        add( constAIRadBtn );
        add( constAIIRadBtn );
        add( sHMRadBtn );
        add( circularRadBtn );
        add( stopRadBtn );
    }

    public void actionPerformed( ActionEvent ae ) {

        if( constAIRadBtn.isSelected() ) {
            //System.out.println("ConstA button pushed.");
            buttonFlag = CONST_AI;
            motionOn = true;
            goingRight = true;
            yNow = height / 2;
            xDoubleNow = 0.0;
            tNow = 0.0;
            myJP.setTimeStep( 35 );
            myJP.setVelFactor( 2.0 );
            myJP.setAccFactor( 6.0 );
        }
        if( constAIIRadBtn.isSelected() ) {
            buttonFlag = CONST_AII;
            motionOn = true;
            yNow = height / 2;
            xDoubleNow = 30.0;
            xNow = (int)xDoubleNow;
            tNow = 0.0;
            myJP.setTimeStep( 35 );
            myJP.setVelFactor( 4.0 );
            myJP.setAccFactor( 13.0 );
        }
        if( sHMRadBtn.isSelected() ) {
            buttonFlag = SHM;
            motionOn = true;
            tNow = 0.0;
            myJP.setTimeStep( 35 );
            myJP.setVelFactor( 10.0 );
            myJP.setAccFactor( 17.0 );
        }
        if( circularRadBtn.isSelected() ) {
            myJP.setTimeStep( 35 );
            myJP.setVelFactor( 4.0 );
            myJP.setAccFactor( 15.0 );

            buttonFlag = CIRCULAR;
            motionOn = true;
            tNow = 0.0;
            myJP.setTimeStep( 35 );
            myJP.setVelFactor( 4.0 );
            myJP.setAccFactor( 15.0 );
        }
        if( ae.getSource() == stopRadBtn ) {
            buttonFlag = STOP;
            motionOn = false;
            myJP.setTimeStep( 10 );
            myJP.setVelFactor( 4.0 );
            myJP.setAccFactor( 6.0 );
            stopRadBtn.setSelected( true );
        }
    }//end of actionPerformed method

    public void nextPosition() {
        double period = 200.0;
        double v01 = 0.5;  //initial velocity
        double acc1 = 0.005;
        double v02 = 20;
        double acc2 = ( v02 * v02 ) / ( 2.0 * ( 9 * width / 10 ) );

        switch( buttonFlag ) {
            case CONST_AI:
                //start with high velocity right accelerating to the left

                if( goingRight && xNow < 9 * width / 10 ) {
                    xDoubleNow += acc1 * tNow * tNow;
                    tNow++;
                    //System.out.println("Rightward");
                }

                //stop at right end
                else if( xNow > 9 * width / 10 && goingRight ) {
                    tNow = tNow - 2;
                    if( tNow < 0 ) {
                        goingRight = false;
                    }
                    //System.out.println("stopped right");
                }
                //accelerate to the left
                else if( !goingRight && xNow > width / 10 ) {
                    xDoubleNow -= acc1 * tNow * tNow;
                    tNow++;
                }
                //stop at the left end
                else if( xNow < width / 10 && !goingRight ) {
                    tNow = tNow - 2;
                    if( tNow < 0 ) {
                        goingRight = true;
                    }
                    //System.out.println("Leftward");
                }
                xNow = (int)xDoubleNow;
                break;
            case CONST_AII:
                //prt("Starting now: xNow = " + xNow);
                if( motionOn && xNow > 29 ) {
                    xDoubleNow = 31.0 + v02 * tNow - ( 0.5 ) * acc2 * tNow * tNow;
                    xNow = (int)xDoubleNow;
                    tNow += 1.0;
                    //prt(xNow);
                }
                if( motionOn && xNow < 29 ) {
                    motionOn = false;
                    //prt("Ending now: xNow = " + xNow);
                }
                break;
            case SHM:
                yNow = height / 2;
                tNow += 1.0;
                xNow = width / 2 + (int)( amplitude * Math.sin( 2 * Math.PI * tNow / period ) );
                break;
            case CIRCULAR:
                tNow += 1.0;
                xNow = width / 2 + (int)( amplitude * Math.cos( 2 * Math.PI * tNow / period ) );
                yNow = height / 2 + (int)( amplitude * Math.sin( 2 * Math.PI * tNow / period ) );
                break;
        }
    }

    public boolean getMotionOnState() {
        return this.motionOn;
    }

    public int getXNow() {
        return xNow;
    }

    public int getYNow() {
        return yNow;
    }

}//end of public class