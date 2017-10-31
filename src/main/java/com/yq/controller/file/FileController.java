package com.yq.controller.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.fh.service.information.pictures.PicturesManager;
import org.change.util.PageData;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    @Resource(name = "picturesService")
    private PicturesManager picturesService;

    @RequestMapping(value = "/upload", produces = {"application/json;charset=UTF-8"}, method = {RequestMethod.POST})
    public @ResponseBody
    String upload(@RequestParam MultipartFile file, HttpServletRequest request) throws IllegalStateException, IOException
    {
        JSONObject jsonObject = new JSONObject();

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
            Map successMap = (Map) picMap.get("successMap");
            String ossUrl = (String) successMap.get(targetFile.getAbsolutePath());
            if(!ossUrl.contains("http:"))
            {
                ossUrl = "http://" + ossUrl;
            }
            jsonObject.put("url", ossUrl);
            jsonObject.put("success", true);
        } else
        {
            jsonObject.put("url", url);
            jsonObject.put("errMsg", picMap.get("errMsg"));
            jsonObject.put("success", false);
        }
        jsonObject.put("result", 1);
        jsonObject.put("fileName", fileName);
        return jsonObject.toString();
    }

}
