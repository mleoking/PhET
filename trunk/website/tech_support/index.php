<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
            <h1>Technical Support</h1>

            <p>This page will help you solve some of the problems people commonly have running our programs. If you can't solve your problem here, please notify us by email at the following address: <a href="mailto:phethelp@colorado.edu?Subject=Help"><span class="red">phethelp@colorado.edu</span></a>.</p>

            <ul class="content-points">
                <li><a href="support-java.php">Java Installation and Troubleshooting</a></li>

                <li><a href="support-flash.php">Flash Installation and Troubleshooting</a></li>
            </ul>

            <h2>FAQ's</h2>

            <div id="faq">
                <ul id="nav">
                    <li class="faq"><a href="#q1">Why can I run some of the simulations but not all?</a></li>

                    <li class="faq"><a href="#q2">What are the System Requirements for running PhET simulations?</a></li>

                    <li class="faq"><a href="#q3">I have a Macintosh and some of the Java-based simulations do not work as expected.</a></li>

                    <li class="faq"><a href="#q4">I use Internet Explorer and the simulations do not run on my computer.</a></li>

                    <li class="faq"><a href="#q5">Why don't Flash simulations run on my computer?</a></li>

                    <li class="faq"><a href="#q6">What is the ideal screen resolution to run PhET simulations?</a></li>

                    <li class="faq"><a href="#q7">I have Windows 2000 and can run Flash simulations but the Java based simulations do not work.</a></li>

                    <li class="faq"><a href="#q8">Why do PhET simulations run slower on my laptop than on a desktop?</a></li>

                    <li class="faq"><a href="#q9">Why does my computer crash when I run one of the simulations that has sound?</a></li>

                    <li class="faq"><a href="#q10">I would like to translate PhET Simulations into another Language. Can this be easily done?</a></li>

                    <li class="faq"><a href="../about/licensing.php">What are Licensing requirements?</a></li>
                </ul>
            </div><br />
            <br />

            <h3 id="q1" style="margin: 0px 0px -10px 0px;">Why can I run some of the simulations but not all?</h3>

            <p><em>Java vs. Flash</em><br />
            Some of our simulations are Java Web Start based applications and others use Macromediaís Flash player. Flash comes with most computers while Java Web Start is a free application that can be downloaded from Sun Microsystems. To run the Java-based simulations you must have Java version 1.4 or higher installed on your computer. <a href="#">Learn about Java installation and Troubleshooting here</a>.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q2" style="margin: 0px 0px -10px 0px;">What are the System Requirements for running PhET simulations?</h3>

            <p><em>System Requirements</em><br />
            <strong>Windows Systems</strong><br />
            Intel Pentium processor<br />
            Microsoft Windows 98SE/2000/XP<br />
            256MB RAM minimum<br />
            Approximately 50MB available disk space (for full installation)<br />
            1024x768 screen resolution or better<br />
            Sun Java 1.4.2_10 or later<br />
            Macromedia Flash 7 or later<br />
            Microsoft Internet Explorer 5.5 or later, Firefox 1.5 or later</p>

            <p><strong>Macintosh Systems</strong><br />
            G3, G4, G5 or Intel processor<br />
            OS 10.3.9 or later<br />
            256MB RAM minimum<br />
            Approximately 40 MB available disk space (for full installation)<br />
            1024x768 screen resolution or better<br />
            Apple Java 1.4.2_09 or later<br />
            Macromedia Flash 7 or later<br />
            Safari 1.3 or later, Firefox 1.5 or later</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <p><strong>Linux Systems</strong><br />
            Intel Pentium processor<br />
            256MB RAM minimum<br />
            Approximately 40 MB disk space (for full installation)<br />
            1024x768 screen resolution or better<br />
            Sun Java 1.4.2_10 or later<br />
            Macromedia Flash 7 or later<br />
            Firefox 1.5 or later<br />
            Support Software<br />
            Some of our simulations use Java, and some use Flash. Both of these are available as free downloads, and our downloadable installer includes Java for those who need it.</p>

            <p><strong>Note for Macintosh Users</strong><br />
            Please note that some of our Java-based simulations will run slowly on Macintosh computer systems. Some simulations may not run correctly on Macintosh systems. These simulations can be identified by the tag below them. If you encounter further problems, please contact us with the relevant details. You should also check the minimum system requirements.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <p><strong>Note for Windows 2000 Users</strong><br />
            Some Windows 2000 systems have been reported to lack part of the necessary Java configuration. These systems will typically start our Flash-based simulations reliably, but will appear to do nothing when launching our Java-based simulations.</p>

            <p><strong>To resolve this situation, please perform the following steps:</strong><br />
            From the desktop or start menu, open "My Computer"<br />
            Click on the "Folder Options" item in the "Tools" menu<br />
            Click on the "File Types" tab at the top of the window that appears<br />
            Locate "JNLP" in the "extensions" column, and click once on it to select the item<br />
            Click on the "change" button<br />
            When asked to choose which program to use to open JNLP files, select "Browse"<br />
            Locate the program "javaws" or "javaws.exe" in your Java installation folder (typically "C:\Program Files\Java\j2re1.xxxx\javaws", where "xxxx" is a series of numbers indicating the software version; choose the latest version)<br />
            Select the program file and then click "Open" to use the "javaws" program to open JNLP files.<br />
            Java-based simulations should now function properly. Please contact us by email at phethelp@colorado.edu if you have any further difficulties.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q3" style="margin: 0px 0px -10px 0px;">I have a Macintosh and some of the Java-based simulations do not work as expected.</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q4" style="margin: 0px 0px -10px 0px;">I use Internet Explorer and the simulations do not run on my computer.</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q5" style="margin: 0px 0px -10px 0px;">Why donít Flash simulations run on my computer?</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q6" style="margin: 0px 0px -10px 0px;">What is the ideal screen resolution to run PhET simulations?</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q7" style="margin: 0px 0px -10px 0px;">I have Windows 2000 and can run Flash simulations but the Java based simulations do not work.</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q8" style="margin: 0px 0px -10px 0px;">Why do PhET simulations run slower on my laptop than on a desktop?</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q9" style="margin: 0px 0px -10px 0px;">Why does my computer crash when I run one of the simulations that has sound?</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>

            <p><a href="#top"><img src="../images/top.gif" alt="Go to top" /></a></p>

            <h3 id="q10" style="margin: 0px 0px -10px 0px;">I would like to translate PhET Simulations into another Language. Can this be easily done?</h3>

            <p>orem ipsum dolor sit amet, consectetuer adipiscing elit. Praesent vehicula sodales dui. Integer feugiat augue id diam. Vivamus vitae ante id velit sagittis tincidunt. Aliquam sed orci. Maecenas nibh sapien, commodo vel, tristique in, sodales ut, quam. Sed dolor lorem, condimentum a, ultrices in, rhoncus eu, nisi. Donec nec ipsum et lectus volutpat mattis. Mauris eget dolor. Maecenas auctor, sem a sollicitudin bibendum, urna est fermentum nisi, id vehicula tortor augue at nibh. Donec pulvinar euismod justo. Nunc in turpis. Pellentesque eu purus quis enim pellentesque aliquam. Morbi scelerisque ipsum vel neque. Sed eget magna. Vestibulum dictum, nisi non auctor suscipit, enim sapien pulvinar tellus, sollicitudin cursus libero purus vitae mi. In hac habitasse platea dictumst. Donec vel eros. Maecenas interdum, magna non scelerisque cursus, mi massa sodales diam, a pulvinar magna diam vitae mi. Sed molestie lobortis urna.</p>
        <?php
    }

    print_site_page('print_content', 5);

?>
