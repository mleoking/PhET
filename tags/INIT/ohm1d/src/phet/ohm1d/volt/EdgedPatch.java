//package phet.ohm1d.volt;
//
//import phet.projects.*;
//import phet.projects.forces.*;
//import phet.ohm1d.applets.hollywood.*;
//
//public class EdgedPatch implements WireRegion
//{
//    WirePatch region;
//    int leftInset;
//    int rightInset;
//    public EdgedPatch(WirePatch region,int leftInset,int rightInset)
//    {
//        this.leftInset=leftInset;
//        this.rightInset=rightInset;
//	this.region=region;
//    }
//    public boolean contains(WireParticle wp)
//    {
//        if (!wp.getWirePatch()==region)
//        return false;
//        double loc=wp.getPosition();
//        if (loc<min)
//            return false;
//        if (loc>max)
//            return false;
//        return true;
//	    }
//}
