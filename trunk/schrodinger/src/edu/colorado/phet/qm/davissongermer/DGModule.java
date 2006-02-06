/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:50:45 PM
 * Copyright (c) Feb 4, 2006 by Sam Reid
 */

public class DGModule extends IntensityModule {
    private Protractor protractor;
    private DGModel dgModel = new DGModel( getDiscreteModel() );
    private DGPlotFrame dgPlotFrame;

    /**
     * @param schrodingerApplication
     */
    public DGModule( PhetApplication schrodingerApplication, IClock clock ) {
        super( "Davisson-Germer Experiment", schrodingerApplication, clock );
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
    }

    private void updateProtractor() {
        WavefunctionGraphic wavefunctionGraphic = getSchrodingerPanel().getWavefunctionGraphic();
        protractor.setOffset( wavefunctionGraphic.getFullBounds().getCenterX(), wavefunctionGraphic.getFullBounds().getCenterY() );
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

    protected HighIntensitySchrodingerPanel createIntensityPanel() {
        return new DGSchrodingerPanel( this );
    }

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
}
