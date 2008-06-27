package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.eatingandexercise.model.EatingAndExerciseUnits;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.view.LabelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 24, 2008 at 11:48:26 AM
 */
public class WarningMessage extends PNode {
    private long lastVisibilityRestart;
    private Human human;

    public WarningMessage( final Human human ) {
        this.human = human;
        LabelNode labelNode = new LabelNode( "<html>This simulation is based on data<br>from 20-60 year olds.<html>" );
        addChild( labelNode );

        human.addListener( new Human.Adapter() {
            public void ageChanged() {
                update();
            }
        } );

        Timer timer = new Timer( 100, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
                updateVisibility();
            }
        } );
        timer.start();
    }

    private void update() {
        if ( human.getAge() < EatingAndExerciseUnits.yearsToSeconds( 20 ) || human.getAge() > EatingAndExerciseUnits.yearsToSeconds( 60 ) ) {
            resetVisibleTime();
        }
    }

    private void updateVisibility() {
        setVisible( System.currentTimeMillis() - lastVisibilityRestart < 5000 );
    }

    private void resetVisibleTime() {
        this.lastVisibilityRestart = System.currentTimeMillis();
        updateVisibility();
    }

}