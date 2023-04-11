const functions = require("firebase-functions");

// // Create and deploy your first functions
// // https://firebase.google.com/docs/functions/get-started
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/event/eventPublic/{eventId}')
    .onCreate((snapshot, context) => {
        const event = snapshot.val();
        const payload = {
            notification: {
                title: 'Nouvel événement',
                body: 'Un nouvel événement a été créé : ' + event.title,
            },
        };

        // Envoyer la notification au topic "allUsers"
        return admin.messaging().sendToTopic('allUsers', payload);
    });