package at.or.wolfram.mate.familytree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"at.or.wolfram.mate.familytree"})
public class FamilyTree {

    public static void main(String[] args) {
        SpringApplication.run(FamilyTree.class, args);
    }

}
