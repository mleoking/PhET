<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class TeacherIdeasMain extends SitePage {
    function render_content() {
        print <<<EOT
            <p>
                Welcome to the Teacher Ideas &amp; Activities page. This page is your gateway to teacher-submitted contributions, designed to be used in conjunction with the <a href="../simulations/index.php">PhET simulations</a>.
            </p>

            <h2>Browse</h2>

            <p>
                Start by <a href="browse.php">browsing</a> existing contributions. These contributions include homework assignments, lectures, activities, concept questions and more, and enable you to get the most out of your PhET experience. If you already have a simulation in mind, head over to the <a href="../simulations/index.php">PhET simulations</a> to browse the contributions designed for that simulation.
            </p>

            <h2>Contribute</h2>

            <p>
                 If you have developed some material you'd like to share with others, please consider <a href="contribute.php">contributing it to PhET</a>.
            </p>

            <h2>PhET’s Advice for Creating Activities</h2>
            
            <p>
            You can read our guidelines for contributions by <a href="contribution-guidelines.pdf">clicking here</a>.
            </p>

EOT;
    }
}

$page = new TeacherIdeasMain("Teacher Ideas & Activities", NAV_TEACHER_IDEAS, get_referrer());
$page->update();
$page->render();

?>