package tijos.framework.sensor.tm1637;

import java.io.IOException;
import java.util.Date;

import tijos.framework.devicecenter.TiGPIO;

/**
 * LED Clock sample for TM1637 LED  
 *
 */
public class TiTM1637Sample
{
    public static void main( String[] args )
    {
    	System.out.println("Hello TiJOS!");

		try {
			
			int gpioPort = 0; //GPIO Port 
			int clkPinId = 0; // CLK Pin ID
			int dioPinId = 1; // DIO Pin ID
			
			TiGPIO gpioClk = TiGPIO.open(gpioPort, clkPinId, dioPinId);

			TiTM1637 tm1637 = new TiTM1637(gpioClk, clkPinId, dioPinId);
			tm1637.init();

			byte[] dispData = new byte[4];

			boolean ClockPoint = false;
			while (true) {

				Date dateobj = new Date();

				dispData[0] = (byte) (dateobj.getHours() / 10);
				dispData[1] = (byte) (dateobj.getHours() % 10);

				dispData[2] = (byte) (dateobj.getMinutes() / 10);
				dispData[3] = (byte) (dateobj.getMinutes() % 10);
				tm1637.display(dispData);

				Thread.sleep(500);

				ClockPoint = !ClockPoint;
				tm1637.point(ClockPoint);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
