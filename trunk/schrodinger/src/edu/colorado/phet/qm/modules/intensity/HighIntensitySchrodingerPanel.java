/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.intensity;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.SplitColorMap;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.ContinuousDisplay;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class HighIntensitySchrodingerPanel extends QWIPanel {

    private QWIModule intensityModule;
    private ContinuousDisplay continuousDisplay;
    private SplitColorMap splitColorMap;
    private HighIntensityGunGraphic highIntensityGun;
    private boolean splitMode = false;
    private boolean continuousMode = false;

    public static final boolean CONTINUOUS_MODE_DEFAULT = true;

    public HighIntensitySchrodingerPanel( QWIModule intensityModule ) {
        super( intensityModule );
        this.intensityModule = intensityModule;
        highIntensityGun = createGun();
        setGunGraphic( highIntensityGun );
        addGunChooserGraphic();

        doAddGunControlPanel();

        getIntensityDisplay().setHighIntensityMode();

        setNormalGraphics();
        continuousDisplay = new ContinuousDisplay( this, getIntensityDisplay() );
        setContinuousMode( CONTINUOUS_MODE_DEFAULT );
        if( intensityModule instanceof IntensityModule ) {//todo fix this.
            splitColorMap = new SplitColorMap( ( (IntensityModule)intensityModule ).getSplitModel(), this );
        }
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

    protected void updateWavefunctionColorMap() {
        super.updateWavefunctionColorMap();
        if( splitColorMap != null ) {
            splitColorMap.setWaveValueAccessor( getWaveValueAccessorForSplit() );
        }
    }

    public void setVisualizationStyle( ComplexColorMap colorMap, WaveValueAccessor waveValueAccessor ) {
        super.setVisualizationStyle( colorMap, waveValueAccessor );
        if( splitMode ) {
            setSplitMode( splitMode );
        }
//        setSplitMode( splitMode );
    }

    private WaveValueAccessor getWaveValueAccessorForSplit() {
        Photon photon = getPhoton();
        if( photon != null ) {
            if( getWaveValueAccessor() instanceof WaveValueAccessor.Imag ) {
                return new WaveValueAccessor() {
                    public double getValue( Wavefunction wavefunction, int i, int j ) {
                        return 0;
                    }
                };
            }
            else {
//                return new WaveValueAccessor.Real();
                return getWaveValueAccessor();
            }
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
            PSwing pSwing = new PSwing( this, highIntensityGun.getComboBox() );
            highIntensityGun.getComboBox().setEnvironment( pSwing, this );
            getSchrodingerScreenNode().setGunTypeChooserGraphic( pSwing );
        }
    }

    public HighIntensityGunGraphic getHighIntensityGun() {
        return highIntensityGun;
    }

    protected boolean useGunChooserGraphic() {
        return true;
    }

    protected HighIntensityGunGraphic createGun() {
        return new HighIntensityGunGraphic( this );
    }

    public void setSplitMode( boolean splitMode ) {
        this.splitMode = splitMode;
        if( splitMode ) {
            setSplitGraphics();
        }
        else {
            setNormalGraphics();
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

    public void setSplitGraphics() {
        getWavefunctionGraphic().setColorMap( splitColorMap );
    }

    public void setNormalGraphics() {
        setVisualizationStyle( getComplexColorMap(), getWaveValueAccessor() );
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
        return highIntensityGun.getRootColor();
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        highIntensityGun.setOn( highIntensityGun.isOn() );
    }

    public boolean isSplitMode() {
        return splitMode;
    }
}
