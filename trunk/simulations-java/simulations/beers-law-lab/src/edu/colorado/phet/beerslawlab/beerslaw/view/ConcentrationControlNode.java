// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PureWater;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control for changing solution's concentration.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationControlNode extends PNode {

    private static final Dimension TRACK_SIZE = new Dimension( 250, 30 );

    public ConcentrationControlNode( Property<BeersLawSolution> solution ) {

        // nodes
        PText labelNode = new PText( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.CONCENTRATION ) );
        labelNode.setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE ) );
        PPath sliderNode = new PPath( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) ); //TODO use a slider
        PPath textFieldNode = new PPath( new Rectangle2D.Double( 0, 0, 40, 40 ) ); //TODO use a JTextField
        final PText unitsNode = new PText( "????" );
        unitsNode.setFont( new PhetFont( BLLConstants.CONTROL_FONT_SIZE  ) );

        // rendering order
        PNode parentNode = new PNode();
        parentNode.addChild( labelNode );
        parentNode.addChild( sliderNode );
        parentNode.addChild( textFieldNode );
        parentNode.addChild( unitsNode );
        addChild( new ZeroOffsetNode( parentNode ) );

        // layout
        sliderNode.setOffset( labelNode.getFullBoundsReference().getMaxX() + 5,
                              labelNode.getFullBoundsReference().getCenterY() - ( sliderNode.getFullBoundsReference().getHeight() / 2 ) );
        textFieldNode.setOffset( sliderNode.getFullBoundsReference().getMaxX() + 10,
                              sliderNode.getFullBoundsReference().getCenterY() - ( textFieldNode.getFullBoundsReference().getHeight() / 2 ) );
        unitsNode.setOffset( textFieldNode.getFullBoundsReference().getMaxX() + 5,
                              textFieldNode.getFullBoundsReference().getCenterY() - ( unitsNode.getFullBoundsReference().getHeight() / 2 ) );

        // units are specific to the solution
        solution.addObserver( new VoidFunction1<BeersLawSolution>() {
            public void apply( BeersLawSolution solution ) {
                setVisible( !( solution instanceof PureWater ) );
                unitsNode.setText( solution.getDisplayUnits() );

            }
        });
    }
}
