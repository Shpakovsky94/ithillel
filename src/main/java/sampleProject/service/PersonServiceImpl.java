package sampleProject.service;

import sampleProject.ConnectionFactory;
import sampleProject.entity.Person;
import sampleProject.mapper.PersonMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PersonServiceImpl {


    public List<Person> getAll() throws SQLException {
        String sql = "SELECT * FROM PERSON";
        Statement statement = ConnectionFactory.getInstance().connect().prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        List<Person> result = PersonMapper.convert(rs);
        return result;
    }

//    public List<Person> insert( Person person) throws SQLException {
//        String sql = "INSERT INTO Users (username, password, fullname, email) VALUES (?, ?, ?, ?)";
//
//        PreparedStatement statement = ConnectionFactory.getInstance().connect().prepareStatement(sql);
//        statement.setString(1, "bill");
//        statement.setString(2, "secretpass");
//        statement.setString(3, "Bill Gates");
//        statement.setString(4, "bill.gates@microsoft.com");
//
//        int rowsInserted = statement.executeUpdate();
//        if (rowsInserted > 0) {
//            System.out.println("A new user was inserted successfully!");
//        }
//    }

}
