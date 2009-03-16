<?php


class PreIomFlashSimulation extends FlashSimulation {
    public function getVersion() {
        // PreIOM sims have all blank version info
        $version = parent::getVersion();

        // If the version is greater than 2, it is post-IOM
        if (!empty($version['major']) && ($version['major'] >= 2)) {
            return $version;
        }

        // Otherwise, clear the sim version so it will be ignored
        foreach ($version as $key => $value) {
            $version[$key] = '';
        }

        return $version;
    }
    
}

?>