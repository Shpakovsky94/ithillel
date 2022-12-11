package sampleProject;

import lombok.extern.slf4j.Slf4j;
import sampleProject.service.PersonServiceImpl;

@Slf4j
public class MainApp {

    public static void main(String[] args) {
        try {
            PersonServiceImpl personService = new PersonServiceImpl();

            System.out.println(personService.getAll());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
