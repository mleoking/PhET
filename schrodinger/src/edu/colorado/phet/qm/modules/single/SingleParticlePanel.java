/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.gun.SingleParticleGun;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:13 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticlePanel extends SchrodingerPanel {
    private SingleParticleGun abstractGun;
//    private AutoFire autoFire;

    public SingleParticlePanel( SchrodingerModule module ) {
        super( module );
        abstractGun = new SingleParticleGun( this );
        setGunGraphic( abstractGun );
        getIntensityDisplay().setMultiplier( 1 );
        getIntensityDisplay().setProbabilityScaleFudgeFactor( 5 );
        getIntensityDisplay().setOpacity( 255 );
        getIntensityDisplay().setNormDecrement( 1.0 );

//        BasicGraphicsSetup setup = new BasicGraphicsSetup();
//        setup.setAntialias( false );
//        setup.setNearestNeighborInterpolation( );
//        setup.setRenderQuality( false );
//        addGraphicsSetup( setup );

//        addGraphicsSetup( new BasicGraphicsSetup() );
//        RepaintDebugGraphic.enable( this, module.getClock() );
    }

//    protected void paintComponent( Graphics graphics ) {
//        Graphics2D g2 = (Graphics2D)graphics;
//        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
//        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
////        System.out.println( "g2.getRenderingHint( RenderingHints.KEY_INTERPOLATION ) = " + g2.getRenderingHint( RenderingHints.KEY_INTERPOLATION ) );
////        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, null);
//        super.paintComponent( graphics );
//    }

    public void reset() {
        super.reset();
        abstractGun.reset();
    }

    public void clearWavefunction() {
        super.clearWavefunction();
        abstractGun.reset();
    }
}
