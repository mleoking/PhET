package edu.colorado.phet.cck;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitChangeListener;
import edu.colorado.phet.cck.model.ResistivityManager;
import edu.colorado.phet.cck.model.components.Branch;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 10:55:51 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */
public interface ICCKModule {
    public static Color BACKGROUND_COLOR = new Color( 100, 160, 255 );

    Circuit getCircuit();

    void setLifelike( boolean b );

    boolean isLifelike();

    CircuitChangeListener getCircuitChangeListener();

    CCKParameters getParameters();

    JComponent getSimulationPanel();

    void setVoltmeterVisible( boolean visible );

    void setVirtualAmmeterVisible( boolean selected );

    void setSeriesAmmeterVisible( boolean selected );

    boolean isStopwatchVisible();

    void setStopwatchVisible( boolean selected );

    void addCurrentChart();

    void addVoltageChart();

    void setAllReadoutsVisible( boolean r );

    void setCircuit( Circuit circuit );

    boolean isInternalResistanceOn();

    void setZoom( double scale );

    void clear();

    ResistivityManager getResistivityManager();

    boolean isElectronsVisible();

    void setElectronsVisible( boolean b );

    Rectangle2D getModelBounds();

    void layoutElectrons( Branch[] branches );

    void resetDynamics();

    void selectAll();

    void addTestCircuit();

    void deleteSelection();

    void desolderSelection();
}
