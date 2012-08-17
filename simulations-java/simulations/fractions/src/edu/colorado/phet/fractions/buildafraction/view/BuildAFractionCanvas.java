// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.numbers.NumberSceneNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.CompositeDelegate;
import edu.colorado.phet.fractions.buildafraction.view.shapes.ShapeSceneNode;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

/**
 * Canvas for the build a fraction tab.  Shows the level selection screen or a particular level.  Provides animation between different scenes.
 *
 * @author Sam Reid
 */
public class BuildAFractionCanvas extends AbstractFractionsCanvas implements LevelSelectionContext, SceneContext {

    private static final int rgb = 240;
    public static final Color CONTROL_PANEL_BACKGROUND = new Color( rgb, rgb, rgb );

    public static final Paint TRANSPARENT = new Color( 0, 0, 0, 0 );
    public static final Stroke controlPanelStroke = new BasicStroke( 2 );

    private PNode currentScene;
    private final BuildAFractionModel model;

    public BuildAFractionCanvas( final BuildAFractionModel model, String title ) {
        this.model = model;
        //Set a really light blue because there is a lot of white everywhere
        setBackground( new Color( 236, 251, 251 ) );

        currentScene = new LevelSelectionNode( title, this, model.audioEnabled, model.selectedPage, model.gameProgress );
        addChild( currentScene );
    }

    public static enum Direction {
        RIGHT,
        LEFT,
        DOWN
    }

    private void crossFadeTo( final PNode newNode ) {
        newNode.setTransparency( 0 );
        addChild( newNode );
        final PNode oldNode = currentScene;

        //REVIEW: Make the 500 a constant if it should be the same in both places.
        PActivity activity = oldNode.animateToTransparency( 0, 500 );
        activity.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                oldNode.removeFromParent();
                newNode.animateToTransparency( 1, 500 );
            }
        } );
        currentScene = newNode;
    }

    private void animateTo( final PNode node, Direction direction ) {
        node.setTransparency( 1 );
        Vector2D nodeOffset = direction == Direction.RIGHT ? new Vector2D( STAGE_SIZE.width, 0 ) :
                              direction == Direction.LEFT ? new Vector2D( -STAGE_SIZE.width, 0 ) :
                              direction == Direction.DOWN ? new Vector2D( 0, STAGE_SIZE.height ) :
                              Vector2D.ZERO;
        node.setOffset( nodeOffset.toPoint2D() );
        addChild( node );

        final PNode oldNode = currentScene;

        Vector2D oldNodeOffset = direction == Direction.RIGHT ? new Vector2D( -STAGE_SIZE.width, 0 ) :
                                 direction == Direction.LEFT ? new Vector2D( STAGE_SIZE.width, 0 ) :
                                 direction == Direction.DOWN ? new Vector2D( 0, -STAGE_SIZE.height ) :
                                 Vector2D.ZERO;

        PActivity activity = currentScene.animateToPositionScaleRotation( oldNodeOffset.x, oldNodeOffset.y, 1, 0, 400 );
        activity.setDelegate( new CompositeDelegate( new PActivityDelegate() {
            //REVIEW: I've seen this in a couple of places.  Why not create a PActivityDelegateAdapter?  Might even want it in common code, since I've created on before too.
            public void activityStarted( final PActivity activity ) {
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {

                PInterpolatingActivity fade = oldNode.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
                fade.setDelegate( new PActivityDelegate() {
                    //REVIEW: Again, the adapter would be good here.  Should find all usages of PActivityDelegate and replace.
                    public void activityStarted( final PActivity activity ) {
                    }

                    public void activityStepped( final PActivity activity ) {
                    }

                    public void activityFinished( final PActivity activity ) {
                        oldNode.removeFromParent();
                    }
                } );
            }
        }, new DisablePickingWhileAnimating( currentScene, true ) ) );
        node.animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
        currentScene = node;
    }

    //REVIEW: Suggest more descriptive name.
    private final HashMap<LevelIdentifier, PNode> map = new HashMap<LevelIdentifier, PNode>();

    public void levelButtonPressed( final LevelInfo info ) {

        //if level was in progress, go back to it.  Otherwise create a new one and cache it.
        animateTo( levelNode( info.levelIdentifier ), Direction.RIGHT );
    }

    private PNode levelNode( final LevelIdentifier level ) {
        if ( !map.containsKey( level ) ) {
            map.put( level, createLevelNode( level.levelIndex, level.levelType ) );
        }
        return map.get( level );
    }

    public void reset() {
        model.resetAll();
        map.clear();
        crossFadeTo( new LevelSelectionNode( Strings.BUILD_A_FRACTION, this, model.audioEnabled, model.selectedPage, model.gameProgress ) );
    }

    public Component getComponent() {
        return this;
    }

    private PNode createLevelNode( final int levelIndex, final LevelType levelType ) {
        return levelType == LevelType.SHAPES ?
               new ShapeSceneNode( levelIndex, model, STAGE_SIZE, this, model.audioEnabled ) :
               new NumberSceneNode( levelIndex, rootNode, model, STAGE_SIZE, this, model.audioEnabled );
    }

    //REVIEW: Suggest rename to "goToShapeLevel".  The 'next' seems to be handled where this is called.
    public void goToNextShapeLevel( final int newLevelIndex ) {
        final PNode newShapeScene = createLevelNode( newLevelIndex, LevelType.SHAPES );
        map.put( new LevelIdentifier( newLevelIndex, LevelType.SHAPES ), newShapeScene );
        animateTo( newShapeScene, Direction.RIGHT );
    }

    //REVIEW: Suggest rename to "goToNumberLevel".  The 'next' seems to be handled where this is called.
    public void goToNextNumberLevel( final int newLevelIndex ) {
        PNode newScene = createLevelNode( newLevelIndex, LevelType.NUMBERS );
        map.put( new LevelIdentifier( newLevelIndex, LevelType.NUMBERS ), newScene );
        animateTo( newScene, Direction.RIGHT );
    }

    public void goToLevelSelectionScreen( final int fromLevelIndex ) {
        model.selectedPage.set( fromLevelIndex < 5 ? 0 : 1 );
        animateTo( new LevelSelectionNode( Strings.BUILD_A_FRACTION, this, model.audioEnabled, model.selectedPage, model.gameProgress ), Direction.LEFT );
    }

    public void resampleShapeLevel( final int levelIndex ) {
        model.resampleShapeLevel( levelIndex );
        final PNode newNode = createLevelNode( levelIndex, LevelType.SHAPES );
        map.put( new LevelIdentifier( levelIndex, LevelType.SHAPES ), newNode );
        crossFadeTo( newNode );
    }

    public void resampleNumberLevel( final int levelIndex ) {
        model.resampleNumberLevel( levelIndex );
        final PNode newNode = createLevelNode( levelIndex, LevelType.NUMBERS );
        map.put( new LevelIdentifier( levelIndex, LevelType.NUMBERS ), newNode );
        crossFadeTo( newNode );
    }
}