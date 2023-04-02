package lesson24.generic.daoPattern;

public interface GenericDao<T1, T2> {
    T1 create(T1 t);

    T1 find(T2 id);

    T1 update(T1 t);

    void delete(T2 id);
}
