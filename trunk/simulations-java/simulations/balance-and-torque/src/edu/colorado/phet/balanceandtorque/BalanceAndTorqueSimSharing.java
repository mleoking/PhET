// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Class where all the user components are defined for items in the sim, which
 * is necessary for the "sim sharing" (a.k.a. data collection) feature.
 *
 * @author John Blanco
 */
public class BalanceAndTorqueSimSharing {

    // Sim sharing components that exist only in the view.
    public static enum UserComponents implements IUserComponent {

        // Tabs
        introTab, balanceLabTab, gameTab,

        // Check box controls
        massLabelsCheckBox, rulersCheckBox, forceFromObjectsCheckBox,
        levelCheckBox,

        // Movable masses
        singleBrick, stackOfTwoBricks, stackOfThreeBricks, stackOfFourBricks,
        statckOfEightBricks, mediumTrashCan, mediumBucket, tire, television,
        sodaBottle, smallRock, smallTrashCan, pottedPlant, tinyRock, flowerPot,
        cinderBlock, mediumRock, largeTrashCan, mysteryMass, barrel,
        fireExtinguisher, bigRock, largeBucket, fireHydrant, man, woman, boy,
        girl, smallBucket,

        // Buttons
        redXRemoveSupportsButton, addSupportsButton, removeSupportsButton,
        nextKitButton, previousKitButton, checkAnswer, tryAgain, displayAnswer,
        nextChallenge, tiltLeftButton, stayBalancedButton, tiltRightButton,

        // Kits
        massKitSelector
    }

    public static enum ModelComponents implements IUserComponent, IModelComponent {
        plank
    }

    public static enum UserActions implements IUserAction {
        createdMass, removedMass
    }

    public static enum ModelComponentTypes implements IModelComponentType {
        // Game challenge types.
        balanceMassesChallenge, massDeductionChallenge, tiltPredictionChallenge
    }

    public static enum ModelActions implements IModelAction {
        massAddedToPlank, massRemovedFromPlank, startedTilting, stoppedTilting,
        challengePresented, correctAnswerSubmitted, incorrectAnswerSubmitted,
        pointsScored
    }

    public static enum ParameterKeys implements IParameterKey {
        massValue, distanceFromPlankCenter, plankTiltAngle, massUserComponent,
        pointsEarnedKey
    }
}
