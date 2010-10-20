/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the game tab.
 */
public class GameCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final GameModel2 model;

    // View
    private final PNode rootNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GameCanvas( final GameModel2 model ) {

        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );


        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Background.
        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // TODO: Temp - Put a "TBD" indicator on the canvas.
        //        PText tbdIndicator = new PText( "TBD" );
        //        tbdIndicator.setFont( new PhetFont( 100, true ) );
        //        tbdIndicator.setTextPaint( new Color(0, 0, 0, 100) );
        //        tbdIndicator.setOffset( 380, 50 );
        //        rootNode.addChild( tbdIndicator );

        updateView( model.getState() );
        model.addListener(new GameModel2.GameModelListener() {
            public void stateChanged() {
                updateView( model.getState() );
            }
        } );

        // TODO: Temp - put a sketch of the tab up as a very early prototype.
        //        PImage image = new PImage( BuildAnAtomResources.getImage( "tab-2-sketch-01.png" ));
        //        image.scale( 0.75 );
        //        image.setOffset( 50, 0 );
        //        rootNode.addChild(image);
    }

    private void updateView( GameModel2.State state ) {
        if ( state instanceof GameModel2.GameSettingsState ) {
            final GameSettingsPanel panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
            final PNode gameSettingsNode = new PSwing( panel );
            panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
                @Override
                public void startButtonPressed() {
                    model.startGame();
                }
            } );
            gameSettingsNode.setOffset(
                    BuildAnAtomDefaults.STAGE_SIZE.width / 2 - gameSettingsNode.getFullBoundsReference().width / 2,
                    BuildAnAtomDefaults.STAGE_SIZE.height / 2 - gameSettingsNode.getFullBoundsReference().height / 2 );
            rootNode.addChild( gameSettingsNode );
        }else{
            rootNode.addChild( new PText("Hello") );
        }
    }


    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */

    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
