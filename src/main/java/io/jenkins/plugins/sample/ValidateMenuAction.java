package io.jenkins.plugins.sample;


import hudson.Util;
import hudson.model.*;
import hudson.util.RunList;
import io.jenkins.plugins.fontawesome.api.SvgTag;
import io.jenkins.plugins.storage.Constants;
import io.jenkins.plugins.storage.ReadUtil;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

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

    public long getLastNBuilds(){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.LAST_N_BUILDS).toString());
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


    public String getMttr(){
        final long[] repairCount = {0};
        final long[] repairTimeSumInMillis = {0};
        long[] repairTimeAux = {0};
        RunList<Run> builds = project.getBuilds();

        builds.stream().sorted().forEach(build -> {
                    System.out.println(build.getId());
                    if(!Result.SUCCESS.equals(build.getResult())){
                        if(repairTimeAux[0] == 0){
                            repairTimeAux[0] = build.getStartTimeInMillis();
                        }
                    } else {
                        if(repairTimeAux[0] != 0){
                            System.out.println("Tiempo auxiliar: " + repairTimeAux[0]);
                            System.out.println("Tiempo inicio build reparado: " + build.getStartTimeInMillis());
                            repairTimeSumInMillis[0] += (build.getStartTimeInMillis() - repairTimeAux[0]);
                           // repairTimeSumInMillis[0] += (repairTimeAux[0] - build.getStartTimeInMillis());
                            System.out.println("Suma tiempo reparacion: " + repairTimeSumInMillis[0]);
                            repairCount[0]++;
                            System.out.println("Contador reparacion: " + repairCount[0]);
                            repairTimeAux[0] = 0;
                        }
                    }
                }
        );
        Long result = null;
        if(repairCount[0] != 0){
            result = repairTimeSumInMillis[0] / repairCount[0];
        }
        System.out.println("Resultado final MTTR: " + result);

        return result != null? Util.getTimeSpanString(result) :"N/A.";
    }

    public String getMtbf(){
        Long result = null;
        if(failBuildsCount() != 0){
            result = Long.valueOf(daysSince(project.getFirstBuild().getTimestamp()))/
                    failBuildsCount();
        }

        return result != null ? result + " días.":"Sin builds erroneos.";
    }

    public String getMbbf(){
        long failBuildsCount = failBuildsCount();
        Long result = null;
        if(failBuildsCount != 0){
            result = project.getBuilds().stream().count()/
                    failBuildsCount;

        }
        return result != null ? result + " builds.":"Sin builds erroneos.";
    }

    private long getAverageLastBuildsNumber(){
        long lastNBuilds = getLastNBuilds();
        RunList<Run> buildList = project.getBuilds();
        Long sum = 0L;
        Long result = null;
        List<Run> completedBuildList = buildList.stream()
                .filter(x -> x.getResult().equals(Result.SUCCESS))
                .limit(lastNBuilds).collect(Collectors.toList());
        System.out.println("N builds completos: " + completedBuildList.stream().count());
        System.out.println("Primer build de la lista: " + completedBuildList.get(0).getId());
        System.out.println("Duracion primer build: " + completedBuildList.get(0).getDuration());
        for (Run run:completedBuildList) {
            sum += run.getDuration();
        }
        if(lastNBuilds != 0){
            result = sum/lastNBuilds;
        }
        return result;
    }
    public String getAverageLastNBuilds(){
        Long result = getAverageLastBuildsNumber();
        return result != null ? Util.getTimeSpanString(result) :"N/A.";
    }

    public String getAverageAllSuccessBuilds(){
        RunList<Run> buildList = project.getBuilds();
        Long sum = 0L;
        Long result = null;
        List<Run> completedBuildList = buildList.stream()
                .filter(x -> x.getResult().equals(Result.SUCCESS))
                .collect(Collectors.toList());
        System.out.println("N builds completos: " + completedBuildList.stream().count());
        System.out.println("Primer build de la lista: " + completedBuildList.get(0).getId());
        System.out.println("Duracion primer build: " + completedBuildList.get(0).getDuration());
        for (Run run:completedBuildList) {
            sum += run.getDuration();
        }
        if(completedBuildList.stream().count() != 0){
            result = sum/completedBuildList.stream().count();
        }
        return result != null ? Util.getTimeSpanString(result) :"N/A.";
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

    public String getViewSpeedStatus(){
        String healthy = "";
        long maxTime = getMaxTimeToBuild() * 60000;
        System.out.println("max time: " + maxTime);
        System.out.println("media: " + getAverageLastBuildsNumber());
        if(getAverageLastBuildsNumber() <= (maxTime-(maxTime*0.3))){
            healthy="sun";
        }
        else if(getAverageLastBuildsNumber() >= (maxTime+(maxTime*0.3))){
            healthy="bolt";
        }
        else if(getAverageLastBuildsNumber() <= (maxTime-(maxTime*0.1))){
            healthy="cloud-sun";
        }
        else if(getAverageLastBuildsNumber() >= (maxTime+(maxTime*0.1))){
            healthy="cloud-showers-heavy";
        }
        else {
            healthy="cloud";
        }
        return healthy;
    }

    public String getSvgTest(){
        return SvgTag.fontAwesomeSvgIcon("sun").render();

    }
}
