package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.BufferedGraphic;
import edu.colorado.phet.forces1d.model.MMTimer;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 14, 2004
 * Time: 3:06:42 PM
 * Copyright (c) Nov 14, 2004 by Sam Reid
 */
public interface PlotConnection {
    ApparatusPanel getApparatusPanel();

    void setPaused( boolean b );

    void setRecordMode();

    boolean isPaused();

    void reset();

    void addListener( Listener listener );

    void cursorMovedToTime( double modelX );

    void relayout();

    void repaintBackground( Rectangle viewBounds );

    BufferedGraphic getBackground();

    MMTimer getRecordingTimer();

    MMTimer getPlaybackTimer();

    Color getBackgroundColor();

    boolean isTakingData();

    void repaintBackground();

    double getMinTime();

    double getMaxTime();

    public class ListenerAdapter implements Listener{
    }

    public interface Listener {
    }
}
