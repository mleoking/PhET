/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.RampObject;
import edu.colorado.phet.theramp.view.bars.BarGraphSuite;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:01:59 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampPanel extends PhetPCanvas {
    private RampModule module;
    private RampLookAndFeel rampLookAndFeel;
    private BarGraphSuite barGraphSuite;
    private TimeGraphic timeGraphic;
    private SpeedReadoutGraphic velocityGraphic;
    public RampWorld rampWorld;

    public Dimension getDefaultRenderingSize() {
        return new Dimension( 1061, 871 );
    }

    public RampPanel( RampModule module ) {
        super();
        this.module = module;
        rampLookAndFeel = new RampLookAndFeel();

//        addChildsSetup( new BasicGraphicsSetup() );
        setBackground( new Color( 240, 200, 255 ) );

        rampWorld = new RampWorld( this, module, this );
        double rampWorldScale = 1.0;
        rampWorld.scale( rampWorldScale );
        rampWorld.translate( 0, -30 );
        addChild( rampWorld );

        barGraphSuite = new BarGraphSuite( this, module.getRampModel() );
        addChild( new OverheatButton( this, module.getRampModel(), barGraphSuite.getMaxDisplayableEnergy() ) );

//        barGraphSet.scale( 0.93, 0.93 );
        barGraphSuite.scale( 0.82 );
        barGraphSuite.setOffset( getDefaultRenderingSize().width - barGraphSuite.getWidth() - 1, barGraphSuite.getY() );

        addChild( barGraphSuite );

        //todo piccolo
//        KeyListener listener = new PanZoomKeyListener( this, getDefaultRenderingSize() );
//        addKeyListener( listener );

//        addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                setReferenceSize( getDefaultRenderingSize() );//my special little rendering size.//TODO add this method to AP2
//                requestFocus();
//            }
//        } );
//        removePanelResizeHandler();

        timeGraphic = new TimeGraphic( this, module.getTimeSeriesModel() );
        timeGraphic.setOffset( 60, 60 );
        addChild( timeGraphic );
        module.getModel().addModelElement( timeGraphic );

        velocityGraphic = new SpeedReadoutGraphic( this, module.getRampModel() );
        velocityGraphic.setOffset( timeGraphic.getX(), timeGraphic.getY() + timeGraphic.getHeight() + 20 );
        addChild( velocityGraphic );
        module.getModel().addModelElement( velocityGraphic );

        requestFocus();
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );

        addMouseListener( new UserAddingEnergyHandler( module ) );

//        setPanEventHandler();
        addInputEventListener( getPanEventHandler() );
        addInputEventListener( getZoomEventHandler() );

        setDebugRegionManagement( true );
    }

    private void addChild( PNode pNode ) {
        addGraphic( pNode );
    }

    private void updateArrowSetGraphics() {
        rampWorld.updateArrowSetGraphics();
    }


    public RampModule getRampModule() {
        return module;
    }

    public RampLookAndFeel getLookAndFeel() {
        return rampLookAndFeel;
    }

    public BlockGraphic getBlockGraphic() {
        return rampWorld.getBlockGraphic();
    }

    public void setCartesianArrowsVisible( boolean selected ) {
        rampWorld.setCartesianArrowsVisible( selected );
    }

    public void setParallelArrowsVisible( boolean selected ) {
        rampWorld.setParallelArrowsVisible( selected );
    }

    public void setPerpendicularArrowsVisible( boolean selected ) {
        rampWorld.setPerpendicularArrowsVisible( selected );
    }

    public void setXArrowsVisible( boolean selected ) {
        rampWorld.setXArrowsVisible( selected );
    }

    public void setYArrowsVisible( boolean selected ) {
        rampWorld.setYArrowsVisible( selected );
    }

    public boolean isCartesianVisible() {
        return rampWorld.isCartesianVisible();
    }

    public boolean isParallelVisible() {
        return rampWorld.isParallelVisible();
    }

    public boolean isPerpendicularVisible() {
        return rampWorld.isPerpendicularVisible();
    }

    public boolean isXVisible() {
        return rampWorld.isXVisible();
    }

    public boolean isYVisible() {
        return rampWorld.isYVisible();
    }

    public void setForceVisible( String force, boolean selected ) {
        rampWorld.setForceVisible( force, selected );
    }

    public SurfaceGraphic getRampGraphic() {
        return rampWorld.getRampGraphic();
    }

    public double getBlockWidthModel() {
        return rampWorld.getBlockWidthModel();
    }

    public double getModelWidth( int viewWidth ) {
        return rampWorld.getModelWidth( viewWidth );
    }

    public void setObject( RampObject rampObject ) {
        getBlockGraphic().setObject( rampObject );
    }

    public int getRampBaseY() {
        Point v = getRampGraphic().getViewLocation( getRampGraphic().getSurface().getLocation( 0 ) );
        return v.y;
    }

    public RampWorld getRampWorld() {
        return rampWorld;
    }

    public void setMeasureTapeVisible( boolean visible ) {
        rampWorld.setMeasureTapeVisible( visible );
    }

    public void updateGraphics() {
        updateArrowSetGraphics();
    }

    public void setEnergyBarsVisible( boolean selected ) {
        barGraphSuite.setEnergyBarsVisible( selected );
    }

    public void setWorkBarsVisible( boolean selected ) {
        barGraphSuite.setWorkBarsVisible( selected );
    }
}
