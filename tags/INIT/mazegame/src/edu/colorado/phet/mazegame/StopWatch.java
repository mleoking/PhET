package edu.colorado.phet.mazegame;

//A simple clock application using javax.swing.Timer class


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StopWatch extends JPanel {
    private Timer myTimer1;
    public static final int ONE_SEC = 1000;   //time step in milliseconds
    public static final int TENTH_SEC = 100;

    private Font myClockFont;

    private JButton startBtn, stopBtn, resetBtn;
    private JLabel timeLbl;
    private JPanel topPanel, bottomPanel;

    private int clockTick;  	//number of clock ticks; tick can be 1.0 s or 0.1 s
    private double clockTime;  	//time in seconds
    private String clockTimeString;


    public StopWatch() {
        clockTick = 0;  		//initial clock setting in clock ticks
        clockTime = ((double) clockTick) / 10.0;

        clockTimeString = new Double(clockTime).toString();
        myClockFont = new Font("Serif", Font.PLAIN, 50);

        timeLbl = new JLabel();
        timeLbl.setFont(myClockFont);
        timeLbl.setText(clockTimeString);

        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");
        resetBtn = new JButton("Reset");


        myTimer1 = new Timer(TENTH_SEC, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clockTick++;
                clockTime = ((double) clockTick) / 10.0;
                clockTimeString = new Double(clockTime).toString();
                timeLbl.setText(clockTimeString);
                //System.out.println(clockTime);
            }
        });


        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                myTimer1.start();
            }
        });

        stopBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                myTimer1.stop();
            }
        });

        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clockTick = 0;
                clockTime = ((double) clockTick) / 10.0;
                clockTimeString = new Double(clockTime).toString();
                timeLbl.setText(clockTimeString);
            }
        });

    }//end of edu.colorado.phet.mazegame.StopWatch constructor

    public void launchStopWatch() {
        topPanel = new JPanel();
        topPanel.setBackground(Color.orange);
        bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.yellow);
        topPanel.add(timeLbl);
        bottomPanel.add(startBtn);
        bottomPanel.add(stopBtn);
        bottomPanel.add(resetBtn);

        this.setLayout(new BorderLayout());

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(300, 200);
        setBackground(Color.orange);

    }//end of launchClock

    public static void main(String[] args) {
        MyTestFrame myTestFrame1 = new MyTestFrame();
    }


}//end of public class

//Testing Code

class MyTestFrame extends JFrame {
    StopWatch StopWatch1;

    public MyTestFrame() {
        super("My Stop Watch");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container myPane = getContentPane();

        StopWatch1 = new StopWatch();
        StopWatch1.launchStopWatch();
        myPane.add(StopWatch1);
        pack();
        setVisible(true);
    }
}