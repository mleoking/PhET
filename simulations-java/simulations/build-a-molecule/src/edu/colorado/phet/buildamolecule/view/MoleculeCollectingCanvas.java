package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Canvas that shows multiple kits AND a collection area to the right
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private CollectionAreaNode collectionAreaNode;

    public MoleculeCollectingCanvas( Frame parentFrame, final KitCollectionModel initialModel, final boolean singleCollectionMode, final VoidFunction0 regenerateCallback ) {
        super( parentFrame, initialModel, singleCollectionMode );

        // TODO: change if we support changing models
        initialModel.allCollectionBoxesFilled.addObserver( new SimpleObserver() {
            public void update() {
                if ( !initialModel.allCollectionBoxesFilled.getValue() ) {
                    // not filled
                    return;
                }
                addWorldChild( new CollectionBoxesFullNode( regenerateCallback, initialModel ) );
            }
        } );
    }

    @Override
    protected void addChildren() {
        collectionAreaNode = new CollectionAreaNode( this, getModel(), singleCollectionMode ) {{
            double collectionAreaPadding = 20;
            setOffset( BuildAMoleculeConstants.STAGE_SIZE.width - getFullBounds().getWidth() - collectionAreaPadding, collectionAreaPadding );
        }};
        addWorldChild( collectionAreaNode );
    }

    private class CollectionBoxesFullNode extends PNode {
        public CollectionBoxesFullNode( final VoidFunction0 regenerateCallback, KitCollectionModel initialModel ) {
            PNode background = new PNode();
            addChild( background );
            addChild( new SwingLayoutNode( new GridBagLayout() ) {{
                // TODO: i18n
                addChild( new HTMLNode( "You have filled all of your collection boxes.<br>Would you like to fill a different set of collection boxes?" ) {{
                    setFont( new PhetFont( 16 ) );
                }},
                          new GridBagConstraints() {{
                              gridx = 0;
                              gridy = 0;
                          }} );
                // TODO: i18n
                addChild( new PSwing( new JButton( "Yes" ) {{ // or how about just "I guess, since I have no other option"
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            regenerateCallback.apply();
                        }
                    } );
                }} ),
                          new GridBagConstraints() {{
                              gridx = 0;
                              gridy = 1;
                              insets = new Insets( 10, 0, 0, 0 );
                          }} );
            }} );

            float padding = 10;

            background.addChild( PhetPPath.createRectangle( (float) getFullBounds().getX() - padding, (float) getFullBounds().getY() - padding, (float) getFullBounds().getWidth() + 2 * padding, (float) getFullBounds().getHeight() + 2 * padding ) );

            Rectangle2D playAreaViewBounds = getModelViewTransform().modelToView( initialModel.getAvailablePlayAreaBounds() ).getBounds2D();
            centerFullBoundsOnPoint( playAreaViewBounds.getCenterX(), playAreaViewBounds.getCenterY() );
        }
    }
}
