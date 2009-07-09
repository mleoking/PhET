/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.view.sprites.*;
import edu.umd.cs.piccolo.PNode;

/**
 * Handles all of the sprites on the simulation canvas (bunnies, wolves, trees, shrubs, etc.)
 *
 * @author Jonathan Olson
 */
public class LandscapeNode extends PNode implements NaturalSelectionModel.Listener, NaturalSelectionSprite.Listener {

    /**
     * A list of the variable (bunny and wolf) sprites
     */
    private List<NaturalSelectionSprite> sprites;

    private List<BunnyNode> bunnies;
    private List<WolfNode> wolves;
    private List<TreeNode> trees;
    private List<ShrubNode> shrubs;

    // cached climate and selection factor
    private int oldClimate;
    private int oldSelection;
    private AffineTransform backgroundTransform;

    private NaturalSelectionModel model;
    private Landscape landscape;

    /**
     * Constructor
     *
     * @param model     The corresponding model
     * @param landscape The landscape model
     */
    public LandscapeNode( NaturalSelectionModel model, Landscape landscape ) {
        this.model = model;
        this.landscape = landscape;

        sprites = new LinkedList<NaturalSelectionSprite>();
        bunnies = new LinkedList<BunnyNode>();
        trees = new LinkedList<TreeNode>();
        shrubs = new LinkedList<ShrubNode>();
        wolves = new LinkedList<WolfNode>();

        backgroundTransform = new AffineTransform();

        oldClimate = NaturalSelectionModel.CLIMATE_EQUATOR;
        oldSelection = NaturalSelectionModel.SELECTION_NONE;

        // create the trees (manually positioned and sized)
        for ( Tree tree : model.getTrees() ) {
            TreeNode treeNode = new TreeNode( this, tree );
            addSprite( treeNode );
            trees.add( treeNode );
        }


        // create the shrubs (manually positioned and sized)
        for ( Shrub shrub : model.getShrubs() ) {
            ShrubNode shrubNode = new ShrubNode( this, shrub );
            addSprite( shrubNode );
            shrubs.add( shrubNode );
            shrubNode.setVisible( false );
        }

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
                    for ( WolfNode wolf : wolves ) {
                        removeSprite( wolf );
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
        // create a wolf node with the correct visual appearance
        WolfNode wolfNode = new WolfNode( this, wolf.getPosition() );

        // randomly position the wolf
        wolfNode.setFlipped( wolf.isMovingRight() );

        // add the wolf
        addSprite( wolfNode );
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
            setTreeVisibility( true );
        }
        else if ( climate == NaturalSelectionModel.CLIMATE_ARCTIC ) {
            setTreeVisibility( false );
        }
    }

    private void onSelectionFactorChange( int selectionFactor ) {
        // turn the shrubs on and off

        if ( selectionFactor == oldSelection ) {
            return;
        }
        oldSelection = selectionFactor;

        if ( selectionFactor == NaturalSelectionModel.SELECTION_FOOD ) {
            setShrubVisibility( true );
        }
        else {
            setShrubVisibility( false );
        }
    }

    private void setTreeVisibility( boolean bool ) {
        for ( TreeNode tree : trees ) {
            tree.setVisible( bool );
        }
    }

    private void setShrubVisibility( boolean bool ) {
        for ( ShrubNode shrub : shrubs ) {
            shrub.setVisible( bool );
        }
    }

    public void reset() {
        List<NaturalSelectionSprite> removedSprites = new LinkedList<NaturalSelectionSprite>();
        for ( NaturalSelectionSprite sprite : sprites ) {
            if ( indexOfChild( sprite ) != -1 && !( sprite instanceof TreeNode || sprite instanceof ShrubNode ) ) {
                removeChild( sprite );
                removedSprites.add( sprite );
            }
        }
        for ( NaturalSelectionSprite removedSprite : removedSprites ) {
            sprites.remove( removedSprite );
        }
        onClimateChange( NaturalSelectionDefaults.DEFAULT_CLIMATE );
        onSelectionFactorChange( NaturalSelectionDefaults.DEFAULT_SELECTION_FACTOR );
        bunnies.clear();
        wolves.clear();
    }

