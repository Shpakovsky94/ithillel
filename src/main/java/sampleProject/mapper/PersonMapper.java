package sampleProject.mapper;

import sampleProject.entity.Person;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class PersonMapper {
    public static List<Person> convert(ResultSet rs) {
        List<Person> result = new LinkedList<>();

        try {
            while (rs.next()) {
                Person p = new Person();
                p.setPersonId(rs.getInt("PK_PERSON_ID"));
                p.setFirstName(rs.getString("FIRST_NAME"));
                p.setLastName(rs.getString("LAST_NAME"));
                p.setAge(rs.getInt("AGE"));
                p.setCity(rs.getString("CITY"));
                result.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
