/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.RampObject;
import edu.colorado.phet.theramp.common.MeasuringTape;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.Surface;
import edu.colorado.phet.theramp.view.arrows.*;
import edu.colorado.phet.theramp.view.panzoom.PanZoomKeyListener;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:01:59 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampPanel extends ApparatusPanel2 {
    private RampModule module;
    private SurfaceGraphic rampGraphic;
    private BlockGraphic blockGraphic;
    private BarGraphSet barGraphSet;
    private RampLookAndFeel rampLookAndFeel;
    private AbstractArrowSet cartesian;
    private AbstractArrowSet perp;
    private AbstractArrowSet parallel;
    private XArrowSet xArrowSet;
    private YArrowSet yArrowSet;
    private ArrayList arrowSets = new ArrayList();
    private PotentialEnergyZeroGraphic potentialEnergyZeroGraphic;
    private LeanerGraphic leanerGraphic;
    private EarthGraphic earthGraphic;
    private SkyGraphic skyGraphic;
    private SurfaceGraphic groundGraphic;
    private MeasuringTape measuringTape;
    private TimeGraphic timeGraphic;
    private RightBarrierGraphic rightBarrierGraphic;
    private LeftBarrierGraphic leftBarrierGraphic;
    private SpeedReadoutGraphic velocityGraphic;

    public Dimension getDefaultRenderingSize() {
        return new Dimension( 1061, 871 );
    }

    public RampPanel( RampModule module ) {
        super( module.getClock() );
        rampLookAndFeel = new RampLookAndFeel();
        addGraphicsSetup( new BasicGraphicsSetup() );
        this.module = module;
        setBackground( new Color( 240, 200, 255 ) );
        RampModel rampModel = module.getRampModel();
        Surface ramp = rampModel.getRamp();
        rampGraphic = new RampGraphic( this, ramp );
        addGraphic( rampGraphic );

        groundGraphic = new FloorGraphic( this, rampModel.getGround() );
        groundGraphic.setIgnoreMouse( true );
        addGraphic( groundGraphic );

        blockGraphic = new BlockGraphic( module, this, rampGraphic, groundGraphic, rampModel.getBlock(), module.getRampObjects()[0] );
        addGraphic( blockGraphic, 2 );

        barGraphSet = new BarGraphSet( this, rampModel );
        addGraphic( barGraphSet );

        cartesian = new CartesianArrowSet( this );
        perp = new PerpendicularArrowSet( this );
        parallel = new ParallelArrowSet( this );
        xArrowSet = new XArrowSet( this );
        yArrowSet = new YArrowSet( this );
        addArrowSet( cartesian );
        addArrowSet( perp );
        addArrowSet( parallel );
        addArrowSet( xArrowSet );
        addArrowSet( yArrowSet );

        perp.setVisible( false );
        parallel.setVisible( false );
        xArrowSet.setVisible( false );
        yArrowSet.setVisible( false );

        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                for( int i = 0; i < arrowSets.size(); i++ ) {
                    AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
                    arrowSet.updateGraphics();
                }
            }
        } );

        KeyListener listener = new PanZoomKeyListener( this, getDefaultRenderingSize() );
        addKeyListener( listener );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
//                System.out.println( "e = " + e );
                Rectangle b = getBounds();
//                System.out.println( "b = " + b );
                setReferenceSize( getDefaultRenderingSize() );//my special little rendering size.//TODO add this method to AP2
                requestFocus();
            }
        } );
        removePanelResizeHandler();

        potentialEnergyZeroGraphic = new PotentialEnergyZeroGraphic( this, rampModel );
        addGraphic( potentialEnergyZeroGraphic, 100 );

        try {
            leanerGraphic = new LeanerGraphic( this, blockGraphic.getObjectGraphic() );
            addGraphic( leanerGraphic, 102 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        earthGraphic = new EarthGraphic( this );
        addGraphic( earthGraphic, -1 );

        skyGraphic = new SkyGraphic( this );
        addGraphic( skyGraphic, -2 );
        requestFocus();
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );

        addMouseListener( new UserAddingEnergyHandler( module ) );
        measuringTape = new MeasuringTape( this, rampGraphic.getScreenTransform(),
                                           RectangleUtils.getCenter2D( rampGraphic.getScreenTransform().getModelBounds() ) );
        addGraphic( measuringTape, 100 );

        timeGraphic = new TimeGraphic( this, module.getTimeSeriesModel() );
        timeGraphic.setLocation( 60, 60 );
        addGraphic( timeGraphic, 100 );
        module.getModel().addModelElement( timeGraphic );

        velocityGraphic = new SpeedReadoutGraphic( this, rampModel );
        velocityGraphic.setLocation( timeGraphic.getX(), timeGraphic.getY() + timeGraphic.getHeight() + 20 );
        addGraphic( velocityGraphic );
        module.getModel().addModelElement( velocityGraphic );

        rightBarrierGraphic = new RightBarrierGraphic( this, this, rampGraphic );
        addGraphic( rightBarrierGraphic, 1 );

        leftBarrierGraphic = new LeftBarrierGraphic( this, this, groundGraphic );
        addGraphic( leftBarrierGraphic, 1 );
    }

    private void addArrowSet( AbstractArrowSet arrowSet ) {
        addGraphic( arrowSet, 3 );
        arrowSets.add( arrowSet );
    }

    public RampModule getRampModule() {
        return module;
    }

    public RampLookAndFeel getLookAndFeel() {
        return rampLookAndFeel;
    }

    public BlockGraphic getBlockGraphic() {
        return blockGraphic;
    }

    public void setCartesianArrowsVisible( boolean selected ) {
        cartesian.setVisible( selected );
    }

    public void setParallelArrowsVisible( boolean selected ) {
        parallel.setVisible( selected );
    }

    public void setPerpendicularArrowsVisible( boolean selected ) {
        perp.setVisible( selected );
    }

    public void setXArrowsVisible( boolean selected ) {
        xArrowSet.setVisible( selected );
    }

    public void setYArrowsVisible( boolean selected ) {
        yArrowSet.setVisible( selected );
    }

    public boolean isCartesianVisible() {
        return cartesian.isVisible();
    }

    public boolean isParallelVisible() {
        return parallel.isVisible();
    }

    public boolean isPerpendicularVisible() {
        return perp.isVisible();
    }

    public boolean isXVisible() {
        return xArrowSet.isVisible();
    }

    public boolean isYVisible() {
        return yArrowSet.isVisible();
    }

    public void setForceVisible( String force, boolean selected ) {
        for( int i = 0; i < arrowSets.size(); i++ ) {
            AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
            arrowSet.setForceVisible( force, selected );
        }
    }

    public SurfaceGraphic getRampGraphic() {
        return rampGraphic;
    }

    public double getBlockWidthModel() {
        int widthView = blockGraphic.getObjectWidthView();
        double widthModel = rampGraphic.getScreenTransform().viewToModelDifferentialX( widthView );
        return widthModel;
    }

    public double getModelWidth( int viewWidth ) {
        return rampGraphic.getScreenTransform().viewToModelDifferentialX( viewWidth );
    }

    public void setObject( RampObject rampObject ) {
        getBlockGraphic().setObject( rampObject );
    }

    public int getRampBaseY() {
        Point v = getRampGraphic().getViewLocation( getRampGraphic().getSurface().getLocation( 0 ) );
        return v.y;
    }
}
