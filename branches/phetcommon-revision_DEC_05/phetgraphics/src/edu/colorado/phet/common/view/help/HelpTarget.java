package edu.colorado.phet.common.view.help;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 9, 2005
 * Time: 9:21:11 AM
 * Copyright (c) May 9, 2005 by Sam Reid
 */
public abstract class HelpTarget {
    private ArrayList listeners = new ArrayList();

    public void addListener( HelpTargetListener helpTargetListener ) {
        listeners.add( helpTargetListener );
    }

    protected void notifyVisibilityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            HelpTargetListener helpTargetListener = (HelpTargetListener)listeners.get( i );
            helpTargetListener.targetVisibilityChanged();
        }
    }

    protected void notifyLocationChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            HelpTargetListener helpTargetListener = (HelpTargetListener)listeners.get( i );
            helpTargetListener.targetLocationChanged();
        }
    }

    public abstract boolean isVisible();

    public abstract Point getLocation();
}
