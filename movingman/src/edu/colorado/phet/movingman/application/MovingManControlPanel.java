/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.movingman.application.motionsuites.AccelerateSuite;
import edu.colorado.phet.movingman.application.motionsuites.MotionSuite;
import edu.colorado.phet.movingman.application.motionsuites.OscillateSuite;
import edu.colorado.phet.movingman.application.motionsuites.WalkSuite;
import edu.colorado.phet.movingman.common.PhetLookAndFeel;
import edu.colorado.phet.movingman.common.plaf.PlafUtil;
import edu.colorado.phet.movingman.elements.DataSeries;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 1:11:38 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManControlPanel extends JPanel {
    private MovingManModule module;
    private JPanel controllerContainer;
//    private JPanel mediaPanel;
    private MediaPanel mediaPanel;
    MotionSuite selectedMotion;
    private MotionActivation mact;

    private ActionListener manualSetup;
    private String recordMouseString;
    private ArrayList motionButtons;

    public class MediaPanel extends JPanel {

        private JButton play;
        private JButton pause;
        private JButton reset;
        private JButton record;
        private JButton rewind;
        private JButton slowMotion;

        public MediaPanel() {

            ImageIcon pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );
            pause = new JButton( "Pause", pauseIcon );
            pause.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //pausing from playback leaves it alone
                    module.setPauseMode();
                    play.setEnabled( true );
                    slowMotion.setEnabled( true );
                    pause.setEnabled( false );
                    record.setEnabled( true );
                }
            } );

            ImageIcon recordIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Movie24.gif" ) );
            record = new JButton( "Record", recordIcon );
            manualSetup = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRecordMode();
                    record.setEnabled( false );
                    pause.setEnabled( true );
                    play.setEnabled( false );
                    slowMotion.setEnabled( false );
                }
            };
            record.addActionListener( manualSetup );

            ImageIcon playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
            play = new JButton( "Playback", playIcon );
            play.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPlaybackMode( 1.0 );
                    play.setEnabled( false );
                    slowMotion.setEnabled( true );
                    pause.setEnabled( true );
                }
            } );
            ImageIcon resetIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Stop24.gif" ) );
            reset = new JButton( "Reset", resetIcon );
            reset.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.reset();
                    module.setPauseMode();
                    getInitialPositionSpinner().setValue( new Double( 0 ) );
                }
            } );

            ImageIcon rewindIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Rewind24.gif" ) );
            rewind = new JButton( "Rewind", rewindIcon );
            rewind.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.rewind();
                }
            } );
            record.setEnabled( false );


            ImageIcon slowIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/StepForward24.gif" ) );
            slowMotion = new JButton( "Slow Playback", slowIcon );
            slowMotion.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPlaybackMode( .4 );
                    play.setEnabled( true );
                    slowMotion.setEnabled( false );
                    pause.setEnabled( true );
                }
            } );

            add( record );
            add( play );
            add( slowMotion );
            add( pause );
            add( rewind );
            add( reset );
        }

    }

    public JButton getAnotherPauseButton() {
        return anotherPauseButton;
    }

    private JButton anotherPauseButton;

    public JSpinner getInitialPositionSpinner() {
        return initialPositionSpinner;
    }

    private JSpinner initialPositionSpinner;

    public JButton getStartMotionButton() {
        return startMotion;
    }

    private JButton startMotion;

    public MovingManControlPanel( final MovingManModule module ) {

        this.module = module;
        final Dimension preferred = new Dimension( 200, 400 );
        StepMotion stay = new StepMotion() {
            public double stepInTime( Man man, double dt ) {
                return man.getX();
            }
        };

        mact = new MotionActivation( module );
        JPanel standStillPanel = new JPanel();
        standStillPanel.add( new JLabel( "<html>No controls.<br>Click 'Run Motion' <br>to start standing still.</html>" ) );

        setSize( preferred );
        setPreferredSize( preferred );
        setLayout( new BorderLayout() );

        controllerContainer = new JPanel();
        controllerContainer.setPreferredSize( new Dimension( 200, 200 ) );
        controllerContainer.setSize( new Dimension( 200, 200 ) );
        add( controllerContainer, BorderLayout.CENTER );


        final JCheckBox positionBox = new JCheckBox( "Position", true );
        positionBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPositionGraphVisible( positionBox.isSelected() );
            }
        } );
        final JCheckBox velocityBox = new JCheckBox( "Velocity", true );
        velocityBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setVelocityGraphVisible( velocityBox.isSelected() );
            }
        } );
        final JCheckBox accelerationBox = new JCheckBox( "Acceleration", true );
        accelerationBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setAccelerationGraphVisible( accelerationBox.isSelected() );
            }
        } );
        JPanel boxes = new JPanel();
        boxes.setLayout( new BoxLayout( boxes, BoxLayout.Y_AXIS ) );
        boxes.add( positionBox );
        boxes.add( velocityBox );
        boxes.add( accelerationBox );
        boxes.setBorder( PhetLookAndFeel.createSmoothBorder( "Show Plots" ) );
        add( boxes, BorderLayout.SOUTH );

        ImageIcon playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
        startMotion = new JButton( "Go!", playIcon );
        startMotion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setMotionMode( selectedMotion );//.getStepMotion());
                mediaPanel.play.setEnabled( false );
                mediaPanel.record.setEnabled( false );
                mediaPanel.pause.setEnabled( true );
                mediaPanel.slowMotion.setEnabled( false );
                startMotion.setEnabled( false );
                anotherPauseButton.setEnabled( true );
            }
        } );


        startMotion.setEnabled( false );
        mediaPanel = new MediaPanel();

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        initialPositionSpinner = new JSpinner( new SpinnerNumberModel( 0.0, -10, 10, 1 ) );
        initialPositionSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number loc = (Number)initialPositionSpinner.getValue();
                double init = loc.doubleValue();
                module.setInitialPosition( init );
                module.setPauseMode();
                anotherPauseButton.setEnabled( false );
                mediaPanel.pause.setEnabled( false );
                startMotion.setEnabled( true );
            }
        } );
        Border tb = PhetLookAndFeel.createSmoothBorder( "Initial Position" );

        initialPositionSpinner.setBorder( tb );
        add( panel, BorderLayout.NORTH );
        ImageIcon pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );
        anotherPauseButton = new JButton( "Pause", pauseIcon );
        anotherPauseButton.setEnabled( false );
        anotherPauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mediaPanel.pause.doClick( 50 );
                startMotion.setEnabled( true );
                anotherPauseButton.setEnabled( false );
            }
        } );

        JButton changeControl = new JButton( "Change number of smoothing points." );
        changeControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String value = JOptionPane.showInputDialog( "How many smoothing points?", "15" );
                module.setNumSmoothingPoints( Integer.parseInt( value ) );
            }
        } );

        MotionSuite still = new MotionSuite( stay, standStillPanel ) {
            public void initialize( Man man ) {
            }

            public void collidedWithWall() {
            }
        };
        still.setName( "Stand Very Still" );
