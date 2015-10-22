package be.uantwerpen.iw.ei.se.models;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * Created by Thomas on 19/10/2015.
 */
@Entity
public class Role extends AbstractPersistable<Long>
{
    private String name;

    @ManyToMany
    @JoinTable(
            name="ROLE_PERMISSION",
            joinColumns={
                    @JoinColumn(name="ROLE_ID", referencedColumnName="ID")},
            inverseJoinColumns={
                    @JoinColumn(name="PERMISSION_ID", referencedColumnName="ID")})
    private List<Permission> permissions;

    public Role(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Permission> getPermissions()
    {
        return this.permissions;
    }

    public void setPermissions(List<Permission> permissions)
    {
        this.permissions = permissions;
    }
}
