package edu.colorado.phet.motion2d;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Motion2DControlFrame extends JFrame implements ChangeListener {
    private JSlider timeStepBar;
    private JSlider velFactorBar, accFactorBar;
    private TextField field5, field6;
    private Motion2DPanel myJP;

    public Motion2DControlFrame( Motion2DPanel myJP ) {
        super( Motion2DResources.getString( "VAScrolls.SliderControlTitle" ) );
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Container scrollPane = getContentPane();
        this.myJP = myJP;

        this.setSize( 400, 120 );
        timeStepBar = new JSlider( JSlider.HORIZONTAL, 3, 50, myJP.getTimeStep() );
        velFactorBar = new JSlider( JSlider.HORIZONTAL, 1, 10, (int) myJP.getVelFactor() );
        accFactorBar = new JSlider( JSlider.HORIZONTAL, 2, 36, (int) myJP.getAccFactor() );

        String str5 = Motion2DResources.getString( "VAScrolls.VelocityScaleLabel" ) + " "
                      + ( new Integer( (int) myJP.getVelFactor() ) ).toString()
                      + Motion2DResources.getString( "VAScrolls.ScaleSuffix" );
        field5 = new TextField( str5, 3 );

        String str6 = Motion2DResources.getString( "VAScrolls.AccelerationScaleLabel" ) + " "
                      + ( new Integer( (int) myJP.getAccFactor() ) ).toString()
                      + Motion2DResources.getString( "VAScrolls.ScaleSuffix" );
        field6 = new TextField( str6, 3 );

        field5.setEditable( false );
        field6.setEditable( false );

        field5.setBackground( Color.white );
        field6.setBackground( Color.white );

        scrollPane.setLayout( new GridLayout( 3, 2, 10, 5 ) );

        scrollPane.add( timeStepBar );
        scrollPane.add( new JLabel( Motion2DResources.getString( "controls.time-scale" ) ) );

        scrollPane.add( velFactorBar );
        scrollPane.add( field5 );

        scrollPane.add( accFactorBar );
        scrollPane.add( field6 );

        timeStepBar.addChangeListener( this );
        velFactorBar.addChangeListener( this );
        accFactorBar.addChangeListener( this );
        this.addWindowListener( new MyWindowAdapter() );
        this.addFocusListener( new FocusListener() {
            public void focusGained( FocusEvent e ) {
            }

            public void focusLost( FocusEvent e ) {
                requestFocus();
            }
        } );
        this.setVisible( true );
        this.requestFocus();
    }//end of edu.colorado.phet.motion2d.VAScrolls() constructor

    public void stateChanged( ChangeEvent e ) {

        if ( e.getSource() == timeStepBar ) {
            int timeStep = timeStepBar.getValue();
            myJP.setTimeStep( timeStep );
        }
        else if ( e.getSource() == velFactorBar ) {
            int velFactor = velFactorBar.getValue();
            field5.setText( Motion2DResources.getString( "VAScrolls.VelocityScaleLabel" ) + " "
                            + velFactor + Motion2DResources.getString( "VAScrolls.ScaleSuffix" ) );
            myJP.setVelFactor( (double) velFactor );
        }
        else if ( e.getSource() == accFactorBar ) {
            int accFactor = accFactorBar.getValue();
            field6.setText( Motion2DResources.getString( "VAScrolls.AccelerationScaleLabel" ) + " "
                            + accFactor + Motion2DResources.getString( "VAScrolls.ScaleSuffix" ) );
            myJP.setAccFactor( (double) accFactor );
        }
    }//end of stateChanged

    class MyWindowAdapter extends WindowAdapter {
        public void windowClosing( WindowEvent we ) {
            myJP.getMoreButton().setEnabled( true );
        }
    }

    public JSlider getTimeStepBar() {
        return timeStepBar;
    }

    public JSlider getVelFactorBar() {
        return velFactorBar;
    }

    public JSlider getAccFactorBar() {
        return accFactorBar;
    }


}//end of public class