//        final String init = ( "Choose Motion" );

        final MotionSuite[] motions = new MotionSuite[]{still, new WalkSuite( module ),
                                                        new AccelerateSuite( module ),
                                                        new OscillateSuite( module )
        };

        JPanel motionPanel = new JPanel();
        motionPanel.setBorder( PhetLookAndFeel.createSmoothBorder( "Motions" ) );

        motionPanel.setLayout( new BoxLayout( motionPanel, BoxLayout.Y_AXIS ) );

        this.recordMouseString = "Manual Control";
        JRadioButton jrb = new JRadioButton( recordMouseString );
        jrb.setSelected( true );
        motionButtons = new ArrayList();
        motionButtons.add( jrb );
        ButtonGroup bg = new ButtonGroup();

        ActionListener changeListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                int index = getSelectedButton();
                module.getManGraphic().setShowIdea( false );
                if( index == 0 ) {
                    setManualMode();
                }
                else {
                    MotionSuite mac = motions[index - 1];
                    selectedMotion = mac;
                    mact.setupInDialog( mac, MovingManControlPanel.this );
                }
            }
        };

        for( int i = 0; i < motions.length; i++ ) {
            motionButtons.add( new JRadioButton( motions[i].toString() ) );
        }

        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        JLabel phetIconLabel = new JLabel( imageIcon );
