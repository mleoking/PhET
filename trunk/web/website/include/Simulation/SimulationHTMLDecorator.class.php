<?php

class SimulationHTMLDecorator implements SimulationInterface, SimulationEnhancerInterface {
    private $sim;

    public function __construct($undecorated_sim) {
        $this->sim = $undecorated_sim;
    }

    public function getWrapped() {
        return $this->sim;
    }

    public function convertDBData($raw_data) {
        return WebUtils::inst()->toHtml($raw_data);
    }

    public function getOriginalDBData() {
        return $this->convertDBData($this->sim->getOriginalDBData());
    }

    public function getUsedDBData() {
        return $this->convertDBData($this->sim->getUsedDBData());
    }

    public function getUnusedDBData() {
        return $this->convertDBData($this->sim->getUnusedDBData());
    }

    public function getType() {
        return $this->sim->getType();
    }

    public function getId() {
        // This is an int, no need to process
        return $this->sim->getId();
    }

    public function getName() {
        return WebUtils::inst()->toHtml($this->sim->getName());
    }

    public function getSortingName() {
        return $this->sim->getSortingName();
    }

    public function getSortingFirstChar() {
        return $this->sim->getSortingFirstChar();
    }

    public function getProjectName() {
        return WebUtils::inst()->toHtml($this->sim->getProjectName());
    }

    public function getSimName() {
        return WebUtils::inst()->toHtml($this->sim->getSimName());
    }

    public function getDescription() {
        return WebUtils::inst()->toHtml($this->sim->getDescription());
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
        return WebUtils::inst()->toHtml($this->sim->getTeachersGuideUrl());
    }

    public function getTeachersGuideFilename() {
        return WebUtils::inst()->toHtml($this->sim->getTeachersGuideFilename());
    }

    public function setTeachersGuide($filename, $contents, $size) {
        return $this->sim->setTeachersGuide($filename, $contents, $size);
    }

    public function removeTeachersGuide() {
        return $this->sim->removeTeachersGuide();
    }

    public function getTeachersGuideAnchorTag($link_text = "teacher's guide") {
        return WebUtils::inst()->buildAnchorTag($this->getTeachersGuideUrl(), $link_text);
    }

    public function getMainTopics() {
        return WebUtils::inst()->toHtml($this->sim->getMainTopics());
    }


    public function getLearningGoals() {
        return WebUtils::inst()->toHtml($this->sim->getLearningGoals());
    }

    public function getDesignTeam() {
        return WebUtils::inst()->toHtml($this->sim->getDesignTeam());
    }

    public function getLibraries() {
        return WebUtils::inst()->toHtml($this->sim->getLibraries());
    }

    public function getThanksTo() {
        return WebUtils::inst()->toHtml($this->sim->getThanksTo());
    }

    public function getKeywords() {
        return WebUtils::inst()->toHtml($this->sim->getKeywords());
    }

    public function getKeywordAnchorTags() {
        $Web = WebUtils::inst();
        $keyword_links = array();
        foreach ($this->sim->getKeywords() as $keyword) {
            $url = SITE_ROOT.'simulations/search.php?search_for='.urlencode($keyword);
            $keyword_links[] = $Web->buildAnchorTag($url, $Web->toHtml($keyword));
        }

        return $keyword_links;
    }

    public function getRatingImageAnchorTag() {
        return SimUtils::inst()->getRatingImageAnchorTag($this->sim->getRating());
    }

    public function getTypeImageTag() {
        // TODO: Remove switch on type
        static $typeMap = NULL;
        if (is_null($typeMap)) {
            $typeMap = array(
                'Java' => WebUtils::inst()->buildImageTag(SITE_ROOT.'images/sims/ratings/java.png', array('alt'=>"Java Icon", 'title'=>"This simulation is a Java simulation")),
                'Flash' => WebUtils::inst()->buildImageTag(SITE_ROOT.'images/sims/ratings/flash.png', array('alt'=>"Flash Icon", 'title'=>"This simulation is a Flash simulation"))
                );
        }

        return $typeMap[$this->getType()];
    }

    public function getTypeImageAnchorTag() {
        // TODO: Remove switch on type
        static $typeMap = NULL;
        if (is_null($typeMap)) {
            $typeMap = array(
                'Java' => WebUtils::inst()->buildAnchorTag(SITE_ROOT.'tech_support/support-java.php', $this->getTypeImageTag()),
                'Flash' => WebUtils::inst()->buildAnchorTag(SITE_ROOT.'tech_support/support-flash.php', $this->getTypeImageTag()),
                );
        }

        return $typeMap[$this->getType()];
    }

    public function getScreenshotFilename() {
        // Don't encode the filename
        return $this->sim->getScreenshotFilename();
    }

    public function getScreenshotUrl() {
        return WebUtils::inst()->toHtml($this->sim->getScreenshotUrl());
    }

    public function getAnimatedScreenshotFilename() {
        // Don't encode the filename
        return $this->sim->getAnimatedScreenshotFilename();
    }

    public function getAnimatedScreenshotUrl() {
        return WebUtils::inst()->toHtml($this->sim->getAnimatedScreenshotUrl());
    }

    public function getThumbnailFilename() {
        // Don't encode the filename
        return $this->sim->getThumbnailFilename();
    }

    public function getThumbnailUrl() {
        return WebUtils::inst()->toHtml($this->sim->getThumbnailUrl());
    }

    public function getWebEncodedName() {
        return WebUtils::inst()->encodeString($this->sim->getName());
    }

    public function getTranslations() {
        // Only locale codes that don't need encoding
        return $this->sim->getTranslations();
    }

    public function getPageUrl() {
        return WebUtils::inst()->toHtml($this->sim->getPageUrl());
    }

    public function getLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        // Don't encode the filename
        return $this->sim->getLaunchFilename($locale);
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        return WebUtils::inst()->toHtml($this->sim->getLaunchUrl($locale));
    }

    public function runsInNewBrowserWindow() {
        return $this->sim->runsInNewBrowserWindow();
    }

    public function getLaunchOnClick($locale = Locale::DEFAULT_LOCALE) {
        if (!$this->sim->runsInNewBrowserWindow()) {
            return '';
        }

        return "javascript:open_limited_window('{$this->sim->getLaunchUrl($locale)}', '_blank'); return false;";
    }

    public function getDownloadFilename($locale = Locale::DEFAULT_LOCALE) {
        // Don't encode the filename
        return $this->sim->getDownloadFilename($locale);
    }

    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE) {
        return WebUtils::inst()->toHtml($this->sim->getDownloadUrl($locale));
    }

    public function getProjectFilename() {
        // Don't encode the filename
        return $this->sim->getProjectFilename();
    }

    public function getScreenshotImageTag() {
        $attributes = array(
            'alt' => 'Sim preview image',
            'title' => 'Click here to launch the simulation from your browser',
            'width' => '300',
            'height' => '225');
        return WebUtils::inst()->buildImageTag($this->sim->getScreenshotUrl(), $attributes);
    }

    public function getVersion($ignore_flash = true) {
        $version = $this->sim->getVersion($ignore_flash);
        foreach ($version as $key => $value) {
            $version[$key] = WebUtils::inst()->toHtml($value);
        }
        return $version;
    }
}

?>