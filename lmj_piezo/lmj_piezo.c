#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/init.h>
#include <asm/uaccess.h>
#include <asm/io.h>
#include <linux/ioport.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/mutex.h>

#define PIEZO_ADDRESS			0x05000050
#define PIEZO_ADDRESS_RANGE 	0x1000

#define DEVICE_NAME			"lmj_piezo"

static int piezo_usage = 0;
static unsigned long int *piezo_ioremap;
static unsigned char *piezo_addr;

static int lmj_piezo_open(struct inode * inode, struct file * file)
{
	if (piezo_usage == -1)
		return -EBUSY;

	piezo_ioremap = ioremap(PIEZO_ADDRESS, PIEZO_ADDRESS_RANGE);

	if ( check_mem_region( (unsigned long int) piezo_ioremap, PIEZO_ADDRESS_RANGE) /* != 0 */)
	{
		printk(KERN_WARNING "Can't get IO Region 0x%x\n", (unsigned int) piezo_ioremap);
		return -1;
	}
	
	request_mem_region( (unsigned long int) piezo_ioremap, PIEZO_ADDRESS_RANGE, DEVICE_NAME);
	piezo_addr = (unsigned char *)piezo_ioremap;
	piezo_usage = 1;

	return 0;
}

static int lmj_piezo_release(struct inode * inode, struct file * file)
{
	release_mem_region( (unsigned long int) piezo_ioremap, PIEZO_ADDRESS_RANGE);
	iounmap(piezo_ioremap);

	piezo_usage = 0;
	return 0;
}

/* not use
static ssize_t fpga_piezo_read(struct file * file, char * buf, size_t length, loff_t * ofs)
{
	printk("fpga_piezo_read, \n");
	
	return 0;
}
*/

static ssize_t lmj_piezo_write(struct file * file, const char * buf, size_t length, loff_t * ofs)
{
	unsigned char c;

	get_user(c, buf);

	*piezo_addr = c;
	
	return length;
}

/* not use
static DEFINE_MUTEX(fpga_piezo_mutex);
static long fpga_piezo_ioctl(struct file * file, unsigned int cmd, unsigned long arg){
	printk("fpga_piezo_ioctl, \n");
	
	switch(cmd){
		default:
			mutex_unlock(&fpga_piezo_mutex);
			return ENOTTY;
	}
	
	mutex_unlock(&fpga_piezo_mutex);
	return 0;
}
*/

static struct file_operations lmj_piezo_fops = {
	.owner = THIS_MODULE,
	.open = lmj_piezo_open,
	.release = lmj_piezo_release,
//	.read = lmj_piezo_read,
	.write = lmj_piezo_write,
//	.unlocked_ioctl = lmj_piezo_ioctl,
};

static struct miscdevice lmj_piezo_driver = {
	.minor = MISC_DYNAMIC_MINOR,
	.name = "lmj_piezo",
	.fops = &lmj_piezo_fops,
};

static int lmj_piezo_init(void){
	printk("lmj_piezo_init, \n");
	
	return misc_register(&lmj_piezo_driver);
}

static void lmj_piezo_exit(void){
	printk("lmj_piezo_exit, \n");

	misc_deregister(&lmj_piezo_driver);
	
}

module_init(lmj_piezo_init);
module_exit(lmj_piezo_exit);

MODULE_AUTHOR("Hanback");
MODULE_DESCRIPTION("lmj_piezo driver test");
MODULE_LICENSE("Dual BSD/GPL");
