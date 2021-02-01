package osmaha.cashmachine.model;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RoleE name;

    public Role() {
    }

    public Role(Long id, RoleE name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleE getName() {
        return name;
    }

    public void setName(RoleE name) {
        this.name = name;
    }
}
