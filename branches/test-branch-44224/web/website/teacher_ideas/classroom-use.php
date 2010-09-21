<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/referrer.php");

class ComingSoonPage extends SitePage {

    function render_content() {
        print <<<EOT
<table style="width: 100%">
	<tr>
		<td style="text-align: center">
			<h3 style="margin-bottom: 0.3em">For use in homework</h3>
		</td>
		<td style="text-align: center">
			<h3 style="margin-bottom: 0.3em">For use in lecture</h3>
		</td>
		<td style="text-align: center">
			<h3 style="margin-bottom: 0.3em">For use in labs</h3>
		</td>
	</tr>
	<tr>
		<td style="text-align: center; width: 33%;">
			<a href="/phet-dist/publications/classroom-use/PhETUseInHomework.pdf">
				<img src="/images/classroom-hw-screenshot-small.png" style="border: 2px solid blue"/>
			</a>
		</td>
		<td style="text-align: center; width: 34%;">
			<a href="/phet-dist/publications/classroom-use/PhETUseInLecture.pdf">
				<img src="/images/classroom-lecture-screenshot-small.png" style="border: 2px solid blue"/>
			</a>
		</td>
		<td style="text-align: center; width: 33%;">
			<span>Coming Soon</span>
		</td>
	</tr>
</table>

EOT;

    }

}

$page = new ComingSoonPage("Using PhET Sims in the Classroom", NavBar::NAV_TEACHER_IDEAS, get_referrer(SITE_ROOT.'teacher_ideas/manage-contributions.php'), SitePage::AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>

