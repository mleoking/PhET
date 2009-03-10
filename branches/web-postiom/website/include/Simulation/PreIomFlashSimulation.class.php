<?php


class PreIomFlashSimulation extends FlashSimulation {
    public function getVersion() {
        // PreIOM sims have all blank version info
        $version = parent::getVersion();
        foreach ($version as $key => $value) {
            $version[$key] = '';
        }

        return $version;
    }
    
}

?>