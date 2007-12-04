<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Sponsors</h1>

			<p>PhET would like to thank the following organizations and companies for their support:</p>
			
			<div id="people">
            	<h2>Financial Support</h2>

	            <ul>
	                <li class="hewlett"><a href="http://www.hewlett.org/Default.htm"><img class="sponsors" src="../images/hewlett-logo.jpg" /><strong>The William and Flora Hewlett Foundation</strong></a></li>

	                <li class="nsf"><a href="http://www.nsf.gov/"><img class="sponsors" src="../images/nsf-logo.gif" /><strong>The National Science Foundation</strong></a></li>

	                <li class="kavli"><a href="http://www.kavlifoundation.org/"><img class="sponsors" src="../images/kavli-logo.jpg" /><strong>The Kavli Operating Institute</strong></a></li>
	
					<li class="cu"><a href="http://www.colorado.edu"><img class="sponsors" src="../images/cu.gif" width="45"/><strong>University of Colorado</strong></a></li>
	            </ul><br />

	            <h2>Technical Support</h2>

	            <ul>
	                <li><strong><a href="http://www.cs.umd.edu/hcil/piccolo/index.shtml"><img class="sponsors" src="../images/piccolo.jpg"/>Piccolo</a></strong><br />
	                <em>An open source graphics library</em></li>

	                <li><strong><a href="http://www.jfree.org/jfreechart/"><img class="sponsors" src="../images/jfreechart.png"/>JFreeChart</a></strong><br />
	                <em>An open source chart library</em></li>

	                <li><strong><a href="http://rsheh.web.cse.unsw.edu.au/homepage/index.php?id=34"><img class="sponsors" src="../images/jade.jpg"/>Raymond Sheh's JADE</a></strong><br />
	                <em>A dynamics engine</em></li>

	                <li><strong><a href="http://sourceforge.net/"><img class="sponsors" src="../images/sourceforge.png"/>Sourceforge.net</a></strong><br />
	                <em>For hosting our source code repository</em></li>

	                <li><strong><a href="http://proguard.sourceforge.net"><img class="sponsors" src="../images/proguard.gif"/>Proguard</a></strong><br />
	                <em>An open source tool for code shrinking</em></li>

	                <li><strong><a href="http://www.jetbrains.com/idea/"><img class="sponsors" src="../images/jetbrains.gif"/>JetBrains</a></strong><br />
	                <em>For providing for our Java development environment</em></li>
                
	                <li><strong><a href="http://www.ej-technologies.com/products/jprofiler/overview.html"><img class="bordered_sponsors" src="../images/ej.gif"/>ej-technologies</a></strong><br />
	                <em>For providing for our Java profilers</em></li>

					<li><strong><a href="http://www.bitrock.com"><img class="bordered_sponsors" src="../images/bitrock.png"/>BitRock</a></strong><br />
	                <em>For providing for our installer builder</em></li>
	
					<li><strong><a href="http://www.royalinteractive.com"><img class="bordered_sponsors" src="../images/royal-interactive.png"/>Royal Interactive</a></strong><br />
	                <em>For original site concept</em></li>
	
					<li><strong><a href="http://www.dynamicalsystems.org"><img class="bordered_sponsors" src="../images/dynamical-systems.gif"/>Dynamical Systems</a></strong><br />
	                <em>For providing one of their tutorials</em></li>
	            </ul>
			</div>
        <?php
    }

    print_site_page('print_content', -1);

?>