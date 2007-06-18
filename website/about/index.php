<?php

    include_once("../admin/site-utils.php");
    
    function print_content() {
        ?>
        <h1>Introduction to PhET</h1>
        
        <p>
        The Physics Education Technology (PhET) project is an ongoing effort to provide an extensive suite of simulations for teaching and learning physics and to make these resources both freely available from the <a href="../index.php">PhET website</a> and easy to incorporate into classrooms. 
        </p>
        
        <p>The <a href="../sims/index.php">simulations</a> are animated, interactive, and game-like environments in which students learn through exploration. In these simulations, we emphasize the connections between real life phenomena and the underlying science and seek to make the visual and conceptual models that expert physicists use accessible to students. 
         </p>  
         
            
        <p>Our team of scientists, software engineers and science educators uses a <a href="../research/index.php">research-based approach</a> in our design – incorporating findings from prior research and our own testing – to create simulations that support student engagement with and understanding of physics concepts. 
        </p>

        <p>
        PhET simulations animate what is invisible to the eye, such as atoms, electrons, photons and electric fields. User interaction is encouraged by engaging graphics and intuitive controls that include click-and-drag manipulation, sliders and radio buttons. By immediately animating the response to any user interaction, the simulations are particularly good at establishing cause-and-effect and at linking multiple representations.
        </p>
        
        <p>For quantitative exploration, the simulations have measurement instruments available, such as a ruler, stop-watch, voltmeter and thermometer. All the simulations are extensively tested for usability and educational effectiveness, and a rating system (<img src="../images/sims/ratings/alpha-rating.gif"/ width="16" height="16">, <img src="../images/sims/ratings/beta-minus-rating.gif" />, <img src="../images/sims/ratings/beta-plus-rating.gif"/>, <img src="../images/sims/ratings/beta-rating.gif" />, <img src="../images/sims/ratings/check_Icon.gif" />) is used to indicate what level of testing they have received. The tests involve student interviews and use of the simulations in a variety of settings, including lectures, group work, homework and lab work.
        </p>
            
        <p>The PhET simulations are easy to use. They are written in <a href="../tech_support/support-java.php">Java</a> and <a href="../tech_support/support-flash.php">Flash</a>, and can be run using a standard web browser as long as the latest Flash and Java plug-ins are installed.
        </p>

        <?php
    }

    print_site_page('print_content', 8);

?>
