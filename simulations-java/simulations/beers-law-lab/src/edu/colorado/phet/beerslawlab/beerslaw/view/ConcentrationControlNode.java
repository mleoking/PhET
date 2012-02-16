// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PureWater;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control for changing solution's concentration.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationControlNode extends PNode {

    private static final Dimension TRACK_SIZE = new Dimension( 200, 15 );
    private static final PhetFont FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE  );
    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<BeersLawSolution> solution;
    private final PNode sliderNode; //TODO create a custom slider node
    private final JTextField textField;
    private final PText unitsNode;

    public ConcentrationControlNode( Property<BeersLawSolution> solution ) {

        this.solution = solution;

        // nodes
        PText labelNode = new PText( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.CONCENTRATION ) ) {{
            setFont( FONT );
        }};
        sliderNode = new PPath( new Rectangle2D.Double( 0, 0, TRACK_SIZE.width, TRACK_SIZE.height ) );
        textField = new JTextField( "??????" ) {{
            setColumns( 4 );
            setFont( FONT );
            setHorizontalAlignment( SwingConstants.RIGHT );
        }};
        PNode textFieldNode = new PSwing( textField );
        unitsNode = new PText( "????" ) {{
            setFont( FONT );
        }};

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

        // update the control when concentration changes
        final VoidFunction1<Double> concentrationObserver = new VoidFunction1<Double>() {
            public void apply( Double concentration ) {
                updateControl();
            }
        };

        // when solution selection changes, rewire the concentration observer and update the control
        solution.addObserver( new ChangeObserver<BeersLawSolution> () {
            public void update( BeersLawSolution newSolution, BeersLawSolution oldSolution ) {
                if ( oldSolution != null ) {
                    oldSolution.concentration.removeObserver( concentrationObserver );
                }
                newSolution.concentration.addObserver( concentrationObserver );
                setVisible( !( newSolution instanceof PureWater ) );
                updateControl();
            }
        });

        //TODO add handlers for textField

        updateControl(); // because adding a ChangeObserver doesn't result in immediate notification
    }

    private void updateControl() {
        BeersLawSolution currentSolution = solution.get();
        //TODO move slider to correct position
        textField.setText( FORMAT.format( currentSolution.concentration.get() * ( 1 / Math.pow( 10, currentSolution.concentrationDisplayExponent ) ) ) );
        unitsNode.setText( currentSolution.getDisplayUnits() );
    }
}
