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
    
    /*
     * This flag is for testing purposes, to confirm that the workaround is effective.
     * true -> workaround enabled, timer paints contentPane while visible
     * false -> workaround disabled, behaves exactly like a JDialog.
     */
    private static final boolean WORKAROUND_ENABLED = true;
    
    private static final int TIMER_DELAY = 250; // ms, initial delay and between-event delay
    
    private PaintImmediateTimer timer;
    
    public PaintImmediateDialog() {
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
    
    /*
     * Initialization specific to this class.
     */
    private void initPaintImmediateDialog() {
        
        // Timer that periodically calls paintImmediately on the dialog's content pane
        timer = new PaintImmediateTimer( (JComponent) getContentPane() );
        
        //TODO will window events be received in a timely manner?
        addWindowListener( new WindowAdapter() {
            
            // Stop the timer when the dialog is iconified.
            public void windowIconified( WindowEvent e ) {
                timer.stop();
            }

            // Start the timer when the dialog is deiconfied.
            public void windowDeiconified( WindowEvent e ) {
                timer.start();
            }
        });
    }
    
    /**
     * Run the timer only while the dialog is visible. 
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            timer.start();
        }
        else {
            timer.stop();
        }
    }
    
    /**
     * Some PhET sims used the deprecated show method, so support it.
     * @deprecated
     */
    @Override
    public void show() {
        timer.start();
        super.show();
    }
    
    /**
     * Some PhET sims used the deprecated hide method, so support it.
     * @deprecated
     */
    @Override
    public void hide() {
        timer.stop();
        super.hide();
    }
    
    /**
     * Stop the timer when the dialog is disposed.
     */
    @Override
    public void dispose() {
        timer.stop();
        super.dispose();
    }
    
    /*
     * A timer that periodically repaints some component.
     */
    private static class PaintImmediateTimer extends Timer {
        
        public PaintImmediateTimer( final JComponent component ) {
            super( TIMER_DELAY, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    component.paintImmediately( 0, 0, component.getWidth(), component.getHeight() );
                }
            } );
            setRepeats( true ); // yes, this intentionally repeats, see #2072
        }
        
        public void start() {
            if ( WORKAROUND_ENABLED ) {
                super.start();
            }
        }
    }

}
