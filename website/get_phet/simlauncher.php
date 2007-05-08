<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Individual Simulation Installers</h1>

            <table id="get_phet" cellspacing="0" summary="">
                <caption>
                    These installers of the PhET Simulations were released on: 1/2/2007. For changes, see <a href="/">What's New</a>. **We suggest uninstalling your earlier versions of PhET before installing.
                </caption>

                <tr>
                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            Choose your computer platform
                        </center>
                    </th>

                    <th scope="row" abbr="" class="specalt-none">
                        <center>
                            Full PhET Installation
                        </center>
                    </th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec">
                        <ul>
                            <li>PC with Windows and Java 1.4 or later</li>

                            <li>Mac with OS 10.3.9 or later</li>

                            <li>Linux</li>

                            <li>Other UNIX</li>
                        </ul>
                    </th>

                    <th scope="row" abbr="" class="spec"><a href="/"><img src="../images/checkbox.jpg" />&nbsp;download (approx. 40MB)</a></th>
                </tr>

                <tr>
                    <th scope="row" abbr="" class="spec">
                        <ul>
                            <li>PC with Windows that does NOT have Java 1.4 or later on it</li>
                        </ul>
                    </th>

                    <th scope="row" abbr="" class="spec"><a href="/"><img src="../images/checkbox.jpg" />&nbsp;download (approx. 65MB)</a></th>
                </tr>
            </table>

            <p>For instructions on creating your own installation CD, <a href="simlauncher.php"><u>click here</u></a>.</p>

            <p>If you have an Internet connection that is too slow, we can send you a CD with the full installation package. Click <a href="../about/contact.php"><u>here to contact us</u></a>.</p>
        <?php
    }

    print_site_page('print_content', 4);

?>