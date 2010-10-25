package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class defines a PNode that represents an interactive symbol for an
 * atom, i.e. one in which the number of protons, neutrons, and electrons can
 * be changed.  The format of the symbol is intended to look much like an
 * entry in the periodic table.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class InteractiveSymbolNode extends PNode {

    private static final Font SYMBOL_FONT = new PhetFont( 60, true );
    private static final double WIDTH = 200;
    private static final double HEIGHT = WIDTH;
    private static final double SPINNER_EDGE_OFFSET = 5;
    private static final double SPINNER_SCALE_FACTOR = 2;

    public InteractiveSymbolNode( final Property<Integer> guessedProtonsProperty,
            final Property<Integer> guessedNeutronsProperty, final Property<Integer> guessedElectronsProperty,
            final boolean showCharge ) {

        PNode boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );

        // Textual symbol.
        PText symbol = new PText("X"){{
            setFont( SYMBOL_FONT );
            setOffset( WIDTH / 2 - getFullBoundsReference().width / 2, HEIGHT / 2 - getFullBoundsReference().height / 2 );
        }};
        addChild( symbol );

        // Spinners for the various subatomic particles.
        final JSpinner protonSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) ){{
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    guessedProtonsProperty.setValue( (Integer)getValue() );
                }
            } );
        }};
        PNode protonSpinnerPSwing = new PSwing( protonSpinner );
        protonSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
        protonSpinnerPSwing.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( protonSpinnerPSwing );

        final JSpinner massSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) ){{
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    guessedNeutronsProperty.setValue( Math.max( (Integer) getValue() - guessedProtonsProperty.getValue(), 0 ) );
                }
            } );
        }};
        PNode massSpinnerPSwing = new PSwing( massSpinner );
        massSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
        massSpinnerPSwing.setOffset( SPINNER_EDGE_OFFSET, HEIGHT - massSpinnerPSwing.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( massSpinnerPSwing );
    }
}
