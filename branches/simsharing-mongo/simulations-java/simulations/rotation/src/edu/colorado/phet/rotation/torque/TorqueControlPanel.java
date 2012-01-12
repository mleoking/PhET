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
 * Oct 24, 2007 at 10:46:26 AM
 */
public class TorqueControlPanel extends FullTorqueControlPanel {
	/**
	 * Constructor.
	 * 
	 * @param rulerNode
	 * @param rotationGraphSet
	 * @param graphSetModel
	 * @param torqueModule
	 * @param vectorViewModel
	 * @param angleUnitModel - if null, no angle unit selector is created on the panel.
	 */
    public TorqueControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, final AbstractTorqueModule torqueModule, VectorViewModel vectorViewModel, AngleUnitModel angleUnitModel ) {
        super( rulerNode, torqueModule, vectorViewModel, angleUnitModel );
    }

    protected FullTorqueControlPanel.TorqueSlider[] getSliders( AbstractTorqueModule torqueModule, RotationPlatform rp ) {
        return new FullTorqueControlPanel.TorqueSlider[]{
                createFrictionSlider( torqueModule )
        };
    }
}
