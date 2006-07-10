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
import edu.colorado.phet.nuclearphysics.model.*;

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
//    private JCheckBox pauseOnDelayCB;
    private AlphaDecaySnapshot snapshot;


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
        timerTF.setPreferredSize( new Dimension( 60, 30 ) );

        // Allow user to pause clock when decay occurs
//        pauseOnDelayCB = new JCheckBox( SimStrings.get("AlphaDecayControlPanel.PauseOnDecay" ));
//        pauseOnDelayCB.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                if( pauseOnDelayCB.isSelected() ) {
//                    ( (Polonium210)module.getNucleus() ).addPreDecayListener( sloMoAgent );
//                }
//                else {
//                    ( (Polonium210)module.getNucleus() ).removePreDecayListener( sloMoAgent );
//                }
//            }
//        } );

        // Allow user to rewind to the last alpha decay event
//        final JButton rewindToDecayBtn = new JButton( "<html>Rewind to<br>last decay</html>");
//        rewindToDecayBtn.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                if( snapshot != null ) {
//                    module.getClock().pause();
//                    snapshot.restore();
//                    rewindToDecayBtn.setEnabled( false );
//                }
//            }
//        } );
//        rewindToDecayBtn.setEnabled( false );

        // Add a listener to the module that will tell us when an alpha decay event is about to occur, and saves
        // a snapshot of the model
//        module.addChangeListener( new AlphaDecayModule.ChangeListener() {
//            public void decayOccurred( AlphaDecayModule.ChangeEvent event, AlphaDecaySnapshot alphaDecaySnapshot ) {
//                snapshot = alphaDecaySnapshot;
//                rewindToDecayBtn.setEnabled( true );
//            }
//        } );


        JButton resetBtn = new JButton( SimStrings.get( "AlphaDecayControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.stop();
                module.start();
                IClock clock = module.getClock();
                if( clock.isPaused() ) {
                    clock.start();
                }
//                if( pauseOnDelayCB.isSelected() ) {
//                    ( (Polonium210)module.getNucleus() ).addPreDecayListener( sloMoAgent );
//                }
            }
        } );

        setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.EAST,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 5, 5, 5 ),0,0 );
        add( new JLabel( SimStrings.get( "AlphaDecayControlPanel.RunningTimeLabel" ) ), gbc );
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        add( timerTF, gbc );

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 2;
//        gbc.gridy = 2;
//        add( rewindToDecayBtn, gbc );
        gbc.gridy = 2;
        add( resetBtn, gbc );


//        int rowIdx = 0;
//        try {
//            GridBagUtil.addGridBagComponent( this, new JLabel( "  " ),
//                                             0, rowIdx++,
//                                             1, 1,
//                                             GridBagConstraints.NONE,
//                                             GridBagConstraints.CENTER );
//            GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "AlphaDecayControlPanel.RunningTimeLabel" ) ),
//                                             0, rowIdx++,
//                                             1, 1,
//                                             GridBagConstraints.NONE,
//                                             GridBagConstraints.CENTER );
//            GridBagUtil.addGridBagComponent( this, timerTF,
//                                             0, rowIdx++,
//                                             1, 1,
//                                             GridBagConstraints.NONE,
//                                             GridBagConstraints.CENTER );
////            GridBagUtil.addGridBagComponent( this, pauseOnDelayCB,
////                                             0, rowIdx++,
////                                             1, 1,
////                                             GridBagConstraints.NONE,
////                                             GridBagConstraints.CENTER );
//            GridBagUtil.addGridBagComponent( this, rewindToDecayBtn,
//                                             0, rowIdx++,
//                                             1, 1,
//                                             GridBagConstraints.NONE,
//                                             GridBagConstraints.CENTER );
//            GridBagUtil.addGridBagComponent( this, new JLabel( "  " ),
//                                             0, rowIdx++,
//                                             1, 1,
//                                             GridBagConstraints.NONE,
//                                             GridBagConstraints.CENTER );
//            GridBagUtil.addGridBagComponent( this, resetBtn,
//                                             0, rowIdx++,
//                                             1, 1,
//                                             GridBagConstraints.NONE,
//                                             GridBagConstraints.CENTER );
//        }
//        catch( AWTException e ) {
//            e.printStackTrace();
//        }
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
