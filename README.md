# TicTacToe- Distributed Java RMI Game

## Introduction

This project presents a distributed TicTacToe game developed in Java, utilizing bidirectional Remote Method Invocation (RMI) for communication. The game's graphical user interface is crafted with Java Swing, offering a visually appealing and user-friendly experience.

## Features

- **Basic TicTacToe Gameplay:** Includes the essential functionalities such as waiting for a match, making moves, winning, losing, and drawing.
- **Chat Functionality:** Enhance the gaming experience with an in-game chat feature, allowing players to communicate during matches.
- **Leaderboard:** Track and display player rankings, adding a competitive edge to the game.
- **Error Handling and Reconnection Strategy:** 
  - If both players unexpectedly disconnect, the match is canceled.
  - If one player voluntarily quits, the other is declared the winner.
  - In case of an accidental disconnection, the remaining player waits for 30 seconds. The match resumes if reconnection is successful; otherwise, it's considered a draw.

## How to Play

1. **Start the Game:** Launch the application.
2. **Wait for an Opponent:** The system will match you with another player.
3. **Gameplay:** Use the mouse to make your moves on the TicTacToe grid.
4. **Chat and Interact:** Communicate with your opponent using the chat feature.
5. **Track Your Progress:** Monitor your ranking on the leaderboard.

## Requirements

- Java Runtime Environment (JRE)
- Network connection for RMI functionality

## Installation

1. Clone the repository: `git clone git@github.com:codesssss/TicTacToe.git`
2. Navigate to the project directory: `cd TicTacToe`
3. Compile the Java files: `maven package`
4. Run the server: `java -jar server.jar <ip> <port>`
5. Launch the client application: `java -jar client.jar <username> <server_ip> <server_port>`

## Contributions

Contributions to the project are welcome. Please follow these steps:

1. Fork the repository.
2. Create a new branch: `git checkout -b new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin new-feature`
5. Submit a pull request.

## License

This project is licensed under the [MIT License](LICENSE.md).

## Acknowledgements

Thanks to all contributors who have helped in developing this fun and interactive TicTacToe game. Special thanks to the Java community for providing invaluable resources and support.

---

**Enjoy the game and may the best player win!**
