package edu.colorado.phet.fractionsintro.matchinggame.model;

/**
 * @author Sam Reid
 */
public enum Mode {
    WAITING_FOR_USER_TO_CHECK_ANSWER, USER_CHECKED_CORRECT_ANSWER, SHOWING_WHY_ANSWER_WRONG, SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS,

    //After getting it wrong, hide the "check answer" button until they remove something from the platform or put something else up there.
    WAITING_FOR_USER_TO_CHANGE_ANSWER,
}