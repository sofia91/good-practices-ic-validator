# good-practices-ic-validator

[![Build Status](https://ci.jenkins.io/job/Plugins/job/good-practices-ic-validator-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/good-practices-ic-validator-plugin/job/master/)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/good-practices-ic-validator-plugin.svg)](https://github.com/jenkinsci/good-practices-ic-validator-plugin/graphs/contributors)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/good-practices-ic-validator.svg)](https://plugins.jenkins.io/good-practices-ic-validator/Changelog.txt)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/good-practices-ic-validator-plugin.svg?label=changelog)](https://github.com/jenkinsci/good-practices-ic-validator-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/good-practices-ic-validator.svg?color=blue)](https://plugins.jenkins.io/good-practices-ic-validator)

## Introduction

Plugin de validación de buenas prácticas IC de Martin Fowler.
Especificamente validará las practicas:
6. Corregir builds fallidos inmediatamente
7. Obtener build rápido


## Getting started

Este plugin monitoriza el tiempo de build para validar su velocidad 
acorde a las buenas practicas de integracion continua definidas por Martin Fowler.

Generara un informe donde realizando una media de todas las subidas al día 
deberá ser menor que el parametro definido por el jefe de proyecto (valor predetermonado será de max 10min).

Mostrar en el informe un histograma por semanas, meses.
Muestra un mensaje en verde comunicando que se cumple con la buena practica de M.F "Obtener build rápido".
En caso de que se cambie de estado de validacion debe enviar un correo al jefe de proyecto
ASUNTO : Bueno | Malo
Correo : <definido_por_jefe_proyecto>
Fecha y Hora
Informe histograma
Tambien puede ser configurado por el Jefe de prouyecto el envio de correo
mediante la insercion de un cron.

=====================================================================

Tambien valida la buena practica de M.F "Corregir builds fallidos inmediatamente".
Base: Cada vez que un build falla bloquea las subidas al jenkins
Capturará la hora de fallo del build y la fecha del build correcto y calculará la diferencia
Parametro de tiempo de reaparacion del build lo define el jefe de proyecto (predeterminado 10min)


## Issues

TODO Decide where you're going to host your issues, the default is Jenkins JIRA, but you can also enable GitHub issues,
If you use GitHub issues there's no need for this section; else add the following line:

Report issues and enhancements in the [Jenkins issue tracker](https://issues.jenkins-ci.org/).

## Contributing

TODO review the default [CONTRIBUTING](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file and make sure it is appropriate for your plugin, if not then add your own one adapted from the base file

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

