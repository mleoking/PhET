package edu.colorado.phet.motion2d;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VAScrolls extends JFrame implements ChangeListener {
    private JSlider nPointsBar, nRadiusBar, nGroupBar, timeStepBar;
    private JSlider velFactorBar, accFactorBar;
    private int nPoints, nRadius, nGroup, timeStep, velFactor, accFactor;
    private Label label1, label2, label3, label4;
    private TextField field5, field6;
    private Motion2DAverages vaa;
    private Motion2DPanel myJP;
    private Container scrollPane;

    public VAScrolls( Motion2DAverages vaa, Motion2DPanel myJP ) {
        super( SimStrings.get( "VAScrolls.SliderControlTitle" ) );
        //setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        scrollPane = getContentPane();
        this.vaa = vaa;
        this.myJP = myJP;

        this.setSize( 400, 120 );
        //nPointsBar = new JSlider(JSlider.HORIZONTAL, vaa.getNP(), 1, 81);
        nRadiusBar = new JSlider( JSlider.HORIZONTAL, 1, 21, vaa.getNA() );
        nGroupBar = new JSlider( JSlider.HORIZONTAL, 1, 36, vaa.getNGroup() );
        timeStepBar = new JSlider( JSlider.HORIZONTAL, 3, 50, myJP.getTimeStep() );
        velFactorBar = new JSlider( JSlider.HORIZONTAL, 1, 10, (int)myJP.getVelFactor() );
        accFactorBar = new JSlider( JSlider.HORIZONTAL, 2, 36, (int)myJP.getAccFactor() );

        String str5 = SimStrings.get( "VAScrolls.VelocityScaleLabel" ) + " "
                      + ( new Integer( (int)myJP.getVelFactor() ) ).toString()
                      + SimStrings.get( "VAScrolls.ScaleSuffix" );
        field5 = new TextField( str5, 3 );

        String str6 = SimStrings.get( "VAScrolls.AccelerationScaleLabel" ) + " "
                      + ( new Integer( (int)myJP.getAccFactor() ) ).toString()
                      + SimStrings.get( "VAScrolls.ScaleSuffix" );
        field6 = new TextField( str6, 3 );

        field5.setEditable( false );
        field6.setEditable( false );

        field5.setBackground( Color.white );
        field6.setBackground( Color.white );

        scrollPane.setLayout( new GridLayout( 3, 2, 10, 5 ) );

        scrollPane.add( timeStepBar );
        scrollPane.add( new JLabel( "Time Scale" ) );

        scrollPane.add( velFactorBar );
        scrollPane.add( field5 );

        scrollPane.add( accFactorBar );
        scrollPane.add( field6 );

        nRadiusBar.addChangeListener( this );
        nGroupBar.addChangeListener( this );
        timeStepBar.addChangeListener( this );
        velFactorBar.addChangeListener( this );
        accFactorBar.addChangeListener( this );
        this.addWindowListener( new MyWindowAdapter() );
        MyFocusListener myFocusListener = new MyFocusListener( this );
        this.addFocusListener( myFocusListener );
        this.setVisible( true );
        this.requestFocus();
    }//end of edu.colorado.phet.motion2d.VAScrolls() constructor

    public void stateChanged( ChangeEvent e ) {

        if( e.getSource() == nRadiusBar ) {
            //System.out.println("2");
            nRadius = nRadiusBar.getValue();
            Integer i2 = new Integer( nRadius );
            vaa.setNA( nRadius );
        }
        else if( e.getSource() == nGroupBar ) {
            //System.out.println("3");
            nGroup = nGroupBar.getValue();
            Integer i3 = new Integer( nGroup );
            vaa.setNGroup( nGroup );
        }
        else if( e.getSource() == timeStepBar ) {
            //System.out.println("4");
            timeStep = timeStepBar.getValue();
            Integer i4 = new Integer( timeStep );
            myJP.setTimeStep( timeStep );
        }
        else if( e.getSource() == velFactorBar ) {
            //System.out.println("5");
            velFactor = velFactorBar.getValue();
            Integer i5 = new Integer( velFactor );
            field5.setText( SimStrings.get( "VAScrolls.VelocityScaleLabel" ) + " "
                            + i5.toString() + SimStrings.get( "VAScrolls.ScaleSuffix" ) );
            myJP.setVelFactor( (double)velFactor );
        }
        else if( e.getSource() == accFactorBar ) {
            //System.out.println("4");
            accFactor = accFactorBar.getValue();
            Integer i6 = new Integer( accFactor );
            field6.setText( SimStrings.get( "VAScrolls.AccelerationScaleLabel" ) + " "
                            + i6.toString() + SimStrings.get( "VAScrolls.ScaleSuffix" ) );
            myJP.setAccFactor( (double)accFactor );
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