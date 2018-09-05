# AlignmentPlugin
VISNAB: Visualization of Network Alignments using BioFabric

### Overview

__VISNAB__ is a BioFabric plugin that allows you to visualize network alignments.

### Preparations

__VISNAB__ requires that the user first install BioFabric Version 2 (Beta), which is currently under development. Note that the current production Version 1.0 of BioFabric is not designed to support plugins; __Version 2 is required__. BioFabric is written in Java, and can be run on your Windows, Mac, or Linux computer. For Windows, you will first need to install the Java Runtime Environment (JRE), which can be downloaded from 
[here](http://www.java.com/) (click the "Free Java Download" button). For the Mac, the executable already contains the JRE. 

BioFabric Version 2 (Beta) is currently available either as a zip file for Windows (named `BioFabric20B1.zip`) or as a mountable disk image for Macs (named `BioFabricInstallImageV2BetaRel1.dmg`). These are both available from the [BioFabric GitHub repository release page](https://github.com/wjrl/BioFabric/releases/tag/V2.0Beta1a).

### Installing the Plugin

The __VISNAB__ plugin is a Java JAR file.  The compiled JAR file is available on the [VISNAB GitHub repository release page](https://github.com/wjrl/AlignmentPlugin/releases/tag/v1.0.0.1). To install it, download the `sVISNAB-V1.0.0.1.jar` file, then simply drop that file into a directory 
on your computer. (Note that there should be only one copy of the VISNAB JAR file in that directory.) Start BioFabric, and use
 __File->Set Plugin Directory...__ to point to the *directory* where you have placed `sVISNAB-V1.0.0.1.jar`. Restart BioFabric, and the 
 VISNAB commands will appear in the __Tools->Network Alignment__ menu. 