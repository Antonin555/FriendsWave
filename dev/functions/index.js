const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

// Fonction pour envoyer une notification
exports.sendNotification = functions.database.ref('event/eventPublic/{eventId}').onWrite((snapshot, context) => {
  const notification = {
    title: 'Titre de la notification',
    body: 'Corps de la notification',
  };



  return admin.messaging().sendToTopic(message, notification)
    .then((response) => {
      console.log('Notification envoyée avec succès :', response);
    })
    .catch((error) => {
      console.error('Erreur lors de l\'envoi de la notification :', error);
    });
});