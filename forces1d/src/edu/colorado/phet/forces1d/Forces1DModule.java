/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.graphics.BufferedGraphic;
import edu.colorado.phet.common.view.plaf.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.forces1d.model.Forces1DModel;
import edu.colorado.phet.forces1d.model.MMTimer;
import edu.colorado.phet.forces1d.view.Force1DPanel;
import edu.colorado.phet.forces1d.view.PlotConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Forces1DModule extends Module implements PlotConnection {
    private Forces1DModel forceModel;
    private Force1DPanel forcePanel;
    private static final double MIN_TIME = 0.0;
    private static final double MAX_TIME = 20.0;

    public Forces1DModule() throws IOException {
        super( "Forces1D" );
        forceModel = new Forces1DModel();
        forcePanel = new Force1DPanel( this );
        setApparatusPanel( forcePanel );
        setModel( new BaseModel() );
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException {
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        Forces1DModule module = new Forces1DModule();
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha", new FrameSetup.CenteredWithSize( 800, 800 ), module, clock );
        PhetApplication phetApplication = new PhetApplication( model );
        phetApplication.startApplication();
    }

    public Forces1DModel getForceModel() {
        return forceModel;
    }

    public Force1DPanel getForcePanel() {
        return forcePanel;
    }

    public void setPaused( boolean b ) {
    }

    public void setRecordMode() {
    }

    public boolean isPaused() {
        return false;
    }

    public void reset() {
    }

    public void addListener( Listener listener ) {
    }

    public void cursorMovedToTime( double modelX ) {
    }

    public void relayout() {
    }

    public void repaintBackground( Rectangle viewBounds ) {
    }

    public BufferedGraphic getBackground() {
        return forcePanel.getBufferedGraphic();
    }

    public MMTimer getRecordingTimer() {
        return forceModel.getRecordingTimer();
    }

    public MMTimer getPlaybackTimer() {
        return forceModel.getPlaybackTimer();
    }

    public Color getBackgroundColor() {
        return Color.white;
    }

    public boolean isTakingData() {
        return false;
    }

    public void repaintBackground() {
    }

    public double getMinTime() {
        return MIN_TIME;
    }

    public double getMaxTime() {
        return MAX_TIME;
    }
}
