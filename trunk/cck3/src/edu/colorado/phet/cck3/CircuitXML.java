package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.grabbag.GrabBagResistor;
import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.cck3.model.Circuit;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.cck3.model.components.*;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 14, 2006
 * Time: 1:51:09 AM
 * Copyright (c) Sep 14, 2006 by Sam Reid
 */

public class CircuitXML {

    public static Circuit parseXML( IXMLElement xml, CircuitChangeListener kl, CCKModule module ) {
        Circuit cir = new Circuit( kl );
        for( int i = 0; i < xml.getChildrenCount(); i++ ) {
            IXMLElement child = xml.getChildAtIndex( i );
            //            int index = child.getAttribute( "index", -1 );
            if( child.getName().equals( "junction" ) ) {
                String xStr = child.getAttribute( "x", "0.0" );
                String yStr = child.getAttribute( "y", "0.0" );
                double x = Double.parseDouble( xStr );
                double y = Double.parseDouble( yStr );
                Junction j = new Junction( x, y );
                cir.addJunction( j );
            }
            else if( child.getName().equals( "branch" ) ) {
                int startIndex = child.getAttribute( "startJunction", -1 );
                int endIndex = child.getAttribute( "endJunction", -1 );
                Junction startJunction = cir.junctionAt( startIndex ); //this only works if everything stays in order.
                Junction endJunction = cir.junctionAt( endIndex );
                Branch branch = toBranch( module, kl, startJunction, endJunction, child );
                cir.addBranch( branch );
            }
        }
        return cir;
    }

    public static Branch toBranch( CCKModule module, CircuitChangeListener kl, Junction startJunction, Junction endJunction, IXMLElement xml ) {
        String type = xml.getAttribute( "type", "null" );
        if( type.equals( Branch.class.getName() ) ) {
            Branch branch = new Branch( kl, startJunction, endJunction );
            return branch;
        }
        double length = Double.parseDouble( xml.getAttribute( "length", "-1" ) );
        double height = Double.parseDouble( xml.getAttribute( "height", "-1" ) );

        if( type.equals( Resistor.class.getName() ) ) {
            Resistor res = new Resistor( kl, startJunction, endJunction, length, height );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            res.setResistance( val );
            return res;
        }
        else if( type.equals( ACVoltageSource.class.getName() ) ) {
            double amplitude = Double.parseDouble( xml.getAttribute( "amplitude", Double.NaN + "" ) );
            double freq = Double.parseDouble( xml.getAttribute( "frequency", Double.NaN + "" ) );
            double internalResistance = Double.parseDouble( xml.getAttribute( "internalResistance", Double.NaN + "" ) );
            ACVoltageSource voltageSource = new ACVoltageSource( kl, startJunction, endJunction, length, height, CCKModel.MIN_RESISTANCE, module.isInternalResistanceOn() );
            voltageSource.setInternalResistance( internalResistance );
            voltageSource.setAmplitude( amplitude );
            voltageSource.setFrequency( freq );
            return voltageSource;
        }
        else if( type.equals( Capacitor.class.getName() ) ) {
            Capacitor capacitor = new Capacitor( kl, startJunction, endJunction, length, height );
            capacitor.setVoltageDrop( Double.parseDouble( xml.getAttribute( "voltage", Double.NaN + "" ) ) );
            capacitor.setCurrent( Double.parseDouble( xml.getAttribute( "current", Double.NaN + "" ) ) );
            capacitor.setCapacitance( Double.parseDouble( xml.getAttribute( "capacitance", Double.NaN + "" ) ) );
            return capacitor;
        }
        else if( type.equals( Battery.class.getName() ) ) {
//            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
//            double resistance = Double.parseDouble( resVal );

            double internalResistance = Double.parseDouble( xml.getAttribute( "internalResistance", Double.NaN + "" ) );
            //            String internalResistanceOnStr = xml.getAttribute( "connectAtRight", "false" );
            //            boolean internalResistanceOn = internalResistanceOnStr != null && internalResistanceOnStr.equals( new Boolean( true ).toString() );
            Battery batt = new Battery( kl, startJunction, endJunction, length, height, CCKModel.MIN_RESISTANCE, module.isInternalResistanceOn() );
            batt.setInternalResistance( internalResistance );
            String voltVal = xml.getAttribute( "voltage", Double.NaN + "" );
            double val = Double.parseDouble( voltVal );
            batt.setVoltageDrop( val );
            return batt;
        }
        else if( type.equals( Switch.class.getName() ) ) {
            String closedVal = xml.getAttribute( "closed", "false" );
            boolean closed = closedVal != null && closedVal.equals( new Boolean( true ).toString() );
            //            boolean closed = Boolean.getBoolean( closedVal );
            return new Switch( kl, startJunction, endJunction, closed, length, height );
        }
        else if( type.equals( Bulb.class.getName() ) ) {
            String widthStr = xml.getAttribute( "width", Double.NaN + "" );
            double width = Double.parseDouble( widthStr );
            boolean schematic = !module.getCircuitGraphic().isLifelike();
            Bulb bulb = new Bulb( kl, startJunction, endJunction, width, length, height, schematic );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            bulb.setResistance( val );
            String connectAtRightStr = xml.getAttribute( "connectAtRight", "true" );
            boolean connectAtRight = connectAtRightStr != null && connectAtRightStr.equals( new Boolean( true ).toString() );
            bulb.setConnectAtRightXML( connectAtRight );
            return bulb;
        }
        else if( type.equals( SeriesAmmeter.class.getName() ) ) {
            return (Branch)new SeriesAmmeter( kl, startJunction, endJunction, length, height );
        }
        else if( type.equals( GrabBagResistor.class.getName() ) ) {
            Resistor res = new Resistor( kl, startJunction, endJunction, length, height );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            res.setResistance( val );
            return res;
        }
        else if( type.equals( Inductor.class.getName() ) ) {
            Inductor inductor = new Inductor( kl, startJunction, endJunction, length, height );
            inductor.setVoltageDrop( Double.parseDouble( xml.getAttribute( "voltage", Double.NaN + "" ) ) );
            inductor.setCurrent( Double.parseDouble( xml.getAttribute( "current", Double.NaN + "" ) ) );
            inductor.setInductance( Double.parseDouble( xml.getAttribute( "inductance", Double.NaN + "" ) ) );
            return inductor;
        }
        return null;
    }

