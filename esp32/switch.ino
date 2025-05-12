#include <WiFi.h>
#include <ESPAsyncWebServer.h>

// Replace with your Wi-Fi credentials
const char* ssid = "Airtel_Ramya";
const char* password = "Graphtheory9528*";

// Define GPIO pins for 8 relays
const int relays[] = {26, 25, 33, 32, 27, 14, 12, 13}; // Adjust pins if needed

AsyncWebServer server(80);

void setup() {
  Serial.begin(115200);

  // Setup relay pins
  for (int i = 0; i < 8; i++) {
    pinMode(relays[i], OUTPUT);
    digitalWrite(relays[i], HIGH); // Relay OFF (active LOW)
  }

  // Connect to Wi-Fi
  WiFi.begin(ssid, password);
  Serial.print("Connecting to WiFi..");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nConnected to WiFi");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  // Create server endpoints for each relay
  for (int i = 0; i < 8; i++) {
    String onPath = "/relay" + String(i + 1) + "/on";
    String offPath = "/relay" + String(i + 1) + "/off";

    server.on(onPath.c_str(), HTTP_GET, [i](AsyncWebServerRequest *request){
      digitalWrite(relays[i], LOW); // Relay ON
      request->send(200, "text/plain", "Relay " + String(i + 1) + " ON");
    });

    server.on(offPath.c_str(), HTTP_GET, [i](AsyncWebServerRequest *request){
      digitalWrite(relays[i], HIGH); // Relay OFF
      request->send(200, "text/plain", "Relay " + String(i + 1) + " OFF");
    });
  }

  server.begin();
}

void loop() {
 
}