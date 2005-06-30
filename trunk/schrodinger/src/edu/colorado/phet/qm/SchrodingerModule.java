/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.DetectorGraphic;
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

    /**
     * @param clock
     */
    public SchrodingerModule( AbstractClock clock ) {
        super( "Schrodinger Module", clock );

        setModel( new BaseModel() );

        discreteModel = new DiscreteModel( 100, 100 );
        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                discreteModel.stepInTime( dt );
            }
        } );
        schrodingerPanel = new SchrodingerPanel( this );
        setApparatusPanel( schrodingerPanel );

        schrodingerControlPanel = new SchrodingerControlPanel( this );
        setControlPanel( schrodingerControlPanel );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public static void main( String[] args ) {
        PhetLookAndFeel.setLookAndFeel();
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        PhetApplication phetApplication = new PhetApplication( args, "Schrodinger Equation",
                                                               "Schrodinger Equation", "v0r0", clock,
                                                               true, new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 800, 600 ) ) );
        final SchrodingerModule module = new SchrodingerModule( clock );
        phetApplication.setModules( new Module[]{module} );
        phetApplication.startApplication();
    }

    public void reset() {
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
        Detector detector = new Detector( 5, 5, 10, 10 );
        discreteModel.addDetector( detector );
        DetectorGraphic detectorGraphic = new DetectorGraphic( getSchrodingerPanel(), detector );
        getSchrodingerPanel().addDetectorGraphic( detectorGraphic );
    }

    public void addPotential() {
        RectangularPotential rectangularPotential = new RectangularPotential( 5, 20, 10, 10 );
        rectangularPotential.setPotential( 10000 );
        discreteModel.addPotential( rectangularPotential );//todo should be a composite.
        RectangularPotentialGraphic rectangularPotentialGraphic = new RectangularPotentialGraphic( getSchrodingerPanel(), rectangularPotential );
        getSchrodingerPanel().addRectangularPotentialGraphic( rectangularPotentialGraphic );
    }

    public void setGunActive( boolean selected ) {
        if( selected ) {

        }
    }

    public SchrodingerControlPanel getSchrodingerControlPanel() {
        return schrodingerControlPanel;
    }

    public IntensityDisplay getIntensityDisplay() {
        return getSchrodingerPanel().getIntensityDisplay();
    }
}
