/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.SplitModel;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.DetectorGraphic;
import edu.colorado.phet.qm.view.piccolo.RestrictedDetectorGraphic;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:50:45 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class DGModule extends SchrodingerModule {
    private Protractor protractor;
    private DGModel dgModel;
    private DGPlotFrame dgPlotFrame;

    private DiscreteModel splitModel;
    private HighIntensitySchrodingerPanel highIntensitySchrodingerPanel;
    private DGControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();

    /**
     * @param schrodingerApplication
     */
    public DGModule( PhetApplication schrodingerApplication, IClock clock ) {
        super( "Davisson-Germer Experiment", schrodingerApplication, clock );
        splitModel = createDiscreteModel();
        setDiscreteModel( splitModel );
        highIntensitySchrodingerPanel = new DGSchrodingerPanel( this );
        setSchrodingerPanel( highIntensitySchrodingerPanel );
        intensityControlPanel = new DGControlPanel( this );
        setSchrodingerControlPanel( intensityControlPanel );
        synchronizeModel();
        finishInit();
        this.dgModel = new DGModel( getDiscreteModel() );
        dgPlotFrame = new DGPlotFrame( getPhetFrame(), this );
        DGControlPanel intensityControlPanel = new DGControlPanel( this );
        setControlPanel( intensityControlPanel );

        setupProtractor();
        getSchrodingerPanel().getSchrodingerScreenNode().getDetectorSheetPNode().setVisible( false );

        DGParticle particle = getDGParticle();
        particle.addMomentumChangeListerner( new AbstractGunGraphic.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                clearWave();
            }
        } );

        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                updateProtractor();
            }
        } );
        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void sizeChanged() {
                updateProtractor();
            }
        } );

        updateProtractor();
        getSchrodingerPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateProtractor();
            }
        } );
        getDiscreteModel().setBarrierAbsorptive( false );
    }

    private void updateProtractor() {
        WavefunctionGraphic wavefunctionGraphic = getSchrodingerPanel().getWavefunctionGraphic();
        double y0 = dgModel.getFractionalAtomLattice().getY0();

        protractor.setOffset( wavefunctionGraphic.getFullBounds().getCenterX(),
                              y0 * wavefunctionGraphic.getFullBounds().getHeight() + wavefunctionGraphic.getFullBounds().getY() );
//        wavefunctionGraphic.getFullBounds().getCenterY() );
    }

    private DGParticle getDGParticle() {
        AbstractGunGraphic gun = getSchrodingerPanel().getSchrodingerScreenNode().getGunGraphic();
        if( gun instanceof DGGun ) {
            DGGun dgGun = (DGGun)gun;
            return dgGun.getDgParticle();
        }
        else {
            return null;
        }
    }
//
//    protected HighIntensitySchrodingerPanel createIntensityPanel() {
//        return new DGSchrodingerPanel( this );
//    }

    private void setupProtractor() {
        protractor = new Protractor();
        protractor.setLeftLegPickable( false );
        protractor.setReadoutGraphicPickable( false );
        protractor.addListener( new Protractor.Listener() {
            public void angleChanged( Protractor protractor ) {
                getPlotPanel().setIndicatorAngle( protractor.getDegrees() );
            }

            public void visibilityChanged( Protractor protractor ) {
                getPlotPanel().setIndicatorVisible( protractor.getVisible() );
            }
        } );
        getSchrodingerPanel().getSchrodingerScreenNode().addChild( protractor );
        setProtractorVisible( false );
    }

    public boolean isProtractorVisible() {
        return protractor.getVisible();
    }

    public void setProtractorVisible( boolean visible ) {
        protractor.setVisible( visible );
    }

    public void clearWave() {
        getDiscreteModel().clearWavefunction();
    }

    public DGModel getDGModel() {
        return dgModel;
    }

    public DGPlotFrame getPlotFrame() {
        return dgPlotFrame;
    }

    public DGPlotPanel getPlotPanel() {
        return dgPlotFrame.getDgPlotPanel();
    }

    public boolean isAtomShapeCircular() {
        return dgModel.isAtomShapeCircular();
    }

    public boolean isAtomShapeSquare() {
        return dgModel.isAtomShapeSquare();
    }

    public void setAtomShapeCircular() {
        dgModel.setAtomShapeCircular();
    }

    public void setAtomShapeSquare() {
        dgModel.setAtomShapeSquare();
    }

    public DGPlotPanel getDGPlotPanel() {
        return getPlotPanel();
    }

    public DGSchrodingerPanel getDGSchrodingerPanel() {
        return (DGSchrodingerPanel)getSchrodingerPanel();
    }

    protected DiscreteModel createDiscreteModel() {
        return new SplitModel();
    }

    public boolean hasHelp() {
        return true;
    }

    public HighIntensitySchrodingerPanel getIntensityPanel() {
        return highIntensitySchrodingerPanel;
    }

    public ColorData getRootColor() {
        return highIntensitySchrodingerPanel.getRootColor();
    }

    private void setDetectorEnabled( Detector detector, boolean selected ) {
        boolean splitMode = shouldBeSplitMode();
        if( selected ) {
            DetectorGraphic detectorGraphic = new RestrictedDetectorGraphic( highIntensitySchrodingerPanel, detector );
            highIntensitySchrodingerPanel.addDetectorGraphic( detectorGraphic );
        }
        else {
            highIntensitySchrodingerPanel.removeDetectorGraphic( detector );
        }
        boolean newSplitMode = shouldBeSplitMode();
        if( newSplitMode != splitMode ) {
            synchronizeModel();
        }
    }

    private boolean shouldBeSplitMode() {
        return false;//todo fix this.
    }

    private void synchronizeModel() {
        boolean splitMode = shouldBeSplitMode();
//        splitModel.setSplitMode( splitMode );//todo fix this
        highIntensitySchrodingerPanel.setSplitMode( splitMode );
    }

}
