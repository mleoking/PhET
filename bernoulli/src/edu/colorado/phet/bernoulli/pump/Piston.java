package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.common.model.AutomatedObservable;
import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 10:08:12 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class Piston extends AutomatedObservable {
    PhetVector unitVector;
    PhetVector origin;
    double extension = .2;
    private double height;
    private double maxExtension;
    private Pump model;
    private double pistonWidth;
    private double x;


    public double getY() {
        return y;
    }

    private double y;

    public Piston( double x, double y, double height, double maxExtension, Pump model, double pistonWidth ) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.maxExtension = maxExtension;
        this.model = model;
        this.pistonWidth = pistonWidth;
        this.origin = new PhetVector( x, y );
        this.unitVector = new PhetVector( -1, 0 );
        unitVector.normalize();

        // Do this so piston is not placed improperly
        extension = pistonWidth;
    }

    public Rectangle2D.Double getHeadRectangle() {
        PhetVector ctr = getExtensionCenter();
        Rectangle2D.Double rect = new Rectangle2D.Double( ctr.getX(), ctr.getY(), 0, 0 );
        rect.add( ctr.getX() + pistonWidth, ctr.getY() + height / 2 );
        rect.add( ctr.getX() + pistonWidth, ctr.getY() - height / 2 );
        return rect;
    }

    public double getX() {
        return x;
    }

    public double getPistonWidth() {
        return pistonWidth;
    }

    public double getHeight() {
        return height;
    }

    public double getExtension() {
        return extension;
    }

    public void setExtension( double extension ) {
        if( extension < pistonWidth ) {
            extension = pistonWidth;
        }
        if( extension > maxExtension ) {
            extension = maxExtension;
        }
        //if this would burst the tank, don't go there.
        if( model.isIsolated() ) {
            double maxext = model.getFullyExtendedPistonPosition();
            if( extension > maxext ) {
                extension = maxext;
            }
        }
        else if( model.isReadyForUptake() ) {
            double de = extension - this.extension;
            if( de < 0 ) {         //Suck in some water.
//                de = -de;
//                double waterVolume = model.getTank().getWaterVolume();
//                if (!model.isFullOfWater()) {
//                    double newWater = de / 10.0 + waterVolume;
//                    double newWater = de *5 + waterVolume;
//                    if (newWater < model.getTank().getVolume())
//                        model.getTank().setWaterVolume(newWater);
//                    else
                model.getTank().fillTank();
//                }
            }
            else {//smaller available tank volume.
                double maxext = model.getFullyExtendedPistonPosition();
//                model.getTank().fillTank();
                if( extension > maxext ) {
                    extension = maxext;
                }
            }
        }
        else if( model.isReadyToExpel() ) {
            double de = extension - this.extension;
            if( de > 0 && model.isPrettyMuchFullOfWater() ) {//expel direction.
                this.extension = extension;
                double waterVolumeInit = model.getTank().getWaterVolume();
                updateObservers();
                model.getTank().fillTank();
                updateObservers();
                double waterVolumeFinal = model.getTank().getWaterVolume();
                double deltaVolume = waterVolumeFinal - waterVolumeInit;
                deltaVolume = Math.abs( deltaVolume );
                model.expelWater( volumeToDropRadius( deltaVolume ) );
                model.getTank().fillTank();
                return;
            }
            else {
                double maxext = model.getFullyExtendedPistonPosition();
//                model.getTank().fillTank();
                if( extension > maxext ) {
                    extension = maxext;
                }
            }
        }
        else //model is fully open.
        {
//            model.getTank().fillTank();
        }
        double de = extension - this.extension;
        if( de < 0 ) {
            model.getTank().fillTank();
        }

        this.extension = extension;
        updateObservers();
    }

    public static double volumeToDropRadius( double volume ) {
        return Math.sqrt( volume ) / Math.PI;
    }

    public PhetVector getUnitVector() {
        return unitVector;
    }

    public PhetVector getOrigin() {
        return origin;
    }

    public PhetVector getExtensionCenter() {
        return origin.getAddedInstance( unitVector.getScaledInstance( extension ) );
    }

    public void translate( double ext ) {
        double targetExt = ext + extension;
        setExtension( targetExt );
    }

    public double getMaxExtension() {
        return maxExtension;
    }

    public double getWidth() {
        return pistonWidth;
    }
}
