package edu.colorado.phet.qm.persistence;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.qm.SchrodingerApplication;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.potentials.CompositePotential;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.modules.single.SingleParticleModule;
import edu.colorado.phet.qm.view.piccolo.RectangularPotentialGraphic;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 8, 2006
 * Time: 9:31:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class QWIState implements Serializable {
    private ArrayList detectorList = new ArrayList();
    private ArrayList rectBarrierList = new ArrayList();

    public QWIState() {
    }

    public QWIState( SchrodingerModule schrodingerModule ) {
        for( int i = 0; i < schrodingerModule.getDiscreteModel().getDetectorSet().numDetectors(); i++ ) {
            Detector detector = schrodingerModule.getDiscreteModel().getDetectorSet().detectorAt( i );
            detectorList.add( new DetectorState( detector ) );
        }
        CompositePotential compositePotential = schrodingerModule.getDiscreteModel().getCompositePotential();
        for( int i = 0; i < compositePotential.numPotentials(); i++ ) {
            Potential pot = compositePotential.potentialAt( i );
            if( pot instanceof RectangularPotential ) {
                rectBarrierList.add( new RectBarrierState( (RectangularPotential)pot ) );
            }
        }
    }

    public ArrayList getDetectorList() {
        return detectorList;
    }

    public void setDetectorList( ArrayList detectorList ) {
        this.detectorList = detectorList;
    }

    public ArrayList getRectBarrierList() {
        return rectBarrierList;
    }

    public void setRectBarrierList( ArrayList rectBarrierList ) {
        this.rectBarrierList = rectBarrierList;
    }

    public String toString() {
        return super.toString() + ", det" + detectorList + ", pot=" + rectBarrierList;
    }

    public void restore( SchrodingerModule schrodingerModule ) {
        schrodingerModule.removeAllDetectors();
        schrodingerModule.removeAllPotentialBarriers();
//        schrodingerModule.addPotential();
        for( int i = 0; i < detectorList.size(); i++ ) {
            DetectorState detectorState = (DetectorState)detectorList.get( i );
            Detector detector = new Detector( schrodingerModule.getDiscreteModel(),
                                              detectorState.getRectangle().x, detectorState.getRectangle().y, detectorState.getRectangle().width, detectorState.getRectangle().height );
            schrodingerModule.getDiscreteModel().addDetector( detector );
            schrodingerModule.getSchrodingerPanel().addDetectorGraphic( detector );
        }
        for( int i = 0; i < rectBarrierList.size(); i++ ) {
            RectBarrierState rectBarrierState = (RectBarrierState)rectBarrierList.get( i );
            RectangularPotential potential = new RectangularPotential( schrodingerModule.getDiscreteModel(), rectBarrierState.getRectangle().x, rectBarrierState.getRectangle().y, rectBarrierState.getRectangle().width, rectBarrierState.getRectangle().height );
            schrodingerModule.getSchrodingerPanel().addRectangularPotentialGraphic( new RectangularPotentialGraphic( schrodingerModule.getSchrodingerPanel(), potential ) );
            schrodingerModule.getDiscreteModel().addPotential( potential );
        }
    }

    public static class SerializableRect implements Serializable {
        private Rectangle rectangle;

        public SerializableRect() {
        }

        public SerializableRect( Rectangle rect ) {
            this.rectangle = new Rectangle( rect );
        }

        public Rectangle getRectangle() {
            return rectangle;
        }

        public void setRectangle( Rectangle rectangle ) {
            this.rectangle = rectangle;
        }

        public String toString() {
            return super.toString() + ", " + rectangle;
        }
    }

    public static class RectBarrierState extends SerializableRect {
        public RectBarrierState() {
        }

        public RectBarrierState( RectangularPotential rectangularPotential ) {
            super( new Rectangle( rectangularPotential.getBounds() ) );
        }
    }

    public static class DetectorState extends SerializableRect {
        public DetectorState() {
        }

        public DetectorState( Detector detector ) {
            super.setRectangle( new Rectangle( detector.getBounds() ) );
        }
    }

    public static void main( String[] args ) throws Exception {
        SchrodingerApplication app = new SchrodingerApplication( args );
        PersistenceManager persistenceManager = new PersistenceManager( new JButton() );
        SchrodingerModule schrodingerModule = new SingleParticleModule( app, new SwingClock( 30, 1 ) );
//        schrodingerModule
        schrodingerModule.getDiscreteModel().addDetector( new Detector( schrodingerModule.getDiscreteModel(), 5, 6, 7, 8 ) );
        schrodingerModule.getDiscreteModel().addDetector( new Detector( schrodingerModule.getDiscreteModel(), 1, 1, 2, 2 ) );
        schrodingerModule.getDiscreteModel().addPotential( new RectangularPotential( schrodingerModule.getDiscreteModel(), 0, 0, 100, 100 ) );
        schrodingerModule.getDiscreteModel().addPotential( new RectangularPotential( schrodingerModule.getDiscreteModel(), 9, 9, 9, 9 ) );
//        persistenceManager.save( new QWIState( schrodingerModule ) );
        Object o = persistenceManager.load();
        System.out.println( "o = " + o );
        System.exit( 0 );
    }
}
