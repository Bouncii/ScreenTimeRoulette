package fr.bounci.screentimeroulette.domain.data

//Chaque tours on reçoit un appUsage et une liste de PlayerToVote
//On doit envoyer l'id de celui dont on suspecte d'etre le titulaire de l'appUsage
data class PlayerToVote(
    val id: String,
    val pseudo: String
)
