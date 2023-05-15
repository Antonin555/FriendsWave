const functions = require('firebase-functions');
const admin = require('firebase-admin');
const capitalizeSentence = require('capitalize-sentence');
const badWordsList = require('french-badwords-list').array;

admin.initializeApp(functions.config().firebase);

exports.moderatorEvent = functions.database.ref('/event/eventPublic/{eventId}').onWrite((change) => {
  const message = change.after.val();
  console.log(message.description)

  if (message && message.description && !message.sanitized) {
    functions.logger.log('Retrieved message content: ', message);
    
    const moderated_description = moderateMessage(message.description);
    const moderated_name_event = moderateMessage(message.name)
    

    functions.logger.log('Message has been moderated. Saving to DB: ', moderated_description);
    return change.after.ref.update({
      description: moderated_description,
      name:moderated_name_event,
      sanitized: true,
      moderated: message.description !== moderated_description,
    });
  }
  return null;
});



exports.moderatorUser = functions.database.ref('/user/{userId}').onWrite((change) => {
  const message = change.after.val();
  console.log(message.pseudo)

  if (message && message.pseudo && !message.sanitized) {
    functions.logger.log('Retrieved message content: ', message);
    
    const moderated_pseudo = moderateMessage(message.pseudo);

    

    functions.logger.log('Message has been moderated. Saving to DB: ', moderated_pseudo);
    return change.after.ref.update({
      pseudo: moderated_pseudo,
      sanitized: true,
      moderated: message.description !== moderated_pseudo,
    });
  }
  return null;
});


function moderateMessage(message) {
  if (containsSwearwords(message)) {
    functions.logger.log('User is swearing. Moderating...');
    message = moderateSwearwords(message);
  }
  return message;
}

function containsSwearwords(message) {
  const cleanedMessage = message.replace(new RegExp(badWordsList.join('|'), 'gi'), '***');
  return cleanedMessage !== message;
}

function moderateSwearwords(message) {
  return message.replace(new RegExp(badWordsList.join('|'), 'gi'), '***');
}

exports.notifHostSubscribeEventPublic = functions.database.ref(`/user/{userid}/hostPendingRequestEventPublic`).onUpdate((change, context) => {
  const host_token_ref = admin.database().ref(`/user/${context.params.userid}/token`);

  return host_token_ref.once('value').then((snapshot) => {
    const host_token = snapshot.val();

    console.log("Le token de l'administrateur : " + host_token);

    const payload = {
      notification: {
        title: 'Someone wants to participate in your event',
        body: 'Check your notifications'
      }
    };

    return admin.messaging().sendToDevice(host_token, payload);
  });
});

exports.sendNotifFriendRequest = functions.database.ref(`/user/{userid}/friendRequest`).onUpdate((change, context) => {

  const host_token_ref = admin.database().ref(`/user/${context.params.userid}/token`);

  return host_token_ref.once('value').then((snapshot) => {
    const host_token = snapshot.val();

    console.log("Le token de l'administrateur : " + host_token);

    const payload = {
      notification: {
        title: 'Someone wants add you as friend',
        body: 'Check your notifications'
      }
    };

    return admin.messaging().sendToDevice(host_token, payload);
  });



});

exports.sendNotificationToSpecificUser = functions.database.ref(`/event/eventPrivate/{userid}/{eventId}`)
    .onUpdate((snapshot, context) => {
        const child_mail = [];

        const listInscritsRef = admin.database().ref(`/event/eventPrivate/${context.params.userid}/${context.params.eventId}/listInscrits`);

        listInscritsRef.once('value', (snapshot) => {
            snapshot.forEach((childSnapshot) => {
                child_mail.push(childSnapshot.val());
            });
        }).then(() => {
            // Get the device tokens for the recipients
            const promises = [];
            child_mail.forEach(email => {
                const userRef = admin.database().ref('/user').orderByChild('email').equalTo(email);
                const promise = userRef.once('value').then((snapshot) => {
                    snapshot.forEach((childSnapshot) => {
                        const recipientDeviceToken = childSnapshot.child('token').val();

                        // Send the notification
                        const payload = {
                            data: {
                                title: 'yo boy',
                                body: 'Notification list inscrit',
                                click_action: 'HOME_FRAGMENT'
                            }
                        };
                        promises.push(admin.messaging().sendToDevice(recipientDeviceToken, payload));
                    });
                });
                promises.push(promise);
            });

            return Promise.all(promises);
        });
    });





    exports.sendNotification = functions.database.ref('/event/eventPublic/{eventPublicId}').onUpdate(async (change, context) => {
        const eventData = change.after.val();
        const topic = "nom-du-topic";
        console.log("TEEEEEEEEEEEEEEEEEEEEEESSSTTTTTTTTTTTTTTTT ::: " + change.after.val())
        const postData = change.after.val(); //3
        console.log("HHHHHHHEEYYY POSTDATA : " + postData.description)
        
        const message = {
  
            data: {
                title: "TTTTTTTTTTTTTEEEEST 2222 ",
                body:"Decouvrez l'evenement : " + postData.name,
                message: "HEEEEEEEEEEEEEEEEEEEEEEEEEEEE",
                click_action: "OPEN_ACTIVITY"
              }


        };
        try {
            const response = await admin.messaging().sendToTopic(topic, message);
            console.log("Successfully sent message:", message);
        } catch (error) {
            console.log("Error sending message:", error);
        }
    });
    

exports.scheduledFunction = functions.pubsub
    .schedule('every 24 hours')
    .onRun((context) => {
      deleteOldItems();
      return null;
    });
  
  function deleteOldItems() {
    const ref = admin.database().ref('/event/eventPublic');
    const now = Date.now();
    const date_expiration = now + (24 * 3600 * 1000);
    const oldItemsQuery = ref.orderByChild('timeStamp').endAt(date_expiration);
    oldItemsQuery.once('value', function(snapshot) {
      const updates = {};
      snapshot.forEach(function(child) {
        updates[child.key] = null;
      });
      ref.update(updates);
    });
  }



  exports.sendNotifAdminEventPublic = functions.database.ref(`/event/eventPublic/{eventId}/pendingRequestEventPublic`)
    .onUpdate((snapshot, context) => {
        const child_mail = [];

        const admin_event = database().ref(`/event/eventPublic/${context.params.eventId}/admin`);

        const userRef = admin.database().ref('/user').orderByChild('uid').equalTo(admin_event);
        const recipientDeviceToken = userRef.child('token').val();

                // Send the notification
                const payload = {
                    notification: {
                        title: 'Votre event a du succés',
                        body: 'Une nouvelle personne souhaite rejoindre votre event'
                    }
                };
                const response = admin.messaging().sendToTopic(topic, message);
                promises.push(admin.messaging().sendToDevice(recipientDeviceToken, payload));
    });
        



