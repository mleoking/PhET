<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Full PhET Installation</h1>

            <p>
            	These installers of the PhET Simulations are updated weekly. We suggest uninstalling your earlier versions of PhET before installing.
            </p>

			<ul>
				<li><a href="../../phet-dist/PhET-1.0-windows-installer.exe">Download installer for Windows</a> - 55 MB</li>
				
				<li><a href="../../phet-dist/PhET-1.0-osx-installer.zip">Download installer for Mac OS X</a> - 39 MB</li>
				
				<li><a href="../../phet-dist/PhET-1.0-linux-installer.bin">Download installer for Linux</a> - 39 MB</li>
			</ul>
            
            <hr/>

            <p>If you have an Internet connection that is too slow, we can send you a CD with the full installation package. Click <a href="../about/contact.php"><u>here to contact us</u></a>.</p>

            <h2>Help &amp; Troubleshooting</h2>

            <p>For assistance with installation and compatibility issues, please visit our <a href="../tech_support/index.php">troubleshooting and support page</a>.</p>

            <h2>Support Software</h2>

            <p>Our simulations require Java 1.4 or newer and Flash 7 or newer, both of which are available at no cost. If you don't have them on your computer or aren't sure about your versions, <a href="../tech_support/index.php">click here</a>.</p>

            <h2>Creating an Installation CD-ROM</h2>

            <p>You can make a CD-ROM for doing the full PhET installation on computers without Internet access. You will need a computer equipped with a CD writer, a blank CD, and an Internet connection. Please <a href="../about/contact.php">contact PhET</a> help with any questions.</p>

            <p>Follow the instructions below:</p>

            <ul>
                <li>Download the <a href="../../phet-dist/PhET-1.0-CD-ROM.zip">PhET CD-ROM Distribution archive</a> onto your computer (140 MB).</li>

                <li>Extract the contents of the archive (on most operating systems, this is done by double-clicking the downloaded file).</li>

                <li>Copy all the files to the CD-ROM.</li>

                <li>Please remember to periodically check for updated versions of the PhET simulations.</li>
            </ul>
        <?php
    }

    print_site_page('print_content', 4);

?>