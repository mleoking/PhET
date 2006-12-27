package edu.colorado.phet.motion2d;

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
    private TextField field1, field2, field3, field4, field5, field6;
    private VelAccAvg vaa;
    private MyJPanel myJP;
    private Container scrollPane;

    public VAScrolls( VelAccAvg vaa, MyJPanel myJP ) {
        super( "Slider Controls" );
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


        String str2 = "nRadius: " + ( new Integer( vaa.getNA() ) ).toString();
        field2 = new TextField( str2, 3 );

        String str3 = "nGroup: " + ( new Integer( vaa.getNGroup() ) ).toString();
        field3 = new TextField( str3, 3 );

        String str4 = "speed: " + ( new Integer( myJP.getTimeStep() ) ).toString();		//time step(ms)
        field4 = new TextField( str4, 3 );

        String str5 = "Velocity scale: " + ( new Integer( (int)myJP.getVelFactor() ) ).toString() + "X";
        field5 = new TextField( str5, 3 );

        String str6 = "Accel scale: " + ( new Integer( (int)myJP.getAccFactor() ) ).toString() + "X";
        field6 = new TextField( str6, 3 );

        field4.setEditable( false );
        field5.setEditable( false );
        field6.setEditable( false );

        field4.setBackground( Color.white );
        field5.setBackground( Color.white );
        field6.setBackground( Color.white );


        //scrollPane.setLayout(new GridLayout(5,2,10,5));
        scrollPane.setLayout( new GridLayout( 3, 2, 10, 5 ) );
        //this.add(field1);
        //this.add(nPointsBar);
        //scrollPane.add(field2);
        //scrollPane.add(nRadiusBar);
        //scrollPane.add(field3);
        //scrollPane.add(nGroupBar);
        scrollPane.add( field4 );
        scrollPane.add( timeStepBar );
        scrollPane.add( field5 );
        scrollPane.add( velFactorBar );
        scrollPane.add( field6 );
        scrollPane.add( accFactorBar );


        //nPointsBar.addAdjustmentListener(this);
        nRadiusBar.addChangeListener( this );
        nGroupBar.addChangeListener( this );
        timeStepBar.addChangeListener( this );
        velFactorBar.addChangeListener( this );
        accFactorBar.addChangeListener( this );
        //add(scrollPane);
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
            field2.setText( "nRadius: " + i2.toString() );
            vaa.setNA( nRadius );
        }
        else if( e.getSource() == nGroupBar ) {
            //System.out.println("3");
            nGroup = nGroupBar.getValue();
            Integer i3 = new Integer( nGroup );
            field3.setText( "nGroup: " + i3.toString() );
            vaa.setNGroup( nGroup );
        }
        else if( e.getSource() == timeStepBar ) {
            //System.out.println("4");
            timeStep = timeStepBar.getValue();
            Integer i4 = new Integer( timeStep );
            field4.setText( "smoothness: " + i4.toString() );
            myJP.setTimeStep( timeStep );
        }
        else if( e.getSource() == velFactorBar ) {
            //System.out.println("5");
            velFactor = velFactorBar.getValue();
            Integer i5 = new Integer( velFactor );
            field5.setText( "Velocity scale: " + i5.toString() + "X" );
            myJP.setVelFactor( (double)velFactor );
        }
        else if( e.getSource() == accFactorBar ) {
            //System.out.println("4");
            accFactor = accFactorBar.getValue();
            Integer i6 = new Integer( accFactor );
            field6.setText( "Accel scale: " + i6.toString() + "X" );
            myJP.setAccFactor( (double)accFactor );
        }
    }//end of stateChanged

    class MyWindowAdapter extends WindowAdapter {
        public void windowClosing( WindowEvent we ) {
            myJP.getMoreButton().setEnabled( true );
            //System.out.println("more button enabled?" + myJP.getMoreButton().isEnabled());
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