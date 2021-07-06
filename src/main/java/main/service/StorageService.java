package main.service;

import main.model.User;
import main.repository.UserRepository;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Objects;
import java.util.UUID;


@Service
public class StorageService {

    private final UserRepository userRepository;

    @Autowired
    public StorageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Метод загружает на сервер изображение в папку upload и три случайные подпапки.
     * @param photo
     * @return
     */
    public ResponseEntity<Object> store(MultipartFile photo) {

        String folder = "upload";

        String[] uuidPath = UUID.randomUUID().toString().split("-", 3);

        String resultPath = folder + "/" + uuidPath[0] + "/" + uuidPath[1] + "/" + uuidPath[2];


        Path uploadDir = Paths.get(resultPath);
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Path filePath = uploadDir.resolve(photo.getOriginalFilename());
        try {
            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new ResponseEntity<>("/" + resultPath + "/" + photo.getOriginalFilename(), HttpStatus.OK);
    }

    public String uploadPhoto(MultipartFile newPhoto, Principal principal) throws Exception {
        String folder = "avatars";

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        String file = folder + "/" + user.getId();
        String resultPath = folder + "/" + user.getId();

        Path uploadDir = Paths.get(resultPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path filePath = uploadDir.resolve(Objects.requireNonNull(newPhoto.getOriginalFilename()));
        File resizeFile = new File(String.valueOf(filePath));
        simpleResizeImage(newPhoto, resizeFile);


        return "/" + file + "/" + newPhoto.getOriginalFilename();
    }

    private void simpleResizeImage(MultipartFile newPhoto, File resizeFile) throws Exception {
        Thumbnails.of(newPhoto.getInputStream()).crop(Positions.CENTER_LEFT).size(36, 36).keepAspectRatio(true).toFile(resizeFile);
    }
}
