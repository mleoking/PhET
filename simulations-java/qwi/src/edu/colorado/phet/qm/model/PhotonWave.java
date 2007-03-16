/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.model.waves.FlatDampedWave;
import edu.colorado.phet.qm.model.waves.PlaneWave;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 5:56:06 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class PhotonWave {
    private QWIModule module;
    private QWIModel QWIModel;
    private CylinderSource waveSource;
    private double momentum = 2.0 / 10.0 * Math.PI;
    private double dPhase = Math.PI / 6;
    private double waveMagnitude = 0.075;
    private double phase = 0.0;
    private double intensity = 0.0;//todo initial value
    private double intensityScale = 0.75;
    private int waveSourceHeight = 2;
    private ModelElement phaseUpdate;
    private QWIModel.Adapter waveSourceBoundaryConditionSetter;

    public PhotonWave( QWIModule module, QWIModel QWIModel ) {
        this.module = module;
        this.QWIModel = QWIModel;
        this.waveSource = new CylinderSource( createRectRegionForCylinder(), createWave( phase ) );
        phaseUpdate = new ModelElement() {
            public void stepInTime( double dt ) {
                updatePhase();
            }
        };
        waveSourceBoundaryConditionSetter = new QWIModel.Adapter() {
            public void beforeTimeStep( QWIModel QWIModel ) {
                initializeEntrantWave();
            }
        };
        QWIModel.addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                updateWaveSource();
            }
        } );
    }

    protected void initializeEntrantWave() {
//        waveSource.initializeEntrantWave( discreteModel);
        waveSource.initializeEntrantWave( QWIModel.getWaveModel(), QWIModel.getSimulationTime() );
        waveSource.initializeEntrantWave( QWIModel.getSourceWaveModel(), QWIModel.getSimulationTime() );
    }

    public CylinderSource getWaveSource() {
        return waveSource;
    }

    public void setMomentum( double momentum ) {
        this.momentum = momentum;
        dPhase = Math.abs( momentum );
        updateWaveSource();
    }

    public void setOff() {
        clearElements();
    }

    private void clearElements() {
        while( getDiscreteModel().containsListener( waveSourceBoundaryConditionSetter ) ) {
            getDiscreteModel().removeListener( waveSourceBoundaryConditionSetter );
        }
        while( module.getModel().containsModelElement( phaseUpdate ) ) {
            module.getModel().removeModelElement( phaseUpdate );
        }
    }

    public void setOn() {
        clearElements();
        waveSource.setRegion( createRectRegionForCylinder() );
        getDiscreteModel().addListener( waveSourceBoundaryConditionSetter );
        module.getModel().addModelElement( phaseUpdate );
    }

    private void updatePhase() {
        phase += dPhase;
        updateWaveSource();
    }

    protected void updateWaveSource() {
        waveSource.setWave( createWave( phase ) );
    }

    protected Wave createWave( double phase ) {
        final PlaneWave planeWave = new PlaneWave( momentum, getDiscreteModel().getGridWidth() );
        planeWave.setPhase( phase );
        planeWave.setMagnitude( waveMagnitude );
//        double dxLattice = 7 * getDiscreteModel().getGridWidth() / 100.0 * 1.2;
        double dxLattice = 7 * getDiscreteModel().getGridWidth() / 100.0 * 1.3;//increase by 30%
//        double dxLattice = 7 * getDiscreteModel().getGridWidth() / 100.0 * 2;
        return new FlatDampedWave( planeWave, intensity * intensityScale, getDiscreteModel().getGridWidth(), dxLattice );
    }

    protected Rectangle createRectRegionForCylinder() {
        return new Rectangle( 0, getWavefunction().getHeight() - waveSourceHeight,
                              getWavefunction().getWidth(), waveSourceHeight );
    }

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    public QWIModel getDiscreteModel() {
        return QWIModel;
    }

    public void setIntensity( double intensity ) {
        this.intensity = intensity;
    }

    public double getMomentum() {
        return momentum;
    }

    public double getMagnitude() {
        return waveMagnitude;
    }

    public double getTotalWaveMagnitude() {
        return waveMagnitude * intensity * intensityScale;
    }

    public double getIntensityScale() {
        return intensityScale;
    }

    public double getPhase() {
        return phase;
    }
}
