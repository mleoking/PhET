// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;


import java.text.NumberFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all problem views that present a single fill-in-the-blank
 * type of question on the right side (e.g. What is the charge?).
 *
 * @author John Blanco
 */
public abstract class ToQuestionView extends ProblemView {

    private final EntryPanel question;
    private final Property<Integer> guessProperty;

    /**
     * Constructor.
     */
    public ToQuestionView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem,
                           String questionText, int minValue, int maxValue, NumberFormat numberFormat ) {

        super( model, gameCanvas, problem );

        guessProperty = new Property<Integer>( 0 );
        guessProperty.addObserver( new SimpleObserver() {
            public void update() {
                // Any change to the property indicates that the user has
                // entered something, so therefore it is time to enable the
                // "Check Guess" button.
                enableCheckButton();
            }
        }, false );

        question = new EntryPanel( questionText, guessProperty, minValue, maxValue, numberFormat );
        question.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width * 3 / 4 - question.getFullBounds().getWidth() / 2,
                            BuildAnAtomDefaults.STAGE_SIZE.height / 2 - question.getFullBounds().getHeight() / 2 );
    }

    @Override
    public void init() {
        super.init();
        addChild( question );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( question );
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        question.setPickable( guessEditable );
        question.setChildrenPickable( guessEditable );
    }

    protected EntryPanel getQuestion() {
        return question;
    }

    protected Property<Integer> getGuessProperty() {
        return guessProperty;
    }
}
