/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.common.MeasuringTape;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.model.Surface;
import edu.colorado.phet.theramp.view.arrows.*;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 2:56:14 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class RampWorld extends PNode {
    private ArrayList arrowSets = new ArrayList();
    private SurfaceGraphic rampGraphic;
    private BlockGraphic blockGraphic;
    private AbstractArrowSet cartesian;
    private AbstractArrowSet perp;
    private AbstractArrowSet parallel;
    private XArrowSet xArrowSet;
    private YArrowSet yArrowSet;
    private PotentialEnergyZeroGraphic potentialEnergyZeroGraphic;
    private PusherGraphic pusherGraphic;
    private EarthGraphic earthGraphic;
    private SkyGraphic skyGraphic;
    private FloorGraphic groundGraphic;
    private MeasuringTape measuringTape;
    private RightBarrierGraphic rightBarrierGraphic;
    private LeftBarrierGraphic leftBarrierGraphic;
    private RampModule module;

    public RampWorld( RampModule module, RampPanel rampPanel ) {
        super();
        this.module = module;
        RampPhysicalModel rampPhysicalModel = module.getRampPhysicalModel();
        Surface ramp = rampPhysicalModel.getRamp();
        rampGraphic = new RampGraphic( rampPanel, ramp );
        earthGraphic = new EarthGraphic( rampPanel, this );
        skyGraphic = new SkyGraphic( rampPanel, this );
        groundGraphic = new FloorGraphic( rampPanel, rampPhysicalModel.getGround() );
//        BoundGraphic groundBounds=new BoundGraphic( groundGraphic,2,2);
        blockGraphic = new BlockGraphic( module, rampPanel, rampGraphic, groundGraphic, rampPhysicalModel.getBlock(), module.getRampObjects()[0] );
        rightBarrierGraphic = new RightBarrierGraphic( rampPanel, rampPanel, rampGraphic );
        leftBarrierGraphic = new LeftBarrierGraphic( rampPanel, rampPanel, groundGraphic );


        addChild( skyGraphic );
        addChild( earthGraphic );
        addChild( rampGraphic );
        addChild( groundGraphic );
        addChild( leftBarrierGraphic );
        addChild( rightBarrierGraphic );
        addChild( blockGraphic );

        cartesian = new CartesianArrowSet( rampPanel, getBlockGraphic() );
        perp = new PerpendicularArrowSet( rampPanel, getBlockGraphic() );
        parallel = new ParallelArrowSet( rampPanel, getBlockGraphic() );
        xArrowSet = new XArrowSet( rampPanel, getBlockGraphic() );
        yArrowSet = new YArrowSet( rampPanel, getBlockGraphic() );
        addArrowSet( cartesian );
        addArrowSet( perp );
        addArrowSet( parallel );
        addArrowSet( xArrowSet );
        addArrowSet( yArrowSet );

        perp.setVisible( false );
        parallel.setVisible( false );
        xArrowSet.setVisible( false );
        yArrowSet.setVisible( false );

        potentialEnergyZeroGraphic = new PotentialEnergyZeroGraphic( rampPanel, rampPhysicalModel, this );
        addChild( potentialEnergyZeroGraphic );

        try {
            pusherGraphic = new PusherGraphic( rampPanel, blockGraphic.getObjectGraphic(), this );
            addChild( pusherGraphic );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        measuringTape = new MeasuringTape( rampPanel, rampGraphic.getScreenTransform(),
                                           RectangleUtils.getCenter2D( rampGraphic.getScreenTransform().getModelBounds() ) );
        measuringTape.setVisible( false );
        addChild( measuringTape );

        setPotentialEnergyZeroGraphicVisible( false );
//        groundBounds.setPaint( Color.blue);
//        addChild( groundBounds);
    }

    void updateArrowSetGraphics() {
        for( int i = 0; i < arrowSets.size(); i++ ) {
            AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
            arrowSet.updateGraphics();
        }
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
        return cartesian.getVisible();
    }

    public boolean isParallelVisible() {
        return parallel.getVisible();
    }

    public boolean isPerpendicularVisible() {
        return perp.getVisible();
    }

    public boolean isXVisible() {
        return xArrowSet.getVisible();
    }

    public boolean isYVisible() {
        return yArrowSet.getVisible();
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

    private void addArrowSet( AbstractArrowSet arrowSet ) {
        addChild( arrowSet );
        arrowSets.add( arrowSet );
    }

    public void setForceVisible( String force, boolean selected ) {
        for( int i = 0; i < arrowSets.size(); i++ ) {
            AbstractArrowSet arrowSet = (AbstractArrowSet)arrowSets.get( i );
            arrowSet.setForceVisible( force, selected );
        }
    }

    public int getRampBaseY() {
        Point v = getRampGraphic().getViewLocation( getRampGraphic().getSurface().getLocation( 0 ) );
        return v.y;
    }

    public Point convertToWorld( Point2D screenPt ) {
        AffineTransform affineTransform = getTransform();
        Point2D out = null;
        try {
            out = affineTransform.inverseTransform( screenPt, null );//todo ignores registration point.
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        return new Point( (int)out.getX(), (int)out.getY() );
    }

    public void setMeasureTapeVisible( boolean visible ) {
        //todo piccolo
        measuringTape.setVisible( visible );
    }

    public FloorGraphic getGroundGraphic() {
        return groundGraphic;
    }

    public LeftBarrierGraphic getLeftBarrierGraphic() {
        return leftBarrierGraphic;
    }

    public RightBarrierGraphic getRightBarrierGraphic() {
        return rightBarrierGraphic;
    }

    public SurfaceGraphic getSurfaceGraphic( double modelLocation ) {
        return getSurfaceGraphic( module.getRampPhysicalModel().getSurfaceGraphic( modelLocation ) );
    }

    private SurfaceGraphic getSurfaceGraphic( Surface surface ) {
        if( surface == groundGraphic.getSurface() ) {
            return groundGraphic;
        }
        else {
            return rampGraphic;
        }
    }

    public void setHeatColor( boolean heatColor ) {
        if( heatColor == this.heatColor ) {
            return;
        }
        else {
            this.heatColor = heatColor;
            if( !heatColor ) {
                restoreOriginalImages();
            }
            else {
                paintImagesRed();
            }
        }
    }

    private void paintImagesRed() {
        if( RampPanel.redRampEnabled ) {
            blockGraphic.paintRed();
            rampGraphic.paintRed();
        }
    }

    private void restoreOriginalImages() {
        blockGraphic.restoreOriginalImage();
        rampGraphic.restoreOriginalImage();
    }

    boolean heatColor = false;

    public EarthGraphic getEarthGraphic() {
        return earthGraphic;
    }

    public PotentialEnergyZeroGraphic getPotentialEnergyZeroGraphic() {
        return potentialEnergyZeroGraphic;
    }

    public void setPotentialEnergyZeroGraphicVisible( boolean visible ) {
        if( !visible && isPotentialEnergyZeroGraphicVisible() ) {
            removeChild( potentialEnergyZeroGraphic );
        }
        else if( visible && !isPotentialEnergyZeroGraphicVisible() ) {
            addChild( potentialEnergyZeroGraphic );
        }
    }

    public boolean isPotentialEnergyZeroGraphicVisible() {
        return getChildrenReference().contains( potentialEnergyZeroGraphic );
    }
}
