package lastunion.application.dao;

import lastunion.application.models.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.validation.constraints.NotNull;

@Repository
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

    @SuppressWarnings({"InstanceMethodNamingConvention", "RedundantSuppression"})
    private void appendStringField(StringBuilder builder, String fieldName, String value) {
        builder.append(fieldName);
        builder.append('=');
        builder.append('\'');
        builder.append(value);
        builder.append('\'');
        builder.append(',');
    }

    @SuppressWarnings({"InstanceMethodNamingConvention", "RedundantSuppression"})
    private void appendIntegerField(StringBuilder builder, @SuppressWarnings("SameParameterValue") String fieldName,
                                    Integer value) {
        builder.append(fieldName);
        builder.append('=');
        builder.append(value);
    }

    public void modifyUser(UserModel user, UserModel changedUser) {
        final String query = "UPDATE users set username=?, useremail=?, userpassword=?, userscore=? WHERE usernam=?";
        final StringBuilder builder = new StringBuilder("UPDATE users set ");
//        appendStringField(builder, "username", changedUser.getUserName());
//        appendStringField(builder, "useremail", changedUser.getUserEmail());
//        appendStringField(builder, "userpassword", changedUser.getUserPasswordHash());
//        appendIntegerField(builder, "userscore", changedUser.getUserHighScore());
//        builder.append(" WHERE ");
//        appendStringField(builder, "username", user.getUserName());
//        builder.deleteCharAt(builder.length() - 1);
//        executeQuery(builder.toString());
//        jdbcTemplate.query(query, new Object[]{changedUser.getUserName()},
//                new Object[]{changedUser.getUserEmail()},
//                new Object[]{changedUser.getUserPasswordHash()},
//                new Object[]{changedUser.getUserHighScore()},
//                new Object[]{user.getUserName()});
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public void saveUser(UserModel user) {
        final StringBuilder builder = new StringBuilder("INSERT INTO users (username, useremail, userpassword) VALUES(");
        builder.append('\'');
        builder.append(user.getUserName());
        builder.append('\'');
        builder.append(',');
        builder.append('\'');
        builder.append(user.getUserEmail());
        builder.append('\'');
        builder.append(',');
        builder.append('\'');
        builder.append(user.getUserPasswordHash());
        builder.append('\'');
        builder.append(')');

        executeQuery(builder.toString());
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public void deleteUserByName(final String userName) {
        final StringBuilder builder = new StringBuilder("DELETE FROM users WHERE username = ");
        builder.append('\'');
        builder.append(userName);
        builder.append('\'');

        executeQuery(builder.toString());
    }

    private void executeQuery(String query) {
        jdbcTemplate.update(query);
    }

}
