package io.jenkins.plugins.sample;


import hudson.Util;
import hudson.model.*;
import hudson.util.RunList;
import io.jenkins.plugins.fontawesome.api.SvgTag;
import io.jenkins.plugins.storage.Constants;
import io.jenkins.plugins.storage.ReadUtil;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.commons.collections.iterators.ListIteratorWrapper;

import java.awt.*;
import java.util.*;
import java.util.List;
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

    public long getTimeRepairBuilds(){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.TIME_REPAIR_BUILDS).toString());
    }

    public long getNRepairBuilds(){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.N_REPAIR_BUILDS).toString());
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
        System.out.println("Resultado final MTTR: " + Util.getTimeSpanString(result));
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
        List<Run> completedBuildList = listBuildsSuccesfully();
        Long sum = 0L;
        Long result = null;
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

    public List<Run> listBuildsSuccesfully(){
        RunList<Run> buildList = project.getBuilds();
        List<Run> completedBuildList = buildList.stream()
                .filter(x -> x.getResult().equals(Result.SUCCESS))
                .collect(Collectors.toList());
        return completedBuildList;
    }

    public List<Long> getTimeBetweenRepairs(){
        final long[] repairCount = {0};
        final long[] repairTimeSumInMillis = {0};
        long[] repairTimeAux = {0};
        List<Long> repairTimeList= new ArrayList<>();
        RunList<Run> builds = project.getBuilds();
        builds.stream().sorted().forEach(build -> {
                    System.out.println(build.getId());
                    if(!Result.SUCCESS.equals(build.getResult())){
                        if(repairTimeAux[0] == 0){
                            repairTimeAux[0] = build.getStartTimeInMillis();
                            System.out.println("0 - repairTimeAux[0]: "+ repairTimeAux[0]);
                        }
                    } else {
                        if(repairTimeAux[0] != 0){
                            repairTimeSumInMillis[0] += (build.getStartTimeInMillis() - repairTimeAux[0]);
                            repairTimeList.add(repairTimeSumInMillis[0]);
                            repairCount[0]++;
                            repairTimeAux[0] = 0;
                            System.out.println("1 - repairCount[0]: "+ repairCount[0]);
                            System.out.println("2 - repairTimeAux[0]: "+ repairTimeAux[0]);
                            System.out.println("3 - repairTimeSumInMillis[0]: "+ repairTimeSumInMillis[0]);
                        }
                    }
                }
        );

        return repairTimeList;
    }

    public String getViewSpeedBuildStatus(){
        List<Run> completedBuildList = listBuildsSuccesfully().stream().limit(getLastNBuilds()).collect(Collectors.toList());
        long maxTime = getMaxTimeToBuild()*60000;
        double valueInc = (getLastNBuilds()/5);
        double cont = 0, formula=0.0;
        for (Run build:completedBuildList) {
            System.out.println(build.getDuration() );
            if(build.getDuration() > maxTime){
                cont=cont + valueInc;
            }
        }
        System.out.println("Total builds superan tiempo max: " + cont);
        formula = ((valueInc*getLastNBuilds())/5);
        return  getGenerateWeather(cont,formula);
        /*
        if( cont <= ((valueInc*getLastNBuilds())/5)*1 ) {
            healthy = "health-80plus.gif";
            System.out.println("sol " + cont + "" + ((valueInc*getLastNBuilds())/5)*1 );
        }
        else if( cont <= ((valueInc*getLastNBuilds())/5)*2 ){
            healthy="health-60to79.gif";
            System.out.println("sol nubes " + cont + "" + ((valueInc*getLastNBuilds())/5)*2 );
        }
        else if( cont <= ((valueInc*getLastNBuilds())/5)*3){
            healthy="health-40to59.gif";
            System.out.println("nubes " + + cont + "" + ((valueInc*getLastNBuilds())/5)*3);
        }
        else if( cont <= ((valueInc*getLastNBuilds())/5)*4 ){
            healthy="health-20to39.gif";
            System.out.println("nubes lluvia " + + cont + "" + ((valueInc*getLastNBuilds())/5)*4);
        }
        else if(cont <= ((valueInc*getLastNBuilds())/5)*5 ){
            healthy="health-00to19.gif";
            System.out.println("nubes truenos" + cont + "" + ((valueInc*getLastNBuilds())/5)*5);
        }
        return healthy;
         */
    }



    public String getViewSpeedRepairBuildStatus(){
        List <Long> timeList= getTimeBetweenRepairs();
        double time = getTimeRepairBuilds()*60.000;
        Collections.reverse(timeList);
        List<Long> lista =  timeList.stream().limit(getNRepairBuilds()).collect(Collectors.toList());
        double valueInc = (getNRepairBuilds()/5);
        double cont = 0, formula=0.0;
        for(Long value: lista ) {
            System.out.println("value: " + value);
            if (value > time) {
                cont = cont + valueInc;
            }
        }
        formula = ((valueInc*getLastNBuilds())/5);
        System.out.println("Time 25 y nBuilds 5 --> cont : " + cont);
        System.out.println("Time 25 y nBuilds 5 --> formula : " + formula);
        return  getGenerateWeather(cont,formula);
    }

    public String getGenerateWeather( double value1,double value2 ){

        String healthy = "";
        if( value1 <= value2*1) {
            healthy = "health-80plus.gif";
        }
        else if( value1 <= value2*2){
            healthy="health-60to79.gif";
        }
        else if( value1 <= value2*3){
            healthy="health-40to59.gif";
        }
        else if( value1 <= value2*4){
            healthy="health-20to39.gif";
        }
        else if( value1 <= value2*5){
            healthy="health-00to19.gif";
        }
        return healthy;
    }

    public String getSvgTest(){
        return SvgTag.fontAwesomeSvgIcon("sun").render();

    }
}
