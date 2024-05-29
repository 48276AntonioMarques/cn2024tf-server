package pt.isel.leic.cn.server.repository;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;
import pt.isel.leic.cn.server.Server;

import java.util.ArrayList;
import java.util.List;

import static pt.isel.leic.cn.server.ExitCode.INVALID_CREDENTIALS;

public class CloudStorageRepository {

    private static Storage storage = null;
    private static String bucketName = null;

    public static void start(String bucketName) {
        StorageOptions storageOptions = StorageOptions.getDefaultInstance();
        Storage storage = storageOptions.getService();
        String projID = storageOptions.getProjectId();
        if (projID != null) System.out.println("Current Project ID:" + projID);
        else {
            System.out.println("The environment variable GOOGLE_APPLICATION_CREDENTIALS isn't well defined!!");
            Server.exit(INVALID_CREDENTIALS);
        }
        CloudStorageRepository.storage = storage;
        CloudStorageRepository.bucketName = bucketName;
    }

    public static List<String> getReferences() {
        System.out.println("Getting image references from cloud storage");
        List<String> refs = new ArrayList<>();
        Bucket images = storage.get(bucketName);
        for (Blob blob : images.list().iterateAll()) {
            System.out.println("Image reference: " + blob.getName());
            refs.add(blob.getName());
        }
        return refs;
    }

    public static void upload(String name, ByteString data) {
        System.out.println("Uploading image to cloud storage");
        Blob blob = storage.get(bucketName).create(name, data.toByteArray());
        System.out.println("Image uploaded: " + blob.getName());
    }
}
