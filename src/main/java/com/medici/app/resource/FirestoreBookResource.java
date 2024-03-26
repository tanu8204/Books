package com.medici.app.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.medici.app.entity.Book;

/**
 * 
 * @author a73s
 *
 */
@RestController
@RequestMapping(value = "/book")
public class FirestoreBookResource {

	private static final String BOOKS = "Books";

	protected Logger logger = Logger.getLogger(FirestoreBookResource.class.getName());

	@Autowired
	Firestore firestore;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<?> save(@RequestBody Book model) {

		try {
			WriteResult writeResult = this.firestore.collection(BOOKS).document(model.getId() != null ? model.getId() : UUID.randomUUID().toString()).set(model).get();
			return new ResponseEntity(writeResult, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> findById(@PathVariable String id) {

		try {
			ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = this.firestore.collection(BOOKS).document(id).get();
			Book payload = documentSnapshotApiFuture.get().toObject(Book.class);
			return new ResponseEntity(payload, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

	}

//	@RequestMapping(value = "/all", method = RequestMethod.GET)
//	public ResponseEntity<?> findAll() {
//
//		try {
//			List<Map<String, Object>> payload = new ArrayList<>();
//
//			ApiFuture<QuerySnapshot> results = this.firestore.collection(BOOKS).get();
//			results.get().getDocuments().stream().forEach(action -> {
//				payload.add(action.getData());
//			});
//
//			return new ResponseEntity(payload, new HttpHeaders(), HttpStatus.OK);
//		} catch (Exception e) {
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<?> findAll() {
		try {
			List<Map<String, Object>> payload = new ArrayList<>();

			// Assuming 'this.firestore' is your Firestore instance
			ApiFuture<QuerySnapshot> querySnapshotApiFuture = this.firestore.collection(BOOKS).get();
			QuerySnapshot querySnapshot = querySnapshotApiFuture.get(); // Blocking call to get the result

			for (DocumentSnapshot document : querySnapshot.getDocuments()) {
				payload.add(document.getData());
			}

			return ResponseEntity.ok(payload);
		} catch (InterruptedException | ExecutionException e) {
			// Handle exceptions
			e.printStackTrace(); // Log the exception for debugging
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve data from Firestore");
		}
	}


}
