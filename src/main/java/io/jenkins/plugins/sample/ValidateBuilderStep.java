package io.jenkins.plugins.sample;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

public class ValidateBuilderStep extends Builder implements SimpleBuildStep {

    private final String buildName;

    @DataBoundConstructor
    public ValidateBuilderStep(String buildName){

        this.buildName = buildName;
    }

    @Override
    public void perform(@NonNull Run<?, ?> run,
                        @NonNull FilePath filePath,
                        @NonNull Launcher launcher,
                        @NonNull TaskListener taskListener) throws InterruptedException, IOException {

        taskListener.getLogger().println("Tiempo de build: " + run.getTimestampString());
        taskListener.getLogger().println("Nombre de build: " + buildName);
        taskListener.getLogger().println("Id de build: " + run.number);
    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {


        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Assigned name + build id +  build time";
        }
    }
}
