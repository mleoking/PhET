package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
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

    private final JSpinner protonSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) ){{
        try {
            //Try to set the text color to red for protons, but be prepared to fail due to type unsafety
            ( (JSpinner.DefaultEditor) getEditor() ).getTextField().setForeground( Color.red );
        }
        catch ( Exception e ) {
            System.out.println( "ignoring = " + e );
        }
    }};
    private final JSpinner massSpinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) );
    private final JSpinner chargeSpinner = new JSpinner( new SpinnerNumberModel( 0, -15, 15, 1 ) ) {
        {
            //TODO: figure out a way to suppress the + prefix for 0 while keeping it for positive numbers
            setEditor( new NumberEditor( this,"+0;-0") );//Shows + sign for positive numbers (but unfortunately also for zero)
        }

        @Override
        public void setValue( Object value ) {
            super.setValue( value );
            try {
                int v = ((Integer)value).intValue();
                Color color = Color.black;
                if (v>0){
                    color=Color.red;
                }
                else if (v<0){
                    color=Color.blue;
                }
                ( (JSpinner.DefaultEditor) getEditor() ).getTextField().setForeground( color );
            }
            catch ( Exception e ) {
                System.out.println( "ignoring = " + e );
            }
        }
    };

    public InteractiveSymbolNode( final boolean showCharge ) {
        PNode boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, WIDTH, HEIGHT ), Color.white,
                new BasicStroke( 3 ), Color.black );
        addChild( boundingBox );

        PText symbol = new PText("X"){{
            setFont( SYMBOL_FONT );
            setOffset( WIDTH / 2 - getFullBoundsReference().width / 2, HEIGHT / 2 - getFullBoundsReference().height / 2 );
        }};
        addChild( symbol );

        protonSpinner.setForeground( Color.red );
        PNode protonSpinnerPSwing = new PSwing( protonSpinner );
        protonSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
        protonSpinnerPSwing.setOffset( SPINNER_EDGE_OFFSET, HEIGHT - protonSpinnerPSwing.getFullBoundsReference().height - SPINNER_EDGE_OFFSET );
        addChild( protonSpinnerPSwing );

        PNode massSpinnerPSwing = new PSwing( massSpinner );
        massSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
        massSpinnerPSwing.setOffset( SPINNER_EDGE_OFFSET, SPINNER_EDGE_OFFSET );
        addChild( massSpinnerPSwing );

        if ( showCharge ){
            PNode chargeSpinnerPSwing = new PSwing( chargeSpinner );
            chargeSpinnerPSwing.scale( SPINNER_SCALE_FACTOR );
            chargeSpinnerPSwing.setOffset(
                    WIDTH - chargeSpinnerPSwing.getFullBoundsReference().width - SPINNER_EDGE_OFFSET,
                    SPINNER_EDGE_OFFSET );
            addChild( chargeSpinnerPSwing );
        }
    }

    /**
     * Determines the particle counts given the proton count, mass number and charge.
     * @return the guess
     */
    public AtomValue getGuess() {
        final int protons = (Integer) protonSpinner.getValue();
        final int massNumber = (Integer) massSpinner.getValue();
        final int charge = (Integer) chargeSpinner.getValue();
        return new AtomValue( protons, massNumber - protons, protons - charge); 
    }

    public void displayAnswer( AtomValue answer ) {
        protonSpinner.setValue( answer.getProtons() );
        massSpinner.setValue( answer.getMassNumber());
        chargeSpinner.setValue( answer.getCharge());
        
        protonSpinner.setEnabled( false );
        massSpinner.setEnabled( false );
        chargeSpinner.setEnabled( false );
    }
}
