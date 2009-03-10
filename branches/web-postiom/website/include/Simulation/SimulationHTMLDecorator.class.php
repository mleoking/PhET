<?php

  // TODO: I made up the term "Enhancer".  This pattern probably
  // already exists and has a name.  Find the pattern and see if this
  // is going in a good direction.  Try: "Design Patterns, GOF"
  //
  // ========================================
  //
  // NEW NOTE SINCE COMMENT WAS WRITTEN:
  // Apparently this idea of an Enhancer is still considered a Decorator.
  // Gotten from a quick skim of Head First: Design Patterns
  //
  // ...more time passes...
  //
  // Ah, I see now.  Head First focuses on just wrapping and enhancing
  // existing behavior in their examples and deemphasize extending or
  // enhancing (until later in the book under "Compound Patterns".
  //
  // GOF uses the term to talk about extending functionality as the
  // _primary_ intent of the pattern.  They use an example of a text
  // view wrapped in a scroll decorator wrapped in a border decorator.
  //
  // Keeping the rest of the comment as is, though it will probably be
  // deleted when I get farther into implementing everything.
  //
  // ========================================
  //
  // This class is a combination Decorator and Enhancer.  It wraps an
  // abstract BaseSimulation object and both decorates and provides
  // more functionality.
  //
  // The abstract BaseSimulation does know where to find things
  // related to the simulation, such as screetshots or the location of
  // the file to launch.
  //
  // The abstract BaseSimulation class knows nothing about HTML,
  // generating proper URLs or the functions needed to do interesting
  // things in HTML.
  //
  // That's the plan as I write this anyway, we'll see how it goes.
  //
  // The Decorator aspect of this just wraps methods such as getName()
  // to return HTML vernions.
  //     Ex: 'Masses & Springs' => 'Masses &amp; Springs'
  //
  // The Enhancer aspect of this gives greater functionality.  A
  // couple aspects planned an this moment:
  //
  // Translating the name from the normal human readable name (with no
  // markup) to something we could use in a web browser.
  //     Ex: 'Masses & Springs' => 'Masses_and_Springs'
  //
  // Generating a relative or absolute URL to the simulation page:
  //     Ex: 'http://phet.colorado.edu/simulations/sims.php?sim=Masses_and_Springs'
  //
  // Generating an anchor to the sim page:
  //     Ex: '<a href="{url}" title="{HTML sim name}">{HTML sim name}</a>'
  //
  // Etc.
  //
  // Brainstorm:
  //
  // Alternate idea would be to make the BaseSimulation class aware of
  // generating URLs to sim pages and the like, but still knows no HTML.
  // Resolution: try above first.  If that is unweildly, do try this idea.
  //
class SimulationHTMLDecorator implements SimulationInterface, SimulationEnhancerInterface {
    private $sim;

    public function __construct($undecorated_sim) {
        $this->sim = $undecorated_sim;
    }

    //    public function __call($method, $arguments) {
    //        throw new RuntimeException("SimulationHTMLDecorator method called that doesn't exist: {$method}");
        /*
         // Will this be needed?  Probably not.
        if (method_exists($this->sim, $method)) {
            return call_user_func_array(
                array($this->sim, $method), $arguments);
        }
        */
    //    }

    private function getBulletedList($list) {
        if (empty($list)) {
            return '';
        }

        return '<ul><li>'.join('</li><li>', $list).'</li></ul>';
    }

    public function getType() {
        return $this->sim->getType();
    }

    public function getId() {
        // This is an int, no need to process
        return $this->sim->getId();
    }

    public function getName() {
        return htmlentities($this->sim->getName());
    }

    public function getDescription() {
        return htmlentities($this->sim->getDescription());
    }

    public function getSize() {
        return $this->sim->getSize();
    }

    public function getGuidanceRecommended() {
        return $this->sim->getGuidanceRecommended();
    }

    public function getRating() {
        return $this->sim->getRating();
    }

    public function hasTeachersGuide() {
        return $this->sim->hasTeachersGuide();
    }

    public function getTeachersGuideUrl() {
        return htmlentities($this->sim->getTeachersGuideUrl());
    }

    public function getTeachersGuideAnchorTag($link_text = "teacher's guide") {
        return "<a href=\"{$this->getTeachersGuideUrl()}\">teacher's guide</a>";
    }

    public function getMainTopics() {
        // TODO: copied from web-utils.php::format_for_html()
        // push this into a utility class
        $formatted_topics = array();
        foreach ($this->sim->getMainTopics() as $topic) {
            $formatted_topics[] = htmlentities($topic);
        }
        return $formatted_topics;
    }

    public function getMainTopicsBulletedList() {
        if (count($this->sim->getMainTopics()) == 0) {
            return '';
        }

        return $this->getBulletedList($this->getMainTopics());
    }

    public function getLearningGoals() {
        // TODO: copied from web-utils.php::format_for_html()
        // push this into a utility class
        $formatted_topics = array();
        foreach ($this->sim->getLearningGoals() as $topic) {
            $formatted_topics[] = htmlentities($topic);
        }
        return $formatted_topics;
    }

