<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class WorkshopsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $portal_root = PORTAL_ROOT;

        $phet_domain_name = PHET_DOMAIN_NAME;

        $workshop_materials_location = PORTAL_ROOT."phet-dist/workshops/";

        print <<<EOT

        <p>Members of the PhET team regularly offer workshops on how to teach with our simulations throughout Colorado and at national meetings.</p>

        <h2>Upcoming Workshops</h2>
        <ul>
        <li>Trish's upcoming workshops Chemistry series in Evergreen (Aug, Sept, Oct, and Nov 2008) (<a href="{$workshop_materials_location}announcement08_09.pdf">PDF flyer</a>) <!-- link to materials once ready (possibly ofter the workshop) --></li>
        <li>Physical Science for Middle School Aug 7, 2008 Boulder <a href="{$workshop_materials_location}Physical_Science_for_Middle_School_using_PhET_simulations.pdf">PDF flyer</a> <!-- link to materials once ready (possibly ofter the workshop) --></li>
        <li>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course, AAPT 2008 Summer Meeting, Edmonton, AB, Jul. 20, 2008. (Wendy Adams, Noah Finkelstein, and Archie Paulson) <a href="http://www.aapt.org/Events/SM2008">link to info</a></li>
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
        <li><em>PhET workshop for High school teachers</em>, Evergreen High School (met once a month throughout 2007-2008 school year) (Trish Loeblein)</li>
        <li><em>Introduction to Inquiry-Based Teaching and PhET's Web-Based Interactive Simulations, Workshop for high school teachers</em>, Soroti, Uganda, Jan. 29 - Feb. 1, 2008. (Sam McKagan)</li>
        <li><em>Exploring Easy and Effective Ways to Use PhET's Web-based Interactive Simulations in Your Physics or Physical Science Course</em>,  NSTA 2007 Western Area Conference, Denver, CO, Nov. 8-10, 2007. (Trish Loeblein, Sam McKagan, Wendy Adams, Archie Paulson, and Angie Jardine)</li>
        <li><em>Chemistry and PhET Interactive Simulations</em>, NSTA 2007 Western Area Conference, Denver, CO, Nov. 8-10, 2007. (Trish Loeblein and Laurie Langdon)</li>
        <li><em>PhET Workshop</em>, Boulder Area physics teachers meeting, Boulder High School, Oct. 30, 2007 (Trish Loeblein, Archie Paulson, Angie Jardine)</li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2007 Summer Meeting, Greensboro, NC, Jul. 28 - Aug. 1, 2007. (Kathy Perkins, Wendy Adams, Sam McKagan, and Carl Wieman)</li>
        <li><em>PhET Workshop</em>, Research Experience for Teachers Program, JILA, Boulder, CO. Jul. 25, 2007 (Sam McKagan, Angie Jardine, Ariel Paul)</li>
        <li><em>PhET Workshop</em>, DAMOP Educator's Day, Calgary, Alberta, Jun. 5, 2007 (Sam McKagan and Carl Wieman)</li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2007 Winter Meeting, Seattle, WA, Jan. 6-11, 2007. (Kathy Perkins, Wendy Adams, Sam McKagan, and Carl Wieman)</li>
        <li><em>PhET workshop for High school teachers</em>, Evergreen High School (met once a month throughout 2006-2007 school year) (Trish Loeblein)</li>
        <li><em>Chemistry Modeling using PhET Simulations</em>, Colorado Science Teachers Association Meeting, Nov 2006. (Trish Loeblein and Linda Koch)</li>
        <li><em>PhET workshop</em>, Dakota Ridge High School Math and Science Departments, Oct 2006 (Trish Loeblein)</li>
        <li><em>PhET Introductory Workshop. Boulder Valley School District Science Curriculum Committee. Sep. 19</em>, 2006. (Kathy Perkins and Trish Loeblein)</li>
        <li><em>Exploring Easy &amp; Effective Ways to Use PhET's Web-Based Interactive Simulations in Your Physics Course</em>, AAPT 2006 Summer Meeting, Syracuse, NY, Jul. 22-26, 2006. (Kathy Perkins, Wendy Adams, Sam McKagan, and Carl Wieman)</li>
        <li><em>PhET Introductory Workshop</em>, STEM-TP summer workshop for HS teachers. Jun. 2006 (Kathy Perkins)</li>
        <li><em>Learning about the Physical World Virtually: Computer Simulations from the Physics Education Technology Project</em>, PhysTEC Conference, Fayetteville, AK, Mar. 24-26, 2006. (Noah Finkelstein)</li>
        </ul>

        <hr />

        <h2>Upcoming Workshops</h2>

        <div class="workshop">

        <div class="workshop-title">
            Chemistry Teachers Workshop Series at Evergreen High School 2008-2009<br /> 
            Teachers of all levels are invited!! 
        </div>

        <div class="workshop-date">
            <h3>Dates</h3>
            <ul>
                <li>Tuesday August 5, 8:30am - 2:30pm</li>
                <li>Tuesday September 9, 4:30pm - 7:00pm</li>
                <li>Tuesday October 7, 4:30pm - 7:00pm</li>
                <li>Tuesday November 4, 4:30pm - 7:00pm</li>
            </ul>
        </div>

        <div class="workshop-location">
            <h3>Location</h3>
            <p>
            <a href="http://sc.jeffco.k12.co.us/education/school/school.php?sectionid=290">Evergreen High School</a><br />
            <a href="http://maps.google.com/maps?f=q&amp;hl=en&amp;geocode=&amp;q=29300+Buffalo+Park+Road,+Evergreen,+CO,+80439&amp;sll=37.0625,-95.677068&amp;sspn=66.964699,113.203125&amp;ie=UTF8&amp;ll=39.62326,-105.333781&amp;spn=0.008132,0.013819&amp;z=16&amp;iwloc=addr">29300 Buffalo Park Road, Evergreen, CO, 80439</a>
            </p>
        </div>

        <div class="workshop-downloadables">
            <h3>Flyers / Supplementary Information</h3>
            <p>
            <a href="{$portal_root}phet-dist/workshops/announcement08_09.pdf">Flyer</a>
            </p>
        </div>

        <div class="workshop-contact">
            <h3>Contact</h3>
            <p>
               <a href="mailto: ploeblei@jeffco.k12.co.us">Trish Loeblein</a>
            </p>
        </div>

        <div class="workshop-description">
            <h3>Description</h3>
            <p>
            The <a href="http://{$phet_domain_name}/">Physics Education Technology Group, PhET</a>, at <a href="http://www.colorado.edu">CU Boulder</a> has developed a free 
website to help with science teaching. This award-winning site has over sixty interactive 
simulations that provide opportunities for student explorations and virtual labs. Many of 
these are helpful with topics in chemistry.
            </p>
            <p>I would like to develop a community of area 
chemistry teachers who use an inquiry approach to teaching (or want to learn about and 
implement an inquiry approach in their classroom) and want to use the simulations. We 
will discuss recent research in science education and how we can enrich our curriculum 
with interactive simulations.
            </p>
            <p>
            We will meet four times during the school year. Each 
teacher will develop lessons to use in their classes and then share their experiences at the 
meetings.  There will be credit available through <a href="http://www.mines.edu/">Colorado School of Mines</a> for $45 if 
you attend all 16 hours. We will provide meals too!
            </p>
        </div>

        </div>

EOT;
    }

}

$page = new WorkshopsPage("PhET Workshops", NAV_TEACHER_IDEAS, get_referrer());
$page->update();
$page->render();

?>