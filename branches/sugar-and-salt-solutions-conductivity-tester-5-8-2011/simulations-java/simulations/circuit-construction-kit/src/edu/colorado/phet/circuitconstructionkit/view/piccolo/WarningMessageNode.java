// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;

public class WarningMessageNode extends PNode {
    private ShadowPText shadowPText;
    private Circuit circuit;

    public WarningMessageNode(Circuit circuit) {
        this.circuit = circuit;
        shadowPText = new ShadowPText(CCKStrings.getString("WarningMessages.PossibleError"));
        shadowPText.setFont(new PhetFont(Font.BOLD, 16));
        shadowPText.setTextPaint(Color.yellow);
        shadowPText.setShadowColor(Color.black);
        shadowPText.setShadowOffset(1, 1);
        addChild(shadowPText);

        circuit.addCircuitListener(new CircuitListenerAdapter() {
            public void editingChanged() {
                updateVisibility();
            }

            public void junctionsConnected(Junction a, Junction b, Junction newTarget) {
                updateVisibility();
            }

            public void branchRemoved(Branch branch) {
                updateVisibility();
            }
        });
        updateVisibility();
    }

    private void updateVisibility() {
        setVisible(circuit.hasProblematicConfiguration());
    }
}
