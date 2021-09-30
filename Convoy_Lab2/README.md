# CIS 4515 - Lab 2

The following lab was completed using:
- Android Studio "Arctic Fox", v2021.3.1, Patch 2
- Pixel 2 emulator (API 28, 11GB of allotted disk size)

Here are the steps that I followed in order to complete this lab:
1. Ported Lab 1 code base (https://github.com/lehmansarahm/CIS4515_2021/tree/main/Convoy_Lab1)
2. Added handlers for new "Join", "Leave", and "Update" APIs (/api/ConvoyAPI.java)
3. Manually configured project to support FCM (DID NOT use the built-in wizard)
    - Added Google GMS Services classpath dependency to project-level "build.gradle" file
    - Added Google GMS Services plugin id to app-level "build.gradle" file
    - Added Firebase dependencies to app-level "build.gradle" file
4. Created new service class to receive FCM messages (/services/FcmService.java)