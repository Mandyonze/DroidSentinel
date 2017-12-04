<a name="top"></a>
<a href="https://github.com/borjalor/DroidSentinel"><img src="https://img.shields.io/badge/build-progress-blue.svg" alt="DroidSentinel-Return0;"></a><br>
<a href="https://github.com/borjalor/DroidSentinel/blob/master/LICENSE"><img src="https://img.shields.io/badge/License-GPL3-blue.svg" alt="License"></a><br>

# Return0 - DroidSentinel

![DroidSentinel Logo](/DroidSentinel_Logo.png)

DroidSentinel es una aplicación desarrollada durante el Hackathon celebrado en Santander en el evento [Cybercamp](https://cybercamp.es/competiciones/hackathon) en el año 2017. A líneas generales, Droidsentinel es un analizador de tráfico para dispositivos Android potencialmente comprometidos como parte de una botnet orientado a detectar ataques DDoS.

## Contenidos

- [Sobre DroidSentinel](#sobre-droidsentinel)
- [Ataques de DDoS](#ataques-de-denegación-de-servicio)
- [Requisitos](#requisitos)
- [Instalación](#requisitos)
- [Otros](#otros)
- [Sobre Nosotros](#sobre-nosotros)



##  Sobre DroidSentinel

DroidSentinel es una aplicación sencilla diseñada para Android está destinada para cualquier tipo de usuario. **Se trata de una herramienta que tiene como objetivo principal el análisis de los flujos de tráfico de salida en busca de indicios de actividades maliciosas**, en particular aquellas que desenmascaren su posible participación en ataques DDoS. DroidSentinel notifica a tiempo real las actividades sospechosas realizadas.  

##  Ataques de Denegación de Servicio

En primer lugar, DroidSentinel tiene como motivación principal el aumento de los ataques de Denegación de Servicio Distribuidos, **¿Qué es un ataque DDoS?** Es un ataque a un sistema de computadoras o red que causa que un servicio o recurso sea inaccesible a los usuarios legítimos. Normalmente provoca la pérdida de la conectividad con la red por el consumo del ancho de banda de la red de la víctima o sobrecarga de los recursos computacionales del sistema atacado. 

## Requisitos
Para poder iniciar nuestra aplicación se necesitan los siguientes programas:

* AndroidStudio 

  Android Studio es el entorno de desarrollo integrado oficial para la plataforma Android y utilizado por nuestro proyecto.

* NOX Emulator 

  Emulador utilizado para la ejecución de nuestra aplicación

* TCPDUMP 

  Tcpdump necesario para el análisis y sniffing de paquetes. Tcpdump es necesario instalarlo en nuestro emulador para que se cree el       archivo con la información de los paquetes.

## Instalación

Para llevar a cabo la ejecución de nuestra aplicación, es necesario cargar el proyecto en el entorno de trabajo Android Studio.
También es necesario el emulador Nox en la que descargaremos la terminal o consola, que es una aplicación que te permite, mediante órdenes escritas realizar todo tipo de operaciones.
La terminal es necesaria para poder instalar el tcpdump para el sniffing de paquetes.

Para poder instalar tcpdump en nuestro emulador, debemos ejecutar los siguientes comandos en la terminal:
 
 Adjuntamos el link de los distintos elementos necesarios para la instalación y ejecución de nuestra aplicación:
 
 * ##### [Descargar Android Studio](https://developer.android.com/studio/index.html?hl=es-419) #####
 * ##### [Descargar Nox Emulator](https://es.bignox.com/) #####
 * ##### [Configuración Tcpdump](https://josetrochecoder.wordpress.com/2013/11/04/installing-tcpdump-for-android/) #####
 

## Otros

Fuentes de información:

**DDoS**

  *Autores:* P. Zhang, H. Wang, C. Hu, C. Lin

  *Paper:* "On Denial of Service Attacks in Software Defined Networks"

  *Publicación:* IEEE Network, Vol. 30 (6), pp. 28-33, December 2016

**Prediction Intervals (Umbrales Adapativos)**

  *Autores:* R. J. Hyndman, A. B. Koehler, J. K. Ord, R.D. Snyder

  *Paper:* "Prediction intervals for exponential smoothing state space models"

  *Publicación:* Journal of Forecasting, vol. 24, pp. 17-37, 2005.

**Anomalías en el tráfico**

  *Autores:* V. Chandola, A. Banerjee, V. Kumar

  *Paper:* "Anomaly Detection: A Survey"

  *Publicación:* ACM Computing Surveys vol. 41, issue 3, article no. 15, July 2009

**Flash Crowd**(análisis de tráfico legítimo)

  *Autores:* S. Bhatia, D. Schmidt, G. Mohay, A. Tickle

  *Paper:* "A framework for generating realistic traffic for Distributed Denial-of-Service attacks and Flash Events",

  *Publicación:* Computers & Security, vol. 40, no. 1, pp. 95-107, February 2014

##  Sobre Nosotros

Somos estudiantes de cuarto año de Ingeniería Informática en la Universidad Complutense de Madrid, actualmente completamos nuestros estudios con la colaboración en el proyecto SelfNet, llevado a cabo por el Grupo de Análisis, Seguridad y Sistemas perteneciente al departamento de Ingeniería de Software e Inteligencia Artificial de la UCM.

El equipo está compuesto por:  

* ##### [Andrés Herranz González](https://github.com/AndresHG) #####
* ##### [Borja Lorenzo Fernández](https://github.com/borjalor) #####
* ##### [Diego Maestre Vidal](https://github.com/voar) #####
* ##### [Guillermo Rius García](https://github.com/GuilleRius) #####

 
 
