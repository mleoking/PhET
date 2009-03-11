<?php

interface SimulationInterface {
    public function runsInNewBrowserWindow();

    public function getType();

    public function getId();

    public function getName();

    public function getSortingName();
    public function getSortingFirstChar();

    public function getDescription();

    public function getSize();

    public function getGuidanceRecommended();

    public function getRating();

    public function getKeywords();

    public function hasTeachersGuide();
    public function getTeachersGuideUrl();

    public function getMainTopics();

    public function getLearningGoals();

    public function getScreenshotUrl();
    public function getAnimatedScreenshotUrl();

    public function getThumbnailUrl();

    public function getPageUrl();

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE);

    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE);

    public function getTranslations();

    public function getVersion();
}

?>