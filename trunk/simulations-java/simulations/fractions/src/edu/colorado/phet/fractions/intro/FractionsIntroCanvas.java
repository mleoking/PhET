// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.model.FractionsIntroModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends PhetPCanvas {

    //Stage where nodes are added and scaled up and down
    private final PNode rootNode;

    //Size for the stage, should have the right aspect ratio since it will always be visible
    //The dimension was determined by running on Windows and inspecting the dimension of the canvas after menubar and tabs are added
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private static final double INSET = 10;

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );

        //Show the main fraction control.  Wrap in a zero offset node since its internal layout is not normalized
        final ZeroOffsetNode fractionNode = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( fractionNode );

        final RepresentationToolbox toolboxNode = new RepresentationToolbox( model.representations ) {{
            setOffset( INSET, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( toolboxNode );

        addChild( new DecimalFractionNode( model.fraction, model.decimalRepresentation.enabled ) {{
            setOffset( ( STAGE_SIZE.getWidth() - fractionNode.getFullBounds().getMaxX() ) / 2 + fractionNode.getFullBounds().getMaxX() - getFullBounds().getWidth() / 2, fractionNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        addChild( new ZeroOffsetNode( new ReducedFractionNode( model.reducedFraction, model.reducedFractionRepresentation.enabled ) ) {{
            setOffset( toolboxNode.getFullBounds().getMaxX() + 40, fractionNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        addChild( new PercentNode( model.fraction, model.percentRepresentation.enabled ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, fractionNode.getFullBounds().getY() / 2 - getFullBounds().getHeight() / 2 );
        }} );

        addChild( new WordsFractionNode( model.numerator, model.denominator, model.wordsRepresentation.enabled ) {{
            new RichSimpleObserver() {
                @Override public void update() {
                    setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - INSET * 2 );
                }
            }.observe( model.numerator, model.denominator );
        }} );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}