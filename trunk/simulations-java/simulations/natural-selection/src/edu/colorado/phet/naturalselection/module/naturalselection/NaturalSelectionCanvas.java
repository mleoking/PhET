/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.*;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Frenzy;
import edu.colorado.phet.naturalselection.view.AddFriendNode;
import edu.colorado.phet.naturalselection.view.FrenzyNode;
import edu.colorado.phet.naturalselection.view.NaturalSelectionBackgroundNode;
import edu.colorado.phet.naturalselection.view.SpritesNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Holds the main simulation area's canvas.
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionCanvas extends PhetPCanvas {

    /**
     * The pixel-level from the top of the "horizon", where the 3d bunny positions would appear if inifinitely far away
     */
    public static final double HORIZON = 120.0;

    private NaturalSelectionModel model;

    private PNode rootNode;

    /**
     * Holds the piccolo node that has all of the sprites (bunnies, trees, shrubs, wolves, etc.)
     */
    public SpritesNode bunnies;

    /**
     * The background node (background images, part of the environment)
     */
    public NaturalSelectionBackgroundNode backgroundNode;


    private AddFriendNode addFriendNode;

    private FrenzyNode frenzyNode;

    /**
     * Constructor
     *
     * @param model The natural selection model
     */
    public NaturalSelectionCanvas( NaturalSelectionModel model ) {

        super( NaturalSelectionDefaults.VIEW_SIZE );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        setBorder( null );

        rootNode = new PNode();
        addWorldChild( rootNode );

        backgroundNode = new NaturalSelectionBackgroundNode( this.model.getClimate() );
        rootNode.addChild( backgroundNode );

        bunnies = new SpritesNode();
        rootNode.addChild( bunnies );

        addFriendNode = new AddFriendNode( model );
        addFriendNode.setOffset( 75, 250 );
        rootNode.addChild( addFriendNode );

        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_FRENZY_START ) {
                    Frenzy frenzy = event.getFrenzy();

                    showFrenzy( 4.0 );

                    NaturalSelectionCanvas.this.setCursor( Toolkit.getDefaultToolkit().createCustomCursor( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_CROSSHAIR ), new Point( 25, 25 ), "NaturalSelectionCrosshair" ) );

                    frenzy.addListener( new Frenzy.Listener() {
                        public void onFrenzyStop( Frenzy frenzy ) {
                            hideFrenzy();
                            NaturalSelectionCanvas.this.setCursor( null );
                        }

                        public void onFrenzyTimeLeft( double timeLeft ) {
                            frenzyNode.setTimeLeft( timeLeft );
                        }
                    } );
                }
            }
        } );

    }

    public void reset() {
        bunnies.reset();
        backgroundNode.reset();
        addFriendNode.setVisible( true );
    }

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( NaturalSelectionConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "NaturalSelectionCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }

    public void showFrenzy( double time ) {
        hideFrenzy();
        frenzyNode = new FrenzyNode( model, time );
        frenzyNode.setOffset( ( NaturalSelectionDefaults.VIEW_SIZE.getWidth() - frenzyNode.getPlacementWidth() ) / 2, 10 );
        rootNode.addChild( frenzyNode );
    }

    public void hideFrenzy() {
        if ( frenzyNode != null ) {
            rootNode.removeChild( frenzyNode );
        }

        frenzyNode = null;
    }
}
