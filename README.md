# Wordie

A Wordle clone built with Java Swing, organized using MVC and common design patterns.

<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/7a7f4fb5-ceae-4e6a-9d5e-2b5eb43059c9" />


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
│   ├── WordDictionary.java  # word list with O(1) validation
│   ├── WordPicker.java      # strategy interface for picking targets
│   └── RandomWordPicker.java # picks a random word from the dictionary
└── view/
    ├── Colors.java           # shared color constants
    ├── GameFrame.java        # top-level JFrame
    ├── KeyboardPanel.java    # on-screen QWERTY keyboard
    └── TilePanel.java        # 6×5 guess grid
```

## Design patterns

| Pattern | Used in |
|---|---|
| **MVC** | Model (`GameModel`), View (panel classes), Controller (`GameController`) — clean separation of state, rendering, and input. |
| **Observer** | `GameListener` interface. Model fires events; the controller implements them and updates the view. Keeps model completely Swing-free. |
| **Strategy** | `WordPicker` interface with `RandomWordPicker` — easy to swap for daily-word, themed, or API-based selection without changing model code. |
| **Strategy** | `GuessEvaluator` is an isolated pure function — trivial to unit test or replace with a different scoring algorithm. |
| **Single Responsibility** | Every class has one concern: `GuessEvaluator` scores, `WordDictionary` validates, `TilePanel` renders tiles, `KeyboardPanel` renders keys, etc. |

## Tech

- Java 17+
- Maven
- Swing (no external dependencies)
