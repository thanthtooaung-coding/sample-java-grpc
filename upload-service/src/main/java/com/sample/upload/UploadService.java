package com.sample.upload;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import java.io.IOException;

public class UploadService {

    public static void main(String[] args) throws IOException {
        GitHubService gitHubService = new GitHubService();

        Javalin app = Javalin.create(config -> {
            config.http.maxRequestSize = 100_000_000; // 100 MB
        }).start(8000);

        app.get("/", ctx -> ctx.result("Upload Service (GitHub) is ready."));

        app.post("/upload", ctx -> {
            UploadedFile uploadedFile = ctx.uploadedFile("file");

            if (uploadedFile == null) {
                ctx.status(400).result("No file uploaded. Please use the 'file' form field.");
                return;
            }

            try {
                String commitMessage = "feat: Upload new file " + uploadedFile.filename();
                gitHubService.uploadFile(
                        uploadedFile.filename(),
                        uploadedFile.content(),
                        commitMessage
                );
                ctx.status(200).result("File '" + uploadedFile.filename() + "' uploaded successfully to GitHub.");
            } catch (Exception e) {
                ctx.status(500).result("Error uploading file: " + e.getMessage());
            }
        });
    }
}
