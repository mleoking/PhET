/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.model.Frenzy;
import edu.colorado.phet.naturalselection.model.Wolf;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Handles all of the sprites on the simulation canvas (bunnies, wolves, trees, shrubs, etc.)
 *
 * @author Jonathan Olson
 */
public class SpritesNode extends PNode implements NaturalSelectionModel.Listener {

    // display properties
    public static final double BUNNY_SIDE_SPACER = 25.0;

    // Bunny coordinate limits
    public static final double MIN_X = BUNNY_SIDE_SPACER;
    public static final double MAX_X = NaturalSelectionDefaults.VIEW_SIZE.getWidth() - BUNNY_SIDE_SPACER;
    public static final double MIN_Y = 0.0;
    public static final double MIN_Z = 1.0;
    public static final double MAX_Z = 2.5;

    /**
     * A list of the variable (bunny and wolf) sprites
     */
    private List<NaturalSelectionSprite> sprites;

    /**
     * Stores a copy for wolf nodes (not wolves)
     */
    private List<WolfNode> wolves;

    // trees and shrubs lists
    private List<TreeNode> trees;
    private List<ShrubNode> shrubs;

    // cached climate and selection factor
    private int oldClimate;
    private int oldSelection;

    /**
     * Constructor
     */
    public SpritesNode() {
        sprites = new LinkedList<NaturalSelectionSprite>();
        trees = new LinkedList<TreeNode>();
        shrubs = new LinkedList<ShrubNode>();
        wolves = new LinkedList<WolfNode>();

        oldClimate = NaturalSelectionModel.CLIMATE_EQUATOR;
        oldSelection = NaturalSelectionModel.SELECTION_NONE;


        // create the trees (manually positioned and sized)

        TreeNode bigTree = new TreeNode( 125, 138, 1 );
        addChildSprite( bigTree );
        trees.add( bigTree );

        TreeNode mediumTree = new TreeNode( 917, 115, 0.7 );
        addChildSprite( mediumTree );
        trees.add( mediumTree );

        TreeNode smallTree = new TreeNode( 635, 90, 0.2 );
        addChildSprite( smallTree );
        trees.add( smallTree );


        // create the shrubs (manually positioned and sized)

        ShrubNode shrubA = new ShrubNode( 80, 330, 1 );
        addChildSprite( shrubA );
        shrubs.add( shrubA );
        shrubA.setVisible( false );

        ShrubNode shrubB = new ShrubNode( 750, 200, 0.8 );
        addChildSprite( shrubB );
        shrubs.add( shrubB );
        shrubB.setVisible( false );

        ShrubNode shrubC = new ShrubNode( 320, 110, 0.6 );
        addChildSprite( shrubC );
        shrubs.add( shrubC );
        shrubC.setVisible( false );

    }

