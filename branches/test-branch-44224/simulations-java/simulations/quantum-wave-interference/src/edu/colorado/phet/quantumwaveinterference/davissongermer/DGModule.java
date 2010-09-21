/*  */
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.quantumwaveinterference.QWIOptionsMenu;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.potentials.RectangularPotential;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.quantumwaveinterference.modules.intensity.IntensityModule;
import edu.colorado.phet.quantumwaveinterference.view.gun.AbstractGunNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.ImagePotentialGraphic;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.RectangularPotentialGraphic;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.WavefunctionGraphic;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Feb 4, 2006
 * Time: 10:50:45 PM
 */

public class DGModule extends IntensityModule {
    private Protractor protractor;
    private DGModel dgModel = new DGModel( getQWIModel() );
    private DGPlotFrame dgPlotFrame;

    /**
     * @param schrodingerApplication
     */
    public DGModule( PhetApplication schrodingerApplication, IClock clock ) {
        super( QWIResources.getString( "davisson-germer.name" ), schrodingerApplication, clock );
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
        getSchrodingerPanel().getWavefunctionGraphic().addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateProtractor();
            }
        } );
        getQWIModel().setBarrierAbsorptive( false );
        updatePotentialGraphics();
    }

//    PPath node = null;

    private void updateProtractor() {
        WavefunctionGraphic wavefunctionGraphic = getSchrodingerPanel().getWavefunctionGraphic();
//        double y0 = dgModel.getFractionalAtomLattice().getY0();
//        if( node == null ) {
//            node = new PPath( wavefunctionGraphic.getFullBounds() );
//            node.setStrokePaint( Color.green );
//            getSchrodingerPanel().addScreenChild( node );
//        }
//        else {
//            node.setPathTo( wavefunctionGraphic.getFullBounds() );
//        }
        Point center = dgModel.getCenterAtomPoint();

//        protractor.setOffset( wavefunctionGraphic.getFullBounds().getX() + wavefunctionGraphic.getColorGrid().getCellWidth() * wavefunctionGraphic.getWavefunction().getWidth() / 2 - wavefunctionGraphic.getColorGrid().getCellWidth() * 0.5,
//                              y0 * wavefunctionGraphic.getFullBounds().getHeight() + wavefunctionGraphic.getFullBounds().getY() - wavefunctionGraphic.getColorGrid().getCellHeight() * 0.5 );
        double protractorOffsetX = 0;
        double protractorOffsetY = 0;
        if( dgModel.getConcreteAtomLattice().getPotentials().length > 0 ) {
            protractorOffsetX = dgModel.getConcreteAtomLattice().getPotentials()[0].getDiameter() % 2 == 1 ? 0.5 : 0;
            protractorOffsetY = dgModel.getConcreteAtomLattice().getPotentials()[0].getDiameter() % 2 == 1 ? 0.5 : 0;
        }
//        System.out.println( "protractorOffsetX = " + protractorOffsetX );
        protractor.setOffset( wavefunctionGraphic.getFullBounds().getX() + wavefunctionGraphic.getColorGrid().getCellWidth() * ( center.x + protractorOffsetX ),
                              wavefunctionGraphic.getFullBounds().getY() + wavefunctionGraphic.getColorGrid().getCellHeight() * ( center.y + protractorOffsetY ) );
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
        AtomPotential[] atomPotentials = dgModel.getConcreteAtomLattice().getPotentials();
        for( int i = 0; i < atomPotentials.length; i++ ) {
            AtomPotential atomPotential = atomPotentials[i];
            addAtomPotentialGraphic( atomPotential );
        }
    }

    public void addAtomPotentialGraphic( AtomPotential atomPotential ) {
        int offsetX = 0;
        int offsetY = 0;
        if( atomPotential.getDiameter() % 2 == 1 ) {//todo discover the cause of this off-by-one-half-the-time error.
            offsetX = 1;
            offsetY = 1;
        }
        RectangularPotential rectangularPotential = new RectangularPotential( getQWIModel(),
                                                                              (int)( atomPotential.getCenter().getX() - atomPotential.getDiameter() / 2.0 ) + offsetX,
                                                                              (int)( atomPotential.getCenter().getY() - atomPotential.getDiameter() / 2.0 ) + offsetY,
                                                                              atomPotential.getDiameter(), atomPotential.getDiameter() );
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

        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                updateProtractorCenter();
            }
        } );
        updateProtractorCenter();
    }

    private void updateProtractorCenter() {
        updateProtractor();
//        protractor.setCenterGridPoint( dgModel.getCenterAtomPoint() );
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

//    public double getVelocityRealUnits() {
//        return getDGGun().getVelocityRealUnits();
//    }

    private DGGun getDGGun() {
        return getDGSchrodingerPanel().getDGGunGraphic();
    }

    public double getVelocityRealUnits() {
        return getDGParticle().getVelocity() * 100;
    }

    public double getSpacing() {
        return dgModel.getFractionalSpacing() * dgModel.getWavefunction().getWidth() / 10.0;
    }
}
