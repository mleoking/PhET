// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

/**
 * @author John Blanco
 */
public class MysteryMass extends LabeledImageMass {

    // This data structure defines the valid configurations for mystery objects.
    private static final List<LabeledImageMassConfig> MYSTERY_MASS_CONFIGURATIONS = new ArrayList<LabeledImageMassConfig>() {{
        // Note that the height value needs to be somewhat coordinated with the
        // image for things to look good.
        add( new LabeledImageMassConfig( 20.0, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_01, 0.25, BalanceAndTorqueResources.Strings.MASS_LABEL_A, true ) );
        add( new LabeledImageMassConfig( 5.0, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_02, 0.30, BalanceAndTorqueResources.Strings.MASS_LABEL_B, true ) );
        add( new LabeledImageMassConfig( 15.0, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_03, 0.35, BalanceAndTorqueResources.Strings.MASS_LABEL_C, true ) );
        add( new LabeledImageMassConfig( 10.0, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_04, 0.40, BalanceAndTorqueResources.Strings.MASS_LABEL_D, true ) );
        add( new LabeledImageMassConfig( 2.5, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_05, 0.25, BalanceAndTorqueResources.Strings.MASS_LABEL_E, true ) );
        add( new LabeledImageMassConfig( 52.5, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_06, 0.35, BalanceAndTorqueResources.Strings.MASS_LABEL_F, true ) );
        add( new LabeledImageMassConfig( 25, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_07, 0.40, BalanceAndTorqueResources.Strings.MASS_LABEL_G, true ) );
        add( new LabeledImageMassConfig( 7.5, BalanceAndTorqueResources.Images.MYSTERY_OBJECT_08, 0.30, BalanceAndTorqueResources.Strings.MASS_LABEL_H, true ) );
    }};

    // Data structure that tracks instances of each configuration, used for
    // creating unique labels for sim sharing.
    private static int[] instanceCounts = new int[MYSTERY_MASS_CONFIGURATIONS.size()];

    // ID for this instance, retained for copy purposes.
    private int myMysteryMassID;

    /**
     * Constructor.
     *
     * @param mysteryMassID
     */
    public MysteryMass( int mysteryMassID ) {
        this( mysteryMassID, new Point2D.Double( 0, 0 ) );
    }

    /**
     * Constructor.
     *
     * @param mysteryMassID
     * @param initialPosition
     */
    public MysteryMass( int mysteryMassID, Point2D initialPosition ) {
        super( createMysteryMassUserComponent( mysteryMassID ), initialPosition, MYSTERY_MASS_CONFIGURATIONS.get( mysteryMassID ) );
        if ( mysteryMassID >= 0 && mysteryMassID < instanceCounts.length ) {
            instanceCounts[mysteryMassID]++;
        }
        myMysteryMassID = mysteryMassID;
    }

    @Override public Mass createCopy() {
        return new MysteryMass( myMysteryMassID, getPosition() );
    }

    private static IUserComponent createMysteryMassUserComponent( int mysteryMassID ) {

        // Bounds checking.
        if ( mysteryMassID >= instanceCounts.length || mysteryMassID < 0 ) {
            return new UserComponent( "mysteryObjectWithInvalidID" );
        }

        // Create the label based on the label and the instance count.
        return new UserComponentChain( BalanceAndTorqueSimSharing.UserComponents.mysteryMass,
                                       new UserComponent( MYSTERY_MASS_CONFIGURATIONS.get( mysteryMassID ).labelText ),
                                       new UserComponent( instanceCounts[mysteryMassID] ) );
    }
}