    public void onEvent( NaturalSelectionModel.Event event ) {
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_CLIMATE_CHANGE ) {
            onClimateChange( event.getNewClimate() );
        }
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_SELECTION_CHANGE ) {
            onSelectionFactorChange( event.getNewSelectionFactor() );
        }
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_NEW_BUNNY ) {
            onNewBunny( event.getNewBunny() );
        }
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_FRENZY_START ) {
            event.getFrenzy().addListener( new Frenzy.Listener() {
                public void onFrenzyStop( Frenzy frenzy ) {
                    for ( Iterator<WolfNode> iterator = wolves.iterator(); iterator.hasNext(); ) {
                        WolfNode wolfNode = iterator.next();
                        removeChildSprite( wolfNode );
                    }
                    wolves.clear();
                }

                public void onFrenzyTimeLeft( double timeLeft ) {

                }

                public void onWolfCreate( Wolf wolf ) {
                    onNewWolf( wolf );
                }
            } );
        }

    }

    private void onNewWolf( Wolf wolf ) {
        // create a bunny node with the correct visual appearance
        WolfNode wolfNode = new WolfNode( this );

        // randomly position the bunny
        wolfNode.setSpriteLocation( wolf.getX(), wolf.getY(), wolf.getZ() );
        wolfNode.setFlipped( wolf.isMovingRight() );

        // add the bunny
        addChildSprite( wolfNode );
        wolf.addListener( wolfNode );
        wolves.add( wolfNode );
        sprites.add( wolfNode );

    }

    private void onClimateChange( int climate ) {
        // turn the trees on and off

        if ( climate == oldClimate ) {
            return;
        }
        oldClimate = climate;

        if ( climate == NaturalSelectionModel.CLIMATE_EQUATOR ) {
            Iterator<TreeNode> iter = trees.iterator();
            while ( iter.hasNext() ) {
                ( iter.next() ).setVisible( true );
            }
        }
        else if ( climate == NaturalSelectionModel.CLIMATE_ARCTIC ) {
            Iterator<TreeNode> iter = trees.iterator();
            while ( iter.hasNext() ) {
                ( iter.next() ).setVisible( false );
            }
        }
    }

    private void onSelectionFactorChange( int selectionFactor ) {
        // turn the shrubs on and off

        if ( selectionFactor == oldSelection ) {
            return;
        }
        oldSelection = selectionFactor;

        if ( selectionFactor == NaturalSelectionModel.SELECTION_FOOD ) {
            Iterator<ShrubNode> iter = shrubs.iterator();
            while ( iter.hasNext() ) {
                ( iter.next() ).setVisible( true );
            }
        }
        else {
            Iterator<ShrubNode> iter = shrubs.iterator();
            while ( iter.hasNext() ) {
                ( iter.next() ).setVisible( false );
            }
        }
    }

    public void reset() {
        for ( NaturalSelectionSprite sprite : sprites ) {
            removeChild( sprite );
        }
        onClimateChange( NaturalSelectionDefaults.DEFAULT_CLIMATE );
        onSelectionFactorChange( NaturalSelectionDefaults.DEFAULT_SELECTION_FACTOR );
        sprites.clear();
        wolves.clear();
    }

    /**
     * Handles a new bunny
     *
     * @param bunny The bunny
     */
    public void onNewBunny( final Bunny bunny ) {
        // create a bunny node with the correct visual appearance
        BunnyNode bunnyNode = new BunnyNode( bunny.getColorPhenotype(), bunny.getTeethPhenotype(), bunny.getTailPhenotype(), this );

        // randomly position the bunny
        bunnyNode.setSpriteLocation( bunny.getX(), bunny.getY(), bunny.getZ() );
        bunnyNode.setFlipped( !bunny.isMovingRight() );

        // add the bunny
        addChildSprite( bunnyNode );
        bunny.addListener( bunnyNode );
        sprites.add( bunnyNode );

        bunnyNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                //System.out.println( "Bunny clicked: " + bunny.getId() );

                if ( bunny.canBeTargeted() ) {
                    bunny.setTargeted( true );
                }
            }
        } );
    }

    /**
     * Add a sprite. Inserted into the correct Z depth
     *
     * @param sprite The sprite to add
     */
    public void addChildSprite( NaturalSelectionSprite sprite ) {

        List displayList = getChildrenReference();

        if ( displayList.size() == 0 ) {
            //displayList.add( sprite );
            addChild( sprite );
            return;
        }

        //ListIterator iter = displayList.listIterator();
        ListIterator iter = getChildrenIterator();

        while ( iter.hasNext() ) {
            NaturalSelectionSprite nextSprite = (NaturalSelectionSprite) iter.next();
            if ( nextSprite.getSpriteZ() < sprite.getSpriteZ() ) {
                addChild( iter.previousIndex(), sprite );
                //iter.previous();
                //iter.add( sprite );
                return;
            }
        }

        // ahah! wasn't adding if it was behind all of the others!
        addChild( sprite );


    }

    public void removeChildSprite( NaturalSelectionSprite sprite ) {
        sprites.remove( sprite );
        removeChild( sprite );
    }


}
