package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseCanvas;

/**
 * Created by: Sam
 * Jun 26, 2008 at 11:57:34 AM
 */
public class ActivityLevelComboBox extends JComboBox {
    private EatingAndExerciseCanvas canvas;
    private Human human;

    public ActivityLevelComboBox( EatingAndExerciseCanvas canvas, final Human human ) {
        this.human = human;
        this.canvas = canvas;
        for ( int i = 0; i < Activity.DEFAULT_ACTIVITY_LEVELS.length; i++ ) {
            addItem( Activity.DEFAULT_ACTIVITY_LEVELS[i] );
        }
        updateSelectedItem();

        setFont( new PhetFont( 13, true ) );
        addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                human.setActivityLevel( ( (Activity) e.getItem() ) );
            }
        } );
        //todo: remove this workaround, which is necessary since piccolo pswing doesn't support jcombobox or pcombobox embedded in container within pswing
        setUI( new MyComboBoxUI() );
//        setSelectedItem(  );
        human.addListener( new Human.Adapter() {
            public void activityLevelChanged() {
                updateSelectedItem();
            }
        } );
    }

    private void updateSelectedItem() {
        for ( int i = 0; i < Activity.DEFAULT_ACTIVITY_LEVELS.length; i++ ) {
            if ( Activity.DEFAULT_ACTIVITY_LEVELS[i].getValue() == human.getActivityLevel() ) {
                setSelectedItem( Activity.DEFAULT_ACTIVITY_LEVELS[i] );
            }
        }
    }

    protected class MyComboBoxUI extends BasicComboBoxUI {
        protected ComboPopup createPopup() {
            return new MyComboPopup( comboBox );
        }
    }

    protected class MyComboPopup extends BasicComboPopup {
        public MyComboPopup( JComboBox combo ) {
            super( combo );
        }

        protected Rectangle computePopupBounds( int px, int py, int pw, int ph ) {
            Rectangle sup = super.computePopupBounds( px, py, pw, ph );
            double y = canvas.getControlPanelY();
            Rectangle2D r = new Rectangle2D.Double( 0, y + sup.y, 0, 0 );
            return new Rectangle( (int) r.getX(), (int) r.getMaxY(), (int) sup.getWidth(), (int) sup.getHeight() );
        }
    }

}