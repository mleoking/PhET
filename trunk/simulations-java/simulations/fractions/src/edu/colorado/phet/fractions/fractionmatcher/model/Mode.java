// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.model;

/**
 * Mode that a game can be in.
 *
 * @author Sam Reid
 */
public enum Mode {

    //User is selecting a level and whether timer is enabled, etc.
    CHOOSING_SETTINGS,

    //User is moving objects
    USER_IS_MOVING_OBJECTS_TO_THE_SCALES,

    //User checked if their answer is correct
    USER_CHECKED_CORRECT_ANSWER,

    //System is showing why the user's answer is wrong.
    SHOWING_WHY_ANSWER_WRONG,

    //System is showing the correct answer after the user guessed wrong
    SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS,

    //After getting it wrong, hide the "check answer" button until they remove something from the platform or put something else up there.
    WAITING_FOR_USER_TO_CHANGE_ANSWER,

    //System is showing the game over screen since a game just finished.
    SHOWING_GAME_OVER_SCREEN
}