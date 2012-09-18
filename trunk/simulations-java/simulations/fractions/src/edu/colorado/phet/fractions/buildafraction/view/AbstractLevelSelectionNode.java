// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.P2;
import fj.data.List;
import lombok.Data;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.controlpanel.PaddedNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.common.view.BackButton;
import edu.colorado.phet.fractions.common.view.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.*;

/**
 * Node that shows the levels and lets the user choose the level and settings
 *
 * @author Sam Reid
 */
class AbstractLevelSelectionNode extends PNode {

    final ResetAllButtonNode resetAllButton;

    //REVIEW doc
    public static @Data class Page {
        public final List<List<LevelInfo>> info;
    }

    //REVIEW doc
    //Rows + Columns
    AbstractLevelSelectionNode( final String title, final List<Page> pages, final LevelSelectionContext context, final IntegerProperty selectedPage ) {

        //Title text, only shown when the user is choosing a level
        PNode titleText = new PNode() {{
            addChild( new PhetPText( title, new PhetFont( 38, true ) ) );
        }};

        titleText.centerFullBoundsOnPoint( STAGE_SIZE.width / 2, INSET + titleText.getFullBounds().getHeight() / 2 );
        addChild( titleText );

        final PNode allPages = new PNode();

        final ArrayList<PNode> pageNodes = new ArrayList<PNode>();
        for ( P2<Page, Integer> page : pages.zipIndex() ) {

            final int index = page._2();
            final VBox box = toButtonSetNode( page._1().info, context );

            final PNode pageNode = new PNode();
            pageNodes.add( pageNode );
            pageNode.addChild( box );

            allPages.addChild( pageNode );
            pageNode.setOffset( STAGE_SIZE.width * index, 0 );

            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer selectedPage ) {
                    if ( selectedPage == index ) {
                        pageNode.setTransparency( 1 );
                    }
                    else {

                        //Wait then fade
                        PActivity activity = pageNode.animateToTransparency( pageNode.getTransparency(), BuildAFractionModule.ANIMATION_TIME );
                        activity.setDelegate( new PActivityDelegateAdapter() {
                            public void activityFinished( final PActivity activity ) {
                                pageNode.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
                            }
                        } );
                    }

                }
            } );
            pageNode.setTransparency( index == selectedPage.get() ? 1 : 0 );
        }

        selectedPage.addObserver( new VoidFunction1<Integer>() {
            public void apply( final Integer selectedPage ) {
                allPages.animateToPositionScaleRotation( -STAGE_SIZE.width * selectedPage, 0, 1, 0, BuildAFractionModule.ANIMATION_TIME );
            }
        } );

        // Have to set the initial offset manually since animateTo only works after attached to scene graph
        allPages.setOffset( -STAGE_SIZE.width * selectedPage.get(), 0 );

        addChild( allPages );

        resetAllButton = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                context.reset();
            }
        }, context.getComponent(), CONTROL_FONT, Color.black, RefreshButtonNode.BUTTON_COLOR ) {{
            setOffset( STAGE_SIZE.width - this.getFullWidth() - INSET, STAGE_SIZE.height - this.getFullHeight() - INSET );
            setConfirmationEnabled( false );
        }};

        addChild( resetAllButton );

        addChild( new HBox( 30,
                            new BackButton( selectedPage._decrement(), selectedPage.greaterThan( 0 ) ),
                            new CarouselDotComponent( selectedPage ),
                            new ForwardButton( selectedPage._increment(), selectedPage.lessThan( 1 ) ) ) {{
            setOffset( pageNodes.get( 0 ).getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, pageNodes.get( pageNodes.size() - 1 ).getFullBounds().getMaxY() + 20 );
        }} );
    }

    //REVIEW doc
    private VBox toButtonSetNode( final List<List<LevelInfo>> pageLevels, final LevelSelectionContext context ) {
        ArrayList<HBox> boxes = new ArrayList<HBox>();
        for ( List<LevelInfo> list : pageLevels ) {
            List<PNode> icons = list.map( new F<LevelInfo, PNode>() {
                @Override public PNode f( final LevelInfo levelInfo ) {
                    return toLevelIcon( levelInfo, pageLevels, context );
                }
            } );
            boxes.add( new HBox( 25, icons.array( PNode[].class ) ) );
        }
        return new VBox( 20, boxes.toArray( new PNode[boxes.size()] ) ) {{
            centerFullBoundsOnPoint( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 );
        }};
    }

    //REVIEW doc
    private static PNode toLevelIcon( final LevelInfo info, final List<List<LevelInfo>> allLevels, final LevelSelectionContext context ) {
        final List<PNode> nodes = allLevels.bind( new F<List<LevelInfo>, List<PNode>>() {
            @Override public List<PNode> f( final List<LevelInfo> list ) {
                return list.map( LevelInfo._icon );
            }
        } );
        final Dimension2DDouble maxSize = PhetPNode.getMaxSize( nodes );

        final Property<Boolean> selected = new Property<Boolean>( false );
        final PaddedNode centerIcon = new PaddedNode( maxSize, info.icon );
        LevelIconNode node = new LevelIconNode( info.name, centerIcon, info.levelProgress.stars, info.levelProgress.maxStars );

        return new ToggleButtonNode( node, selected, new VoidFunction0() {
            public void apply() {
                SimSharingManager.sendButtonPressed( UserComponentChain.chain( Components.levelButton, info.levelIdentifier.getLevelType() + ": " + info.name ) );
                selected.set( true );

                //Show it pressed in for a minute before starting up.
                new Timer( 100, new ActionListener() {
                    public void actionPerformed( final ActionEvent e ) {
                        context.levelButtonPressed( info );

                        //prep for next time
                        selected.set( false );
                    }
                } ) {{
                    setInitialDelay( 100 );
                    setRepeats( false );
                }}.start();
            }
        } );
    }

    //Button icon for a single level, shows the level name, a shape and the progress stars
    public static class LevelIconNode extends PNode {
        public LevelIconNode( final String text, PNode icon, int numStars, int maxStars ) {
            addChild( new VBox( new PhetPText( text, new PhetFont( 18, true ) ), icon, new StarSetNode( numStars, maxStars ) ) );
        }
    }
}