package edu.colorado.phet.mazegame;

//A simple clock application using javax.swing.Timer class

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockPanel extends JPanel {
    private Timer myTimer1;
    public static final int ONE_SEC = 1000;   //time step in milliseconds
    public static final int TENTH_SEC = 100;

    private Font myClockFont;

    //private JButton startBtn, stopBtn, resetBtn;
    private JLabel timeLbl;
    //private JPanel topPanel, bottomPanel;

    private int clockTick;  	//number of clock ticks; tick can be 1.0 s or 0.1 s
    private double clockTime;  	//time in seconds
    private String clockTimeString;

    private Border raisedBevel, loweredBevel, compound1, compound2;

    public ClockPanel() {
        setBackground(Color.white);

        raisedBevel = BorderFactory.createRaisedBevelBorder();
        loweredBevel = BorderFactory.createLoweredBevelBorder();
        compound1 = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);
        setBorder(compound1);

        clockTick = 0;  		//initial clock setting in clock ticks
        clockTime = ((double) clockTick) / 10.0;

        clockTimeString = new Double(clockTime).toString();
        myClockFont = new Font("Serif", Font.PLAIN, 25);

        timeLbl = new JLabel();
        timeLbl.setBackground(Color.white);
        timeLbl.setFont(myClockFont);
        timeLbl.setText(clockTimeString);

        add(timeLbl);

        //startBtn = new JButton("Start");
        //stopBtn = new JButton("Stop");
        //resetBtn = new JButton("Reset");


        myTimer1 = new Timer(TENTH_SEC, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clockTick++;
                clockTime = ((double) clockTick) / 10.0;
                clockTimeString = new Double(clockTime).toString();
                timeLbl.setText(clockTimeString);
                //System.out.println(clockTime);
            }
        });

    }//end of edu.colorado.phet.mazegame.ClockPanel constructor


    public void start() {
        myTimer1.start();
    }

    public void stop() {
        myTimer1.stop();
    }

    public void reset() {
        clockTick = 0;
        clockTime = ((double) clockTick) / 10.0;
        clockTimeString = new Double(clockTime).toString();
        timeLbl.setText(clockTimeString);
    }

    public double getTime() {
        return clockTime;
    }

}//end of public class

