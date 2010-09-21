/*  */
package edu.colorado.phet.quantumwaveinterference.controls;

import edu.colorado.phet.common.phetcommon.view.AdvancedPanel;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;

/**
 * User: Sam Reid
 * Date: Jan 1, 2006
 * Time: 5:25:55 PM
 */

public class ExpandableDoubleSlitPanel extends AdvancedPanel {
    private QWIModule module;

    public ExpandableDoubleSlitPanel( final QWIModule module ) {
        super( QWIResources.getString( "controls.slits.show" ), QWIResources.getString( "controls.slits.hide" ) );
        this.module = module;
        addControlFullWidth( new DoubleSlitControlPanel( module.getQWIModel(), module ) );
        addListener( new Listener() {
            public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                getDiscreteModel().setDoubleSlitEnabled( false );
            }

            public void advancedPanelShown( AdvancedPanel advancedPanel ) {
                getDiscreteModel().setDoubleSlitEnabled( true );
            }
        } );

        getDiscreteModel().addListener( new QWIModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                if( getDiscreteModel().isDoubleSlitEnabled() ) {
                    showAdvanced();
                }
                else {
                    hideAdvanced();
                }
            }
        } );
    }

    private QWIModel getDiscreteModel() {
        return module.getQWIModel();
    }
}