    public static XMLElement toXML( Circuit circuit ) {
        XMLElement xe = new XMLElement( "circuit" );
        for( int i = 0; i < circuit.numJunctions(); i++ ) {
            Junction j = circuit.junctionAt( i );
            XMLElement junctionElement = new XMLElement( "junction" );
            junctionElement.setAttribute( "index", i + "" );
            junctionElement.setAttribute( "x", j.getPosition().getX() + "" );
            junctionElement.setAttribute( "y", j.getPosition().getY() + "" );
            xe.addChild( junctionElement );
        }
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            XMLElement branchElement = new XMLElement( "branch" );
            Junction startJ = branch.getStartJunction();
            Junction endJ = branch.getEndJunction();
            int startIndex = circuit.indexOf( startJ );
            int endIndex = circuit.indexOf( endJ );
            branchElement.setAttribute( "index", "" + i );
            branchElement.setAttribute( "type", branch.getClass().getName() );
            branchElement.setAttribute( "startJunction", startIndex + "" );
            branchElement.setAttribute( "endJunction", endIndex + "" );
            if( branch instanceof CircuitComponent ) {
                CircuitComponent cc = (CircuitComponent)branch;
                branchElement.setAttribute( "length", cc.getLength() + "" );
                branchElement.setAttribute( "height", cc.getHeight() + "" );
            }
            if( branch instanceof ACVoltageSource ) {
                ACVoltageSource batt = (ACVoltageSource)branch;
                branchElement.setAttribute( "amplitude", batt.getAmplitude() + "" );
                branchElement.setAttribute( "frequency", batt.getFrequency() + "" );
                branchElement.setAttribute( "internalResistance", batt.getInteralResistance() + "" );
            }
            else if( branch instanceof Battery ) {
                Battery batt = (Battery)branch;
                branchElement.setAttribute( "voltage", branch.getVoltageDrop() + "" );
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
                branchElement.setAttribute( "internalResistance", batt.getInteralResistance() + "" );
            }
            else if( branch instanceof Resistor ) {
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
            }
            else if( branch instanceof Bulb ) {
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
                Bulb bulb = (Bulb)branch;
                branchElement.setAttribute( "width", bulb.getWidth() + "" );
                branchElement.setAttribute( "length", branch.getStartJunction().getDistance( branch.getEndJunction() ) + "" );
                branchElement.setAttribute( "schematic", bulb.isSchematic() + "" );
                branchElement.setAttribute( "connectAtRight", bulb.isConnectAtRight() + "" );
            }
            else if( branch instanceof Switch ) {
                Switch sw = (Switch)branch;
                branchElement.setAttribute( "closed", sw.isClosed() + "" );
            }
            else if( branch instanceof SeriesAmmeter ) {
            }
            else if( branch instanceof Capacitor ) {
                Capacitor cap = (Capacitor)branch;
                branchElement.setAttribute( "capacitance", cap.getCapacitance() + "" );
                branchElement.setAttribute( "voltage", cap.getVoltageDrop() + "" );
                branchElement.setAttribute( "current", cap.getCurrent() + "" );
            }
            else if( branch instanceof Inductor ) {
                Inductor ind = (Inductor)branch;
                branchElement.setAttribute( "inductance", ind.getInductance() + "" );
                branchElement.setAttribute( "voltage", ind.getVoltageDrop() + "" );
                branchElement.setAttribute( "current", ind.getCurrent() + "" );
            }
            xe.addChild( branchElement );
        }
        try {
            new XMLWriter( System.out ).write( xe );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return xe;
    }

}
