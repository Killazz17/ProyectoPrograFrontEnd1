Frontend configuration notes

- Added src/main/resources/app.properties with api.host and api.port (defaults: localhost:7070).
- Added Presentation.Config.ApiConfig to load configuration from app.properties.
- Updated Services.BaseService to read host/port from ApiConfig.

How to change backend address

- Edit src/main/resources/app.properties and set api.host and api.port, then rebuild.

Note about missing image

- If the application still crashes on startup with an ImageIcon NullPointerException, the LoginView expects an image resource in the classpath. Place the image at the correct path under src/main/resources (for example src/main/resources/images/logo.png) matching the path used by the UI, then rebuild.

---