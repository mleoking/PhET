/**
 * Class: AlphaDecayControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 2, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.Format;

public class AlphaDecayControlPanel extends JPanel {
    private NuclearPhysicsModule module;
    private JTextField timerTF;
    private long startTime;
    private Timer timer = new Timer();

    public AlphaDecayControlPanel( final AlphaDecayModule module ) {
        this.module = module;

        timerTF = new JTextField();
        timerTF.setEditable( false );
        timerTF.setBackground( Color.white );
        timerTF.setHorizontalAlignment( JTextField.RIGHT );
        timerTF.setPreferredSize( new Dimension( 80, 30 ) );

        JButton replayBtn = new JButton( "Reset" );
        replayBtn.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.stop();
                module.start();
            }
        } );

        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new JLabel( "  " ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "<html>Running time<br> to decay (msec)" ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, timerTF,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( "  " ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, replayBtn,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Controls" );
        this.setBorder( titledBorder );
    }


    public void startTimer() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Thread timerThread = new Thread( AlphaDecayControlPanel.this.timer );
                timerThread.start();
            }
        } );
    }


    public void stopTimer() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                timer.setRunning( false );
            }
        } );
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
    }


    private class Timer implements Runnable {
        private boolean running = false;
        private Format formatter = new DecimalFormat( "##" );

        public void run() {
            double startTime = module.getModel().getClock().getRunningTime();

            running = true;
            while( running ) {
                try {
                    Thread.sleep( 50 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                final double runningTime = module.getModel().getClock().getRunningTime() - startTime;
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        timerTF.setText( formatter.format( new Double( runningTime ) ) );
                        AlphaDecayControlPanel.this.repaint();
                    }
                } );
            }
        }

        synchronized void setRunning( boolean running ) {
            this.running = running;
        }

        boolean isRunning() {
            return running;
        }
    }
}
