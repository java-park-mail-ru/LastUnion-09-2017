package lastunion.application.managers;



import lastunion.application.dao.UserDAO;
import lastunion.application.models.SignInModel;
import lastunion.application.models.SignUpModel;
import lastunion.application.models.UserModel;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

@Service
public class UserManager {
    @NotNull
    private final UserDAO userDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);

    public enum ResponseCode {
        @SuppressWarnings("EnumeratedConstantNamingConvention") OK,
        LOGIN_IS_BUSY,
        INCORRECT_LOGIN,
        INCORRECT_PASSWORD,
        DATABASE_ERROR
    }

    @Autowired
    public UserManager(final JdbcTemplate jdbcTemplate) {
        userDAO = new UserDAO(jdbcTemplate);
    }

    // Work with password
    ////////////////////////////////////////////////////////////////////////
    @Bean
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String makePasswordHash(@NotNull final String password) {
        return passwordEncoder().encode(password);
    }

    private boolean checkPassword(@NotNull final String password, @NotNull final String passwordHash) {
        return passwordEncoder().matches(password, passwordHash);
    }

    public boolean checkPasswordByUserName(@NotNull final String password, @NotNull final String userLogin) {
        try {
            final UserModel savedUser = userDAO.getUserByName(userLogin);

            if (checkPassword(password, savedUser.getUserPasswordHash())) {
                return true;
            }
        } catch (DataAccessException ex) {
            return false;
        }
        return false;
    }
    //////////////////////////////////////////////////////////////////////////

    public ResponseCode signInUser(@NotNull final SignInModel signInUserData) {

        // Check user storaged in database
        try {
            final UserModel savedUser = userDAO.getUserByName(signInUserData.getUserName());

            // wrong password
            if (!checkPassword(signInUserData.getUserPassword(), savedUser.getUserPasswordHash())) {
                return ResponseCode.INCORRECT_PASSWORD;
            }
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException ex) {
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode signUpUser(@NotNull final SignUpModel signUpUserData) {

        // Creating UserModel to stoarage
        final UserModel newUser = new UserModel(signUpUserData);
        newUser.setUserPasswordHash(makePasswordHash(signUpUserData.getUserPassword()));

        // trying to save user
        try {
            userDAO.saveUser(newUser);
            LOGGER.info("User registered with name " + newUser.getUserName());
        } catch (DuplicateKeyException dupEx) {
            return ResponseCode.LOGIN_IS_BUSY;
        } catch (DataAccessException daEx) {
            LOGGER.info(daEx.getMessage());
            return ResponseCode.DATABASE_ERROR;
        }

        return ResponseCode.OK;
    }

    public ResponseCode changeUserEmail(@NotNull final String newEmail, @NotNull final String userName) {
        // trying to get storaged user and copy its data to new
        // user, than in new user modify email and save it
        try {
            final UserModel user = userDAO.getUserByName(userName);
            final UserModel modifiedUser = new UserModel(user);
            modifiedUser.setUserEmail(newEmail);
            userDAO.modifyUser(user, modifiedUser);
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.info(daEx.getMessage());
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public boolean userExists(@Nullable String userName) {
        if (userName == null) {
            return false;
        }
        try {
            userDAO.getUserByName(userName);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public ResponseCode changeUserPassword(@NotNull final String newPassword, @NotNull final String userName) {
        // trying to get storaged user and copy its data to new
        // user, than in new user modify email and save it
        try {
            final UserModel user = userDAO.getUserByName(userName);
            final UserModel modifiedUser = new UserModel(user);
            modifiedUser.setUserPasswordHash(makePasswordHash(newPassword));
            userDAO.modifyUser(user, modifiedUser);
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.info(daEx.getMessage());
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode getUserByName(@NotNull final String userName, UserModel user) {
        // trying to get storaged user
        try {
            final UserModel tempUser = userDAO.getUserByName(userName);
            user.setUserName(tempUser.getUserName());
            user.setUserEmail(tempUser.getUserEmail());
            user.setUserHighScore(tempUser.getUserHighScore());
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.info(daEx.getMessage());
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }

    public ResponseCode deleteUserByName(@NotNull final String userName) {
        // trying to get storaged user
        try {
            userDAO.deleteUserByName(userName);
        } catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        } catch (DataAccessException daEx) {
            LOGGER.info(daEx.getMessage());
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }
    /*
    @SuppressWarnings("unused")
    public ResponseCode getUserById(@NotNull final Integer userId, UserModel user){
        // trying to get storaged user
        try {
            final UserModel tempUser = userDAO.getUserById(userId);
            user.setUserName(tempUser.getUserName());
            user.setUserEmail(tempUser.getUserEmail());
            user.setUserHighScore(tempUser.getUserHighScore());
        }
        // No user found
        catch (EmptyResultDataAccessException ex) {
            return ResponseCode.INCORRECT_LOGIN;
        }
        // error db
        catch (DataAccessException daEx) {
            LOGGER.info(daEx.getMessage());
            return ResponseCode.DATABASE_ERROR;
        }
        return ResponseCode.OK;
    }
    */
}
