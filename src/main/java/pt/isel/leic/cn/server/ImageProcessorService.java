package pt.isel.leic.cn.server;

import image_processor.ImageProcessorServiceGrpc;
import image_processor.ImageReference;
import image_processor.ImageTag;
import image_processor.ImageUpload;
import io.grpc.stub.StreamObserver;

public class ImageProcessorService extends ImageProcessorServiceGrpc.ImageProcessorServiceImplBase {

    public ImageProcessorService() {
        System.out.println("Image Processing Service laoded.");
    }
    // Uploads an image to be tagged in specified language
    // Implements: rpc processImage(ImageUpload) returns (ImageReference);
    @Override
    public void processImage(ImageUpload request, StreamObserver<ImageReference> responseObserver) {

    }

    // Gets all tags for a given image
    // Implements: rpc getImageTags(ImageReference) returns (stream ImageTag);
    @Override
    public void getImageTags(ImageReference request, StreamObserver<ImageTag> responseObserver) {
        
    }
}