    public function getLearningGoalsBulletedList() {
        if (count($this->sim->getLearningGoals()) == 0) {
            return '';
        }

        return $this->getBulletedList($this->getLearningGoals());
    }

    public function getDesignTeam() {
        // TODO: copied from web-utils.php::format_for_html()
        // push this into a utility class
        $formatted_topics = array();
        foreach ($this->sim->getDesignTeam() as $topic) {
            $formatted_topics[] = htmlentities($topic);
        }
        return $formatted_topics;
    }

    public function getDesignTeamBulletedList() {
        if (count($this->sim->getDesignTeam()) == 0) {
            return '';
        }

        return $this->getBulletedList($this->getDesignTeam());
    }

    public function getLibraries() {
        // TODO: copied from web-utils.php::format_for_html()
        // push this into a utility class
        $formatted_topics = array();
        foreach ($this->sim->getLibraries() as $topic) {
            $formatted_topics[] = htmlentities($topic);
        }
        return $formatted_topics;
    }

    public function getLibrariesBulletedList() {
        if (count($this->sim->getLibraries()) == 0) {
            return '';
        }

        return $this->getBulletedList($this->getLibraries());
    }

    public function getThanksTo() {
        // TODO: copied from web-utils.php::format_for_html()
        // push this into a utility class
        $formatted_topics = array();
        foreach ($this->sim->getThanksTo() as $topic) {
            $formatted_topics[] = htmlentities($topic);
        }
        return $formatted_topics;
    }

    public function getThanksToBulletedList() {
        if (count($this->sim->getThanksTo()) == 0) {
            return '';
        }

        return $this->getBulletedList($this->getThanksTo());
    }

    public function getKeywords() {
        // Semi-duplicated from 
        // web-utils.php::convert_comma_list_into_linked_keyword_list()
        $xml = '<span class="keywordlist">';
        $keyword_links = array();
        foreach ($this->sim->getKeywords() as $keyword) {
            $keyword_links[] = '<a href="'.SITE_ROOT.'simulations/search.php?search_for='.urlencode($keyword).'">'.$keyword.'</a>';
        }

        return 
            '<span class="keywordlist">'.
            implode(', ', $keyword_links).
            '</span>';

    }


    public function getRatingImageAnchorTag() {
        return SimUtils::inst()->getRatingImageAnchorTag($this->sim->getRating());
    }

    public function getTypeImageAnchorTag() {
        // TODO: Remove switch on type
        static $typeMap = NULL;
        if (is_null($typeMap)) {
            $typeMap = array(
                'Java'  => '<a href="'.SITE_ROOT.'tech_support/support-java.php"> <img src="'.SITE_ROOT.'images/sims/ratings/java.png" alt="Java Icon" title="This simulation is a Java simulation" /></a>',
                'Flash' => '<a href="'.SITE_ROOT.'tech_support/support-flash.php"><img src="'.SITE_ROOT.'images/sims/ratings/flash.png" alt="Java Icon" title="This simulation is a Flash simulation" /></a>'
                );
        }

        return $typeMap[$this->getType()];
    }

    public function getScreenshotUrl() {
        return htmlentities($this->sim->getScreenshotUrl());
    }

    public function getWebEncodedName() {
        return web_encode_string($this->sim->getName());
    }

    public function getTranslations() {
        throw new RuntimeError("Not Implemented");
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        return htmlentities($this->sim->getLaunchUrl($locale));
    }

    public function runsInNewBrowserWindow() {
        return $this->sim->runsInNewBrowserWindow();
    }

    public function getLaunchOnClick($locale = Locale::DEFAULT_LOCALE) {
        if (!$this->sim->runsInNewBrowserWindow()) {
            return '';
        }

        return 'onclick="javascript:open_limited_window(\''.$this->sim->getLaunchUrl($locale).'\',\'simwindow\'); return false;"';
    }

    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE) {
        return htmlentities($this->sim->getDownloadUrl($locale));
    }

    public function getScreenshotImageTag() {
        return <<<EOT
<img src="{$this->sim->getScreenshotUrl()}" alt="Sim preview image" title="Click here to launch the simulation from your browser" width="300" height="225"/>
EOT;
    }

    public function getVersion($ignore_flash = true) {
        $version = $this->sim->getVersion($ignore_flash);
        foreach ($version as $key => $value) {
            $version[$key] = htmlentities($value);
        }
        return $version;
    }

    public function getGuidanceAnchorTag() {
        $root = SITE_ROOT;
        return <<<EOT
<a href="{$root}about/legend.php">{$this->getGuidanceImageTag()}</a>
EOT;
    }

    public function getGuidanceImageTag() {
        return <<<EOT
<img src="{$this->getGuidanceImageUrl()}" alt="Not standalone" width="37" title="{$this->getGuidanceDescription()}"/>
EOT;
        
    }

    public function getGuidanceImageUrl() {
        return SITE_ROOT.'images/sims/ratings/crutch25x25.png';
    }
    public function getGuidanceDescription() {
        return 'Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity.';
    }
}

?>