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
import io.jenkins.plugins.storage.Constants;
import io.jenkins.plugins.storage.WriteUtil;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import java.io.File;
import java.io.IOException;


public class ValidateBuilderStep extends Builder implements SimpleBuildStep {

    private long maxTimeToBuild;
    private long lastNBuilds;

    public String getMaxTimeToBuild(){
        return String.valueOf(maxTimeToBuild);
    }
    public String getLastNBuilds (){
        return String.valueOf(lastNBuilds);
    }

    @DataBoundConstructor
    public ValidateBuilderStep(long maxTimeToBuild,
                               long lastNBuilds){
        if(maxTimeToBuild <= 0){
            this.maxTimeToBuild = 10;
            this.lastNBuilds = 5;
        } else {
            this.maxTimeToBuild = maxTimeToBuild;
            this.lastNBuilds = lastNBuilds;
        }
    }

    @Override
    public void perform(@NonNull Run<?, ?> run,
                        @NonNull FilePath filePath,
                        @NonNull Launcher launcher,
                        @NonNull TaskListener taskListener) throws InterruptedException, IOException {

        File storeFile = new File(run.getParent().getRootDir().getAbsolutePath()
                + File.separator + Constants.VALIDATE_PROPERTIES);
        WriteUtil.writeProjectPropertie(storeFile, getMaxTimeToBuild(), getLastNBuilds());

        taskListener.getLogger().println("Tiempo máximo de build configurado: " + maxTimeToBuild);
        taskListener.getLogger().println("Número builds exitosos: " + maxTimeToBuild);
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

        public FormValidation doCheckLastNBuilds(@QueryParameter String lastNBuilds) {
            if (Util.fixEmptyAndTrim(lastNBuilds) == null) {
                return FormValidation.error("El valor por defecto será 5 minutos.");
            }
            try {
                Long.valueOf(lastNBuilds);
                return FormValidation.ok();
            } catch (NumberFormatException exception) {
                return FormValidation.error("El valor debe ser entero.");
            }
        }

    }
}
