package io.jenkins.plugins.sample;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Project;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ValidateMenuAction implements Action {

    private Project project;
    private AbstractBuild<?,?> build;
    final long maxTimeToBuild = ValidateBuilderStep.maxTimeToBuild;


    public ValidateMenuAction(Project project) {
        this.project = project;
        this.build = project.getLastBuild();
    }

    public int getBuildStepsCount() {
        return project.getBuilders().size();
    }

    public int getPostBuildStepsCount() {
        return project.getPublishersList().size();
    }

    public boolean getMaxTimeBuildValidate(){

        if(project.getLastBuild().getDuration() <= (maxTimeToBuild*60000) ){
            System.out.println("es true" + project.getLastBuild().getDuration());
            return true;
        } else {
            System.out.println("es false" + project.getLastBuild().getDuration());
            return false;

        }
    }

    public String getTiempo2(){
        /*
        System.out.println(project.getLastFailedBuild().getTimeInMillis());
        System.out.println(project.getLastFailedBuild().getTimestamp());
        System.out.println(project.getLastFailedBuild().getTime());
        System.out.println(project.getLastFailedBuild().getTimestampString());
        System.out.println(project.getLastFailedBuild().getTimestampString2());
        System.out.println(project.getLastFailedBuild().getStartTimeInMillis());
        System.out.println(project.getLastFailedBuild().getFullDisplayName());
        System.out.println(project.getLastFailedBuild().getDurationString());
        System.out.println(project.getLastFailedBuild().getDuration());
        System.out.println(project.getLastCompletedBuild().getDurationString());
        System.out.println(project.getLastCompletedBuild().getDuration());
         */

        //Si build roto no saca ningun mensaje
      //  System.out.println(project.getLastBuild().getDurationString());

System.out.println(project.getLastBuild().getDuration());
System.out.println("este es el buil roto:" +project.getLastFailedBuild().getDuration());

        Long result = project.getLastBuild().getDuration();

        return result.toString();
    }



    public String getDaysWithoutBrokenBuilds(){
        if(project.getLastFailedBuild() != null){
            return daysSince(project.getLastFailedBuild().getTimestamp());
        } else {
            return daysSince(project.getFirstBuild().getTimestamp());
        }
    }

    public boolean existBuilds(){
        return !project.getBuilds().isEmpty();
    }

    public String getLastFailedBuild() {
        return (project.getLastFailedBuild() != null?"Ultimo build roto: "
                + project.getLastFailedBuild().getDisplayName()
                + " hace: " + project.getLastFailedBuild().getTimestampString()
                :"");
    }

    public AbstractBuild<?,?> getBuild() {
        return this.build;
    }

    @Override
    public String getIconFileName() {
        return "document.png";
    }

    @Override
    public String getDisplayName() {
        return "CI validator statistics";
    }

    @Override
    public String getUrlName() {
        return "stats";
    }

    private String daysSince(Calendar timestamp){
        Long duration = (new GregorianCalendar()).getTimeInMillis() - timestamp.getTimeInMillis();
        Long days = duration / 86400000L;
        return days.toString();
    }

}
