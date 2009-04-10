<?php

interface SimulationInterface {
    public function runsInNewBrowserWindow();

    public function getOriginalDBData();
    public function getUsedDBData();
    public function getUnusedDBData();

    public function getType();

    public function getId();

    public function getName();

    public function getSortingName();
    public function getSortingFirstChar();

    public function getProjectName();
    public function getSimName();

    public function getDescription();

    public function getSize();

    public function getGuidanceRecommended();

    public function getRating();

    public function getKeywords();

    public function hasTeachersGuide();
    public function getTeachersGuideFilename();
    public function getTeachersGuideUrl();
    public function setTeachersGuide($filename, $contents, $size);
    public function removeTeachersGuide();

    public function getMainTopics();

    public function getLearningGoals();

    public function getScreenshotFilename();
    public function getScreenshotUrl();
    public function getAnimatedScreenshotFilename();
    public function getAnimatedScreenshotUrl();

    public function getThumbnailFilename();
    public function getThumbnailUrl();

    public function getPageUrl();

    public function getLaunchFilename($requested_locale = Locale::DEFAULT_LOCALE);
    public function getLaunchUrl($requested_locale = Locale::DEFAULT_LOCALE);

    public function getDownloadFilename($requested_locale = Locale::DEFAULT_LOCALE);
    public function getDownloadUrl($requested_locale = Locale::DEFAULT_LOCALE);

    public function getProjectFilename();

    public function getTranslations();

    public function getVersion();

    public function isReal();
}

?>