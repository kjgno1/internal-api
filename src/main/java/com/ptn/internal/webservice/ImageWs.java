package com.ptn.internal.webservice;

import com.ptn.internal.model.TblImageInfo;
import com.ptn.internal.model.dto.GeneralResponse;
import com.ptn.internal.model.dto.img.ImageResponse;
import com.ptn.internal.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/images")
public class ImageWs extends BaseWS {
    @Autowired
    private ImageRepository imageRepository;

    @RequestMapping(value = "/retrieveByStatus", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<List<TblImageInfo>>> getAllListImages(@RequestParam(required = false, defaultValue = "150") int limit,
                                                                                @RequestParam(required = false, defaultValue = "0") int status,
                                                                                @RequestParam(required = false, defaultValue = "wallpaperscraft.com") String type) {
        List<TblImageInfo> imageInfoList;
        try {
            imageInfoList = imageRepository.getAllListImage(status, limit, type);
        } catch (Exception e) {
            return failed(Optional.ofNullable(e.getMessage()));
        }
        return success(imageInfoList);

    }

    @RequestMapping(value = "/performUpdate/{id}/{status}", method = {RequestMethod.PUT}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<Object>> getAllListImages(@PathVariable BigInteger id, @PathVariable int status) {
        try {
           imageRepository.updateStatusImage(id, status);
        } catch (Exception e) {
            return failed(Optional.ofNullable(e.getMessage()));
        }
        return success();
    }




}
