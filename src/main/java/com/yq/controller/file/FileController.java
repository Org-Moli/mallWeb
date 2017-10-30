package com.yq.controller.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.fh.service.information.pictures.PicturesManager;
import org.apache.log4j.Logger;
import org.change.util.PageData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
public class FileController {
    Gson js = new Gson();
    Logger log = Logger.getLogger(this.getClass());

    @Resource(name = "picturesService")
    private PicturesManager picturesService;

    @ResponseBody
    @RequestMapping(value = "/upload")
    public String upload(@RequestParam MultipartFile file, HttpServletRequest request) throws IllegalStateException, IOException
    {
        Map<String, Object> map = new HashMap<String, Object>();
        String path = request.getSession().getServletContext().getRealPath("/") + "/../mallupload";

        String fileName = new Date().getTime() + ".png";

        File targetFile = new File(path, fileName);
        if (!targetFile.exists())
        {
            targetFile.mkdirs();
        }
        //保存
        file.transferTo(targetFile);

        PageData pageData = new PageData();
        pageData.put("bucketKey", "oss.bucket.pic");
        pageData.put("ossPath", "product/pic/");
        pageData.put("targetPath", targetFile.getAbsolutePath());

        String url = "/mallupload/" + fileName;
        //开始上传oss
        Map<String, Object> picMap = picturesService.uploadOss(pageData);
        if ((boolean) picMap.get("success"))
        {
            Map successMap = (Map) map.get("successMap");
            System.out.println("successMap:" + successMap);
            map.put("url", targetFile.getAbsolutePath());
        } else
        {
            map.put("url", url);
            map.put("errMsg", picMap.get("errMsg"));
        }
        map.put("result", 1);
        map.put("fileName", fileName);
        log.info(js.toJson(map));
        return js.toJson(map);
    }

}
