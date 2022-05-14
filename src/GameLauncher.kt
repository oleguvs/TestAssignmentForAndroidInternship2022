/**
 * An entry point of the GuessingGame application.
 * The form of main can accept a number of String arguments
 * as a target number that the user "thinks" of.
 */
fun main(args: Array<String>) {
    val game = GuessingGame()
    game.setUp(args)
    game.startPlaying()
}

/*
 * The GuessingGame class defines methods that simulate a game between two players where first
 * of them “thinks” of a number of the specified range and second tries to guess it,
  * and then they swap places and start game again.
 */
class GuessingGame {
    companion object {
        const val LOWER_BOUND = 0
        const val UPPER_BOUND = 100
    }

    /**
     * Creates two Player objects and assigns them to the two Player
     * instance variables setting [targetNum] variable that represents
     * the number these players guessed
     */
    fun setUp(args: Array<String>) {
        val num = if (args.isNotEmpty() && args[0].toIntOrNull() != null) {//checks command line args for validity
            args[0].toInt()
        } else {
            GameHelper.readNotNegativeInteger("Enter a number you think of:", LOWER_BOUND, UPPER_BOUND)
        }

        p1 = Computer(GameHelper.chooseNumber(LOWER_BOUND, UPPER_BOUND))
        p2 = Human(num)
    }

    /**
     * Simulates the entire game which consists of two separate simple games
     */
    fun startPlaying() {
        playGame(p1, p2)
        playGame(p2, p1)
    }

    /**
     * Simulates a simple game where one player thinks of a number and the other one tries to guess it
     */
    private fun playGame(p1: Player, p2: Player) {
        var from = LOWER_BOUND
        var to = UPPER_BOUND
        println((if (p1 is Human) "You are " else "I'm ") + "thinking of a number between $from and $to inclusively.")
        println("Number to guess is ${p1.targetNum}")
        while (from <= to) {
            //get a guess of the player2
            val proposedNum = p2.makeGuess(
                (if (p2 is Human) "You're " else "I'm ") + "guessing: ",
                from,
                to
            )
            //verify the guess of the player2
            when (p1.checkIsGuessRight(proposedNum)) {
                1 -> to = proposedNum - 1    //set the upper bound to one less than proposed number
                -1 -> from = proposedNum + 1 //set the lower bound to one more than proposed number
                else -> return               //the player guessed the number and the game is over
            }
        }
        println(
            "You are thinking of a number that is out of the range from ${LOWER_BOUND} to ${UPPER_BOUND} " +
                    "\nPlease start a new game."
        )
    }

    // instance variables for the two players
    private lateinit var p1: Player
    private lateinit var p2: Player
}

/**
 * The Player class is used to represent a player who will play the guessing game.
 */
abstract class Player(val targetNum: Int) {

    /**
     * Tries to guess the number that the another player thought of
     */
    abstract fun makeGuess(prompt: String, from: Int, to: Int): Int

    /**
     * Checks whether the [proposedNum] is equal, smaller or larger than the [targetNum]
     * and prints out the result of the comparison and returns int value 0,1 or -1 respectively.
     */
    fun checkIsGuessRight(proposedNum: Int): Int {
        return when {
            proposedNum == targetNum -> {
                println("Your guess is correct. Congratulations!")
                0
            }
            proposedNum > targetNum -> {
                println("$proposedNum is greater than the actual number")
                1
            }
            else -> {
                println("$proposedNum is less than the actual number")
                -1
            }
        }
    }
}

/*
 * The Human class extends the Player class and represents a user who plays the guessing game.
 * This class implements the makeGuess method so that the user can input his guess from the console.
 */
class Human(num: Int) : Player(num) {
    override fun makeGuess(prompt: String, from: Int, to: Int): Int {
        return GameHelper.readNotNegativeInteger(prompt, from, to)
    }
}

/*
 * The Computer class extends the Player class and represents a machine who plays the guessing game.
 * This class implements the makeGuess method so that it generates a random number withing the specified
 * range.
 */
class Computer(num: Int) : Player(num) {
    override fun makeGuess(prompt: String, from: Int, to: Int): Int {
        val guess = GameHelper.chooseNumber(from, to)
        println(prompt + guess)
        Thread.sleep(1500)
        return guess
    }
}

/*Constant representing a regular expression for a not negative value. */
const val REGULAR_EXPRESSION_FOR_INTEGER = "\\s*[+]?\\d+"

/*
 * The GameHelper class defines the methods that help for the game.
 */
class GameHelper {
    companion object {
        /**
         * Generates a random number between [from] and [to] values inclusively.
         */
        fun chooseNumber(from: Int, to: Int): Int = (from..to).random()

        /**
         * This method requests the user to enter a not negative integer,
         * checks it for validity, and if true, parses it to integer value,
         * otherwise - request the user to repeat entering until the input
         * data is valid.
         */
        fun readNotNegativeInteger(prompt: String, from: Int, to: Int): Int {
            print(prompt)
            //If readLine() is null assign a value that is generated by the chooseNumber method
            var stringNumber: String = readLine() ?: return GameHelper.chooseNumber(from, to)
            while (!(stringNumber.matches(REGULAR_EXPRESSION_FOR_INTEGER.toRegex())) //check if a not negative integer
            ) {
                //report of the invalid input
                println(
                    "The input data is incorrect.\n" + "\""
                            + stringNumber + "\"" + " is not a positive integer. Please try again."
                )
                print(prompt)
                stringNumber = readLine() ?: "0"
            }
            return Integer.parseInt(stringNumber)
        }
    }
}