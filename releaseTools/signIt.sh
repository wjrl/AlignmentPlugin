#! /bin/bash

KEYDIR=$1
CODESIGN=$2
VER=$3

# Sign the jar file:
jarsigner -keystore ${KEYDIR}/ISBSignCert.jks -tsa http://timestamp.comodoca.com/rfc3161 \
-signedJar ${CODESIGN}/sVISNAB-V${VER}.jar ${CODESIGN}/VISNAB-V${VER}.jar "isbcert" 