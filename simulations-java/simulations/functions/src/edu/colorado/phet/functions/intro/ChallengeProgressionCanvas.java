// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.intro;

import fj.data.List;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.CenteredStageCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.controlpanel.SettingsOnOffPanel;
import edu.colorado.phet.common.piccolophet.nodes.controlpanel.SettingsOnOffPanel.Feature;
import edu.colorado.phet.functions.buildafunction.BuildAFunctionCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.games.GameConstants.SOUND_ICON;
import static edu.colorado.phet.common.games.GameConstants.SOUND_OFF_ICON;

/**
 * @author Sam Reid
 */
public abstract class ChallengeProgressionCanvas extends CenteredStageCanvas {

    public static final double INSET = 10; //Default inset between edges, etc.
    public static final long ANIMATION_DELAY = 200;

    protected final ResetAllButtonNode resetAllButtonNode;
    protected final HTMLImageButtonNode nextButton;
    public final IntegerProperty level = new IntegerProperty( 0 );

    private static final boolean dev = false;

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
                if ( level.get() < getLevelCount() - 1 ) { level.increment(); }
            }
        } );
        addChild( nextButton );
        if ( !dev ) {
            nextButton.setVisible( false );
        }

        //Add the audio on/off panel
        addChild( new SettingsOnOffPanel( List.list( new Feature( new PImage( SOUND_OFF_ICON ),
                                                                  new PImage( SOUND_ICON ), new BooleanProperty( true ), null ) ) ) {{
            setOffset( getStageSize().getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }} );
    }

    protected abstract int getLevelCount();

    public void showNextButton() {
        nextButton.setVisible( true );
    }

    protected void animateToNewScene( final PNode newScene ) {
        if ( !dev ) {
            nextButton.setVisible( false );
        }
        addChild( newScene );
        newScene.setOffset( getStageSize().getWidth(), 0 );
        newScene.animateToPositionScaleRotation( 0, 0, 1, 0, ANIMATION_DELAY );
        finishAnimation( newScene );
    }

    protected abstract void finishAnimation( final PNode newScene );
}