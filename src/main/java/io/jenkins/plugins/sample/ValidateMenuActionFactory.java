package io.jenkins.plugins.sample;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Project;
import jenkins.model.TransientActionFactory;

import java.util.Collection;
import java.util.Collections;

@Extension
public class ValidateMenuActionFactory extends TransientActionFactory<Project> {

    @Override
    public Class<Project> type() {
        return Project.class;
    }

    @NonNull
    @Override
    public Collection<? extends Action> createFor(@NonNull Project project) {
        return Collections.singleton(new ValidateMenuAction(project));
    }
}
