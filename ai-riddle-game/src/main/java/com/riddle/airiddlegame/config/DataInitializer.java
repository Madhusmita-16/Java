package com.riddle.airiddlegame.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riddle.airiddlegame.entity.*;
import com.riddle.airiddlegame.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final RiddleRepository riddleRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataInitializer(CategoryRepository categoryRepository,
                           RiddleRepository riddleRepository,
                           UserRepository userRepository,
                           ScoreRepository scoreRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.riddleRepository = riddleRepository;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            seedCategoriesAndRiddles();
        }
        if (userRepository.count() == 0) {
            seedUsers();
        }
    }

    private void seedUsers() {
        // Admin Account
        User admin = new User("admin", "admin@riddle.com", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
        User savedAdmin = userRepository.save(admin);
        scoreRepository.save(new Score(savedAdmin));

        // Player Account 1
        User player1 = new User("player1", "player1@riddle.com", passwordEncoder.encode("password123"), Role.ROLE_USER);
        User savedPlayer1 = userRepository.save(player1);
        Score score1 = new Score(savedPlayer1);
        score1.setHighestScore(450);
        score1.setTotalGamesPlayed(3);
        score1.setAverageAccuracy(80.0);
        scoreRepository.save(score1);

        // Player Account 2
        User player2 = new User("riddlemaster", "master@riddle.com", passwordEncoder.encode("riddle123"), Role.ROLE_USER);
        User savedPlayer2 = userRepository.save(player2);
        Score score2 = new Score(savedPlayer2);
        score2.setHighestScore(720);
        score2.setTotalGamesPlayed(5);
        score2.setAverageAccuracy(92.0);
        scoreRepository.save(score2);
    }

    private void seedCategoriesAndRiddles() throws Exception {
        Category logic = categoryRepository.save(new Category("Logic", "Puzzles testing deductive reasoning and problem-solving skills"));
        Category math = categoryRepository.save(new Category("Mathematics", "Riddles involving numbers, patterns, and mathematical concepts"));
        Category wordplay = categoryRepository.save(new Category("Wordplay", "Clever word games, anagrams, double meanings, and puns"));
        Category science = categoryRepository.save(new Category("Science", "Enigmas based on physics, chemistry, biology, and the natural world"));
        Category tech = categoryRepository.save(new Category("Technology", "Riddles about computing, algorithms, hardware, and digital concepts"));
        Category gk = categoryRepository.save(new Category("General Knowledge", "Tricky trivia questions and broad knowledge riddles"));

        // --- Logic Riddles ---
        createRiddle("I speak without a mouth and hear without ears. I have no body, but I come alive with wind. What am I?",
                Arrays.asList("An Echo", "A Shadow", "A Whisper", "A Kite"),
                "An Echo",
                Arrays.asList("echo", "an echo"),
                "It reverberates back to you in mountains and empty halls.",
                "Easy", logic, 100);

        createRiddle("A man looks at a painting in a museum and says: 'Brothers and sisters I have none, but that man's father is my father's son.' Who is in the painting?",
                Arrays.asList("His Son", "His Father", "Himself", "His Nephew"),
                "His Son",
                Arrays.asList("his son", "son", "my son"),
                "Break down 'my father's son' first.",
                "Medium", logic, 150);

        createRiddle("You are in a dark room with a single match. There is an oil lamp, a candle, and a wood fireplace. What do you light first?",
                Arrays.asList("The Match", "The Lamp", "The Candle", "The Fireplace"),
                "The Match",
                Arrays.asList("match", "the match"),
                "Without this, none of the others can catch fire.",
                "Easy", logic, 100);

        // --- Mathematics Riddles ---
        createRiddle("If 5 cats can catch 5 mice in 5 minutes, how many cats are needed to catch 100 mice in 100 minutes?",
                Arrays.asList("5 Cats", "100 Cats", "20 Cats", "50 Cats"),
                "5 Cats",
                Arrays.asList("5", "5 cats", "five"),
                "Think about the rate of catching mice per cat.",
                "Medium", math, 150);

        createRiddle("A hat and a coat cost $110 in total. The coat costs $100 more than the hat. How much does the hat cost?",
                Arrays.asList("$5", "$10", "$15", "$20"),
                "$5",
                Arrays.asList("5", "$5", "5 dollars", "five dollars"),
                "Let Hat = x, Coat = x + 100. Solve x + (x + 100) = 110.",
                "Hard", math, 200);

        createRiddle("I am an odd number. Take away a letter and I become even. What number am I?",
                Arrays.asList("Seven", "Eleven", "Nine", "Five"),
                "Seven",
                Arrays.asList("seven", "7"),
                "Remove the letter 'S'.",
                "Easy", math, 100);

        // --- Wordplay Riddles ---
        createRiddle("What gets wetter and wetter the more it dries?",
                Arrays.asList("A Towel", "A Sponge", "Rain", "A Cloud"),
                "A Towel",
                Arrays.asList("towel", "a towel"),
                "You use it every day after taking a shower.",
                "Easy", wordplay, 100);

        createRiddle("What word in the English language is always spelled incorrectly?",
                Arrays.asList("Incorrectly", "Wrong", "Misspelled", "Error"),
                "Incorrectly",
                Arrays.asList("incorrectly", "the word incorrectly"),
                "Look closely at the literal spelling of the word itself.",
                "Easy", wordplay, 100);

        createRiddle("Forward I am heavy, but backward I am not. What am I?",
                Arrays.asList("Ton", "Weight", "Stone", "Anchor"),
                "Ton",
                Arrays.asList("ton", "a ton"),
                "Spell 'ton' in reverse.",
                "Medium", wordplay, 150);

        // --- Science Riddles ---
        createRiddle("What can travel around the world while remaining in a corner?",
                Arrays.asList("A Stamp", "A Compass", "The Moon", "A Map"),
                "A Stamp",
                Arrays.asList("stamp", "a stamp", "postage stamp"),
                "It sticks to an envelope.",
                "Easy", science, 100);

        createRiddle("I am invisible, but I can make things float. I can be harnessed to power cities, but too much of me causes destruction. What am I?",
                Arrays.asList("Wind", "Gravity", "Magnetism", "Electricity"),
                "Wind",
                Arrays.asList("wind", "air current"),
                "It moves wind turbines and sails ships.",
                "Medium", science, 150);

        createRiddle("I have no skeleton, no heart, and no brain, yet I am 95% water and can sting. What am I?",
                Arrays.asList("Jellyfish", "Octopus", "Sea Anemone", "Coral"),
                "Jellyfish",
                Arrays.asList("jellyfish", "a jellyfish"),
                "A floating ocean creature translucent in water.",
                "Hard", science, 200);

        // --- Technology Riddles ---
        createRiddle("I have a spine, but no bones. I store memories, but have no brain. I can execute code without hands. What am I?",
                Arrays.asList("A Server", "A Computer", "A Hard Drive", "A Book"),
                "A Computer",
                Arrays.asList("computer", "a computer", "server", "pc"),
                "It runs operating systems and applications.",
                "Easy", tech, 100);

        createRiddle("I run continuously without moving a single muscle. I have recursion, loops, and conditions. What am I?",
                Arrays.asList("A Program", "A Processor", "A Battery", "A Router"),
                "A Program",
                Arrays.asList("program", "software", "algorithm", "code"),
                "Written by developers in languages like Java.",
                "Medium", tech, 150);

        createRiddle("I am a chain that binds blocks together without metal. I ensure trust without central authority. What am I?",
                Arrays.asList("Blockchain", "Internet", "Encryption", "Database"),
                "Blockchain",
                Arrays.asList("blockchain", "block chain"),
                "Used in decentralized ledger systems.",
                "Hard", tech, 200);

        // --- General Knowledge Riddles ---
        createRiddle("The more of this you take, the more you leave behind. What are they?",
                Arrays.asList("Footsteps", "Memories", "Fingerprints", "Time"),
                "Footsteps",
                Arrays.asList("footsteps", "foot steps", "steps"),
                "Left in sand or snow when walking.",
                "Easy", gk, 100);

        createRiddle("What belongs to you, but other people use it far more than you do?",
                Arrays.asList("Your Name", "Your Phone Number", "Your Car", "Your Advice"),
                "Your Name",
                Arrays.asList("your name", "name", "my name"),
                "People call you by this.",
                "Easy", gk, 100);

        createRiddle("I have cities, but no houses. I have mountains, but no trees. I have water, but no fish. What am I?",
                Arrays.asList("A Map", "A Globe", "A Painting", "A Mirror"),
                "A Map",
                Arrays.asList("map", "a map", "globe"),
                "Cartographers draw me.",
                "Medium", gk, 150);
    }

    private void createRiddle(String question, List<String> options, String correctAnswer, List<String> altAnswers,
                              String hint, String difficulty, Category category, int basePoints) throws Exception {
        Riddle riddle = new Riddle();
        riddle.setQuestion(question);
        riddle.setOptionsJson(objectMapper.writeValueAsString(options));
        riddle.setCorrectAnswer(correctAnswer);
        riddle.setAltAnswersJson(objectMapper.writeValueAsString(altAnswers));
        riddle.setHint(hint);
        riddle.setDifficulty(difficulty);
        riddle.setCategory(category);
        riddle.setBasePoints(basePoints);
        riddleRepository.save(riddle);
    }
}
