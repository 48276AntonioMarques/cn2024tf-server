package pt.isel.leic.cn.server;

import io.grpc.ServerBuilder;
import pt.isel.leic.cn.server.repository.CloudStorageRepository;
import pt.isel.leic.cn.server.repository.FirestoreRepository;
import pt.isel.leic.cn.server.service.ImageProcessorService;
import pt.isel.leic.cn.server.service.PublishService;

public class Server {

    private static String projectId = null;
    private static String bucketName = null;
    private static String dbName = null;
    private static String topicId = null;
    private static int svcPort = 8000;

    public static void main(String[] args) {
        try {
            switch (args.length) {
                case 5: // Optional port number
                    svcPort = Integer.parseInt(args[4]);
                case 4: // Mandatory bucket name and database name
                    topicId = args[3];
                    dbName = args[2];
                    bucketName = args[1];
                    projectId = args[0];
                    break;
                default:
                    System.out.println("Usage: Server <project-id> <bucket-name> <db-name> <topic-id> [<port>]");
                    System.exit(1);
            }

            CloudStorageRepository.start(bucketName);
            FirestoreRepository.start(dbName);

            PublishService publisher = new PublishService(projectId, topicId);

            io.grpc.Server svc = ServerBuilder.forPort(svcPort)
                    // Add one or more services.
                    // The Server can host many services in same TCP/IP port
                    .addService(new ImageProcessorService(publisher))
                    .build();
            svc.start();
            System.out.println("Server started on port " + svcPort);
            // Java virtual machine shutdown hook
            // to capture normal or abnormal exits
            Runtime.getRuntime().addShutdownHook(new ShutdownHook(svc, publisher));
            // Waits for the server to become terminated
            svc.awaitTermination();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void exit(ExitCode code) {
        System.exit(code.ordinal());
    }
}

