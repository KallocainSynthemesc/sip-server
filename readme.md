# SIP SERVER

L'objectif de ce projet est de réussir la mise en place d'un nouveau serveur SIP qui doit envoyer des messages d'événements à tous les clients connectés et qui doit s'intégrer avec le reste de l'environnement architectural.

## BUSINESS LOGIC

Nous commençons par les bases. Le message SIP SUBSCRIBE est utilisé comme un moyen pour le client de communiquer qu'il est disponible (en ligne).

    1. Le client envoie une demande de souscription avec un en-tête expire.
    2. Le serveur SIP délègue l'authentification au serveur BIMI.
    3. Si l'authentification est valide, le serveur SIP envoie un message ACCEPTED au client et un NOTIFY suivant pour vérifier que le client est capable de traiter les messages NOTIFY et sauve le client s'il envoie une réponse OK.
    4. Si l'authentification n'est pas valide, le serveur SIP envoie un message UNAUTHORIZED.


Un message JMS du serveur est converti en un message PUBLISH puis envoyé au client.

    1. Un message JMS du serveur.
    2. La conversion de JMS en SIP PUBLISH (via Apache camel)
    3. Le message est enregistré dans la base de données pour un accès ultérieur.

La séquence suivante est le résultat d'un événement de souscription ou de publication réussi.

    1. S'il y a un message pour un client, le message est envoyé via une requête NOTIFY au client s'il est en ligne/authentifié.
    2. Le message est supprimé de la base de données lorsque le client envoie une réponse OK.
    3. Le client est retiré de la liste des clients qui sont en ligne/authentifiés lorsque le client envoie une réponse d'erreur.
