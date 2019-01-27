package at.or.wolfram.mate.familytree.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Tree {

	private List<Person> persons = new ArrayList<Person>();
	
}
