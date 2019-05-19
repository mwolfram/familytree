package at.or.wolfram.mate.familytree.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.or.wolfram.mate.familytree.service.familytree.FamilyTreeService;

@RestController
@RequestMapping(name = "app", value = "/rest/api/v1")
public class FamilyTreeController {

    private static final Logger logger = LoggerFactory.getLogger(FamilyTreeController.class);
    
    @Autowired
    private FamilyTreeService familyTreeService;
    
    @RequestMapping(value = "/sync",
    		method = RequestMethod.PUT)
    public 
    @ResponseBody
    ResponseEntity<String> sync(
    		@RequestParam(value="from", required=false) String fromDateStr,
    		@RequestParam(value="to", required=false) String toDateStr,
    		@RequestParam(value="user", required=false) String userName) {
//    	try {

    		
    	
			return new ResponseEntity<>("Successfully synchronized Family Tree", HttpStatus.OK);
//		} catch () {
//			String message = "Error synchronizing ERP Accounts between Odoo and Jira";
//			logger.error(message, e);
//			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
    }
    
}