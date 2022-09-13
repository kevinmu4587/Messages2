Project Messages2 (App name: Schism)
---  
  
### Summary  
An Android text-based story game based on sending text messages. This game simulates a text message application, where the player "chats" with others, following a pre-defined story.  Each message comes with a few options, some of which will play a major role in the direction the story follows.   
  
### Technical Details  
This application is based on the Android Room MVVM architecture, using an SQL database to store sent messages. `SharedPreferences` are used to save specific game settings,  such as player names. The game saves the current state of the conversations whenever the player closes the app, and reloads the state upon their return (this took a long time to get just right!).
The UI is built using `xml` and the code is written in `Java`. The app is designed to be an "engine," as it reads a local `JSON` file containing the all the messages that will be used in the story. The `JSON` file specifies the order and pre-determined NPC responses to the options the player chooses. So, by supplying a different  `JSON` file, an entirely new story can be told!

### Setup
This is just the source code.

### Screenshots
Basic gameplay screenshot - a chat with an NPC:  
<img src="Screenshot%201.jpg"  width=26% height=26%>  

Choosing a message to send:  
<img src="Screenshot%202.jpg"  width=22% height=22%>