//        motionPanel.add( new JLabel( imageIcon ) );
        for( int i = 0; i < motionButtons.size(); i++ ) {
            JRadioButton jRadioButton = (JRadioButton)motionButtons.get( i );

            motionPanel.add( jRadioButton );
            bg.add( jRadioButton );
            jRadioButton.addActionListener( changeListener );
        }

        JPanel northPanel = new JPanel();
        northPanel.setLayout( new BoxLayout( northPanel, BoxLayout.Y_AXIS ) );

        final JMenu viewMenu = new JMenu( "View" );
        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < items.length; i++ ) {
            JMenuItem item = items[i];
            viewMenu.add( item );
        }

        new Thread( new Runnable() {
            public void run() {
                try {
                    while( MovingManModule.FRAME == null || !MovingManModule.FRAME.isVisible() ) {
                        Thread.sleep( 1000 );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                }
                MovingManModule.FRAME.getJMenuBar().add( viewMenu );
                MovingManModule.FRAME.setExtendedState( JFrame.MAXIMIZED_HORIZ );
                MovingManModule.FRAME.setExtendedState( JFrame.MAXIMIZED_BOTH );
            }
        } ).start();
        northPanel.add( phetIconLabel );
        northPanel.add( motionPanel );
        final JCheckBox invertAxes = new JCheckBox( "Invert X-Axis", false );
        invertAxes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRightDirPositive( !invertAxes.isSelected() );
            }
        } );
        northPanel.add( invertAxes );
        panel.add( northPanel, BorderLayout.NORTH );
    }

    private int getSelectedButton() {
        for( int i = 0; i < motionButtons.size(); i++ ) {
            JRadioButton jRadioButton = (JRadioButton)motionButtons.get( i );
            if( jRadioButton.isSelected() ) {
                return i;
            }
        }
        return -1;
    }

    public static DataSeries parseString( String str ) {
        DataSeries data = new DataSeries();
        StringTokenizer st = new StringTokenizer( str );
        while( st.hasMoreElements() ) {
            String s = (String)st.nextElement();
            double value = Double.parseDouble( s );
            data.addPoint( value );
        }
        return data;
    }

    public void setManualMode() {
        manualSetup.actionPerformed( null );
        setSelectedItem( 0 );
        mact.clearDialogs();
    }

    private void setSelectedItem( int index ) {
        JRadioButton button = (JRadioButton)motionButtons.get( index );
        button.setSelected( true );
    }

    public static String toString( DataSeries data ) {
        StringBuffer string = new StringBuffer();
        for( int i = 0; i < data.size(); i++ ) {
            double x = data.pointAt( i );
            string.append( x );
            if( i < data.size() ) {
                string.append( "," );
            }
        }
        return string.toString();
    }

    public void invokeMotionMode( MotionSuite mac ) {
        mediaPanel.pause.setEnabled( true );
        mediaPanel.play.setEnabled( false );
        mediaPanel.record.setEnabled( false );
        module.setMotionMode( mac );
    }

    public void setPaused() {
        mediaPanel.pause.setEnabled( false );
        mediaPanel.play.setEnabled( true );
        mediaPanel.record.setEnabled( true );
    }

    public Container getControllerContainer() {
        return controllerContainer;
    }

    public JComponent getMediaPanel() {
        return mediaPanel;
    }

    public void resetComboBox() {
        setSelectedItem( 0 );
    }
}
