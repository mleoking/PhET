/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.movingman.application.motionsuites.*;
import edu.colorado.phet.movingman.common.VerticalLayoutPanel;
import edu.colorado.phet.movingman.common.plaf.PlafUtil;
import edu.colorado.phet.movingman.elements.DataSeries;

import javax.swing.*;
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
    private MediaPanel mediaPanel;
    private ActionListener manualSetup;
    private MotionPanel motionPanel;
    private JButton reset;

    public void setRunningState() {
        mediaPanel.pause.setEnabled( false );
    }

    public void setPauseState() {
        mediaPanel.pause.doClick( 50 );
    }

    public void setMotionState() {
        mediaPanel.play.setEnabled( false );
//        mediaPanel.record.setEnabled( false );
        mediaPanel.pause.setEnabled( true );
        mediaPanel.slowMotion.setEnabled( false );
    }

    public void setFrame( PhetFrame frame ) {
        MotionSuite[] ms = motionPanel.motions;
        for( int i = 0; i < ms.length; i++ ) {
            MotionSuite m = ms[i];
            m.setFrame( frame );
        }
    }

    public void goPressed() {
        reset.setEnabled( true );
    }

    public class MediaPanel extends JPanel {

        private JButton play;
        private JButton pause;
//        private JButton reset;
//        private JButton record;
        private JButton rewind;
        private JButton slowMotion;

        public MediaPanel() {
            ImageIcon pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );
            pause = new JButton( "Pause", pauseIcon );
            pause.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //pausing from playback leaves it alone
                    module.setPaused( true );
                    play.setEnabled( true );
                    slowMotion.setEnabled( true );
                    pause.setEnabled( false );
                    rewind.setEnabled( true );
//                    record.setEnabled( true );
                }
            } );

            ImageIcon recordIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Movie24.gif" ) );
//            record = new JButton( "Record", recordIcon );
//            manualSetup = new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    doManual();
//                }
//            };
//            record.addActionListener( manualSetup );

            ImageIcon playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
            play = new JButton( "Playback", playIcon );
            play.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.startPlaybackMode( 1.0 );
                    play.setEnabled( false );
                    slowMotion.setEnabled( true );
                    pause.setEnabled( true );
                    rewind.setEnabled( true );
                }
            } );
            ImageIcon resetIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Stop24.gif" ) );
//            reset = new JButton( "Reset", resetIcon );
//            reset.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    module.reset();
//                }
//            } );

            ImageIcon rewindIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Rewind24.gif" ) );
            rewind = new JButton( "Rewind", rewindIcon );
            rewind.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.rewind();
                    rewind.setEnabled( false );
                }
            } );
//            record.setEnabled( false );


            ImageIcon slowIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/StepForward24.gif" ) );
            slowMotion = new JButton( "Slow Playback", slowIcon );
            slowMotion.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.startPlaybackMode( .4 );
                    play.setEnabled( true );
                    slowMotion.setEnabled( false );
                    pause.setEnabled( true );
                    rewind.setEnabled( true );
                }
            } );

//            add( record );
            add( play );
            add( slowMotion );
            add( pause );
            add( rewind );
//            add( reset );
        }

        private void doManual() {
            module.setRecordMode();
            module.setPaused( false );
//            record.setEnabled( false );
            pause.setEnabled( true );
            play.setEnabled( false );
            slowMotion.setEnabled( false );
            MotionSuite.hideDialogs();
        }

    }

    class MotionPanel extends VerticalLayoutPanel {
        private String recordMouseString;
        private ArrayList motionButtons;
        private MotionSuite[] motions;

        public MotionPanel() {

            motions = new MotionSuite[]{
                new StandSuite( module ),
                new WalkSuite( module ),
                new AccelerateSuite( module ),
                new OscillateSuite( module )
            };

            setBorder( PhetLookAndFeel.createSmoothBorder( "Motions" ) );
            recordMouseString = "Manual Control";
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
                        startRecordingManual();
                    }
                    else {
                        MotionSuite mac = motions[index - 1];
                        module.setMotionSuite( mac );
                        mac.showDialog();
                    }
                }
            };

            for( int i = 0; i < motions.length; i++ ) {
                motionButtons.add( new JRadioButton( motions[i].toString() ) );
            }

            for( int i = 0; i < motionButtons.size(); i++ ) {
                JRadioButton jRadioButton = (JRadioButton)motionButtons.get( i );

                add( jRadioButton );
                bg.add( jRadioButton );
                jRadioButton.addActionListener( changeListener );
            }

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

        private void setSelectedItem( int index ) {
            JRadioButton button = (JRadioButton)motionButtons.get( index );
            button.setSelected( true );
        }
    }

    public MovingManControlPanel( final MovingManModule module ) {
        this.module = module;
        final Dimension preferred = new Dimension( 200, 400 );
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

        mediaPanel = new MediaPanel();

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        add( panel, BorderLayout.NORTH );
        JButton changeControl = new JButton( "Change number of smoothing points." );
        changeControl.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String value = JOptionPane.showInputDialog( "How many smoothing points?", "15" );
                module.setNumSmoothingPoints( Integer.parseInt( value ) );
            }
        } );


        motionPanel = new MotionPanel();

        VerticalLayoutPanel northPanel = new VerticalLayoutPanel();
//        northPanel.setLayout( new BoxLayout( northPanel, BoxLayout.Y_AXIS ) );

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
        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        JLabel phetIconLabel = new JLabel( imageIcon );
        northPanel.add( phetIconLabel );
        northPanel.add( motionPanel );
        final JCheckBox invertAxes = new JCheckBox( "Invert X-Axis", false );
        invertAxes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRightDirPositive( !invertAxes.isSelected() );
            }
        } );
        northPanel.add( invertAxes );
        reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
                reset.setEnabled( false );
            }
        } );
        reset.setEnabled( false );
        northPanel.setFill( GridBagConstraints.NONE );
        northPanel.setAnchor( GridBagConstraints.WEST );
        northPanel.setInsets( new Insets( 4, 4, 4, 4 ) );
        northPanel.add( reset );
        panel.add( northPanel, BorderLayout.NORTH );
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

    public void startRecordingManual() {
        mediaPanel.doManual();
        motionPanel.setSelectedItem( 0 );
        reset.setEnabled( true );
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

    public void setPaused() {
        mediaPanel.pause.setEnabled( false );
        mediaPanel.play.setEnabled( true );
//        mediaPanel.record.setEnabled( true );
    }

    public JComponent getMediaPanel() {
        return mediaPanel;
    }

    public void resetComboBox() {
        motionPanel.setSelectedItem( 0 );
    }
}
