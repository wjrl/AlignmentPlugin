# AlignmentPlugin
VISNAB: Visualization of Network Alignments using BioFabric

### Overview

__VISNAB__ is a BioFabric plugin that allows you to visualize network alignments.

### Preparations

__VISNAB__ is written as a BioFabric plugin, and requires that your first install __BioFabric Version 2 Beta Release 2__ (or higher). 
Note that the current production Version 1.0 of BioFabric is *not designed to support plugins*; at least
__Version 2 Beta Release 2 is required__. BioFabric is written in Java, and can be run on your Windows, Mac, 
or Linux computer. Starting with Version 2 Beta Release 2, the Java runtime (OpenJDK 12) is <i>bundled with</i> BioFabric on 
all platforms, so a separate Java download is no longer required.

BioFabric Version 2 Beta Release 2 is currently available either as a zip file for Windows (named `BioFabric20B2.zip`),
as a mountable disk image for Mac (named `BioFabricInstallImageV2BetaRel2.dmg`), or as a tgz file for Linux (named `BioFabric-2.0.B.2.tgz`).
These are all available from the [BioFabric GitHub repository release page](https://github.com/wjrl/BioFabric/releases/tag/V2.0Beta2). 

* __Windows:__  Unzip the `BioFabric20B2.zip` file as a folder on your desktop. Go into the folder. If you want to install 
BioFabric in the Windows __Program Files__ folder, you will need to run the BioFabricInstaller as an Admin user. To do that, 
right-click on the BioFabricInstaller program and choose __Run as administrator__. If you are not an Admin, you will need to 
install it somewhere within your home directory by changing the install location as part of the installation. Once the installation is 
complete, you can right-click on the BioFabric program and select __Create shortcut__, then e.g. drag it to your desktop.

* __Mac:__ Double-click the downloaded `BioFabricInstallImageV2BetaRel2.dmg` disk image file to mount it, and then 
open a Finder window for the mounted disk. From the Finder window, drag the BioFabric icon inside onto your desktop (or, if you prefer, 
into your Applications folder). If __Mac Gatekeeper__ has been set to restrict to only __App Store__, you will need to 
launch __System Preferences__, click on __Security & Privacy__, and on the __General__ tab set 
__Allow apps downloaded from: App Store and identified developers__. If you take this step, you probably want to reset it 
back to __App Store__ after starting BioFabric the first time. 

* __Linux:__ Untar the `BioFabric-2.0.B.2.tgz` file (`tar xvzf BioFabric-2.0.B.2.tgz`). The shell script
to run the program is `BioFabric/BioFabricV2BetaRel2.sh`.


### Installing the Plugin

The __VISNAB__ plugin is a Java JAR file. This compiled JAR file is available on the [VISNAB GitHub repository release page](https://github.com/wjrl/AlignmentPlugin/releases/tag/v1.1.0.0). To install it, download the `sVISNAB-V1.1.0.0.jar` file, then simply drop that file into
 a directory on your computer. (Note that there should be only one copy of the VISNAB JAR file in that directory.) 
 Start BioFabric, and use __File->Set Plugin Directory...__ to point to the *directory* where you have placed 
`sVISNAB-V1.1.0.0.jar`. Restart BioFabric, and the VISNAB commands will appear in the __Tools->Network Alignment__ menu.

