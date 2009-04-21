/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Handles all of the sprites on the simulation canvas (bunnies, wolves, trees, shrubs, etc.)
 *
 * @author Jonathan Olson
 */
public class SpritesNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {

    /**
     * A list of the variable (bunny and wolf) sprites
     */
    private ArrayList sprites;

    // trees and shrubs lists
    private ArrayList trees;
    private ArrayList shrubs;

    // cached climate and selection factor
    private int oldClimate;
    private int oldSelection;

    /**
     * Constructor
     */
    public SpritesNode() {
        sprites = new ArrayList();
        trees = new ArrayList();
        shrubs = new ArrayList();

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

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void onClimateChange( int climate ) {
        // turn the trees on and off

        if ( climate == oldClimate ) {
            return;
        }
        oldClimate = climate;

        if ( climate == NaturalSelectionModel.CLIMATE_EQUATOR ) {
            Iterator iter = trees.iterator();
            while ( iter.hasNext() ) {
                ( (TreeNode) iter.next() ).setVisible( true );
            }
        }
        else if ( climate == NaturalSelectionModel.CLIMATE_ARCTIC ) {
            Iterator iter = trees.iterator();
            while ( iter.hasNext() ) {
                ( (TreeNode) iter.next() ).setVisible( false );
            }
        }
    }

    public void onSelectionFactorChange( int selectionFactor ) {
        // turn the shrubs on and off

        if ( selectionFactor == oldSelection ) {
            return;
        }
        oldSelection = selectionFactor;

        if ( selectionFactor == NaturalSelectionModel.SELECTION_FOOD ) {
            Iterator iter = shrubs.iterator();
            while ( iter.hasNext() ) {
                ( (ShrubNode) iter.next() ).setVisible( true );
            }
        }
        else {
            Iterator iter = shrubs.iterator();
            while ( iter.hasNext() ) {
                ( (ShrubNode) iter.next() ).setVisible( false );
            }
        }
    }

    public void reset() {
        Iterator iter = sprites.iterator();
        while ( iter.hasNext() ) {
            BunnyNode bunnyNode = (BunnyNode) iter.next();
            removeChild( bunnyNode );
        }
        onClimateChange( NaturalSelectionDefaults.DEFAULT_CLIMATE );
        onSelectionFactorChange( NaturalSelectionDefaults.DEFAULT_SELECTION_FACTOR );
        sprites.clear();
    }

    /**
     * Called from the model when a new bunny occurs. Used here to create the bunny sprite, and to randomly position it
     * @param bunny The bunny
     */
    public void onNewBunny( Bunny bunny ) {
        // create a bunny node with the correct visual appearance
        BunnyNode bunnyNode = new BunnyNode( bunny.getColorGenotype().getPhenotype(), bunny.getTeethGenotype().getPhenotype(), bunny.getTailGenotype().getPhenotype() );

        // randomly position the bunny
        double bunnyDepth = Math.random();
        double sidePadding = 25;
        double locationX = ( NaturalSelectionDefaults.VIEW_SIZE.getWidth() - sidePadding * 2 ) * Math.random() + sidePadding;
        double locationY = 0;
        double locationZ = 1 + bunnyDepth * 1.5;
        bunnyNode.setSpriteLocation( locationX, locationY, locationZ );

        // add the bunny
        addChildSprite( bunnyNode );
        bunny.addListener( bunnyNode );
        sprites.add( bunnyNode );
    }

    /**
     * Add a sprite. Inserted into the correct Z depth 
     * @param sprite The sprite to add
     */
    public void addChildSprite( NaturalSelectionSprite sprite ) {
        List displayList = getChildrenReference();

        addChild( sprite );

        Collections.sort( displayList );

        /* TODO: don't sort the entire list, do something like this (but damn you piccolo for not supporting insertChild)

        if( displayList.size() == 0 ) {
            //displayList.add( sprite );
            addChild( sprite );
            return;
        }

        //ListIterator iter = displayList.listIterator();
        ListIterator iter = getChildrenIterator();

        while( iter.hasNext() ) {
            NaturalSelectionSprite nextSprite = (NaturalSelectionSprite) iter.next();
            if( nextSprite.getSpriteZ() > sprite.getSpriteZ() ) {
                //addChild( iter.previousIndex(), sprite );
                iter.previous();
                iter.add( sprite );
                return;
            }
        }

        */
    }


}
