# MedleySimulation Project

## Overview
The **MedleySimulation** project is a race simulation for a swimming medley relay, designed to demonstrate object-oriented programming and synchronization techniques in Java. It simulates a medley relay race with a graphical user interface (GUI), where swim teams compete in a controlled stadium grid. The simulation models various aspects of the race, including team and swimmer actions, stadium navigation, and race synchronization using multiple threads.

This README provides an overview of the project, the components involved, enhancements made, and instructions on how to run the simulation.

---

## Project Components

### 1. **MedleySimulation**
- The main class responsible for setting up the GUI and initializing simulation components. It controls the overall flow, including starting and quitting the simulation.

### 2. **CounterDisplay**
- A separate thread that updates race results in real-time, including displaying the winning team.

### 3. **FinishCounter**
- Tracks race status and identifies the winning team, providing methods to check the race's completion and retrieve the winner.

### 4. **GridBlock**
- Represents a block in the stadium grid and handles swimmer occupancy within the block.

### 5. **StadiumGrid**
- Represents the stadium grid, consisting of multiple `GridBlock` instances. It manages swimmer movements and interactions within the stadium.

### 6. **PeopleLocation**
- Tracks the location and state of each swimmer, including whether they are inside the stadium and their position.

### 7. **StadiumView**
- A JPanel that visualizes the simulation with a real-time rendering of the stadium, pool, starting blocks, and swimmer movements.

### 8. **SwimTeam**
- Represents a swim team with four swimmers. This class manages the order of swimmers and ensures proper race progression.

### 9. **Swimmer**
- Models an individual swimmer in the race, handling their entry, participation, and exit from the race.

---

## Key Enhancements

### 1. **User Interface (UI) Improvements**
- **Cover Page**: A welcome screen with essential information about the simulation.
- **Styled Buttons**: "Start" and "Quit" buttons added for user interaction to begin or end the race.
  
### 2. **Sound Features**
- **Background Music**: Plays a sound effect when the cover page is displayed for an immersive experience.
- **Start Button Sound**: A sound effect is triggered when the race begins, providing immediate feedback to the user.

### 3. **Synchronization Mechanisms**
- **CountDownLatch**: Ensures that no swimmer enters the race until the Start button is pressed.
- **CyclicBarrier**: Synchronizes the entry of all swimmers before the race begins, ensuring proper team order.
- **Mutex Locks**: Prevents data races when accessing shared resources like `GridBlock` and swimmer states.
- **Condition Variables**: Manage the synchronization between teammates, ensuring each swimmer waits for their predecessor to finish.

---

## Race Mechanics and Synchronization

### Array for Tracking Swimmer Presence
The `inside[]` array tracks whether each swimmer is inside the stadium, ensuring that no swimmer begins their leg of the race until they are physically present in the stadium.

### Ensuring Order of Swimmers
- **Race Order**: Swimmers must follow a specific stroke sequence (backstroke, breaststroke, butterfly, freestyle).
- **Synchronization**: The race sequence is enforced using `CyclicBarrier` and condition variables, ensuring that no swimmer can begin their leg until the previous swimmer finishes.

---

## Installation and Setup

### Requirements
- **Java Development Kit (JDK)** installed
- **Integrated Development Environment (IDE)** (e.g., IntelliJ IDEA, Eclipse)
- **Audio Files**: Ensure necessary audio files are available for the sound features.

### Steps to Run the Simulation

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd MedleySimulation
   ```

2. **Open in IDE**:
   - Import the project into your preferred IDE (IntelliJ, Eclipse, etc.).
   - Ensure that all dependencies and resources are correctly configured.

3. **Configure Audio Files**:
   - Place the audio files required for background music and start button sound in the appropriate resource directory (e.g., `/src/resources`).

4. **Build the Project**:
   - Compile all the Java classes using your IDE's build tool.

5. **Run the Simulation**:
   - Run the `MedleySimulation` class, which launches the simulation GUI.
   - Press the "Start" button to begin the race simulation, and "Quit" to exit.

### Notes on Running
- The **Start** button begins the simulation, triggering swimmer threads to begin the race in a synchronized manner.
- Real-time updates of race status and the winning team will be displayed on the GUI.
- Sound effects are enabled upon starting the simulation.

---

## Conclusion
This project showcases complex synchronization and multithreading techniques within a visual race simulation, ensuring accurate race progression and order adherence. The enhanced user interface and sound features improve user engagement, while the underlying synchronization mechanisms ensure a smooth and synchronized race experience.

For any inquiries or further improvements, feel free to contribute or reach out!

---

### License
This project is licensed under the MIT License.


