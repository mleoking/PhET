<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");


class ContributionGuidelinesPage extends SitePage {

    function render_title() {
        return;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
<div class="contribution_guidelines">
<h1>Creating PhET Activities using<br />Guided Inquiry Strategies</h1> 
<h2>Physics Education Technology Project  Perkins/Loeblein/Harlow</h2> 
<h3>4/25/2008</h3> 

<p>
The <a href="http://phet.colorado.edu">PhET simulations</a> are specifically designed and tested to 
support student learning.  However, what students do with the sims is as important as the 
simulations themselves.  PhET sims may be used in many different types of activities but 
we believe the sims are most effective when integrated with guided inquiry activities 
which encourage students to construct their own understanding. We suggest:  
</p>

<ol> 
<li>
<span class="header">Define specific learning goals</span><br />
<span class="content">
The learning goals need to be specific and measurable. Many of the sims are complex and 
students can become overwhelmed; align the lesson with your goals.
</span> 
</li>

<li>
<span class="header">Encourage students to use sense-making and reasoning</span><br /> 
<span class="content">
The activity should be geared towards encouraging the student to operate in learning 
mode not performance mode. What can they discover about the physics? What 
connections do they find?  How does it make sense? How do they explain what they 
discover? 
</span>
</li>

<li>
<span class="header">Connect and build on students' prior knowledge &amp; understanding</span><br />
<span class="content">
Ask questions to elicit their ideas. Guide students' use of the sims to test their ideas and 
confirm their ideas or confront any misconceptions. Provide ways for them to resolve 
their understanding.
</span>
</li>

<li>
<span class="header">Connect to and make sense of real-world experiences</span><br />
<span class="content">
Students will learn more if they can see that the knowledge is relevant to their everyday 
life. The sims use images from everyday life, but the lesson should explicitly help them 
relate to their lives. As you write the questions and examples, consider their interests, 
age, gender, and ethnicity.
</span>
</li>

<li>
<span class="header">Design collaborative activities</span><br />
<span class="content">
The sims provide a common language for students to construct their understanding 
together. More learning happens when they communicate their ideas and reasoning to 
each other.
</span>
</li>

<li>
<span class="header">Give only minimal directions on sim use</span><br />
<span class="content">
The sims are designed and tested to encourage students to explore and make-sense.  
Recipe-type directions can suppress their active thinking.
</span>
</li>

<li>
<span class="header">Require reasoning/sense-making in words and diagrams</span><br />
<span class="content">
The sims are designed to help students develop and test their understanding and 
reasoning about things. Lessons are most effective when students are asked to explain 
their reasoning in a variety of ways.
</span>
</li>

<li>
<span class="header">Help students monitor their understanding</span><br />
<span class="content">
Provide opportunities for students to check their own understanding. One way is to ask 
them to predict something based on their new knowledge and then check the prediction 
with the simulation.
</span>
</li>

</ol>
</div>

EOT;
    }
}


$page = new ContributionGuidelinesPage("Contribution Guidelines", NAV_TEACHER_IDEAS, null);
$page->update();
$page->render();

?>