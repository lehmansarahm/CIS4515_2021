# CIS 4515 - Lab 1

The following lab was completed using:
- Android Studio "Arctic Fox", v2021.3.1, Patch 2
- Pixel 2 emulator (API 28, 11GB of allotted disk size)

Here are the steps that I followed in order to complete this lab:
1. Start a new Android Studio project with a "basic" activity template to handle the login / register functionality
   - Renamed to LoginActivity, LoginFragment, RegisterFragment, nav_login_register as appropriate
   - Updated the corresponding layouts (login form, registration form) and associated buttons
   - Tested navigation between empty fragments with LogCat print statements
2. Added classes to handle API calls
   - Abstract class (BaseAPI) to handle common logic such as submitting a POST request
   - Dedicated class to handle account-based operations (register, login, logout)
   - Dedicated class to handle convoy-based operations (create, end)
3. Add a secondary activity using the "Google Maps" template