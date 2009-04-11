<?php

class TestFlashSimulation extends FlashSimulation {
    public function getDownloadUrl($locale = Locale::DEFAULT_LOCALE) {
        return parent::getDownloadUrl($locale).'&enable_test_sims=1';
    }

    public function getPageUrl($locale = Locale::DEFAULT_LOCALE) {
        $url = parent::getPageUrl($locale);
        if (strlen($url) == 0) {
            return $url;
        }

        return $url.'&enable_test_sims=1';
    }
}

?>