    /**
     * Handles a new bunny
     *
     * @param bunny The bunny
     */
    public void onNewBunny( final Bunny bunny ) {
        if ( !bunny.isAlive() ) {
            // don't instantiate a dead bunny (loading config, etc)
            return;
        }
        // create a bunny node with the correct visual appearance
        BunnyNode bunnyNode = new BunnyNode( bunny.getColorPhenotype(), bunny.getTeethPhenotype(), bunny.getTailPhenotype(), this, bunny.getPosition() );

        // randomly position the bunny
        bunnyNode.setFlipped( !bunny.isMovingRight() );

        // add the bunny
        addSprite( bunnyNode );
        bunny.addListener( bunnyNode );
        sprites.add( bunnyNode );
        bunnies.add( bunnyNode );
    }

    /**
     * Add a sprite. Inserted into the correct Z depth
     *
     * @param sprite The sprite to add
     */
    public void addSprite( NaturalSelectionSprite sprite ) {
        sprites.add( sprite );
        sprite.addSpriteListener( this );

        List displayList = getChildrenReference();

        if ( displayList.size() == 0 ) {
            addChild( sprite );
            return;
        }

        ListIterator iter = getChildrenIterator();

        boolean placed = false;

        while ( iter.hasNext() ) {
            NaturalSelectionSprite nextSprite = (NaturalSelectionSprite) iter.next();
            // TODO: optimize
            if ( nextSprite.getPosition().getZ() < sprite.getPosition().getZ() ) {
                addChild( iter.previousIndex(), sprite );
                placed = true;
                break;
            }
        }

        // ahah! wasn't adding if it was behind all of the others!
        if ( !placed ) {
            addChild( sprite );
        }

        repositionSprite( sprite );
        //spriteDepthCheck( sprite );


    }

    public void removeSprite( NaturalSelectionSprite sprite ) {
        sprites.remove( sprite );
        removeChild( sprite );
        sprite.removeSpriteListener( this );
    }

    public void spriteMoved( NaturalSelectionSprite sprite, boolean zChanged ) {
        repositionSprite( sprite );
        if ( zChanged ) {
            spriteDepthCheck( sprite );
        }
    }

    private void spriteDepthCheck( NaturalSelectionSprite sprite ) {
        List children = getChildrenReference();
        int idx = children.indexOf( sprite );
        if ( idx == -1 ) {
            return;
        }

        boolean ok = true;
        if ( idx > 0 && ( (NaturalSelectionSprite) getChild( idx - 1 ) ).getPosition().getZ() < sprite.getPosition().getZ() ) {
            ok = false;
        }
        if ( idx < children.size() - 1 && ( (NaturalSelectionSprite) getChild( idx + 1 ) ).getPosition().getZ() > sprite.getPosition().getZ() ) {
            ok = false;
        }

        if ( !ok ) {
            replaceSprite( sprite );
        }
    }

    private void replaceSprite( NaturalSelectionSprite sprite ) {
        removeChild( sprite );
        List children = getChildrenReference();
        for ( int i = 0; i < children.size(); i++ ) {
            NaturalSelectionSprite testSprite = (NaturalSelectionSprite) children.get( i );
            if ( sprite.getPosition().getZ() >= testSprite.getPosition().getZ() ) {
                addChild( i, sprite );
                return;
            }
        }
        addChild( children.size(), sprite );
    }


    private void repositionSprite( NaturalSelectionSprite sprite ) {
        Point3D position = sprite.getPosition();
        Point2D screenPosition = landscape.spriteToScreen( position );
        sprite.setOffset( screenPosition.getX(), screenPosition.getY() );
    }

    public void updateLayout( double width, double height ) {
        landscape.updateSize( width, height );
        for ( NaturalSelectionSprite sprite : sprites ) {
            repositionSprite( sprite );
            if ( sprite instanceof Rescalable ) {
                ( (Rescalable) sprite ).rescale();
            }
        }
    }

    public List<NaturalSelectionSprite> getSprites() {
        return sprites;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    public void setSpriteTransform( AffineTransform backgroundTransform ) {
        this.backgroundTransform = backgroundTransform;
    }

    public AffineTransform getSpriteTransform() {
        return backgroundTransform;
    }
}
