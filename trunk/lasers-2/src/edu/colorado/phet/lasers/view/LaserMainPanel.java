/**
 * Class: LaserMainPanel
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.view;


import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.util.Observable;

public class LaserMainPanel extends TabbedMainPanel {

    private BaseLaserApparatusPanel singleAtomApparatusPanel;

    private EnergyLevelsDialog energyLevelsDialog;

    public LaserMainPanel( PhetApplication application ) {
        super( application );
    }

    /**
     *
     */
    public void init() {

        // Set up apparatus panels
        this.addApparatusPanel( singleAtomApparatusPanel = new OneAtomTwoLevelsApparatusPanel() );
        this.addApparatusPanel( new OneAtomThreeLevelsApparatusPanel() );
        this.addApparatusPanel( new MultipleAtomTwoLevelApparatusPanel() );
        this.addApparatusPanel( new MultipleAtomThreeLevelApparatusPanel() );
        this.addApparatusPanel( new TestApparatusPanel() );

        super.initTabs();
        setCurrentApparatusPanel( singleAtomApparatusPanel );

        // Set up the energy levels panel
//        energyLevelsDialog = new EnergyLevelsDialog( PhetApplication.instance().getPhetFrame() );

        // Set up the monitor panel
//        PhetMonitorPanel monitorPanel = new MonitorPanel();
//        monitorPanel.add( new ThreeEnergyLevelMonitorPanel() );
//        setMonitorPanel( new ThreeEnergyLevelMonitorPanel() );
    }

    /**
     *
     * @param isVisible
     */
    public void setEnergyLevelsVisible( boolean isVisible ) {
        if( isVisible ) {
            energyLevelsDialog.show();
        }
        else {
            energyLevelsDialog.hide();
        }
    }

    /**
     *
     * @return
     */
    public JPanel getApplicationControlPanel() {
        return new ApplicationControlPanel( PhetApplication.instance() );
    }

    public void update( Observable o, Object arg ) {
    }
}
