/**
 * Class: LaserGI
 * Class: edu.colorado.phet.lasers.guidedinquiry
 * User: Ron LeMaster
 * Date: Apr 30, 2003
 * Time: 7:54:52 AM
 */
package edu.colorado.phet.lasers.guidedinquiry;

import edu.colorado.phet.guidedinquiry.model.*;
import edu.colorado.phet.guidedinquiry.controller.command.LaunchGuidedInquiryCmd;
import edu.colorado.phet.guidedinquiry.controller.GIControlPanel;
import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.lasers.controller.LaserApplication;
import edu.colorado.phet.lasers.view.SingleAtomBaseApparatusPanel;
import edu.colorado.phet.lasers.view.OneAtomTwoLevelsApparatusPanel;
import edu.colorado.phet.lasers.view.MultipleAtomThreeLevelApparatusPanel;

import java.util.ArrayList;

public class LaserGI extends GuidedInquiry {

    public LaserGI() {

         this.addExercise( createExercise1() );
         this.addExercise( createExercise2() );
     }

     private Exercise createExercise1() {
         // Create a question, exercise, etc., etc.
         String t = "Who is the style master of the Western World?";
         Answer a1 = new Answer( "Giorgio Armani");
         Answer a2 = new Answer( "Coco Channel");
         Answer a3 = new Answer( "Michael Dubson");
         ArrayList aList = new ArrayList();
         aList.add( a1 );
         aList.add( a2 );
         aList.add( a3 );
         Question q = new Question( t, aList, a3 );

         ApparatusConfig ac = new ApparatusConfig( new OneAtomTwoLevelsApparatusPanel() );
         Exercise e = new Exercise( ac, q );
         return e;
     }

     private Exercise createExercise2() {
         // Create a question, exercise, etc., etc.
         String t = "Who will receive the Rookie Teacher of the \nYear Award for 2002-2003 at the \nUniversity of Colorado?";
         Answer a1 = new Answer( "Ronald McDonald");
         Answer a2 = new Answer( "Gov. Bill Owens");
         Answer a3 = new Answer( "Katherine Perkins");
         ArrayList aList = new ArrayList();
         aList.add( a1 );
         aList.add( a2 );
         aList.add( a3 );
         Question q = new Question( t, aList, a3 );

         ApparatusConfig ac = new ApparatusConfig( new MultipleAtomThreeLevelApparatusPanel() );
         Exercise e = new Exercise( ac, q );
         return e;
     }

     public void conduct( GuidedInquiry gi ) {
         LaunchGuidedInquiryCmd lgiCmd = new LaunchGuidedInquiryCmd( gi );
         lgiCmd.doIt();
     }

     public static void main( String[] args ) {

         LaserApplication application = new LaserApplication();
         application.start();

         GIControlPanel controlPanel = new GIControlPanel( "http://colorado.edu" );
         PhetApplication.instance().getPhetMainPanel().setApplicationControlPanel( controlPanel );
     }
}
