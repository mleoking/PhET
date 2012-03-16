// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import edu.colorado.phet.common.piccolophet.help.DefaultWiggleMe;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Created by: Sam
 * May 7, 2008 at 2:41:45 PM
 */
public class EatingAndExerciseWiggleMe extends DefaultWiggleMe {
    private PActivity activity;
    private PNode target;
    private PCanvas canvas;

    public EatingAndExerciseWiggleMe( final PCanvas canvas, PNode target ) {
        super( canvas, EatingAndExerciseResources.getString( "diet.choose" ) );
        this.target = target;
        this.canvas = canvas;

        setBackground( Color.green );
        setOffset( 1000, 1000 );
        setArrowTailPosition( MotionHelpBalloon.TOP_LEFT );
        setArrowLength( 60 );

        // Clicking on the canvas makes the wiggle me go away.
        canvas.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                setEnabled( false );
                getParent().removeChild( EatingAndExerciseWiggleMe.this );
                canvas.removeMouseListener( this );
            }
        } );
    }

    public void updateWiggleMeTarget() {
        //todo: remove this workaround for when this wiggle me has been removed
        if ( getParent() != null ) {
            PBounds bounds = target.getGlobalFullBounds();
            getParent().globalToLocal( bounds );
            if ( activity != null ) {
                activity.terminate();
                setOffset( 800, 600 );
            }
            activity = animateTo( bounds.getCenterX(), bounds.getMaxY() );
        }
    }

}
