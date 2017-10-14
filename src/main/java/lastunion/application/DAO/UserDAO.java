package lastunion.application.DAO;

import lastunion.application.Models.UserModel;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(final JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserModel getUserById(final Integer id)  {
        final String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                new UserModel(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("score")
                )
        );
    }

    public UserModel getUserByName(final String name)  {
        final String sql = "SELECT * FROM users WHERE login = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{name}, (rs, rowNum) ->
                new UserModel(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("score")
                )
        );
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    private void AppendStringField(StringBuilder builder, String fieldName, String value){
        builder.append(fieldName);
        builder.append('=');
        builder.append('\'');
        builder.append(value);
        builder.append('\'');
        builder.append(',');
    }

    @SuppressWarnings("InstanceMethodNamingConvention")
    private void AppendIntegerField(StringBuilder builder, @SuppressWarnings("SameParameterValue") String fieldName,
                                    Integer value){
        builder.append(fieldName);
        builder.append('=');
        builder.append(value);
    }

    public void modifyUser(UserModel user, UserModel changedUser) {
        final StringBuilder builder = new StringBuilder ("UPDATE users set ");// login = ");
        AppendStringField(builder, "login", changedUser.getUserName());
        AppendStringField(builder, "email", changedUser.getUserEmail());
        AppendStringField(builder, "password", changedUser.getUserPasswordHash());
        AppendIntegerField(builder, "score", changedUser.getUserHighScore());
        builder.append(" WHERE ");
        AppendStringField(builder, "login", user.getUserName());
        builder.deleteCharAt(builder.length() - 1);
        executeQuery(builder.toString());
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    public void saveUser(UserModel user)  {
        final StringBuilder builder = new StringBuilder("INSERT INTO users (login, email, password) VALUES(");
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
    public void deleteUserByName(final String userName){
        final StringBuilder builder = new StringBuilder("DELETE FROM users WHERE login = ");
        builder.append('\'');
        builder.append(userName);
        builder.append('\'');

        executeQuery(builder.toString());
    }

    private void executeQuery(String query)  {
        jdbcTemplate.update(query);
    }

}
