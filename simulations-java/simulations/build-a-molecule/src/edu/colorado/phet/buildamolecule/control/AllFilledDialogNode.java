package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.model.KitCollectionModel;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Displays a dialog that tells the user that all collection boxes are full.
 */
public class AllFilledDialogNode extends PNode {
    public AllFilledDialogNode( KitCollectionModel initialModel, ModelViewTransform mvt, final VoidFunction0 regenerateCallback ) {
        PNode background = new PNode();
        addChild( background );
        addChild( new SwingLayoutNode( new GridBagLayout() ) {{
            // TODO: i18n
            addChild( new HTMLNode( "You have filled all of your collection boxes!" ) {{
                setFont( new PhetFont( 20, true ) );
            }},
                      new GridBagConstraints() {{
                          gridx = 0;
                          gridy = 0;
                      }} );
            // TODO: i18n
            addChild( new PSwing( new JButton( "Try with different molecules" ) {{ // or how about just "I guess, since I have no other option"
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        regenerateCallback.apply();
                    }
                } );
            }} ) {{
                setScale( 1.5 );
            }},
                      new GridBagConstraints() {{
                          gridx = 0;
                          gridy = 1;
                          insets = new Insets( 10, 0, 0, 0 );
                      }} );
        }} );

        float padding = 10;

        background.addChild( PhetPPath.createRectangle( (float) getFullBounds().getX() - padding, (float) getFullBounds().getY() - padding, (float) getFullBounds().getWidth() + 2 * padding, (float) getFullBounds().getHeight() + 2 * padding ) );

        Rectangle2D playAreaViewBounds = mvt.modelToView( initialModel.getAvailablePlayAreaBounds() ).getBounds2D();
        centerFullBoundsOnPoint( playAreaViewBounds.getCenterX(), playAreaViewBounds.getCenterY() );
    }
}
