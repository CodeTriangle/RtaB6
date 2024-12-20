package tel.discord.rtab.games;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class DominoTrain extends MiniGameWrapper {
	static class Domino {
		public int left;
		public int right;

		public Domino(int left, int right) {
			this.left = left;
			this.right = right;
		}

		/**
		 * Flip the domino 180 degrees.
		 */
		public void flip() {
			int tmp = this.left;
			this.left = this.right;
			this.right = tmp;
		}

		/**
		 * Test whether the left side of the domino can be played next to a number, including using wilds.
		 *
		 * @param value The value to compare against
		 *
		 * @return true if the domino matches, false otherwise.
		 */
		public boolean matchesLeft(int value) {
			return this.left == value || this.left == 0 || value == 0;
		}

		/**
		 * Test whether the right side of the domino can be played next to a number, including using wilds.
		 *
		 * @param value The value to compare against
		 *
		 * @return true if the domino matches, false otherwise.
		 */
		public boolean matchesRight(int value) {
			return this.right == value || this.right == 0 || value == 0;
		}

		/**
		 * Test whether either side of the domino can be played next to a number, including using wilds.
		 *
		 * @param value The value to compare against
		 *
		 * @return true if the domino matches, false otherwise.
		 */
		public boolean matches(int value) {
			return this.left == value || this.right == value || this.left == 0 || this.right == 0 || value == 0;
		}

		public String toStringLarge() {
			return String.format("""
				.-------.
				| %d | %d |
				`-------´""",
				left,
				right
			);
		}

		public String toString() {
			return String.format("[%d|%d]", left, right);
		}
	}

	static List<Domino> createSet(int maxNumber) {
		int sizeOfSet = 0;
		for (int i = 1; i <= maxNumber + 1; i++) {
			sizeOfSet += i;
		}

		List<Domino> result = new ArrayList<>(sizeOfSet);
		for (int l = 0; l <= maxNumber; l++) {
			for (int r = l; r <= maxNumber; r++) {
				result.add(new Domino(l, r));
			}
		}

		return result;
	}

	List<Domino> pool;
	Deque<Domino> train;

	List<Domino> hand;

	boolean canPlayOne;

	int maxHandSize = 4;
	int roundNum = 0;
	int score;

	@Override
	void startGame() {
		this.pool = createSet(6); // create a set of double-6 dominoes

		this.hand = new ArrayList<>(4); // initial hand size is 4

		Domino current = pool.removeFirst(); // get the 0-0 tile

		train = new ArrayDeque<>();

		train.add(current);

		Collections.shuffle(pool);

		LinkedList<String> output = new LinkedList<>();

		Collections.addAll(
			output,
			new String[] {
				"In this game, you must create the longest train you can using dominoes.",
				"There are 28 dominoes in total, one for each pair of numbers between 0 and 6.",
				"A domino can be placed next to another domino if they share a number.",
				"Zeroes are wild and can be placed next to any other number.",
				"You can play on either end of the train, so long as the domino matches.",
				"Dominoes can be flipped before being placed.",
				"Every time you make a match, you score points equal to the number on the dominoes.",
				"However, a number matched with Zero scores no points.",
				"You'll start with a hand of four dominoes, and this number decreases as you proceed.",
			}
		);

		sendSkippableMessages(output);

		updateGameState(null, 0);

		getInput();
	}

	@Override
	void playNextTurn(String input) {
		input = input.toLowerCase();

		Domino next = null;
		boolean placeRight = false;
		boolean placeFlipped = false;

		String[] words = input.split("\\s+");

		int roundScore = 0;

		if (words.length == 3) {
			// Handle the case for if there are three separate words.

			// PART ONE: HANDLE THE DOMINO

			// if it is not a single character, complain
			if (words[0].length() != 1) {
				sendMessage("Please designate a domino by letter.");
				getInput();
				return;
			}

			// if it is not a letter that appeared in the hand summary, complain
			int idx = words[0].charAt(0) - 'a';
			if (idx < 0 || idx >= hand.size()) {
				sendMessage("Please type a valid character.");
				getInput();
				return;
			}

			Domino toPlay = hand.get(idx);
			if (toPlay.matches(train.getLast().right) || toPlay.matches(train.getFirst().left)) {
				next = toPlay;
			} else {
				sendMessage("That domino cannot be played.");
				getInput();
				return;
			}

			// PART TWO: HANDLE THE SIDE
			if (words[1].equals("left") || words[1].equals("l")) {
				placeRight = false;
			} else if (words[1].equals("right") || words[1].equals("r")) {
				placeRight = true;
			} else {
				sendMessage("Please tell me whether to place the domino LEFT or RIGHT.");
				getInput();
				return;
			}

			// PART THREE: HANDLE THE ORIENTATION
			if (words[2].equals("normal") || words[2].equals("n")) {
				placeFlipped = false;
			} else if (words[2].equals("flipped") || words[2].equals("f")) {
				placeFlipped = true;
			} else {
				sendMessage("Please tell me whether the domino is NORMAL or FLIPPED.");
				getInput();
				return;
			}
		} else if (words.length == 1 || words[0].length() == 3) {
			// Handle the case for if it is three letters in one word.

			// PART ONE: HANDLE THE DOMINO

			// if it is not a letter that appeared in the hand summary, complain
			int idx = words[0].charAt(0) - 'a';
			if (idx < 0 || idx >= hand.size()) {
				sendMessage("First character is not a valid domino.");
				getInput();
				return;
			}

			Domino toPlay = hand.get(idx);
			if (toPlay.matches(train.getLast().right) || toPlay.matches(train.getFirst().left)) {
				next = toPlay;
			} else {
				sendMessage("That domino cannot be played.");
				getInput();
				return;
			}

			// PART TWO: HANDLE THE SIDE
			if (words[0].charAt(1) == 'l') {
				placeRight = false;
			} else if (words[0].charAt(1) == 'r') {
				placeRight = true;
			} else {
				sendMessage("Second character must be L (for left) or R (for right).");
				getInput();
				return;
			}

			// PART THREE: HANDLE THE ORIENTATION
			if (words[0].charAt(2) == 'n') {
				placeFlipped = false;
			} else if (words[0].charAt(2) == 'f') {
				placeFlipped = true;
			} else {
				sendMessage("Third character must be N (for normal) or F (for flipped).");
				getInput();
				return;
			}
		} else {
			sendMessage("I need three words on one line, or three letters.");
			getInput();
			return;
		}

		int leftValue = train.getFirst().left;
		int rightValue = train.getLast().right;

		if (placeRight) {
			if (placeFlipped) {
				if (!next.matchesRight(rightValue)) {
					sendMessage("Cannot place that domino RIGHT FLIPPED. Try again.");
					getInput();
					return;
				}
				next.flip();
			} else {
				if (!next.matchesLeft(rightValue)) {
					sendMessage("Cannot place that domino RIGHT NORMAL. Try again.");
					getInput();
					return;
				}
			}

			if (rightValue != 0 && next.left != 0) {
				roundScore = rightValue;
				score += rightValue;
			}

			train.add(next);
		} else {
			if (placeFlipped) {
				if (!next.matchesLeft(leftValue)) {
					sendMessage("Cannot place that domino LEFT FLIPPED. Try again.");
					getInput();
					return;
				}
				next.flip();
			} else {
				if (!next.matchesRight(leftValue)) {
					sendMessage("Cannot place that domino LEFT NORMAL. Try again.");
					getInput();
					return;
				}
			}

			if (leftValue != 0 && next.right != 0) {
				roundScore = leftValue;
				score += leftValue;
			}

			train.addFirst(next);
		}

		hand.remove(next);

		updateGameState(next, roundScore);

		if (!canPlayOne) {
			awardMoneyWon(score * 20000); 
		}

		getInput();
	}

	/**
	 * Create a representation of the hand.
	 *
	 * Also, for efficiency's sake, this checks if the hand is playable.
	 * 
	 * @return A string with details of the hand.
	 */
	String getHandString() {
		canPlayOne = false;
		char counter = 'A';

		StringBuilder outputBuilder = new StringBuilder();

		for (Domino next : hand) {
			boolean canPlay = next.matches(train.getLast().right) || next.matches(train.getFirst().left);
			if (canPlay) {
				canPlayOne = true;
			}

			boolean canGoRightUnflipped = next.matchesLeft(train.getLast().right);
			boolean canGoRightFlipped = next.matchesRight(train.getLast().right);

			boolean canGoLeftUnflipped = next.matchesRight(train.getFirst().left);
			boolean canGoLeftFlipped = next.matchesLeft(train.getFirst().left);

			ArrayList<String> orientations = new ArrayList<>();

			if (canGoRightUnflipped) {
				orientations.add("RIGHT NORMAL");
			}
			if (canGoRightFlipped) {
				orientations.add("RIGHT FLIPPED");
			}
			if (canGoLeftUnflipped) {
				orientations.add("LEFT NORMAL");
			}
			if (canGoLeftFlipped) {
				orientations.add("LEFT FLIPPED");
			}

			outputBuilder.append(String.format(
				"%c - %s (%s)\n",
				counter,
				next,
				canPlay ? String.join(", ", orientations) : "UNPLAYABLE"
			));

			counter++;
		}

		return outputBuilder.toString();
	}

	List<String> drawHand() {
		LinkedList<String> output = new LinkedList<>();

		if (hand.size() < maxHandSize) {
			output.add("Drawing up to " + maxHandSize + " dominoes...");
		}

		while (hand.size() < maxHandSize) {
			Domino drawn = pool.removeLast();
			hand.add(drawn);
			output.add(String.format("Drew this domino: `%s`", drawn));
		}

		return output;
	}

	String getTrainString() {
		StringBuilder trainBuilder = new StringBuilder();

		int i = 0;
		for (Domino d : train) {
			if (i != 0 && i % 7 == 0) {
				trainBuilder.append("\n");
			}
			trainBuilder.append(d);
			i++;
		}

		return trainBuilder.toString();
	}

	List<String> getPrompt() {
		LinkedList<String> messages = new LinkedList<>();
		messages.add(String.format("""
			```
			SCORE: %d

			TRAIN:

			%s

			HAND:

			%s
			```
			""",
			this.score,
			this.getTrainString(),
			this.getHandString()
		));

		if (canPlayOne) {
			messages.add(
				"Choose a domino to play, which side (LEFT or RIGHT), and whether it is FLIPPED or NORMAL, in that order. " +
				"For instance, type 'D RIGHT FLIPPED' or 'DRF' to place domino D on the right of the train, flipped."
			);
		}

		return messages;
	}

	void updateGameState(Domino placed, int scoreAdded) {
		List<String> messages = new LinkedList<>();

		if (placed != null) {
			String placedMsg = String.format("Placed domino `%s`", placed);

			if (scoreAdded > 0) {
				placedMsg += ", scoring " + scoreAdded + " points.";
			} else {
				placedMsg += ", but you scored nothing for it, because you matched with a wild.";
			}

			messages.add(placedMsg);
		}

		if (roundNum % 7 == 6) {
			messages.add("Good job getting this far! Your hand size has decreased by one.");
			maxHandSize--;
		}
		roundNum++;

		messages.addAll(drawHand());

		messages.addAll(getPrompt());

		sendMessages(messages);
	}

	@Override
	String getBotPick() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getBotPick'");
	}

	@Override
	void abortGame() {
	}

	@Override
	public String getName() {
		return "Domino Train";
	}

	@Override
	public String getShortName() {
		return "Train";
	}

	@Override
	public boolean isBonus() {
		return false;
	}
}
