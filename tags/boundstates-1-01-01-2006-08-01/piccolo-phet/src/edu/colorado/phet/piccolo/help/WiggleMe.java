/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.help;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.activities.OscillateActivity;
import edu.colorado.phet.piccolo.nodes.BoundGraphic;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: Sam Reid
 * Date: Jul 28, 2005
 * Time: 12:30:23 PM
 * Copyright (c) Jul 28, 2005 by Sam Reid
 */

public class WiggleMe extends PNode {

    private OscillateActivity oscillate;
    boolean oscillating = false;

    public WiggleMe( String message, int x, int y ) {
        if( !message.startsWith( "<html>" ) ) {
            message = "<html>" + message + "</html>";
        }
        HTMLNode htmlGraphic = new HTMLNode( message );
        BoundGraphic htmlBound = new BoundGraphic( htmlGraphic, 2, 2 );
        htmlBound.setPaint( Color.yellow );
        addChild( htmlBound );
        addChild( htmlGraphic );

        oscillate = new OscillateActivity( this, x, y, new Vector2D.Double( 30, 0 ), 3.5 );
        setOscillating( true );
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        if( isVisible ) {
            setOscillating( true );
        }
        else {
            setOscillating( false );
        }
        ensureActivityCorrect();
    }

    public void setOscillating( boolean b ) {
        this.oscillating = b;
        ensureActivityCorrect();
    }

    protected void paint( PPaintContext paintContext ) {
        ensureActivityCorrect();
        super.paint( paintContext );
    }

    public void ensureActivityCorrect() {
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
        boolean ex = !( getRoot() == null || getRoot().getActivityScheduler() == null );
        return ex;
    }

    private void addActivity() {
        if( !getRoot().getActivityScheduler().getActivitiesReference().contains( oscillate ) ) {
            getRoot().getActivityScheduler().addActivity( oscillate );
        }
    }

    public void setOscillationCenter( Point2D center ) {
        oscillate.setOscillationCenter( center );
    }
}