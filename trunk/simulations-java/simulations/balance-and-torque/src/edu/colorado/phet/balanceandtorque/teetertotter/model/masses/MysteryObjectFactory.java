// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.balanceandtorque.teetertotter.model.LabeledImageMass;

/**
 * This class creates mystery objects.  A mystery object is an mass that the
 * user does not know the mass of.
 *
 * @author John Blanco
 */
public class MysteryObjectFactory {

    // This data structure defines the valid configurations for mystery objects.
    private static final List<MysteryObjectConfig> MYSTERY_OBJECT_CONFIGURATIONS = new ArrayList<MysteryObjectConfig>() {{
        // Note that the height value needs to be somewhat coordinated with
        // the image for things to look good.
        // TODO: i18n of all labels (not sure how this will work with single character labels).
        add( new MysteryObjectConfig( 20.0, Images.MYSTERY_OBJECT_01, 0.25, "A" ) );
        add( new MysteryObjectConfig( 5.0, Images.MYSTERY_OBJECT_02, 0.30, "B" ) );
        add( new MysteryObjectConfig( 15.0, Images.MYSTERY_OBJECT_03, 0.35, "C" ) );
        add( new MysteryObjectConfig( 10.0, Images.MYSTERY_OBJECT_04, 0.40, "D" ) );
        add( new MysteryObjectConfig( 2.5, Images.MYSTERY_OBJECT_05, 0.25, "E" ) );
        add( new MysteryObjectConfig( 52.5, Images.MYSTERY_OBJECT_06, 0.35, "F" ) );
        add( new MysteryObjectConfig( 25, Images.MYSTERY_OBJECT_07, 0.40, "G" ) );
        add( new MysteryObjectConfig( 7.5, Images.MYSTERY_OBJECT_08, 0.30, "H" ) );
    }};

    /**
     * Create a mystery object of the specified type at the default location.
     *
     * @param mysteryObjectID
     * @return
     */
    public static LabeledImageMass createLabeledMysteryObject( int mysteryObjectID ) {
        return createLabeledMysteryObject( mysteryObjectID, new Point2D.Double( 0, 0 ) );
    }

    /**
     * Create a mystery object based on the provided configuration ID.
     *
     * @param mysteryObjectID
     * @param initialLocation
     * @return
     */
    public static LabeledImageMass createLabeledMysteryObject( int mysteryObjectID, Point2D initialLocation ) {
        assert ( mysteryObjectID < MYSTERY_OBJECT_CONFIGURATIONS.size() );
        MysteryObjectConfig config = MYSTERY_OBJECT_CONFIGURATIONS.get( mysteryObjectID );
        return new LabeledImageMass( config.mass, config.image, config.height, initialLocation, config.labelText, true );
    }

    /**
     * Create a mystery object with no label.
     *
     * @param mysteryObjectID
     * @return
     */
    public static ImageMass createUnlabeledMysteryObject( int mysteryObjectID ) {
        return createUnlabeledMysteryObject( mysteryObjectID, new Point2D.Double( 0, 0 ) );
    }

    /**
     * Create a mystery object with no label.
     *
     * @param mysteryObjectID
     * @param initialLocation
     * @return
     */
    public static ImageMass createUnlabeledMysteryObject( int mysteryObjectID, Point2D initialLocation ) {
        assert ( mysteryObjectID < MYSTERY_OBJECT_CONFIGURATIONS.size() );
        MysteryObjectConfig config = MYSTERY_OBJECT_CONFIGURATIONS.get( mysteryObjectID );
        return new ImageMass( config.mass, config.image, config.height, initialLocation, true );
    }

    // Collection of information needed to define a particular configuration
    // of mystery object.
    private static class MysteryObjectConfig {
        private final double mass;            // In kg
        private final BufferedImage image;    // Image to use when depicting this object.
        private final double height;          // In model space, which is in meters
        private final String labelText;

        private MysteryObjectConfig( double mass, BufferedImage image, double height, String labelText ) {
            this.mass = mass;
            this.image = image;
            this.height = height;
            this.labelText = labelText;
        }
    }
}
