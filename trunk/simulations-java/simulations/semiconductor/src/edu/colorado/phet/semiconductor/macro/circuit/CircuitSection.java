package edu.colorado.phet.semiconductor.macro.circuit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor.SemiconductorApplication;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.circuit.particles.WireParticle;
import edu.colorado.phet.semiconductor.macro.circuit.particles.WireParticleGraphic;
import edu.colorado.phet.semiconductor.macro.doping.DopantChangeListener;
import edu.colorado.phet.semiconductor.macro.doping.DopantDropListener;
import edu.colorado.phet.semiconductor.macro.doping.DopantGraphic;
import edu.colorado.phet.semiconductor.macro.doping.DopantSlot;
import edu.colorado.phet.semiconductor.macro.energy.states.Speed;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:17:44 PM
 */
public class CircuitSection implements ModelElement, Graphic, DopantDropListener, ConductionListener, Speed {

    MacroCircuit circuit;
    MacroCircuitGraphic circuitGraphic;
    private SemiconductorApplication application;
    private ModelViewTransform2D transform;
    ArrayList particles = new ArrayList();
    ArrayList particleGraphics = new ArrayList();
    private BatterySpinner batterySpinner;
    ArrayList dopantSlots = new ArrayList();
    private JButton jb;
    private ArrayList dopantChangeListeners = new ArrayList();
    private boolean conductionAllowed;
    private Shape dopantRect;
    private double macroSpeed = 0;

