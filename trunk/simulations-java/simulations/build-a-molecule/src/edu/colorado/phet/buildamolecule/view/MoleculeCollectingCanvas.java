package edu.colorado.phet.buildamolecule.view;

import java.awt.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.control.CollectionAreaNode;
import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Canvas that shows multiple kits AND a collection area to the right
 */
public class MoleculeCollectingCanvas extends BuildAMoleculeCanvas {

    private CollectionAreaNode collectionAreaNode;

    public MoleculeCollectingCanvas( Frame parentFrame, final KitCollectionModel initialModel, final boolean singleCollectionMode, final VoidFunction0 regenerateCallback ) {
        super( parentFrame, initialModel, singleCollectionMode );

//        // TODO: change if we support changing models
//        initialModel.allCollectionBoxesFilled.addObserver( new SimpleObserver() {
//            public void update() {
//                if ( !initialModel.allCollectionBoxesFilled.getValue() ) {
//                    // not filled
//                    return;
//                }
//                addWorldChild( new PNode() {{
//                    addChild( new SwingLayoutNode( new GridBagLayout() ) {{
//                        // TODO: i18n
//                        addChild( new HTMLNode( "You have filled all of your collection boxes.<br>Would you like to fill a different set of collection boxes?" ),
//                                  new GridBagConstraints() {{
//                                      gridx = 0;
//                                      gridy = 0;
//                                  }} );
//                        // TODO: i18n
//                        addChild( new PSwing( new JButton( "Yes" ) {{ // or how about just "I guess, since I have no other option"
//                            addActionListener( new ActionListener() {
//                                public void actionPerformed( ActionEvent e ) {
//                                    regenerateCallback.apply();
//                                }
//                            } );
//                        }} ),
//                                  new GridBagConstraints() {{
//                                      gridx = 0;
//                                      gridy = 1;
//                                  }} );
//                    }} );
//                    PhetPPath.createRectangle( (float) getFullBounds().getX() - 10, (float) getFullBounds().getY() - 10, (float) getFullBounds().getWidth() + 20, (float) getFullBounds().getHeight() + 20 );
//                }} );
//            }
//        } );
    }

    @Override
    protected void addChildren() {
        collectionAreaNode = new CollectionAreaNode( this, getModel(), singleCollectionMode ) {{
            double collectionAreaPadding = 20;
            setOffset( BuildAMoleculeConstants.STAGE_SIZE.width - getFullBounds().getWidth() - collectionAreaPadding, collectionAreaPadding );
        }};
        addWorldChild( collectionAreaNode );
    }
}
