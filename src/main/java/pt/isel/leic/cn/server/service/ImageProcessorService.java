package pt.isel.leic.cn.server.service;

import com.google.protobuf.ByteString;
import image_processor.*;
import image_processor.Void;
import io.grpc.stub.StreamObserver;
import pt.isel.leic.cn.server.repository.CloudStorageRepository;
import pt.isel.leic.cn.server.repository.FirestoreRepository;
import pt.isel.leic.cn.server.repository.LanguageRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class ImageProcessorService extends ImageProcessorServiceGrpc.ImageProcessorServiceImplBase {

    private final PublishService publishService;
    private final int SECONDS = 60;

    public ImageProcessorService(PublishService publishService) {
        this.publishService = publishService;
    }

    @Override
    public StreamObserver<ImageChunk> uploadImage(StreamObserver<ImageReference> responseObserver) {
        return new StreamObserver<>() {
            final ByteString image = ByteString.EMPTY;

            @Override
            public void onNext(ImageChunk imageChunk) {
                image.concat(imageChunk.getData());
            }

            @Override
            public void onError(Throwable throwable) {
                throw new RuntimeException(throwable);
                // System.out.println("Error uploading image.");
            }

            @Override
            public void onCompleted() {
                String name = UUID.randomUUID().toString();
                CloudStorageRepository.upload(name, image);
            }
        };
    }

    @Override
    public void getImageReferences(Void request, StreamObserver<ImageReference> responseObserver) {
        // Get all image references from cloud storage
        System.out.println("Getting image references...");
        try {
            List<String> refs = CloudStorageRepository.getReferences();
            for (String ref : refs) {
                responseObserver.onNext(ImageReference.newBuilder().setReference(ref).build());
            }
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            System.out.println("Error getting image references.");
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void getImageTags(ImageClassification request, StreamObserver<ImageTag> responseObserver) {
        String reference = request.getReference();
        String language = request.getLanguage();
        // Try to get image tags from firestore
        System.out.println("Getting image tags...");
        Stream<String> tags = null;
        try {
            tags = FirestoreRepository.getTags(reference, language);
            tags.forEach( tag ->
                    responseObserver.onNext(ImageTag.newBuilder().setTag(tag).build())
            );
            responseObserver.onCompleted();
        }
        catch (RuntimeException e) {
            System.out.println("There where no tags already available.");
        }
        if (tags != null) return;
        // If not found, publish request on topic
        System.out.println("Publishing image classification request...");
        publishService.publish(reference, language);

        // Now wait for the firestore response
        System.out.println("Waiting for response...");
        for (int i = 0; i < SECONDS; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                i--;
            }
            try {
                FirestoreRepository.getTags(reference, language);
            }
            catch (NullPointerException e) {
                if (i == SECONDS - 1) {
                    System.out.println("Time expired while waiting for image tags.");
                    responseObserver.onError(new Exception("Time expired while waiting for image tags."));
                }
            }
        }
    }

    @Override
    public void getLanguages(Void request, StreamObserver<Language> responseObserver) {
        List<Language> languages = LanguageRepository.getLanguages();
        languages.forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
