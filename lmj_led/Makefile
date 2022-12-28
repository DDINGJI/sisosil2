.SILENT:

include C:/AndroXStudio/Tools/make_adb

export ARCH=arm
export CROSS_COMPILE=arm-linux-androideabi-

ifneq ($(KERNELRELEASE),)
obj-m := lmj_led.o
else
KDIR := /platform/linux
all:
	$(MAKE) -C $(KDIR) M=$(shell pwd) modules

	echo 
	echo "**** Install:" /system/lib/modules/lmj_led.ko "****"
	$(ADB) push lmj_led.ko /system/lib/modules/
	$(ADB) shell chmod 644 /system/lib/modules/lmj_led.ko
	echo 
	echo "**** Load Module:" /system/lib/modules/lmj_led.ko "****"
	$(ADB) shell toolbox rmmod lmj_led > /dev/null
	$(ADB) shell insmod /system/lib/modules/lmj_led.ko
	$(ADB) shell lsmod | grep lmj_led
	echo

endif

clean:
	rm -f *.symvers
	rm -f *.ko
	rm -f *.o
	rm -f *.mod.c
	rm -f *.order
	rm -f .*.cmd
	rm -rf .tmp_versions
