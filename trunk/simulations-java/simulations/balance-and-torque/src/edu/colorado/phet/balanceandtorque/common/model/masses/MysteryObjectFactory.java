// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

/**
 * This class creates mystery objects.  A mystery object is a mass that the
 * user does not know the mass of.
 *
 * @author John Blanco
 */
public class MysteryObjectFactory {

    // This data structure defines the valid configurations for mystery objects.
    private static final List<MysteryObjectConfig> MYSTERY_OBJECT_CONFIGURATIONS = new ArrayList<MysteryObjectConfig>() {{
        // Note that the height value needs to be somewhat coordinated with
        // the image for things to look good.
        add( new MysteryObjectConfig( 20.0, Images.MYSTERY_OBJECT_01, 0.25, BalanceAndTorqueResources.Strings.MASS_LABEL_A ) );
        add( new MysteryObjectConfig( 5.0, Images.MYSTERY_OBJECT_02, 0.30, BalanceAndTorqueResources.Strings.MASS_LABEL_B ) );
        add( new MysteryObjectConfig( 15.0, Images.MYSTERY_OBJECT_03, 0.35, BalanceAndTorqueResources.Strings.MASS_LABEL_C ) );
        add( new MysteryObjectConfig( 10.0, Images.MYSTERY_OBJECT_04, 0.40, BalanceAndTorqueResources.Strings.MASS_LABEL_D ) );
        add( new MysteryObjectConfig( 2.5, Images.MYSTERY_OBJECT_05, 0.25, BalanceAndTorqueResources.Strings.MASS_LABEL_E ) );
        add( new MysteryObjectConfig( 52.5, Images.MYSTERY_OBJECT_06, 0.35, BalanceAndTorqueResources.Strings.MASS_LABEL_F ) );
        add( new MysteryObjectConfig( 25, Images.MYSTERY_OBJECT_07, 0.40, BalanceAndTorqueResources.Strings.MASS_LABEL_G ) );
        add( new MysteryObjectConfig( 7.5, Images.MYSTERY_OBJECT_08, 0.30, BalanceAndTorqueResources.Strings.MASS_LABEL_H ) );
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
        return new LabeledImageMass( createMysteryObjectUserComponent( config.labelText ), initialLocation, config.mass, config.image, config.height, config.labelText, true );
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
        return new ImageMass( createMysteryObjectUserComponent( config.labelText ), config.mass, config.image, config.height, initialLocation, true );
    }

    public static int getNumAvailableMysteryObjects() {
        return MYSTERY_OBJECT_CONFIGURATIONS.size();
    }

    // For tracking instance counts for mystery objects with a given label.
    private static Map<String, Integer> labelToInstanceCountMap = new HashMap<String, Integer>();

    private static IUserComponent createMysteryObjectUserComponent( String labelText ) {
        if ( !labelToInstanceCountMap.containsKey( labelText ) ) {
            // Add initial entry for this label.
            labelToInstanceCountMap.put( labelText, 0 );
        }
        int instanceCount = labelToInstanceCountMap.get( labelText );
        labelToInstanceCountMap.put( labelText, instanceCount + 1 );
        return UserComponentChain.chain( UserComponentChain.chain( BalanceAndTorqueSimSharing.UserComponents.mysteryMass, labelText ), instanceCount );
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
