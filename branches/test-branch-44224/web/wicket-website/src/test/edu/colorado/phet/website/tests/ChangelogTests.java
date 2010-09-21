package edu.colorado.phet.website.tests;

import org.junit.Test;

import edu.colorado.phet.website.data.Changelog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangelogTests {
    @Test
    /**
     * Tests parsing of the correct changelog format
     */
    public void testCorrectFormat() {
        String logString = "# 3.22.00 (38666) Feb 16, 2010\n" +
                           "2/16/10 Deployment to production server.\n" +
                           "# 3.21.01 (38614) Feb 12, 2010\n" +
                           "2/12/10 > Fixed an issue where U-238 was shown decaying into Pb-207 instead of Pb-206.\n" +
                           "# 3.21.00 (38193) Jan 22, 2010\n" +
                           "1/22/10 Deployment to production server.";

        Changelog devLog = new Changelog( logString );
        System.out.println( "*** dev log: \n" + devLog + "\n\n" );

        Changelog log = devLog.getNonDevChangelog();
        System.out.println( "*** non-dev log: \n" + log + "\n\n" );

        /*---------------------------------------------------------------------------*
        * development log tests
        *----------------------------------------------------------------------------*/

        assertTrue( devLog.getEntries().size() == 3 );
        Changelog.Entry dev1 = devLog.getEntries().get( 0 );
        assertTrue( dev1.getMajorVersion() == 3 );
        assertTrue( dev1.getDevVersion() == 0 );
        assertTrue( dev1.getRevision() == 38666 );
        assertTrue( dev1.getDate() != null );
        assertTrue( dev1.getLines().size() == 1 );
        assertTrue( !dev1.getLines().get( 0 ).isVisible() );
        assertTrue( dev1.getLines().get( 0 ).getMessage().equals( "Deployment to production server." ) );
        Changelog.Entry dev2 = devLog.getEntries().get( 1 );
        assertTrue( dev2.getLines().size() == 1 );
        Changelog.Line devE = dev2.getLines().get( 0 );
        assertTrue( devE.isVisible() );
        assertTrue( devE.getDate() != null );
        assertTrue( devE.getMessage().equals( "Fixed an issue where U-238 was shown decaying into Pb-207 instead of Pb-206." ) );

        /*---------------------------------------------------------------------------*
        * visible log tests
        *----------------------------------------------------------------------------*/

        assertTrue( log.getEntries().size() == 2 );
        Changelog.Entry e1 = devLog.getEntries().get( 0 );
        assertTrue( e1.getMajorVersion() == 3 );
        assertTrue( e1.getDevVersion() == 0 );
        assertTrue( e1.getRevision() == 38666 );
        assertTrue( e1.getDate() != null );
        assertTrue( e1.getLines().size() == 1 );
        for ( Changelog.Entry entry : log.getEntries() ) {
            assertTrue( entry.getDevVersion() == 0 );
            for ( Changelog.Line line : entry.getLines() ) {
                assertTrue( line.isVisible() );
            }
        }

    }

    @Test
    /**
     * Tests parsing of weird changelog formats
     */
    public void testUglyFormat() {
        String logString = "# 2.04.00 (40105) Jan 10, 2010\n" +
                           "1/10/10 > Something visible\n" +
                           "1/10/10 >Something visible\n" +
                           "1/10/10 Something invisible\n" +
                           "> Another visible thing\n" +
                           ">Yet another visible thing\n" +
                           "Something invisible\n" +
                           "\n" +
                           "\n" +
                           "# 2.02.03 (28282) 02-09-2009\n" +
                           "# 1.01.11 12-31-2008\n" +
                           "# 1.01.05\n" +
                           "#### I AM A COMMENT BECAUSE I HAVE TOO MANY #s\n" +
                           "I AM A GARBAGE LINE";

        Changelog devLog = new Changelog( logString );
        System.out.println( "*** dev log: \n" + devLog + "\n\n" );

        Changelog log = devLog.getNonDevChangelog();
        System.out.println( "*** non-dev log: \n" + log + "\n\n" );

        assertEquals( devLog.getEntries().size(), 4 );
        Changelog.Entry firstEntry = devLog.getEntries().get( 0 );
        assertEquals( firstEntry.getLines().size(), 6 );

        assertTrue( firstEntry.getLines().get( 0 ).getDate() != null );
        assertTrue( firstEntry.getLines().get( 0 ).isVisible() );
        assertTrue( firstEntry.getLines().get( 0 ).getMessage().equals( "Something visible" ) );

        assertTrue( firstEntry.getLines().get( 1 ).getDate() != null );
        assertTrue( firstEntry.getLines().get( 1 ).isVisible() );
        assertTrue( firstEntry.getLines().get( 1 ).getMessage().equals( "Something visible" ) );

        assertTrue( firstEntry.getLines().get( 2 ).getDate() != null );
        assertTrue( !firstEntry.getLines().get( 2 ).isVisible() );
        assertTrue( firstEntry.getLines().get( 2 ).getMessage().equals( "Something invisible" ) );

        assertTrue( firstEntry.getLines().get( 3 ).getDate() == null );
        assertTrue( firstEntry.getLines().get( 3 ).isVisible() );
        assertTrue( firstEntry.getLines().get( 3 ).getMessage().equals( "Another visible thing" ) );

        assertTrue( firstEntry.getLines().get( 4 ).getDate() == null );
        assertTrue( firstEntry.getLines().get( 4 ).isVisible() );
        assertTrue( firstEntry.getLines().get( 4 ).getMessage().equals( "Yet another visible thing" ) );

        assertTrue( firstEntry.getLines().get( 5 ).getDate() == null );
        assertTrue( !firstEntry.getLines().get( 5 ).isVisible() );
        assertTrue( firstEntry.getLines().get( 5 ).getMessage().equals( "Something invisible" ) );

        assertTrue( devLog.getEntries().get( 1 ).getDate() != null );
        assertTrue( devLog.getEntries().get( 2 ).getDate() != null );
    }

    @Test
    public void testParseCCKLog() {
        String logString = "# 3.18.00 (37980) Jan 8, 2010\n" +
                           "1/8/10 public version for milestone \"full redeploy, 2010 Q1\"\n" +
                           "# 3.17.24 (37754) Dec 29, 2009\n" +
                           "> Fixed a bug that made the circuit run very quickly while dragging voltage or resistance sliders, see #2040\n" +
                           "# 3.17.23 (37750) Dec 29, 2009\n" +
                           "Removed vertical struts to decrease height of control panel content, see #2039\n" +
                           "Put lifelike and schematic on the same line to reduce control panel height, see #2039\n" +
                           "> Improved layout in control panel to remove scroll bars in AC version.\n" +
                           "# 3.17.22 (37339) Dec 11, 2009\n" +
                           "12/11/09 test version for milestone \"full redeploy, 2010 Q1\"\n" +
                           "# 3.17.21 (36812) Nov 19, 2009\n" +
                           "Redeploy for testing, with phetcommon changes in clock control panel\n" +
                           "# 3.17.20 (36660) Nov 15, 2009\n" +
                           "Signify a circuit change and recompute physics when an element is added to the play area, resolves #1889\n" +
                           "Small refactors\n" +
                           "# 3.17.19 (36620) Nov 13, 2009\n" +
                           "> Fixed stopwatch to correspond to sim time (was off by 100x or so), see #359\n" +
                           "# 3.17.18 (36091) Oct 26, 2009\n" +
                           "> Fixed: advanced wire resistivity slider starts at incorrect slider position, resolves #1842\n" +
                           "> Made it so the switch graphic is shown as closed when loading a closed switch, resolves #384\n" +
                           "# 3.17.17 (36021) Oct 22, 2009\n" +
                           "> Fixed time scale for e-e repulsion to match scale as in 3.17.00, resolves #1828\n" +
                           "# 3.17.16 (35899) Oct 16, 2009\n" +
                           "> Added a workaround to help improve behavior for situations such as a battery connected directly to a capacitor, see #1813\n" +
                           "# 3.17.15 (35749) Oct 12, 2009\n" +
                           "# 3.17.14 (35746) Oct 12, 2009\n" +
                           "> Made it possible to change the background color using the menu options\n" +
                           "# 3.17.13 (33522) Jul 21, 2009\n" +
                           "# 3.17.12 (33462) Jul 19, 2009\n" +
                           "> Make default frame size 1024x768\n" +
                           "Removed PhET icon logo with weblink\n" +
                           "Increase font in play area to compensate for new size\n" +
                           "# 3.17.11 (33457) Jul 19, 2009\n" +
                           "> Rewrote voltage calculation to use node voltages\n" +
                           "# 3.17.10 (33455) Jul 19, 2009\n" +
                           "# 3.17.09 (33439) Jul 19, 2009\n" +
                           "7/19/09 made it so open switches are treated as OpenComponents, not resistors with high resistance, see #367\n" +
                           "# 3.17.08 (33030) Jul 5, 2009\n" +
                           "# 3.17.07 (32918) Jul 1, 2009\n" +
                           "# 3.17.06 (32767) Jun 26, 2009\n" +
                           "> Fixed a bug that prevented components (e.g. batteries) from being updated while machine locale and selected locale differ in decimal character, see #1700\n" +
                           "# 3.17.03 (32287) Jun 3, 2009\n" +
                           "6/3/09 Fixed a bug that prevented the model from updating.\n" +
                           "# 3.17.02 (32285) Jun 3, 2009\n" +
                           "6/3/09 Added battery internal resistance\n" +
                           "# 3.17.01 (32260) Jun 2, 2009\n" +
                           "Converted to Scala MNA implementation\n" +
                           "Added numbers and units back to chart time axes, see #1333\n" +
                           "# 3.17.00 (30589) Apr 20, 2009\n" +
                           "4/20/09 Batch deploy as part of IOM milestone with revision 30588\n" +
                           "# 3.16.05 (30174) Apr 1, 2009\n" +
                           "> Removed numbers and units from chart time axis, see #1333\n" +
                           "# 3.16.04 (29974) Mar 27, 2009\n" +
                           "3/27/09 #1526, incorporate Romanian sim translation\n" +
                           "# 3.16.03 (29914) Mar 26, 2009\n" +
                           "3/26/09 #1526, this version to be used for Romanian translations\n" +
                           "# 3.16.02 (29629) Mar 22, 2009\n" +
                           "3/22/09 Batch deploy for Alpha Simulation Tests 2, svn 29628\n" +
                           "# 3.16.01 (28780) Feb 19, 2009\n" +
                           "Batch deploy for alpha Simulation Tests, svn 28773\n" +
                           "#3.16.00 12-1-2008\n" +
                           "Deploying version to tigercat with tracking for testing purposes.\n" +
                           "# 3.15.04 11-29-2008\n" +
                           "Deploying version with tracking for feasibility tests.\n" +
                           "# 3.15.03 (26342) 11-19-2008\n" +
                           "Deploying version with tracking for feasibility tests. \n" +
                           "# 3.15.02 (26340) 11-19-2008\n" +
                           "Deploying version with tracking for feasibility tests. \n" +
                           "# 3.15.01 (26335) 11-19-2008\n" +
                           "Deploying version with tracking for feasibility tests.\n" +
                           "New in 3.15\n" +
                           "Published 3.14.01 on main site as 3.15\n" +
                           "\n" +
                           "New in 3.14.01\n" +
                           "Added right click help message\n" +
                           "\n" +
                           "10-8-2008: use PhetApplicationConfig.launchSim\n" +
                           "\n" +
                           "New in 3.13.24\n" +
                           "Fixed: AC Source: internal resistance display for AC voltage is incorrect (always zero)\n" +
                           "Reduced pencil lead resistance from 3000 to 300 Ohms\n" +
                           "Renamed Clear to Reset, moved out of File Save/Load panel\n" +
                           "Renamed File to Circuit in Save/Load panel\n" +
                           "\n" +
                           "10-15-2007: Fixed: \"Internal Resistance\" controls for battery are unavailable\n" +
                           "10-15-2007: Removed model-wide flag for internal-resistance; internal-resistance is now always available for each model component\n" +
                           "9-24-2007: Fixed: \"Advanced\" controls should be show/hide, not enable/disable\n" +
                           "9-24-2007: Fixed: Voltmeter sometimes shows \"-0.00 V\"\n" +
                           "7-25-2007: added initial support for loading saved cck files from previous web version\n" +
                           "\n" +
                           "7-17-2007\n" +
                           "Fixed a bug that prevented saving when the user chose a file extension other than .cck\n" +
                           "\n" +
                           "3.06.03 (6-6-2007)\n" +
                           "Added docs to the dev site.\n" +
                           "\n" +
                           "3.06 (12-20-2006)\n" +
                           "Added interationalization for toolbox strings.\n" +
                           "\n" +
                           "3.05 (12-20-2006)\n" +
                           "Posted on main site as 3.05\n" +
                           "\n" +
                           "3.04.15 (12-20-2006)\n" +
                           "Fixed a bug that prevented saving circuits from webstart.\n" +
                           "\n" +
                           "3.04.14\n" +
                           "Trial version to test save/load.\n" +
                           "\n" +
                           "3.04.13\n" +
                           "Trial version to test save/load.\n" +
                           "\n" +
                           "3.04.12 (12-20-2006)\n" +
                           "Reduced ammeter size.\n" +
                           "Moved switches to be in front of other components (Except for selected components.)\n" +
                           "Improved title font for component editors.\n" +
                           "\n" +
                           "3.04.11 (12-18-2006)\n" +
                           "Removed phetcommon tab, which was used for testing purposes.\n" +
                           "Clicking on the background de-selects the highlighted element (if any).\n" +
                           "Fixed: Connected wires look as if there is a gap between the copper part of the wires.\n" +
                           "Fixed: When dragging a junction in a connected circuit, there becomes a gap in the electron flow 2 components away.\n" +
                           "Fixed: When adjusting a voltage or resistance continuously with a slider, the circuit doesn't update until stopped dragging.\n" +
                           "Fixed: \"Show Connection at Right\" for bulb turns the bulb upside-down.\n" +
                           "Fixed: Bulb left/right directions are backwards.\n" +
                           "Converted \"show values\" checkbox to a pair of buttons (on control panel).\n" +
                           "Fixed: Sometimes readout is still displayed after its component is deleted.\n" +
                           "Fixed: Bulb rays are grabbable.\n" +
                           "Fixed: Zoom in/out incorrectly translates the play area.\n" +
                           "Fixed: Zoom in/out makes it possible to lose the voltmeter.\n" +
                           "Fixed: Number of significant digits in slider is different from on-component readout.\n" +
                           "Fixed: Virtual Ammeter doesn't update unless dragged.\n" +
                           "Fixed: AC Voltage source is off by a factor of 5.\n" +
                           "Fixed: AC Edit dialogs are initialized incorrectly.\n" +
                           "Renamed \"Frequency\" to \"Change Frequency\" in AC Voltage Source menu.\n" +
                           "Renamed \"Edit Capacitance\" to \"Change Capacitance\" in capacitor menu.\n" +
                           "Renamed \"Edit Inductance\" to \"Change Inductance\" in inductor menu.\n" +
                           "Removed Zoom handler.\n" +
                           "Voltage Strip chart probes now work correctly at different zoom levels.\n" +
                           "Fixed: Capacitor reads ohms instead of Farads.\n" +
                           "Fixed: Battery voltage editor has incorrect behavior when switching to larger range & back.\n" +
                           "Fixed: Capacitor editor initialized to wrong value.\n" +
                           "Fixed: Inductor reads ohms instead of Henries.\n" +
                           "Bugs fixed in 3.04.09 bug report [1-12, 14-15, 18, 21-24, 27, 30, 36]\n" +
                           "Bugs recommended but skipped [16, 26, 32, 33]  \n" +
                           "\n" +
                           "3.04.10 (12-12-2006)\n" +
                           "Added a launch file for DC Only version.\n" +
                           "\n" +
                           "3.04.09 (10-9-2006)\n" +
                           "Fixed grab bag button & toolbox so they don't obscure one another.\n" +
                           "\n" +
                           "3.04.08 (10-9-2006)\n" +
                           "Bugfix: Grab bag button obscured the toolbox at low resolution.\n" +
                           "\n" +
                           "3.04.07 (10-9-2006)\n" +
                           "Generalized the about dialog, added spanish, etc.\n" +
                           "\n" +
                           "3.04.04 (10-9-2006)\n" +
                           "Feasibility test for repainting problem workaround.\n" +
                           "\n" +
                           "3.04.03 (10-9-2006)\n" +
                           "Reduced bulb size 75% to match previous version.\n" +
                           "Reduced battery size and fixed aspect ratio to match previous version.\n" +
                           "Reduced voltmeter size to match previous version.\n" +
                           "Bugfix: right-click causes some components to jump around.\n" +
                           "Bugfix: single left-click causes components to jump around.\n" +
                           "\n" +
                           "3.04.01 (10-9-2006)\n" +
                           "Added a cursor hand to the phet logo, which links to main page.\n" +
                           "Added an improved about dialog, with license, projects, link to main page, and phet icon.\n" +
                           "\n" +
                           "3.04.00 (10-9-2006)\n" +
                           "Finished main body of piccolo translation, still needs thorough testing.\n" +
                           "\n" +
                           "3.03.06 (10-5-2006)\n" +
                           "Piccolo version includes most tools, most functionality.\n" +
                           "\n" +
                           "3.03.02 (9-20-2006)\n" +
                           "Partial completion of translation to Piccolo.\n" +
                           "\n" +
                           "3.02.02 (9-3-2006)\n" +
                           "Added local jnlp file implementation.\n" +
                           "Switched to Windows look and feel preferred over Ocean.\n" +
                           "Bugfix: AC Circuit with open switch crashes (see MNACircuit line 524)\n" +
                           "\n" +
                           "3.02.01 (8-21-2006)\n" +
                           "Renamed \"sp\" to \"es\" for internationalization.\n" +
                           "\n" +
                           "3.01.12\n" +
                           "Added \"(DC & AC)\" to the title.\n" +
                           "\n" +
                           "3.01.11 (7-10-2006)\n" +
                           "Added a Pause/Play/Step control panel.\n" +
                           "Moved the charges from the centerline of a capacitor, when there is only 1 column of charges.\n" +
                           "Bugfix: Stopwatch was intercepting mouse events (even when disabled).\n" +
                           "\n" +
                           "3.01.10 (7-10-2006) \n" +
                           "Added 'Amps' units to current readout.\n" +
                           "\n" +
                           "3.01.09 (7-10-2006)\n" +
                           "Improved graphics for lower resolution.\n" +
                           "Bugfix: Text for stopwatch is broken.\n" +
                           "\n" +
                           "3.01.08 (7-9-2006)\n" +
                           "Strip charts show a gap in the data when not taking data.\n" +
                           "Inductors auto-discharge when not in a loop.\n" +
                           "Capacitors\n" +
                           "    Capacitor charges are be blue for negative, red for positive.\n" +
                           "    Square plates are 3d\n" +
                           "    Area is a function of capacitance.\n" +
                           "Bugfix: In-circuit ammeter fails to move off the toolbar correctly.\n" +
                           "\n" +
                           "3.01.07 (7-7-2006)\n" +
                           "Improved graphics for the voltage strip chart.\n" +
                           "Strip charts start reading from the right.\n" +
                           "Strip charts maintain the correct time-range.\n" +
                           "\n" +
                           "3.01.06 (7-7-2006)\n" +
                           "Charts can now be removed.\n" +
                           "Bugfix: Charts don't show any data, when first data points are out of range.\n" +
                           "Bugfix: Time-series charts looked non-smooth for simple AC RC circuit\n" +
                           ">>We can no longer have simulation time dynamic because it destroys smoothness of the plots; converted to static.\n" +
                           "Added x and y axis labels for the stripcharts.\n" +
                           "Added the Voltage chart: a 2-terminal version of the current meter.\n" +
                           "Added a timer.\n" +
                           "\n" +
                           "3.01.05 (7-5-2006)\n" +
                           "Bugfix: When you charge a capacitor and completely disconnect if, the capacitor loses all its charge.\n" +
                           "Added readout for inductors.\n" +
                           "Added component editor for inductors.\n" +
                           "Bugfix: 'Advanced' border title disappears on control panel when expanding the advanced panel.\n" +
                           "Added schematic view for inductors.\n" +
                           "Increased spacing between buttons at the bottom of CCK control panel.\n" +
                           "Add a per-component menu item for clearing capacitors & inductors?\n" +
                           "\n" +
                           "Version 3.01.04 (6-26-2006)\n" +
                           "Added spanish for new components.\n" +
                           "Bugfix: Capacitor clip should disappear when capacitors disappear.\n" +
                           "\n" +
                           "Version 3.01.03 (6-24-2006)\n" +
                           "Added a floating graph components for reading current.\n" +
                           "Added inductors.\n" +
                           "\n" +
                           "Version 3.01.02 (6-22-2006)\n" +
                           "Added charge and current graphics for capacitors.\n" +
                           "Bugfix: There is some text near the top left of the screen; we can see the descenders dropping down.\n" +
                           "Added schematic ac graphic\n" +
                           "\n" +
                           "Version 3.01.01\n" +
                           "Added save/load for AC & Capacitors.\n" +
                           "Added readout graphics for AC\n" +
                           "\n" +
                           "Version 3.01.00 (6-19-2006)\n" +
                           "Rewrote circuit analysis algorithm.\n" +
                           "Bugfix: Help panel was gone.\n" +
                           "Added capacitors and AC\n" +
                           "\n" +
                           "Version 3.49 (4-17-2006)\n" +
                           "* fixed build script, posted 3.49 with improved look and feel (mac support, windows support).";

        Changelog devLog = new Changelog( logString );
        System.out.println( "*** dev log: \n" + devLog + "\n\n" );

        Changelog log = devLog.getNonDevChangelog();
        System.out.println( "*** non-dev log: \n" + log + "\n\n" );
    }
}