package pt.isel.leic.cn.server.repository;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import pt.isel.leic.cn.server.ExitCode;
import pt.isel.leic.cn.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class FirestoreRepository {
    private static Firestore db;

    public static void start(String dbName) {
        try {
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            FirestoreOptions options = FirestoreOptions
                    .newBuilder().setDatabaseId(dbName).setCredentials(credentials)
                    .build();
            FirestoreRepository.db = options.getService();
        }
        catch (IOException e) {
            Server.exit(ExitCode.INVALID_CREDENTIALS);
            throw new RuntimeException(e);
        }
    }

    public static Stream<String> getTags(String reference, String language) {
        // Get tags from firestore
        ApiFuture<DocumentSnapshot> future = db.collection(language).document(reference).get();
        try {
            DocumentSnapshot realDoc = future.get();
            String object = realDoc.get("labels").toString();

            return Arrays
                    .stream(object.substring(0, object.length() - 1).split(","))
                    .map( tag -> tag.substring(1));
        }
        catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
