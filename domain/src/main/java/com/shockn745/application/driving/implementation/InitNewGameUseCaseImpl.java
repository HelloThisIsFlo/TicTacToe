package com.shockn745.application.driving.implementation;

import com.shockn745.application.driven.GameStatusRepository;
import com.shockn745.application.driving.dto.GameStatus;
import com.shockn745.application.driving.presentation.InitNewGameUseCase;
import com.shockn745.domain.Game;
import com.shockn745.domain.GameFactory;
import com.shockn745.domain.datamapper.GameMapper;

/**
 * @author Kempenich Florian
 */
public class InitNewGameUseCaseImpl implements InitNewGameUseCase {

    private final GameStatusRepository gameStatusRepository;
    private final GameFactory factory;
    private final GameMapper gameMapper;

    public InitNewGameUseCaseImpl(
            GameStatusRepository gameStatusRepository,
            GameFactory factory, GameMapper gameMapper) {
        this.gameStatusRepository = gameStatusRepository;
        this.factory = factory;
        this.gameMapper = gameMapper;
    }

    @Override
    public void execute(Callback callback) {
        Game game = factory.makeNewGame();
        GameStatus status = gameMapper.transform(game);

        int id = gameStatusRepository.saveGame(status);
        status = status.updateWithId(id);

        callback.newGameReady(status);
    }
}
