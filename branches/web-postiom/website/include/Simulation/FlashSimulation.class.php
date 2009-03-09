<?php


class FlashSimulation extends BaseSimulation {
    public function runsInNewBrowserWindow() {
        return TRUE;
    }

    public function getSize() {
        $file = self::sim_root."{$this->project_name}/{$this->project_name}.swf";
        if (!file_exists($file)) {
            throw new RuntimeException("Cannot get size, SWF file '{$file}' does not exist");
        }

        return (int) (filesize($file) / 1024);

    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        if (!Locale::inst()->isValid($locale)) {
            return '';
        }

        $url = SIMS_ROOT."{$this->project_name}/{$this->sim_name}_{$locale}.html";
        return $url;
    }

    protected function getTranslationGlob() {
        $base = self::sim_root."{$this->project_name}/{$this->sim_name}";
        $base_glob = $base."*.*ml";
        $base_regex = $base."(-strings)?_([A-Za-z]{2})?(_([A-Za-z]{2}))?.(html|xml)";
        return array($base_glob, $base_regex);
    }
}

?>