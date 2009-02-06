package edu.colorado.phet.ohm1d.volt;

import edu.colorado.phet.ohm1d.gui.VoltageListener;

public class BatteryDirectionChanger implements VoltageListener {
    BatteryPainter bp;

    public BatteryDirectionChanger( BatteryPainter bp ) {
        this.bp = bp;
    }

    public void valueChanged( double val ) {
        if ( val < 0 ) {
            bp.setLeft( true );
        }
        else {
            bp.setLeft( false );
        }
    }

}

///**
// * Created by IntelliJ IDEA.
// * User: Sam Reid
// * Date: Nov 7, 2002
// * Time: 8:01:17 PM
// * To change this template use Options | File Templates.
// */
//package phet.ohm1d.volt;
//
//import phet.ohm1d.gui.VoltageListener;
//import phet.paint.Painter;
//import phet.paint.LayeredPainter;
//
//public class BatteryDirectionChanger implements VoltageListener
// {
//    Painter left;
//    Painter right;
//    Painter current;
//    LayeredPainter lp;
//    int layer;
//    public BatteryDirectionChanger(Painter left,Painter right, LayeredPainter lp,int layer)
//    {
//        this.left=left;
//        this.right=right;
//        this.lp = lp;
//        this.layer=layer;
//    }
//    public void coreCountChanged(double val) {
//          if (val<0&&current!=left)
//          {
//              lp.removePainter(right,layer);
//             lp.removePainter(left,layer);
//              lp.addPainter(left,layer) ;
//          }
//         else if (val>=0&&current!=right)
//         {
//             lp.removePainter(left,layer);
//             lp.removePainter(right,layer);
//             lp.addPainter(right,layer)  ;
//         }
//    }
//}
