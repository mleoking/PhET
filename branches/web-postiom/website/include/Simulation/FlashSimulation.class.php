<?php


class FlashSimulation extends BaseSimulation {
    public function runsInNewBrowserWindow() {
        return TRUE;
    }

    public function getType() {
        return 'Flash';
    }

    public function getSize() {
        $filename = $this->getProjectFilename();

        if (!file_exists($filename)) {
            throw new RuntimeException("Cannot get size, SWF file '{$filename}' does not exist");
        }

        return (int) (filesize($filename) / 1024);

    }

    public function getLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.html";
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        return $this->getLaunchFilename($locale);
    }

    public function getProjectFilename() {
        return self::sim_root."{$this->project_name}/{$this->project_name}.swf";
    }

    protected function getTranslationGlob() {
        $base = self::sim_root."{$this->project_name}/{$this->sim_name}";
        $base_glob = $base."*.*ml";
        $base_regex = $base."(-strings)?_([A-Za-z]{2})?(_([A-Za-z]{2}))?.(html|xml)";
        return array($base_glob, $base_regex);
    }
}

?>