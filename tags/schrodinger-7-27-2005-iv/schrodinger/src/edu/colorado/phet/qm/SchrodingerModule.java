/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.IntensityDisplay;
import edu.colorado.phet.qm.view.RectangularPotentialGraphic;
import edu.colorado.phet.qm.view.SchrodingerPanel;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerModule extends Module {
    private SchrodingerPanel schrodingerPanel;
    private DiscreteModel discreteModel;
    private SchrodingerControlPanel schrodingerControlPanel;
    private SchrodingerApplication schrodingerApplication;

    /**
     * @param clock
     */
    public SchrodingerModule( String name, SchrodingerApplication clock ) {
        super( name, clock.getClock() );
        this.schrodingerApplication = clock;
        setModel( new BaseModel() );
    }

    protected void setDiscreteModel( DiscreteModel model ) {
        if( discreteModel != null ) {
            getModel().removeModelElement( discreteModel );
        }
        discreteModel = model;
        addModelElement( discreteModel );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void reset() {
        clearPotential();
        discreteModel.reset();
        schrodingerPanel.reset();
    }

    public void fireParticle( WaveSetup waveSetup ) {
        discreteModel.fireParticle( waveSetup );
        schrodingerPanel.updateGraphics();
    }

    public void setGridSpacing( final int nx, final int ny ) {
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                discreteModel.setGridSpacing( nx, ny );
                getModel().removeModelElement( this );
            }
        } );
    }

    public void addDetector() {
        Detector detector = new Detector( getDiscreteModel(), 5, 5, 10, 10 );
        addDetector( detector );
    }

    public void addDetector( Detector detector ) {
        discreteModel.addDetector( detector );
        schrodingerPanel.addDetectorGraphic( detector );
    }

    public void addPotential() {
        RectangularPotential rectangularPotential = new RectangularPotential( 5, 20, 10, 10 );
        rectangularPotential.setPotential( Double.MAX_VALUE / 100.0 );
        discreteModel.addPotential( rectangularPotential );//todo should be a composite.
        RectangularPotentialGraphic rectangularPotentialGraphic = new RectangularPotentialGraphic( getSchrodingerPanel(), rectangularPotential );
        getSchrodingerPanel().addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    public SchrodingerControlPanel getSchrodingerControlPanel() {
        return schrodingerControlPanel;
    }

    public IntensityDisplay getIntensityDisplay() {
        return getSchrodingerPanel().getIntensityDisplay();
    }

    protected void setSchrodingerPanel( SchrodingerPanel schrodingerPanel ) {
        setApparatusPanel( schrodingerPanel );
        this.schrodingerPanel = schrodingerPanel;
    }

    protected void setSchrodingerControlPanel( SchrodingerControlPanel schrodingerControlPanel ) {
        setControlPanel( schrodingerControlPanel );
        this.schrodingerControlPanel = schrodingerControlPanel;
    }

    public PhetFrame getPhetFrame() {
        return schrodingerApplication.getPhetFrame();
    }

    public void removePotential( RectangularPotentialGraphic rectangularPotentialGraphic ) {
        getDiscreteModel().removePotential( rectangularPotentialGraphic.getPotential() );
        getSchrodingerPanel().removeGraphic( rectangularPotentialGraphic );
    }

    public void clearPotential() {
        getDiscreteModel().clearPotential();
        getSchrodingerPanel().clearPotential();
    }
}
