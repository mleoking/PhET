package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.common.VerticalLayoutPanel;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:02:09 PM
 * To change this template use Options | File Templates.
 */
public abstract class MotionSuite {
    private StepMotion motion;
    private JPanel controlPanel;
    private String name;

    private JSpinner initialPositionSpinner;
    private JButton pauseButton;
    private JButton goButton;
    private JButton resetButton;
    private JDialog dialog;
    private static ArrayList dialogs = new ArrayList();
    private MovingManModule module;

    public MotionSuite( final MovingManModule module, String name ) {
        this.module = module;
        this.name = name;
        this.controlPanel = new VerticalLayoutPanel();

        initialPositionSpinner = new JSpinner( new SpinnerNumberModel( 0.0, -10, 10, 1 ) );
        initialPositionSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Number loc = (Number)initialPositionSpinner.getValue();
                double init = loc.doubleValue();
                module.setInitialPosition( init );
                module.setPauseMode();
                pauseButton.setEnabled( false );
                module.getMovingManControlPanel().setRunningState();
                goButton.setEnabled( true );
            }
        } );
        Border tb = PhetLookAndFeel.createSmoothBorder( "Initial Position" );

        initialPositionSpinner.setBorder( tb );

        ImageIcon pauseIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Pause24.gif" ) );
        pauseButton = new JButton( "Pause", pauseIcon );
        pauseButton.setEnabled( false );
        pauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doPause();
            }
        } );

        ImageIcon playIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Play24.gif" ) );
        goButton = new JButton( "Go!", playIcon );
        goButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doGo();
            }
        } );
        ImageIcon resetIcon = new ImageIcon( new ImageLoader().loadImage( "images/icons/java/media/Stop24.gif" ) );
        resetButton = new JButton( "Reset", resetIcon );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                doReset();
            }
        } );
        createDialog();
    }

    protected MovingManModule getModule() {
        return module;
    }

    private void doPause() {
        module.getMovingManControlPanel().setPauseState();
        goButton.setEnabled( true );
        pauseButton.setEnabled( false );
    }

    private void doGo() {
        module.setMotionMode( MotionSuite.this );//.getStepMotion());
        goButton.setEnabled( false );
        pauseButton.setEnabled( true );
        resetButton.setEnabled( true );
    }

    private void doReset() {
        module.reset();
    }

    public String toString() {
        return name;
    }

    public JButton getPauseButton() {
        return pauseButton;
    }

    public JSpinner getInitialPositionSpinner() {
        return initialPositionSpinner;
    }

    public MotionSuite( StepMotion motion, JPanel controlPanel ) {
        this.motion = motion;
        this.controlPanel = controlPanel;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public StepMotion getStepMotion() {
        return motion;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public void setMotion( StepMotion motion ) {
        this.motion = motion;
    }

    public static JLabel createLabel( String text ) {
        JLabel labbie = new JLabel( text );
        return labbie;
    }

    public abstract void initialize( Man man );

    public abstract void collidedWithWall();

    public void reset() {
        getInitialPositionSpinner().setValue( new Double( 0 ) );
        goButton.setEnabled( true );
        pauseButton.setEnabled( false );
        resetButton.setEnabled( false );
    }

    public void showDialog() {
        module.getMovingManControlPanel().setPauseState();
        pauseButton.setEnabled( false );
        module.setMotionMode( this );//.getStepMotion());
        initialize( module.getMan() );
        module.setPauseMode();

        dialog.pack();
        JFrame f = (JFrame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
        centerInParent( dialog, f );
        moveRight( dialog );
        dialog.setVisible( true );
        SwingUtilities.updateComponentTreeUI( dialog );
        repaintDialog();
        repaintLater();
    }

    private void createDialog() {
        dialog = new JDialog( (Frame)SwingUtilities.getWindowAncestor( module.getApparatusPanel() ), "Controls", false );
        dialog.addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                repaintLater();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
                repaintNowAndLater();
            }

            public void windowGainedFocus( WindowEvent e ) {
                repaintNowAndLater();
            }

            public void windowOpened( WindowEvent e ) {
                repaintNowAndLater();
            }

            public void windowDeiconified( WindowEvent e ) {
                repaintNowAndLater();

            }
        } );
        dialogs.add( dialog );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                if( module.isMotionMode() ) {
                    module.setPauseMode();
                }
                module.getMovingManControlPanel().resetComboBox();
            }
        } );
//        controls.getInitialPositionSpinner().setValue( new Double( module.getMan().getX() ) );
        //TODO this causes resets.

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        if( !( this instanceof OscillateSuite ) ) {
            panel.add( getInitialPositionSpinner() );
        }
        panel.add( controlPanel );

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( goButton );
        buttonPanel.add( pauseButton );
        buttonPanel.add( resetButton );

        panel.add( buttonPanel );
        dialog.setContentPane( panel );
        dialog.setTitle( getName() );
        dialog.pack();
    }

    private void repaintNowAndLater() {
        repaintDialog();
        repaintLater();
    }

    private void repaintLater() {
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 150 );
                    repaintDialog();
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();

    }

    private void repaintDialog() {
        dialog.invalidate();
        dialog.validate();
        dialog.repaint();
        Container pan = dialog.getContentPane();
        pan.invalidate();
        pan.validate();
        pan.repaint();
    }

    private void moveRight( JDialog dialog ) {
        int width = dialog.getWidth();
        Window ancestor = SwingUtilities.getWindowAncestor( module.getApparatusPanel() );
        int x = ancestor.getWidth() + ancestor.getX() - width;
        dialog.setLocation( x, dialog.getY() );
    }

    public static void centerInParent( JDialog dialog, JFrame parent ) {
        Rectangle frameBounds = parent.getBounds();
        Rectangle dialogBounds = new Rectangle( (int)( frameBounds.getMinX() + frameBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int)( frameBounds.getMinY() + frameBounds.getHeight() / 2 - dialog.getHeight() / 2 ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    public void hideDialogs() {
        for( int i = 0; i < dialogs.size(); i++ ) {
            JDialog jDialog = (JDialog)dialogs.get( i );
            jDialog.setVisible( false );
        }
    }

    public void deactivate() {
        dialog.setVisible( false );
    }

    public void timeFinished() {
        pauseButton.doClick( 100 );
        goButton.setEnabled( false );
        pauseButton.setEnabled( false );
    }

    public void setControlPanel( JPanel panel ) {
        this.controlPanel = panel;
    }
}