    public CircuitSection( SemiconductorApplication application, ModelViewTransform2D transform, double x, double y, double width, double height, int numDopantRegions ) throws IOException {
        this.application = application;
        this.transform = transform;
        double resistorThickness = 1;
        circuit = new MacroCircuit( x, y, width, height, resistorThickness );
        circuitGraphic = new MacroCircuitGraphic( circuit, transform );

        double dx = .5;
        double length = circuit.getLength();
        int numParticles = (int) ( length / dx + 1 );
        double particleX = 0;
        for ( int i = 0; i < numParticles; i++ ) {
            WireParticle p = new WireParticle( particleX, circuit );
            particles.add( p );
            Graphic wireParticleGraphic = new WireParticleGraphic( p, transform, MacroCircuitGraphic.getParticleImage() );
            particleGraphics.add( wireParticleGraphic );
            particleX += dx;
        }

        batterySpinner = new BatterySpinner( circuit.getBattery() );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                relayoutBatterySpinner();
                relayoutClearButton();
            }
        } );
        jb = new JButton( SimStrings.get( "CircuitSection.ClearButton" ) ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke( new BasicStroke( 1 ) );
                super.paintComponent( g );
            }
        };
        jb.setEnabled( false );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearDopants();
            }
        } );
        addDopantSlots( numDopantRegions );
    }

    private void clearDopantSlots() {
        dopantSlots.clear();
    }

    private void addDopantSlots( int numDopantRegions ) {
        Color slotColor = ( new Color( 220, 155, 225 ) );
//        Color slotColor=Color.yellow;
        for ( int i = 0; i < numDopantRegions; i++ ) {
            //get shape for the dopant.
            Shape dopantSlotShape = getDopantSlotShape( i, numDopantRegions );
//            System.out.println( "dopantSlotShape = " + dopantSlotShape );
            DopantSlot ds = null;
            try {
                ds = new DopantSlot( null, dopantSlotShape, transform, slotColor );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            dopantSlots.add( ds );
        }
    }

    private void clearDopants() {
        for ( int i = 0; i < numDopantSlots(); i++ ) {
            dopantSlotAt( i ).setDopantType( null );
        }
//        updateCircuitListeners();
        updateDopantChangeListeners();
        jb.setEnabled( false );
    }

    public Shape getDopantSlotShape( int index, int numDopantSlots ) {
        index++;
        double resLen = circuit.getResistor().getLength();
        double dist = index * 2 - 1;
        double maxDist = numDopantSlots * 2;
        double myfrac = dist / maxDist;
        double segmentWidth = resLen / numDopantSlots;
        PhetVector center = circuit.getResistor().getLocation( myfrac * resLen );
        double height = circuit.getResistor().getHeight();

        RoundRectangle2D.Double rect = createCenteredRect( center, segmentWidth, height, .3, .3 );
//        System.out.println( "x=" + rect.getX() + ", y=" + rect.getY() + ", width=" + rect.getWidth() + ", h=" + rect.getNumFilledLevels() );
        return rect;
    }

    private RoundRectangle2D.Double createCenteredRect( PhetVector center, double width, double height, double dx, double dy ) {
        return new RoundRectangle2D.Double( center.getX() - width / 2, center.getY() - height / 2, width, height, dx, dy );
    }

    public int numDopantSlots() {
        return dopantSlots.size();
    }

    public DopantSlot dopantSlotAt( int i ) {
        return (DopantSlot) dopantSlots.get( i );
    }

    private void relayoutClearButton() {
        Point viewPtRes = transform.modelToView( circuit.getResistor().getStartPosition() );
        jb.setBounds( viewPtRes.x, viewPtRes.y - 75, jb.getPreferredSize().width, jb.getPreferredSize().height );
    }

    public void stepInTime( double v ) {
        if ( conductionAllowed ) {
            for ( int i = 0; i < particles.size(); i++ ) {
                WireParticle wireParticle = (WireParticle) particles.get( i );
                wireParticle.setSpeed( getMacroSpeed() );
                wireParticle.stepInTime( v );
            }
        }
    }

    private double getMacroSpeed() {
        return macroSpeed;
    }

    public double getSpeed() {
        double volts = getCircuit().getBattery().getVoltage();
        double scale = 1.0 / 15.0;
//        double scale=1.0/25.0;
        double speed = volts * scale;
        if ( speed >= 0 && speed < .021 ) {
            speed = .021;
        }
        if ( speed <= 0 && speed > -.021 ) {
            speed = .021;
        }
        if ( speed < 0 ) {
            speed = -speed;
        }
//        System.out.println("Volts="+volts+", speed = " + speed);
        return speed;
    }

    private void relayoutBatterySpinner() {
        Point viewPtBatt = transform.modelToView( circuit.getBattery().getEndPosition() );
        JSpinner batterySp = this.batterySpinner.getSpinner();
        batterySp.setBounds( viewPtBatt.x, viewPtBatt.y + batterySp.getPreferredSize().height - 75, batterySp.getPreferredSize().width, batterySp.getPreferredSize().height );
        batterySp.invalidate();
        batterySp.validate();
        batterySp.doLayout();
        batterySp.revalidate();
        batterySp.repaint();
    }

    public void paint( Graphics2D graphics2D ) {
        circuitGraphic.paint( graphics2D );
//        dopantGraphic.paint(graphics2D);
        for ( int i = 0; i < dopantSlots.size(); i++ ) {
            DopantSlot ds = dopantSlotAt( i );
            ds.paint( graphics2D );
        }
        for ( int i = 0; i < particleGraphics.size(); i++ ) {
            Graphic graphic = (Graphic) particleGraphics.get( i );
            graphic.paint( graphics2D );
        }
    }

    public MacroCircuit getCircuit() {
        return circuit;
    }

    public BatterySpinner getBatterySpinner() {
        return batterySpinner;
    }

    public void addDopantChangeListener( DopantChangeListener dcl ) {
        dopantChangeListeners.add( dcl );
    }

    public void dopantDropped( DopantGraphic dopant ) {
        DopantSlot closest = null;
        double closestDist = 0;
        dopantRect = dopant.getShape();//the guy being dropped
        for ( int i = 0; i < numDopantSlots(); i++ ) {
            DopantSlot ds = dopantSlotAt( i );
            Shape shape = ds.getViewShape();
            double dist = dopant.getCenter().getSubtractedInstance( getCenter( shape ) ).getMagnitude();
            boolean ok = dopantSlotAt( i ).getViewShape().intersects( dopantRect.getBounds2D() );
            if ( ok && ( closest == null || dist < closestDist ) ) {
                closest = ds;
                closestDist = dist;
            }
        }
        if ( closest != null ) {
            closest.setDopantType( dopant.getType() );
//            updateCircuitListeners();
            application.removeDopantGraphic( dopant );
            jb.setEnabled( true );
            updateDopantChangeListeners();
        }
    }

    PhetVector getCenter( Shape s ) {
        Rectangle2D r = s.getBounds2D();
        return RectangleUtils.getCenter( r );
    }

    private void updateDopantChangeListeners() {
        for ( int i = 0; i < dopantChangeListeners.size(); i++ ) {
            DopantChangeListener listener = (DopantChangeListener) dopantChangeListeners.get( i );
            listener.dopingChanged( this );
        }
    }

//    private void updateCircuitListeners() {
//        for( int i = 0; i < circuitListeners.size(); i++ ) {
//            CircuitListener circuitListener = (CircuitListener)circuitListeners.get( i );
//            circuitListener.circuitChanged( this );
//        }
//    }

    public JButton getClearDopantButton() {
        return jb;
    }

    public void setConductionAllowed( boolean allowed ) {
        this.conductionAllowed = allowed;
    }

    public void setSingleSection() {
        setSectionCount( 1 );
    }

    public void setDoubleSection() {
        setSectionCount( 2 );
    }

    public void setTripleSection() {
        setSectionCount( 3 );
    }

    private void setSectionCount( int count ) {
//        clearDopants();
        clearDopantSlots();
        addDopantSlots( count );
    }

    public void setMacroSpeed( double avg ) {
        this.macroSpeed = avg;
    }
}
