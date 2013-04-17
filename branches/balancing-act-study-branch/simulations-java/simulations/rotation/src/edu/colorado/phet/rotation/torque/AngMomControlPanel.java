// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:56:44 PM
 */
public class AngMomControlPanel extends FullTorqueControlPanel {
    public AngMomControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, AbstractTorqueModule abstractTorqueModule, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( rulerNode, abstractTorqueModule, vectorViewModel, angleUnitModel );
    }

    protected FullTorqueControlPanel.TorqueSlider[] getSliders( AbstractTorqueModule torqueModule, RotationPlatform rp ) {
        return new FullTorqueControlPanel.TorqueSlider[]{
                createOuterRadiusSlider( rp ),
                createInnerRadiusSlider( rp ),
                createMassSlider( rp ),
                createFrictionSlider( torqueModule )
        };
    }

}
