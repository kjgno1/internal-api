package com.ptn.internal.model.dto.img;

import com.ptn.internal.model.TblImageInfo;
import com.ptn.internal.model.dto.BaseResponse;
import com.ptn.internal.model.dto.GeneralResponse;

import java.util.List;

public class ImageResponse extends GeneralResponse {
    private List<TblImageInfo> imageList;

    public List<TblImageInfo> getImageList() {
        return imageList;
    }

    public void setImageList(List<TblImageInfo> imageList) {
        this.imageList = imageList;
    }
}
