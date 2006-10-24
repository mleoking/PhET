/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.model.QWIModel;

/**
 * User: Sam Reid
 * Date: Jan 1, 2006
 * Time: 5:25:55 PM
 * Copyright (c) Jan 1, 2006 by Sam Reid
 */

public class ExpandableDoubleSlitPanel extends AdvancedPanel {
    private QWIModule module;

    public ExpandableDoubleSlitPanel( final QWIModule module ) {
        super( QWIStrings.getString( "double.slits" ), QWIStrings.getString( "disable.slits" ) );
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
