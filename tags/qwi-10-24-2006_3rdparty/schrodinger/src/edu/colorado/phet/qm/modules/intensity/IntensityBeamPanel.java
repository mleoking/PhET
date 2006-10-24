/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.gun.IntensityGunNode;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.ContinuousDisplay;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityBeamPanel extends QWIPanel {

    private QWIModule intensityModule;
    private ContinuousDisplay continuousDisplay;
    private SplitColorMap splitColorMap;
    private IntensityGunNode intensityGun;
    private boolean splitMode = false;
    private boolean continuousMode = false;

    public static final boolean CONTINUOUS_MODE_DEFAULT = true;

    public IntensityBeamPanel( QWIModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        intensityGun = createGun();
        setGunGraphic( intensityGun );
        addGunChooserGraphic();

        doAddGunControlPanel();

        getIntensityDisplay().setHighIntensityMode();


        continuousDisplay = new ContinuousDisplay( this, getIntensityDisplay() );
        setContinuousMode( CONTINUOUS_MODE_DEFAULT );
        if( intensityModule instanceof IntensityModule ) {//todo fix this.
            splitColorMap = new SplitColorMap( ( (IntensityModule)intensityModule ).getSplitModel(), this );
        }
        super.update();
        setPhoton( super.getDisplayPhotonColor() );
        getDetectorSheetPNode().getDetectorSheetControlPanel().setBrightness();
        intensityModule.addListener( new QWIModule.Listener() {
            public void deactivated() {
            }

            public void activated() {
            }

            public void beamTypeChanged() {
                updateWavefunctionColorMap();
                setSplitMode( splitMode );
            }
        } );
    }

    private WaveValueAccessor getWaveValueAccessorForSplit() {
        if( isPhotonMode() && getWaveValueAccessor() instanceof WaveValueAccessor.Imag ) {
            return new WaveValueAccessor.Empty();
        }
        else {
            return getWaveValueAccessor();
        }
    }

    protected void doAddGunControlPanel() {
        super.addGunControlPanel();
    }

    protected void addGunChooserGraphic() {
        if( useGunChooserGraphic() ) {
            PSwing pSwing = new PSwing( this, intensityGun.getComboBox() );
            intensityGun.getComboBox().setEnvironment( pSwing, this );
            getSchrodingerScreenNode().setGunTypeChooserGraphic( pSwing );
        }
    }

    public IntensityGunNode getHighIntensityGun() {
        return intensityGun;
    }

    protected boolean useGunChooserGraphic() {
        return true;
    }

    protected IntensityGunNode createGun() {
        return new IntensityGunNode( this );
    }

    public void setSplitMode( boolean splitMode ) {
        this.splitMode = splitMode;
        super.update();
    }

    protected ColorMap createColorMap() {
        if( splitColorMap != null ) {
            splitColorMap.setWaveValueAccessor( getWaveValueAccessorForSplit() );
        }
        if( splitMode ) {
            return splitColorMap;
        }
        else {
            return super.createColorMap();
        }
    }

    public void setContinuousMode( boolean continuousMode ) {
        if( continuousMode != this.continuousMode ) {
            this.continuousMode = continuousMode;
            getDetectorSheetPNode().clearScreen();
            if( continuousMode ) {
                getDetectorSheetPNode().setSaveButtonVisible( true );
            }
        }
    }

    protected void updateScreen() {
        if( continuousMode ) {
            continuousDisplay.updateValues();
        }
        else {
            getIntensityDisplay().tryDetecting();
        }
    }

    public boolean isContinuousMode() {
        return continuousMode;
    }

    public ContinuousDisplay getContinuousDisplay() {
        return continuousDisplay;
    }

    public void setPhoton( Photon photon ) {
        super.setPhoton( photon );
        if( splitColorMap != null ) {
            splitColorMap.setRootColor( photon == null ? null : new ColorData( photon.getWavelengthNM() ) );
        }
        if( continuousDisplay != null ) {
            continuousDisplay.setPhotonColor( photon == null ? null : photon.getRootColor() );
        }
    }

    public ColorData getRootColor() {
        return intensityGun.getRootColor();
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        intensityGun.setOn( intensityGun.isOn() );
    }

    public boolean isSplitMode() {
        return splitMode;
    }
}
