![Cover Image](zinger_seller_app_cover_with_text.jpg)

<p align="center">
  <strong>
    <a href="https://zinger.pw/app">website</a>
    •
    <a href="https://zinger.pw/app/docs">docs</a>
    •
    <a href="https://zinger.pw">framework</a>
  </strong>
</p>

<p align="center">
  <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-green"></a>
  <a href="https://zinger.pw/app/docs"><img alt="Documentation" src="https://img.shields.io/badge/code-documented-brightgreen.svg?style=flat-square"></a>
  <a href="https://github.com/harshavardhan98/Zinger-Seller-App/pulls"><img alt="PRs Welcome" src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square"></a>
  <a href="https://github.com/harshavardhan98/Zinger-Seller-App/pulls"><img alt="Contributions Welcome" src="https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat-square"></a>

</p>

<h1 align="center">
  Welcome to Zinger Partner Android App
</h1>

This is an open source food ordering application developed to showcase the capabilities of <a href="https://zinger.pw" target="_blank">Zinger Framework</a>. This app can be used by restaurant owners to accept food orders within a campus, tech park, or mall. This application receives orders placed using <a href="https://github.com/shrikanth7698/Zinger-Android-App" target="_blank">Zinger Customer App</a>. Seller can accept orders, manage inventory and fulfill delivery and pickup orders using this app.

## Major libraries used
This Android Application is written in **Kotlin** with **MVVM Architecture** using the following libraries
- Android Architecture Components (ViewModel, LiveData)
- Koin
- Coroutines
- Retrofit
- Picasso

## Key app features
*  Manage and track orders
*  Edit menu items and dish availability with ease
*  Manage restaurant opening and closing time
*  Handle both delivery and pickup orders

## Getting involved
We encourage you to participate in this open source project. We love Pull Requests, Bug Reports, ideas, (security) code reviews or any other kind of positive contribution.

* [View the Docs](https://zinger.pw/app/docs).

* [View current Issues](https://github.com/harshavardhan98/Zinger-Seller-App/issues), [view current Pull Requests](https://github.com/harshavardhan98/Zinger-Seller-App/pulls).

* [Opt-in](https://join.slack.com/t/zinger-workspace/shared_invite/zt-e6xt0gc2-nBEy85RhEy7NZv3gWCt6Dg) to our slack channel for discussions.

* [Join](https://discord.gg/TqADaXV) our discord channel


## Build Instructions


1. Clone or Download the repository:

    ```shell
    git clone https://github.com/harshavardhan98/Zinger-Seller-App.git
    ```

2. Import the project into Android Studio

3. Before running the project, you need to setup the <a href="https://zinger.pw/setup" target="_blank">Zinger Backend Server</a>. Follow the intructions mentioned in this <a href="https://zinger.pw/setup" target="_blank">Link</a> to setup the backend for this android app. 

4. Change the base url in **build.gradle (app level)** to point to the server that you setup in previous step.

    ```gradle
    debug {
        //...
        buildConfigField "String", "CUSTOM_BASE_URL", '"YOUR_BASE_URL"'
    }
    ```

5. Create a firebase project, add this android app to that project:
<a href="https://firebase.google.com/docs/android/setup" target="_blank">Firebase setup</a>

6. Download **google-services.json** from the firebase project you created earlier and add it to the project under **app** folder

7. Enable Phone Number sign-in for your Firebase project

   * In the <a href="https://console.firebase.google.com/" target="_blank">Firebase console</a>, open the **Authentication** section.

   * On the **Sign-in Method** page, enable the **Phone Number** sign-in method.
   
8. Run the project into an emulator or a physical device. 

## Downloads

* Download the Zinger Partner <a href="https://drive.google.com/open?id=1dJ2R8eqsz2vc54rB4lZ8E6EN_oB1AuOD" target="_blank">apk</a>
* Download the Zinger <a href="https://drive.google.com/open?id=14D8b8t7Au3oz_TEp609Qyb3dvR5Vk7Xe" target="_blank">apk</a>

## Checkout

* <a href="https://github.com/shrikanth7698/Zinger-Android-App" target="_blank">Zinger Customer App Repo</a>
* <a href="https://zinger.pw" target="_blank">Zinger Framework</a>

## License
```
MIT License

Copyright (c) 2020 Harshavardhan P

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

[![Love](https://forthebadge.com/images/badges/built-with-love.svg)](https://zinger.pw/app) [![Android](https://forthebadge.com/images/badges/built-for-android.svg)](https://www.android.com) [![Developers](https://forthebadge.com/images/badges/built-by-developers.svg)](https://github.com/harshavardhan98/Zinger-Seller-App/graphs/contributors)
