const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/eventPublic/{eventId}')
    .onWrite((change, context) => {
        const notificationId = context.params.notificationId;
        const notificationData = change.after.val();

        const payload = {
            notification: {
                title: notificationData.title,
                body: notificationData.body,
                // Add other notification properties as needed
            },
        };

        return admin.messaging().sendToTopic(notificationId, payload)
            .then(() => {
                console.log('Notification sent successfully:', notificationId);
            })
            .catch(error => {
                console.error('Error sending notification:', error);
            });
    });