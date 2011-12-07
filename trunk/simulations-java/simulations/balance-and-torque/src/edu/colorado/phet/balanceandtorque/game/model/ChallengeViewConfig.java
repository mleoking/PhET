// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

/**
 * This class contains information about how an individual balance challenge
 * should be presented to the user.  It is generally contained by a challenge
 * and used by the view when displaying that challenge.
 *
 * @author John Blanco
 */
public class ChallengeViewConfig {
    public final String title;
    public final boolean showMassEntryDialog;
    public final boolean showTipPredictionSelector;

    public ChallengeViewConfig( String title, boolean showMassEntryDialog, boolean showTipPredictionSelector ) {
        this.title = title;
        this.showMassEntryDialog = showMassEntryDialog;
        this.showTipPredictionSelector = showTipPredictionSelector;
    }
}
