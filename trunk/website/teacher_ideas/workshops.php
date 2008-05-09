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

        print <<<EOT
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
            The <a href="http://phet.colorado.edu/">Physics Education Technology Group, PhET</a>, at <a href="http://www.colorado.edu">CU Boulder</a> has developed a free 
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