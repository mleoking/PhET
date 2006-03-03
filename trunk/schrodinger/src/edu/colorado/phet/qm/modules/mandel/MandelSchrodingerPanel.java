/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:03:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelSchrodingerPanel extends HighIntensitySchrodingerPanel {
    private MandelModule mandelModule;

    public MandelSchrodingerPanel( MandelModule mandelModule ) {
        super( mandelModule );
        this.mandelModule = mandelModule;
    }

    protected SchrodingerScreenNode createSchrodingerScreenNode( SchrodingerModule module ) {
        return new MandelSchrodingerScreenNode( module, this );
    }

    protected void doAddGunControlPanel() {
//don't  super.doAddGunControlPanel(), please
    }

    protected MandelModule getMandelModule() {
        return mandelModule;
    }

    protected HighIntensityGunGraphic createGun() {
        return new MandelGunSet( this );
    }

    protected boolean useGunChooserGraphic() {
        return false;
    }

    public MandelGun getLeftGun() {
        return getGunSet().getLeftGun();
    }

    public MandelGun getRightGun() {
        return getGunSet().getRightGun();
    }

    private MandelGunSet getGunSet() {
        return (MandelGunSet)getGunGraphic();
    }

    public void setSplitGraphics() {
        getWavefunctionGraphic().setColorMap( new MandelSplitColorMap( getMandelModule() ) );
    }

    public void setNormalGraphics() {
        super.setNormalGraphics();
    }

    public void wavelengthChanged() {
        getWavefunctionGraphic().setColorMap( new MandelSplitColorMap( getMandelModule() ) );
    }

    public MandelGunSet getMandelGunSet() {
        return getGunSet();
    }
}
