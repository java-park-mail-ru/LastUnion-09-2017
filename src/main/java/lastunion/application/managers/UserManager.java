package lastunion.application.managers;


import com.github.javafaker.Bool;
import lastunion.application.dao.UserDAO;
import lastunion.application.models.SignInModel;
import lastunion.application.models.SignUpModel;
import lastunion.application.models.UserModel;
import org.apache.catalina.User;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.List;

@Service
public class UserManager {
    @NotNull
    private final UserDAO userDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);

    public enum ResponseCode {
        @SuppressWarnings("EnumeratedConstantNamingConvention") OK,
        LOGIN_IS_TAKEN,
        EMAIL_IS_TAKEN,
        INCORRECT_LOGIN,
        INCORRECT_PASSWORD,
        DATABASE_ERROR
    }

    @Autowired
    public UserManager(final JdbcTemplate jdbcTemplate) {
        userDAO = new UserDAO(jdbcTemplate);
    }

    @Autowired
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ResponseCode checkPasswordByUserName(@NotNull final String password, @NotNull final String userLogin) {
        try {
            final UserModel savedUser = userDAO.getUserByName(userLogin);

            if (passwordEncoder().matches(password, savedUser.getUserPasswordHash())) {
                return ResponseCode.OK;
            }
        } catch (DataAccessException daEx) {
            LOGGER.error("Error Database", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.INCORRECT_PASSWORD;
    }

    public ResponseCode signInUser(@NotNull final SignInModel signInUserData) {

        try {
            final UserModel savedUser = userDAO.getUserByName(signInUserData.getUserName());

            if (!passwordEncoder().matches(signInUserData.getUserPassword(), savedUser.getUserPasswordHash())) {
                return ResponseCode.INCORRECT_PASSWORD;
            }
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;

        } catch (DataAccessException daEx) {
            LOGGER.error("Error Database", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode signUpUser(@NotNull final SignUpModel signUpUserData) {
        final UserModel newUser = new UserModel(signUpUserData);
        newUser.setUserPasswordHash(passwordEncoder().encode(signUpUserData.getUserPassword()));

        try {
            userDAO.saveUser(newUser);
        } catch (DuplicateKeyException dupEx) {
            if (dupEx.getMessage().contains("username_key")) {
                return ResponseCode.LOGIN_IS_TAKEN;
            } else {
                return ResponseCode.EMAIL_IS_TAKEN;
            }
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }

        return ResponseCode.OK;
    }

    public ResponseCode changeUserEmail(@NotNull final String newEmail, @NotNull final String userName) {
        try {
            final UserModel user = userDAO.getUserByName(userName);
            final UserModel modifiedUser = new UserModel(user);
            modifiedUser.setUserEmail(newEmail);
            userDAO.modifyUser(user, modifiedUser);
        } catch (DuplicateKeyException dupEx) {
            return ResponseCode.EMAIL_IS_TAKEN;
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode userExists(@Nullable String userName) {
        if (userName == null) {
            return ResponseCode.INCORRECT_LOGIN;
        }
        try {
            if (!userDAO.userExist(userName)) {
                return ResponseCode.INCORRECT_LOGIN;
            }
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode changeUserPassword(@NotNull final String oldPassword, @NotNull String newPassword, @NotNull final String userName) {
        final ResponseCode checkUserResponseCode = userExists(userName);
        if (checkUserResponseCode != ResponseCode.OK) {
            return checkUserResponseCode;
        }

        final ResponseCode checkPasswordResponseCode = checkPasswordByUserName(oldPassword, userName);
        if (checkPasswordResponseCode != ResponseCode.OK) {
            return checkPasswordResponseCode;
        }

        try {
            final UserModel user = userDAO.getUserByName(userName);
            final UserModel modifiedUser = new UserModel(user);
            modifiedUser.setUserPasswordHash(passwordEncoder().encode(newPassword));
            userDAO.modifyUser(user, modifiedUser);
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode getUserByName(@NotNull final String userName, UserModel user) {
        try {
            final UserModel tempUser = userDAO.getUserByName(userName);
            user.setUserName(tempUser.getUserName());
            user.setUserEmail(tempUser.getUserEmail());
            user.setUserHighScore(tempUser.getUserHighScore());
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode deleteUserByName(@NotNull final String userName) {
        try {
            userDAO.deleteUserByName(userName);
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode getScores(@NotNull Integer limit, Integer offset, Boolean order, List<UserModel> scores) {
        try {
            List<UserModel> scores_ = userDAO.getScores(limit, offset, order);
            for (UserModel model : scores_) {
                scores.add(model);
            }
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode changeUserHighScore(@NotNull String userName, int score) {
        try {
            final UserModel user = userDAO.getUserByName(userName);
            final UserModel modifiedUser = new UserModel(user);
            modifiedUser.setUserHighScore(score);
            userDAO.modifyUser(user, modifiedUser);
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.error("Error DataBase", daEx);
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }
}
