//package edu.colorado.phet.cck.piccolo_cck;
//
//import edu.colorado.phet.cck.ICCKModule;
//import edu.colorado.phet.cck.model.Circuit;
//import edu.colorado.phet.cck.model.components.Branch;
//import edu.colorado.phet.common_cck.util.SimpleObserver;
//
///**
// * User: Sam Reid
// * Date: Oct 8, 2006
// * Time: 8:51:44 PM
// * Copyright (c) Oct 8, 2006 by Sam Reid
// */
//
//public class EditingReadoutSetNode extends ReadoutSetNode {
//    public EditingReadoutSetNode( ICCKModule module, Circuit circuit ) {
//        super( module, circuit );
//    }
//
//    protected void removeBranchReadout( Branch branch ) {
//        //todo remove the observer?
//    }
//
//    protected ReadoutNode createReadoutNode( final Branch branch ) {
//        final ReadoutNode node = super.createReadoutNode( branch );
//        //todo do we need to delete this observer manually?
//        branch.addObserver( new SimpleObserver() {
//            public void update() {
//                updateNode( node, branch );
//            }
//        } );
//        updateNode( node, branch );
////        Timer timer = new Timer( 30, new ActionListener() {
////            public void actionPerformed( ActionEvent e ) {
////                System.out.println( "branch.geth=" + branch.hashCode() + ", vis=" + node.getVisible() );
////            }
////        } );
////        timer.start();
//        return node;
//    }
//
//    private void updateNode( ReadoutNode node, Branch branch ) {
//        System.out.println( "branch.isEditing() = " + branch.isEditing() );
//        node.setVisible( branch.isEditing() );
//    }
//}
