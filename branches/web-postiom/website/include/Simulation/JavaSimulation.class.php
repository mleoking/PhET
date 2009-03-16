<?php

class JavaSimulation extends BaseSimulation {
    public function runsInNewBrowserWindow() {
        // Java sims run as a Web Start application, not in a browser window
        return FALSE;
    }

    public function getType() {
        return 'Java';
    }

    public function getSize() {
        $filename = $this->getProjectFilename();

        if (!file_exists($filename)) {
            throw new PhetSimException("Cannot get size, project file '{$filename}' does not exist");
        }

        return (int) (filesize($filename) / 1024);
    }

    public function getLaunchFilename($locale = Locale::DEFAULT_LOCALE) {
        return self::sim_root."{$this->project_name}/{$this->sim_name}_{$locale}.jnlp";
    }

    public function getLaunchUrl($locale = Locale::DEFAULT_LOCALE) {
        if (!Locale::inst()->isValid($locale)) {
            return '';
        }

        return $this->getLaunchFilename($locale);
    }

    public function getProjectFilename() {
        return self::sim_root."{$this->project_name}/{$this->project_name}_all.jar";
    }

    protected function getTranslationGlob() {
        $base = self::sim_root."{$this->project_name}/{$this->sim_name}";
        $base_glob = $base."*.jnlp";
        $base_regex = $base."(_)([A-Za-z]{2})?(_([A-Za-z]{2}))?.jnlp";
        return array($base_glob, $base_regex);
    }
}

?>