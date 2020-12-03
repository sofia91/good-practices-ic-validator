package io.jenkins.plugins.sample;


import hudson.model.*;
import hudson.util.RunList;
import io.jenkins.plugins.storage.Constants;
import io.jenkins.plugins.storage.ReadUtil;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

public class ValidateMenuAction implements Action {

    private Project project;

    public ValidateMenuAction(Project project) {
        this.project = project;
    }

    public long getMaxTimeToBuild (){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.MAX_TIME_TO_BUILD).toString());
    }

    public Project getProject() {
        return project;
    }

    public int getBuildStepsCount() {
        return project.getBuilders().size();
    }

    public int getPostBuildStepsCount() {
        return project.getPublishersList().size();
    }

    public boolean getMaxTimeBuildValidate(){

        long maxTimeToBuild = getMaxTimeToBuild();

        if(project.getLastBuild().getDuration() <= (maxTimeToBuild*60000) ){
            System.out.println("es true" + project.getLastBuild().getDuration());
            return true;
        } else {
            System.out.println("es false" + project.getLastBuild().getDuration());
            return false;

        }
    }

    public String getTiempo2(){
        Long result = project.getLastBuild().getDuration();
        return result.toString();
    }

    private long failBuildsCount(){
        RunList<Run> builds = project.getBuilds().failureOnly();
        return builds.stream().count();
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

    public String getMtbf(){
        Long result = null;
        if(failBuildsCount() != 0){
            result = Long.valueOf(daysSince(project.getFirstBuild().getTimestamp()))/
                    failBuildsCount();
        }
        return result != null ? result + " días.":"Sin builds erroneos.";
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
