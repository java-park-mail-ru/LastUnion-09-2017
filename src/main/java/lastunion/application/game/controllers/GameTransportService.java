package lastunion.application.game.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.messages.*;
import lastunion.application.game.views.GameView;
import lastunion.application.game.views.UserGameView;
import lastunion.application.models.UserModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameTransportService implements Runnable {
    private final Integer WAIT = 1000;
    public enum ErrorCodes {
        ERROR,
        READY_START,
        OK,
    }

    //-----------------------------------------------------------------
    private final Random random = new Random();
    static final int LENGTH = 10;
    static final int TYPES = 5;
    static final int DELTA = 256;
    //----------------------------------------------------------------

    //-------------------------------------------------------------------
    final Integer WIDTH = 100;
    final Integer HEIGHT = 100;
    final Integer JUMPPOWER = 33;
    //---------------------------------------------------------------------


    private class UserCommand {
        CommandMessage msg;
        String userId;
        public UserCommand (CommandMessage msg, String userId) {
            this.msg = msg;
            this.userId = userId;
        }
    }

    volatile boolean stopGame;
    private final Map<String, GameUserController> users;
    private final Map<String, Boolean> usersReady;
    private final ObjectMapper mapper;
    private final ConcurrentLinkedQueue<UserCommand> msgQueue;

    private String genWorldSeq() {
        int len = random.nextInt(LENGTH) + LENGTH; // length of sequence from 9 to 18
        String res = "";
        byte nextType = 0;
        byte delta = 0;
        for (int i = len; i >= 0; i--) {
            nextType = (byte) random.nextInt(TYPES); // types 0 - 4
            res = res + nextType;
            delta = (byte) random.nextInt(DELTA); // delta 0-255
            res = res + (char) delta;
        }
        return res;
    }

    public void gameProcess() {
        UserCommand userCommand = msgQueue.poll();
        if (userCommand != null) {
            String cmd = userCommand.msg.getArguments();
            if (cmd.equals("Up")) {
//                this.wait(1000);

            }
            else {

            }
        }
    }

    public void run() {
        while (!stopGame) {
            gameProcess();
        }
    }

    public void stop() {
        this.stopGame = true;
    }


    public GameTransportService(ObjectMapper mapper) {
        this.mapper = mapper;
        this.msgQueue = new ConcurrentLinkedQueue<>();
        this.users = new ConcurrentHashMap<>();
        this.usersReady = new ConcurrentHashMap<>();
    }

    public void addMessage(CommandMessage cMsg, String userId) {
        msgQueue.add(new UserCommand(cMsg, userId));
    }

    public synchronized ErrorCodes addUser(@NotNull GameUserController user) {
        final String userId = user.getUserId();
        if (userId == null) {
            return ErrorCodes.ERROR;
        }
        if (users.containsKey(userId)) {
            return ErrorCodes.ERROR;
        }
        users.put(userId, user);
        if (users.size() == 2) {
            return ErrorCodes.READY_START;
        }
        sendMessageAll(new UserAddedMessage(user.getUserDataView().getUserName()));
        return ErrorCodes.OK;
    }

    public void setStatus(@NotNull String userId, Boolean status) {
        usersReady.put(userId, status);
    }

    public boolean checkStatus() {
        for (String userId : users.keySet()) {
            try {
                if (!usersReady.get(userId)) {
                    return false;
                }
            } catch(NullPointerException ex) {
                return false;
            }
        }
        return true;
    }

    public ErrorCodes removeUser(@NotNull String userId) {
        if (!users.containsKey(userId)) {
            return ErrorCodes.ERROR;
        }
        final GameUserController user = users.get(userId);
        users.remove(userId);
        final UserModel userModel = user.getUserDataView();
        user.close();
        return sendMessageAll(new UserExitedMessage(userModel.getUserName()));
    }

    @SuppressWarnings({"UnusedReturnValue", "SameReturnValue"})
    public ErrorCodes closeConnectionsWithOut(String userId) {
        for (Map.Entry<String, GameUserController> entry : users.entrySet()) {
            final GameUserController user = entry.getValue();
            if (!user.getUserId().equals(userId)) {
                user.close();
            }
        }

        return ErrorCodes.OK;
    }

    private ErrorCodes sendMessageAll(String msg) {
        for (Map.Entry<String, GameUserController> entry : users.entrySet()) {
            final GameUserController user = entry.getValue();
            final GameUserController.ErrorCodes err = user.sendMessageToUser(msg);
            switch (err) {
                case OK:
                    break;

                default:
                    return ErrorCodes.ERROR;

            }
        }
        return ErrorCodes.OK;
    }

    public ErrorCodes sendMessageAll(BaseMessage baseMessage) {
        final String result = baseMessage.to_json(mapper);
        if (result == null) {
            return ErrorCodes.ERROR;
        }
        return sendMessageAll(result);
    }


    public ErrorCodes sendWithOut(String msg, String userId) {
        for (Map.Entry<String, GameUserController> entry : users.entrySet()) {
            final GameUserController user = entry.getValue();
            if (!user.getUserId().equals(userId)) {
                final GameUserController.ErrorCodes err = user.sendMessageToUser(msg);
                switch (err) {
                    case OK:
                        break;

                    default:
                        return ErrorCodes.ERROR;

                }
            }
        }
        return ErrorCodes.OK;
    }


    @SuppressWarnings("UnusedReturnValue")
    public void gameStart() {
        try {
            this.wait(WAIT);
        } catch (Exception e) {
//            return sendMessageAll(new GameReadyMessage());
        }
        run();
//        sendMessageAll(new GameReadyMessage());
    }

    public GameView getGameView() {
        final ArrayList<UserGameView> userList = new ArrayList<>();
        for (GameUserController tab : users.values()) {
            final UserGameView view = tab.getGameView();
            userList.add(view);
        }
        return new GameView(mapper, userList);
    }
}
