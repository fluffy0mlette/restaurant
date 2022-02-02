package com.springrest.restaurant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class RestaurantController {

	
	HashMap<Integer,HashMap<Integer,Integer>> itemList = new HashMap<Integer,HashMap<Integer,Integer>>();
	
	@EventListener(ApplicationReadyEvent.class)
	public HttpStatus StartUp() {
		
		//File myObj = new File("/Users/fluffy/Downloads/delivery/initialData.txt");
		String basePath = new File("").getAbsolutePath();
		File myObj = new File(basePath + "/initialData.txt");
		Scanner myReader = null;
		try {
			myReader = new Scanner(myObj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(myReader.hasNextLine()) {
			Scanner s2 = new Scanner(myReader.nextLine());
			String str1 = s2.next();
			if(str1.charAt(0) == '*') {
				break;
			}
			String str2 = s2.next();
			Integer restId = Integer.parseInt(str1);
			Integer numItem = Integer.parseInt(str2);
			HashMap<Integer,Integer> innerMap = new HashMap<Integer,Integer>();
			for(int i=0; i<numItem; i++) {
				Scanner s3 = new Scanner(myReader.nextLine());
				Integer itemId = Integer.parseInt(s3.next());
				s3.next(); //skipping over the price.
				Integer qty = Integer.parseInt(s3.next());
				innerMap.put(itemId,qty);
				System.out.println("Done");
			}
			itemList.put(restId, innerMap);
		}
		myReader.close();
		return HttpStatus.CREATED;
	}
	
	
	@GetMapping("/showItems") 
	public HashMap<Integer,HashMap<Integer,Integer>> showItems() {
		return itemList;
	}
	
	@PostMapping("/acceptOrder")
	public ResponseEntity<String> acceptOrder(@RequestBody Map<String, Integer> item) {
		Integer restId = item.get("restId");
		Integer itemId = item.get("itemId");
		Integer qty = item.get("qty");
		
		Integer availableQty = itemList.get(restId).get(itemId);
		if(availableQty >= qty) {
			itemList.get(restId).put(itemId, availableQty-qty);
			return new ResponseEntity<String>(HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<String>(HttpStatus.GONE);
		}
	}
	
	@PostMapping("/refillItem")
	public HttpStatus refillItem(@RequestBody Map<String, Integer> item) {
		
		Integer restId = item.get("restId");
		Integer itemId = item.get("itemId");
		Integer qty = item.get("qty");
		Integer availableQty = itemList.get(restId).get(itemId);
		itemList.get(restId).put(itemId, availableQty+qty);
		return HttpStatus.CREATED;
		
	}
	
	@PostMapping("/reInitialize")
	public ResponseEntity<String> reInitialize() {
		//File myObj = new File("/Users/fluffy/Downloads/delivery/initialData.txt");
		String basePath = new File("").getAbsolutePath();
		File myObj = new File(basePath + "/initialData.txt");
		Scanner myReader = null;
		try {
			myReader = new Scanner(myObj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(myReader.hasNextLine()) {
			Scanner s2 = new Scanner(myReader.nextLine());
			String str1 = s2.next();
			if(str1.charAt(0) == '*') {
				break;
			}
			String str2 = s2.next();
			Integer restId = Integer.parseInt(str1);
			Integer numItem = Integer.parseInt(str2);
			
			for(int i=0; i<numItem; i++) {
				Scanner s3 = new Scanner(myReader.nextLine());
				Integer itemId = Integer.parseInt(s3.next());
				s3.next(); //skipping over the price.
				Integer qty = Integer.parseInt(s3.next());
				itemList.get(restId).put(itemId,qty);
				System.out.println("Done");
			}
			
		}
		myReader.close();
		return ResponseEntity.status(HttpStatus.CREATED).body("CREATED");
	}
	
	
	
}
