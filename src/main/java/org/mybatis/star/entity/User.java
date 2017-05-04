package org.mybatis.star.entity;

import java.util.function.Consumer;

/**
 * @author stanislav.lapitsky created 5/3/2017.
 */
public class User {
    private Long id;
    private String login;
    private String email;

    public User() {
    }

    public User(Consumer<User> builder) {
        builder.accept(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", login='").append(login).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
