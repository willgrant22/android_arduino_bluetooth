#include <SoftwareSerial.h>
#include <DHT.h>

#define DHTPIN 2     // what pin we're connected to
#define DHTTYPE DHT11

DHT dht(DHTPIN, DHTTYPE);

SoftwareSerial BlueTooth(5, 6); // (TXD, RXD) of HC-06

char BT_input; // to store input character received via BT.
String x = "Now LED is ON";
String y = "Now LED is OFF";

int chk;
float hum;  //Stores humidity value
float temp;
String h,h2,p,t,t2,f;

void setup()
{
  pinMode(13, OUTPUT);     // Arduino Board LED Pin
  BlueTooth.begin(9600);
  Serial.begin(9600);
  dht.begin();
}

void loop()
{
  if (BlueTooth.available())

  {
    BT_input = (BlueTooth.read());
    Serial.println(BT_input);    
    
    if (BT_input == 'a')
    {
      digitalWrite(13, HIGH);
      BlueTooth.println(x);
      Serial.println(x);
            
    }
    else if (BT_input == 'b')
    {
      digitalWrite(13, LOW);
      BlueTooth.println(y);
      Serial.println(y);
      
    }
    else if (BT_input == 'c')
    {
      hum = dht.readHumidity();
      temp= dht.readTemperature();
      h = " Humidity: ";
      t = "Temperature: ";
      f= "F";
      t2 = temp*9/5 +32;
      p = "%";
      h2 = t + t2+ f + h + hum + p;
      BlueTooth.println(h2);
      
      
    }
    else if (BT_input == 'd')
    {
      BlueTooth.println("Now bluetooth is OFF");
      BlueTooth.end();
      
    }
    else if (BT_input == '?')
    {
      BlueTooth.println("Send 'a' to turn LED ON");
      BlueTooth.println("Send 'b' to turn LED OFF");
    }
    
  }
}
