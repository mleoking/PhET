/**
 * Class: AntennaPanel
 * Package: edu.colorado.phet.waves.view
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.command.AddTransmittingElectronCmd;
import edu.colorado.phet.command.SetMovementSinusoidalCmd;
import edu.colorado.phet.common.userinterface.graphics.ApparatusPanel;
import edu.colorado.phet.common.model.command.commands.AddModelElementCommand;
import edu.colorado.phet.emf.EmfApplication;
import edu.colorado.phet.emf.model.EmfModel;
import edu.colorado.phet.emf.model.EmfSensingElectron;
import edu.colorado.phet.emf.model.Electron;

import java.awt.*;
import java.awt.geom.Point2D;

public class AntennaPanel extends ApparatusPanel {
    private FieldLatticeView fieldLatticeView;

    public AntennaPanel() {

        AntennaPanel.setInstance( this );

        Point origin = new Point( 100, 300 );
        int fieldWidth = 400;
        int fieldHeight = 1;
//        int fieldHeight = 360;
        int latticeSpacingX = 10;
        int latticeSpacingY = 40;

        float amplitude = 50;
        float freq = (float)EmfApplication.s_speedOfLight / fieldWidth;

        freq = .01f;
//        amplitude = (float)EmfApplication.s_speedOfLight / ( freq );

        Electron electron = new Electron( new Point2D.Double( origin.getX(), origin.getY() + 00 ) );
        new AddTransmittingElectronCmd( electron ).doIt();
        new SetMovementSinusoidalCmd( (float)freq, amplitude ).doIt();

        ElectronGraphic electronGraphic = new ElectronGraphic( electron );
        electron.addObserver( electronGraphic );
        addGraphic( electronGraphic, 5 );

//        Wavefront emfWave = new Wavefront( new PlaneWavefront(), new ConstantFunction(), 0.1f, 1 );
//        WaveMedium waveMedium = new WaveMedium();
//        waveMedium.addWavefront( emfWave );
//        new AddEmfModelElementCmd( waveMedium ).doIt();

//        WaveMediumView waveMediumView = new WaveMediumView(
//                new Rectangle2D.Double( 150, 50, 300, 100 ), 20 );
//        waveMedium.addObserver( waveMediumView );
//        addGraphic( waveMediumView, 5 );

        int latticeOriginX = origin.x + latticeSpacingX;
        int numLatticeRows = ( fieldHeight - 1 ) / latticeSpacingY + 1;
        int latticeOriginY = origin.y - ( numLatticeRows / 2 ) * latticeSpacingY;
        Point latticeOrigin = new Point( latticeOriginX, latticeOriginY );
        fieldLatticeView = new FieldLatticeView( electron,
                                                                          latticeOrigin,
                                                                          fieldWidth - latticeSpacingX, fieldHeight,
                                                                          latticeSpacingX,
                                                                          latticeSpacingY );
        addGraphic( fieldLatticeView, 4 );

        Point2D.Double receivingElectronLoc = new Point2D.Double( origin.x + fieldWidth, origin.y );
        EmfSensingElectron receivingElectron = new EmfSensingElectron( receivingElectronLoc, electron );
        electron.addObserver( receivingElectron );
        new AddModelElementCommand( EmfModel.instance(), receivingElectron ).doItLater();

        ElectronGraphic receivingElectronGraphic = new ElectronGraphic( receivingElectron );
        receivingElectron.addObserver( receivingElectronGraphic );
        addGraphic( receivingElectronGraphic, 5 );

        EmfModel.instance().addObserver( this );
    }


    public void setFieldCurvesVisible( boolean enabled ) {
        fieldLatticeView.setFieldCurvesEnabled( enabled );
    }

    //
    // Static fields and methods
    //
    private static AntennaPanel s_instance;

    private static void setInstance( AntennaPanel panel ) {
        s_instance = panel;
    }

    public static AntennaPanel instance() {
        return s_instance;
    }
}
