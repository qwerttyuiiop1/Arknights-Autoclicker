# Arknights-Autoclicker
<img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/55974797-81f7-4d54-928d-239a37a6a9f1" width = 250 height = 250> <br/>
Android autoclicker for Arknights. <br/>
This app is based heavily on OCR to detect the current state of the game and click on the screen accordingly. <br/>
It is designed to be lightweight and standalone, and does not require root access.

## Setup
1. Download the latest release from the releases tab
2. Install the app on your phone
3. Click on the app icon to start the app
4. Enable permissions for the app


Display over other apps:
<div style="display: flex; flex-direction: row;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/573b8b71-16cf-4d3b-8827-0bda2a69b3ff" style="width: 33%;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/cf498de3-beb6-43b5-8d29-ae9316f88167" style="width: 33%;">
</div>

Accessibility:
<div style="display: flex; flex-direction: row;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/9e4b4a68-cf30-4891-baeb-6dc88df250ea" style="width: 25%;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/2f22293a-8051-4dd3-a8d3-f326007a6bd2" style="width: 25%;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/e65efa9e-abb4-4f63-9a57-fe913675d937" style="width: 25%;">
</div>

## Features
### Floating Bubble:
A floating bubble that can be moved around the screen. 
Clicking on the bubble will open the app menu and pause the autoclicker.
Recommended to be placed at the top left of the screen.
<div style="display: flex; flex-direction: row;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/836df156-d491-4a13-93d7-e283f6fabcf1" style="width: 33%;">
    <img src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/8419bd6f-93f7-4faf-a4a0-617a469ac8d7" style="width: 33%;">
</div>

<br/>

### Autobattle: 
This feature repeatedly enters the stage until either the sanity runs out or stopped.
<br/>
Start in this screen or any of succeeding screens
![264052613-3d2f6c5c-a779-493e-ad3a-4dbf51008c0d](https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/b2c16a61-3715-481f-9eb2-fb674f634bc9)

<br/>

### Recruitment / Expedite Recruitment: 
Automatically select the best tag combination for the highest rarity operator.
The app ignores operators 2* and below. The timer is set to 7:40 for combinations that guarantees a
minimum of a 3* operator, and 9:00 for 4*. <br/>
When a combination guarantees a 5* or higher operator, the app will stop this task and notify the user.
In this case, please use a calculator and manually confirm the tags. <br/>
Expedite Recruitment is Recruitment using expedite tickets.
<br/>
Start in this screen or any of the succeeding screens
![264054690-4201ac2d-cb9c-4b95-8059-1d1ad5e64251](https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/3bec3132-30b0-4ed6-b8c0-db6cd41ae3dc)

<br/>

### Base: 
Automatically collect all the resources from the base (factory, trading post, and morale).
Automatically assign operators to the dorms, and fill up empty slots in the base. <br/>
This app does not collect clues from the reception room, and spend drones.
<br/>
Start in any of the following screens
![266899609-a0697d76-8fcb-49ad-b925-985773b05996](https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/5dfc128a-bb48-4f41-9269-7d28d8575a22)
<img width="1402" alt="266899756-7625ae6d-92df-4f1e-ad48-ba47e70e505a" src="https://github.com/qwerttyuiiop1/Arknights-Autoclicker/assets/64955571/ab0ad942-6f75-4735-b709-6b27e28b3309">

<br/>

### Screenshot: 
Take a screenshot without the overlay
### Github: 
Open the github page in the browser

## Bug Report
If you encounter any bugs, please report them in the issues tab. Please include the following information:
* Phone model
* Android version
* Steps to reproduce the bug
* Screenshots of the bug (use the screenshot feature in the app)

## TODO
- [ ] remove click on overlay
- [ ] add support for screen sizes other than 2400 x 1080
- [ ] ~~add support for split screen~~
- [ ] some way to update the tag combinations
- [ ] code cleanup (ongoing)

## Credits
* ML kit: https://developers.google.com/ml-kit/vision/text-recognition/android
* Floating Bubble View: https://github.com/TorryDo/Floating-Bubble-View
* Flexbox Layout: https://github.com/google/flexbox-layout
* Recruitment tag combinations: https://aceship.github.io/AN-EN-Tags/akhr.html
* App icon: zumbz
* ples support me :>
