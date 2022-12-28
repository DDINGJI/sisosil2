#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <asm/uaccess.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/mutex.h>

#define LED_ADDRESS			0x05000020
#define LED_ADDRESS_RANGE 	0x1000

#define DEVICE_NAME			"lmj_led"

static int led_usage = 0;
static unsigned long int *led_ioremap;
	
static int lmj_led_open(struct inode * inode, struct file * file){
	if (led_usage == -1)
			return -EBUSY;

		led_ioremap = ioremap(LED_ADDRESS, LED_ADDRESS_RANGE);

		if ( check_mem_region( (unsigned long int) led_ioremap, LED_ADDRESS_RANGE) /* != 0 */)
		{
			printk(KERN_WARNING "Can't get IO Region 0x%x\n", (unsigned int) led_ioremap);
			return -1;
		}

		request_mem_region( (unsigned long int) led_ioremap, LED_ADDRESS_RANGE, DEVICE_NAME);
		led_usage = 1;

		return 0;
}

static int lmj_led_release(struct inode * inode, struct file * file){
	release_mem_region( (unsigned long int) led_ioremap, LED_ADDRESS_RANGE);
		iounmap(led_ioremap);

		led_usage = 0;
		return 0;
}

/*not use
static ssize_t lmj_led_read(struct file * file, char * buf, size_t length, loff_t * ofs){
	printk("lmj_led_read, \n");
	
	return 0;
}
*/

static ssize_t lmj_led_write(struct file * file, const char * buf, size_t length, loff_t * ofs){
	unsigned char value;

	get_user(value, buf);

	*(unsigned short int *)led_ioremap = value;
	
	return length;
}

/*not use
static DEFINE_MUTEX(lmj_led_mutex);
static long lmj_led_ioctl(struct file * file, unsigned int cmd, unsigned long arg){
	printk("lmj_led_ioctl, \n");
	
	switch(cmd){
		default:
			mutex_unlock(&lmj_led_mutex);
			return ENOTTY;
	}
	
	mutex_unlock(&lmj_led_mutex);
	return 0;
}
*/

static struct file_operations lmj_led_fops = {
		.owner = THIS_MODULE,
		.open = lmj_led_open,
		.release = lmj_led_release,
	//	.read = lmj_led_read,
		.write = lmj_led_write,
	//	.unlocked_ioctl = lmj_led_ioctl,
};

static struct miscdevice lmj_led_driver = {
		.minor = MISC_DYNAMIC_MINOR,
		.name = "lmj_led",
		.fops = &lmj_led_fops,
};

static int lmj_led_init(void){
	printk("lmj_led_init, \n");
	
	return misc_register(&lmj_led_driver);
}

static void lmj_led_exit(void){
	printk("lmj_led_exit, \n");

	misc_deregister(&lmj_led_driver);
	
}

module_init(lmj_led_init);
module_exit(lmj_led_exit);

MODULE_AUTHOR("Myeongji");
MODULE_DESCRIPTION("lmj_led driver test");
MODULE_LICENSE("Dual BSD/GPL");
