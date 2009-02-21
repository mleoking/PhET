<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class WorkshopsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_domain_name = PHET_DOMAIN_NAME;

        $workshop_materials_location = PHET_DIST_ROOT."workshops/";

        print <<<EOT

        <p>Members of the PhET team regularly offer workshops on how to teach with our simulations throughout Colorado and at national meetings.</p>

        <h2>Upcoming Workshops</h2>
        <ul>
        <li><em>Using the Updated PhET Motion Simulations in HS Physics: Moving Man, Forces in 1 Dimension, Ramps, Ladybug Revolution, Torque</em>; APS HS Teacher Day, Sheraton Denver Hotel, Denver, CO, <strong>May 5, 2009</strong></li>
        <li><em>Teacher and Researcher: Designing Research Studies around PhET's Interactive Simulations</em>; AAPT Summer 2009 Meeting, Ann Arbor, MI, <strong>July 25-29, 2009</strong></li>
        </ul>

        <h2>Workshop Materials:</h2>
        <ul>
        <li><a href="workshop_uganda.php">Uganda Workshop and Guide to giving PhET workshops in developing countries</a></li>
        <li>4 hour workshop for American Association of Physics Teachers meetings (<a href="{$workshop_materials_location}Phet_Workshop_AAPT_Summer2007.pdf">PDF</a> or <a href="{$workshop_materials_location}Phet_Workshop_AAPT_Summer2007.ppt">PPT</a>)</li>
        <li>Trish Loeblein's workshop series for high school teachers (<a href="{$workshop_materials_location}PhET_Workshop_Series_07-08.pdf">PDF</a>)</li>
        <li>Carl Wieman talk about PhET Simulations (<a href="{$workshop_materials_location}WiemanPhetTalk2007.pdf">PDF</a> or <a href="{$workshop_materials_location}WiemanPhetTalk2007.ppt">PPT</a>)</li>
        <li>Handouts used in PhET Workshops (<a href="{$workshop_materials_location}PhETWorkshopHandouts.zip">ZIP</a>)</li>
        </ul>

        <h2>Schedule of Past Workshops</h2>
        <ul>
        <li><em>Using PhET for Physical Science in Middle School</em>; Colorado Science Convention, Denver, CO, <strong>Nov 21, 2008</strong> (<a href="{$workshop_materials_location}MS_CSC_2008.pdf">PDF flyer</a>)</li>
        <li><em>Chemistry series in Evergreen</em> (Aug, Sept, Oct, and Nov 2008) (<a href="{$workshop_materials_location}announcement08_09.pdf">PDF flyer</a>) <!-- link to materials once ready (possibly ofter the workshop) --></li>
        <li><em>Physical Science for Middle School</em> Aug 7, 2008 Boulder (<a href="{$workshop_materials_location}Physical_Science_for_Middle_School_using_PhET_simulations.pdf">PDF flyer</a>) <!-- link to materials once ready (possibly ofter the workshop) --></li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2008 Summer Meeting, Edmonton, AB, Jul. 20, 2008. (Wendy Adams, Noah Finkelstein, and Archie Paulson) <a href="http://www.aapt.org/Events/SM2008">link to info</a></li>
        <li><em>PhET workshop for High school teachers</em>, Evergreen High School (met once a month throughout <strong>2007-2008</strong> school year) (Trish Loeblein)</li>
        <li><em>Introduction to Inquiry-Based Teaching and PhET's Web-Based Interactive Simulations, Workshop for high school teachers</em>, Soroti, Uganda, <strong>Jan. 29 - Feb. 1, 2008</strong>. (Sam McKagan)</li>
        <li><em>Exploring Easy and Effective Ways to Use PhET's Web-based Interactive Simulations in Your Physics or Physical Science Course</em>,  NSTA 2007 Western Area Conference, Denver, CO, <strong>Nov. 8-10, 2007</strong>. (Trish Loeblein, Sam McKagan, Wendy Adams, Archie Paulson, and Angie Jardine)</li>
        <li><em>Chemistry and PhET Interactive Simulations</em>, NSTA 2007 Western Area Conference, Denver, CO, <strong>Nov. 8-10, 2007</strong>. (Trish Loeblein, Laurie Langdon, Archie Paulson, and Angie Jardine)</li>
        <li><em>PhET Workshop</em>, Boulder Area physics teachers meeting, Boulder High School, <strong>Oct. 30, 2007</strong> (Trish Loeblein, Archie Paulson, Angie Jardine)</li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2007 Summer Meeting, Greensboro, NC, <strong>Jul. 28 - Aug. 1, 2007</strong>. (Kathy Perkins, Wendy Adams, Sam McKagan, and Carl Wieman)</li>
        <li><em>PhET Workshop</em>, Research Experience for Teachers Program, JILA, Boulder, CO. <strong>Jul. 25, 2007</strong> (Sam McKagan, Angie Jardine, Ariel Paul)</li>
        <li><em>PhET Workshop</em>, DAMOP Educator's Day, Calgary, Alberta, <strong>Jun. 5, 2007</strong> (Sam McKagan and Carl Wieman)</li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2007 Winter Meeting, Seattle, WA, <strong>Jan. 6-11, 2007</strong>. (Kathy Perkins, Wendy Adams, Sam McKagan, and Carl Wieman)</li>
        <li><em>PhET workshop for High school teachers</em>, Evergreen High School (met once a month throughout <strong>2006-2007</strong> school year) (Trish Loeblein)</li>
        <li><em>Chemistry Modeling using PhET Simulations</em>, Colorado Science Teachers Association Meeting, <strong>Nov 2006</strong>. (Trish Loeblein and Linda Koch)</li>
        <li><em>PhET workshop</em>, Dakota Ridge High School Math and Science Departments, <strong>Oct 2006</strong> (Trish Loeblein)</li>
        <li><em>PhET Introductory Workshop. Boulder Valley School District Science Curriculum Committee.</em> <strong>Sep. 19, 2006</strong>. (Kathy Perkins and Trish Loeblein)</li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2006 Summer Meeting, Syracuse, NY, <strong>Jul. 22-26, 2006</strong>. (Kathy Perkins, Wendy Adams, Sam McKagan, and Carl Wieman)</li>
        <li><em>PhET Introductory Workshop</em>, STEM-TP summer workshop for HS teachers. <strong>Jun. 2006</strong> (Kathy Perkins)</li>
        <li><em>Learning about the Physical World Virtually: Computer Simulations from the Physics Education Technology Project</em>, PhysTEC Conference, Fayetteville, AK, <strong>Mar. 24-26, 2006</strong>. (Noah Finkelstein)</li>
        </ul>

EOT;
    }

}

$page = new WorkshopsPage("PhET Workshops", NAV_TEACHER_IDEAS, null);
$page->update();
$page->render();

?>
