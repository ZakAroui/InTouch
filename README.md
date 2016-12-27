# InTouch
This repo includes the source code of the project InTouch.

![InTouch icon] (GetInTouch_icon.png)

InTouch is a mobile app developed in Android using Android Studio. The app has a simple approach of saving the contact information of the people you get in touch with at events or meetups, in an easy and simple way, just by saving the person's name, email, phone, and a note about how did you met (to help you memorize your people!!!)

All the contacts that you save using InTouch are stored in you phone's contacts local database with your other contacts, and can be accessed from your default contacts or dialer apps.

# Description

InTouch is a simply designed app that helps you stay in touch with the people that you meet and want them to be in you network.

The below screenshot is the first avtivity (view), which is started when the app is launched. Is shows a list of contacts that you have stored in your phone. The app is designed to show only the contact that have email addresses, meaning that if you have a contact that doesn't have an email address then it won't showup on the list (**NOTE:** _**the contacts that are shown in the below screenshot are fake as well as all of their informations, even mine !!**_ ). 
Additionally, the contacts' information shown in this activity includes only name, email and phone number, which makes the app simple and gives quick review of the contact.

This app makes use of Android's content providers to get the contacts list stored in your phone.

Also, the "Plus" floating button in this activity is for adding new Touch (new contact). Once pressed, it will take you the the activity of creating a new Touch.

![main activity] (Screenshot_mainactivity.png)

This below screenshot represents the new contact activity that will start when a user presses the "Plus" floating point or chooses "Create Contact" from the drop-down menu.
Again, for the simplicity of the app, this activity has four inputs for name, email, phone number and a note. The user can add a new contact by typing the contact's information in the specified fields and then press the "Plus" floating point. 
The app uses implicit intents to store contacts to the phone, by using the phone's default contacts app.

![new activity](Screenshot_newTouch.png)



![phone contact app activity](Screenshot_phoneContactApp.png)

![edit activity](Screenshot_editTouch.png)

![share](Screenshot_shareTouch.png)

# Needed Permissions


# Future implementations

