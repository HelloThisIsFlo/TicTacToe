package com.shockn745.application.driving.implementation;

import com.shockn745.application.driven.GameRepository;
import com.shockn745.application.driven.NetworkListenerRepository;
import com.shockn745.application.driving.dto.GameError;
import com.shockn745.application.driving.dto.GameStatus;
import com.shockn745.application.driving.dto.Move;
import com.shockn745.application.driving.network.AddMoveFromNetworkUseCase;
import com.shockn745.domain.Game;
import com.shockn745.domain.MoveModel;
import com.shockn745.domain.exceptions.IllegalMoveException;

import java.util.Set;

/**
 * @author Kempenich Florian
 */
public class AddMoveFromNetworkUseCaseImpl implements AddMoveFromNetworkUseCase {

    private final GameRepository gameRepository;
    private final NetworkListenerRepository networkListenerRepository;

    public AddMoveFromNetworkUseCaseImpl(
            GameRepository gameRepository,
            NetworkListenerRepository networkListenerRepository) {
        this.gameRepository = gameRepository;
        this.networkListenerRepository = networkListenerRepository;
    }

    @Override
    public void execute(Move move, int gameId, Callback errorCallback) {
        if (shouldExecute(gameId)) {
            playMoveAndNotifyListeners(move, gameId, errorCallback);
        }
    }

    private boolean shouldExecute(int gameId) {
        return getListeners(gameId) != null && gameRepository.contains(gameId);
    }

    private Set<NetworkListenerRepository.GameNetworkListener> getListeners(int gameId) {
        return networkListenerRepository.getListeners(gameId);
    }

    private void playMoveAndNotifyListeners(Move move, int gameId, Callback errorCallback) {
        Game game = gameRepository.getGame(gameId);

        try {
            game.play(new MoveModel(move));
            game.checkIfFinishedAndUpdateWinner();
            GameStatus status = game.makeStatus(gameId);

            // notify listeners
            for (NetworkListenerRepository.GameNetworkListener listener : getListeners(gameId)) {
                listener.onNewMoveFromNetwork(status);
            }
        } catch (IllegalMoveException e) {
            GameError error = new GameError(e.getMessage());
            errorCallback.onError(error);
        }

    }
}