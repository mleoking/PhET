<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Technical Support - Flash</h1>

            <p>This page will help you solve some of the problems people commonly have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:phethelp@colorado.edu"><span class="red">phethelp@colorado.edu</span></a>.</p>

            <p>To run the Flashñbased simulations you must have Flash 7 (available free) or newer installed on your computer.</p><a href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash"><img src="../images/get-flash.gif" /></a>

            <p>If you get a blank window when you try to launch a flash simulation, you probably need a new version of the Flash player.</p>

            <p>Older versions of the Flash Player can cause problems. Updating your flash player is recommended if you receive an error similar to:<br />
            <br />
            <a href="/"><img src="../images/flash-error.gif" /></a></p>

            <p>If you are unsure if you currently have a version of Flash, we can check for you if you <a href="http://phet.colorado.edu/web-pages/misc-pages/flash_detect_v7.php">click here.</a></p>

            <p><a href="#top"><img src="../images/top.gif" /></a></p>
        <?php
    }

    print_site_page('print_content', 5);

?>
