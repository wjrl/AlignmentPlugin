# AlignmentPlugin
VISNAB: Visualization of Network Alignments using BioFabric

### Overview

__VISNAB__ is a BioFabric plugin that allows you to visualize network alignments.

### Preparations

**IMPORTANT NOTE:** BioFabric Version 2.0 Beta Release 2 has just been released (June 10, 2019). The V1 Release of __VISNAB__ is not compatible
with Version 2.0 Beta Release 2; it will be updated shortly. Builds from the master branch are currently compatible.

__VISNAB__ is written as a BioFabric plugin, and requires that your first install BioFabric Version 2 (Beta), which is currently under development. 
Note that the current production Version 1.0 of BioFabric is not designed to support plugins; __Version 2 is required__. BioFabric is written in Java, 
and can be run on your Windows, Mac, or Linux computer. For Windows, you will first need to install the Java Runtime Environment (JRE), which 
can be downloaded from [here](http://www.java.com/) (click the "Free Java Download" button). For the Mac, the executable already contains the JRE.

BioFabric Version 2 (Beta) is currently available either as a zip file for Windows (named `BioFabric20B1.zip`) or as a mountable disk image 
for Macs (named `BioFabricInstallImageV2BetaRel1.dmg`). These are both available from the 
[BioFabric GitHub repository release page](https://github.com/wjrl/BioFabric/releases/tag/V2.0Beta1a). 

* __Windows:__ Unzip the <`BioFabric20B1.zip` file and drag the `BioFabric.exe` file out of the archive and 
drop it on your desktop. Double-clicking on the desktop 
icon will start the program running. On Windows, you will probably need to agree to run software from an unknown publisher, or read a 
message that "Windows protected your PC", that the app is unrecognized, and you will need to click on the __More info__ link to be able to choose
the option to __Run Anyway__. (Note: The executable file *has been signed*, but the signing certificate has currently expired.) 


* __Mac:__ Double-click the downloaded `BioFabricInstallImageV2BetaRel1.dmg` disk image file to mount it, and then 
open a Finder window for the mounted disk. From the Finder window, drag the BioFabric icon inside onto your desktop (or, if you 
prefer, into your Applications folder). Both the disk image and the BioFabric program itself have been signed. However, if __Mac Gatekeeper__ 
has been set to restrict to only __App Store__, you will need to launch __System Preferences__, 
click on __Security & Privacy__, and on the __General__ tab set __Allow apps downloaded from: App Store and identified developers__. 
If you take this step, you may wish to reset it back to __App Store__ after starting BioFabric the first time. 

### Installing the Plugin

The __VISNAB__ plugin is a Java JAR file. This compiled JAR file is available on the [VISNAB GitHub repository release page](https://github.com/wjrl/AlignmentPlugin/releases/tag/v1.0.0.1). To install it, download the `sVISNAB-V1.0.0.1.jar` file, then simply drop that file into
 a directory on your computer. (Note that there should be only one copy of the VISNAB JAR file in that directory.) 
 Start BioFabric, and use __File->Set Plugin Directory...__ to point to the *directory* where you have placed 
`sVISNAB-V1.0.0.1.jar`. Restart BioFabric, and the VISNAB commands will appear in the __Tools->Network Alignment__ menu.
</b>
