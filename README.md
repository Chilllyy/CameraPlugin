# Info
This is a plugin and mod combination I created to take in-game screenshots, sort of like an amusement park ride

depends on [Camera Plugin Helper](https://github.com/Chilllyy/CameraPluginHelper)

# Usage
1. Install the server plugin, make sure you have a free port to host the webserver 
2. Setup a minecraft account to autojoin the server and install the companion mod
3. make sure camera account has `camera.camera` permission node
4. run `/camera create (name)` **if this doesn't work, make sure you have the `camera.admin` permission
5. run `/camera setup (name) camera` *to set where the camera will be*
6. move to where players will stand to activate the camera and do `/camera setup (name) sense`
7. whenever players walk near that location it will take a photo *polls every 5 seconds*



## Command Usage (parentheses means required, square brackets means optional)
- /camera (setup | create | delete | rename | render | reload | overlay)
  - /camera setup (name) (range | camera | timer | sense)
    - /camera setup (name) range (range) **Sets sensor range (how far it detects players in)**
    - /camera setup (name) camera **Sets the camera location**
    - /camera setup (name) timer (timer) **Sets default timer countdown**
    - /camera setup (name) sense **Sets Sense Location (where camera checks for players to take photo)**
    - /camera setup (name) overlay (overlay filename) **Sets image overlay, all stored in the *data/public/overlays* folder**
  - /camera create (name) [static] **Creates Shot**
  - /camera delete (name) **Deletes Shot**
  - /camera rename (name) (New Name) **Renames Shot**
  - /camera render (name) [Countdown Timer] **Manually Renders Camera Shot**
  - /camera reload **Reloads Plugin**

### Permissions
- camera.admin
  - Allows use of /camera command
- camera.camera
  - Allows player to be a camera (if they have the companion mod installed)