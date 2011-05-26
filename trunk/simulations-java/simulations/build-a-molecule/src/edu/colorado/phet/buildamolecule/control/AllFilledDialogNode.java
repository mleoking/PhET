package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Displays a dialog that tells the user that all collection boxes are full.
 */
public class AllFilledDialogNode extends PNode {
    public AllFilledDialogNode( PBounds availablePlayAreaBounds, ModelViewTransform mvt, final VoidFunction0 regenerateCallback ) {
        PNode background = new PNode();
        addChild( background );
        addChild( new SwingLayoutNode( new GridBagLayout() ) {{
            // smiley face
            addChild( new FaceNode( 120 ) {{
                          smile();
                      }},
                      new GridBagConstraints() {{
                          gridx = 0;
                          gridy = 0;
                      }}
            );

            // text explaining all collection boxes are filled
            addChild( new HTMLNode( BuildAMoleculeStrings.COLLECTION_ALL_FILLED ) {{
                          setFont( new PhetFont( 20, true ) );
                      }},
                      new GridBagConstraints() {{
                          gridx = 0;
                          gridy = 1;
                          insets = new Insets( 10, 0, 0, 0 );
                      }}
            );

            // button to generate a new kit/collection
            addChild( new HTMLImageButtonNode( BuildAMoleculeStrings.COLLECTION_TRY_WITH_DIFFERENT_MOLECULES, new PhetFont( Font.BOLD, 18 ), Color.ORANGE ) {{
                          addActionListener( new ActionListener() {
                              public void actionPerformed( ActionEvent e ) {
                                  regenerateCallback.apply();
                                  AllFilledDialogNode.this.setVisible( false );
                              }
                          } );
                      }},
                      new GridBagConstraints() {{
                          gridx = 0;
                          gridy = 2;
                          insets = new Insets( 10, 0, 0, 0 );
                      }}
            );
        }} );

        float padding = 10;

        PPath backgroundNode = PhetPPath.createRectangle( (float) getFullBounds().getX() - padding, (float) getFullBounds().getY() - padding, (float) getFullBounds().getWidth() + 2 * padding, (float) getFullBounds().getHeight() + 2 * padding );
        backgroundNode.setPaint( BuildAMoleculeConstants.COMPLETE_BACKGROUND_COLOR );
        background.addChild( backgroundNode );

        Rectangle2D playAreaViewBounds = mvt.modelToView( availablePlayAreaBounds ).getBounds2D();
        centerFullBoundsOnPoint( playAreaViewBounds.getCenterX(), playAreaViewBounds.getCenterY() );
    }
}
