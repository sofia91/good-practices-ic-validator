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

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.IOException;


public class ValidateBuilderStep extends Builder implements SimpleBuildStep {

    private long maxTimeToBuild;
    private long lastNBuilds;
    private long timeRepairBuilds;
    private long nRepairBuilds;


    public String getMaxTimeToBuild(){
        return String.valueOf(maxTimeToBuild);
    }
    public String getLastNBuilds (){
        return String.valueOf(lastNBuilds);
    }
    public String getTimeRepairBuilds(){
        return String.valueOf(timeRepairBuilds);
    }
    public String getNRepairBuilds(){return String.valueOf(nRepairBuilds);}

    @DataBoundConstructor
    public ValidateBuilderStep(long maxTimeToBuild,
                               long lastNBuilds, long timeRepairBuilds){
        if(maxTimeToBuild <= 0){
            this.maxTimeToBuild = 10;
            this.lastNBuilds = 5;
            this.timeRepairBuilds=25;
            this.nRepairBuilds=5;
        } else {
            this.maxTimeToBuild = maxTimeToBuild;
            this.lastNBuilds = lastNBuilds;
            this.timeRepairBuilds= timeRepairBuilds;
            this.nRepairBuilds=nRepairBuilds;
        }
    }

    @Override
    public void perform(@NonNull Run<?, ?> run,
                        @NonNull FilePath filePath,
                        @NonNull Launcher launcher,
                        @NonNull TaskListener taskListener) throws InterruptedException, IOException {
        System.out.println("run.getParent().getRootDir().getAbsolutePath()" + run.getParent().getRootDir().getAbsolutePath());
        File storeFile = new File(run.getParent().getRootDir().getAbsolutePath()
                + File.separator + Constants.VALIDATE_PROPERTIES);
        WriteUtil.writeProjectPropertie(storeFile, getMaxTimeToBuild(), getLastNBuilds(), getTimeRepairBuilds(),getNRepairBuilds());

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
            }else if(Long.valueOf(lastNBuilds) < 5){
                return FormValidation.error("No puede tener una valor inferior a 5 minutos.");
            }
            try {
                Long.valueOf(lastNBuilds);
                return FormValidation.ok();
            } catch (NumberFormatException exception) {
                return FormValidation.error("El valor debe ser entero.");
            }
        }

        public FormValidation doCheckTimeRepairBuilds(@QueryParameter String timeRepairBuilds) {
            if (Util.fixEmptyAndTrim(timeRepairBuilds) == null) {
                return FormValidation.error("El valor por defecto será 25 minutos.");
            }
            try {
                Long.valueOf(timeRepairBuilds);
                return FormValidation.ok();
            } catch (NumberFormatException exception) {
                return FormValidation.error("El valor debe ser entero.");
            }
        }


        public FormValidation doCheckNRepairBuilds(@QueryParameter String nRepairBuilds) {
            if (Util.fixEmptyAndTrim(nRepairBuilds) == null) {
                return FormValidation.error("El valor por defecto será 5 minutos.");
            }
            try {
                Long.valueOf(nRepairBuilds);
                return FormValidation.ok();
            } catch (NumberFormatException exception) {
                return FormValidation.error("El valor debe ser entero.");
            }
        }
    }
}
