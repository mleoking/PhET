package edu.colorado.phet.common.phetcommon.application;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.Timer;

/**
 * This dialog attempts to paint its content pane immediately using a Timer.
 * Use this in situations where your dialog doesn't paint in a timely manner (or at all).
 * <p>
 * Workaround was originally developed in response to Unfuddle #89.
 * See #2072 for redesign to fix performance problems.
 * <p>
 * TODO: This is unnecessary in Java 6.
 */
public class PaintImmediateDialog extends JDialog {
    
    private static final int TIMER_DELAY = 250; // ms, initial delay and between-event delay
    
    private PaintImmediateTimer timer;
    
    public PaintImmediateDialog() {
        super();
        initPaintImmediateDialog();
    }

    public PaintImmediateDialog( Frame frame ) {
        super( frame );
        initPaintImmediateDialog();
    }

    public PaintImmediateDialog( Frame frame, String title ) {
        super( frame, title );
        initPaintImmediateDialog();
    }

    public PaintImmediateDialog( Dialog owner ) {
        super( owner );
        initPaintImmediateDialog();
    }

    public PaintImmediateDialog( Dialog owner, String title ) {
        super( owner, title );
        initPaintImmediateDialog();
    }

    public PaintImmediateDialog( Frame owner, boolean modal ) {
        super( owner, modal );
        initPaintImmediateDialog();
    }
    
    public PaintImmediateDialog( Dialog owner, boolean modal ) {
        super( owner, modal );
        initPaintImmediateDialog();
    }
    
    public PaintImmediateDialog( Frame owner, String title, boolean modal ) {
        super( owner, title, modal );
        initPaintImmediateDialog();
    }
    
    public PaintImmediateDialog( Dialog owner, String title, boolean modal ) {
        super( owner, title, modal );
        initPaintImmediateDialog();
    }
    
    /**
     * Enables/disables the workaround, for testing purposes.
     * true -> workaround enabled, timer paints contentPane while visible,
     * false -> workaround disabled, behaves exactly like a JDialog.
     * @param enabled
     */
    public void setWorkaroundEnabled( boolean enabled ) {
        timer.setWorkaroundEnabled( enabled );
        if ( isVisible() && enabled ) {
            timer.start();
        }
    }
    
    /**
     * Enables/disables debug output on System.out, for testing purposes.
     * @param enabled
     */
    public void setDebugOutputEnabled( boolean enabled ) {
        timer.setDebugOutputEnabled( enabled );
    }
    
    /*
     * Initialization specific to this class.
     */
    private void initPaintImmediateDialog() {
        
        // Timer that periodically calls paintImmediately on the dialog's content pane
        timer = new PaintImmediateTimer( this );
        
        //TODO will window events be received in a timely manner?
        addWindowListener( new WindowAdapter() {
            
            // Start the timer when the dialog is opened.
            @Override
            public void windowOpened( WindowEvent e ) {
                timer.start();
            }

            // Stop the timer when the dialog is closed.
            @Override
            public void windowClosed( WindowEvent e ) {
                timer.stop();
            }
            
            // Stop the timer when the dialog is iconified.
            @Override
            public void windowIconified( WindowEvent e ) {
                timer.stop();
            }

            // Start the timer when the dialog is deiconfied.
            @Override
            public void windowDeiconified( WindowEvent e ) {
                timer.start();
            }
        });
    }
    
    /*
     * A timer that periodically repaints some component.
     */
    private static class PaintImmediateTimer extends Timer {
        
        private boolean workaroundEnabled = true;
        private boolean debugOutputEnabled = false;
        
        public PaintImmediateTimer( final JDialog dialog ) {
            super( TIMER_DELAY, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                	JComponent component = (JComponent)dialog.getContentPane();
                	component.paintImmediately( 0, 0, component.getWidth(), component.getHeight() );
                }
            } );
            setRepeats( true ); // yes, this intentionally repeats, see #2072
        }
        
        public void setWorkaroundEnabled( boolean enabled ) {
            workaroundEnabled = enabled;
        }
        
        public void setDebugOutputEnabled( boolean enabled ) {
            debugOutputEnabled = enabled;
        }
        
        @Override
        public void start() {
            if ( debugOutputEnabled ) {
                System.out.println( "PaintImmediateTimer.start" );
            }
            if ( workaroundEnabled ) {
                super.start();
            }
        }
        
        @Override
        public void stop() {
            if ( debugOutputEnabled ) {
                System.out.println( "PaintImmediateTimer.stop" );
            }
            if ( workaroundEnabled ) {
                super.stop();
            }
        }
    }

}
