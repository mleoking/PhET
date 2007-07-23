<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Three Ways to Run Our Free Simulations</h1>

            <div id="get_phet">
                <table>
                    
                    <caption>
                        If you would like to be notified when new versions of the PhET simulations are available, send us a note with the word "Subscribe" in the subject line: <a href="mailto:physphet@colorado.edu">physphet@colorado.edu</a>
                    </caption>
                    
                    <tr>
                        <th scope="row" abbr="" class="specalt-none"></th>

                        <th scope="row" abbr="" class="specalt-none">
                            <p>Use the PhET website while connected to the web</p>
                        </th>

                        <th scope="row" abbr="" class="specalt-none">
                            <p>Download the <strong>entire</strong> website onto your computer, USB, or CD</p>
                        </th>

                        <th scope="row" abbr="" class="specalt-none">
                            <p>Download <strong>one or more</strong> simulations to your computer, USB, or CD</p>
                        </th>
                    </tr>
                
                    <tr>
                        <th scope="row" abbr="" class="specalt-none"></th>

                        <th scope="row" abbr="" class="specalt-none">
                            <p><a href="../simulations/index.php"><img src="../images/button-clickhere.jpg" alt="Click here"/></a></p>
                        </th>

                        <th scope="row" abbr="" class="specalt-none">
                            <p><a href="../get_phet/full_install.php"><img src="../images/button-clickhere.jpg"  alt="Click here"/></a></p>
                        </th>

                        <th scope="row" abbr="" class="specalt-none">
                            <p><a href="../get_phet/simlauncher.php"><img src="../images/button-clickhere.jpg"  alt="Click here"/></a></p>
                        </th>
                    </tr>
                
                    <tr>
                        <th scope="row" abbr="" class="spec-none"><p>How do I get the simulations?</p></th>

                        <th scope="row" abbr="" class="specalt"><p>Click on the simulation icon on the web page of the simulation you want to run.</p></th>

                        <th scope="row" abbr="" class="specalt"><p><a href="../get_phet/full_install.php">Click here</a> to go to the download page.</p></th>

                        <th scope="row" abbr="" class="specalt"><p><a href="../get_phet/simlauncher.php">Click here</a> to go to the all-sims download page.</p></th>
                    </tr>

                    <tr>
                        <th scope="row" abbr="" class="spec-none"><p>What simulations are installed?</p></th>

                        <th scope="row" abbr="" class="spec"><p>The simulations you use are temporarily installed.</p></th>

                        <th scope="row" abbr="" class="spec"><p>All simulations.</p></th>

                        <th scope="row" abbr="" class="spec"><p>Only the ones you want.</p></th>
                    </tr>

                    <tr>
                        <th scope="row" abbr="" class="spec-none"><p>How large is the download?</p></th>

                        <th scope="row" abbr="" class="specalt"><p>Under 1.5 MB for each simulation.</p></th>

                        <th scope="row" abbr="" class="specalt"><p>Approximately 40MB if you already have Java, or 65MB if you do not.</p></th>

                        <th scope="row" abbr="" class="specalt"><p>Under 1.5 MB for each simulation.</p></th>
                    </tr>
                
                    <tr>
                        <th scope="row" abbr="" class="specalt-none"><p>How often are updates made available?</p></th>

                        <th scope="row" abbr="" class="spec"><p>As soon as new or updated simulations are posted on the web site.</p></th>

                        <th scope="row" abbr="" class="spec"><p>Three or four times a year.</p></th>

                        <th scope="row" abbr="" class="spec"><p>If you connect to the Internet, new or updated simulations are available.</p></th>
                    </tr>

                    <tr>
                        <th scope="row" abbr="" class="specalt-none"><p>Is an Internet connection required to run simulations?</p></th>

                        <th scope="row" abbr="" class="specalt"><p>Yes</p></th>

                        <th scope="row" abbr="" class="specalt"><p>No</p></th>

                        <th scope="row" abbr="" class="specalt"><p>No</p></th>
                    </tr>
                
                    <tr>
                        <th scope="row" abbr="" class="spec-none"><p>Where can I save them to on my computer?</p></th>
                    
                        <th scope="row" abbr="" class="spec"><p>Flash applets cannot be saved. Java applications are automatically stored in your computer's WebStart cache, but cannot be moved.</p></th>
                    
                        <th scope="row" abbr="" class="spec"><p>CD, USB or hard drive.</p></th>
                    
                        <th scope="row" abbr="" class="spec"><p>CD, USB or hard drive.</p></th>
                    </tr>
                </table>
            </div>
        <?php
    }

    print_site_page('print_content', 4);

?>