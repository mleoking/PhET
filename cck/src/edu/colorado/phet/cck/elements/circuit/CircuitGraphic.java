/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.circuit;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.branch.AbstractBranchGraphic;
import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchGraphicFactory;
import edu.colorado.phet.cck.elements.branch.components.*;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.CompositeTransformListener;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 1:54:00 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public class CircuitGraphic extends CompositeInteractiveGraphic implements CircuitObserver, TransformListener {
    Circuit circuit;

    CCK2Module module;
    CompositeTransformListener compositeTransformListener = new CompositeTransformListener();
    Hashtable graphicsTable = new Hashtable();  //key =branch, value=graphic

    BranchGraphicFactory graphicFactory;

    public CircuitGraphic( Circuit circuit, CCK2Module module ) {
        this.graphicFactory = module.getLifelikeGraphicFactory();
//        this.graphicFactory = module.getSchematicGraphicFactory();
        this.circuit = circuit;
        this.module = module;
        circuit.addCircuitObserver( this );
        module.getTransform().addTransformListener( this );
    }

    public AbstractBranchGraphic createBranchGraphic( Branch branch ) {
        AbstractBranchGraphic cbg = null;
//        cbg=branch.createGraphic();
        if( branch instanceof Resistor ) {               //TODO this code could be associated with each element.
            cbg = graphicFactory.getResistorGraphic( branch );//circuit2, module.getTransform(), branch, module, image);
        }
        else if( branch instanceof Battery ) {
            cbg = graphicFactory.getBatteryGraphic( (Battery)branch );//circuit2, module.getTransform(), (Battery) branch, module, image);
        }
        else if( branch instanceof Bulb ) {
            cbg = graphicFactory.getBulbGraphic( (Bulb)branch );//circuit2, module.getTransform(), branch, module, image);
        }
        else if( branch instanceof Wire ) {
            cbg = graphicFactory.getWireGraphic( branch );//circuit2, module.getTransform(), branch, module);
        }
        else if( branch instanceof Switch ) {
//            Switch swit=(Switch) branch;
            cbg = graphicFactory.getSwitchGraphic( (Switch)branch );//circuit2, module.getTransform(), branch, module, module.getBaseSwitchImage());
//            DefaultCompositeBranchGraphic dc=(DefaultCompositeBranchGraphic) cbg;
//            SwitchGraphic sg=(SwitchGraphic) dc.getAbstractBranchGraphic();
////            sg.update();
//            sg.setOpen(swit.isOpen());
        }
        else if( branch instanceof AmmeterBranch ) {
            cbg = graphicFactory.getAmmeterBranchGraphic( (AmmeterBranch)branch );
        }
        else {
            throw new RuntimeException( "Branch type not supported: " + branch.getClass() );
        }
        return cbg;
    }

    public void branchAdded( Circuit circuit2, Branch branch ) {
        AbstractBranchGraphic cbg = createBranchGraphic( branch );
        addGraphic( cbg, 0 );
        graphicsTable.put( branch, cbg );
        compositeTransformListener.addTransformListener( cbg );
        module.repaint();
    }

    public AbstractBranchGraphic[] getBranchGraphics() {
        return (AbstractBranchGraphic[])graphicsTable.values().toArray( new AbstractBranchGraphic[0] );
    }

    public LeadWireConnection getVoltageVertex( Shape dvmTip, Junction pleaseDoNotUse ) {
        AbstractBranchGraphic[] dc = getBranchGraphics();
        for( int i = 0; i < dc.length; i++ ) {
            AbstractBranchGraphic defaultCompositeBranchGraphic = dc[i];
            Shape start = defaultCompositeBranchGraphic.getStartWireShape();
            Shape end = defaultCompositeBranchGraphic.getEndWireShape();
            Area a = new Area( dvmTip );
            a.intersect( new Area( start ) );
            Area b = new Area( dvmTip );
            b.intersect( new Area( end ) );
            //The voltage vertex is the closer one.
            if( a.isEmpty() && b.isEmpty() ) {
            }
            else if( !a.isEmpty() && b.isEmpty() ) {
                double dist = getDistFromVertex( a, defaultCompositeBranchGraphic, true );
                return new LeadWireConnection( defaultCompositeBranchGraphic, true, dist );
            }
            else if( !b.isEmpty() && a.isEmpty() ) {
                double dist = getDistFromVertex( a, defaultCompositeBranchGraphic, true );
                return new LeadWireConnection( defaultCompositeBranchGraphic, false, dist );
            }
            else if( !a.isEmpty() && !b.isEmpty() ) {//should get called for wires.
                Junction startJ = defaultCompositeBranchGraphic.getBranch().getStartJunction();
                Junction endJ = defaultCompositeBranchGraphic.getBranch().getEndJunction();
                double distA = getDistFromVertex( a, defaultCompositeBranchGraphic, true );
                double distB = getDistFromVertex( b, defaultCompositeBranchGraphic, false );
                if( pleaseDoNotUse == startJ || startJ.hasConnection( pleaseDoNotUse ) ) {
                    return new LeadWireConnection( defaultCompositeBranchGraphic, false, distB );
                }
                if( pleaseDoNotUse == endJ || endJ.hasConnection( pleaseDoNotUse ) ) {
                    return new LeadWireConnection( defaultCompositeBranchGraphic, true, distA );
                }

//                System.out.println("distA="+dita);
//                System.out.println("distB = " + distB);
                if( distA < distB ) {
                    return new LeadWireConnection( defaultCompositeBranchGraphic, true, distA );
                }
                else {
                    return new LeadWireConnection( defaultCompositeBranchGraphic, false, distB );
                }
            }
        }
        return null;
    }

    public double getDistFromVertex( Area b, AbstractBranchGraphic defaultCompositeBranchGraphic, boolean startVertex ) {
        Rectangle2D bounds = b.getBounds2D();
        PhetVector centroid = new PhetVector( bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2 );
        Point2D.Double modelCentroid = module.getTransform().viewToModel( new Point( (int)centroid.getX(), (int)centroid.getY() ) );
        PhetVector modelCentroidVtr = new PhetVector( modelCentroid.x, modelCentroid.y );
        if( startVertex ) {
            PhetVector src = defaultCompositeBranchGraphic.getBranch().getStart();
            PhetVector dir = modelCentroidVtr.getSubtractedInstance( src );
            double dist = dir.getMagnitude();
            return dist;
        }
        else {
            PhetVector src = defaultCompositeBranchGraphic.getBranch().getEnd();
            PhetVector dir = modelCentroidVtr.getSubtractedInstance( src );
            double dist = dir.getMagnitude();
            return dist;
        }
    }

    public AbstractBranchGraphic getOverlappingElement( Point pt ) {
        AbstractBranchGraphic[] dc = getBranchGraphics();
        for( int i = 0; i < dc.length; i++ ) {
            AbstractBranchGraphic defaultCompositeBranchGraphic = dc[i];
            Shape start = defaultCompositeBranchGraphic.getStartWireShape();
            Shape end = defaultCompositeBranchGraphic.getEndWireShape();
            if( start.contains( pt ) || end.contains( pt ) ) {
                return defaultCompositeBranchGraphic;
            }
        }
        return null;
    }

    public void branchRemoved( Circuit circuit2, Branch branch ) {
        AbstractBranchGraphic g = (AbstractBranchGraphic)graphicsTable.get( branch );
        removeGraphic( g );
        graphicsTable.remove( branch );
        module.repaint();
    }

    public void connectivityChanged( Circuit circuit2 ) {
    }

    public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
        compositeTransformListener.transformChanged( ModelViewTransform2D );
        module.repaint();
    }

    public AbstractBranchGraphic getGraphic( Branch newElm ) {
        return (AbstractBranchGraphic)graphicsTable.get( newElm );
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public void removeGraphic( Branch b ) {
        AbstractBranchGraphic abg = (AbstractBranchGraphic)graphicsTable.get( b );
//        System.out.println("graphicsTable(before removal) = " + graphicsTable);
        graphicsTable.remove( b );
//        module.getApparatusPanel().removeGraphic(abg);
        removeGraphic( abg );
//        System.out.println("module.getApparatusPanel().getCompositeGraphic() = " + module.getApparatusPanel().getCompositeGraphic());
//        System.out.println("graphicsTable = " + graphicsTable);
    }

    public void removeAllGraphics() {
        while( graphicsTable.size() > 0 ) {
            Branch b = (Branch)graphicsTable.keySet().iterator().next();
            removeGraphic( b );
        }
    }

    public void setBranchGraphicFactory( BranchGraphicFactory gf ) {
        if( gf == graphicFactory ) {
            return;
        }
        this.graphicFactory = gf;
        removeAllGraphics();
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            branchAdded( circuit, circuit.branchAt( i ) );
        }
    }

    public void setLifelikeWireColor( Color color ) {
        AbstractBranchGraphic[] gr = getBranchGraphics();
        for( int i = 0; i < gr.length; i++ ) {
            AbstractBranchGraphic abstractBranchGraphic = gr[i];
            abstractBranchGraphic.setWireColor( color );
        }
    }

}
