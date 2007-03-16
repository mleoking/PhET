/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.gun.IntensityGunNode;
import edu.colorado.phet.qm.view.piccolo.QWIScreenNode;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:03:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelSchrodingerPanel extends IntensityBeamPanel {
    private MandelModule mandelModule;

    public MandelSchrodingerPanel( MandelModule mandelModule ) {
        super( mandelModule );
        this.mandelModule = mandelModule;
    }

    protected QWIScreenNode createSchrodingerScreenNode( QWIModule module ) {
        return new MandelSchrodingerScreenNode( (MandelModule)module, this );
    }

    protected void doAddGunControlPanel() {
//don't  super.doAddGunControlPanel(), please
    }

    protected MandelModule getMandelModule() {
        return mandelModule;
    }

    protected IntensityGunNode createGun() {
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

//    public void setSplitMode( boolean splitMode ) {
//        updateWavefunctionColorMap();
//    }

    public void wavelengthChanged() {
        getWavefunctionGraphic().setColorMap( new MandelSplitColorMap( getMandelModule(), createAccessor() ) );
        updateDetectorColors();
    }

    public void updateDetectorColors() {
//        ColorData leftColor=new ColorData( getLeftGun().getWavelength());
//        ColorData rightColor=new ColorData( getRightGun().getWavelength( ));
//        Color a=leftColor.toColor( getLeftGun().getIntensity()*20);
//        Color b=rightColor.toColor( getRightGun().getIntensity( )*20);
//        Color sum=MandelSplitColorMap.add( a,b);
//        VisibleColor visibleColor=new VisibleColor( sum );
//        double wav=visibleColor.getWavelength();
//
//
//        getDetectorSheetPNode().setDisplayPhotonColor( new ColorData( wav ) );
//        getSmoothIntensityDisplay().setPhotonColor( new ColorData( wav ) );

//        MandelSplitColorMap mandelSplitColorMap = new MandelSplitColorMap( getMandelModule() );
//        Color mix = (Color)mandelSplitColorMap.getColor( 10, 10 );
//        System.out.println( "mix = " + mix );
//        getSmoothIntensityDisplay().setPhotonColor( new ColorData( mix ) );

//        double avgWavelength = ( getLeftGun().getWavelength() * getLeftGun().getIntensity() + getRightGun().getWavelength() * getRightGun().getIntensity() ) / ( getLeftGun().getIntensity() + getRightGun().getIntensity() );
//        getDetectorSheetPNode().setDisplayPhotonColor( new ColorData( avgWavelength ) );
//        getSmoothIntensityDisplay().setPhotonColor( new ColorData( avgWavelength ) );

        VisibleColor leftColor = new VisibleColor( getLeftGun().getWavelength() );
        VisibleColor rightColor = new VisibleColor( getRightGun().getWavelength() );
//        Color mix = mix( leftColor, getLeftGun().getIntensity(), rightColor, getRightGun().getIntensity() );
        Color mix = Color.white;
        getContinuousDisplay().setPhotonColor( new ColorData( mix ) );
        getDetectorSheetPNode().setDisplayPhotonColor( new ColorData( mix ) );
    }

    public static Color mix( Color c1, double intensityA, Color c2, double intensityB ) {
        float r = (float)( ( c1.getRed() * intensityA + c2.getRed() * intensityB ) / 255.0 );
        float g = (float)( ( c1.getGreen() * intensityA + c2.getGreen() * intensityB ) / 255.0 );
        float b = (float)( ( c1.getBlue() * intensityA + c2.getBlue() * intensityB ) / 255.0 );
        return new Color( Math.min( r, 1 ), Math.min( g, 1 ), Math.min( b, 1 ) );
    }

    public MandelGunSet getMandelGunSet() {
        return getGunSet();
    }

    protected ColorMap createColorMap() {
        if( getMandelModule() == null || getMandelModule().getMandelModel() == null ) {
            return new PhotonColorMap( this, 0, createAccessor() );
        }
        if( getMandelModule().getMandelModel().isSplit() ) {
//            System.out.println( "MandelSchrodingerPanel.createColorMap: using mandelSplitColorMap" );
            return new MandelSplitColorMap( mandelModule, createAccessor() );
        }
        else {
//            System.out.println( "MandelSchrodingerPanel.createColorMap: using photonColormap (average)." );
            return new PhotonColorMap( this, ( getLeftGun().getWavelength() + getRightGun().getWavelength() ) / 2, createAccessor() );
        }
    }

    private WaveValueAccessor createAccessor() {
//        return new WaveValueAccessor.Real();//todo uncomment this to facilitate testing of wavelength
        return new WaveValueAccessor.Magnitude();
    }
}
