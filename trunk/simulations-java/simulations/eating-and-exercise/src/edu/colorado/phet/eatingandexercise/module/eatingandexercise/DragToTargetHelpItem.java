package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.eatingandexercise.control.CaloricItem;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Aug 15, 2008 at 2:41:17 PM
 */
public class DragToTargetHelpItem {
    private MotionHelpBalloon motionHelpBalloon;
    private EatingAndExerciseModule module;
    private EatingAndExerciseCanvas canvas;
    private PNode dropTarget;

    public DragToTargetHelpItem( final EatingAndExerciseModule module, EatingAndExerciseCanvas canvas, PNode dropTarget, String helpMessage, final CalorieSet selectionModel ) {
        this.module = module;
        this.canvas = canvas;
        this.dropTarget = dropTarget;
        motionHelpBalloon = new DefaultWiggleMe( canvas, helpMessage );
        motionHelpBalloon.setArrowTailPosition( MotionHelpBalloon.TOP_RIGHT );
        motionHelpBalloon.setOffset( 0, 0 );
        selectionModel.addListener( new CalorieSet.Adapter() {
            public void itemAdded( CaloricItem item ) {
                MotionHelpBalloon balloon = DragToTargetHelpItem.this.motionHelpBalloon;
                balloon.getParent().removeChild( balloon );
                balloon.setEnabled( false );

                selectionModel.removeListener( this );
            }
        } );
    }

    public void start() {
        module.getDefaultHelpPane().add( motionHelpBalloon );
        motionHelpBalloon.animateTo( dropTarget, 1500 );
    }
}
