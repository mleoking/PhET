package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class GenerationCountNode extends PNode implements NaturalSelectionModel.Listener {
    private PText label;

    public GenerationCountNode( NaturalSelectionModel model ) {
        label = new PText( "Generations: 1" );
        label.setFont( new PhetFont( 14, true ) );
        addChild( label );
        model.addListener( this );
    }

    public double getTextWidth() {
        return label.getWidth();
    }

    public void onEvent( NaturalSelectionModel.Event event ) {
        if ( event.getType() == NaturalSelectionModel.Event.TYPE_NEW_GENERATION ) {
            label.setText( "Generations: " + String.valueOf( event.getNewGeneration() + 1 ) );
        }
    }
}
