package ru.alex.professionalslanguageapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.entity.LeaderboardItem;
import ru.alex.professionalslanguageapi.database.repository.LeaderboardItemRepository;
import ru.alex.professionalslanguageapi.database.repository.WordRepository;
import ru.alex.professionalslanguageapi.dto.game.Game;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.dto.game.GamePlay;
import ru.alex.professionalslanguageapi.dto.game.GameStatus;
import ru.alex.professionalslanguageapi.dto.game.Player;
import ru.alex.professionalslanguageapi.dto.game.Question;
import ru.alex.professionalslanguageapi.dto.user.UserDetailsDto;
import ru.alex.professionalslanguageapi.exception.InvalidGameException;
import ru.alex.professionalslanguageapi.exception.InvalidParamException;
import ru.alex.professionalslanguageapi.mapper.game.GameDataMapper;
import ru.alex.professionalslanguageapi.storage.GameStorage;
import ru.alex.professionalslanguageapi.util.AuthUtils;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {
    private final GameStorage gameStorage;
    private final WordRepository wordRepository;
    private final LeaderboardItemRepository leaderboardItemRepository;
    private final GameDataMapper gameDataMapper;

    public GameData generateGameData() {
        return gameDataMapper.toDto(wordRepository.fetchGameDataRaw());
    }

    public List<String> getAllAvailableRooms() {
        return gameStorage.getAllAvailableRooms();
    }

    public Game createGame() {
        Game game = new Game();
        game.setGameData(generateGameData());
        game.setId(UUID.randomUUID().toString());
        game.setPlayer1(getNewPlayer());
        game.setStatus(GameStatus.NEW);
        game.setCurrentQuestion(0);
        gameStorage.setGame(game);

        return game;
    }

    public Game connectToGame(String gameId) {
        if(!gameStorage.games.containsKey(gameId)) {
            throw new InvalidParamException("Game with id " + gameId + " doesn't exists");
        }
        Game game = gameStorage.games.get(gameId);

        if(game.getPlayer2() != null) {
            throw new InvalidGameException("Game is already taken");
        }
        game.setPlayer2(getNewPlayer());
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setQuestionIsFinished(false);
        gameStorage.setGame(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) {
        if(!gameStorage.getGames().containsKey(gamePlay.gameId())) {
            throw new EntityNotFoundException("Game with id " + gamePlay.gameId() + " not found");
        }

        Game game = gameStorage.getGames().get(gamePlay.gameId());
        if(game.getStatus().equals(GameStatus.NEW)) {
            throw new InvalidGameException("The game hasn't started yet");
        }
        if(game.getStatus().equals(GameStatus.FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }
        checkCorrectAnswer(gamePlay, game);

        return game;
    }

    @Transactional
    public Game nextQuestion(String gameId) {
        if(!gameStorage.getGames().containsKey(gameId)) {
            throw new EntityNotFoundException("Game with id " + gameId + " not found");
        }
        Game game = gameStorage.getGames().get(gameId);
        if(!game.getQuestionIsFinished()) {
            throw new InvalidParamException("The question is not finished yet");
        }
        int currentQuestion = game.getCurrentQuestion();
        int numberOfQuestions = game.getGameData().questions().size();
        if(currentQuestion + 1 < numberOfQuestions) {
            game.setCurrentQuestion(currentQuestion + 1);
            game.getPlayer1().setSelectedAnswer(null);
            game.getPlayer1().setAnswerIsRight(null);
            game.getPlayer2().setSelectedAnswer(null);
            game.getPlayer2().setAnswerIsRight(null);
            game.setQuestionIsFinished(false);
        } else {
            finishGame(game);
        }
        return game;
    }

    private void checkCorrectAnswer(GamePlay gamePlay, Game game) {
        Player currentPlayer = getPlayer(game);
        if(currentPlayer.getSelectedAnswer() != null) {
            throw new InvalidGameException("You have already chosen an answer option");
        }

        Question currentQuestion = game.getGameData().questions().get(game.getCurrentQuestion());

        if(gamePlay.selectedAnswer().equals(currentQuestion.getCorrectAnswerNumber())) {
            currentPlayer.setAnswerIsRight(true);
            currentPlayer.setScore(currentPlayer.getScore() + 1);
            game.setQuestionIsFinished(true);
        } else {
            currentPlayer.setAnswerIsRight(false);
        }
        currentPlayer.setSelectedAnswer(gamePlay.selectedAnswer());
        if(game.getPlayer1().getSelectedAnswer() != null && game.getPlayer2().getSelectedAnswer() != null) {
            game.setQuestionIsFinished(true);
        }
    }

    private void finishGame(Game game) {
        game.setStatus(GameStatus.FINISHED);
        Integer player1Score = game.getPlayer1().getScore();
        Integer player2Score = game.getPlayer2().getScore();
        if(player1Score > player2Score) {
            game.setWinnerPlayer(game.getPlayer1().getId());
        } else if(player1Score < player2Score) {
            game.setWinnerPlayer(game.getPlayer2().getId());
        }
        if(game.getWinnerPlayer() != null) {
            LeaderboardItem leaderboardItem = leaderboardItemRepository.findByUser(game.getWinnerPlayer())
                    .orElseThrow(() -> new EntityNotFoundException("user with id " + game.getWinnerPlayer() + " not found"));
            leaderboardItem.setScore(leaderboardItem.getScore() + 2);
            leaderboardItemRepository.save(leaderboardItem);
        } else {
            LeaderboardItem player1LeaderboardItem = leaderboardItemRepository.findByUser(game.getPlayer1().getId())
                    .orElseThrow(() -> new EntityNotFoundException("user with id " + game.getPlayer1().getId() + " not found"));
            LeaderboardItem player2LeaderboardItem = leaderboardItemRepository.findByUser(game.getPlayer2().getId())
                    .orElseThrow(() -> new EntityNotFoundException("user with id " + game.getPlayer2().getId() + " not found"));
            player1LeaderboardItem.setScore(player1LeaderboardItem.getScore() + 1);
            player2LeaderboardItem.setScore(player2LeaderboardItem.getScore() + 1);
            leaderboardItemRepository.saveAll(List.of(player1LeaderboardItem, player2LeaderboardItem));
            gameStorage.games.remove(game.getId());
        }
    }

    private static Player getPlayer(Game game) {
        if(game.getQuestionIsFinished()) {
            throw new InvalidParamException("The current question is already finished. Go to the next question.");
        }
        Integer authorizedUserId = AuthUtils.getAuthorizedUserId();
        Player currentPlayer;
        if(authorizedUserId.equals(game.getPlayer1().getId())) {
            currentPlayer = game.getPlayer1();
        } else if(authorizedUserId.equals(game.getPlayer2().getId())) {
            currentPlayer = game.getPlayer2();
        } else {
            throw new InvalidParamException("Player with id " + authorizedUserId + " not found in this game");
        }
        return currentPlayer;
    }

    private Player getNewPlayer() {
        UserDetailsDto userDetails = AuthUtils.getUserDetails();
        return new Player(
                userDetails.id(),
                userDetails.email(),
                0,
                null,
                null
        );
    }
}
