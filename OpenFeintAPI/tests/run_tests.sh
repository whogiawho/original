#!/bin/sh

rm -f local.properties
android update project -p .
echo "adb.device.arg=-e" >> local.properties

# if there isn't an AVD running, make sure there is
if [ ! -e ~/.android/avd/${BAMBOO_AVD}.avd/cache.img.lock ]; then
        echo "Starting AVD ${BAMBOO_AVD}..."
        emulator -avd ${BAMBOO_AVD} &

        # Note: adb wait-for-device sucks.  Sometimes drops out early,
        # sometimes hangs indefinitely.  not suitable for unattended use.
        # thanks Google!

        # adb -e wait-for-device
        sleep 60
fi

ant run
