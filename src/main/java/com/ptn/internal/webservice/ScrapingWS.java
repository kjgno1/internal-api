package com.ptn.internal.webservice;

import com.ptn.internal.model.dto.BaseResponse;
import com.ptn.internal.model.dto.BestHqRequest;
import com.ptn.internal.model.dto.Status;
import com.ptn.internal.service.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/api/v1/scraping")
public class ScrapingWS extends BaseWS {

    @Autowired
    private ScrapingService scrapingService;

    @RequestMapping(value = "/bestHqWallPapers", method = {RequestMethod.POST}, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BaseResponse> getFromBestHqWallpapers(@RequestBody BestHqRequest bestHqRequest) {

        try {
            scrapingService.getBestWallPapersImage(bestHqRequest);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    @RequestMapping(value = "/wallPapersCraft", method = {RequestMethod.POST}, consumes = {
            MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BaseResponse> getFromWallPapersCraft(@RequestBody BestHqRequest bestHqRequest) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse = scrapingService.getBestWallPapersCraftKafka(bestHqRequest);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(baseResponse);
    }

    @RequestMapping(value = "/retry", method = {RequestMethod.GET},  produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BaseResponse> retry(@RequestParam(required = false, defaultValue = "50") int limit) {

        scrapingService.retryFail(limit);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(Status.builder().message("Retry successfully").code("0").build());
        return ResponseEntity.ok(baseResponse);
    }

}
