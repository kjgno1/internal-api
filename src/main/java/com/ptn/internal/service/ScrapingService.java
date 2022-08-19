package com.ptn.internal.service;

import com.ptn.internal.common.Utility;
import com.ptn.internal.model.TblBackup;
import com.ptn.internal.model.TblImageInfo;
import com.ptn.internal.model.dto.BaseResponse;
import com.ptn.internal.model.dto.BestHqRequest;
import com.ptn.internal.model.dto.Status;
import com.ptn.internal.repository.BackupRepository;
import com.ptn.internal.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class ScrapingService {
    private List<TblBackup> failList = new ArrayList<>();
    private static final Pattern pattern_craft = Pattern.compile("\\/+[a-z_0-9]+\\/[0-9]+x+[0-9]+");
    private static final String root_craft_download = "https://images.wallpaperscraft.com/image/single/";
    private static final String root_craft = "https://wallpaperscraft.com";
    private static final String kafka_topic = "image-craft-topic";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private BackupRepository backupRepository;


    public BaseResponse getBestWallPapersImage(BestHqRequest bestHqRequest) throws ExecutionException, InterruptedException {
        List<String> urlList = IntStream.rangeClosed(bestHqRequest.getPageNumber(), bestHqRequest.getPageNumber() + bestHqRequest.getTotal()).mapToObj(x -> bestHqRequest.getBaseUrl() + x).collect(Collectors.toList());
        List<TblImageInfo> lstRs = this.parallelScan(urlList);
        log.debug("All Image got: {}", lstRs.size());
        return null;
    }

    public BaseResponse getBestWallPapersCraft(BestHqRequest bestHqRequest) throws ExecutionException, InterruptedException {
        List<String> urlList = IntStream.rangeClosed(bestHqRequest.getPageNumber(), bestHqRequest.getPageNumber() + bestHqRequest.getTotal()).mapToObj(x -> bestHqRequest.getBaseUrl() + x).collect(Collectors.toList());
        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        List<TblImageInfo> lstRs = customThreadPool.submit(
                () -> urlList.parallelStream().map(x -> getLinkWallpapersCraft(x)).flatMap(Collection::parallelStream).filter(Objects::nonNull).collect(Collectors.toList())).get();
        customThreadPool.shutdown();

        log.info("Saving {} Image...", lstRs.size());
        saveAllImage(lstRs);
        log.info("Image Saved");
        log.info("Saving {} fail...", failList.size());
        saveAllBackUp(failList);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(Status.builder().message("Get success: " + lstRs.size() + " Failed: " + failList.size()).code("0").build());
        return baseResponse;
    }
    @Async("taskExecutor")
    public BaseResponse getBestWallPapersCraftKafka(BestHqRequest bestHqRequest) throws ExecutionException, InterruptedException {
        List<String> urlList = IntStream.rangeClosed(bestHqRequest.getPageNumber(), bestHqRequest.getPageNumber() + bestHqRequest.getTotal()).mapToObj(x -> bestHqRequest.getBaseUrl() + x).collect(Collectors.toList());
        ForkJoinPool customThreadPool = new ForkJoinPool(4);

        customThreadPool.submit(
                () -> urlList.parallelStream().forEach(x -> putLinkWallpapersCraft(x)));
        customThreadPool.shutdown();


        log.info("Image Saved");
        log.info("Saving {} fail...", failList.size());
        saveAllBackUp(failList);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(Status.builder().message("Failed: " + failList.size()).code("0").build());
        return baseResponse;
    }

    private List<TblImageInfo> parallelScan(List<String> urlList) throws ExecutionException, InterruptedException {
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        List<TblImageInfo> lstRs = customThreadPool.submit(
                () -> urlList.parallelStream().map(x -> getMetaDataBestWallHq(x)).flatMap(Collection::parallelStream).collect(Collectors.toList())).get();
        customThreadPool.shutdown();
        return lstRs;
    }

    private List<TblImageInfo> getMetaDataBestWallHq(String url) {
        List<TblImageInfo> tblImageInfoList = null;
        try {
            Document doc = Jsoup.connect(url).userAgent("Opera").get();

            Elements listThumb = doc.getElementsByClass("wallpaper-thumb");

            tblImageInfoList = listThumb.parallelStream().map(x -> {

                Element element = x.getElementsByClass("img-responsive").get(0);
                Pattern nameRegex = Pattern.compile("[^/]+$");
                TblImageInfo tblImageInfo = TblImageInfo.builder().url(element.attr("src"))
                        .name(String.valueOf(element.attr("src").matches(nameRegex.pattern())))
                        .tags(element.attr("title"))
                        .descriptions(element.attr("title"))
                        .type("BHQ")
                        .build();
                log.info("Scraped page: {}", url);
                return tblImageInfo;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Fail to get HTML Document: {}", url);
        }
        return tblImageInfoList;
    }

    private List<TblImageInfo> getLinkWallpapersCraft(String url) {
        List<TblImageInfo> tblImageInfoList = null;
        try {
            Document doc = Jsoup.connect(url).userAgent("Opera").get();

            Elements listThumb = doc.getElementsByClass("wallpapers__link");

            tblImageInfoList = listThumb.parallelStream().map(x -> getMetaDataCraft(root_craft + x.attr("href"))).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Fail to get HTML Document: {}", url);
        }
        return tblImageInfoList;
    }

    private void putLinkWallpapersCraft(String url) {
        try {
            Document doc = Jsoup.connect(url).userAgent("Opera").get();

            Elements listThumb = doc.getElementsByClass("wallpapers__link");

            listThumb.stream().forEach(x -> {

                sendMessage(root_craft + x.attr("href"));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        } catch (Exception e) {
            log.error("Fail to get HTML Document: {}", url);
        }
    }

    public void sendMessage(String message) {

        ListenableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(kafka_topic, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }

    @KafkaListener(topics = kafka_topic, groupId = "npt")
    public void listenGroupFoo(String message) {
        log.info("Received Message in group npt: " + message);
        try {
            TblImageInfo tblImageInfo = getMetaDataCraft(message);
            if (tblImageInfo.getName() != null)
                imageRepository.save(tblImageInfo);

        } catch (ConstraintViolationException e2) {
            log.error("An error has occurred: {}", e2.getMessage());
        } catch (Exception e) {
            backupRepository.save(new TblBackup(message));
            log.error("An error has occurred: {}", e.getMessage());
        } finally {
        }
    }

    private TblImageInfo getMetaDataCraft(String url) {
        TblImageInfo tblImageInfo = null;
        try {
            Document childDoc = Jsoup.connect(url).get();
            Elements resolutionsLink = childDoc.getElementsByClass("resolutions__link");
            Element maxResolution = resolutionsLink.stream().max(Comparator.comparing(y -> Integer.valueOf(y.html().split("x")[0]))).orElse(null);
            String name = Utility.getValueFromRegex(maxResolution.attr("href"), pattern_craft).replace("/", "_") + ".jpg";
            String href = root_craft_download + name;
            Elements tags = childDoc.getElementsByClass("wallpaper__tags").get(0).getElementsByTag("a");
            String tagStr = tags.stream().map(z -> z.html()).collect(Collectors.joining(","));
            tblImageInfo = TblImageInfo.builder().url(href)
                    .name(name)
                    .tags(tagStr)
                    .descriptions(tagStr)
                    .type("wallpaperscraft.com")
                    .build();
            log.info("Scraped page: {}", url);
        } catch (ConstraintViolationException e2) {
            log.error("An error has occurred: {}", e2.getMessage());
        } catch (Exception e) {
            failList.add(new TblBackup(url));
            log.error("An error has occurred: {}", e.getMessage());
        }
        return tblImageInfo;
    }

    public void retryFail(int limit) {
        List<TblBackup> backupList = backupRepository.getAllListBackup(limit);
        List<TblImageInfo> imageInfoList = backupList.stream().map(x -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    backupRepository.delete(x);
                    return getMetaDataCraft(x.getValue());
                }
        ).filter(Objects::nonNull).collect(Collectors.toList());
        log.info("Saving {} Image...", imageInfoList.size());
        saveAllImage(imageInfoList);
        log.info("Image Saved");
        log.info("Saving {} fail...", failList.size());
        saveAllBackUp(failList);
        log.info("Successfully");
    }

    private void saveAllImage(List<TblImageInfo> lst) {
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        customThreadPool.submit(
                () -> lst.parallelStream().forEach(x -> {
                    try {
                        imageRepository.save(x);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }));
        customThreadPool.shutdown();

    }

    private void saveAllBackUp(List<TblBackup> lst) {
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        customThreadPool.submit(
                () -> lst.parallelStream().forEach(x -> {
                    try {
                        backupRepository.save(x);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }));
        customThreadPool.shutdown();
    }

}
