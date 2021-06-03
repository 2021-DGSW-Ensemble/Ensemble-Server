package org.dgsw.ensemble.controller;

import org.dgsw.ensemble.service.VideoStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static org.dgsw.ensemble.constants.AppConstants.PATH_VIDEO;

// https://github.com/saravanastar/video-streaming/blob/master/src/main/java/com/ask/home/videostream/controller/VideoStreamController.java
@RestController
public class VideoStreamController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
        logger.info("VideoStreamController Initialize");
    }

    // https://royzero.tistory.com/29 [로이의 개발공장]

    @RequestMapping(value="/video/{video_name}", method= RequestMethod.GET)
    public String stream(@PathVariable("video_name") String video_name, HttpServletRequest request, HttpServletResponse response)
            throws IOException { // 확장자 확인 //

         String[] filename_seperate = video_name.split("\\.");
         String exp;
         if(filename_seperate.length <= 1 ) {
             // 확장자 에러
             throw new RuntimeException("Wrong file name. You need to include expand file name");
         } else {
             exp = filename_seperate[1];
         }
         // Progressbar 에서 특정 위치를 클릭하거나 해서 임의 위치의 내용을 요청할 수 있으므로
         // 파일의 임의의 위치에서 읽어오기 위해 RandomAccessFile 클래스를 사용한다.
         // 해당 파일이 없을 경우 예외 발생
         File file = new File("video/" + video_name);
         if(!file.exists()) throw new FileNotFoundException();

         // 요청 범위의 시작 위치
         // 요청 범위의 끝 위치
         //부분 요청일 경우 true, 전체 요청의 경우 false
         //randomFile 을 클로즈 하기 위하여 try~finally 사용
         try (RandomAccessFile randomFile = new RandomAccessFile(file, "r")) {
             long rangeStart;
             long rangeEnd;
             boolean isPart = false;
            // 동영상 파일 크기
            long movieSize = randomFile.length();
            // 스트림 요청 범위, request의 헤더에서 range를 읽는다.
            String range = request.getHeader("range");
            // 브라우저에 따라 range 형식이 다른데, 기본 형식은 "bytes={start}-{end}" 형식이다.
            // range가 null이거나, reqStart가 0이고 end가 없을 경우 전체 요청이다.
            // 요청 범위를 구한다.
            if (range != null) {
                // 처리의 편의를 위해 요청 range에 end 값이 없을 경우 넣어줌
                if (range.endsWith("-")) {
                    range = range + (movieSize - 1);
                }
                int idxm = range.trim().indexOf("-");
                //"-" 위치
                rangeStart = Long.parseLong(range.substring(6, idxm));
                rangeEnd = Long.parseLong(range.substring(idxm + 1));
                if (rangeStart > 0) {
                    isPart = true;
                }
            } else {
                //range가 null인 경우 동영상 전체 크기로 초기값을 넣어줌. 0부터 시작하므로 -1
                rangeStart = 0;
                rangeEnd = movieSize - 1;
            }
            // 전송 파일 크기
            long partSize = rangeEnd - rangeStart + 1;
            // 전송시작

            response.reset();
            response.setStatus(isPart ? 206 : 200);
            response.setContentType("video/mp4");
            response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + movieSize);
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", "" + partSize);
            OutputStream out = response.getOutputStream();
            randomFile.seek(rangeStart);

            // 파일 전송... java io는 1회 전송 byte수가 int로 지정됨
            // 동영상 파일의 경우 int형으로는 처리 안되는 크기의 파일이 있으므로
            // 8kb로 잘라서 파일의 크기가 크더라도 문제가 되지 않도록 구현
            int bufferSize = 8 * 1024;
            byte[] buf = new byte[bufferSize];
            do {
                int block = partSize > bufferSize ? bufferSize : (int) partSize;
                int len = randomFile.read(buf, 0, block);
                out.write(buf, 0, len);
                partSize -= block;
            } while (partSize > 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
