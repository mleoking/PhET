<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Full PhET Installation</h1>

            <table id="get_phet" cellspacing="0" summary="">
                <caption>
                    These installers of the PhET Simulations was released on: 1/2/2007. For changes, see <a href="/">What's New</a>. **We suggest uninstalling your earlier versions of PhET before installing.
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

            <p>For instructions on creating your own installation CD, <a href="/"><u>click here</u></a>.</p>

            <p>If you have an Internet connection that is too slow, we can send you a CD with the full installation package. Click <a href="../about/contact.php"><u>here to contact us</u></a>.</p>

            <h2>Help &amp; Troubleshooting</h2>

            <p>For assistance with installation and compatibility issues, please visit our <a href="../tech_support/index.php">troubleshooting and support page</a>.</p>

            <h2>Support Software</h2>

            <p>Our simulations require Java 1.4 or newer and Flash 7 or newer, both of which are available at no cost. If you don't have them on your computer or aren't sure about your versions, <a href="../tech_support/index.php">click here</a>.</p>

            <h2>Creating an Installation CD-ROM</h2>

            <p>You can make a CD-ROM for doing the full PhET installation on computers without Internet access. You will need a computer equipped with a CD writer, a blank CD, and an Internet connection. Please <a href="../about/contact.php">contact PhET</a> help with any questions.</p>

            <p>Follow the instructions for your computer type below:</p>

            <h3>For Windows Computers</h3>

            <ul>
                <li>Identify whether you need the installer with or without Java - if in doubt, use the installer with Java.</li>

                <li>From the downloads page (click on the "Click Here to Download" button, above), right click on the appropriate link and select "Save Target As..." (your web browser may vary) to save the "install.exe" file to your computer.</li>

                <li>Using the CD-writing software on your computer, write the "install.exe" file you have just downloaded onto the blank CD-ROM and label it "PhET Install Disk" along with the installer's date (noted on the PhET web site front page).</li>

                <li>You can now use this CD-ROM to install PhET by inserting the CD-ROM and double-clicking on the "install.exe" file.</li>

                <li>Please remember to periodically check for updated versions of the PhET simulations.</li>
            </ul>

            <h3>For Macintosh Computers</h3>

            <ul>
                <li>Please note that PhET requires Mac OS X 10.2 or later</li>

                <li>From the downloads page (click on the "Click Here to Download" button, above), right click on the link to the Macintosh installer and select "Save Target As..." (your web browser may vary) to save the "install.zip" file to your computer.</li>

                <li>Using the CD-writing software on your computer, write the "install.zip" file you have just downloaded onto the blank CD-ROM and label it "PhET Install Disk" along with the installer's date (noted on the PhET web site front page).</li>

                <li>You can now use this CD-ROM to install PhET by inserting the CD-ROM and double-clicking on the "install" file. The compressed installer should be recognized by StuffIt Expander and should automatically be expanded after downloading. If it is not expanded, you can expand it manually using StuffIt Expander 6.0 or later.</li>

                <li>If you have any problems launching the installer once it has been expanded, make sure that the compressed installer was expanded using StuffIt Expander. If you continue to have problems, please contact technical support.</li>

                <li>Please remember to periodically check for updated versions of the PhET simulations.</li>
            </ul>

            <h3>For UNIX/Linux Computers</h3>

            <ul>
                <li>From the downloads page (click on the "Click Here to Download" button, above), right click on the link to the appropriate Linux or UNIX installer and select "Save Target As..." (your web browser may vary) to save the "install.bin" file to your computer.</li>

                <li>Using the CD-writing software on your computer, write the "install.bin" file you have just downloaded onto the blank CD-ROM and label it "PhET Install Disk" along with the installer's date (noted on the PhET web site front page).</li>

                <li>You can now use this CD-ROM to install PhET by opening a shell, changing to the CD-ROM directory and typing: <strong>sh ./install.bin</strong></li>

                <li>Please remember to periodically check for updated versions of the PhET simulations.</li>
            </ul>
        <?php
    }

    print_site_page('print_content', 4);

?>