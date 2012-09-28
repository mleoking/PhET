// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.maketheequation.MTE_Challenge;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for "Graph the Line" (GTL) challenges that use slope-intercept (SI) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_SI_ChallengeNode extends MTE_ChallengeNode {

    //TODO: these should come from somewhere else
    protected static final Property<DoubleRange> RISE_RANGE = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );
    protected static final Property<DoubleRange> RUN_RANGE = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );
    protected static final Property<DoubleRange> Y_INTERCEPT_RANGE = new Property<DoubleRange>( new DoubleRange( -10, 10 ) );

    public MTE_SI_ChallengeNode( LineGameModel model, MTE_Challenge challenge, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override protected MTE_GraphNode createGraphNode( MTE_Challenge challenge ) {
        return new MTE_SI_GraphNode( challenge );
    }
}
