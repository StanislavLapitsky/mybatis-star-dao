package org.mybatis.star.entity;

import java.util.function.Consumer;

/**
 * A Simple entity (POJO) to illustrate how it works
 * @author stanislav.lapitsky created 5/3/2017.
 */
public class City {
    private Long id;
    private String name;

    public City() {
    }

    public City(Consumer<City> builder) {
        builder.accept(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("City{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
