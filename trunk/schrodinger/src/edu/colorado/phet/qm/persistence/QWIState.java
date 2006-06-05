package edu.colorado.phet.qm.persistence;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.qm.QWIApplication;
import edu.colorado.phet.qm.QWIModule;
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

    public QWIState( QWIModule qwiModule ) {
        for( int i = 0; i < qwiModule.getDiscreteModel().getDetectorSet().numDetectors(); i++ ) {
            Detector detector = qwiModule.getDiscreteModel().getDetectorSet().detectorAt( i );
            detectorList.add( new DetectorState( detector ) );
        }
        CompositePotential compositePotential = qwiModule.getDiscreteModel().getCompositePotential();
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

    public void restore( QWIModule qwiModule ) {
        qwiModule.removeAllDetectors();
        qwiModule.removeAllPotentialBarriers();
//        schrodingerModule.addPotential();
        for( int i = 0; i < detectorList.size(); i++ ) {
            DetectorState detectorState = (DetectorState)detectorList.get( i );
            Detector detector = new Detector( qwiModule.getDiscreteModel(),
                                              detectorState.getX(), detectorState.getY(), detectorState.getWidth(), detectorState.getHeight() );
            qwiModule.getDiscreteModel().addDetector( detector );
            qwiModule.getSchrodingerPanel().addDetectorGraphic( detector );
        }
        for( int i = 0; i < rectBarrierList.size(); i++ ) {
            RectBarrierState rectBarrierState = (RectBarrierState)rectBarrierList.get( i );
            RectangularPotential potential = new RectangularPotential( qwiModule.getDiscreteModel(), rectBarrierState.getX(), rectBarrierState.getY(), rectBarrierState.getWidth(), rectBarrierState.getHeight() );
            qwiModule.getSchrodingerPanel().addRectangularPotentialGraphic( new RectangularPotentialGraphic( qwiModule.getSchrodingerPanel(), potential ) );
            qwiModule.getDiscreteModel().addPotential( potential );
        }
    }

    public static class SerializableRect implements Serializable {
        private int x;
        private int y;
        private int width;
        private int height;

        public SerializableRect() {
        }

        public SerializableRect( Rectangle rect ) {
            this.x = rect.x;
            this.y = rect.y;
            this.width = rect.width;
            this.height = rect.height;
        }

        public int getX() {
            return x;
        }

        public void setX( int x ) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY( int y ) {
            this.y = y;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth( int width ) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight( int height ) {
            this.height = height;
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
            super( new Rectangle( detector.getBounds() ) );
        }
    }

    public static void main( String[] args ) throws Exception {
        QWIApplication app = new QWIApplication( args );
        PersistenceManager persistenceManager = new PersistenceManager( new JButton() );
        QWIModule qwiModule = new SingleParticleModule( app, new SwingClock( 30, 1 ) );
//        schrodingerModule
        qwiModule.getDiscreteModel().addDetector( new Detector( qwiModule.getDiscreteModel(), 5, 6, 7, 8 ) );
        qwiModule.getDiscreteModel().addDetector( new Detector( qwiModule.getDiscreteModel(), 1, 1, 2, 2 ) );
        qwiModule.getDiscreteModel().addPotential( new RectangularPotential( qwiModule.getDiscreteModel(), 0, 0, 100, 100 ) );
        qwiModule.getDiscreteModel().addPotential( new RectangularPotential( qwiModule.getDiscreteModel(), 9, 9, 9, 9 ) );
//        persistenceManager.save( new QWIState( schrodingerModule ) );
        Object o = persistenceManager.load();
        System.out.println( "o = " + o );
        System.exit( 0 );
    }
}
