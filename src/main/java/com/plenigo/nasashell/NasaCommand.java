package com.plenigo.nasashell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class NasaCommand {

    @Value( "${nasa.api-key}" )
    private String nasaApiKey;

    private final NasaFeignClient nasaFeignClient;


    @ShellMethod(key = "get-nasa-images", value = "getting nasa images")
    public void getNasaImages(@ShellOption String[] args) {

        String date;
        String folder;

        // if only folder name is passed, get the latest available date from nasa
        if (args.length == 1) {
            folder = args[0];

            List<NasaDateDto> dateList = nasaFeignClient.getAvailableDates(nasaApiKey);

            date = dateList.get(0).getDate();
        }
        else if (args.length == 2) {
            date = args[0];
            folder = args[1];
        } else {
            System.out.println("Wrong number of arguments!");
            throw new RuntimeException("Wrong number of arguments!");
        }

        List<NasaImageDto> imageList = nasaFeignClient.getImagesByDate(date, nasaApiKey);

        String currentFolder = System.getProperty("user.dir");
        String targetFolderPath = currentFolder + File.separator + folder;
        String subFolderPath = targetFolderPath + File.separator + date;

        boolean isTargetFolderCreated = createFolder(targetFolderPath);

        if (isTargetFolderCreated) {
            System.out.println("Target folder created successfully at: " + targetFolderPath);

            boolean isSubFolderCreated = createFolder(subFolderPath);

            if (isSubFolderCreated) {
                System.out.println("Subfolder created successfully at: " + subFolderPath);

                imageList.forEach(image -> {
                    try {
                        saveImage(image.getImageName(), date, subFolderPath);
                    } catch (IOException e) {
                        System.out.println("Error while saving image: " + image.getImageName());
                    }
                });

            } else {
                System.out.println("Failed to create subfolder. It may already exist at: " + subFolderPath);
            }

        } else {
            System.out.println("Failed to create target folder. It may already exist at: " + targetFolderPath);
        }
    }

    private boolean createFolder(String folderName) {

        File folder = new File(folderName);
        boolean folderCreated = false;
        if (!folder.exists()) {
            folderCreated = folder.mkdir();
        }
        return folder.exists() || folderCreated;
    }

    private void saveImage(String imageName, String date, String folder) throws IOException {

        String[] dateParts = date.split("-");
        String year = dateParts[0];
        String month = dateParts[1];
        String day = dateParts[2];
        imageName+=".png";

        byte[] imageBytes = nasaFeignClient.getImage(year, month, day, imageName, nasaApiKey);

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(folder+"/"+imageName));
        outputStream.write(imageBytes);

    }
}
