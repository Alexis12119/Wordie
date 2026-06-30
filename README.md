# Wordie

A Wordle clone built with Java Swing, organized using MVC and common design patterns.

<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/8e7d24ef-dbb1-4ebc-8925-88cb00d1e330" />

## How to run

```bash
mvn compile
java -cp target/classes com.Wordie.App
```

## How to play

- Guess the 5-letter word in 6 tries.
- Each guess must be a valid word from the dictionary.
- After each guess, tile colors show how close you are:
  - **Green** — correct letter in the correct position.
  - **Yellow** — correct letter in the wrong position.
  - **Gray** — letter is not in the word.
- Type with your physical keyboard or click the on-screen keys.
- Press ENTER to submit a guess, DEL/BACKSPACE to delete.
- Close the window triggers an exit confirmation dialog.

## Architecture

```
com.Wordie
├── App.java                 # entry point, wires dependencies together
├── controller/
│   └── GameController.java  # handles input, coordinates model ↔ view
├── model/
│   ├── GameModel.java       # core game state, notifies listeners on changes
│   ├── GameListener.java    # observer interface for view updates
│   ├── GuessEvaluator.java  # scores a guess against the target word
│   ├── TileState.java       # enum: EMPTY / CORRECT / PRESENT / ABSENT
│   ├── WordBank.java        # word list (550+ five-letter words)
│   ├── WordDictionary.java  # validates guesses against word bank (HashSet)
│   ├── WordPicker.java      # strategy interface for picking targets
│   └── RandomWordPicker.java # picks a random word from the dictionary
└── view/
    ├── Colors.java           # shared color constants
    ├── GameFrame.java        # top-level JFrame, fixed 400×500
    ├── KeyboardPanel.java    # on-screen QWERTY keyboard (custom-painted)
    └── TilePanel.java        # 6×5 guess grid (custom-painted)
```

## Design patterns

| Pattern | Used in |
|---|---|
| **MVC** | Model (`GameModel`), View (custom-painted panels), Controller (`GameController`) — clean separation of state, rendering, and input. |
| **Observer** | `GameListener` interface. Model fires events; the controller implements them and updates the view. Keeps model completely Swing-free. |
| **Strategy** | `WordPicker` interface with `RandomWordPicker` — easy to swap for daily-word, themed, or API-based selection without changing model code. |
| **Strategy** | `GuessEvaluator` is an isolated pure function — trivial to unit test or replace with a different scoring algorithm. |
| **Single Responsibility** | Every class has one concern: `GuessEvaluator` scores, `WordDictionary`/`WordBank` manage words, `TilePanel` draws the grid, `KeyboardPanel` draws keys, etc. |

## Rendering

Both `TilePanel` and `KeyboardPanel` use **custom `paintComponent()`** instead of Swing component trees (no `JLabel`/`JButton` children). All tile and key dimensions are computed dynamically from the panel's current size at paint time, making the layout naturally proportional to the window. This approach eliminates layout manager complexity and keeps the visual proportions consistent regardless of screen size.

## Tech

- Java 17+
- Maven
- Swing (custom painting, no external dependencies)
