package lesson24.generic.daoPattern;

public interface UserDao extends GenericDao<User, Long> {

    User loadUserByUsername(String username);
}
