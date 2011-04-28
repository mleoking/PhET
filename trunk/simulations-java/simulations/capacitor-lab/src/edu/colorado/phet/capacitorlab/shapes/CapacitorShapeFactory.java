// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
import edu.colorado.phet.common.phetcommon.view.util.ShapeUtils;

/**
 * Creates 2D projections of shapes that are related to the 3D capacitor model.
 * All of these shapes are 2D projections of pseudo-3D boxes.
 * These shapes are subtracted using constructive area geometry to account for
 * occlusion that occurs in our pseudo-3D view.
 * Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorShapeFactory {

    private final Capacitor capacitor;
    private final BoxShapeFactory boxShapeFactory;

    public CapacitorShapeFactory( Capacitor capacitor, CLModelViewTransform3D mvt ) {
        this.capacitor = capacitor;
        this.boxShapeFactory = new BoxShapeFactory( mvt );
    }

    //----------------------------------------------------------------------------------------
    // unoccluded shapes
    //----------------------------------------------------------------------------------------

    // Top plate
    private Shape createTopPlateShape() {
        return createBoxShape( capacitor.getX(), capacitor.getTopPlateCenter().getY(), capacitor.getZ(), capacitor.getPlateSize() );
    }

    /**
     * Bottom plate, unoccluded.
     *
     * @return
     */
    public Shape createBottomPlateShape() {
        return createBoxShape( capacitor.getX(), capacitor.getY() + ( capacitor.getPlateSeparation() / 2 ), capacitor.getZ(), capacitor.getPlateSize() );
    }

    // Dielectric
    private Shape createDielectricShape() {
        return createBoxShape( capacitor.getX() + capacitor.getDielectricOffset(),
                               capacitor.getY() - ( capacitor.getDielectricSize().getHeight() / 2 ), capacitor.getZ(), capacitor.getDielectricSize() );
    }

    // Volume between the capacitor plates
    private Shape createBetweenPlatesShape() {
        return createBoxShape( capacitor.getX(), capacitor.getY() - ( capacitor.getPlateSeparation() / 2 ), capacitor.getZ(), capacitor.getDielectricSize() );
    }

    // Portion of the dielectric that is between the capacitor plates
    private Shape createDielectricBetweenPlatesShape() {
        if ( capacitor.getDielectricOffset() >= capacitor.getPlateWidth() ) {
            return createEmptyShape();
        }
        else {
            return ShapeUtils.intersect( createDielectricShape(), createBetweenPlatesShape() );
        }
    }

    // Air that is between the capacitor plates
    private Shape createAirBetweenPlateShape() {
        if ( capacitor.getDielectricOffset() == 0 ) {
            return createEmptyShape();
        }
        else {
            return ShapeUtils.subtract( createBetweenPlatesShape(), createDielectricBetweenPlatesShape() );
        }
    }

    //----------------------------------------------------------------------------------------
    // occluded shapes
    //----------------------------------------------------------------------------------------

    /**
     * Visible portion of the top plate.
     * Nothing occludes the top plate.
     *
     * @return
     */
    public Shape createTopPlateShapeOccluded() {
        return createTopPlateShape();
    }

    /**
     * Visible portion of the bottom plate.
     * May be partially occluded by the top plate.
     *
     * @return
     */
    public Shape createBottomPlateShapeOccluded() {
        return ShapeUtils.subtract( createBottomPlateShape(), createTopPlateShape() );
    }

    /**
     * Visible portion of the dielectric between the plates.
     * May be partially occluded by the top plate.
     *
     * @return
     */
    public Shape createDielectricBetweenPlatesShapeOccluded() {
        return ShapeUtils.subtract( createDielectricBetweenPlatesShape(), createTopPlateShape() );
    }

    /**
     * Visible portion of air between the plates.
     * May be partially occluded by the top plate.
     *
     * @return
     */
    public Shape createAirBetweenPlatesShapeOccluded() {
        return ShapeUtils.subtract( createAirBetweenPlateShape(), createTopPlateShape() );
    }

    //----------------------------------------------------------------------------------------
    // general shapes
    //----------------------------------------------------------------------------------------

    // A box, relative to a specific origin.
    private Shape createBoxShape( double x, double y, double z, Dimension3D size ) {
        return boxShapeFactory.createBoxShape( x, y, z, size.getWidth(), size.getHeight(), size.getDepth() );
    }

    // Encapsulation of empty shape.
    private Shape createEmptyShape() {
        return new Rectangle2D.Double( 0, 0, 0, 0 );
    }
}
