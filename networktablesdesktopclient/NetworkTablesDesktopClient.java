package networktablesdesktopclient;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;

public class NetworkTablesDesktopClient {
	
  public static void main(String[] args) {
    new NetworkTablesDesktopClient().run();
  }

  public void run() {
	  
	SerialPort serial = null;
	  
	try {
		serial = setup(); // Sets up the serial port with the arduino, should just be first available
	} catch (IOException e) {
		System.out.println("IO exception on serial open");
		e.printStackTrace();
	} catch (InterruptedException e) {
		System.out.println("interrupted serial opening");
		e.printStackTrace();
	}
	  
    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable table = inst.getTable("display");
    
    NetworkTableEntry xEntry = table.getEntry("x");
    NetworkTableEntry yEntry = table.getEntry("y");
    NetworkTableEntry driverControl = table.getEntry("driverControl");
    
    inst.startClientTeam(294);
    inst.startDSClient();  // recommended if running on DS computer; this gets the robot IP from the DS
    
    // Listeners for specific keys
    table.addEntryListener("X", (tbl, key, entry, value, flags) -> { System.out.println("X changed value: " + value.getValue()); }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    
    yEntry.addListener(event -> {System.out.println("Y changed value: " + event.value.getValue()); }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    
    // Listener for an entire table
    //table.addEntryListener((tbl, key, entry, value, flags) -> {System.out.println("Key: " + key + " Value: " + value.getValue() + " Flags: " + flags);
    //}, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);
    
    int i = 0;
    
    while (true) {
    	
      try {
        Thread.sleep(100);	// Updates every 100 millisecond
      } catch (InterruptedException ex) {
        System.out.println("interrupted");
        return;
      }
      
      double x = xEntry.getDouble(0.0);
      double y = yEntry.getDouble(0.0);
      boolean driver = driverControl.getBoolean(true);
      
      double upscale = x * 10;
      
      
      // Write the x value to the serial port
      try {
		write(serial, (byte)((driver) ? 1 : 0)); // Write a 1 if the robot is being driven by humans, 0 if by vision
      } catch (IOException e) {
		System.out.println("IO exception on write");
		e.printStackTrace();
      } catch (InterruptedException e) {
		System.out.println("Write operation interrupted");
		e.printStackTrace();
      }
      
      System.out.println("X: " + x + " Y: " + y);
      i++;
    }
  }
  
  /**
   * Sets up a serial port
   * @param commPort The String ID of the port to open
   * @return The opened serial port
   * @throws IOException
   * @throws InterruptedException
   */
  public SerialPort setup(String commPort) throws IOException, InterruptedException {
	  SerialPort serial = SerialPort.getCommPort(commPort); // Need to update according to testing to what the COM port is
	  serial.setComPortParameters(9600, 8, 1, 0); // These should be arduino default parameters
	  serial.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written
	  
	  if (serial.openPort()) System.out.println("Port open");
	  else System.out.println("Failed to open port");
	  
	  return serial;
  }
  
  /**
   * Sets up the first serial port available
   * @return The opened serial port
   * @throws IOException
   * @throws InterruptedException
   */
  public SerialPort setup() throws IOException, InterruptedException {
	  SerialPort serial = SerialPort.getCommPorts()[0]; // Need to update according to testing to what the COM port is
	  serial.setComPortParameters(9600, 8, 1, 0); // These should be arduino default parameters
	  serial.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be written
	  
	  if (serial.openPort()) System.out.println("Port open");
	  else System.out.println("Failed to open port");
	  
	  return serial;
  }
  
  /** 
   * Writes a byte to the serial console
   * @param serial SerialPort to write to
   * @param b Byte to write
   * @throws IOException
   * @throws InterruptedException
   */
  public void write(SerialPort serial, byte b) throws IOException, InterruptedException {
	  serial.getOutputStream().write(b);
	  serial.getOutputStream().flush();
	  
	  System.out.println("Sent: " + b);
	  Thread.sleep(1000);
  }
  
  public void close(SerialPort serial) {
	  if(serial.closePort()) {
		  System.out.println("Port closed");
	  } else {
		  System.out.println("Failed to close port");
	  }
  }
}