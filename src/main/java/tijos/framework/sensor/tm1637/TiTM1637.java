package tijos.framework.sensor.tm1637;

import java.io.IOException;
import tijos.framework.devicecenter.TiGPIO;

/**
 * TM1637 LED driver for TiJOS
 *
 */
public class TiTM1637 {

	public final int ADDR_AUTO = 0x40;
	public final int ADDR_FIXED = 0x44;
	public final int STARTADDR = 0xc0;

	public final int BRIGHT_DARKEST = 0;
	public final int BRIGHT_TYPICAL = 2;
	public final int BRIGHTEST = 7;

	static final byte TubeTab[] = { 0x3f, 0x06, 0x5b, 0x4f, 0x66, 0x6d, 0x7d, 0x07, 0x7f, 0x6f, 0x77, 0x7c, 0x39, 0x5e,
			0x79, 0x71 };// 0~9,A,b,C,d,E,F

	int Cmd_SetData = ADDR_AUTO;
	int Cmd_SetAddr = STARTADDR;
	int Cmd_DispCtrl = BRIGHT_TYPICAL + 0x88;

	/**
	 * _PointFlag=1:the clock point on
	 */
	boolean _PointFlag;

	/**
	 * data io handler
	 */
	IOHandler ioHandler = null;

	public TiTM1637(TiGPIO gpio, int clkPinId, int dataPinId) throws IOException {
		ioHandler = new IOHandler(gpio, clkPinId, dataPinId);

		this.set(BRIGHT_TYPICAL, ADDR_AUTO, STARTADDR);
	}

	/**
	 * clear the display
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException {
		clearDisplay();
	}

	/**
	 * display function.Write to full-screen.
	 * 
	 * @param DispData
	 * @throws IOException
	 */
	void display(byte[] DispData) throws IOException {

		if (DispData.length < 4)
			throw new IOException("data length is less than 4");

		byte[] SegData = new byte[4];
		for (int i = 0; i < 4; i++) {
			SegData[i] = DispData[i];
		}

		coding(SegData);

		ioHandler.start(); // start signal sent to TM1637 from MCU
		ioHandler.writeByte(ADDR_AUTO);
		ioHandler.stop();
		ioHandler.start();
		ioHandler.writeByte(Cmd_SetAddr);
		for (int i = 0; i < 4; i++) {
			ioHandler.writeByte(SegData[i]);
		}
		ioHandler.stop();
		ioHandler.start();
		ioHandler.writeByte(Cmd_DispCtrl);
		ioHandler.stop();
	}

	/**
	 * Display the specified data to the position
	 * @param BitAddr position 
	 * @param DispData data to be displayed
	 * @throws IOException
	 */
	void display(int BitAddr, int DispData) throws IOException {

		int SegData = coding(DispData);

		// start signal sent to TM1637 from MCU
		ioHandler.start(); 
		ioHandler.writeByte(ADDR_FIXED);
		ioHandler.stop(); 
		ioHandler.start(); 
		ioHandler.writeByte(BitAddr | 0xc0);
		ioHandler.writeByte(SegData);
		ioHandler.stop(); 
		ioHandler.start(); 
		ioHandler.writeByte(Cmd_DispCtrl);
		ioHandler.stop(); 

	}

	/**
	 * Clear the screen
	 * @throws IOException
	 */
	void clearDisplay() throws IOException {
		display(0x00, 0x7f);
		display(0x01, 0x7f);
		display(0x02, 0x7f);
		display(0x03, 0x7f);
	}

	/**
	 * take effect the next time it displays
	 * 
	 * @param brightness
	 * @param SetData
	 * @param SetAddr
	 */
	void set(int brightness, int SetData, int SetAddr) {
		Cmd_SetData = SetData;
		Cmd_SetAddr = SetAddr;

		// Set the brightness and it takes effect the next time it displays.
		Cmd_DispCtrl = 0x88 + brightness;
	}

	/**
	 * Whether to light the clock point ":". To take effect the next time it
	 * displays.
	 * 
	 * @param PointFlag
	 */
	void point(boolean PointFlag) {
		_PointFlag = PointFlag;
	}

	void coding(byte[] dispData) {
		int PointData;
		if (_PointFlag)
			PointData = 0x80;
		else
			PointData = 0;

		for (int i = 0; i < 4; i++) {
			if (dispData[i] == 0x7f)
				dispData[i] = 0x00;
			else
				dispData[i] = (byte) (TubeTab[dispData[i]] + PointData);
		}
	}

	byte coding(int dispData) {
		int pointData;
		if (_PointFlag)
			pointData = 0x80;
		else
			pointData = 0;
		if (dispData == 0x7f)
			dispData = (byte) (0x00 + pointData);// The bit digital tube off
		else
			dispData = (byte) (TubeTab[dispData] + pointData);
		return (byte) dispData;
	}
}
