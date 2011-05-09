<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class TeacherIdeasMainPage extends SitePage {

    function render_content() {
        print <<<EOT
            <p>
                Welcome to the Teacher Ideas &amp; Activities page. This page is your gateway to teacher-submitted contributions, designed to be used in conjunction with the <a href="{$this->prefix}simulations/index.php">PhET simulations</a>.
            </p>

            <h2>Browse</h2>

            <p>
                Start by <a href="browse.php">browsing</a> existing contributions. These contributions include homework assignments, lectures, activities, concept questions and more, and enable you to get the most out of your PhET experience. If you already have a simulation in mind, head over to the <a href="{$this->prefix}simulations/index.php">PhET simulations</a> to browse the contributions designed for that simulation.
            </p>

            <h2>Contribute</h2>

            <p>
                 If you have developed some material you'd like to share with others, please consider <a href="contribute.php">contributing it to PhET</a>.
            </p>

            <h2>PhET's Advice for Creating Activities</h2>

            <h3>&nbsp;&nbsp;&nbsp;General Guidelines</h3>

            <p style="padding-bottom: 1em;">
            You can read our <a href="contribution-guidelines.php">guidelines for contributions</a> (<a href="contribution-guidelines.pdf">PDF</a>).

            <h3>&nbsp;&nbsp;&nbsp;Example Course Use</h3>
            <ul>
            <li><a href="HighSchoolSampleUse.pdf">High School</a></li>
            <li><a href="ModernPhysicsSampleUse.pdf">Modern Physics</a></li>
            <li><a href="PhysicsOfEverydayLifeSampleUse.pdf">Physics of Everyday Life</a></li>
            </ul>
            <br/>

EOT;
    }

}

$page = new TeacherIdeasMainPage("Teacher Ideas & Activities", NavBar::NAV_TEACHER_IDEAS, null);
$page->update();
$page->render();

?>
