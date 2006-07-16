/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.qm.QWIOptionsMenu;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.view.gun.AbstractGunNode;
import edu.colorado.phet.qm.view.piccolo.ImagePotentialGraphic;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;
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
    private DGModel dgModel = new DGModel( getQWIModel() );
    private DGPlotFrame dgPlotFrame;

    /**
     * @param schrodingerApplication
     */
    public DGModule( PhetApplication schrodingerApplication, IClock clock ) {
        super( "Davisson-Germer Experiment", schrodingerApplication, clock );
        dgPlotFrame = new DGPlotFrame( getPhetFrame(), this );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                updatePotentialGraphics();
            }
        } );
        DGControlPanel intensityControlPanel = new DGControlPanel( this );
        setControlPanel( intensityControlPanel );

        setupProtractor();
        getSchrodingerPanel().getSchrodingerScreenNode().getDetectorSheetPNode().setVisible( false );

        DGParticle particle = getDGParticle();
        particle.addMomentumChangeListerner( new AbstractGunNode.MomentumChangeListener() {
            public void momentumChanged( double val ) {
                clearWave();
            }
        } );

        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                updateProtractor();
            }
        } );
        getQWIModel().addListener( new QWIModel.Adapter() {
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
        getQWIModel().setBarrierAbsorptive( false );
        updatePotentialGraphics();
    }

    private void updateProtractor() {
        WavefunctionGraphic wavefunctionGraphic = getSchrodingerPanel().getWavefunctionGraphic();
        double y0 = dgModel.getFractionalAtomLattice().getY0();

        protractor.setOffset( wavefunctionGraphic.getFullBounds().getCenterX(),
                              y0 * wavefunctionGraphic.getFullBounds().getHeight() + wavefunctionGraphic.getFullBounds().getY() );
//        wavefunctionGraphic.getFullBounds().getCenterY() );
    }

    private DGParticle getDGParticle() {
        AbstractGunNode gun = getSchrodingerPanel().getSchrodingerScreenNode().getGunGraphic();
        if( gun instanceof DGGun ) {
            DGGun dgGun = (DGGun)gun;
            return dgGun.getDgParticle();
        }
        else {
            return null;
        }
    }

    private void updatePotentialGraphics() {
        getSchrodingerPanel().clearPotential();
        AtomPotential[]atomPotentials = dgModel.getConcreteAtomLattice().getPotentials();
        for( int i = 0; i < atomPotentials.length; i++ ) {
            AtomPotential atomPotential = atomPotentials[i];
            addAtomPotentialGraphic( atomPotential );
        }
    }

    public void addAtomPotentialGraphic( AtomPotential atomPotential ) {
        RectangularPotential rectangularPotential = new RectangularPotential( getQWIModel(), (int)( atomPotential.getCenter().getX() - atomPotential.getRadius() ), (int)( atomPotential.getCenter().getY() - atomPotential.getRadius() ),
                                                                              (int)atomPotential.getRadius() * 2, (int)atomPotential.getRadius() * 2 );
        rectangularPotential.setPotential( Double.MAX_VALUE / 100.0 );
        RectangularPotentialGraphic rectangularPotentialGraphic = createPotentialGraphic( rectangularPotential );
        getSchrodingerPanel().addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    protected RectangularPotentialGraphic createPotentialGraphic( RectangularPotential rectangularPotential ) {
        return new ImagePotentialGraphic( getSchrodingerPanel(), rectangularPotential );
    }

    protected IntensityBeamPanel createIntensityPanel() {
        return new DGSchrodingerPanel( this );
    }

    private void setupProtractor() {
        protractor = new Protractor();
        protractor.setLeftLegPickable( false );
        protractor.setReadoutGraphicPickable( false );
        protractor.addListener( new Protractor.Listener() {
            public void angleChanged( Protractor protractor ) {
                getPlotPanel().setIndicatorAngle( protractor.getDegreesUnsigned() );//todo is sign correct?
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
        getQWIModel().clearWavefunction();
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

    protected QWIOptionsMenu createOptionsMenu() {
        return new DGOptionsMenu( this );
    }
}
