package edu.colorado.phet.mazegame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

public class ScorePanel extends JPanel implements ItemListener {
    ParticleArena pArena;
    ControlBoxPanel cbPanel;

    boolean soundOn;
    boolean traceOn;
    boolean clockOn;

    JButton startBtn = new JButton( MazeGameResources.getString( "ScorePanel.RestartButton" ) );
    JButton pauseBtn = new JButton( MazeGameResources.getString( "ScorePanel.PauseButton" ) );
    JButton resetBtn = new JButton( MazeGameResources.getString( "ScorePanel.ResetButton" ) );
    //JButton soundBtn = new JButton("Sound ON/OFF");
    JCheckBox soundChkBox = new JCheckBox( MazeGameResources.getString( "ScorePanel.SoundCheckBox" ), true );
    JCheckBox traceChkBox = new JCheckBox( MazeGameResources.getString( "ScorePanel.TraceCheckBox" ), false );
    JButton scoreBtn = new JButton( MazeGameResources.getString( "ScorePanel.StartButton" ) );

    JRadioButton radio0 = new JRadioButton( MazeGameResources.getString( "ScorePanel.PracticeRadioButton" ), true );
    JRadioButton radio1 = new JRadioButton( MazeGameResources.getString( "ScorePanel.Level1RadioButton" ), false );
    JRadioButton radio2 = new JRadioButton( MazeGameResources.getString( "ScorePanel.Level2RadioButton" ), false );
    JRadioButton radio3 = new JRadioButton( MazeGameResources.getString( "ScorePanel.CertainDeathRadioButton" ), false );

    ButtonGroup radioGroup = new ButtonGroup();
    ClockPanel myClock1;

    JLabel timeLbl = new JLabel( MazeGameResources.getString( "ScorePanel.TimeLabel" ) + ":" );

    //JLabel timeInSecLbl = new JLabel("0.0");
    JLabel collisionsLbl = new JLabel( MazeGameResources.getString( "ScorePanel.CollisionsLabel" ) + ":" );
    JLabel nbrCollisionsLbl = new JLabel( "0" );
    JLabel scoreLbl = new JLabel( MazeGameResources.getString( "ScorePanel.ScoreLabel" ) + ":" );
    JLabel ptsScoreLbl = new JLabel( MazeGameResources.getString( "ScorePanel.NoGameLabel" ) );

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JPanel panel3 = new JPanel();
    JPanel panel3L = new JPanel();
    JPanel panel3R = new JPanel();
    JPanel panel4 = new JPanel();
    JPanel panel4L = new JPanel();  //Panel 4, Left
    JPanel panel4M = new JPanel();    //Panel 4, Mid
    JPanel panel4R = new JPanel();    //Panel 4, Right


