/**
 * Class: IdealGasModule
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.help.HelpItem;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.controller.command.RemoveMoleculeCmd;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.*;
import edu.colorado.phet.idealgas.view.monitors.*;
import edu.colorado.phet.instrumentation.Thermometer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class IdealGasModule extends Module {

    private IdealGasModel idealGasModel;
    private PressureSensingBox box;
    private Gravity gravity;
    private Pump pump;
    private CmLines cmLines;
    private static WiggleMeGraphic wiggleMeGraphic;

    private PressureSlice pressureSlice;
    private AbstractClock clock;
    private PressureSliceGraphic pressureSliceGraphic;
    private DefaultInteractiveGraphic rulerGraphic;
    private EnergyHistogramDialog histogramDlg;
    private ArrayList visibleInstruments = new ArrayList();
    private JDialog measurementDlg;
    private JDialog speciesMonitorDlg;
    private IdealGasControlPanel idealGasControlPanel;


    public IdealGasModule( AbstractClock clock ) {
        this( clock, SimStrings.get( "ModuleTitle.IdealGas" ) );
        this.clock = clock;
    }

    protected IdealGasModel getIdealGasModel() {
        return (IdealGasModel)getModel();
    }

    public JDialog setMeasurementDlgVisible( boolean isVisible ) {
        if( measurementDlg == null ) {
            measurementDlg = new MeasurementDialog( PhetApplication.instance().getApplicationView().getPhetFrame(), this );
        }
        measurementDlg.setVisible( isVisible );
        if( isVisible ) {
            visibleInstruments.add( measurementDlg );
        }
        else {
            visibleInstruments.remove( measurementDlg );
        }
        return measurementDlg;
    }

    public JDialog setSpeciesMonitorDlgEnabled( boolean isEnabled ) {
        if( speciesMonitorDlg == null ) {
            speciesMonitorDlg = new SpeciesMonitorDialog( PhetApplication.instance().getApplicationView().getPhetFrame(),
                                                          idealGasModel );
        }
        speciesMonitorDlg.setVisible( isEnabled );
        if( isEnabled ) {
            visibleInstruments.add( speciesMonitorDlg );
        }
        else {
            visibleInstruments.remove( speciesMonitorDlg );
        }
        return speciesMonitorDlg;
    }

    class ExpIdealGasApparatusPanel extends BaseIdealGasApparatusPanel {
        public ExpIdealGasApparatusPanel( Module module, Box2D box ) {
            super( module, box );
        }

        public void repaint( long tm, int x, int y, int width, int height ) {
            //            super.repaint( tm, x, y, width, height );
        }

        public void repaint() {
            //            super.repaint();
        }

        public void repaint( long tm ) {
            //            super.repaint( tm );
        }

        public void repaint( int x, int y, int width, int height ) {
            //            super.repaint( x, y, width, height );
        }

        public void repaint( Rectangle r ) {
            //            super.repaint( r );
        }
    }

    class Rendered implements ModelElement {
        private BufferedImage buffer;
        private Graphics2D imgBuffGraphics;
        private ApparatusPanel ap;
        private int displayWidth;
        private int displayHeight;

        public Rendered( ApparatusPanel ap ) {
            this.ap = ap;
        }

        public void stepInTime( double dt ) {
            if( ap.getWidth() != displayWidth || ap.getHeight() != displayHeight ) {
                buffer = new BufferedImage( ap.getWidth(), ap.getHeight(), BufferedImage.TYPE_INT_RGB );
                imgBuffGraphics = (Graphics2D)buffer.getGraphics();
            }
            ap.paint( imgBuffGraphics );
            Graphics g = PhetApplication.instance().getApplicationView().getPhetFrame().getGraphics();
            g.drawImage( buffer, 0, 0, null );
            g.dispose();
        }
    }

    public IdealGasModule( AbstractClock clock, String name ) {
        super( name );

        // Create the model
        idealGasModel = new IdealGasModel( clock.getDt() );
        setModel( idealGasModel );

        gravity = new Gravity( idealGasModel );
        idealGasModel.addModelElement( gravity );

        // Add collision experts
        idealGasModel.addCollisionExpert( new SphereSphereExpert( idealGasModel, clock.getDt() ) );
        idealGasModel.addCollisionExpert( new SphereBoxExpert( idealGasModel ) );

        // Create the box
        double xOrigin = 132 + IdealGasConfig.X_BASE_OFFSET;
        double yOrigin = 252 + IdealGasConfig.Y_BASE_OFFSET;
        double xDiag = 434 + IdealGasConfig.X_BASE_OFFSET;
        double yDiag = 497 + IdealGasConfig.Y_BASE_OFFSET;
        box = new PressureSensingBox( new Point2D.Double( xOrigin, yOrigin ),
                                      new Point2D.Double( xDiag, yDiag ), idealGasModel, clock );
        idealGasModel.addBox( box );
        setApparatusPanel( new BaseIdealGasApparatusPanel( this, box ) );

        // Add the pressure gauge
        PressureSlice gaugeSlice = new PressureSlice( box, idealGasModel, clock );
        gaugeSlice.setY( box.getMinY() + 50 );
        idealGasModel.addModelElement( gaugeSlice );
        PressureDialGauge pressureGauge = new PressureDialGauge( box, getApparatusPanel(), gaugeSlice );
        addGraphic( pressureGauge, 20 );

        // Add the thermometer
        double thermometerHeight = 100;
        Point2D.Double thermometerLoc = new Point2D.Double( box.getMaxX() - 30, box.getMinY() - thermometerHeight );
        Thermometer thermometer = new IdealGasThermometer( getApparatusPanel(),
                                                           idealGasModel, thermometerLoc,
                                                           thermometerHeight, 10, true, 0, 1000E3 );
        addGraphic( thermometer, 8 );

        // Create the pump
        pump = new Pump( this, box );

        // Set up the graphics for the pump
        try {
            BufferedImage pumpImg = ImageLoader.loadBufferedImage( IdealGasConfig.PUMP_IMAGE_FILE );
            BufferedImage handleImg = ImageLoader.loadBufferedImage( IdealGasConfig.HANDLE_IMAGE_FILE );
            PhetImageGraphic handleGraphic = new PhetImageGraphic( getApparatusPanel(), handleImg );

            PumpHandleGraphic handleGraphicImage = new PumpHandleGraphic( pump, handleGraphic,
                                                                          IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 238,
                                                                          IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 100,
                                                                          IdealGasConfig.X_BASE_OFFSET + 578, IdealGasConfig.Y_BASE_OFFSET + 238 );
            this.addGraphic( handleGraphicImage, -6 );
            PhetImageGraphic pumpGraphic = new PhetImageGraphic( getApparatusPanel(), pumpImg, IdealGasConfig.X_BASE_OFFSET + 436, IdealGasConfig.Y_BASE_OFFSET + 253 );
            this.addGraphic( pumpGraphic, -4 );

            if( wiggleMeGraphic == null ) {
                wiggleMeGraphic = new WiggleMeGraphic( getApparatusPanel(),
                                                       new Point2D.Double( IdealGasConfig.X_BASE_OFFSET + 470, IdealGasConfig.Y_BASE_OFFSET + 200 ) );
                addGraphic( wiggleMeGraphic, 40 );
                Thread wiggleMeThread = new Thread( wiggleMeGraphic );
                wiggleMeThread.start();
            }
            pump.addObserver( new SimpleObserver() {
                public void update() {
                    if( wiggleMeGraphic != null ) {
                        wiggleMeGraphic.kill();
                        getApparatusPanel().removeGraphic( wiggleMeGraphic );
                        wiggleMeGraphic = null;
                        pump.removeObserver( this );
                    }
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Set up the box
        Box2DGraphic boxGraphic = new Box2DGraphic( getApparatusPanel(), box );
        addGraphic( boxGraphic, 10 );

        // Add the animated mannequin
        Mannequin pusher = new Mannequin( getApparatusPanel(), idealGasModel, box, boxGraphic );
        addGraphic( pusher, 10 );

        // Set up the control panel
        idealGasControlPanel = new IdealGasControlPanel( this );
        PhetControlPanel controlPanel = new PhetControlPanel( this, idealGasControlPanel);
        setControlPanel( controlPanel );

        // Place a slider to control the stove
        StoveControlPanel stoveControlPanel = new StoveControlPanel( this );
        stoveControlPanel.setBounds( 400, 450, 200, 150 );
        getApparatusPanel().add( stoveControlPanel );

        // Add help items
        addHelp();
        //        Rendered renderer = new Rendered( getApparatusPanel() );
        //        idealGasModel.addModelElement( renderer );
    }

    private void addHelp(){
        HelpItem helpItem1 = new HelpItem( "Wall can be moved\nleft and right",
                                           box.getPosition().getX(), box.getPosition().getY(),
                                           HelpItem.BELOW, HelpItem.LEFT );
        helpItem1.setForegroundColor( IdealGasConfig.helpColor );
        addHelpItem( helpItem1 );
        HelpItem helpItem2 = new HelpItem( "Door can be slid\nleft and right",
                                           box.getPosition().getX(), box.getPosition().getY(),
                                           HelpItem.ABOVE, HelpItem.RIGHT );
        helpItem2.setForegroundColor( IdealGasConfig.helpColor );
        addHelpItem( helpItem2 );
        HelpItem helpItem3 = new HelpItem( "Heat can be removed or added\nby adjusting stove",
                                            box.getPosition().getX(), box.getMaxY() );
        helpItem3.setForegroundColor( IdealGasConfig.helpColor );
        addHelpItem( helpItem3 );
    }

    public void setCurrentSpecies( Class moleculeClass ) {
        pump.setCurrentGasSpecies( moleculeClass );
    }

    public void setCmLinesOn( boolean cmLinesOn ) {
        if( cmLines == null ) {
            cmLines = new CmLines( getApparatusPanel(), idealGasModel );
        }
        if( cmLinesOn ) {
            addGraphic( cmLines, 20 );
        }
        else {
            getApparatusPanel().removeGraphic( cmLines );
        }

    }

    public void setStove( int value ) {
        idealGasModel.setHeatSource( (double)value );
        ( (BaseIdealGasApparatusPanel)getApparatusPanel() ).setStove( value );
    }

    public void setGravity( double value ) {
        gravity.setAmt( value );
    }

    public void pumpGasMolecules( int numMolecules ) {
        pump.pump( numMolecules );
    }

    public void removeGasMolecule() {
        Command cmd = new RemoveMoleculeCmd( idealGasModel, pump.getCurrentGasSpecies() );
        cmd.doIt();
    }

    protected PressureSensingBox getBox() {
        return box;
    }

    public Pump getPump() {
        return pump;
    }


    public void setPressureSliceEnabled( boolean pressureSliceEnabled ) {
        if( pressureSlice == null ) {
            pressureSlice = new PressureSlice( getBox(), (IdealGasModel)getModel(), clock );
            pressureSliceGraphic = new PressureSliceGraphic( getApparatusPanel(),
                                                             pressureSlice,
                                                             getBox() );
        }
        if( pressureSliceEnabled ) {
            getModel().addModelElement( pressureSlice );
            addGraphic( pressureSliceGraphic, 20 );
        }
        else {
            getApparatusPanel().removeGraphic( pressureSliceGraphic );
            getModel().removeModelElement( pressureSlice );
        }
    }

    public void setRulerEnabed( boolean rulerEnabled ) {
        if( rulerEnabled ) {
            rulerGraphic = new RulerGraphic( getApparatusPanel() );
            getApparatusPanel().addGraphic( rulerGraphic, Integer.MAX_VALUE );
        }
        else {
            getApparatusPanel().removeGraphic( rulerGraphic );
        }
        getApparatusPanel().repaint();
    }

    public JDialog setHistogramDlgEnabled( boolean histogramDlgEnabled ) {
        if( histogramDlg == null ) {
            histogramDlg = new EnergyHistogramDialog( PhetApplication.instance().getApplicationView().getPhetFrame(),
                                                      (IdealGasModel)getModel() );
        }
        histogramDlg.setVisible( histogramDlgEnabled );
        if( histogramDlgEnabled ) {
            visibleInstruments.add( histogramDlg );
        }
        else {
            visibleInstruments.remove( histogramDlg );
        }
        return histogramDlg;
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        for( int i = 0; i < visibleInstruments.size(); i++ ) {
            Component component = (Component)visibleInstruments.get( i );
            component.setVisible( true );
        }
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        for( int i = 0; i < visibleInstruments.size(); i++ ) {
            Component component = (Component)visibleInstruments.get( i );
            component.setVisible( false );
        }
    }

    protected IdealGasControlPanel getIdealGasControlPanel(){
        return idealGasControlPanel;
    }
}
