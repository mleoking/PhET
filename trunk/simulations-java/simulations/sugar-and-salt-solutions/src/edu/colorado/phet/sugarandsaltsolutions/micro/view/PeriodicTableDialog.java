package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.CellFactory.HighlightElements;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;

/**
 * Dialog that shows the periodic table and updates when the selected solute type changes
 *
 * @author Sam Reid
 */
public class PeriodicTableDialog extends JDialog {
    public PeriodicTableDialog( final Property<DispenserType> dispenser, final SugarAndSaltSolutionsColorScheme colorScheme, PhetFrame parentFrame ) {
        super( parentFrame );
        setContentPane( new PhetPCanvas() {{
            colorScheme.backgroundColorSet.color.addObserver( new VoidFunction1<Color>() {
                public void apply( Color color ) {
                    setBackground( color );
                }
            } );
            final PeriodicTableNode node = new PeriodicTableNode( Color.white, new HighlightElements( dispenser.get().getElementAtomicMasses() ) ) {{
                scale( 1.5 );
            }};
            addScreenChild( node );
            setPreferredSize( new Dimension( (int) node.getFullBounds().getWidth(), (int) node.getFullBounds().getHeight() ) );
        }} );
        pack();
    }
}
