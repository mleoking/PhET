/**
 * Class: EmfApplication
 * Package: edu.colorado.phet.emf
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf;

//import edu.colorado.phet.common.model.AbstractModel;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ApplicationModel;
import edu.colorado.phet.common.userinterface.PhetFrame;
import edu.colorado.phet.common.userinterface.components.media.ApplicationModelControlPanel;
import edu.colorado.phet.common.userinterface.view.BasicViewMasterX;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.view.AntennaControlPanel;
import edu.colorado.phet.emf.view.AntennaPanel;
import edu.colorado.phet.emf.view.EmfGraphicFactory;
import edu.colorado.phet.waves.model.PlaneWavefront;
import edu.colorado.phet.waves.model.SineFunction;
import edu.colorado.phet.waves.model.Wavefront;

import javax.swing.*;
import java.awt.*;

public class EmfApplication {

    ApplicationModel model;
    JFrame mainFrame;

    EmfApplication( ApplicationModel model) {
        this.model = model;
        mainFrame = new PhetFrame( "Electro-Magnetic Fields",
                                   "An application dealing with radio waves",
                                   "0.1",
                                   new Dimension( 1024, 768 ) );
        Container contentPane = mainFrame.getContentPane();
        AntennaPanel antennaPanel = new AntennaPanel();
        contentPane.add( new ApplicationModelControlPanel( model ), BorderLayout.SOUTH );
        contentPane.add( antennaPanel, BorderLayout.CENTER );
        contentPane.add( new AntennaControlPanel(), BorderLayout.EAST );
        mainFrame.pack();

        EmfGraphicFactory graphicFactory = new EmfGraphicFactory();
        BasicViewMasterX viewMaster = new BasicViewMasterX( antennaPanel, graphicFactory );
        model.addObserver( viewMaster );
    }

    public void startApplication() {
        model.start();
        mainFrame.setVisible(true);
    }

    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 50;

    public static void main( String[] args ) {
        Wavefront emfWave = new Wavefront( new PlaneWavefront(), new SineFunction(), 1000, 1 );
        EmfApplication app = new EmfApplication( EmfModel.instance() );
        app.startApplication();
    }
}
