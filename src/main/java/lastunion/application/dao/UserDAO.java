package lastunion.application.dao;

import lastunion.application.models.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.validation.constraints.NotNull;

public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean userExist(@NotNull String userName) {
        final String query = "SELECT COUNT(*) FROM USERS WHERE username=?";
        final int count = jdbcTemplate.queryForObject(query, new Object[] {userName}, Integer.class);
        return count != 0;
    }

    @SuppressWarnings("unused")
    public UserModel getUserById(final Integer id) {
        final String query = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(query, new Object[] {id}, (rs, rowNum) ->
                new UserModel(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("useremail"),
                        rs.getString("userpassword"),
                        rs.getInt("userscore")
                )
        );
    }

    public UserModel getUserByName(final String userName) {
        final String sql = "SELECT * FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] {userName}, (rs, rowNum) ->
                new UserModel(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("useremail"),
                        rs.getString("userpassword"),
                        rs.getInt("userscore")
                )
        );
    }

    public void modifyUser(UserModel user, UserModel changedUser) {
        final String query = "UPDATE users set username=?, useremail=?, userpassword=?, userscore=? WHERE username=?";
        jdbcTemplate.update(query, changedUser.getUserName(),
                changedUser.getUserEmail(),
                changedUser.getUserPasswordHash(),
                changedUser.getUserHighScore(),
                user.getUserName());
    }

    public void saveUser(UserModel user) {
        final String query = "INSERT INTO users (username, useremail, userpassword) VALUES(?, ?, ?)";
        jdbcTemplate.update(query, user.getUserName(), user.getUserEmail(), user.getUserPasswordHash());
    }

    public void deleteUserByName(final String userName) {
        final String query = "DELETE FROM users WHERE username = ?";
        jdbcTemplate.update(query, userName);
    }
}
