<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Three Ways to Run Our Free Simulations</h1>

            <table id="get_phet" cellspacing="0" summary="">
                <tr>
                    <th scope="row" abbr="" class="specalt-none"></th>

                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            From the PhET Web Site<br />
                            <a href="../simulations/index.php"><img src="../images/button-clickhere.jpg" /></a>
                        </center>
                    </th>

                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            Full PhET Installation<br />
                            <a href="full_install.php"><img src="../images/button-clickhere.jpg" /></a>
                        </center>
                    </th>

                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            Individual Sim Installers<br />
                            <a href="simlauncher.php"><img src="../images/button-clickhere.jpg" /></a>
                        </center>
                    </th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec-none">What simulations are installed?</th>

                    <th scope="row" abbr="" class="spec">None</th>

                    <th scope="row" abbr="" class="spec">All simulations</th>

                    <th scope="row" abbr="" class="spec">Simulations selected by you</th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="specalt-none">How often are updates made available?</th>

                    <th scope="row" abbr="" class="specalt">Whenever you visit the web site</th>

                    <th scope="row" abbr="" class="specalt">Three or four times a year</th>

                    <th scope="row" abbr="" class="specalt">Any time you connect to the Internet, new or updated simulations are available</th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec-none">Size of download package</th>

                    <th scope="row" abbr="" class="spec">No download required</th>

                    <th scope="row" abbr="" class="spec">37MB, 63MB with Java VM</th>

                    <th scope="row" abbr="" class="spec">2MB, 27MB with Java VM, plus the size of the individual simulations you install (typically 500KB to 1.5MB each)</th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="specalt-none">Internet connection required to run simulations?</th>

                    <th scope="row" abbr="" class="specalt">Yes</th>

                    <th scope="row" abbr="" class="specalt">No</th>

                    <th scope="row" abbr="" class="specalt">No</th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec-none">How to install?</th>

                    <th scope="row" abbr="" class="spec">No installation required</th>

                    <th scope="row" abbr="" class="spec">From PhET web site or CD</th>

                    <th scope="row" abbr="" class="spec">From PhET web site</th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="specalt-none">How to update simulations?</th>

                    <th scope="row" abbr="" class="specalt">You always have access to the latest versions</th>

                    <th scope="row" abbr="" class="specalt">Re-install the Full PhET Installation</th>

                    <th scope="row" abbr="" class="specalt">Re-install when you have an Internet connection</th>
                </tr>

                <caption>
                    If you would like to be notified when new versions of the PhET simulations are available, send us a note with the word "Subscribe" in the subject line: <a href="mailto:physphet@colorado.edu">physphet@colorado.edu</a>
                </caption>
            </table>
        <?php
    }

    print_site_page('print_content', 4);

?>