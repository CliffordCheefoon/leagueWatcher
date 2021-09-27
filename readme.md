# League Watcher
A java (maven) project that keeps track of league players and sends per-game achievements as well as weekly top performers to a discord text channel via webhooks.


## Requirements
- Google Firestore
- Riot Games API key 
- A runtime Environment with scheduler (currently using Google function with Cloud scheduler)


## Google Firestore Format
### 3 Collections
 - summonerList (required) - each document in represents a player, the document name is the summoner name of the player
 - runRecord (generated)  - logs execution history
 - previousMatchList (generated) - records the matchId of all the previously processed matches for the summonerList