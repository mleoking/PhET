package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.eatingandexercise.model.Human;

/**
 * Created by: Sam
 * Jun 26, 2008 at 4:37:47 PM
 */
public class GameOverDialog {
    private PhetFrame parentFrame;
    private Human human;
    private EatingAndExerciseModule module;

    public GameOverDialog( PhetFrame parentFrame, final Human human, EatingAndExerciseModule module ) {
        this.parentFrame = parentFrame;
        this.human = human;
        this.module = module;
        human.addListener( new Human.Adapter() {
            public void aliveChanged() {
                if ( !human.isAlive() ) {
                    String causeOfDeath = human.getCauseOfDeath();
                    showDialog( causeOfDeath );
                }
            }
        } );
    }

    private void showDialog( final String causeOfDeath ) {
        final Timer timer = new Timer( 1000, null );
        timer.setRepeats( false );

        timer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getClock().pause();
                JOptionPane.showMessageDialog( parentFrame, "Game Over.  Body died from " + causeOfDeath + ". Click to start over." );
                module.resetAll();
            }
        } );
        timer.start();

    }

    public void start() {
    }
}
