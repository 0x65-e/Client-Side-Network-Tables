/*-------------------------------------------------------------------------------------
 * Template file for 4-pin I2C OLED display, e.g. from Geekcreit
 * using Adafruit SSD1306 driver and GFX libraries.
 * Tutorial:
 * https://startingelectronics.org/tutorials/arduino/modules/OLED-128x64-I2C-display/
 *-------------------------------------------------------------------------------------*/
#include <Wire.h>
#include <Adafruit_SSD1306.h>
#include <Adafruit_GFX.h>

// OLED display TWI address
#define OLED_ADDR   0x3C

// reset pin not used on 4-pin OLED module
Adafruit_SSD1306 display(-1);  // -1 = no reset pin

// 128 x 64 pixel display
#if (SSD1306_LCDHEIGHT != 64)
#error("Height incorrect, please fix Adafruit_SSD1306.h!");
#endif

void setup() {
  // initialize and clear display
  display.begin(SSD1306_SWITCHCAPVCC, OLED_ADDR);
  display.clearDisplay();
  display.display();

  Serial.begin(9600);
  while (!Serial) {
    ; // Wait for port to connect
  }

  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.print("Init done");

  display.display();
  
}

void loop() {
  // put your main code here, to run repeatedly:

  if (Serial.available() > 0) {
    display.clearDisplay();
    display.setCursor(0, 0);
    byte incomingByte = 0;
    incomingByte = Serial.read();
    if (incomingByte != -1) { // -1 means no data
      display.print(incomingByte);
      //Serial.print(incomingByte);
      display.display();
    }
  }

}
