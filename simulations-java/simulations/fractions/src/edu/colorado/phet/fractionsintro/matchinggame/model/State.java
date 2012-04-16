package edu.colorado.phet.fractionsintro.matchinggame.model;

/**
 * @author Sam Reid
 */
public enum State {
    WAITING_FOR_USER_TO_CHECK_ANSWER, RIGHT, SHOWING_WHY_ANSWER_WRONG, SHOWING_CORRECT_ANSWER,

    //After getting it wrong, hide the "check answer" button until they remove something from the platform or put something else up there.
    WAITING_FOR_USER_TO_CHANGE_ANSWER,
}