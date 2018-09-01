#! /bin/bash

KEYDIR=$1
CODESIGN=$2
VER=$3

rm META-INF/.DS_Store
jar cvf AlignmentPlugin.jar META-INF org

#echo -n "Enter the key:"
#read -s PERM
#echo

# Sign the jar file:
#jarsigner -storetype pkcs12 -storepass ${PERM} -keystore ${KEYDIR}/isbcert.p12 -tsa http://timestamp.comodoca.com/rfc3161 \
#  -signedJar ${CODESIGN}/AlignmentPlugin-V${VER}.jar ${CODESIGN}/AlignmentPlugin.jar "institute for systems biology's comodo ca limited id"
