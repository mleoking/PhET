<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class DonatePage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT

<p>
	If you use our simulations, and/or are interested in supporting science education worldwide, please consider donating to the PhET Project. While the simulations are free, they are expensive to create, test and maintain.
</p>

<p>
	Donations of any size--$10, $50, $100, or more--help make it possible for us to continue to grow our library of simulations, provide on-going support and assist underserved populations around the world. All donations are tax deductible.
</p>

<div style="text-align: center; width: 100%;">
	<a href="http://www.cufund.org/giving-opportunities/fund-description/?id=7397" onclick="javascript:window.open('http://www.cufund.org/giving-opportunities/fund-description/?id=7397', 'cufund', 'resizable=yes,status=yes,toolbar=yes,location=yes,menubar=yes,directories=yes,scrollbars=yes'); return false;">
		<img src="../images/donate-now.gif" alt="Click here to go to the donation page for PhET"/>
	</a>
</div>

<p>
	For any questions on the donation process, contact Kathy Dessau at <a href="mailto:Kathryn.Dessau@colorado.edu">Kathryn.Dessau@colorado.edu</a>.
</p>

<p>
	NOTE: Donations to PhET are routed through the CU Foundation, the University of Colorado's fundraising partner. CU Foundation is a tax-exempt organization under IRC 501(c)(3) and a public charity under IRC sections 509(a)(1) and 170(b)(1)(A)(iv). All money donated will go directly to PhET. 
</p>

EOT;
    }

}

$page = new DonatePage("Support PhET Simulations", NavBar::NAV_DONATE, null);
$page->update();
$page->render();

?>
