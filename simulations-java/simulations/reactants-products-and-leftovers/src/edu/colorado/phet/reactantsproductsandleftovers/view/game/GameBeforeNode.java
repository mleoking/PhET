package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameListener;
import edu.colorado.phet.reactantsproductsandleftovers.view.BoxNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


public class GameBeforeNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    
    private static final String TITLE = RPALStrings.LABEL_BEFORE_REACTION;
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final double TITLE_Y_SPACING = 10;
    
    private final GameModel model;
    private final BoxNode boxNode;
    private final GameListener gameListener;
    private final ImageLayoutNode imageLayoutNode;
    
    public GameBeforeNode( GameModel model ) {
        super();
        
        // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        PText titleNode = new PText( TITLE );
        titleNode.setFont( TITLE_FONT );
        titleNode.setTextPaint( Color.BLACK );
        addChild( titleNode );
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
        
        // sync with model
        this.model = model;
        gameListener = new GameAdapter() {
            //XXX show the user's guess, if applicable
            //XXX show the answer, when requested
        };
        model.addGameListener( gameListener );
        
        // image layout strategy
        imageLayoutNode = new GridLayoutNode( BOX_SIZE );
        addChild( imageLayoutNode );
        addReactantImages();
    }
    
    public void cleanup() {
        model.removeGameListener( gameListener );
    }
    
    /**
     * Box width, used by layout code.
     * @return
     */
    public double getBoxWidth() {
        return boxNode.getFullBoundsReference().getWidth();
    }
    
    /**
     * Box height, used by layout code.
     * @return
     */
    public double getBoxHeight() {
        return boxNode.getFullBoundsReference().getHeight();
    }
    
    /*
     * Add images for the reactants.
     */
    private void addReactantImages() {
        
        Reactant[] reactants = model.getReaction().getReactants();
        PNode previousNode = null;
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            for ( int j = 0; j < reactant.getQuantity(); j++ ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageLayoutNode.addNode( imageNode, previousNode, null );
                previousNode = imageNode;
            }
        }
    }
}
