# InTouch
This repo includes the source code of the project InTouch.

![InTouch icon](GetInTouch_icon.png)

InTouch is a mobile app developed for Android devices. The app has a simple approach of saving contact information of the people you get in touch with at events or meetups, in an easy and simple way, just by scanning the business card of the person you meet, and saving the person's name, email, phone, a note about how you met (to help you memorize them people!!!). The app's core feature is scanning business cards and populating the details on them to the respective fields.
All the contacts that you save using InTouch, are stored in your phone's contacts local database with your other contacts, and can be accessed from your default contacts app.

# Description

InTouch is a simply designed app that helps you stay in touch with the people that you meet, and want them to be in your network.

__The Main Activity__

The below screenshot is the first activity (screen), which is started when the app is launched (except for the onboarding experience, that is displayed only at the first time the app was installed). Is shows the list of contacts that you have stored in your phone. This app makes use of Android's content providers to get the contacts list stored in your phone. The app is designed to show only contacts that have email addresses, meaning that if you have a contact that doesn't have an email address then it won't show up on the list (**NOTE:** _**the contacts that are shown in the below screenshot are fake as well as all of their information, including mine !!**_ ). 
Additionally, the contacts' info shown in this activity includes only name, email and phone number, which makes the app simple and gives a quick outline of the contact.

The Search Dialog at the top of the screen helps you find a contact from the list quickly. Also, the "Plus" floating button at the bottom of the screen helps you add a new Touch (new contact); when pressed, it will take you to the new contact activity.

![main activity](Screenshot_mainactivity.png)

__The New Contact Activity__

The below screenshot represents the new contact activity that will start when you presse the "Plus" floating point or choose "Create Contact" from the drop-down menu.

Again, for the simplicity of the app, this activity has inputs for name, email, phone number, a note, and business card's picture. You can add a new contact by scanning a business card, choosing a business card from the phone's image gallery, or by typing the contact's info in the fields. After filling in the fields, when you press the "Plus" floating point. 

The app uses implicit intents to store contacts to the phone, by using the phone's default contacts app.

![new activity](Screenshot_newTouch.png)

__The phone's Default Contacts App Activity__

When you type the contact's information in the specified fields and then press the "Plus" floating point, the app starts the phone's default contacts app with the activity responsible for creating a new contact (as shown in the screenshot below). Then you can finish the creation of the new contact or add more information about the contact. When done creating the contact or pressing Back, you will go back to InTouch's mainactivity.

![phone contact app activity](Screenshot_phoneContactApp.png)

__The Contact's Edit Activity__

You can click on any of his contacts in the main activity to see more info about that specific contact; when you do this, the below activity starts. This activity provides some functions to be done on the contact's information, like: sharing, editing or deleting.

The share button in the action bar allows you to share some information about the contact with other contacts, using any of the apps that have the share action, which you have installed in the phone, like the Messaging app.

The delete button in the action bar allows you to delete the selected contact and all its information definitely  from the phone !!

The floating point in the bottom right side of the screen allows you to edit the selected contact. If pressed, you will be able to continue editing the contact using the phone's default contacts app. Once done or by pressing Back, you will go back to InTouch's mainactivity.

![edit activity](Screenshot_editTouch.png)

__The Share Button__

When you press the share button, a menu of all the available apps that can share the contacts information are provided, which will allow you to choose the convenient app. An example of the available sharing apps is shown in the below screenshot.

![share](Screenshot_shareTouch.png)

# Try InTouch

You can try InTouch in two different ways. One, by downloading the source code, and then build the app using Android Studio (which is the IDE used to develop InTouch). Two, by downloading the apk file included in this repo under the name of __InTouch_debug.apk__ (this is a debug apk) and then install it in your phone.

# Needed Permissions

As InTouch focuses on user contacts, it needs permission to access user contacts stored locally in the phone.

# Third Party Libraries/APIs

InTouch is pretty simple (Actually that's what the app is intended for. Simplicity!!); However, a couple of features make the app more enjoyable. First, the integration of a search bar to allow you to search for contacts in the contacts list; for this purpose, we used the [Android Search Dialog][1], which was really useful with some cool features.

Second, we used Google's [ML Kit][2], Machine learning for mobile developers kit, to allow you to scan business cards and retrieve the contact's information from them.

## Further resources

[Android's search dialog setup][1]

[Machine learning for mobile developers Kit][2]


[1]: https://developer.android.com/guide/topics/search/search-dialog.html "Title"
[2]: https://firebase.google.com/docs/ml-kit/android/recognize-text "Title"
