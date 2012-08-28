// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.functions.buildafunction.AbstractFunctionsCanvas;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class ChallengeProgressionCanvas extends AbstractFunctionsCanvas {
    public static final long ANIMATION_DELAY = 200;
    protected final ResetAllButtonNode resetAllButtonNode;
    protected final HTMLImageButtonNode nextButton;
    public final IntegerProperty level = new IntegerProperty( 0 );

    private static final boolean dev = true;

    public ChallengeProgressionCanvas() {
        setBackground( BuildAFunctionCanvas.BACKGROUND_COLOR );
        resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
            }
        }, this, new PhetFont( 18, true ), Color.black, Color.orange ) {{
            setOffset( 1024 - this.getFullWidth() - INSET - INSET - INSET, 768 - this.getFullHeight() - INSET - 40 - 40 - 10 - INSET );
        }};
        addScreenChild( resetAllButtonNode );


        nextButton = new HTMLImageButtonNode( "Next" ) {{
            setFont( resetAllButtonNode.getFont() );
            setBackground( Color.green );
            setOffset( resetAllButtonNode.getFullBounds().getX(), resetAllButtonNode.getFullBounds().getMinY() - getFullBounds().getHeight() - 10 );
        }};
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                level.increment();
                Integer level = ChallengeProgressionCanvas.this.level.get();
                if ( level >= Scenes.scenes.length() ) {
                    level = Scenes.scenes.length() - 1;
                }
                nextButtonPressed();
            }
        } );
        addChild( nextButton );
        if ( !dev ) {
            nextButton.setVisible( false );
        }
    }

    protected abstract void nextButtonPressed();

    public void showNextButton() {
        nextButton.setVisible( true );
    }

    protected void animateToNewScene( final PNode newScene ) {
        if ( !dev ) {
            nextButton.setVisible( false );
        }
        addChild( newScene );
        newScene.setOffset( STAGE_SIZE.width, 0 );
        newScene.animateToPositionScaleRotation( 0, 0, 1, 0, ANIMATION_DELAY );
        finishAnimation( newScene );
    }

    protected abstract void finishAnimation( final PNode newScene );
}