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
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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

    private final JSpinner protonSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) );
    private final JSpinner massSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) );
    private final JSpinner chargeSpinner = new JSpinner( new SpinnerNumberModel( 0, -15, 15, 1 ) );

    private final Property<Integer> guessedProtonsProperty;
    private final Property<Integer> guessedNeutronsProperty;
    private final Property<Integer> guessedElectronsProperty;

    public InteractiveSymbolNode( final Property<Integer> guessedProtonsProperty,
            final Property<Integer> guessedNeutronsProperty, final Property<Integer> guessedElectronsProperty,
            final boolean showCharge ) {

        this.guessedProtonsProperty = guessedProtonsProperty;
        this.guessedNeutronsProperty = guessedNeutronsProperty;
        this.guessedElectronsProperty = guessedElectronsProperty;

        /*
        guessedProtonsProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ((Integer)protonSpinner.getValue() != guessedProtonsProperty.getValue()){
                    protonSpinner.setValue( guessedProtonsProperty.getValue() );
                }
            }
        });
        guessedNeutronsProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ((Integer)massSpinner.getValue() != guessedNeutronsProperty.getValue() + guessedProtonsProperty.getValue() ){
                    massSpinner.setValue( guessedNeutronsProperty.getValue() + guessedProtonsProperty.getValue() );
                }
            }
        });
        guessedElectronsProperty.addObserver( new SimpleObserver() {
            public void update() {
                System.out.println("----------------");
                System.out.println("electron number update, charge = " + (guessedProtonsProperty.getValue() - guessedElectronsProperty.getValue()));
                System.out.println("electrons = " + guessedElectronsProperty.getValue());
                System.out.println("protons = " + guessedProtonsProperty.getValue());
                if ((Integer)chargeSpinner.getValue() != guessedProtonsProperty.getValue() - guessedElectronsProperty.getValue() ){
                    chargeSpinner.setValue( guessedProtonsProperty.getValue() - guessedElectronsProperty.getValue() );
                }
            }
        });
        */

        PNode boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );

        PText symbol = new PText("X"){{
            setFont( SYMBOL_FONT );
            setOffset( WIDTH / 2 - getFullBoundsReference().width / 2, HEIGHT / 2 - getFullBoundsReference().height / 2 );
        }};
        addChild( symbol );

        protonSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                guessedProtonsProperty.setValue( (Integer) protonSpinner.getValue() );
                guessedElectronsProperty.setValue( (Integer) protonSpinner.getValue() - (Integer) chargeSpinner.getValue() );
            }
        } );
        PNode protonSpinnerPSwing = new PSwing( protonSpinner );
        protonSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
        protonSpinnerPSwing.setOffset( SPINNER_EDGE_OFFSET, HEIGHT - protonSpinnerPSwing.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( protonSpinnerPSwing );

        massSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                guessedNeutronsProperty.setValue( Math.max( (Integer) massSpinner.getValue() - (Integer) protonSpinner.getValue(), 0 ) );
            }
        } );
        PNode massSpinnerPSwing = new PSwing( massSpinner );
        massSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
        massSpinnerPSwing.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( massSpinnerPSwing );

        if ( showCharge ){
            chargeSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    guessedElectronsProperty.setValue( Math.max( (Integer) protonSpinner.getValue() - (Integer) chargeSpinner.getValue(), 0 ) );
                }
            } );
            PNode chargeSpinnerPSwing = new PSwing( chargeSpinner );
            chargeSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
            chargeSpinnerPSwing.setOffset(
                    WIDTH - chargeSpinnerPSwing.getFullBoundsReference().width - SPINNER_EDGE_OFFSET,
                    SPINNER_EDGE_OFFSET );
            addChild( chargeSpinnerPSwing );
        }
    }
}
