package edu.colorado.phet.fractionmatcher.model;

import lombok.Data;

/**
 * The user's last wrong answer is stored for purposes of making sure they change the answer before pressing "try again"
 *
 * @author Sam Reid
 */
public @Data class Answer {
    public final MovableFractionID leftFraction, rightFraction;
}