    public ScorePanel( ParticleArena pArena, ControlBoxPanel cbPanel ) {

        setBackground( Color.green );
        this.cbPanel = cbPanel;
        this.pArena = pArena;
        this.myClock1 = new ClockPanel();
        clockOn = false;
        soundOn = true;

        //startBtn.setBackground(Color.green);
        //pauseBtn.setBackground(Color.green);
        //resetBtn.setBackground(Color.green);

        panel1.setBackground( Color.green );
        panel2.setBackground( Color.green );
        panel3L.setBackground( Color.green );
        panel3R.setBackground( Color.green );
        panel4L.setBackground( Color.green );
        panel4M.setBackground( Color.green );
        panel4R.setBackground( Color.green );

        radio0.setBackground( Color.green );
        radio1.setBackground( Color.green );
        radio2.setBackground( Color.green );
        radio3.setBackground( Color.green );

        soundChkBox.setBackground( Color.green );
        traceChkBox.setBackground( Color.green );

        //panel1.add(startStopLbl);
        panel1.add( startBtn );
        panel1.add( pauseBtn );
        panel1.add( resetBtn );

        panel2.add( radio0 );
        panel2.add( radio1 );
        panel2.add( radio2 );
        panel2.add( radio3 );

        panel3L.add( scoreBtn );
        panel3R.add( soundChkBox );
        panel3R.add( traceChkBox );
        //panel3R.add(soundBtn);

        panel3.setLayout( new GridLayout( 1, 2 ) );
        panel3.add( panel3L );
        panel3.add( panel3R );

        //timeInSecLbl.setFont(myBigFont);


        nbrCollisionsLbl.setBackground( Color.white );
        ptsScoreLbl.setBackground( Color.white );

        panel4L.add( timeLbl );
        panel4L.add( myClock1 );
        //panel4.add(timeInSecLbl);
        panel4M.add( collisionsLbl );
        panel4M.add( nbrCollisionsLbl );
        panel4R.add( scoreLbl );
        panel4R.add( ptsScoreLbl );

        panel4.setLayout( new GridLayout( 1, 3 ) );
        panel4.add( panel4L );
        panel4.add( panel4M );
        panel4.add( panel4R );


        GridLayout gLayout = new GridLayout( 4, 1, 3, 3 );
        this.setLayout( gLayout );
        this.add( panel1 );
        this.add( panel2 );
        this.add( panel3 );
        this.add( panel4 );

        startBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent aevt ) {
                ScorePanel.this.pArena.start();
                if ( clockOn ) {
                    myClock1.start();
                }
                //startStopLbl.setText("Drag tip of arrow");
                startBtn.setEnabled( false );
                pauseBtn.setEnabled( true );
                resetBtn.setEnabled( true );
            }
        } );//end of anonymous inner class

        pauseBtn.addActionListener( new PauseHandler() );

        resetBtn.addActionListener( new ResetHandler() );

        scoreBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent aevt ) {
                myClock1.start();
                clockOn = true;
            }

        } ); //end of anonymous class

        //soundBtn.addActionListener(new SoundBtnHandler());
        soundChkBox.addActionListener( new SoundChkBoxHandler() );
        traceChkBox.addActionListener( new TraceChkBoxHandler() );


        radio0.addItemListener( this );
        radio1.addItemListener( this );
        radio2.addItemListener( this );
        radio3.addItemListener( this );

        radioGroup.add( radio0 );
        radioGroup.add( radio1 );
        radioGroup.add( radio2 );
        radioGroup.add( radio3 );

    }//end of constructor

    class PauseHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            ScorePanel.this.pArena.stop();
            myClock1.stop();
            //startStopLbl.setText("Press start to begin.");
            startBtn.setText( MazeGameResources.getString( "ScorePanel.RestartButton" ) );
            startBtn.setEnabled( true );
            pauseBtn.setEnabled( false );
            resetBtn.setEnabled( true );
        }
    }


    class ResetHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            ScorePanel.this.pArena.start();
            ScorePanel.this.cbPanel.reset();
            myClock1.stop();
            myClock1.reset();
            clockOn = false;
            ptsScoreLbl.setText( "0" );
            //startStopLbl.setText("Drag tip of arrow");
            startBtn.setEnabled( false );
            pauseBtn.setEnabled( true );

        }
    }

    class SoundChkBoxHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            soundOn = soundChkBox.isSelected();
        }
    }

    class TraceChkBoxHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            if ( traceChkBox.isSelected() ) {
                //cbPanel.getTrace().moveTo((float)cbPanel.getX(), (float)cbPanel.getY());
                traceOn = true;
                cbPanel.setTraceState( traceOn );

            }
            else {
                traceOn = false;
                cbPanel.setTraceState( traceOn );
                cbPanel.setTraceStartedState( false );
                cbPanel.setTraceToZero();
                cbPanel.repaint();
            }
        }
    }

    public void itemStateChanged( ItemEvent ievt ) {
        if ( ievt.getSource() == radio0 ) {
            pArena.setBarrierState( 0 );
            //System.out.println("barrier state is " + pArena.getBarrierState());
        }
        else if ( ievt.getSource() == radio1 ) {
            pArena.setBarrierState( 1 );
            //System.out.println("barrier state is " + pArena.getBarrierState());
        }
        else if ( ievt.getSource() == radio2 ) {
            pArena.setBarrierState( 2 );
        }
        else if ( ievt.getSource() == radio3 ) {
            pArena.setBarrierState( 3 );
        }
    }

}//end of public class
