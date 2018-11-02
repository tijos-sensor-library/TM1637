package tijos.framework.sensor.tm1637;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;

/**
 * TM1637 GPIO communication handler  
 * @author lemon
 *
 */
class IOHandler {

	TiGPIO gpio;
	int clkPinId;
	int dataPinId;

	/**
	 * Initialize 
	 * @param gpio gpio port
	 * @param clkPinId clock pin id
	 * @param dataPinId data pin id
	 * @throws IOException 
	 */
	public IOHandler(TiGPIO gpio, int clkPinId, int dataPinId) throws IOException {

		this.gpio = gpio;
		this.clkPinId = clkPinId;
		this.dataPinId = dataPinId;

		gpio.setWorkMode(dataPinId, TiGPIO.OUTPUT_OD);
		gpio.setWorkMode(clkPinId, TiGPIO.OUTPUT_OD);

	}
	
	/**
	 * send start signal to TM1637
	 * @throws IOException
	 */
	public void start() throws IOException {
		gpio.writePin( clkPinId, 1);// send start signal to TM1637
		gpio.writePin( dataPinId, 1);
		gpio.writePin( dataPinId, 0);
		gpio.writePin( clkPinId, 0);
	}

	/**
	 * End of transmission
	 * @throws IOException
	 */
	public void stop() throws IOException {
		gpio.writePin( clkPinId, 0);
		gpio.writePin( dataPinId, 0);
		gpio.writePin( clkPinId, 1);
		gpio.writePin( dataPinId, 1);
	}

	/**
	 * Write a byte to device
	 * @param wr_data data to be written
	 * @throws IOException
	 */
	public void writeByte(int wr_data) throws IOException {
		
		int i, count1 = 0;
		for (i = 0; i < 8; i++) // sent 8bit data
		{
			gpio.writePin( clkPinId, 0);

			if ((wr_data & 0x01) > 0) {
				gpio.writePin( dataPinId, 1);// LSB first
			}
			else
			{
				gpio.writePin( dataPinId, 0);
			}

			wr_data >>= 1;

			gpio.writePin( clkPinId, 1);

		}

		gpio.writePin( clkPinId, 0); // wait for the ACK
		gpio.writePin( dataPinId, 1);		
		gpio.writePin( clkPinId, 1);

		//check ack
		while (gpio.readPin(dataPinId) > 0) {
			count1 += 1;
			if (count1 == 10)
			{
				gpio.writePin( dataPinId, 0);
				count1 = 0;
			}
		}
	}

}
