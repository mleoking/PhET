package edu.colorado.phet.quantumwaveinterference.persistence;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.quantumwaveinterference.QWIConstants;
import edu.colorado.phet.quantumwaveinterference.QuantumWaveInterferenceApplication;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.model.Detector;
import edu.colorado.phet.quantumwaveinterference.model.FractionalDoubleSlit;
import edu.colorado.phet.quantumwaveinterference.model.Potential;
import edu.colorado.phet.quantumwaveinterference.model.potentials.CompositePotential;
import edu.colorado.phet.quantumwaveinterference.model.potentials.RectangularPotential;
import edu.colorado.phet.quantumwaveinterference.modules.single.SingleParticleModule;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.RectangularPotentialGraphic;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jan 8, 2006, 9:31:36 PM
 */
public class QWIState implements Serializable, IProguardKeepClass {
    private ArrayList detectorList = new ArrayList();
    private ArrayList rectBarrierList = new ArrayList();
    private DoubleSlitState doubleSlitState;
    private boolean doubleSlitEnabled;

    public QWIState() {
    }

    public boolean equals( Object obj ) {
        if( obj instanceof QWIState ) {
            QWIState qwiState = (QWIState)obj;
            return detectorList.equals( qwiState.detectorList ) &&
                   rectBarrierList.equals( qwiState.rectBarrierList ) &&
                   doubleSlitState.equals( qwiState.doubleSlitState ) &&
                   doubleSlitEnabled == qwiState.doubleSlitEnabled;
        }
        else {
            return false;
        }

    }

    public QWIState( QWIModule qwiModule ) {
        for( int i = 0; i < qwiModule.getQWIModel().getDetectorSet().numDetectors(); i++ ) {
            Detector detector = qwiModule.getQWIModel().getDetectorSet().detectorAt( i );
            detectorList.add( new DetectorState( detector ) );
        }
        CompositePotential compositePotential = qwiModule.getQWIModel().getCompositePotential();
        for( int i = 0; i < compositePotential.numPotentials(); i++ ) {
            Potential pot = compositePotential.potentialAt( i );
            if( pot instanceof RectangularPotential ) {
                rectBarrierList.add( new RectBarrierState( (RectangularPotential)pot ) );
            }
        }
        this.doubleSlitState = new DoubleSlitState( qwiModule.getQWIModel().getFractionalDoubleSlit() );
        this.doubleSlitEnabled = qwiModule.getQWIModel().isDoubleSlitEnabled();
    }

    public DoubleSlitState getDoubleSlitState() {
        return doubleSlitState;
    }

    public void setDoubleSlitState( DoubleSlitState doubleSlitState ) {
        this.doubleSlitState = doubleSlitState;
    }

    public boolean isDoubleSlitEnabled() {
        return doubleSlitEnabled;
    }

    public void setDoubleSlitEnabled( boolean doubleSlitEnabled ) {
        this.doubleSlitEnabled = doubleSlitEnabled;
    }

    public static class DoubleSlitState implements IProguardKeepClass {
        private double height;
        private double separation;
        private double size;
        private double y;

        public DoubleSlitState() {
        }

        public boolean equals( Object obj ) {
            if( obj instanceof DoubleSlitState ) {
                DoubleSlitState doubleSlitState = (DoubleSlitState)obj;
                return height == doubleSlitState.height &&
                       separation == doubleSlitState.separation &&
                       size == doubleSlitState.size &&
                       y == doubleSlitState.y;
            }
            else {
                return false;
            }
        }

        public DoubleSlitState( FractionalDoubleSlit doubleSlitPotential ) {
            this.height = doubleSlitPotential.getHeight();
            this.separation = doubleSlitPotential.getSlitSeparation();
            this.size = doubleSlitPotential.getSlitSize();
            this.y = doubleSlitPotential.getY();
        }

        public double getHeight() {
            return height;
        }

        public void setHeight( double height ) {
            this.height = height;
        }

        public double getSeparation() {
            return separation;
        }

        public void setSeparation( double separation ) {
            this.separation = separation;
        }

        public double getSize() {
            return size;
        }

        public void setSize( double size ) {
            this.size = size;
        }

        public double getY() {
            return y;
        }

        public void setY( double y ) {
            this.y = y;
        }

        public void restore( FractionalDoubleSlit fractionalDoubleSlit ) {
            fractionalDoubleSlit.setHeight( height );
            fractionalDoubleSlit.setSlitSeparation( separation );
            fractionalDoubleSlit.setSlitSize( size );
            fractionalDoubleSlit.setY( y );
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
        qwiModule.getQWIModel().setDoubleSlitEnabled( false );
        qwiModule.removeAllDetectors();
        qwiModule.removeAllPotentialBarriers();
//        schrodingerModule.addPotential();
        for( int i = 0; i < detectorList.size(); i++ ) {
            DetectorState detectorState = (DetectorState)detectorList.get( i );
            Detector detector = new Detector( qwiModule.getQWIModel(),
                                              detectorState.getX(), detectorState.getY(), detectorState.getWidth(), detectorState.getHeight() );
            qwiModule.getQWIModel().addDetector( detector );
            qwiModule.getSchrodingerPanel().addDetectorGraphic( detector );
        }
        for( int i = 0; i < rectBarrierList.size(); i++ ) {
            RectBarrierState rectBarrierState = (RectBarrierState)rectBarrierList.get( i );
            RectangularPotential potential = new RectangularPotential( qwiModule.getQWIModel(), rectBarrierState.getX(), rectBarrierState.getY(), rectBarrierState.getWidth(), rectBarrierState.getHeight() );
            qwiModule.getSchrodingerPanel().addRectangularPotentialGraphic( new RectangularPotentialGraphic( qwiModule.getSchrodingerPanel(), potential ) );
            qwiModule.getQWIModel().addPotential( potential );
        }
        doubleSlitState.restore( qwiModule.getQWIModel().getFractionalDoubleSlit() );
        qwiModule.getQWIModel().setDoubleSlitEnabled( doubleSlitEnabled );
    }

    public static class SerializableRect implements IProguardKeepClass {
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
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new QuantumWaveInterferenceApplication( config );
            }
        };
        PhetApplicationConfig config = new PhetApplicationConfig( args, appConstructor, QWIConstants.PROJECT_NAME, QWIConstants.FLAVOR_QUANTUM_WAVE_INTERFERENCE );
        QuantumWaveInterferenceApplication app = (QuantumWaveInterferenceApplication) appConstructor.getApplication( config );
        PersistenceManager persistenceManager = new PersistenceManager( new JButton() );
        QWIModule qwiModule = new SingleParticleModule( app, new SwingClock( 30, 1 ) );
//        schrodingerModule
        qwiModule.getQWIModel().addDetector( new Detector( qwiModule.getQWIModel(), 5, 6, 7, 8 ) );
        qwiModule.getQWIModel().addDetector( new Detector( qwiModule.getQWIModel(), 1, 1, 2, 2 ) );
        qwiModule.getQWIModel().addPotential( new RectangularPotential( qwiModule.getQWIModel(), 0, 0, 100, 100 ) );
        qwiModule.getQWIModel().addPotential( new RectangularPotential( qwiModule.getQWIModel(), 9, 9, 9, 9 ) );
//        persistenceManager.save( new QWIState( schrodingerModule ) );
        Object o = persistenceManager.load();
        System.out.println( "o = " + o );
        System.exit( 0 );
    }
}
