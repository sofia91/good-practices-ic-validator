package io.jenkins.plugins.sample;

import hudson.Extension;
import hudson.model.RootAction;

@Extension
public class ValidateRootAction implements RootAction {

    @Override
    public String getIconFileName() {
        return "clipboard.png";
    }

    @Override
    public String getDisplayName() {
        return "Report FOWLER IC";
    }

    @Override
    public String getUrlName() {
        return "https://www.google.es/";
    }
}
