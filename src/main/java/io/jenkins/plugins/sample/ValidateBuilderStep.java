package io.jenkins.plugins.sample;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
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

    public static long maxTimeToBuild;

    @DataBoundConstructor
    public ValidateBuilderStep(long maxTimeToBuild){

        if (maxTimeToBuild == 0){
            this.maxTimeToBuild = 10;
        } else {
            this.maxTimeToBuild = maxTimeToBuild;

        }
    }

    @Override
    public void perform(@NonNull Run<?, ?> run,
                        @NonNull FilePath filePath,
                        @NonNull Launcher launcher,
                        @NonNull TaskListener taskListener) throws InterruptedException, IOException {
        taskListener.getLogger().println("Tiempo máximo de build configurado: " + maxTimeToBuild);
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
            return "Validación build rápido";
        }

        public FormValidation doCheckMaxTimeToBuild(@QueryParameter String maxTimeToBuild) {
            if (Util.fixEmptyAndTrim(maxTimeToBuild) == null) {
                return FormValidation.error("El valor por defecto será 10 minutos.");
            }

            try {
                Long.valueOf(maxTimeToBuild);
                return FormValidation.ok();
            } catch (NumberFormatException exception) {
                return FormValidation.error("El valor debe ser entero.");
            }
        }

    }
}
