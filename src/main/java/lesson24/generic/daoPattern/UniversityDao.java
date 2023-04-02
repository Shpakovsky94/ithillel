package lesson24.generic.daoPattern;

public interface UniversityDao extends GenericDao<University, Long> {
    University getByNumber(Long number);

}
