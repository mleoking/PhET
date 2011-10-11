package edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.umd.cs.piccolo.PNode;

/**
 * Dialog that shows the periodic table and updates when the selected solute type changes
 *
 * @author Sam Reid
 */
public class PeriodicTableDialog extends JDialog {

    //Color for metals in the periodic table. darker light-gray.  Chosen to make visible on the projector and work well for colorblindness (deuteranope and protanope) and contrast with non-metals
    public static final Color METAL_COLOR = new Color( 160, 160, 160 );

    //Color for nonmetals in the periodic table. Brighter pink.  Chosen to make visible on the projector and work well for colorblindness (deuteranope and protanope) and contrast with metals
    public static final Color NON_METAL_COLOR = new Color( 255, 195, 195 );

    public PeriodicTableDialog( final Property<DispenserType> dispenser, final SugarAndSaltSolutionsColorScheme colorScheme, PhetFrame parentFrame ) {
        super( parentFrame );

        setContentPane( new PhetPCanvas() {
            final PhetPCanvas canvas = this;
            private final PNode root = new PNode();

            {
                addWorldChild( root );

                //Match the background with the rest of the sim
                colorScheme.backgroundColorSet.color.addObserver( new VoidFunction1<Color>() {
                    public void apply( Color color ) {
                        setBackground( color );
                    }
                } );

                //On init, and when the dispenser type changes, show the periodic table for the specified DispenserType
                dispenser.addObserver( new VoidFunction1<DispenserType>() {
                    public void apply( DispenserType dispenserType ) {
                        //inset is necessary since the periodic table bounds doesn't account for the stroke width so the top and left would be truncated without this.
                        //Kelly also requested the inset to be larger than the original value of 2: "I am not sure the idea that "salts are made of atoms on opposite sides of the PT" is clear. Can we add more space around the PT in the popup window?"
                        final int inset = 26;
                        final double scale = 1.5;
                        final PeriodicTableNode periodicTableNode = new PeriodicTableNode( METAL_COLOR, new HighlightMetals( dispenser.get().getElementAtomicMasses() ) ) {{
                            scale( scale );
                            setOffset( inset, inset );
                        }};

                        root.removeAllChildren();
                        root.addChild( periodicTableNode );

                        //Show a legend below the periodic table to indicate the coloring scheme for metals vs nonmetals
                        PeriodicTableLegend legend = new PeriodicTableLegend( periodicTableNode.getFullBounds().getWidth(), scale, colorScheme.whiteBackground ) {{
                            setOffset( inset, periodicTableNode.getFullBounds().getMaxY() + inset );
                        }};
                        root.addChild( legend );
                        final Dimension preferredSize = new Dimension( (int) periodicTableNode.getFullBounds().getWidth() + inset * 2,
                                                                       (int) ( periodicTableNode.getFullBounds().getHeight() + legend.getFullBounds().getHeight() + inset * 3 ) );

                        //Set a centered stage strategy so the periodic table will be centered and scale up and down with the dialog bounds
                        setWorldTransformStrategy( new CenteredStage( canvas, preferredSize ) );
                        setPreferredSize( preferredSize );
                    }
                } );
            }
        } );
        pack();
    }
}