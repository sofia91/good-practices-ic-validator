package io.jenkins.plugins.sample;


import hudson.Util;
import hudson.model.*;
import hudson.util.RunList;
import io.jenkins.plugins.storage.Constants;
import io.jenkins.plugins.storage.ReadUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ValidateMenuAction implements Action {

    private Project project;
    private String style;

    public ValidateMenuAction(Project project) {
        this.project = project;
        this.style = "display:none;";
    }

    public long getMaxTimeToBuild (){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            //  System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.MAX_TIME_TO_BUILD).toString());
    }

    public long getLastNBuilds(){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            // System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.LAST_N_BUILDS).toString());
    }

    public long getTimeRepairBuilds(){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            // System.out.println("No se ha podido leer el fichero");
        }
        return Long.valueOf(properties.get(Constants.TIME_REPAIR_BUILDS).toString());
    }

    public long getNRepairBuilds(){
        Properties properties = ReadUtil.getJobProperties(project, Constants.VALIDATE_PROPERTIES);
        if (properties == null) {
            //  System.out.println("No se ha podido leer el fichero");
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
        return result != null? Util.getTimeSpanString(result) + " dÃ­as." : "Sin builds erroneos.";
    }

    public String getMtbf(){
        Long result = null;
        if(failBuildsCount() != 0){
            result = Long.valueOf(daysSince(project.getFirstBuild().getTimestamp()))/
                    failBuildsCount();
        }

        return result != null ? result + " dÃ­as.":"Sin builds erroneos.";
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
        return getMediaWeather();
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
                            // System.out.println("0 - repairTimeAux[0]: "+ repairTimeAux[0]);
                        }
                    } else {
                        if(repairTimeAux[0] != 0){
                            repairTimeSumInMillis[0] += (build.getStartTimeInMillis() - repairTimeAux[0]);
                            repairTimeList.add(repairTimeSumInMillis[0]);
                            repairCount[0]++;
                            repairTimeAux[0] = 0;
                            //    System.out.println("1 - repairCount[0]: "+ repairCount[0]);
                            //    System.out.println("2 - repairTimeAux[0]: "+ repairTimeAux[0]);
                            //    System.out.println("3 - repairTimeSumInMillis[0]: "+ repairTimeSumInMillis[0]);
                        }
                    }
                }
        );

        return repairTimeList;
    }

    public List<String> getViewSpeedBuildStatus() {
        List<String> array = new ArrayList<String>();
        List<Run> completedBuildList = listBuildsSuccesfully().stream().limit(getLastNBuilds()).collect(Collectors.toList());
        long maxTime = getMaxTimeToBuild() * 60000;
        double cont = 0;
        long result = 0;
        String description = "";
        if ( getLastNBuilds() != 0 ) {
            for (Run build : completedBuildList) {
                if (build.getDuration() > maxTime) {
                    cont++;
                }
            }
            result = (long) (((getLastNBuilds() - cont) * 100) / getLastNBuilds());
            description = "Estabilidad: " + (long)cont + " de los " + getLastNBuilds() + " builds no cumplen la practica 7:\"Obtener un build rÃ¡pido - M.F\"";
        }else{
            array.add("");
        }

        String tabla = "<div data-placement=\"right\"><table><thead><tr style=\"background: #fdffce;border: 2px solid #fcffc4;padding:2%;\"><th scope=\"col\">%</th><th scope=\"col\">Description</th></tr></thead><tbody><tr style=\"background: #ffffff;\"><td>"+result +"</td><td>"+description+"</td></tr></tbody></table></div>";
        String titlePractise="PrÃ¡ctica 7: Obtener build rÃ¡pido - M.F";

        array.add(getGenerateWeather(result));//imagen
        array.add(tabla); //tabla
        array.add(String.valueOf(result));//score
        array.add(titlePractise); //titulo de la practica
        array.add(String.valueOf(getNRepairBuilds() - cont)); //numero de builds que no superan el maximo
        array.add(String.valueOf(getMaxTimeToBuild())); //tiempo maximo de reaparaciÃ³n
        return array;


    }
    public List<String> getViewSpeedRepairBuildStatus() {
        List<String> array = new ArrayList<String>();
        List<Long> timeList = getTimeBetweenRepairs();
        Collections.reverse(timeList);
        List<Long> list = timeList.stream().limit(getNRepairBuilds()).collect(Collectors.toList());
        double time = getTimeRepairBuilds() * 60.000, cont = 0;
        String description = "";
        long result = 0;

        if (getNRepairBuilds() != 0) {
            for (Long value : list) {
                System.out.println("value: " + value);
                if (value > time) {
                    cont++;
                }
            }
            result = (long) (((getNRepairBuilds() - cont) * 100) / getNRepairBuilds());
            description = "Estabilidad: " + (long)cont + " de los " + getNRepairBuilds() + " builds no cumplen la practica 6: \"Corregir builds fallidos - M.F\"";
        }
        else{
            array.add("");
        }
        String tabla = "<div data-placement=\"right\"><table><thead><tr style=\"background: #fdffce;border: 2px solid #fcffc4;padding:2%;\"><th scope=\"col\">%</th><th scope=\"col\">Description</th></tr></thead><tbody><tr style=\"background: #ffffff;\"><td>"+result +"</td><td>"+description+"</td></tr></tbody></table></div>";
        String titlePractise="PrÃ¡ctica 6: Reparar builds fallidos immediatamente - M.F";
        String cardTitle = String.valueOf(result) + titlePractise;
        String texto = "" ;
        array.add(getGenerateWeather(result));//imagen
        array.add(tabla);//tabla
        array.add(cardTitle); //titulo con score
        array.add(titlePractise); //titulo de la practica
        array.add(String.valueOf((getNRepairBuilds() - cont))); //numero de builds que no superan el maximo
        array.add(String.valueOf(getTimeRepairBuilds())); //tiempo maximo de reaparaciÃ³n
        return array;
    }

    public List<String> getStatusWeatherJenkins(){

        List<String> lista = new ArrayList<String>();
        HealthReport report = project.getBuildHealth();
        String[] getImage= report.getIconClassName().split("-");
        String icono = getImage[1]+"-"+getImage[2]+".gif";
        String tabla = "<div data-placement=\"right\"><table><thead><tr style=\"background: #fdffce;border: 2px solid #fcffc4;\"><th scope=\"col\">%</th><th scope=\"col\">Description</th></tr></thead><tbody><tr style=\"background: #ffffff;\"><td>"+report.getScore() +"</td><td>"+report.getDescription()+"</td></tr></tbody></table></div>";
        String titlePractice ="Estatus del proyecto (Jenkins)";
        lista.add(icono); //Nombre icono image
        lista.add(tabla); //tabla
        lista.add(String.valueOf(report.getScore()));//score
        lista.add(titlePractice);//Nombre de la practica


        //  lista.add(String.valueOf(report.getScore())); //% weather
        //   lista.add(report.getDescription()); //description
        return lista;
    }

    public String getMediaWeather(){
        double p0 = Double.parseDouble(getStatusWeatherJenkins().get(2));
        double p1 = Double.parseDouble(getViewSpeedRepairBuildStatus().get(2));
        double p2 = Double.parseDouble(getViewSpeedBuildStatus().get(2));
        double sumTotal = (p0+p1+p2)/3;
        System.out.println("total" + sumTotal +"p0, p1, p2 : "+ p0 );
        String result = getGenerateWeather(sumTotal);

        return result;
    }

    public String getGenerateWeather( double score ){
        String healthy = "";

        if (score <= 20) {
            healthy="health-00to19.gif";
        } else if (score <= 40) {
            healthy="health-20to39.gif";
        } else if (score <= 60) {
            healthy="health-40to59.gif";
        } else if (score <= 80) {
            healthy="health-60to79.gif";
        } else {
            healthy = "health-80plus.gif";
        }
        return healthy;
    }



}
