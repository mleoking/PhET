/**
 * Class: AlphaDecayControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Mar 2, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.coreadditions.GridBagUtil;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.model.DecayListener;
import edu.colorado.phet.nuclearphysics.model.AlphaDecayProducts;
import edu.colorado.phet.nuclearphysics.model.PreDecayListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;

public class AlphaDecayControlPanel extends JPanel {
    private NuclearPhysicsModule module;
    private JTextField timerTF;
    private Timer timer = new Timer();
    private SloMoAgent sloMoAgent;
    private JCheckBox sloMoCB;


    class SloMoAgent implements PreDecayListener {
        IClock clock;

        public SloMoAgent( IClock clock ) {
            this.clock = clock;
        }

        public void alphaDecayOnNextTimeStep() {
            clock.pause();
        }
    }


    public AlphaDecayControlPanel( final AlphaDecayModule module ) {

        this.module = module;
        this.sloMoAgent = new SloMoAgent( module.getClock() );

        timerTF = new JTextField();
        timerTF.setEditable( false );
        timerTF.setBackground( Color.white );
        timerTF.setHorizontalAlignment( JTextField.RIGHT );
        timerTF.setPreferredSize( new Dimension( 80, 30 ) );

        // Allow user to pause clock when decay occurs
        sloMoCB = new JCheckBox( SimStrings.get("AlphaDecayControlPanel.PauseOnDecay" ));
        sloMoCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( sloMoCB.isSelected() ) {
                    ( (Uranium235)module.getNucleus() ).addPreDecayListener( sloMoAgent );
                }
                else {
                    ( (Uranium235)module.getNucleus() ).removePreDecayListener( sloMoAgent );
                }
            }
        } );

        JButton resetBtn = new JButton( SimStrings.get( "AlphaDecayControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                IClock clock = module.getClock();
                if( clock.isPaused() ) {
                    clock.start();
                }
                if( sloMoCB.isSelected() ) {
                    ( (Uranium235)module.getNucleus() ).addPreDecayListener( sloMoAgent );
                }
            }
        } );

        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GridBagUtil.addGridBagComponent( this, new JLabel( "  " ),
                                             0, rowIdx++,
                                             1, 1,
                                             GridBagConstraints.NONE,
                                             GridBagConstraints.CENTER );
            GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "AlphaDecayControlPanel.RunningTimeLabel" ) ),
                                             0, rowIdx++,
                                             1, 1,
                                             GridBagConstraints.NONE,
                                             GridBagConstraints.CENTER );
            GridBagUtil.addGridBagComponent( this, timerTF,
                                             0, rowIdx++,
                                             1, 1,
                                             GridBagConstraints.NONE,
                                             GridBagConstraints.CENTER );
            GridBagUtil.addGridBagComponent( this, sloMoCB,
                                             0, rowIdx++,
                                             1, 1,
                                             GridBagConstraints.NONE,
                                             GridBagConstraints.CENTER );
            GridBagUtil.addGridBagComponent( this, new JLabel( "  " ),
                                             0, rowIdx++,
                                             1, 1,
                                             GridBagConstraints.NONE,
                                             GridBagConstraints.CENTER );
            GridBagUtil.addGridBagComponent( this, resetBtn,
                                             0, rowIdx++,
                                             1, 1,
                                             GridBagConstraints.NONE,
                                             GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        Border titledBorder = BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "AlphaDecayControlPanel.ControlBorder" ) );
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
        private Format formatter = new DecimalFormat( "####" );

        public void run() {
            double startTime = module.getClock().getSimulationTime();
            running = true;
            while( running ) {
                try {
                    Thread.sleep( 100 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                final double runningTime = module.getClock().getSimulationTime() - startTime;
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        timerTF.setText( formatter.format( new Double( runningTime ) ) + SimStrings.get( "AlphaDecayControlPanel.TimeUnits" ) );
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
