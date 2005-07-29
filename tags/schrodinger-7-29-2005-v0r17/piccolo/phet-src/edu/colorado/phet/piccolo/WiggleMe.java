/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 12:30:23 PM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class WiggleMe extends PNode {
    private String message;
    private PActivity oscillate;
    boolean oscillating = false;

    public WiggleMe( String message ) {
        this.message = message;
        if( !message.startsWith( "<html>" ) ) {
            message = "<html>" + message + "</html>";
        }
        HTMLGraphic htmlGraphic = new HTMLGraphic( message );
        BoundGraphic htmlBound = new BoundGraphic( htmlGraphic );
        htmlBound.setPaint( Color.yellow );
        addChild( htmlBound );
        addChild( htmlGraphic );

        oscillate = new Oscillate( this );
    }

    public void setOscillating( boolean b ) {
        this.oscillating = b;
        ensureActivityCorrect();
    }

    protected void paint( PPaintContext paintContext ) {
        ensureActivityCorrect();
        super.paint( paintContext );
    }

    private void ensureActivityCorrect() {
        if( rootSchedulerExists() ) {
            if( oscillating && !isActivityRunning() ) {
                addActivity();
            }
            else if( !oscillating && isActivityRunning() ) {
                removeActivity();
            }
        }
    }

    private void removeActivity() {
        while( getRoot().getActivityScheduler().getActivitiesReference().contains( oscillate ) ) {
            getRoot().getActivityScheduler().removeActivity( oscillate );
        }
    }

    private boolean isActivityRunning() {
        if( !rootSchedulerExists() ) {
            return false;
        }
        else {
            PActivityScheduler sched = getRoot().getActivityScheduler();
            return sched.getActivitiesReference().contains( oscillate );
        }
    }

    private boolean rootSchedulerExists() {
        return !( getRoot() == null || getRoot().getActivityScheduler() == null );
    }

    private void addActivity() {
        if( !getRoot().getActivityScheduler().getActivitiesReference().contains( oscillate ) ) {
            getRoot().getActivityScheduler().addActivity( oscillate );
        }
    }
}
