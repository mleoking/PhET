package edu.colorado.phet.fitness.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Created by: Sam
 * Apr 23, 2008 at 9:15:17 AM
 */
public class ActivityNode extends PNode {
    public static class Activity {
        private String name;
        private double activityLevel;

        public Activity( String name, double activityLevel ) {
            this.name = name;
            this.activityLevel = activityLevel;
        }

        public String toString() {
            return name;
        }

        public double getActivityLevel() {
            return activityLevel;
        }
    }

    public ActivityNode( final FitnessModel model, PhetPCanvas phetPCanvas ) {
        Activity moderate = new Activity( "Moderate Activity", 1.5 );
        final PComboBox p = new PComboBox( new Object[]{
                new Activity( "Very Sedentary", 1.3 ),
                new Activity( "Sedentary", 1.4 ),
                moderate,
                new Activity( "Very Active", 1.6 ),
                new Activity( "Competitive Athlete", 1.7 )
        } );
        PSwing child = new PSwing( p );
        p.setEnvironment( child, phetPCanvas );
        p.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Activity a = (Activity) p.getSelectedItem();
                model.getHuman().setActivityLevel( a.getActivityLevel() - 1 );
            }
        } );
        p.setSelectedItem( moderate );

        addChild( child );
    }
}
