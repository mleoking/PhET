package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Bulb;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolox.nodes.PClip;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 3, 2006
 * Time: 11:33:13 PM
 * Copyright (c) Oct 3, 2006 by Sam Reid
 */

//public class BulbTopLayer extends PPath {
public class BulbTopLayer extends PClip {
    private Bulb bulb;
    private TotalBulbComponentNode totalBulbComponentNode;
//    private PPath debugChild;

    public BulbTopLayer( CCKModel cckModel, Bulb bulb, JComponent component, ICCKModule module ) {
        this.bulb = bulb;
        totalBulbComponentNode = new TotalBulbComponentNode( cckModel, bulb, component, module );
        totalBulbComponentNode.removeFilamentNode();
        totalBulbComponentNode.getBulbComponentNode().getBulbNode().setShowCoverOnly();

        addChild( totalBulbComponentNode );
        setStroke( null );
        bulb.addObserver( new SimpleObserver() {
            public void update() {
                BulbTopLayer.this.update();
            }
        } );
        update();
//        debugChild = new PhetPPath( Color.green);
//        addChild( debugChild );
    }

    private void update() {
        if( getParent() != null ) {
            Shape conductorShape = totalBulbComponentNode.getBulbComponentNode().getBulbNode().getCoverShapeOnFilamentSide();
            PAffineTransform a = totalBulbComponentNode.getBulbComponentNode().getBulbNode().getLocalToGlobalTransform( null );
            PAffineTransform b = getParent().getGlobalToLocalTransform( null );
            conductorShape = a.createTransformedShape( conductorShape );
            conductorShape = b.createTransformedShape( conductorShape );
            System.out.println( "conductorShape.getBounds2D() = " + conductorShape.getBounds2D() );
            setPathTo( conductorShape );

//            debugChild.setPathTo( conductorShape );
        }
    }

    public Branch getBranch() {
        return bulb;
    }
}
