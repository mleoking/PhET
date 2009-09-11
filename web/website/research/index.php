<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/research-utils.php");

class ResearchPage extends SitePage {

    function handle_action($action) {
        if ($action == 'new') {
            research_add_from_script_params();
        }
        else if ($action == 'update') {
            research_update_from_script_params();
        }
        else if ($action == 'delete') {
            research_delete($_REQUEST['research_id']);
        }
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['action'])) {
            //sdo_authentication(true);

            $this->action = $_REQUEST['action'];

            $this->handle_action($this->action);
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $phet_dist_root = PHET_DIST_ROOT;

        print <<<EOT
            PhET conducts research on both the <strong>design</strong> and <strong>use</strong> of interactive simulations to better understand:
            <ol>
                <li>Which characteristics make these tools effective for learning and why</li>
                <li>How students engage and interact with these tools to learn, and what influences this process</li>
                <li>When, how, and why these tools are effective in a variety of learning environments</li>
            </ol>
            The PhET simulation design principles are based on research on how students learn (Bransford et al., 2000) and from our simulation interviews (see <a href="../phet-dist/publications/phet_design_process.pdf">PhET Design Process</a>). Between four and six think-aloud style interviews with individual students are done with each simulation. These interviews provide a rich data source for studying interface design and student learning. The <a href="../phet-dist/publications/PhET%20Look%20and%20Feel.pdf">PhET Look and Feel</a> briefly describes our interface design principles and a complete discussion is found in the pair of papers by Adams et al., 2008.
            <br>
            <div class="citation">
                Bransford, J.D., Brown, A. L. And Cocking, R. R. (2000). <em>How People Learn, Brain, Mind, Experience, and School</em>. Washington, D.C.: National Academy Press
                </font>
            </div>
            <br>

            <h2>Research answers to commonly asked questions:</h2>
            <div class="research-question">"Can PhET sims replace real lab equipment?"</div>
            <div class="research-answer">
                Our studies have shown that PhET sims are more effective for conceptual understanding; however, there are many goals of hands-on labs that simulations do not address. For example, specific skills relating to the functioning of equipment. Depending on the goals of your laboratory, it may be more effective to use just sims or a combination of sims and real equipment
            </div>
            <br/>

            <div class="research-question">"Do students learn if I just tell them to go home and play with a sim?"</div>
            <div class="research-answer">
                Most students do not have the necessary drive to spend time playing with a science simulation (they're fun, but not that fun) on their own time unless there is a <em>direct</em> motivation such as their grade. This is one of the reasons we are pursuing the project of how to best integrate sims into homework.
            </div>
            <br/>

            <div class="research-question">"Where is the best place to use PhET sims in my course?"</div>
            <div class="research-answer">
                We have found PhET sims to be very effective in lecture, in class activities, lab and homework. They are designed with minimal text so that they can easily be integrated into every aspect of a course.
            </div>

            <br>
            <br>

            <h2>Our immediate interests are:</h2>

            <p>
                <strong>Use of analogy to construct understanding:</strong> Students use analogies in sims to make sense of unfamiliar phenomena. Representations play a key role in student use of analogy.
            </p>

            <p>
                <strong>Simulations as tools for changing classroom norms:</strong> Sims are shaped by socio-cultural norms of science, but can also be used to change the traditional norms of how students engage in the classroom.
            </p>

            <p>
                <strong>Specific features of sims that promote learning and engaged exploration:</strong> Our design principles identify key characteristics of sims that make them productive tools for student engagement. Now we wish to study in detail how each feature impacts student understanding.
            </p>

            <p>
                <strong>Integrating simulations into homework:</strong> Simulations have unique features that are not available in most learning tools (interactivity, animation, dynamic feedback, allow for productive exploration).
            </p>

            <p>
                <strong>Effectiveness of Chemistry simulations:</strong> We have just begun investigating the envelope of where and how chemistry simulations can be effective learning tools.
            </p>

            <br>

            <h2>Publications and Presentations</h2>

EOT;

            $can_edit = false;

            if ($this->authentication_level >= SitePage::AUTHLEVEL_TEAM) {
                $can_edit = true;
            }

            foreach(research_get_all_categories() as $category) {
                print "<h3>$category</h3>";

                print "<ul class=\"content-points\">";

                foreach(research_get_all_by_category($category) as $research) {
                    print "<li>";

                    $research_id = $research['research_id'];

                    research_print($research_id);

                    if ($can_edit) {
                        print '(<a href="index.php?action=edit&amp;research_id='.$research_id.'#update-edit-form">edit</a>, <a href="index.php?action=delete&amp;research_id='.$research_id.'">delete</a>)';
                    }

                    print "</li>";
                }

                print "</ul>";
            }

            if ($can_edit) {
                if (isset($this->action) && $this->action == 'edit') {
                    $legend         = "Update Research Item";
                    $op_desc        = 'edit an existing research item or <a href="index.php">add</a> a new item';
                    $research_id    = $_REQUEST['research_id'];
                    $button_caption = "Update";
                    $action_html    = '<input type="hidden" name="action" value="update" />';
                }
                else {
                    $legend         = "Add Research Item";
                    $op_desc        = "add a new research item";
                    $research_id    = null;
                    $button_caption = "Add";
                    $action_html    = '<input type="hidden" name="action" value="new" />';
                }

                print <<<EOT
                    <form id="update-edit-form" method="post" action="index.php">
                        <fieldset>
                            <legend>$legend</legend>

                            <p>As a PhET team member, you may use this form to $op_desc.</p>

EOT;

                research_print_edit($research_id);

                print <<<EOT
                            $action_html

                            <input type="submit" name="submit" value="$button_caption" />
                        </fieldset>
                    </form>

EOT;
            }
    }

}

$page = new ResearchPage("Research", NavBar::NAV_RESEARCH, null, SitePage::AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>
